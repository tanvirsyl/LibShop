package com.example.libshop.ui.orders;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.libshop.Adapter.OrderAdapter;
import com.example.libshop.Model.OrderModel;
import com.example.libshop.RecylerviewClickInterface;
import com.example.libshop.View.LoginRegistration;
import com.example.libshop.View.OrderDetails;
import com.example.libshop.databinding.FragmentOrdersBinding;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements RecylerviewClickInterface {


    private OrdersViewModel ordersViewModel;
    private FragmentOrdersBinding binding;

    private RecyclerView mOrderRecyclerView;
    List<OrderModel> listOrderItem = new ArrayList<>();
    OrderAdapter mOrder_Adapter;

    //Firebase Auth
    private String dUserUID = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    private TextView mOrderHeadText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                new ViewModelProvider(this).get(OrdersViewModel.class);

        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mOrderHeadText = binding.orderHeadText;
        mOrderRecyclerView = binding.orderRecyclerView;
        ordersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mOrderHeadText.setText(s);
            }
        });

        //Auth Logic
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d("DashboardFragment", "Profile:1 user not null");
                    //dUserUID = user.getUid();
                    dUserUID = user.getUid();
                    mOrderHeadText.setText("Loading...");
                    checkUserType();

                }else{
                    Log.d("DashboardFragment", "Profile:1 user not null");
                    mOrderHeadText.setText("Please Login");
                }
            }


        };

        return root;
    }

    private void getOrderListMethod(String dUserType, String dUserUID) {
            OrdersViewModel notificationViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
            Log.d("ViewModel", "allViewModel:1 notificationViewModel start");
            notificationViewModel.callOrderList(dUserType,dUserUID).observe(getViewLifecycleOwner(), new Observer<List<OrderModel>>() {
                @Override
                public void onChanged(List<OrderModel> list_Order_Item) {
                    Log.d("ViewModel", "allViewModel:1 onChanged listview4 size = "+list_Order_Item.size());

                    if (list_Order_Item.get(0).getOrder_UID().equals("NULL")){
                        Toast.makeText(getContext(),"No Orders Found ", Toast.LENGTH_SHORT).show();;
                        mOrderHeadText.setText("No Orders!");
                    }else{
                        mOrderHeadText.setText("My Orders");
                        mOrder_Adapter = new OrderAdapter(getContext(),list_Order_Item, OrdersFragment.this);
                        mOrder_Adapter.notifyDataSetChanged();
                        listOrderItem = list_Order_Item;

                        int orientation = getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            mOrderRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                            mOrderRecyclerView.setAdapter(mOrder_Adapter);
                        } else {
                            mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
                            mOrderRecyclerView.setAdapter(mOrder_Adapter);
                        }
                    }
                }
            });
        
    }


    public void checkUserType(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("AllShop").document("ShoeBox")
                .collection("Users").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String dUserType = documentSnapshot.getString("usertype");
                    if(dUserType.equals("Owner")){
                        getOrderListMethod("uid_shop_owner", "NOT SET");
                    }else{
                        getOrderListMethod("uid_buyer", dUserUID);

                    }
                    Toast.makeText(getContext(),"userType "+dUserType, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"Error User Information Not Found", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), LoginRegistration.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        String dsListOrderUID = listOrderItem.get(position).getOrder_UID();
        String dsListOrderNote = listOrderItem.get(position).getDsNote();

        Intent intent = new Intent(getContext(), OrderDetails.class);
        intent.putExtra("dsListOrderUID", dsListOrderUID);
        intent.putExtra("dsListOrderNote", dsListOrderNote);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onProductItemClick(int position) {

    }
}