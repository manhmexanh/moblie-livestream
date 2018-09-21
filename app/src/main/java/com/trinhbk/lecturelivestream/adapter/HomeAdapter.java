package com.trinhbk.lecturelivestream.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrinhBK on 9/21/2018.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RecyclerViewHolder> {

    private List<Video> data = new ArrayList<>();

    public HomeAdapter(List<Video> data) {
        this.data = data;
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


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView tvVideoName;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvVideoName = itemView.findViewById(R.id.tvItemHome);
        }
    }
}