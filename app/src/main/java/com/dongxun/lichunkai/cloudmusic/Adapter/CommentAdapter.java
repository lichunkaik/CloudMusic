package com.dongxun.lichunkai.cloudmusic.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dongxun.lichunkai.cloudmusic.Bean.Comment;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.millisecondToDate;

/**
 * 搜索歌曲RecyclerView适配器
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> mList;
    private OnItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView_head;//头像
        TextView textView_nickname;//昵称
        TextView textView_time;//时间
        TextView textView_likeCount;//点赞数
        TextView textView_contentText;//评论内容
        TextView textView_returnCount;//回复数
        LinearLayout LinearLayout_like;//点赞
        LinearLayout LinearLayout_return;//回复


        public ViewHolder( View itemView) {
            super(itemView);
            imageView_head = (ImageView)itemView.findViewById(R.id.imageView_head);
            textView_nickname = (TextView)itemView.findViewById(R.id.textView_nickname);
            textView_time = (TextView)itemView.findViewById(R.id.textView_time);
            textView_likeCount = (TextView)itemView.findViewById(R.id.textView_likeCount);
            textView_contentText = (TextView)itemView.findViewById(R.id.textView_contentText);
            textView_returnCount = (TextView)itemView.findViewById(R.id.textView_returnCount);
            LinearLayout_like = (LinearLayout)itemView.findViewById(R.id.LinearLayout_like);
            LinearLayout_return = (LinearLayout)itemView.findViewById(R.id.LinearLayout_return);
        }
    }

    public CommentAdapter(ArrayList<Comment> searchHistoryList) {
        mList = searchHistoryList;
    }

    //第一步 定义接口（点击播放/菜单）
    public interface OnItemClickListener {
        void onClick(int position);
        void onClickLike(int position);
        void onClickReturn(int position);
    }

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Comment comment = mList.get(position);
        holder.imageView_head.setImageResource(R.drawable.img_head);
        holder.textView_nickname.setText(comment.getUser().getNickname());
        holder.textView_likeCount.setText(comment.getLikedCount());
        holder.textView_contentText.setText(comment.getContent());
        holder.textView_time.setText(millisecondToDate(Long.valueOf(comment.getTime())));
        holder.textView_returnCount.setText("250");

        //点击评论内容，弹出操作
        holder.textView_contentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });
        //点赞
        holder.LinearLayout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickLike(position);
                }
            }
        });
        //查看回复
        holder.LinearLayout_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickReturn(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}