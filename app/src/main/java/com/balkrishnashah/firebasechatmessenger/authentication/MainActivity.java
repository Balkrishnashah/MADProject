package com.balkrishnashah.firebasechatmessenger.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.balkrishnashah.firebasechatmessenger.message.ChatLogActivty;
import com.balkrishnashah.firebasechatmessenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneNumber = findViewById(R.id.enterYourPhone);
        mAuth=FirebaseAuth.getInstance();


        findViewById(R.id.sendOtpBtnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile=mPhoneNumber.getText().toString();
                Intent intent=new Intent(MainActivity.this, VerifyPhoneNumber.class);
                intent.putExtra("mobile",mobile);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //        To keep the user Logged In
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(getApplicationContext(), ChatLogActivty.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }
}