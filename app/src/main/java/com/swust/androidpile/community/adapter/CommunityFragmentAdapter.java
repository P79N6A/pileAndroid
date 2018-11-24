package com.swust.androidpile.community.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.swust.androidpile.R;
import com.swust.androidpile.mine.activity.AdviceActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/14/014.
 */

public class CommunityFragmentAdapter extends RecyclerView.Adapter<CommunityFragmentAdapter.ViewHolder> {

    private List<Map<String, Object>> list;
    private View view;

    public CommunityFragmentAdapter(List<Map<String, Object>> list, MyItemClickListener myItemClickListener) {
        this.list = list;
        this.myItemClickListener = myItemClickListener;
    }

    private MyItemClickListener mItemClickListener;

    @Override
    public CommunityFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_community_adapter1, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CommunityFragmentAdapter.ViewHolder holder, final int position) {
        String title = (String) list.get(position).get("title");
        String content = (String) list.get(position).get("content");
        int imageId = (Integer) list.get(position).get("image");
        String msgTV = (String) list.get(position).get("msgTV");

        holder.title.setText(title);
        holder.content.setText(content);
        holder.image.setImageResource(imageId);
        holder.msgTV.setText(msgTV);

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
                return true;
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
        ImageView image;//图片
        TextView title;//摘要
        TextView content;//详细内容
        TextView msgTV;//消息获取条数

        public ViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.title = (TextView) view.findViewById(R.id.title);
            this.content = (TextView) view.findViewById(R.id.content);
            this.rl = (RelativeLayout) view.findViewById(R.id.relativeLayout);
            this.msgTV = (TextView) view.findViewById(R.id.msgTV);
        }
    }

    private MyItemClickListener myItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }
}
