package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.expensetracker.databinding.ActivityAboutUsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class AboutUsActivity extends AppCompatActivity {
    ActivityAboutUsBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private ImageView appLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        appLogo = findViewById(R.id.iv_appLogo);
        Picasso.get().load("https://icons.iconarchive.com/icons/flat-icons.com/flat/256/Wallet-icon.png").into(appLogo);

    }
}