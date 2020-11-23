package com.balkrishnashah.firebasechatmessenger.user_creation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balkrishnashah.firebasechatmessenger.message.ChatLogActivty;
import com.balkrishnashah.firebasechatmessenger.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class UserCreationActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView bProfileImageView;
    private EditText bUserName;
    private Button bContinue,bSignOut;
    public static final int PICK_IMAGE = 1;
    private Uri selectedImageUri;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_creation);

        init();
    }
    private void init(){
        bProfileImageView = findViewById(R.id.profile_image);
        bUserName = findViewById(R.id.username);
        bContinue = findViewById(R.id.continue_btn);
        bSignOut = findViewById(R.id.sign_out_user_id);
        mTextView = findViewById(R.id.creating_profile_text);
        mProgressBar = findViewById(R.id.progress_bar);

        bProfileImageView.setOnClickListener(this);
        bContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_image:
                Intent intent =new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                intent.putExtra("crop", "true");
                intent.putExtra("scale", true);
                intent.putExtra("outputX", 256);
                intent.putExtra("outputY", 256);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 1);

                break;
            case R.id.continue_btn:
                String username = bUserName.getText().toString();
                if (!username.isEmpty()){
                    mTextView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    uploadImageToFirebase();
                }else {
                    Toasty.info(getApplicationContext(), "enter username please").show();
                }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            selectedImageUri = data.getData();
            Log.d("UserCreationActivity", "uri :"+selectedImageUri);
            final Bundle extras = data.getExtras();
            try {
                Bitmap newProfilePic = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                selectedImageUri);
                bProfileImageView.setImageBitmap(newProfilePic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadImageToFirebase(){
        String filename = UUID.randomUUID().toString();
//        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference("/Images/"+filename);

        firebaseStorage.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("UserCreationActivity ","Successfully Uploaded Image:"+ taskSnapshot.getMetadata().getPath());
                firebaseStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        saveUserToFirebase(uri.toString());
                    }
                });
            }
        });

    }

    private void saveUserToFirebase(String profileImageUrl){
        String uid = FirebaseAuth.getInstance().getUid();
        String username = bUserName.getText().toString();
        User mUser = new User(uid,username, profileImageUrl, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),"online");
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference("ChatMessenger").child("BchatUser");
        assert uid != null;
        mUserDB.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())).setValue(mUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                delete_data_from_new_user();
            }
        });
    }

    private void delete_data_from_new_user(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatMessenger").child("NewUser");
        Query newUserQuery = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("phone").equalTo(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("UserCreationActivity","enter into delte method of new node");
                for (DataSnapshot newUserSnapshot: dataSnapshot.getChildren()) {
                    newUserSnapshot.getRef().removeValue();
                    Toasty.info(getApplicationContext(), "save to db").show();
                    mTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), ChatLogActivty.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserCreationActivity", "onCancelled", databaseError.toException());
            }
        });
    }

    public void saveImage(Context context, Bitmap bitmap, String name, String extension){
        name = name + "." + extension;
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadImageBitmap(Context context,String name,String extension){
        name = name + "." + extension;
        FileInputStream fileInputStream;
        Bitmap bitmap = null;
        try{
            fileInputStream = context.openFileInput(name);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }

}