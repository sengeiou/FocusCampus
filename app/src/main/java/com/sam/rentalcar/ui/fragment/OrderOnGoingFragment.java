package com.sam.rentalcar.ui.fragment;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.rentalcar.R;
import com.sam.rentalcar.common.MyFragment;
import com.sam.rentalcar.http.net.RetrofitClient;
import com.sam.rentalcar.http.response.OrderListResponseBean;
import com.sam.rentalcar.ui.activity.HomeActivity;
import com.sam.rentalcar.ui.adapter.OrderAdapter;
import com.sam.rentalcar.utils.SPUtils;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * desc:订单进行中
 */
public class OrderOnGoingFragment extends MyFragment<HomeActivity> {
    public static final String TAG = "OrderOnGoingFragment";

    public static final int status = 1;

    @BindView(R.id.order_going_recyclerview)
    RecyclerView mRecyclerViewGoing;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order_on_going;
    }

    @Override
    protected void initView() {
        mRecyclerViewGoing.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onRightClick(View v) {
        Log.d(TAG, "right_click");
    }

    @Override
    protected void initData() {
        String userId = SPUtils.getInstance(getActivity()).getString("UserId");
        RetrofitClient.getRetrofitService().getUserOrderListInfo(userId, status).enqueue(new Callback<OrderListResponseBean>() {
            @Override
            public void onResponse(Call<OrderListResponseBean> call, Response<OrderListResponseBean> response) {
                OrderListResponseBean orderListResponseBean = response.body();
                if (orderListResponseBean.getCode().equals("200")) {

                    if (orderListResponseBean.getData().size() > 0) {
                        OrderAdapter orderAdapter = new OrderAdapter(getContext(), orderListResponseBean.getData());
                        orderAdapter.setStatus(status);
                        mRecyclerViewGoing.setAdapter(orderAdapter);
                    }

                } else {
                    toast("获取数据失败");
                }

            }

            @Override
            public void onFailure(Call<OrderListResponseBean> call, Throwable t) {
                toast("获取数据失败");
            }
        });

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

}