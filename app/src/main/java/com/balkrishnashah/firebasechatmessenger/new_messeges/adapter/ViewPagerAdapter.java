package com.balkrishnashah.firebasechatmessenger.new_messeges.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.balkrishnashah.firebasechatmessenger.Model.Chat;
import com.balkrishnashah.firebasechatmessenger.new_messeges.fragment.ChatFragment;
import com.balkrishnashah.firebasechatmessenger.new_messeges.fragment.FindUserFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public ViewPagerAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
         context = c;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                FindUserFragment findUserFragment = new FindUserFragment();
                return findUserFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
