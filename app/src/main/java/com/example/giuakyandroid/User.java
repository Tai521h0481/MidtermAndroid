package com.example.giuakyandroid;

import android.util.Log;

import java.io.Serializable;

public class User implements Serializable {
    private String userId, name, email, password, role, phone, avatar, status;
    private Integer age;
    private Long loginHistory;
    public User() {
    }
    public User(String name, String email, String password, String role, String status, Integer age, String phone, String avatar, Long loginHistory) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.age = age;
        this.phone = phone;
        this.avatar = avatar;
        this.status = status;
        this.loginHistory = loginHistory;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getLoginHistory() {
        return loginHistory;
    }

    public void setLoginHistory(Long loginHistory) {
        this.loginHistory = loginHistory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", phone='" + phone + '\'' +
                ", avatar='" + avatar + '\'' +
                ", status='" + status + '\'' +
                ", loginHistory='" + loginHistory + '\'' +
                ", age=" + age +
                '}';
    }
}
