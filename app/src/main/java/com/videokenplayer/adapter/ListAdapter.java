package com.videokenplayer.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.videokenplayer.R;
import com.videokenplayer.database.VideoModel;
import com.videokenplayer.listerners.RecyclersOnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranjith on 31/5/17.

 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CustomViewHolder> {


    private List<VideoModel> mVideoList = new ArrayList<VideoModel>();

    private final Activity mActivity;
    RecyclersOnItemClickListener mRecyclersOnItemClickListener;

    public ListAdapter(Activity mActivity, RecyclersOnItemClickListener listener, List<VideoModel> mVideoList) {
        this.mActivity = mActivity;
        this.mVideoList = mVideoList;
        this.mRecyclersOnItemClickListener = listener;

    }

    @Override
    public ListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.list_item, parent, false);
        return new CustomViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ListAdapter.CustomViewHolder holder, int position) {
        holder.name.setText(mVideoList.get(position).get_speachtext());


    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView name;
        CardView cardView;
        LinearLayout cardLayout;
        public CustomViewHolder(View view) {
            super(view);

            cardLayout = (LinearLayout)view.findViewById(R.id.cardLayout);
            name = (TextView) view.findViewById(R.id.tvName);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            mRecyclersOnItemClickListener.onItemClick(getPosition(), v);

        }

    }
}