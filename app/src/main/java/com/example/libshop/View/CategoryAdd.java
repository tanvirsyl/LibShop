package com.example.libshop.View;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.libshop.MainActivity;
import com.example.libshop.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CategoryAdd extends AppCompatActivity {

    //Note Search Key not added 404
    private ImageView mCategory_ImageView;
    private EditText mCategory_Name, mCategory_Info, mCategory_Priority, mCategory_Views;

    private Button mCategory_UpdateBtn;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    //Photo Selecting and Croping
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Variablls
    private String dUserUID = "NO";
    private String dsPhotoUrl = "NO", dsCategoryName = "NO", dsCategoryInfo = "NO", dsCategoryPriority = "NO",  dsCategoryViews = "NO";
    private int  diCategoryPriority = 0, diCategoryViews = 0;
    private String dsLevel1_Name = "NO", dsLevel2_Name = "NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);

        mCategory_ImageView = (ImageView)findViewById(R.id.category_imageview);
        mCategory_Name = (EditText)findViewById(R.id.category_name);
        mCategory_Info = (EditText)findViewById(R.id.category_info);
        mCategory_Priority = (EditText)findViewById(R.id.category_priority);
        mCategory_Views = (EditText)findViewById(R.id.category_view);
        mCategory_UpdateBtn = (Button)findViewById(R.id.category_update_btn);


        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if(user != null){
                    String dsUserName = user.getDisplayName();
                    dUserUID = user.getUid();

                    // Toast.makeText(getApplicationContext(),"Add Level3 Information", Toast.LENGTH_SHORT).show();;
                }else{
                    Toast.makeText(getApplicationContext(),"Login", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };

        mCategory_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });

        mCategory_UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsCategoryName = mCategory_Name.getText().toString();
                dsCategoryInfo = mCategory_Info.getText().toString();
                dsCategoryPriority = mCategory_Priority.getText().toString();
                dsCategoryViews = mCategory_Views.getText().toString();

                if(imageUriResultCrop == null){
                    if(imageUri_storage == null){
                        Toast.makeText(getApplicationContext(),"Please Select Image", Toast.LENGTH_SHORT).show();;
                    }else{
                        Toast.makeText(getApplicationContext(),"Please Crop Image", Toast.LENGTH_SHORT).show();;
                    }

                }else if(dsCategoryName.equals("NO")  || dsCategoryInfo.equals("NO") || dsCategoryPriority.equals("NO") ||  dsCategoryViews.equals("NO") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else if( dsCategoryName.equals("")  || dsCategoryInfo.equals("") || dsCategoryPriority.equals("") ||  dsCategoryViews.equals("") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else{
                    diCategoryPriority = Integer.parseInt(dsCategoryPriority);
                    diCategoryViews = Integer.parseInt(dsCategoryViews);
                    UploadCropedImageFunction(imageUriResultCrop);
                }
            }
        });
    }



    private void UploadCropedImageFunction(Uri filePath) {
        if(filePath != null)
        {
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());

            ref = storageReference.child("AllShop/ShoeBox/Category/"+dsCategoryName+"/"+dsCategoryName+ dsTimeMiliSeconds +"."+getFileExtention(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //Photo Uploaded now get the URL
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String dPhotoURL = uri.toString();
                                    Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();

                                    Map<String, Object> note = new HashMap<>();
                                    note.put("CategoryName", dsCategoryName);
                                    note.put("CategoryPhotoUrl", dPhotoURL);
                                    note.put("CategoryBio", dsCategoryInfo);
                                    note.put("CategoryCreator", dUserUID);
                                    note.put("CategoryiViews", diCategoryViews);
                                    note.put("CategoryiPriority", diCategoryPriority);

                                    db.collection("AllShop").document("ShoeBox").collection("Category").add(note)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    //String dsLevel3_UID = documentReference.getId();
                                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    mCategory_UpdateBtn.setText("Upload Again");
                                                    mCategory_Name.setText("");
                                                    mCategory_Info.setText("");
                                                    mCategory_Priority.setText("");
                                                    mCategory_Views.setText("");

                                                   /* finish();
                                                    Intent intent = new Intent(CategoryAdd.this, MainActivity.class);
                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    startActivity(intent);*/
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            mCategory_UpdateBtn.setText("Try Again");
                                            mCategory_Name.setText("Failed");
                                            mCategory_Info.setText("");
                                            mCategory_Priority.setText("");
                                            Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();

                                        }
                                    });



                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            mCategory_UpdateBtn.setText("Failed Photo Upload");
                            Toast.makeText(getApplicationContext(), "Failed Photo"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Upload Failed Photo Not Found ", Toast.LENGTH_SHORT).show();
        }
    }


    //Dont forget to add class code on MainfestXml
    @Override   //Selecting Image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null){
            //Photo Successfully Selected
            imageUri_storage = data.getData();
            String dFileSize = getSize(imageUri_storage);       //GETTING IMAGE FILE SIZE
            double  dFileSizeDouble = Double.parseDouble(dFileSize);
            int dMB = 1000;
            dFileSizeDouble =  dFileSizeDouble/dMB;
            //dFileSizeDouble =  dFileSizeDouble/dMB;

            if(dFileSizeDouble <= 5000){
                Picasso.get().load(imageUri_storage).into(mCategory_ImageView);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                //startCrop(imageUri_storage);
                imageUriResultCrop = imageUri_storage;
            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Canceled",Toast.LENGTH_SHORT).show();
        }
    }
    public String getSize(Uri uri) {
        String fileSize = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex);
                }
            }
        } finally {
            cursor.close();
        }
        return fileSize;
    }
    private String getFileExtention(Uri uri){   //IMAGE
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
    }

    String dsHospitalUID = "NO", dsHospitalName = "NO";
    private boolean intentFoundError = true;

    private boolean CheckIntentMethod(String dsTestIntent){
        if(TextUtils.isEmpty(dsTestIntent)) {
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
        }else if (dsHospitalUID.equals("")){
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent 404" , Toast.LENGTH_SHORT).show();
        }else{
            intentFoundError = false;
        }
        return intentFoundError;
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