package adapter;

import android.content.Context;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.databinding.ConversationListRowBinding;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import utils.FontUtils;
import utils.Utils;

/**
 * Purpose - ConversationListAdapter is a subclass of RecyclerView Adapter which is used to display
 * the list of conversations. It helps to organize the list data in recyclerView.
 * It also help to perform search operation on list of conversation.
 * <p>
 * Created on - 20th December 2019
 * <p>
 * Modified on  - 23rd March 2020
 */

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder> implements Filterable {
    private static final String TAG = "ConversationListAdapter";
    private Context context;
    private String avatar;
    private String cometid1 = "";
    ConversationViewHolder conversationViewHolderllll;
    /**
     * ConversationListAdapter maintains two arrayList i.e conversationList and filterConversationList.
     * conversationList is a original list and it will not get modified while filterConversationList
     * will get modified as per search filter. In case if search field is empty then to retrieve
     * original list we set filerConversationList = conversationList.
     * Here filterConversationList will be main list for this adapter.
     */
    private List<Conversation> conversationList = new ArrayList<>();

    private List<Conversation> filterConversationList = new ArrayList<>();

    private FontUtils fontUtils;

    /**
     * It is constructor which takes conversationList as parameter and bind it with conversationList
     * and filterConversationList in adapter.
     *
     * @param context          is a object of Context.
     * @param conversationList is list of conversations used in this adapter.
     */
    public ConversationListAdapter(Context context, List<Conversation> conversationList) {
        this.conversationList = conversationList;
        this.filterConversationList = conversationList;
        this.context = context;
        fontUtils = FontUtils.getInstance(context);
    }

    /**
     * It is a constructor which is used to initialize wherever we needed.
     *
     * @param context
     */
    public ConversationListAdapter(Context context) {
        this.context = context;
        fontUtils = FontUtils.getInstance(context);


    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ConversationListRowBinding conversationListRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.conversation_list_row, parent, false);

        return new ConversationViewHolder(conversationListRowBinding);
    }

    /**
     * This method is used to bind the ConversationViewHolder contents with conversation at given
     * position. It set avatar, name, lastMessage, unreadMessageCount and messageTime of conversation
     * in a respective ConversationViewHolder content. It checks whether conversation type is user
     * or group and set name and avatar as accordingly. It also checks whether last message is text, media
     * or file and modify txtUserMessage view accordingly.
     *
     * @param conversationViewHolder is a object of ConversationViewHolder.
     * @param position               is a position of item in recyclerView.
     * @see Conversation
     */
    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder conversationViewHolder, int position) {
        Conversation conversation = filterConversationList.get(position);
        conversationViewHolderllll = conversationViewHolder;
        //String avatar;
        String name;
        String lastMessageText = null;
        BaseMessage baseMessage = conversation.getLastMessage();
        conversationViewHolder.conversationListRowBinding.setConversation(conversation);
        conversationViewHolder.conversationListRowBinding.executePendingBindings();

        String type = null;
        String category = null;
        if (baseMessage != null) {
            Log.e(TAG, "onBindViewHolder: " + baseMessage.getConversationId());
            type = baseMessage.getType();
            category = baseMessage.getCategory();
            // setStatusIcon(conversationViewHolder.conversationListRowBinding.messageTime, baseMessage);
            setStatusIcon(conversationViewHolder.conversationListRowBinding.messageTime, conversationViewHolder.conversationListRowBinding.txtUserMessage, baseMessage);
            conversationViewHolder.conversationListRowBinding.messageTime.setVisibility(View.VISIBLE);
            conversationViewHolder.conversationListRowBinding.messageTime.setText(Utils.getLastMessageDate(baseMessage.getSentAt()));
            lastMessageText = Utils.getLastMessage(baseMessage);
        } else {
            lastMessageText = context.getResources().getString(R.string.tap_to_start_conversation);
            conversationViewHolder.conversationListRowBinding.txtUserMessage.setMarqueeRepeatLimit(100);
            conversationViewHolder.conversationListRowBinding.txtUserMessage.setHorizontallyScrolling(true);
            conversationViewHolder.conversationListRowBinding.txtUserMessage.setSingleLine(true);
            conversationViewHolder.conversationListRowBinding.messageTime.setVisibility(View.GONE);
        }

        if (lastMessageText != null && lastMessageText.equals("You sent a file")) {
            conversationViewHolder.conversationListRowBinding.messageCount.setCount(conversation.getUnreadMessageCount());
            conversationViewHolder.conversationListRowBinding.txtUserMessage.setText("Has enviado un archivo");
        } else if (lastMessageText != null && lastMessageText.equals("You received a file")) {
            conversationViewHolder.conversationListRowBinding.messageCount.setCount(conversation.getUnreadMessageCount());
            conversationViewHolder.conversationListRowBinding.txtUserMessage.setText("Has recibida un archivo");
        } else if (lastMessageText != null && lastMessageText.equals("You received a image")) {
            conversationViewHolder.conversationListRowBinding.messageCount.setCount(conversation.getUnreadMessageCount());
            conversationViewHolder.conversationListRowBinding.txtUserMessage.setText("Recibiste una imagen");
        } else if (lastMessageText != null && lastMessageText.equals("You sent a image")) {
            conversationViewHolder.conversationListRowBinding.messageCount.setCount(conversation.getUnreadMessageCount());
            conversationViewHolder.conversationListRowBinding.txtUserMessage.setText("Has enviado un imagen");
        } else if (lastMessageText != null && lastMessageText.equals(" ")) {
            conversationViewHolder.conversationListRowBinding.messageCount.setCount(0);
            conversationViewHolder.conversationListRowBinding.messageTime.setText("");
            conversationViewHolder.conversationListRowBinding.txtUserMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            conversationViewHolder.conversationListRowBinding.messageCount.setCount(conversation.getUnreadMessageCount());
            conversationViewHolder.conversationListRowBinding.txtUserMessage.setText(lastMessageText);
            /*if (lastMessageText != null) {
                conversationViewHolder.conversationListRowBinding.messageCount.setCount(conversation.getUnreadMessageCount());
                conversationViewHolder.conversationListRowBinding.txtUserMessage.setText(lastMessageText);
            }else {
                conversationViewHolder.conversationListRowBinding.messageCount.setCount(0);

            }*/
        }

      /*  if (lastMessageText != null && lastMessageText.equals(" ")) {
            conversationViewHolder.conversationListRowBinding.messageCount.setCount(0);
        } else {
            conversationViewHolder.conversationListRowBinding.messageCount.setCount(conversation.getUnreadMessageCount());
        }*/

        conversationViewHolder.conversationListRowBinding.txtUserMessage.setTypeface(fontUtils.getTypeFace(FontUtils.robotoRegular));
        conversationViewHolder.conversationListRowBinding.txtUserName.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        conversationViewHolder.conversationListRowBinding.messageTime.setTypeface(fontUtils.getTypeFace(FontUtils.robotoRegular));

        if (conversation.getConversationType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            name = ((User) conversation.getConversationWith()).getName();
            conversationViewHolder.conversationListRowBinding.txtUserName.setText(name);
            avatar = ((User) conversation.getConversationWith()).getAvatar();
        } else {
            name = ((Group) conversation.getConversationWith()).getName();
            //avatar = ((Group) conversation.getConversationWith()).getIcon();
            String[] separated = name.split("_");
            if (separated.length > 1) {
                name = separated[1];
            }

            // String sss = ((Group) conversation.getConversationWith()).getDescription();
            // String[] separater = sss.split("\\*");
            // avatar = separater[2];
            // conversationViewHolder.conversationListRowBinding.txtUserName.setText(Utils.capitalize(separater[0]) + " - " + servicename);
          /*  if (avatar != null && !avatar.isEmpty() && !avatar.equals("http://34.223.228.22/52a363e725235501a0fc865f1f628006.jpg")) {
                conversationViewHolder.conversationListRowBinding.avUser.setAvatar(avatar);
            } else {
                conversationViewHolder.conversationListRowBinding.avUser.setInitials(separater[0]);

            }*/
            getGroupInfo(((Group) conversation.getConversationWith()).getGuid(), conversationViewHolder, name, conversation);

        }

       /* conversationViewHolder.conversationListRowBinding.messageCount.setCount(conversation.getUnreadMessageCount());
        conversationViewHolder.conversationListRowBinding.avUser.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        conversationViewHolder.conversationListRowBinding.getRoot().setTag(R.string.conversation, conversation);*/

    }

    private void getGroupInfo(String GUID, ConversationViewHolder conversationViewHolder, String sername, Conversation conversation) {
        GroupMembersRequest groupMembersRequest = null;
        // String GUID = "GUID";
        int limit = 3;

        groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(GUID).setLimit(limit).build();

        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                String uuu = CometChat.getLoggedInUser().getUid();
                Log.e(TAG, "onSuccess getLoggedInUser : " + uuu);
                for (int i = 0; i < list.size(); i++) {
                    if (!uuu.equals(list.get(i).getUid()) && !"camaround_admin_1".equals(list.get(i).getUid())) {
                        avatar = list.get(i).getAvatar();
                        conversationViewHolder.conversationListRowBinding.txtUserName.setText(Utils.capitalize(list.get(i).getName()) + " - " + sername);
                        if (avatar != null && !avatar.isEmpty() && !avatar.equals("http://34.223.228.22/52a363e725235501a0fc865f1f628006.jpg")) {
                            conversationViewHolder.conversationListRowBinding.avUser.setAvatar(avatar);
                        } else {
                            conversationViewHolder.conversationListRowBinding.avUser.setInitials(list.get(i).getName());

                        }
                        conversationViewHolder.conversationListRowBinding.avUser.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                        conversationViewHolder.conversationListRowBinding.getRoot().setTag(R.string.conversation, conversation);
                    }
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "Group Member list fetching failed with exception: " + e.getMessage());
            }

        });
    }

    private void setStatusIcon(TextView txtTime, TextView txtRecipt, BaseMessage baseMessage) {
        if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP) &&
                baseMessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())) {
            if (baseMessage.getReadAt() != 0) {
                txtTime.setText(Utils.getLastMessageDate(baseMessage.getSentAt()));
                txtRecipt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_double_tick, 0, 0, 0);
                txtRecipt.setCompoundDrawablePadding(3);
            } else if (baseMessage.getDeliveredAt() != 0) {
                txtTime.setText(Utils.getHeaderDate(baseMessage.getSentAt() * 1000));
                txtRecipt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_all_black_24dp, 0, 0, 0);
                txtRecipt.setCompoundDrawablePadding(3);
            } else {
                txtTime.setText(Utils.getHeaderDate(baseMessage.getSentAt() * 1000));
                txtRecipt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_all_black_24dp, 0, 0, 0);
                txtRecipt.setCompoundDrawablePadding(3);
            }
        } else {
            txtTime.setText(Utils.getHeaderDate(baseMessage.getSentAt()));
            txtRecipt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return filterConversationList.size();
    }

    /**
     * This method is used to update the filterConversationList with new conversations and avoid
     * duplicates conversations.
     *
     * @param conversations is a list of conversation which will be updated in adapter.
     */
    public void updateList(List<Conversation> conversations) {

        for (int i = 0; i < conversations.size(); i++) {
            if (filterConversationList.contains(conversations.get(i))) {
                int index = filterConversationList.indexOf(conversations.get(i));
                filterConversationList.remove(conversations.get(i));
                filterConversationList.add(index, conversations.get(i));
            } else {
                filterConversationList.add(conversations.get(i));
            }
        }
        notifyDataSetChanged();
    }

    public void setReadReceipts(MessageReceipt readReceipts) {
        for (int i = 0; i < filterConversationList.size() - 1; i++) {
            Conversation conversation = filterConversationList.get(i);
            if (conversation.getConversationType().equals(CometChatConstants.RECEIVER_TYPE_USER) &&
                    readReceipts.getSender().getUid().equals(((User) conversation.getConversationWith()).getUid())) {
                BaseMessage baseMessage = filterConversationList.get(i).getLastMessage();
                if (baseMessage != null && baseMessage.getReadAt() == 0) {
                    baseMessage.setReadAt(readReceipts.getReadAt());
                    int index = filterConversationList.indexOf(filterConversationList.get(i));
                    filterConversationList.remove(index);
                    conversation.setLastMessage(baseMessage);
                    filterConversationList.add(index, conversation);

                }
            }
        }
        notifyDataSetChanged();
    }

    public void setDeliveredReceipts(MessageReceipt deliveryReceipts) {
        Log.e(TAG, "setDeliveredReceipts: ");
        for (int i = 0; i < filterConversationList.size() - 1; i++) {
            Conversation conversation = filterConversationList.get(i);
            if (conversation.getConversationType().equals(CometChatConstants.RECEIVER_TYPE_USER) &&
                    deliveryReceipts.getSender().getUid().equals(((User) conversation.getConversationWith()).getUid())) {

                BaseMessage baseMessage = filterConversationList.get(i).getLastMessage();
                if (baseMessage != null && baseMessage.getDeliveredAt() == 0) {
                    baseMessage.setReadAt(deliveryReceipts.getDeliveredAt());
                    int index = filterConversationList.indexOf(filterConversationList.get(i));

                    filterConversationList.remove(index);
                    conversation.setLastMessage(baseMessage);
                    filterConversationList.add(index, conversation);

                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * This method is used to remove the conversation from filterConversationList
     *
     * @param conversation is a object of conversation.
     * @see Conversation
     */
    public void remove(Conversation conversation) {
        int position = filterConversationList.indexOf(conversation);
        filterConversationList.remove(conversation);
        notifyItemRemoved(position);
    }


    /**
     * This method is used to update conversation in filterConversationList.
     *
     * @param conversation is an object of Conversation. It is used to update the previous conversation
     *                     in list
     * @see Conversation
     */
    public void update(Conversation conversation) {

        if (filterConversationList.contains(conversation)) {
            Conversation oldConversation = filterConversationList.get(filterConversationList.indexOf(conversation));
            filterConversationList.remove(oldConversation);
            conversation.setUnreadMessageCount(oldConversation.getUnreadMessageCount() + 1);
            filterConversationList.add(0, conversation);
        } else {
            filterConversationList.add(0, conversation);
        }
        notifyDataSetChanged();

    }

    /**
     * This method is used to add conversation in list.
     *
     * @param conversation is an object of Conversation. It will be added to filterConversationList.
     * @see Conversation
     */
    public void add(Conversation conversation) {
        if (filterConversationList != null)
            filterConversationList.add(conversation);
    }

    /**
     * This method is used to reset the adapter by clearing filterConversationList.
     */
    public void resetAdapterList() {
        filterConversationList.clear();
        notifyDataSetChanged();
    }

    /**
     * It is used to perform search operation in filterConversationList. It will check
     * whether searchKeyword is similar to username or group name and modify filterConversationList
     * accordingly. In case if searchKeyword is empty it will set filterConversationList = conversationList
     *
     * @return
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchKeyword = charSequence.toString();

                if (searchKeyword.isEmpty()) {
                    filterConversationList = conversationList;
                } else {
                    List<Conversation> tempFilter = new ArrayList<>();
                    for (Conversation conversation : filterConversationList) {
                        if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER) &&
                                ((User) conversation.getConversationWith()).getName().toLowerCase().contains(searchKeyword)) {
                            tempFilter.add(conversation);
                        } else if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_GROUP) &&
                                ((Group) conversation.getConversationWith()).getName().toLowerCase().contains(searchKeyword)) {
                            // String gid = ((Group)conversation.getConversationWith()).getGuid();
                            tempFilter.add(conversation);
                        } else if (conversation.getLastMessage() != null && conversation.getLastMessage().getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)
                                && ((TextMessage) conversation.getLastMessage()).getText().contains(searchKeyword)) {

                            tempFilter.add(conversation);
                        }
                    }

                    filterConversationList = tempFilter;
                  /*  int startPos = tempFilter.indexOf(searchKeyword);
                    int endPos = startPos + searchKeyword.length();
                    Spannable spanText = Spannable.Factory.getInstance().newSpannable(conversationViewHolderllll.conversationListRowBinding.txtUserMessage.getText()); // <- EDITED: Use the original string, as `country` has been converted to lowercase.
                    spanText.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    conversationViewHolderllll.conversationListRowBinding.txtUserMessage.setText(spanText, TextView.BufferType.SPANNABLE);*/

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterConversationList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterConversationList = (List<Conversation>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static CharSequence highlight(String search, String originalText) {
        Log.e(TAG, "highlight: " + search + "  " + originalText);
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        int start = normalizedText.indexOf(search);
        if (start <= 0) {
            return originalText;
        } else {
            Spannable highlighted = new SpannableString(originalText);
            while (start > 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());
                highlighted.setSpan(new BackgroundColorSpan(Color.YELLOW), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = normalizedText.indexOf(search, spanEnd);
            }
            return highlighted;
        }
    }


    class ConversationViewHolder extends RecyclerView.ViewHolder {

        ConversationListRowBinding conversationListRowBinding;

        ConversationViewHolder(ConversationListRowBinding conversationListRowBinding) {
            super(conversationListRowBinding.getRoot());
            this.conversationListRowBinding = conversationListRowBinding;
        }

    }
}
