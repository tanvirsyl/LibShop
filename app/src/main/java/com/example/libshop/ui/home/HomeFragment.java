package com.example.libshop.ui.home;

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
import com.example.libshop.Model.CategoryModel;
import com.example.libshop.Model.ProductModel;
import com.example.libshop.RecylerviewClickInterface;
import com.example.libshop.View.CategoryAdd;
import com.example.libshop.View.LoginRegistration;
import com.example.libshop.View.ProductAdd;
import com.example.libshop.View.ProductDetails;
import com.example.libshop.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecylerviewClickInterface {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    //Category RecyclerView
    private RecyclerView mCategory_RecyclerView;
    List<CategoryModel> listCategoryItem;
    CategoryAdapter mCategory_Adapter;

    //Product RecyclerView
    private RecyclerView mProduct_RecyclerView;
    List<ProductModel> listProductItem = new ArrayList<>();
    ProductAdapter mProduct_Adapter;


    //Firebase Auth
    private String dUserUID = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        Log.d("HomeFragment", "Start");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView mAddCategoryBtn = binding.homeBtnAdd;
        mCategory_RecyclerView = binding.homeCategoryRecyclerview;
        mProduct_RecyclerView = binding.homeProductRecyclerview;

        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d("HomeFragment", " user not null, loged in");
                    dUserUID = user.getUid();
                    checkUserType();
                }else{
                    Log.d("HomeFragment","user not login");

                }
            }


        };


        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        mAddCategoryBtn.setVisibility(View.GONE);
        mAddCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CategoryAdd.class);
                startActivity(intent);
            }
        });


        return root;
    }

    private void callProductViewModel(String dsCategoryUID) {
        Log.d("ViewModel", "allViewModel:1 homeViewModel start");
        homeViewModel.callProductList(dsCategoryUID).observe(getViewLifecycleOwner(), new Observer<List<ProductModel>>() {
            @Override
            public void onChanged(List<ProductModel> list_Product_Item) {
                Log.d("ViewModel", "allViewModel:1 onChanged listview4 size = "+list_Product_Item.size());

                if (list_Product_Item.get(0).getProductName().equals("NULL")){
                    Toast.makeText(getContext(),"No Product Found "+dsCategoryUID, Toast.LENGTH_SHORT).show();;
                }else{
                    mProduct_Adapter = new ProductAdapter(getContext(),list_Product_Item,HomeFragment.this);
                    mProduct_Adapter.notifyDataSetChanged();
                    listProductItem = list_Product_Item;

                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mProduct_RecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                        mProduct_RecyclerView.setAdapter(mProduct_Adapter);
                    } else {
                        mProduct_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
                        mProduct_RecyclerView.setAdapter(mProduct_Adapter);
                    }
                }
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //private HomeViewModel homeViewModel;
    private void callViewModel() {
        Log.d("ViewModel", "allViewModel:1 homeViewModel start");
        homeViewModel.LoadCategoryList().observe(getViewLifecycleOwner(), new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> list_cateagory_models) {
                Log.d("ViewModel", "allViewModel:1 onChanged listview4 size = "+list_cateagory_models.size());

                if (list_cateagory_models.get(0).getCategoryName().equals("NULL")){
                    Toast.makeText(getContext(),"No Category Found", Toast.LENGTH_SHORT).show();;
                }else{
                    if(dUserType.equals("Owner")){
                        list_cateagory_models.add(new CategoryModel("Add","Add Category", "categoryPhotoUrl", "categoryBio", "categoryCreator",0,0));
                    }

                    mCategory_Adapter = new CategoryAdapter(getContext(),list_cateagory_models,HomeFragment.this);
                    mCategory_Adapter.notifyDataSetChanged();
                    listCategoryItem = list_cateagory_models;

                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mCategory_RecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                        mCategory_RecyclerView.setAdapter(mCategory_Adapter);
                    } else {
                        mCategory_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
                        mCategory_RecyclerView.setAdapter(mCategory_Adapter);
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
                    }else{
                        Toast.makeText(getContext(),"Type User", Toast.LENGTH_SHORT).show();

                    }
                    callViewModel();
                }else{
                    Toast.makeText(getContext(),"Error User Information Not Found", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), LoginRegistration.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });
    }



    @Override   //Add Category
    public void onItemClick(int position) {
        String dsCategoryUID = listCategoryItem.get(position).getCategoryUID();
        if(dsCategoryUID.equals("Add")){
            Toast.makeText(getContext(),"Clicked on Add Category", Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent(getContext(), CategoryAdd.class);
            startActivity(intent);
        }else{
            callProductViewModel(dsCategoryUID);
        }

    }

    @Override   //Add Product
    public void onItemLongClick(int position) {
        if(dUserType.equals("Owner")){
            String dsCategoryUID = listCategoryItem.get(position).getCategoryUID();
            String dsCategoryName= listCategoryItem.get(position).getCategoryName();
            Intent intent = new Intent(getContext(), ProductAdd.class);
            intent.putExtra("dsCategoryUID",  dsCategoryUID);
            intent.putExtra("dsCategoryName", dsCategoryName);
            startActivity(intent);
        }else{
            Toast.makeText(getContext(),"You are not the Admin", Toast.LENGTH_SHORT).show();;
        }


    }

    @Override
    public void onProductItemClick(int position) {
        if(listProductItem != null){
            String dsCategoryUID = listProductItem.get(position).getProductCategory();
            String dsProductUID = listProductItem.get(position).getProductUID();
            Intent intent = new Intent(getContext(), ProductDetails.class);
            intent.putExtra("dsCategoryUID",  dsCategoryUID);
            intent.putExtra("dsProductUID", dsProductUID);
            startActivity(intent);
        }else{
            Toast.makeText(getContext(),"Product UID NULL", Toast.LENGTH_SHORT).show();;
        }

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
}