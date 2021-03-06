package com.sam.globalRentalCar.bean;

/**
 * description 当前播放视频的作者Userbean切换
 */
public class CurUserBean {
    private VideoBean.UserBean userBean;

    public CurUserBean(VideoBean.UserBean userBean) {
        this.userBean = userBean;
    }

    public VideoBean.UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(VideoBean.UserBean userBean) {
        this.userBean = userBean;
    }
}
