package com.sam.globalRentalCar.http.request;

/**
 * author:sam
 * time:2020/08/29
 * desc: 修改个人资料需要请求数据封装
 * version:1.0
 */
public class ModifyMessageRequestBean {

    /**
     * headImg : string
     * nickName : string
     * phone : string
     * realName : string
     * userBirthday : string
     * userDesc : string
     * userId : 0
     * userLocation : string
     * userSex : 0
     */

    private String headImg;
    private String nickName;
    private String phone;
    private String realName;
    private String userBirthday;
    private String userDesc;
    private String userId;
    private String userLocation;
    private int userSex;

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }
}
