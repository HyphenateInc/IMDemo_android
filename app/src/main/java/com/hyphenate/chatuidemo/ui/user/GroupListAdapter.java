package com.hyphenate.chatuidemo.ui.user;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.easeui.widget.EaseListItemClickListener;
import java.util.List;

/**
 * Created by benson on 2016/10/21.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private Context context;
    private List<EMGroup> groupList;
    private List<EMGroupInfo> publicGroupList;
    private EaseListItemClickListener listener;
    private boolean isPublic;

    GroupListAdapter(Context context, List<EMGroup> groups) {
        this.context = context;
        this.groupList = groups;
    }

    GroupListAdapter(Context context, List<EMGroupInfo> groups, boolean isPublic) {
        this.context = context;
        this.publicGroupList = groups;
        this.isPublic = isPublic;
    }

    public void setItemClickListener(EaseListItemClickListener listener) {
        this.listener = listener;
    }

    @Override public int getItemCount() {
        if (isPublic) {
            return publicGroupList.size();
        } else {
            return groupList.size();
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(context).inflate(R.layout.em_item_group_list, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, final int position) {

        if (isPublic) {
            holder.groupJoinView.setVisibility(View.VISIBLE);
            holder.guideArrowView.setVisibility(View.GONE);
            EMGroupInfo group = publicGroupList.get(position);
            holder.nameView.setText(group.getGroupName());
            for (EMGroupInfo info : publicGroupList) {
                if (EMClient.getInstance().groupManager().getGroup(info.getGroupId()) != null) {
                    holder.groupJoinView.setTextColor(Color.parseColor("#8798a4"));
                    holder.groupJoinView.setText("REQUESTED");
                    holder.groupJoinView.setClickable(false);
                    holder.groupJoinView.setEnabled(false);
                    holder.groupJoinView.setBackgroundResource(0);
                }
            }
        } else {
            EMGroup group = groupList.get(position);
            holder.nameView.setText(group.getGroupName());
            holder.memberSizeView.setText(group.getMembers().size() + "");
        }

        if (listener != null) {
            if (isPublic) {
                holder.groupJoinView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(v, position);
                    }
                });
            } else {
                holder.groupItem.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(v, position);
                    }
                });

                holder.groupItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override public boolean onLongClick(View v) {
                        listener.onLongItemClick(v, position);
                        return true;
                    }
                });
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_group_list) RelativeLayout groupItem;
        @BindView(R.id.img_group_avatar) ImageView avatarView;
        @BindView(R.id.text_group_name) TextView nameView;
        @BindView(R.id.text_group_member_size) TextView memberSizeView;
        @BindView(R.id.btn_group_join) Button groupJoinView;
        @BindView(R.id.img_guide_arrow) ImageView guideArrowView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}