package io.agora.chatdemo.user;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.agora.chatdemo.R;
import io.agora.chatdemo.group.InviteMembersActivity;
import io.agora.chatdemo.user.model.UserEntity;
import io.agora.easeui.utils.EaseUserUtils;
import io.agora.easeui.widget.EaseImageView;
import io.agora.easeui.widget.EaseListItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by benson on 2016/10/8.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private Context context;
    private List<UserEntity> userEntities;
    private EaseListItemClickListener listener;
    private boolean showCheckBox;
    private List<String> membersList;

    ContactListAdapter(Context context, List<UserEntity> list) {

        this.context = context;
        userEntities = list;
    }

    public ContactListAdapter(Context context, List<UserEntity> list, boolean showCheckBox) {

        this.context = context;
        userEntities = list;
        this.showCheckBox = showCheckBox;
    }

    public ContactListAdapter(Context context, List<UserEntity> list, boolean showCheckBox, List<String> membersList) {

        this.context = context;
        userEntities = list;
        this.showCheckBox = showCheckBox;
        if (showCheckBox) {
            this.membersList = membersList;
        }
    }

    public void setOnItemClickListener(EaseListItemClickListener listener) {
        this.listener = listener;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.em_item_contact_list, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position) {

        final UserEntity user = userEntities.get(position);
        EaseUserUtils.setUserAvatar(context, user.getUsername(), holder.avatarView);
        EaseUserUtils.setUserNick(user.getUsername(), holder.contactNameView);

        if (showCheckBox) {
            //set checkbox listener
            ((InviteMembersActivity) context).checkBoxListener(holder.checkBoxView, user);
            holder.checkBoxView.setVisibility(View.VISIBLE);

            if (membersList != null && membersList.contains(user.getUsername())) {
                holder.checkBoxView.setChecked(true);
                holder.checkBoxView.setEnabled(false);
                holder.checkBoxView.setClickable(false);
            } else {
                if (((InviteMembersActivity) context).selectedMembers.contains(user.getUsername())) {
                    holder.checkBoxView.setChecked(true);
                } else {
                    holder.checkBoxView.setChecked(false);
                    holder.checkBoxView.setEnabled(true);
                    holder.checkBoxView.setClickable(true);
                }
            }
        }

        if (position == 0 || user.getInitialLetter() != null && !user.getInitialLetter().equals(userEntities.get(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(user.getInitialLetter())) {
                holder.headerView.setVisibility(View.INVISIBLE);
                holder.baseLineView.setVisibility(View.INVISIBLE);
            } else {
                holder.headerView.setVisibility(View.VISIBLE);
                holder.baseLineView.setVisibility(View.VISIBLE);
                holder.headerView.setText(user.getInitialLetter());
            }
        } else {
            holder.headerView.setVisibility(View.INVISIBLE);
            holder.baseLineView.setVisibility(View.INVISIBLE);
        }

        if (listener != null) {
            holder.contactItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (showCheckBox) {
                        listener.onItemClick(holder.checkBoxView, position);
                    } else {
                        listener.onItemClick(v, position);
                    }
                }
            });

            holder.contactItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    listener.onItemLongClick(v, position);
                    return true;
                }
            });
        }
    }

    @Override public int getItemCount() {
        return userEntities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_contact_name) TextView contactNameView;
        @BindView(R.id.layout_contact_item) RelativeLayout contactItemLayout;
        @BindView(R.id.txt_header) TextView headerView;
        @BindView(R.id.txt_base_line) TextView baseLineView;
        @BindView(R.id.checkbox) CheckBox checkBoxView;
        @BindView(R.id.img_contact_avatar) EaseImageView avatarView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
