package com.pcamarounds.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pcamarounds.R;
import com.pcamarounds.activities.MessagesDetailActivity;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.FragmentMessageBinding;
import com.pcamarounds.utils.Utility;

import java.util.List;

import constant.StringContract;
import listeners.OnItemClickListener;

import static com.pcamarounds.utils.Utility.setupParent;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    private static final String TAG = "MessageFragment";
    private Context context;
    private FragmentMessageBinding binding;
    //private MessagesAdapter messagesAdapter;
    //private List<MessagesModel> messagesModelList = new ArrayList<>();
    private ConversationsRequest conversationsRequest;
    private static OnItemClickListener events;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        context = getActivity();
        setupParent(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getActivity());
        initView();
        return binding.getRoot();
    }

    private void initView() {
        // setupRvMessage();
        binding.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rlSearch.setVisibility(View.GONE);
                binding.llSearchBar.setVisibility(View.VISIBLE);
            }
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    conversationsRequest = null;
                    binding.rvConversationList.clearList();
                    makeConversationList();
                } else {
                    binding.rvConversationList.searchConversation(editable.toString());
                }
            }
        });
        binding.rvConversationList.setItemClickListener(new OnItemClickListener<Conversation>() {
            @Override
            public void OnItemClick(Conversation conversation, int position) {
                if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_GROUP))
                    startGroupIntent(((Group) conversation.getConversationWith()));
                else
                    startUserIntent(((User) conversation.getConversationWith()));
            }
        });

        binding.etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                binding.rvConversationList.searchConversation(textView.getText().toString());
                return true;
            }
            return false;
        });


        // Uses to fetch next list of conversations if rvConversationList (RecyclerView) is scrolled in upward direction.
        binding.rvConversationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)) {
                    makeConversationList();
                }

            }
        });
    }

    private void makeConversationList() {

        if (conversationsRequest == null) {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(50).build();
        }
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations.size() != 0) {
                    stopHideShimmer();
                    binding.llNoLising.setVisibility(View.GONE);
                  /*  for (int i = 0;i<conversations.size();i++)
                    {
                        if (conversations.get(i).getLastMessage() != null && !conversations.get(i).getLastMessage().equals(""))
                        {
                            binding.rvConversationList.setConversationList(conversations);
                        }
                    }*/

                    binding.rvConversationList.setConversationList(conversations);
                } else {
                    checkNoConverstaion();
                }
            }

            @Override
            public void onError(CometChatException e) {
                stopHideShimmer();
                if (getActivity() != null)
                    //Toast.makeText(getActivity(),"Unable to load conversations",Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void startUserIntent(User user) {
        Intent intent = new Intent(context, MessagesDetailActivity.class);
        intent.putExtra(StringContract.IntentStrings.UID, user.getUid());
        intent.putExtra(StringContract.IntentStrings.AVATAR, user.getAvatar());
        intent.putExtra(StringContract.IntentStrings.STATUS, user.getStatus());
        intent.putExtra(StringContract.IntentStrings.NAME, user.getName());
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
        startActivity(intent);
    }

    private void startGroupIntent(Group group) {
        Intent intent = new Intent(context, MessagesDetailActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, group.getGuid());
        intent.putExtra(StringContract.IntentStrings.AVATAR, group.getIcon());
        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, group.getOwner());
        intent.putExtra(StringContract.IntentStrings.NAME, group.getName());
        intent.putExtra(StringContract.IntentStrings.DESCRIPTION, group.getDescription());
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
        startActivity(intent);
    }

    private void checkNoConverstaion() {
        if (binding.rvConversationList.size() == 0) {
            stopHideShimmer();
            binding.llNoLising.setVisibility(View.VISIBLE);
            binding.rvConversationList.setVisibility(View.GONE);
        } else {
            binding.llNoLising.setVisibility(View.GONE);
            binding.rvConversationList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param onItemClickListener An object of <code>OnItemClickListener&lt;T&gt;</code> abstract class helps to initialize with events
     *                            to perform onItemClick & onItemLongClick.
     * @see OnItemClickListener
     */
    public static void setItemClickListener(OnItemClickListener<Conversation> onItemClickListener) {
        events = onItemClickListener;
    }

    /**
     * This method has message listener which recieve real time message and based on these messages, conversations are updated.
     *
     * @see CometChat#addMessageListener(String, CometChat.MessageListener)
     */
    private void addConversationListener() {
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                if (binding.rvConversationList != null) {
                    binding.rvConversationList.refreshConversation(message);
                    checkNoConverstaion();
                }
            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                if (binding.rvConversationList != null) {
                    binding.rvConversationList.refreshConversation(message);
                    checkNoConverstaion();
                }
            }

            @Override
            public void onCustomMessageReceived(CustomMessage message) {
                if (binding.rvConversationList != null) {
                    binding.rvConversationList.refreshConversation(message);
                    checkNoConverstaion();
                }
            }

            @Override
            public void onMessagesDelivered(MessageReceipt messageReceipt) {
                if (binding.rvConversationList != null)
                    binding.rvConversationList.setReciept(messageReceipt);
            }

            @Override
            public void onMessagesRead(MessageReceipt messageReceipt) {
                if (binding.rvConversationList != null)
                    binding.rvConversationList.setReciept(messageReceipt);
            }

            @Override
            public void onMessageEdited(BaseMessage message) {
                if (binding.rvConversationList != null)
                    binding.rvConversationList.refreshConversation(message);
            }

            @Override
            public void onMessageDeleted(BaseMessage message) {
                if (binding.rvConversationList != null)
                    binding.rvConversationList.refreshConversation(message);
            }
        });
        CometChat.addGroupListener(TAG, new CometChat.GroupListener() {
            @Override
            public void onGroupMemberKicked(Action action, User kickedUser, User kickedBy, Group kickedFrom) {
                Log.e(TAG, "onGroupMemberKicked: " + kickedUser);
                if (kickedUser.getUid().equals(CometChat.getLoggedInUser().getUid())) {
                    if (binding.rvConversationList != null)
                        updateConversation(action, true);
                } else {
                    updateConversation(action, false);
                }
            }

            @Override
            public void onMemberAddedToGroup(Action action, User addedby, User userAdded, Group addedTo) {
                Log.e(TAG, "onMemberAddedToGroup: ");
                updateConversation(action, false);
            }

            @Override
            public void onGroupMemberJoined(Action action, User joinedUser, Group joinedGroup) {
                Log.e(TAG, "onGroupMemberJoined: ");
                updateConversation(action, false);
            }

            @Override
            public void onGroupMemberLeft(Action action, User leftUser, Group leftGroup) {
                Log.e(TAG, "onGroupMemberLeft: ");
                if (leftUser.getUid().equals(CometChat.getLoggedInUser().getUid())) {
                    updateConversation(action, true);
                } else {
                    updateConversation(action, false);
                }
            }
        });
    }

    /**
     * This method is used to update conversation received in real-time.
     *
     * @param baseMessage is object of BaseMessage.class used to get respective Conversation.
     * @param isRemove    is boolean used to check whether conversation needs to be removed or not.
     * @see CometChatHelper#getConversationFromMessage(BaseMessage) This method return the conversation
     * of receiver using baseMessage.
     */
    private void updateConversation(BaseMessage baseMessage, boolean isRemove) {
        if (binding.rvConversationList != null) {
            Conversation conversation = CometChatHelper.getConversationFromMessage(baseMessage);
            if (isRemove)
                binding.rvConversationList.remove(conversation);
            else
                binding.rvConversationList.update(conversation);
            checkNoConverstaion();
        }
    }

    /**
     * This method is used to remove the conversationlistener.
     */
    private void removeConversationListener() {
        CometChat.removeMessageListener(TAG);
        CometChat.removeGroupListener(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        conversationsRequest = null;

        binding.rvConversationList.clearList();
        checkLogin();
        addConversationListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        removeConversationListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");

    }

    private void stopHideShimmer() {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);
    }

    private void checkLogin() {
        if (!Utility.isCheckEmptyOrNull(SessionManager.getCometId(context))) {
            SessionManager.setCometId(context, Utility.getCometId(RealmController.getUser().getId()));
        }
        Log.e(TAG, "checkLogin: "+ SessionManager.getCometId(context) );
        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(SessionManager.getCometId(context), AppConfig.AppDetails.API_KEY, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    SessionManager.setCometId(context, user.getUid());
                    FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" + user.getUid());
                    makeConversationList();
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e(TAG, "onError: " + e.getMessage());
                }
            });
        } else {
            makeConversationList();
        }
    }
/*
    private void setupRvMessage() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rVMessage.setLayoutManager(linearLayoutManager1);
        messagesAdapter = new MessagesAdapter(context, messagesModelList);
        binding.rVMessage.setAdapter(messagesAdapter);

        messagesAdapter.setOnItemClickListener(new MessagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, MessagesDetailActivity.class);
                startActivity(intent);
            }
        });
    }
*/

/*
    private void setUpToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.myToolbar.myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.messages));
    }
*/

}
