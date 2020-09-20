package com.sam.rental.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.sam.rental.R;
import com.sam.rental.common.MyActivity;

/**
 * 身份认证页面
 */
public class IdentityAuthenticationActivity extends MyActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_identity_authentication;
    }

    @Override
    protected void initView() {
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UpLoadCertificatesActivity.class);
            }
        });
    }

    @Override
    protected void initData() {

    }
}
