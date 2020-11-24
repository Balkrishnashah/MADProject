package com.balkrishnashah.firebasechatmessenger.new_messeges.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.balkrishnashah.firebasechatmessenger.Adapter.UserAdapter;
import com.balkrishnashah.firebasechatmessenger.R;
import com.balkrishnashah.firebasechatmessenger.new_messeges.adapter.ChatUserAdapter;
import com.balkrishnashah.firebasechatmessenger.user_creation.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class FindUserFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> chatUsers;

    public FindUserFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.find_user_frament, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatUsers = new ArrayList<>();
        findUsers();
        return view;

    }
    private void findUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatMessenger/BchatUser");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
//                    Toast.makeText(getContext(), "enter in find user", Toast.LENGTH_SHORT).show();
                    Log.d("UserFragment","enter in find user function");
                    User display_user = datasnapshot.getValue(User.class);
                    assert display_user != null;
                    Log.d("UsersFragment",display_user.getDisplayName());
                    if (display_user.getUserId() != null && !display_user.getUserId().equals(firebaseUser.getUid())) {
                        chatUsers.add(display_user);
                    }
                    ChatUserAdapter mChatUserAdapter = new ChatUserAdapter(chatUsers ,getContext());
                    recyclerView.setAdapter(mChatUserAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
