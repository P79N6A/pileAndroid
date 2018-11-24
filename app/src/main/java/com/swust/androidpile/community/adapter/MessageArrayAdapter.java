package com.swust.androidpile.community.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.swust.androidpile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/14/014.
 */

public class MessageArrayAdapter extends RecyclerView.Adapter<MessageArrayAdapter.ViewHolder> {

    private View view;
    private Context context;
    private MyItemClickListener mItemClickListener;
    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private JsonArray json;

    public MessageArrayAdapter(JsonArray json, MyItemClickListener myItemClickListener, Context context) {
        this.myItemClickListener = myItemClickListener;
        this.json = json;
        this.context = context;
        for (int i = 0; i < json.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            JsonObject jsonObject = json.get(i).getAsJsonObject();
            map.put("title", jsonObject.get("title").getAsString());
            map.put("content", jsonObject.get("content").getAsString());
            list.add(map);
        }
    }

    @Override
    public MessageArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_community_msgadapter, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageArrayAdapter.ViewHolder holder, final int position) {
        String title = list.get(position).get("title");
        final String content = list.get(position).get("content");

        holder.title.setText(title);
        holder.content.setText(content);

        //点击颜色变化、监听点击事件
        holder.rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.rl.setBackgroundColor(context.getResources().getColor(R.color.darkgrey));
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
        return json.size();
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
        LinearLayout rl;
        TextView title;//摘要
        TextView content;//详细内容

        public ViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.title);
            this.content = (TextView) view.findViewById(R.id.content);
            this.rl = (LinearLayout) view.findViewById(R.id.relativeLayout);
        }
    }

    private MyItemClickListener myItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }
}
