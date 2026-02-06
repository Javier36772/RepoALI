package com.gastosapp.network.model;

import com.gastosapp.model.User;

public class LoginResponse {
    private boolean success;
    private String message;
    private User user;
    private String token;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
