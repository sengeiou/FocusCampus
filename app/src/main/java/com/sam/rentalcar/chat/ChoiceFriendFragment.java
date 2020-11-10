package com.sam.rentalcar.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.sam.rentalcar.R;
import com.sam.rentalcar.bean.FansBean;
import com.sam.rentalcar.http.net.RetrofitClient;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 底部弹出选择好友的dialog
 */
public class ChoiceFriendFragment extends BaseDialogFragment {

    private RecyclerView mRecyclerView;
    private TextView mTextButton;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_choice_friend, container, false);
        mRecyclerView = view.findViewById(R.id.friend_recycleview);
        mTextButton = view.findViewById(R.id.but_creat_group);
        initData();
        initListener();
        return view;
    }


    private void initData() {
        String userId = "368499958493609284";
        String page = "1";
        getData(userId, page);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initListener() {
        mTextButton.setOnClickListener(v -> clickCreateGroup());
    }

    private void getData(String userId, String page) {
        RetrofitClient.getRetrofitService().getFans(userId, page)
                .enqueue(new Callback<FansBean>() {
                    @Override
                    public void onResponse(Call<FansBean> call, Response<FansBean> response) {
                        if (response.code() == HttpURLConnection.HTTP_OK) {
                            ChoiceFriendAdapter choiceFriendAdapter = new ChoiceFriendAdapter(getContext(), response.body().getData());
                            mRecyclerView.setAdapter(choiceFriendAdapter);
                        }

                    }

                    @Override
                    public void onFailure(Call<FansBean> call, Throwable t) {

                    }
                });

    }


    private void clickCreateGroup() {
        //创建群组
        Toast.makeText(getActivity(), "开始创建群组", Toast.LENGTH_SHORT).show();
        // 进入选择群组页面
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] allMembers = new String[]{"hqyc15065157322"};
                EMGroupOptions option = new EMGroupOptions();
                option.maxUsers = 200;
                option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                try {
                    EMClient.getInstance().groupManager().createGroup("群组名称", "群描述", allMembers, "添加原因", option);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "创建群组成功", Toast.LENGTH_SHORT).show();
                            //Intent intent = new Intent(getActivity(),ChatGroupActivity.class);
                            //intent.putExtra(EaseConstant.EXTRA_USER_ID, "222");
                            //intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EMMessage.ChatType.GroupChat);
                            //startActivity(intent);

                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "创建失败" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();

    }
}
