package com.example.libshop.View;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.libshop.Adapter.CartAdapter;
import com.example.libshop.Cart_VM;
import com.example.libshop.MainActivity;
import com.example.libshop.Model.CartModel;
import com.example.libshop.R;
import com.example.libshop.RecylerviewClickInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PB_Cart extends AppCompatActivity implements RecylerviewClickInterface {

    private Button mConfirmOrderBtn;
    //Product RecyclerView
    private RecyclerView mProduct_RecyclerView;
    List<CartModel> listCartItem = new ArrayList<>();
    CartAdapter cart_adapter;

    private String dUserUID  = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pb_cart);
        mProduct_RecyclerView = (RecyclerView) findViewById(R.id.cart_recycler_view) ;
        mConfirmOrderBtn = (Button) findViewById(R.id.pb_cart_order_btn) ;

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if(user != null){
                    String dsUserName = user.getDisplayName();
                    dUserUID = user.getUid();
                    callProductViewModel(dUserUID);
                    // Toast.makeText(getApplicationContext(),"Add Level3 Information", Toast.LENGTH_SHORT).show();;
                }else{
                    Toast.makeText(getApplicationContext(),"Login", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };

        mConfirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderSetToServer();
            }
        });
    }

    private void OrderSetToServer() {
        String dsMiliSeconds = String.valueOf(System.currentTimeMillis());
        String dsProductUID = listCartItem.get(0).getProductUID();
        //Order Data Creating to Server
        FieldValue ddDate =  FieldValue.serverTimestamp();
        Map<String, Object> note = new HashMap<>();
        note.put("diTime", ddDate);
        note.put("diTotal_Money", 200);
        note.put("uid_cart", dsMiliSeconds);  //ERROR on uid cart   //inside of .collection("Ordered").document("MyOrder");
        note.put("uid_buyer", dUserUID);    //je order dise
        note.put("uid_shop_owner", "NOT SET");    //je show owner // je order paibo user order korar pore
        note.put("dsNote", "NIL");
        note.put("dsDeliveryHour", "NIL");
        note.put("dsPaymentStatus", "Pending");
        note.put("dsCompleteLevel", "1");
        OrderCreatingToServer(note);

    }
    private void OrderCreatingToServer(Map myNote) {
        //Creating Order Documents to server now.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userProfileMyCartpath = db.collection("AllShop").document("ShoeBox")
                .collection("Users").document(dUserUID).collection("MyCart");
        CollectionReference orderPath = db.collection("AllShop").document("ShoeBox")
                .collection("Orders");
        orderPath.add(myNote).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String dsOrderUID = documentReference.getId();
                Toast.makeText(PB_Cart.this, "Order Submitting", Toast.LENGTH_SHORT).show();
                int diListSize = listCartItem.size();
                for(int i = 0; i<diListSize; i++){
                    CartModel cartModel =   listCartItem.get(i);
                    String dsProductUID = cartModel.getProductUID();
                    String dsProductName= cartModel.getProductName();

                    Map<String, Object> cart = new HashMap<>();
                    cart.put("ProductUID", dsProductUID);
                    cart.put("ProductName", dsProductName);
                    cart.put("ProductPHOTO", cartModel.getProductPHOTO()); //map is done
                    cart.put("ProductColor", cartModel.getProductColor());
                    cart.put("ProductSize", cartModel.getProductSize());
                    cart.put("ProductiPrice", cartModel.getProductiPrice()); //Integer
                    cart.put("ProductOrderTime", cartModel.getProductOrderTime()); //Integer
                    cart.put("ProductQuantity", cartModel.getProductQuantity()); //Integer


                    orderPath.document(dsOrderUID).collection("CartItems").document(dsProductUID).set(cart)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(PB_Cart.this, dsProductName+" added to order list", Toast.LENGTH_SHORT).show();
                            userProfileMyCartpath.document(dsProductUID).delete();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(PB_Cart.this, dsProductName+" failed to add on order list", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                
                /*Intent intent = new Intent(PB_Cart.this, UserProfile.class);
                startActivity(intent);*/
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PB_Cart.this, "Order Uplaod Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Cart_VM cartViewModel;
    private void callProductViewModel(String dUserUID) {
        Log.d("ViewModel", "allViewModel:1 cartViewModel start");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cartRef;
        cartRef = db.collection("AllShop").document("ShoeBox").collection("Users")
                .document(dUserUID).collection("MyCart");

        cartViewModel = new ViewModelProvider(this).get(Cart_VM.class);
        cartViewModel.callProductList(dUserUID,cartRef).observe(this, new Observer<List<CartModel>>() {
            @Override
            public void onChanged(List<CartModel> list_Product_Item) {
                Log.d("ViewModel", "allViewModel:1 onChanged listview4 size = "+list_Product_Item.size());
                //Toast.makeText(getApplicationContext(),"Size "+list_Product_Item.size(), Toast.LENGTH_SHORT).show();;
                if (list_Product_Item.get(0).getProductName().equals("NULL")){
                    Toast.makeText(getApplicationContext(),"No Product Added", Toast.LENGTH_SHORT).show();;
                    mConfirmOrderBtn.setVisibility(View.GONE);
                }else{
                    cart_adapter = new CartAdapter(getApplicationContext(),list_Product_Item, PB_Cart.this);
                    cart_adapter.notifyDataSetChanged();
                    listCartItem = list_Product_Item;

                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mProduct_RecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
                        mProduct_RecyclerView.setAdapter(cart_adapter);
                    } else {
                        mProduct_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                        mProduct_RecyclerView.setAdapter(cart_adapter);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
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