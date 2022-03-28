package com.example.libshop.View;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import java.util.Random;

public class LoginRegistration extends AppCompatActivity {

    private ImageView mUserProfilePic;
    private EditText mUserInfoName, mUserInfoPhoneNo, mUserInfoHomeAddress;
    private RadioGroup mRadioGenderGroup;
    private RadioButton mRadioGenderMale, mRadioGenderFemale;
    private RadioGroup mRadioUserTypeGroup;
    private RadioButton mRadioTypeRider, mRadioTypeUser, mRadioTypeAdmin;
    private Button mUserInfoUpdateBtn;
    //private static final String NO = "NO";

    private String dUserName = "NO",dHomeAddress = "NO", dUserBio = "NO", dUserPhone = "NO", dUserType = "NO", dGender = "NO";
    private String dUserUID = "NO",dUserEmail = "NO", dUserRegistrationDate = "NO", dUserLastActivity = "NO"; private long diUserLastActivity = 0;
    private String dExtra = "NO"; int diSize = 0, diGender = 0;
    //Photo Selecting and Croping
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference category_ref;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        mUserProfilePic = (ImageView)findViewById(R.id.image_add_user_name);
        mUserInfoName = (EditText)findViewById(R.id.edit_add_user_name);
        mUserInfoHomeAddress = (EditText)findViewById(R.id.edit_add_user_address_text);
        mUserInfoPhoneNo = (EditText)findViewById(R.id.edit_add_user_phone);
        mRadioGenderGroup =(RadioGroup)findViewById(R.id.user_add_info_radio_group);
        mRadioGenderMale =(RadioButton)findViewById(R.id.radio_male);
        mRadioGenderFemale =(RadioButton) findViewById(R.id.radio_female);
        mRadioUserTypeGroup =(RadioGroup)findViewById(R.id.user_add_info_radio_group_user_type);
        mRadioTypeRider =(RadioButton)findViewById(R.id.radio_rider);
        mRadioTypeUser =(RadioButton)findViewById(R.id.radio_user);
        mRadioTypeAdmin =(RadioButton) findViewById(R.id.radio_admin);
        mUserInfoUpdateBtn = (Button)findViewById(R.id.user_infor_update_btn);
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                String dsUserName = user.getDisplayName();
                mUserInfoName.setText(dsUserName);
                if(user != null){
                    Toast.makeText(getApplicationContext(),"Update Profile of "
                            +dsUserName, Toast.LENGTH_SHORT).show();;
                    dUserEmail = user.getEmail();
                    dUserUID = user.getUid();
                    //getIntentMethod();
                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };

        mUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
                //go to this method >> onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
            }
        });

        mUserInfoUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mRadioGenderMale.isChecked() && !mRadioGenderFemale.isChecked()){
                    dGender = "NO";
                    Toast.makeText(getApplicationContext(),"Please Select Gender", Toast.LENGTH_SHORT).show();;
                }else if(mRadioGenderMale.isChecked()){
                    dGender = mRadioGenderMale.getText().toString();
                }else if(mRadioGenderFemale.isChecked()){
                    dGender = mRadioGenderFemale.getText().toString();
                }

                if(!mRadioTypeRider.isChecked() && !mRadioTypeUser.isChecked() && !mRadioTypeAdmin.isChecked()){
                    dUserType = "NO";
                    Toast.makeText(getApplicationContext(),"Please Select User Type", Toast.LENGTH_SHORT).show();;
                }else if(mRadioTypeRider.isChecked()){
                    dUserType = mRadioTypeRider.getText().toString();
                }else if(mRadioTypeUser.isChecked()){
                    dUserType = mRadioTypeUser.getText().toString();
                }else if(mRadioTypeAdmin.isChecked()){
                    dUserType = mRadioTypeAdmin.getText().toString();
                }
                dUserName = mUserInfoName.getText().toString();
                dUserBio = mUserInfoHomeAddress.getText().toString();
                dHomeAddress = mUserInfoHomeAddress.getText().toString();
                dUserPhone = mUserInfoPhoneNo.getText().toString();
                if(imageUriResultCrop == null   && dRetrivePhotoURL.equals("NO")){
                    if(imageUri_storage == null){
                        Toast.makeText(getApplicationContext(),"Please Select Image", Toast.LENGTH_SHORT).show();;
                    }else{
                        Toast.makeText(getApplicationContext(),"Please Crop Image", Toast.LENGTH_SHORT).show();;
                    }

                }else if(dUserName.equals("") || dUserBio.equals("") || dGender.equals("") || dUserType.equals("") ){
                    Toast.makeText(getApplicationContext(),"Please Fill Up All", Toast.LENGTH_SHORT).show();;
                }else if(dUserName.equals("NO") || dUserBio.equals("NO") || dGender.equals("NO") || dUserType.equals("NO") ){
                    Toast.makeText(getApplicationContext(),"Please Fill Up All", Toast.LENGTH_SHORT).show();;
                }else{
                    String date = String.valueOf(System.currentTimeMillis());
                    dUserRegistrationDate = date;
                    dUserLastActivity = date;
                    long diDate = System.currentTimeMillis();
                    long diDate2 = 1623163000000L;
                    diDate = diDate - diDate2 ;
                    diUserLastActivity = diDate;
                    if(dUserPhone.equals("")){
                        dUserPhone = "123";
                    }
                    if(dGender.equals("Female")){
                        diGender = 2;   //FEMALE
                    }else{
                        diGender = 1;   //MALE
                    }
                    if(dUserType.equals("User")){
                        diUserType = 1;
                    }else if(dUserType.equals("Rider")){
                        diUserType = 2;
                    }else if(dUserType.equals("Admin")){
                        diUserType = 3;
                    }else {
                        diUserType = 1;
                    }
                    UploadCropedImageFunction(imageUriResultCrop);
                }
            }
        });
    }
    String dsEditMode = "NO";
/*    private void getIntentMethod() {
        final Intent intent = getIntent();
        if (intent.getExtras() != null) {
            dsEditMode = intent.getExtras().getString("dsEditMode");
            if (TextUtils.isEmpty(dsEditMode)) {
                dsEditMode = "NO";
                Toast.makeText(getApplicationContext(), "intent NULL  ", Toast.LENGTH_SHORT).show();
            } else if (dsEditMode.equals("")) {
                dsEditMode = "NO";
                Toast.makeText(getApplicationContext(), "intent 404", Toast.LENGTH_SHORT).show();
            } else {
                if(dsEditMode.equals("True")){
                    dHomeAddress = mUserInfoHomeAddress.getText().toString();
                    if(dHomeAddress.length()==0)
                        RetriveUserOldInformation();
                    else
                        dRetrivePhotoURL = "NO";
                }

            }
        }else{
            Toast.makeText(getApplicationContext(), "intent not Exists", Toast.LENGTH_SHORT).show();
        }
    }*/
    String dRetrivePhotoURL = "NO";
    private void RetriveUserOldInformation() {

    }

    private int diUserType = 1;
    //Uplaoding Photo to FireStorage
    private void UploadCropedImageFunction(Uri filePath) {
        if(filePath != null){
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            //System.currentTimeMills();
            ref = storageReference.child("ShoeBox/UserPicture/"+ dUserUID +"."+getFileExtention(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //Photo Uploaded now get the URL
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            float dProPicServerSize = taskSnapshot.getTotalByteCount() /1024 ;
                            diSize = (int)dProPicServerSize;
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String dPhotoURL = uri.toString();

                                    //Setting PhotoURL for
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(dUserName)
                                            .setPhotoUri(uri)
                                            .build();
                                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Photo URL Attached",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Photo URL Attach Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();


                                    Map<String, Object> note = new HashMap<>();
                                    note.put("name", dUserName);
                                    note.put("email", dUserEmail); //map is done
                                    note.put("uid",dUserUID);
                                    note.put("home_address",dUserBio);
                                    note.put("imageURL",dPhotoURL);
                                    note.put("cell_no",dUserPhone);;
                                    note.put("usertype",dUserType);;
                                    note.put("cart_uid","NO");      //String
                                    note.put("choiced","NO");      //address   new
                                    note.put("balance",0);        //address   new

                                    db.collection("AllShop").document("ShoeBox").collection("Users").document(dUserUID).set(note)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    mUserInfoUpdateBtn.setText("UPDATED");
                                                    mUserInfoName.setText("");
                                                    mUserInfoHomeAddress.setText("");
                                                    mUserInfoPhoneNo.setText("");
                                                    finish();
//
                                                    Intent intent = new Intent(LoginRegistration.this, MainActivity.class); //ERROR change the target class
                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    startActivity(intent);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                            mUserInfoUpdateBtn.setText("FAILED Information Sent");
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
                            mUserInfoUpdateBtn.setText("Failed Photo Upload");
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override   //Selecting Image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null){
            //Photo Successfully Selected
            dRetrivePhotoURL = "NO";
            imageUri_storage = data.getData();
            String dFileSize = getSize(imageUri_storage);       //GETTING IMAGE FILE SIZE
            double  dFileSizeDouble = Double.parseDouble(dFileSize);
            int dMB = 1000;
            dFileSizeDouble =  dFileSizeDouble/dMB;
            //dFileSizeDouble =  dFileSizeDouble/dMB;

            if(dFileSizeDouble <= 5000){
                Picasso.get().load(imageUri_storage).resize(200, 200).centerCrop().into(mUserProfilePic);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                //startCrop(imageUri_storage);
                dRetrivePhotoURL = "NO";
                imageUriResultCrop = imageUri_storage;
                Picasso.get().load(imageUriResultCrop).into(mUserProfilePic);

            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }


        }else{
            Toast.makeText(this, "onActivityResult! Error",Toast.LENGTH_SHORT).show();
        }
    }
    //Croping Function
    Random random = new Random();


    private String getFileExtention(Uri uri){   //IMAGE
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
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