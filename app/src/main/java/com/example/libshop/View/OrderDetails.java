package com.example.libshop.View;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.libshop.Adapter.CartAdapter;
import com.example.libshop.Cart_VM;
import com.example.libshop.MainActivity;
import com.example.libshop.Model.CartModel;
import com.example.libshop.R;
import com.example.libshop.RecylerviewClickInterface;

import java.util.ArrayList;
import java.util.List;

public class OrderDetails extends AppCompatActivity implements RecylerviewClickInterface {

    //Product RecyclerView
    private RecyclerView mProduct_RecyclerView;
    List<CartModel> listCartItem = new ArrayList<>();
    CartAdapter cart_adapter;

    private TextView mOrderStatusText, mOrderTotalAMount;
    private EditText mOrderNoteEditText;
    private Button mOrderNoteSubtmitBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        mProduct_RecyclerView = (RecyclerView) findViewById(R.id.order_details_recyclerview);
        mOrderStatusText  = (TextView)findViewById(R.id.order_details_status);
        mOrderTotalAMount  = (TextView)findViewById(R.id.order_total_amount_text);
        mOrderNoteEditText = (EditText)findViewById(R.id.order_detail_note_edittxt);
        mOrderNoteSubtmitBtn = (Button)findViewById(R.id.order_detail_note_submit_btn);

        getIntentMethod();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            dUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            checkUserType();
            callProductViewModel(dUserUID);
        }else{
            Toast.makeText(getApplicationContext(),"Please Login ", Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        mOrderNoteSubtmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dsNoteEdiTText = mOrderNoteEditText.getText().toString();
                mOrderStatusText.setText("Order Status : "+dsNoteEdiTText);
                mOrderNoteEditText.setVisibility(View.GONE);
                mOrderNoteSubtmitBtn.setVisibility(View.GONE);
                db.collection("AllShop").document("ShoeBox").collection("Orders")
                        .document(dsListOrderUID).update("dsNote",dsNoteEdiTText);
            }
        });
    }
    String dUserUID =  "NO";
    private Cart_VM cartViewModel;

    private void callProductViewModel(String dUserUID) {
        Log.d("ViewModel", "allViewModel:1 cartViewModel start");

        CollectionReference cartRef;
        cartRef = db.collection("AllShop").document("ShoeBox").collection("Orders")
                .document(dsListOrderUID).collection("CartItems");


        cartViewModel = new ViewModelProvider(this).get(Cart_VM.class);
        cartViewModel.callProductList(dUserUID, cartRef).observe(this, new Observer<List<CartModel>>() {
            @Override
            public void onChanged(List<CartModel> list_Product_Item) {
                Log.d("ViewModel", "allViewModel:1 onChanged listview4 size = " + list_Product_Item.size());
                //Toast.makeText(getApplicationContext(),"Size "+list_Product_Item.size(), Toast.LENGTH_SHORT).show();;
                if (list_Product_Item.get(0).getProductName().equals("NULL")) {
                    Toast.makeText(getApplicationContext(), "No Product Added", Toast.LENGTH_SHORT).show();

                } else {
                    cart_adapter = new CartAdapter(getApplicationContext(), list_Product_Item, OrderDetails.this);
                    cart_adapter.notifyDataSetChanged();
                    listCartItem = list_Product_Item;

                    //Order Total Price
                    int totalsize = list_Product_Item.size();
                    int totalAmount = 0;
                    for(int i = 0; i<totalsize; i++){
                        totalAmount += list_Product_Item.get(i).getProductiPrice();
                    }
                    mOrderTotalAMount.setText("Total Price = "+totalAmount);
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mProduct_RecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                        mProduct_RecyclerView.setAdapter(cart_adapter);
                    } else {
                        mProduct_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        mProduct_RecyclerView.setAdapter(cart_adapter);
                    }
                }
            }
        });
    }
    String dUserType = "NO";
    public void checkUserType(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("AllShop").document("ShoeBox")
                .collection("Users").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    dUserType = documentSnapshot.getString("usertype");
                    if(dUserType.equals("Owner")){
                        dUserType = "Owner";
                        mOrderNoteEditText.setVisibility(View.VISIBLE);
                        mOrderNoteSubtmitBtn.setVisibility(View.VISIBLE);
                    }else{
                        mOrderNoteEditText.setVisibility(View.GONE);
                        mOrderNoteSubtmitBtn.setVisibility(View.GONE);

                    }

                }else{
                    Toast.makeText(getApplicationContext(),"Error User Information Not Found", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginRegistration.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });
    }

    String dsListOrderUID = "NO", dsListOrderNote = "NO";
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsListOrderUID = intent.getExtras().getString("dsListOrderUID");
            dsListOrderNote = intent.getExtras().getString("dsListOrderNote");
            intentFoundError = CheckIntentMethod(dsListOrderUID);
            intentFoundError = CheckIntentMethod(dsListOrderNote);

            if(!intentFoundError){
                //callViewModel();
                if(dsListOrderNote.equals("NIL")){
                    mOrderStatusText.setText("Order Status : Pending.");
                }else
                    mOrderStatusText.setText("Order Status : "+dsListOrderNote);

            }else{
                Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;
            }
        }else{
            dsListOrderUID = "NO";
            dsListOrderNote = "NO";
            Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();
        }

    }
    private boolean CheckIntentMethod(String dsTestIntent){
        if(TextUtils.isEmpty(dsTestIntent)) {
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
        }else if (dsTestIntent.equals("")){
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent 404" , Toast.LENGTH_SHORT).show();
        }else{
            intentFoundError = false;
        }
        return intentFoundError;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onProductItemClick(int position) {

    }
}