package com.trinhbk.lecturelivestream.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrinhBK on 9/21/2018.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RecyclerViewHolder> {

    private List<Video> data;
    private OnClickVideo onClickVideo;

    public HomeAdapter(List<Video> data, OnClickVideo onClickVideo) {
        this.data = new ArrayList<>();
        this.data = data;
        this.onClickVideo = onClickVideo;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_home_video, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.tvVideoName.setText(data.get(position).getVideoName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvVideoName;
        ImageView ivVideoEdit;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvVideoName = itemView.findViewById(R.id.tvItemHome);
            ivVideoEdit = itemView.findViewById(R.id.ivItemHome);
            itemView.setOnClickListener(this);
            ivVideoEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivItemHome:
                    onClickVideo.onItemEditVideo(getAdapterPosition());
                    break;
                default:
                    onClickVideo.onItemWatchVideo(getAdapterPosition());
                    break;
            }
        }
    }

    public interface OnClickVideo {
        void onItemWatchVideo(int position);

        void onItemEditVideo(int position);
    }
}