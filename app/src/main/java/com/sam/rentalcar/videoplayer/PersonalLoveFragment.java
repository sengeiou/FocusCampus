package com.sam.rentalcar.videoplayer;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.rentalcar.R;
import com.sam.rentalcar.adapter.GridVideoAdapter;
import com.sam.rentalcar.bean.UserProductionOrLoveBean;
import com.sam.rentalcar.common.MyFragment;
import com.sam.rentalcar.http.net.RetrofitClient;
import com.sam.rentalcar.ui.activity.HomeActivity;

import java.net.HttpURLConnection;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 个人喜欢的Fragment
 */
public class PersonalLoveFragment extends MyFragment<HomeActivity> {
    @BindView(R.id.recycle_personal_love)
    RecyclerView mPersonalLoveRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal_love;
    }

    @Override
    protected void initView() {
        mPersonalLoveRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    @Override
    protected void initData() {
        Bundle arguments = this.getArguments();
        String userId;
        if (arguments != null) {
            userId = arguments.getString("userId");
            String page = "1";
            getData(userId, page);
        }
    }

    private void getData(String userId, String page) {
        RetrofitClient.getRetrofitService().getPersonalLove(userId, page)
                .enqueue(new Callback<UserProductionOrLoveBean>() {
                    @Override
                    public void onResponse(Call<UserProductionOrLoveBean> call, Response<UserProductionOrLoveBean> response) {
                        UserProductionOrLoveBean productionOrLoveBean = response.body();
                        if (productionOrLoveBean.getCode().equals("200")) {
                            PersonLoveGridVideoAdapter fansAdapter = new PersonLoveGridVideoAdapter(response.body().getData());
                            mPersonalLoveRecyclerView.setAdapter(fansAdapter);
                        } else {
                            toast("获取数据失败");
                        }

                    }

                    @Override
                    public void onFailure(Call<UserProductionOrLoveBean> call, Throwable t) {
                        toast("网络错误");
                    }

                });
    }
}