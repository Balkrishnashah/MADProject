package com.balkrishnashah.firebasechatmessenger.new_messeges;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.balkrishnashah.firebasechatmessenger.R;
import com.balkrishnashah.firebasechatmessenger.authentication.MainActivity;
import com.balkrishnashah.firebasechatmessenger.new_messeges.adapter.ViewPagerAdapter;
import com.balkrishnashah.firebasechatmessenger.user_creation.User;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatActivity extends AppCompatActivity {

    Button signout;
    TabLayout tabLayout;
    ViewPager viewPager;
    CircleImageView mCircleImageView;
    TextView mDisplayName;
    FirebaseUser firebaseUser;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        mCircleImageView = findViewById(R.id.profile_image);
        mDisplayName = findViewById(R.id.username);
        fetchUserData();
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);


        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        signout = findViewById(R.id.sign_out_user_id);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        final ViewPagerAdapter adapter = new ViewPagerAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void fetchUserData(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference("ChatMessenger/BchatUser").child(user.getPhoneNumber());
            mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            User mUser = snapshot.getValue(User.class);
//                            mUser = snapshot.getValue(User.class);
                            assert mUser != null;
                            mDisplayName.setText(mUser.getDisplayName());
                            Glide.with(getApplicationContext()).asBitmap().load(mUser.getProfileImageUrl()).into(mCircleImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("ChatMessenger/BchatUser").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }
}