package com.sam.rental.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.sam.rental.R;
import com.sam.rental.bean.VideoBean;
import com.sam.rental.ui.adapter.BaseRvAdapter;
import com.sam.rental.ui.adapter.BaseRvViewHolder;
import com.sam.rental.widget.ControllerView;
import com.sam.rental.widget.LikeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by libo
 * create on 2020-05-20
 * description
 */
public class VideoAdapter extends BaseRvAdapter<VideoBean, VideoAdapter.VideoViewHolder> {

    public VideoAdapter(Context context, List<VideoBean> datas) {
        super(context, datas);
    }

    @Override
    protected void onBindData(VideoViewHolder holder, VideoBean videoBean, int position) {
        holder.controllerView.setVideoData(videoBean);

        holder.ivCover.setImageResource(videoBean.getCoverRes());

        holder.likeView.setOnLikeListener(() -> {
            if (!videoBean.isLiked()) {  //未点赞，会有点赞效果，否则无
                holder.controllerView.like();
            }

        });
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    public class VideoViewHolder extends BaseRvViewHolder {
        @BindView(R.id.likeview)
        LikeView likeView;
        @BindView(R.id.controller)
        ControllerView controllerView;
        @BindView(R.id.iv_cover)
        ImageView ivCover;

        public VideoViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
