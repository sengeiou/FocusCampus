package com.sam.globalRentalCar.ui.fragment;

import androidx.recyclerview.widget.RecyclerView;

import com.sam.globalRentalCar.R;
import com.sam.globalRentalCar.adapter.FansAdapter;
import com.sam.globalRentalCar.bean.FansBean;
import com.sam.globalRentalCar.common.MyFragment;
import com.sam.globalRentalCar.http.net.RetrofitClient;
import com.sam.globalRentalCar.ui.activity.HomeActivity;

import java.net.HttpURLConnection;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 关注好友的列表
 */
public class FocusFragment extends MyFragment<HomeActivity> {

    @BindView(R.id.recycle_focus)
    RecyclerView mRecyclerView;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_focus;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        String userId = "368499958493609284";
        String page = "1";
        getData(userId,page);
    }

    private void getData(String userId, String page) {
        RetrofitClient.getRetrofitService().getFocus(userId,page)
                .enqueue(new Callback<FansBean>() {
                    @Override
                    public void onResponse(Call<FansBean> call, Response<FansBean> response) {
                        if (response.code()== HttpURLConnection.HTTP_OK) {
                            FansAdapter fansAdapter = new FansAdapter(getContext(),response.body().getData());
                            mRecyclerView.setAdapter(fansAdapter);
                        }

                    }

                    @Override
                    public void onFailure(Call<FansBean> call, Throwable t) {

                    }
                });
    }
}