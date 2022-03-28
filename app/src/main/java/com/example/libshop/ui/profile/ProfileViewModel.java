package com.example.libshop.ui.profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<DocumentSnapshot> muDocumentSnapshot;


    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<DocumentSnapshot> getUserData(String dUserUID){
        Log.d("DashboardFragmentVM", "Profile:1 DocumentSnapshotVM exist");
        if(muDocumentSnapshot == null){
            Log.d("DashboardFragmentVM", "Profile:1 muDocumentSnapshot  null");
            muDocumentSnapshot = new MutableLiveData<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference user_data_ref =  db.collection("AllShop").document("ShoeBox").collection("Users").document(dUserUID);
            user_data_ref.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d("DashboardFragmentVM", "Profile:1 documentSnapshot  success");
                            muDocumentSnapshot.postValue(documentSnapshot);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Log.d("DashboardFragmentVM", "Profile:1 documentSnapshot  failed");
                }
            });
        }else{
            Log.d("DashboardFragmentVM", "Profile:1 muDocumentSnapshot not null");
        }
        Log.d("DashboardFragmentVM", "Profile:1 documentSnapshot  returning");
        return muDocumentSnapshot;
    }




}