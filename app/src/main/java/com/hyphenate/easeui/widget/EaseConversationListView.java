package com.hyphenate.easeui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.adapter.EaseConversationListAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by wei on 2016/9/28.
 * Conversation list view, which extends RecyclerView
 */
public class EaseConversationListView extends RecyclerView {
    protected final int MSG_REFRESH_ADAPTER_DATA = 0;

    protected Context mContext;
    protected List<EMConversation> mConversationList = new ArrayList<EMConversation>();
    protected EaseConversationListAdapter mAdapter;

    public EaseConversationListView(Context context) {
        this(context, null);
    }

    public EaseConversationListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseConversationListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseConversationList);
//        primaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListPrimaryTextColor, getResources().getColor(R.color.list_itease_primary_color));
//        secondaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListSecondaryTextColor, getResources().getColor(R.color.list_itease_secondary_color));
//        timeColor = ta.getColor(R.styleable.EaseConversationList_cvsListTimeTextColor, getResources().getColor(R.color.list_itease_secondary_color));
//        primarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListPrimaryTextSize, 0);
//        secondarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListSecondaryTextSize, 0);
//        timeSize = ta.getDimension(R.styleable.EaseConversationList_cvsListTimeTextSize, 0);

//        ta.recycle();

    }

    /**
     * Init this view, which use a default sorted conversation list.
     * If you want to show list with your own sort, use {@link #init(List)}
     */
    public void init() {
        init(null);
    }

    /**
     * Init list view with the passed Comparator
     *
     * @param comparator
     */
    public void init(Comparator<EMConversation> comparator) {
        mConversationList = loadConversationList();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        setLayoutManager(layoutManager);

        if(comparator == null){
            comparator = new Comparator<EMConversation>() {
                @Override public int compare(EMConversation o1, EMConversation o2) {
                    return Long.valueOf(o2.getLastMessage().getMsgTime()).compareTo(o1.getLastMessage().getMsgTime());
                }
            };
        }

        EaseConversationListAdapter mAdapter = new EaseConversationListAdapter(getContext(), comparator);
        setAdapter(mAdapter);
    }

    /**
     * filter conversation list with passed string
     * @param str
     */
    public void filter(CharSequence str) {
        //mAdapter.getFilter().filter(str);
    }

    /**
     * Refresh list view
     */
    public void refresh() {
        if (!handler.hasMessages(MSG_REFRESH_ADAPTER_DATA)) {
            handler.sendEmptyMessage(MSG_REFRESH_ADAPTER_DATA);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_REFRESH_ADAPTER_DATA:
//                    if (adapter != null) {
//                        adapter.notifyDataSetChanged();
//                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * load conversation list
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<EMConversation> conversationList = new ArrayList<>(conversations.values());
        return conversationList;
    }



}