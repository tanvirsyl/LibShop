package com.example.libshop.View;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.libshop.MainActivity;
import com.example.libshop.Model.ProductModel;
import com.example.libshop.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProductDetails extends AppCompatActivity {

    private ImageView mProductImage;
    private Button mProductBookedBtn;
    private TextView mProductName, mProductAbout, mProductTimeTable, mProductRatingTxt, mProductInfo, mProductTotalOrderText, mProductTotalViewText, mProductPriceText, mProductAddress;

    private ImageView mRedColorImageBtn, mBlackColorImageBtn, mWhiteColorImageBtn;
    private ImageView m38SizeImageBtn, m40SizeImageBtn, m42SizeImageBtn, m44SizeImageBtn ;
    private Button mProductAddBtn;

    //Auth
    private String dUserUID  = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    private String dsProductColor = "NO", dsProductSize = "NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        mProductImage  = (ImageView)findViewById(R.id.product_profile_imageview);
        mProductName = (TextView)findViewById(R.id.product_profile_name);
        mProductRatingTxt = (TextView)findViewById(R.id.product_profile_rating_text);
        mProductInfo = (TextView)findViewById(R.id.product_profile_info_about);
        mProductTotalOrderText = (TextView)findViewById(R.id.product_profile_total_order);
        mProductTotalViewText =(TextView)findViewById(R.id.product_profile_total_view);
        mProductPriceText = (TextView)findViewById(R.id.product_profile_charge);
        mProductBookedBtn = (Button)findViewById(R.id.product_profile_addbtn);

        mRedColorImageBtn = (ImageView)findViewById(R.id.product_profile_red_color_img) ;
        mBlackColorImageBtn = (ImageView)findViewById(R.id.product_profile_black_color_img) ;
        mWhiteColorImageBtn = (ImageView)findViewById(R.id.product_profile_white_color_img) ;

        m38SizeImageBtn = (ImageView)findViewById(R.id.product_profile_38_size_img) ;
        m40SizeImageBtn = (ImageView)findViewById(R.id.product_profile_40_size_img) ;
        m42SizeImageBtn = (ImageView)findViewById(R.id.product_profile_42_size_img) ;
        m44SizeImageBtn = (ImageView)findViewById(R.id.product_profile_44_size_img) ;

        mProductAddBtn = (Button) findViewById(R.id.product_profile_addbtn) ;

        getIntentMethod();

        //Firebase Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if(user != null){
                    dUserUID = user.getUid();

                }else{
                    Toast.makeText(getApplicationContext(),"Login", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);    //passing data
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        ////////////Btn Click
        mProductAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dsProductColor.equals("NO")){
                    Toast.makeText(getApplicationContext(),"Please Select Color", Toast.LENGTH_SHORT).show();
                }else if(dsProductSize.equals("NO")){
                    Toast.makeText(getApplicationContext(),"Please Select Size", Toast.LENGTH_SHORT).show();
                }else{
                    UpdateCartServer("Add");
                }

            }
        });


        mRedColorImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsProductColor = "RED";
                mRedColorImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorRed) );
                mBlackColorImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500) );
                mWhiteColorImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500) );
            }
        });
        mBlackColorImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsProductColor = "BLACK";
                mRedColorImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500) );
                mBlackColorImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack) );
                mWhiteColorImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500) );
            }
        });
        mWhiteColorImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsProductColor = "WHITE";
                mRedColorImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500));
                mBlackColorImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500));
                mWhiteColorImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorLightGray));
            }
        });

        m38SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
        m40SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
        m42SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
        m44SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
        m38SizeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsProductSize = "38";
                m38SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500) );
                m40SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
                m42SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray ) );
                m44SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );

            }
        });
        m40SizeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsProductSize = "40";
                m38SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
                m40SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500) );
                m42SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
                m44SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );

            }
        });
        m42SizeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsProductSize = "42";
                m38SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
                m40SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
                m42SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500) );
                m44SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );

            }
        });
        m44SizeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsProductSize = "44";
                m38SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
                m40SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
                m42SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGray) );
                m44SizeImageBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500) );

            }
        });


    }
    ProductModel ProductModel = new ProductModel();
    private void getProductData(String dsProductUID) {
        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 mProductModelLiveData start");
        notebookRef =  db.collection("AllShop").document("ShoeBox").collection("Product");
        notebookRef.document(dsProductUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    ProductModel = documentSnapshot.toObject(ProductModel.class);
                    String ProductUID = documentSnapshot.getId();
                    ProductModel.setProductUID(ProductUID);

                    String ProductName = ProductModel.getProductName();
                    String ProductPhotoUrl = ProductModel.getProductPhotoUrl();
                    String ProductBio =  ProductModel.getProductBio();
                    String ProductCreator =  ProductModel.getProductCreator();
                    String ProductExtra =  ProductModel.getProductExtra();

                    String ProductCategory =  ProductModel.getProductCategory();
                    long ProductiViews =  ProductModel.getProductiViews();
                    long ProductiPriority =  ProductModel.getProductiPriority();
                    long ProductiRating = ProductModel.getProductiRating();
                    long ProductiOrders =  ProductModel.getProductiOrders();
                    long ProductiPrice =  ProductModel.getProductiPrice();
                    long ProductiDiscount =  ProductModel.getProductiDiscount();

                    Picasso.get().load(ProductPhotoUrl).fit().centerCrop().into(mProductImage);
                    mProductName.setText(ProductName);
                    mProductInfo.setText(ProductBio);
                    mProductRatingTxt.setText(String.valueOf(ProductiRating));
                    /*mProductTotalOrderText.setText(String.valueOf(ProductiOrders)+"+ Orders");
                    mProductTotalViewText.setText(String.valueOf(ProductiViews)+"+ Viewed");
                    mProductPriceText.setText(String.valueOf(ProductiPrice)+"+ Charge");*/
                }else{
                    Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public String dsCartPresentUID = "NO";
    private void UpdateCartServer(String type) {

        Map<String, Object> cart = new HashMap<>();
        String dsProductUID = ProductModel.getProductUID();
        cart.put("ProductUID", dsProductUID);
        cart.put("ProductName", ProductModel.getProductName());
        cart.put("ProductPHOTO", ProductModel.getProductPhotoUrl()); //map is done
        cart.put("ProductColor", dsProductColor);
        cart.put("ProductSize", dsProductSize);
        cart.put("ProductiPrice", ProductModel.getProductiPrice()); //Integer

        FieldValue ddDate =  FieldValue.serverTimestamp();
        cart.put("ProductOrderTime", ddDate); //Integer


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference mCartRef, mUserUidRef;
        mUserUidRef = db.collection("AllShop").document("ShoeBox").collection("Users").document(dUserUID);

        mUserUidRef.collection("MyCart").document(dsProductUID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    long dlProductQuantity = documentSnapshot.getLong("ProductQuantity");
                    dlProductQuantity += 1;
                    mUserUidRef.collection("MyCart").document(dsProductUID).update("ProductQuantity",dlProductQuantity);
                    Toast.makeText(getApplicationContext(), "Quantity Increased" , Toast.LENGTH_SHORT).show();
                }else{

                        //First Time Adding Cart
                        cart.put("ProductQuantity", 1); //Integer
                        mUserUidRef.collection("MyCart").document(dsProductUID).set(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "First time Added" , Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(getApplicationContext(), "First time Added Failed !" , Toast.LENGTH_SHORT).show();
                            }
                        });

                }
            }
        });


    }

    String dsCategoryUID = "NO", dsProductUID = "NO";
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsCategoryUID = intent.getExtras().getString("dsCategoryUID");
            dsProductUID = intent.getExtras().getString("dsProductUID");
            intentFoundError = CheckIntentMethod(dsCategoryUID);
            intentFoundError = CheckIntentMethod(dsProductUID);

            if(!intentFoundError){
                getProductData(dsProductUID);
            }else{
                Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;
            }
        }else{
            dsCategoryUID = "NO";
            dsProductUID = "NO";
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