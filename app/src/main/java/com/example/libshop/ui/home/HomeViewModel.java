package com.example.libshop.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.libshop.Model.CategoryModel;
import com.example.libshop.Model.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData mLiveData;
    public MutableLiveData<List<CategoryModel>> LoadCategoryList() {
        List<CategoryModel> listCategoryItem ; listCategoryItem =new ArrayList<>();

        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 LoadLevel4List start");

        notebookRef = db.collection("AllShop").document("ShoeBox").collection("Category");

        if(mLiveData == null) {
            mLiveData = new MutableLiveData();
            notebookRef.orderBy("CategoryiPriority", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if(queryDocumentSnapshots.isEmpty()) {
                                //String categoryUID, String categoryName, String categoryPhotoUrl, String categoryBio, String categoryCreator, long categoryiViews, long categoryiPriority, long categoryiClicked
                                listCategoryItem.add(new CategoryModel("UID","NULL", "categoryPhotoUrl", "categoryBio", "categoryCreator",0,0));
                                mLiveData.postValue(listCategoryItem);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            }else {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    CategoryModel category_model = documentSnapshot.toObject(CategoryModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String dsCategory_UID = documentSnapshot.getId();
                                    String dsCategory_Name = category_model.getCategoryName();
                                    String dsCategory_PhotoUrl = category_model.getCategoryPhotoUrl();
                                    String dsCategory_Bio= category_model.getCategoryBio();
                                    String dsCategory_Creator= category_model.getCategoryCreator();
                                    long diCategoryView = category_model.getCategoryiViews();
                                    long diCategoryiPriority = category_model.getCategoryiPriority();


                                    //String UID, String hospitalName, String hospitalPhotoUrl, String hospitalBio, String hospitalCreator, String hospitalAddress, long hospitaliPriority
                                    listCategoryItem.add(new CategoryModel(dsCategory_UID, dsCategory_Name,dsCategory_PhotoUrl,dsCategory_Bio, dsCategory_Creator,diCategoryView,diCategoryiPriority));
                                    mLiveData.postValue(listCategoryItem);
                                }
                                mLiveData.postValue(listCategoryItem);    //All Items level 4 , it is a one type category

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

        return mLiveData;
    }
    
    //////////Product List
    public MutableLiveData mProductLiveData;
    public MutableLiveData<List<ProductModel>> callProductList(String dsCategoryUID) {
        List<ProductModel> listProductItem ; listProductItem =new ArrayList<>();

        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 mProductLiveData start");

        notebookRef = db.collection("AllShop").document("ShoeBox").collection("Product");

        if(mProductLiveData == null) {
            mProductLiveData = new MutableLiveData();
        }
            notebookRef
                    .whereEqualTo("ProductCategory",dsCategoryUID)
                    //.orderBy("ProductiPriority", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if(queryDocumentSnapshots.isEmpty()) {
                                //String productUID, String productName, String productPhotoUrl, String productBio, String productCreator, long productiViews, long productiPriority, long productiRating, long productiOrders, long productiPrice, long productiDiscount, String productExtra, String productSize, String productCategory
                                listProductItem.add(new ProductModel("UID","NULL", "productPhotoUrl", "productBio", "productCreator",0,0,0,0,0,0,"productExtra","productSize","productCategory"));
                                mProductLiveData.postValue(listProductItem);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            }else {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    ProductModel category_model = documentSnapshot.toObject(ProductModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String ProductUID = documentSnapshot.getId();
                                    String ProductName = category_model.getProductName();
                                    String ProductPhotoUrl = category_model.getProductPhotoUrl();
                                    String ProductBio = category_model.getProductBio();
                                    String ProductCreator = category_model.getProductCreator();

                                    long ProductiViews = category_model.getProductiViews();
                                    long ProductiPriority = category_model.getProductiPriority();
                                    long ProductiRating = category_model.getProductiRating();
                                    long ProductiOrders = category_model.getProductiOrders();
                                    long ProductiPrice = category_model.getProductiPrice();
                                    long ProductiDiscount = category_model.getProductiDiscount();
                                    String ProductExtra =  category_model.getProductExtra();
                                    String ProductSize  = category_model.getProductSize();
                                    String ProductCategory  = category_model.getProductCategory();


                                    //String productUID, String productName, String productPhotoUrl, String productBio, String productCreator, long productiViews, long productiPriority, long productiRating, long productiOrders, long productiPrice, long productiDiscount, String productExtra, String productSize, String productCategory
                                    listProductItem.add(new ProductModel(ProductUID,ProductName, ProductPhotoUrl, ProductBio, ProductCreator,ProductiViews,ProductiPriority,ProductiRating,
                                            ProductiOrders,ProductiPrice,ProductiDiscount,ProductExtra,ProductSize,ProductCategory));
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