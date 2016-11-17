package com.hyphenate.chatuidemo.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.apply.ApplyActivity;
import com.hyphenate.chatuidemo.chat.ChatActivity;
import com.hyphenate.chatuidemo.group.GroupListActivity;
import com.hyphenate.chatuidemo.call.VideoCallActivity;
import com.hyphenate.chatuidemo.call.VoiceCallActivity;
import com.hyphenate.chatuidemo.user.model.UserEntity;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.widget.EaseListItemClickListener;
import com.hyphenate.exceptions.HyphenateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by benson on 2016/10/8.
 */

public class ContactListFragment extends Fragment {

    private static String TAG = ContactListFragment.class.getSimpleName();

    @BindView(R.id.text_unread_notifications_number) TextView unreadNumberView;
    @BindView(R.id.rv_contacts) RecyclerView recyclerView;
    ShowDialogFragment dialogFragment;

    LinearLayoutManager layoutManager;
    ContactListAdapter adapter;

    private List<UserEntity> entityList;

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRecyclerView();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.em_fragment_contact_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setRecyclerView() {
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        refresh();

        adapter.setOnItemClickListener(new EaseListItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                UserEntity user = entityList.get(position);
                itemClick(user);
            }

            @Override public void onItemLongClick(View view, int position) {
                UserEntity user = entityList.get(position);
                itemLongClick(user);
            }
        });
    }

    public void filter(String newText) {
        List<UserEntity> list = new ArrayList<>();
        if (entityList == null) {
            entityList = new ArrayList<>();
        }
        entityList.clear();
        entityList.addAll(DemoHelper.getInstance().getContactList().values());
        for (UserEntity userEntity : entityList) {
            if (userEntity.getNickname().contains(newText)) {
                list.add(userEntity);
            }
        }
        entityList.clear();
        entityList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * Item click event
     *
     * @param user current user item
     */
    private void itemClick(final UserEntity user) {

        dialogFragment = new ShowDialogFragment();
        dialogFragment.show(getFragmentManager(), "dialog");

        dialogFragment.setOnShowDialogClickListener(new ShowDialogFragment.OnShowDialogClickListener() {
            @Override public String getUserId() {
                return user.getUsername();
            }

            @Override public void onVoiceCallClick() {
                startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername())
                        .putExtra(EaseConstant.EXTRA_IS_INCOMING_CALL, false));
                dialogFragment.dismiss();
            }

            @Override public void onSendMessageClick() {
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
                dialogFragment.dismiss();
            }

            @Override public void onVideoCallClick() {
                startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername())
                        .putExtra(EaseConstant.EXTRA_IS_INCOMING_CALL, false));
                dialogFragment.dismiss();
            }
        });
    }

    /**
     * item long click event
     *
     * @param userEntity current click item
     */
    private void itemLongClick(final UserEntity userEntity) {

        String[] menus = { "Delete Contact", "Add Blacklist" };

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setItems(menus, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        deleteContacts(userEntity);
                        break;
                    case 1:
                        addBlackUser(userEntity);
                        break;
                }
            }
        });
        AlertDialog contactsMenuDialog = alertDialogBuilder.create();
        contactsMenuDialog.show();
    }

    /**
     * delete contacts
     */
    private void deleteContacts(final UserEntity userEntity) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(userEntity.getUsername());
                    DemoHelper.getInstance().deleteContacts(userEntity);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override public void run() {
                            refresh();
                            Toast.makeText(getActivity(), "contacts is deleted", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Add user to Blacklist
     */
    private void addBlackUser(final UserEntity userEntity) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    EMClient.getInstance().contactManager().addUserToBlackList(userEntity.getUsername(), true);
                    DemoHelper.getInstance().deleteContacts(userEntity);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override public void run() {
                            Toast.makeText(getActivity(), "Contacts is add blacklist", Toast.LENGTH_LONG).show();
                            refresh();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void refresh() {

        loadContacts();

        if (unreadNumberView != null) {
            EMConversation conversation = EMClient.getInstance()
                    .chatManager()
                    .getConversation(Constant.CONVERSATION_NAME_APPLY,
                            EMConversation.EMConversationType.Chat, true);
            int count = conversation.getUnreadMsgCount();
            if (count != 0) {
                unreadNumberView.setText(String.valueOf(count));
                unreadNumberView.setVisibility(View.VISIBLE);
            } else {
                unreadNumberView.setVisibility(View.GONE);
            }
        }

        if (adapter == null) {
            adapter = new ContactListAdapter(getActivity(), entityList);
            if (recyclerView != null) {
                recyclerView.setAdapter(adapter);
            }
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Load contacts
     */
    private void loadContacts() {
        if (entityList == null) {
            entityList = new ArrayList<>();
        }
        entityList.clear();
        entityList.addAll(DemoHelper.getInstance().getContactList().values());
        // sort
        Collections.sort(entityList, new Comparator<UserEntity>() {
            @Override public int compare(UserEntity o1, UserEntity o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        // refresh ui
        refresh();
    }

    @Override public void onStop() {
        super.onStop();
    }

    @OnClick({ R.id.layout_group_entry, R.id.layout_apply_entry }) void onclick(View v) {
        switch (v.getId()) {
            case R.id.layout_group_entry:
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case R.id.layout_apply_entry:
                startActivity(new Intent(getActivity(), ApplyActivity.class));
                break;
        }
    }
}