package com.swust.androidpile.mine.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swust.androidpile.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/14/014.
 */

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {

    private View view;
    private MyItemClickListener mItemClickListener;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    public SettingAdapter(List<Map<String, Object>> list, MyItemClickListener myItemClickListener) {
        this.list = list;
        this.myItemClickListener = myItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_mine_settingadapter, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int imageId = (int) list.get(position).get("imageId");
        String content = (String) list.get(position).get("content");

        holder.image.setImageResource(imageId);
        holder.content.setText(content);

        //点击颜色变化、监听点击事件
        holder.rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.rl.setBackgroundColor(R.color.darkgrey);
                        break;
                    case MotionEvent.ACTION_UP:
                        holder.rl.setBackgroundColor(Color.WHITE);
                        myItemClickListener.onItemClick(holder.rl, position);
                        break;
                }
                return true;//设置为true代表还可以被onclick等监听到
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl;
        ImageView image;//图标
        TextView content;//详细内容

        public ViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.content = (TextView) view.findViewById(R.id.content);
            this.rl = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        }
    }

    private MyItemClickListener myItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }
}

