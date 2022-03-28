package com.example.libshop;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.libshop.Model.CartModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cart_VM extends AndroidViewModel {


    public Cart_VM(@NonNull Application application) {
        super(application);
        Log.d("ViewModel", "allViewModel:4 Level_D_VM start");
    }

    //////////Product List
    public MutableLiveData mProductLiveData;
    public MutableLiveData<List<CartModel>> callProductList(String dUserUID, CollectionReference cartRef) {
        Log.d("ViewModel", "allViewModel:4 mProductLiveData start");
        List<CartModel> listProductItem ; listProductItem =new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notebookRef = cartRef;

        Date dDate;
        if(mProductLiveData == null) {
            mProductLiveData = new MutableLiveData();
        }
            notebookRef
                    //.whereEqualTo("ProductCategory",dsCategoryUID)
                    //.orderBy("ProductiPriority", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if(queryDocumentSnapshots.isEmpty()) {
                                //String productUID, String productName, String productPHOTO, String productColor, String productColor, long productiPrice, Date productOrderTime
                                listProductItem.add(new CartModel("UID","NULL", "productPHOTO", "productColor", "productColor",0,0,null));
                                mProductLiveData.postValue(listProductItem);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            }else {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    CartModel cart_model = documentSnapshot.toObject(CartModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String ProductUID = documentSnapshot.getId();
                                    String ProductName = cart_model.getProductName();
                                    String ProductPhotoURL = cart_model.getProductPHOTO();
                                    String ProductColor = cart_model.getProductColor();
                                    String ProductSize = cart_model.getProductSize();
                                    long ProductiPrice = cart_model.getProductiPrice();
                                    long ProductiQuantity = cart_model.getProductQuantity();
                                    Date ProductDate = cart_model.getProductOrderTime();

                                    //String productUID, String productName, String productPhotoUrl, String productBio, String productCreator, long productiViews, long productiPriority, long productiRating, long productiOrders, long productiPrice, long productiDiscount, String productExtra, String productSize, String productCategory
                                    listProductItem.add(new CartModel(ProductUID,ProductName, ProductPhotoURL, ProductSize, ProductColor,ProductiPrice,ProductiQuantity,ProductDate));
                                    mProductLiveData.postValue(listProductItem);
                                }
                                mProductLiveData.postValue(listProductItem);

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


        return mProductLiveData;
    }
}
