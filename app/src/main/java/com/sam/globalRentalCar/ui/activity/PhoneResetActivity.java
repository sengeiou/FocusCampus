package com.sam.globalRentalCar.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sam.globalRentalCar.R;
import com.sam.globalRentalCar.aop.DebugLog;
import com.sam.globalRentalCar.aop.SingleClick;
import com.sam.globalRentalCar.common.MyActivity;
import com.sam.globalRentalCar.helper.InputTextHelper;
import com.sam.globalRentalCar.constant.IntentKey;
import com.sam.widget.view.CountdownView;

import butterknife.BindView;

/**
 * desc   : 更换手机号
 */
public final class PhoneResetActivity extends MyActivity {

    @DebugLog
    public static void start(Context context, String code) {
        Intent intent = new Intent(context, PasswordResetActivity.class);
        intent.putExtra(IntentKey.CODE, code);
        context.startActivity(intent);
    }

    @BindView(R.id.et_phone_reset_phone)
    EditText mPhoneView;
    @BindView(R.id.et_phone_reset_code)
    EditText mCodeView;

    @BindView(R.id.cv_phone_reset_countdown)
    CountdownView mCountdownView;

    @BindView(R.id.btn_phone_reset_commit)
    Button mCommitView;

    /**
     * 验证码
     */
    private String mCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_reset;
    }

    @Override
    protected void initView() {
        InputTextHelper.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .setMain(mCommitView)
                .setListener(helper -> mPhoneView.getText().toString().length() == 11 && mCodeView.getText().toString().length() == 4)
                .build();

        setOnClickListener(R.id.cv_phone_reset_countdown, R.id.btn_phone_reset_commit);
    }

    @Override
    protected void initData() {
        mCode = getString(IntentKey.CODE);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_phone_reset_countdown:
                // 获取验证码
                if (mPhoneView.getText().toString().length() != 11) {
                    toast(R.string.common_phone_input_error);
                    return;
                }

                if (true) {
                    toast(R.string.common_code_send_hint);
                    mCountdownView.start();
                    return;
                }
                break;
            case R.id.btn_phone_reset_commit:
                if (true) {
                    toast(R.string.phone_reset_commit_succeed);
                    finish();
                    return;
                }
                break;
            default:
                break;
        }
    }
}