package com.balkrishnashah.firebasechatmessenger.fragments;


import com.balkrishnashah.firebasechatmessenger.Notifications.MyResponse;
import com.balkrishnashah.firebasechatmessenger.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=chat-app-23b5e"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
