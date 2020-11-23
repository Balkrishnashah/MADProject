package com.balkrishnashah.firebasechatmessenger.user_creation;

public class User {
    String userId;
    String displayName;
    String profileImageUrl;
    String phone;
    String status;

    public User() {
    }

    public User(String userId, String displayName, String profileImageUrl, String phone,String status) {
        this.userId = userId;
        this.displayName = displayName;
        this.profileImageUrl = profileImageUrl;
        this.phone = phone;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(String uid) {
        this.userId = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String username) {
        this.displayName = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
