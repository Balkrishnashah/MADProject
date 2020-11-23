package com.balkrishnashah.firebasechatmessenger.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.balkrishnashah.firebasechatmessenger.R;
import com.balkrishnashah.firebasechatmessenger.user_creation.UserCreationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumber extends AppCompatActivity {

    private String mVerification;
    private FirebaseAuth mAuth;
    private EditText mCode1,mCode2,mCode3,mCode4,mCode5,mCode6;
    private TextView mPhone_no;
    ProgressBar mProgressBar;
    private String bNewUserFlag = "False";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        mAuth=FirebaseAuth.getInstance();
        mCode1=findViewById(R.id.otp_1);
        mCode2 = findViewById(R.id.otp_2);
        mCode3 = findViewById(R.id.otp_3);
        mCode4 = findViewById(R.id.otp_4);
        mCode5 = findViewById(R.id.otp_5);
        mCode6 = findViewById(R.id.otp_6);
        mPhone_no = findViewById(R.id.phone_no_ID);
        mProgressBar = findViewById(R.id.pBar);

//        Toast.makeText(getApplicationContext(), "otp is : "+mCode1.getText().toString()+mCode2.getText().toString()+mCode3.getText().toString()+mCode4.getText().toString()+mCode5.getText().toString()+mCode6.getText().toString(),Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        String mobile = intent.getStringExtra("mobile");
//        Toast.makeText(getApplicationContext(),"mobile is" + mobile,Toast.LENGTH_SHORT).show();
        mPhone_no.setText("+91"+mobile);
        sendVerificationCode(mobile);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,             // Activity (for callback binding)
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks()

    {
        @Override
        public void onCodeSent (String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
        {
            super.onCodeSent(s, forceResendingToken);
            mVerification = s;
            Toast.makeText(getApplicationContext(), "Code sent", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onVerificationCompleted (PhoneAuthCredential phoneAuthCredential){
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){

                List<String> code_break = Collections.singletonList(code);
//                Toast.makeText(getApplicationContext(), "codebreak "+code_break, Toast.LENGTH_SHORT).show();
//                mCode.setText(code);
                int size = 1;
                String[] tokens = code.split("(?<=\\G.{" + size + "})");
//                System.out.println(Arrays.toString(tokens));
                mCode1.setText(tokens[0]);
                mCode2.setText(tokens[1]);
                mCode3.setText(tokens[2]);
                mCode4.setText(tokens[3]);
                mCode5.setText(tokens[4]);
                mCode6.setText(tokens[5]);
//                verifyCode(code);
                findViewById(R.id.verify_otp_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        findViewById(R.id.verification_text).setVisibility(View.VISIBLE);
                        findViewById(R.id.verify_otp_btn).setVisibility(View.GONE);
                        if (mCode1.length() !=0 && mCode3.length() !=0 && mCode3.length() !=0 && mCode4.length() !=0 && mCode5.length() !=0 && mCode6.length() !=0)
                            verifyCode(code);
                    }
                });
            }
        }

        @Override
        public void onVerificationFailed (FirebaseException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    };

    public void verifyCode(String code){
        try{
            PhoneAuthCredential credential= PhoneAuthProvider.getCredential(mVerification,code);
            checkForNewUser();
            signInWithPhone(credential);
        }catch (Exception e){
            Toast toast = Toast.makeText(getApplicationContext(), "Verification Code is wrong, try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    private void checkForNewUser(){

        //check for new user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference("ChatMessenger").child("NewUser").child(user.getUid());
            mUserDB.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        bNewUserFlag = "True";
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    private void signInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneNumber.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                    final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference("ChatMessenger").child("NewUser").child(user.getUid());
                                    mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.exists()) {
                                                Map<String, Object> userMap = new HashMap<>();
                                                userMap.put("phone", user.getPhoneNumber());
                                                userMap.put("name", user.getDisplayName());
                                                mUserDB.updateChildren(userMap);
                                            }
                                            mProgressBar.setVisibility(View.GONE);
                                            findViewById(R.id.verification_text).setVisibility(View.GONE);

                                            Intent intent = new Intent(VerifyPhoneNumber.this, UserCreationActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
//                                else {
//                                    Intent intent = new Intent(VerifyPhoneNumber.this, ChatLogActivty.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                }

                            }
                        }
                });

    }



}