package com.example.libshop.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.example.libshop.LoginCheck;
import com.example.libshop.MainActivity;
import com.example.libshop.View.LoginRegistration;
import com.example.libshop.View.PB_Cart;
import com.example.libshop.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;

    //Firebase Auth
    private String dUserUID = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    //Initialize
    TextView mUserName;
    ImageView mUserImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mUserName = binding.dashboardusername;
        mUserImage = binding.dashboarduserimage;

        Button mLoginBtn = binding.dashboardloginbtn2;
        Button mCartBtn = binding.dashboardCartBtn;
        Log.d("DashboardFragment", "Profile:1 Start");
        //Auth Logic
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d("DashboardFragment", "Profile:1 user not null");
                    dUserUID = user.getUid();
                    mLoginBtn.setText("Sign Out");
                    mCartBtn.setVisibility(View.VISIBLE);
                    mUserImage.setVisibility(View.VISIBLE);
                    mUserName.setVisibility(View.VISIBLE);
                    getUserDataMethod();
                }else{
                    Log.d("DashboardFragment", "Profile:1 user not null");
                    mLoginBtn.setText("Login Here");
                    mCartBtn.setVisibility(View.GONE);
                    mUserImage.setVisibility(View.GONE);
                    mUserName.setVisibility(View.GONE);
                    /*Toast.makeText(getApplicationContext(),"Please Login", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);*/
                }
            }


        };

        // Calling
        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mUserName.setText(s);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dUserUID.equals("NO")){
                    Intent intent = new Intent(getContext(), LoginCheck.class);
                    //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }else{
                    mAuth.signOut();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }

            }
        });
        mCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), PB_Cart.class);
                //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        return root;
    }
    private void getUserDataMethod() {
        Log.d("DashboardFragment", "Profile:1 getUserDataMethod()");
        profileViewModel.getUserData(dUserUID).observe(getViewLifecycleOwner(), new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(DocumentSnapshot documentSnapshot) {
                Log.d("DashboardFragment", "Profile:1 DocumentSnapshot onChanged");
                if(documentSnapshot.exists()){
                    Log.d("DashboardFragment", "Profile:1 DocumentSnapshotVM exist");
                        String dUserName = documentSnapshot.getString("name");
                        String dUserPhotoURL = documentSnapshot.getString("imageURL");
                        Picasso.get().load(dUserPhotoURL).into(mUserImage); //Library
                        mUserName.setText(dUserName);
                }else{
                    Log.d("DashboardFragment", "Profile:1 DocumentSnapshotVM not exist");
                    Toast.makeText(getContext(),"User Data 404", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getContext(), LoginRegistration.class);
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
}