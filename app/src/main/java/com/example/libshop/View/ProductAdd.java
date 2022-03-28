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

public class ProductAdd extends AppCompatActivity {

    //Note Search Key not added 404
    private ImageView mProduct_ImageView;
    private EditText mProduct_Name, mProduct_Info, mProduct_Priority, mProduct_Views;
    private EditText mProduct_Rating, mProduct_Orders, mProduct_Price, mProduct_Discount;
    private Button mProduct_UpdateBtn;
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
    private String dsPhotoUrl = "NO", dsProductName = "NO", dsProductInfo = "NO", dsProductPriority = "NO",  dsProductViews = "NO";
    private String dsProduct_Rating = "NO", dsProduct_Orders = "NO", dsProduct_Price = "NO", dsProduct_Discount = "NO", dsProduct_Extra = "NO";

    private int  diProductPriority = 0, diProductViews = 0;
    private int diRating = 0, diOrders = 0, diPrice = 0, diDiscount = 0;
    private String dsLevel1_Name = "NO", dsLevel2_Name = "NO";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mProduct_ImageView = (ImageView)findViewById(R.id.product_imageview);
        mProduct_Name = (EditText)findViewById(R.id.product_name);
        mProduct_Info = (EditText)findViewById(R.id.product_info);
        mProduct_Priority = (EditText)findViewById(R.id.product_priority);
        mProduct_Views = (EditText)findViewById(R.id.product_view);
        mProduct_UpdateBtn = (Button)findViewById(R.id.product_update_btn);

        mProduct_Rating = (EditText)findViewById(R.id.product_rating);
        mProduct_Orders = (EditText)findViewById(R.id.product_orders);
        mProduct_Price = (EditText)findViewById(R.id.product_price);
        mProduct_Discount = (EditText) findViewById(R.id.product_discount);
        getIntentMethod();
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

        mProduct_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });

        mProduct_UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsProductName = mProduct_Name.getText().toString();
                dsProductInfo = mProduct_Info.getText().toString();
                dsProductPriority = mProduct_Priority.getText().toString();
                dsProductViews = mProduct_Views.getText().toString();

                dsProduct_Rating = mProduct_Rating.getText().toString();
                dsProduct_Orders = mProduct_Orders.getText().toString();
                dsProduct_Price = mProduct_Price.getText().toString();
                dsProduct_Discount = mProduct_Discount.getText().toString();
                dsProduct_Extra = "NO";

                if(imageUriResultCrop == null){
                    if(imageUri_storage == null){
                        Toast.makeText(getApplicationContext(),"Please Select Image", Toast.LENGTH_SHORT).show();;
                    }else{
                        Toast.makeText(getApplicationContext(),"Please Crop Image", Toast.LENGTH_SHORT).show();;
                    }

                }else if(dsProductName.equals("NO")  || dsProductInfo.equals("NO") || dsProductPriority.equals("NO") ||  dsProductViews.equals("NO") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else if( dsProductName.equals("")  || dsProductInfo.equals("") || dsProductPriority.equals("") ||  dsProductViews.equals("") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else if(dsProduct_Rating.equals("NO")  || dsProduct_Orders.equals("NO") || dsProduct_Price.equals("NO") ||  dsProduct_Discount.equals("NO") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else if(dsProduct_Rating.equals("")  || dsProduct_Orders.equals("") || dsProduct_Price.equals("") ||  dsProduct_Discount.equals("") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else{
                    diProductPriority = Integer.parseInt(dsProductPriority);
                    diProductViews = Integer.parseInt(dsProductViews);

                    diRating   = Integer.parseInt(dsProduct_Rating);
                    diOrders   = Integer.parseInt(dsProduct_Orders);
                    diPrice    = Integer.parseInt(dsProduct_Price);
                    diDiscount = Integer.parseInt(dsProduct_Discount);
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

            ref = storageReference.child("AllShop/ShoeBox/Category/"+dsCategoryUID+"/"+dsProductName+ dsTimeMiliSeconds +"."+getFileExtention(filePath));
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
                                    note.put("ProductName", dsProductName);
                                    note.put("ProductPhotoUrl", dPhotoURL);
                                    note.put("ProductBio", dsProductInfo);
                                    note.put("ProductCreator", dUserUID);
                                    note.put("ProductiViews", diProductViews);
                                    note.put("ProductiPriority", diProductPriority);
                                    
                                    note.put("ProductiRating", diRating);
                                    note.put("ProductiOrders", diOrders);
                                    note.put("ProductiPrice", diPrice);
                                    note.put("ProductiDiscount", diDiscount);
                                    note.put("ProductExtra", "NO");
                                    note.put("ProductSize", "40a41b42c");
                                    note.put("ProductCategory", dsCategoryUID);

                                    

                                    db.collection("AllShop").document("ShoeBox").collection("Product").add(note)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    //String dsLevel3_UID = documentReference.getId();
                                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    mProduct_UpdateBtn.setText("Upload Again");
                                                    mProduct_Name.setText("");
                                                    mProduct_Info.setText("");
                                                    mProduct_Priority.setText("");
                                                    mProduct_Views.setText("");

                                                   /* finish();
                                                    Intent intent = new Intent(ProductAdd.this, MainActivity.class);
                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    startActivity(intent);*/
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            mProduct_UpdateBtn.setText("Try Again");
                                            mProduct_Name.setText("Failed");
                                            mProduct_Info.setText("");
                                            mProduct_Priority.setText("");
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
                            mProduct_UpdateBtn.setText("Failed Photo Upload");
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
                Picasso.get().load(imageUri_storage).into(mProduct_ImageView);
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

    String dsCategoryUID = "NO", dsCategoryName = "NO";
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsCategoryUID = intent.getExtras().getString("dsCategoryUID");
            dsCategoryName = intent.getExtras().getString("dsCategoryName");
            intentFoundError = CheckIntentMethod(dsCategoryUID);
            intentFoundError = CheckIntentMethod(dsCategoryName);

            if(!intentFoundError){
                //callViewModel();
            }else{
                Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;
            }
        }else{
            dsCategoryUID = "NO";
            dsCategoryName = "NO";
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