package com.sam.globalRentalCar.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.sdk.android.vod.upload.common.utils.StringUtil;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.sam.globalRentalCar.R;
import com.sam.globalRentalCar.aop.SingleClick;
import com.sam.globalRentalCar.common.MyActivity;
import com.sam.globalRentalCar.constant.Constant;
import com.sam.globalRentalCar.http.net.RetrofitClient;
import com.sam.globalRentalCar.http.request.LoginRequestBean;
import com.sam.globalRentalCar.http.request.LoginWithAccountRequestBean;
import com.sam.globalRentalCar.http.response.LoginBean;
import com.sam.globalRentalCar.utils.IpUtils;
import com.sam.globalRentalCar.utils.SPUtils;
import com.sam.rentalcar.wxapi.WXEntryActivity;
import com.sam.umeng.Platform;
import com.sam.umeng.UmengClient;
import com.sam.umeng.UmengLogin;

import java.net.HttpURLConnection;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * desc   : 账号密码登录界面
 */
public final class AccountLoginActivity extends MyActivity
        implements UmengLogin.OnLoginListener {

    @BindView(R.id.account_login_title)
    TitleBar mTitleBarAccountTitle;

    @BindView(R.id.et_user_phone)
    EditText mPhoneView;

    @BindView(R.id.et_user_code)
    EditText mCodeView;

    @BindView(R.id.password_forget)
    TextView mForgetTextView;

    @BindView(R.id.btn_login_commit)
    Button mLoginCommitView;

    @BindView(R.id.v_login_blank)
    View mBlankView;

    @BindView(R.id.ll_login_other)
    View mOtherView;
    @BindView(R.id.iv_login_qq)
    View mQQView;
    @BindView(R.id.iv_login_wx)
    View mWeChatView;

    String traceId;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_account;
    }

    @Override
    protected void initView() {
        mTitleBarAccountTitle.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {

            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {
                // 进入账号注册界面
                startActivity(RegisterActivity.class);
            }
        });
        setOnClickListener(R.id.password_forget, R.id.btn_login_commit, R.id.iv_login_qq, R.id.iv_login_wx);
    }

    @Override
    protected void initData() {
        // 判断用户当前有没有安装 QQ
        if (!UmengClient.isAppInstalled(this, Platform.QQ)) {
            mQQView.setVisibility(View.GONE);
        }

        // 判断用户当前有没有安装微信
        if (!UmengClient.isAppInstalled(this, Platform.WECHAT)) {
            mWeChatView.setVisibility(View.GONE);
        }

        // 如果这两个都没有安装就隐藏提示
        if (mQQView.getVisibility() == View.GONE && mWeChatView.getVisibility() == View.GONE) {
            mOtherView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onLeftClick(View v) {
        // 跳转到主界面
        //startActivity(HomeActivity.class);
        finish();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //登录
            case R.id.btn_login_commit:
                showDialog();
                if (mPhoneView.getText().toString().length() != 11) {
                    toast(R.string.common_phone_input_error);
                    hideDialog();
                    return;
                }
                if (StringUtil.isEmpty(mCodeView.getText().toString())) {
                    toast("请输入密码");
                    hideDialog();
                    return;
                }
                LoginWithAccountRequestBean loginRequestBean = new LoginWithAccountRequestBean();
                loginRequestBean.setIpAddress(IpUtils.getHostIP());
                loginRequestBean.setAccount(mPhoneView.getText().toString());
                loginRequestBean.setPwd(mCodeView.getText().toString());
                RetrofitClient.getRetrofitService().loginWithAccount(loginRequestBean)
                        .enqueue(new Callback<LoginBean>() {
                            @Override
                            public void onResponse(Call<LoginBean> call, Response<LoginBean> response) {
                                Log.d("login", response.code() + "");
                                int Code = Integer.parseInt(response.body().getCode());
                                if (Code == HttpURLConnection.HTTP_OK) {
                                    EMClient.getInstance().login(response.body().getData().getHxuid(), response.body().getData().getHxpwd(), new EMCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            hideDialog();

                                        }

                                        @Override
                                        public void onError(int i, String s) {
                                            hideDialog();
//                                            toast("登录失败" + s.toString());
                                        }

                                        @Override
                                        public void onProgress(int i, String s) {
                                            showDialog();
                                        }
                                    });
                                    SPUtils.getInstance(AccountLoginActivity.this).put("token", response.body().getData().getToken());
                                    SPUtils.getInstance(AccountLoginActivity.this).put("HeadImage", response.body().getData().getHeadImg());
                                    SPUtils.getInstance(AccountLoginActivity.this).put("NickName", response.body().getData().getNickName());
                                    SPUtils.getInstance(AccountLoginActivity.this).put("UserId", response.body().getData().getUserId() + "");
                                    SPUtils.getInstance(AccountLoginActivity.this).put("userSex", response.body().getData().getUserSex());
                                    SPUtils.getInstance(AccountLoginActivity.this).put("userDesc", response.body().getData().getUserDesc());
                                    SPUtils.getInstance(AccountLoginActivity.this).put("userBirthday", response.body().getData().getUserBirthday());
                                    SPUtils.getInstance(AccountLoginActivity.this).put("userLocation", response.body().getData().getUserLocation());
                                    startActivity(HomeActivity.class);
                                } else {
                                    hideDialog();
                                    toast("登录失败" + response.message());
                                }

                            }

                            @Override
                            public void onFailure(Call<LoginBean> call, Throwable t) {
                                hideDialog();
                                toast("登录失败" + t.getMessage().toString());
                            }
                        });

                break;
            case R.id.password_forget:
                // 进入忘记密码界面
                startActivity(new Intent(AccountLoginActivity.this, PasswordForgetActivity.class));
                break;
            case R.id.iv_login_qq:
            case R.id.iv_login_wx:
                toast("第三方 AppID 和 AppKey");
                Platform platform;
                switch (v.getId()) {
                    case R.id.iv_login_qq:
                        platform = Platform.QQ;
                        break;
                    case R.id.iv_login_wx:
                        platform = Platform.WECHAT;
                        toast("微信 " + WXEntryActivity.class.getSimpleName() + " 类所在的包名");
                        break;
                    default:
                        throw new IllegalStateException("are you ok?");
                }
                UmengClient.login(this, platform, this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 友盟登录回调
        UmengClient.onActivityResult(this, requestCode, resultCode, data);
    }

    /**
     * {@link UmengLogin.OnLoginListener}
     */

    /**
     * 授权成功的回调
     *
     * @param platform 平台名称
     * @param data     用户资料返回
     */
    @Override
    public void onSucceed(Platform platform, UmengLogin.LoginData data) {
        // 判断第三方登录的平台
        switch (platform) {
            case QQ:
                break;
            case WECHAT:
                break;
            default:
                break;
        }
        toast("昵称：" + data.getName() + "\n" + "性别：" + data.getSex());
        toast("id：" + data.getId());
        toast("token：" + data.getToken());
    }

    /**
     * 授权失败的回调
     *
     * @param platform 平台名称
     * @param t        错误原因
     */
    @Override
    public void onError(Platform platform, Throwable t) {
        toast("第三方登录出错：" + t.getMessage());
    }

    /**
     * 授权取消的回调
     *
     * @param platform 平台名称
     */
    @Override
    public void onCancel(Platform platform) {
        toast("取消第三方登录");
    }

    @Override
    public boolean isSwipeEnable() {
        return false;
    }
}