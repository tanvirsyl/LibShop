package com.example.libshop.ui.orders;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.libshop.Model.OrderModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    //////////Order List
    public MutableLiveData mOrderLiveData;
    public MutableLiveData<List<OrderModel>> callOrderList(String dUserType,String dUserUID) {
        List<OrderModel> listOrderItem ; listOrderItem =new ArrayList<>();

        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 mOrderLiveData start");

        notebookRef = db.collection("AllShop").document("ShoeBox").collection("Orders");

        if(mOrderLiveData == null) {
            mOrderLiveData = new MutableLiveData();
        }
        notebookRef
                .whereEqualTo(dUserType,dUserUID)
                //.orderBy("OrderiPriority", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        if(queryDocumentSnapshots.isEmpty()) {
                            //Date diTime, String order_UID, String uid_cart, String uid_buyer, String uid_shop_owner, String dsNote, String dsDeliveryHour, String dsPaymentStatus, String dsCompleteLevel, long diTotal_Money) {
                            listOrderItem.add(new OrderModel(null, "NULL", "uidCart", "uidBuyer", "uidSHopOwner","dsNote","dsDevliveryHour", "dsPaymentMethod","dsCompleteLevel",0));
                            mOrderLiveData.postValue(listOrderItem);
                            Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                        }else {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                OrderModel book_model = documentSnapshot.toObject(OrderModel.class);
                                //messageModel.setDocumentID(documentSnapshot.getId());
                                String OrderUID = documentSnapshot.getId();
                                Date OrderDate = book_model.getDiTime();
                                String dsNote = book_model.getDsNote();;
                                //String productUID, String productName, String productPhotoUrl, String productBio, String productCreator, long productiViews, long productiPriority, long productiRating, long productiOrders, long productiPrice, long productiDiscount, String productExtra, String productSize, String productCategory
                                listOrderItem.add(new OrderModel(OrderDate, OrderUID, "uidCart", "uidBuyer", "uidSHopOwner",dsNote,"dsDevliveryHour", "dsPaymentMethod","dsCompleteLevel",0));
                                mOrderLiveData.postValue(listOrderItem);
                            }
                            mOrderLiveData.postValue(listOrderItem);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


        return mOrderLiveData;
    }
}