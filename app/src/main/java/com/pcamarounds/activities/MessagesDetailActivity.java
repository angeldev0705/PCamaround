package com.pcamarounds.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.ActivityMessagesDetailBinding;
import com.pcamarounds.databinding.BsChooseImageBinding;
import com.pcamarounds.models.postdetail.PostDetailModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.MediaUtils;
import com.pcamarounds.utils.PersmissionUtils;
import com.pcamarounds.utils.Utility;
import com.schibstedspain.leku.LocationPickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import adapter.MessageAdapter;
import constant.StringContract;
import id.zelory.compressor.Compressor;
import listeners.StickyHeaderDecoration;
import okhttp3.ResponseBody;
import retrofit2.Response;
import utils.KeyBoardUtils;
import utils.Utils;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.facebook.appevents.AppEventsLogger;
import com.schibstedspain.leku.LocationPickerActivity;

public class MessagesDetailActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppEventsLogger logger;

    private static final String TAG = "MessagesDetailActivity";
    private Context context;
    private ActivityMessagesDetailBinding binding;
    private MessageAdapter messageAdapter;
    private String name = "", Id = "", avatarUrl = "", status = "", type = "";
    private StickyHeaderDecoration stickyHeaderDecoration;
    private List<BaseMessage> baseMessages = new ArrayList<>();
    private List<BaseMessage> messageList = new ArrayList<>();
    private boolean isBlockedByMe;
    private User loggedInUser = CometChat.getLoggedInUser();
    private Timer timer = new Timer();
    private MessagesRequest messagesRequest;    //Used to fetch messages.
    private static final int LIMIT = 30;
    private String memberNames;
    private LinearLayoutManager linearLayoutManager;
    private String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean isNoMoreMessages;
    private boolean isInProgress;
    private boolean isSmartReplyClicked;
    private boolean isEdit;
    private File Image_Path;
    private PostDetailModel postDetailModel;
    private String jobid = "", userid = "", bidid = "";
    public String photoFileName = "photo.jpg";
    private File file;
    private String bookingStatus = "",bidStatus = "";
    private boolean isCheck = true;
    private MediaRecorder recorder = null;
    private boolean isRecordingGranted = false;
    private final static int MAP_BUTTON_REQUEST_CODE = 34334;

    private View recordingStatusPanel = null;
    private View slideText = null;
    private Chronometer recording_time_text = null;
    private float startedDraggingX = -1;
    private float distCanMove = dp(160);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logger = AppEventsLogger.newLogger(this);

        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_messages_detail);
        context = this;
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_primary));

        recordingStatusPanel = findViewById(R.id.recordStatusPanel);
        recordingStatusPanel.setVisibility(View.GONE);
        slideText = findViewById(R.id.slideText);
        recording_time_text = findViewById(R.id.recording_time_text);

        //Utility.hideKeyboardNew(this);
        initView();
        checkPermission();
        if (!isRecordingGranted) {
            binding.btnMic.setVisibility(View.GONE);
            binding.btnSend.setVisibility(View.VISIBLE);
    }
    }

    private void initView() {
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.rvMessageList.setLayoutManager(linearLayoutManager);
        setComposeBoxListener();
        if (getIntent() != null) {
            //Id = getIntent().getStringExtra(StringContract.IntentStrings.UID);
            avatarUrl = getIntent().getStringExtra(StringContract.IntentStrings.AVATAR);
            status = getIntent().getStringExtra(StringContract.IntentStrings.STATUS);
            name = getIntent().getStringExtra(StringContract.IntentStrings.NAME);
            type = getIntent().getStringExtra(StringContract.IntentStrings.TYPE);
            if (type != null && type.equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                Id = getIntent().getStringExtra(StringContract.IntentStrings.GUID);
                String[] separated = Id.split("_");
                jobid = separated[0].replace("job", "");
                bidid = separated[1].replace("bid", "");
                userid = separated[3];
                Log.e(TAG, "initView: " + Id + "  " + jobid + "  " + bidid + "  " + userid);
                // description = getIntent().getStringExtra(StringContract.IntentStrings.DESCRIPTION);
                //  Log.e(TAG, "initView::::::::::::::: " + description);
                //  String[] separater = description.split("\\*");
                //  avatarUrl = separater[3];
                //  name = separater[1];
                //   binding.tvName.setText(Utility.capitalize(name));
              //  setAvatar();
                post_details();
            } else {
                binding.tvName.setText(Utility.capitalize(name));
                setAvatar();
            }

        }
        KeyBoardUtils.setKeyboardVisibilityListener(this, (View) binding.rvMessageList.getParent(), keyboardVisible -> {
            if (keyboardVisible) {
                scrollToBottom();
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheck) {
                    if (binding.recordLayout.getVisibility() == View.VISIBLE) {
                        sendAudioMessage();
                    } else {
                        String message = binding.etMessage.getText().toString().trim();
                        binding.etMessage.setText("");
                        if (!message.isEmpty()) {
                            sendMessage(message);

                        }
                    }

                } else {
                    Toast.makeText(context, "Este camaround ya no está disponible.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.btnMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) slideText
                            .getLayoutParams();
                    params.leftMargin = dp(30);
                    slideText.setLayoutParams(params);
                    slideText.setAlpha(1);

                    startedDraggingX = -1;
                    // startRecording();
                    startRecording();
                    view.getParent()
                            .requestDisallowInterceptTouchEvent(true);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    startedDraggingX = -1;
                    stopRecording(false);
                    // stopRecording(true);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = motionEvent.getX();
                    if (x < -distCanMove) {
                        stopRecording(true);
                        // stopRecording(false);
                    }
                    x = x + view.getX();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) slideText
                            .getLayoutParams();
                    if (startedDraggingX != -1) {
                        float dist = (x - startedDraggingX);
                        params.leftMargin = dp(30) + (int) dist;
                        slideText.setLayoutParams(params);
                        float alpha = 1.0f + dist / distCanMove;
                        if (alpha > 1) {
                            alpha = 1;
                        } else if (alpha < 0) {
                            alpha = 0;
                }
                        slideText.setAlpha(alpha);
            }
                    if (x <= slideText.getX() + slideText.getWidth()
                            + dp(30)) {
                        if (startedDraggingX == -1) {
                            startedDraggingX = x;
                            distCanMove = (recordingStatusPanel.getMeasuredWidth()
                                    - slideText.getMeasuredWidth() - dp(48)) / 2.0f;
                            if (distCanMove <= 0) {
                                distCanMove = dp(80);
                            } else if (distCanMove > dp(80)) {
                                distCanMove = dp(80);
                    }
                }
            }
                    if (params.leftMargin > dp(30)) {
                        params.leftMargin = dp(30);
                        slideText.setLayoutParams(params);
                        slideText.setAlpha(1);
                        startedDraggingX = -1;
                    }
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });

        binding.ivChatAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.llName.performClick();
            }
        });

        binding.llName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (userid != null && !userid.equals("")) {
                        Intent intent = new Intent(context, UserProfileActivity.class);
                        intent.putExtra(AppConstants.USER_ID, userid);
                        startActivity(intent);
                }

            }
        });

        binding.btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording(true);
            }
        });
        binding.btnAudioDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord();
            }
        });

        binding.ivAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (isCheck) {
                    bottomSheetDialog();
                } else {
                    Toast.makeText(context, "Este camaround ya no está disponible.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        binding.rvMessageList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                //for toolbar elevation animation i.e stateListAnimator
                // toolbar.setSelected(rvChatListView.canScrollVertically(-1));
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (!isNoMoreMessages && !isInProgress) {
                    if (linearLayoutManager.findFirstVisibleItemPosition() == 10 || !binding.rvMessageList.canScrollVertically(-1)) {
                        isInProgress = true;
                        fetchMessage();
                    }
                }
            }

        });

    }

    private void sendMessage(String message) {
        TextMessage textMessage;
        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
            textMessage = new TextMessage(Id, message, CometChatConstants.RECEIVER_TYPE_USER);
        } else {
            textMessage = new TextMessage(Id, message, CometChatConstants.RECEIVER_TYPE_GROUP);
        }
        sendTypingIndicator(true);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                //here
                Bundle bundle = new Bundle();
                mFirebaseAnalytics.logEvent("chat_success", bundle);
                logger.logEvent("chat_success");

                //isSmartReplyClicked=false;
                if (messageAdapter != null) {
                    // MediaUtils.playSendSound(context, com.cometchat.pro.uikit.R.raw.outgoing_message);
                    messageAdapter.addMessage(textMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });

    }

    private void setSubTitle(String... users) {
        if (users != null && users.length != 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (String user : users) {
                stringBuilder.append(user).append(",");
            }

            memberNames = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();

            binding.tvStatus.setText(memberNames);
        }

    }

    private void addGroupListener() {
        CometChat.addGroupListener(TAG, new CometChat.GroupListener() {
            @Override
            public void onGroupMemberJoined(Action action, User joinedUser, Group joinedGroup) {
                super.onGroupMemberJoined(action, joinedUser, joinedGroup);
                binding.tvStatus.setText(memberNames + "," + joinedUser.getName());
                if (messageAdapter != null) {
                    messageAdapter.addMessage(action);
                }
            }

            @Override
            public void onGroupMemberLeft(Action action, User leftUser, Group leftGroup) {
                super.onGroupMemberLeft(action, leftUser, leftGroup);
                Log.d(TAG, "onGroupMemberLeft: " + leftUser.getName());
                if (memberNames != null)
                    binding.tvStatus.setText(memberNames.replace("," + leftUser.getName(), ""));
                if (messageAdapter != null) {
                    messageAdapter.addMessage(action);
                    if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() < 10)
                        scrollToBottom();
                }
            }

            @Override
            public void onGroupMemberKicked(Action action, User kickedUser, User kickedBy, Group kickedFrom) {
                super.onGroupMemberKicked(action, kickedUser, kickedBy, kickedFrom);
                Log.d(TAG, "onGroupMemberKicked: " + kickedUser.getName());
                if (kickedUser.getUid().equals(CometChat.getLoggedInUser().getUid())) {
                    if (this != null)
                        finish();
//                        ((Activity) getActivity()).onBackPressed();

                }
                binding.tvStatus.setText(memberNames.replace("," + kickedUser.getName(), ""));
                if (messageAdapter != null) {
                    messageAdapter.addMessage(action);
                    scrollToBottom();
                }
            }

            @Override
            public void onGroupMemberBanned(Action action, User bannedUser, User bannedBy, Group bannedFrom) {
                if (bannedUser.getUid().equals(CometChat.getLoggedInUser().getUid())) {
                    if (this != null) {
                        onBackPressed();
                        Toast.makeText(context, "You have been banned", Toast.LENGTH_SHORT).show();
                    }
                }
                if (messageAdapter != null) {
                    messageAdapter.addMessage(action);
                    scrollToBottom();
                }

            }

            @Override
            public void onGroupMemberUnbanned(Action action, User unbannedUser, User unbannedBy, Group unbannedFrom) {
                if (messageAdapter != null) {
                    messageAdapter.addMessage(action);
                    scrollToBottom();
                }
            }

            @Override
            public void onGroupMemberScopeChanged(Action action, User updatedBy, User updatedUser, String scopeChangedTo, String scopeChangedFrom, Group group) {
                if (messageAdapter != null) {
                    messageAdapter.addMessage(action);
                    scrollToBottom();
                }
            }

            @Override
            public void onMemberAddedToGroup(Action action, User addedby, User userAdded, Group addedTo) {
                binding.tvStatus.setText(memberNames + "," + userAdded.getName());
                if (messageAdapter != null) {
                    messageAdapter.addMessage(action);
                    if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() < 10)
                        scrollToBottom();
                }
            }
        });
    }

    /**
     * This method is used to get real time user status i.e user is online or offline.
     *
     * @see CometChat#addUserListener(String, CometChat.UserListener)
     */
    private void addUserListener() {
        if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            CometChat.addUserListener(TAG, new CometChat.UserListener() {
                @Override
                public void onUserOnline(User user) {
                    Log.d(TAG, "onUserOnline: " + user.toString());
                    if (user.getUid().equals(Id)) {
                        binding.tvStatus.setText(user.getStatus());
                        binding.tvStatus.setTextColor(getResources().getColor(com.cometchat.pro.uikit.R.color.green_600));
                    }
                }

                @Override
                public void onUserOffline(User user) {
                    Log.d(TAG, "onUserOffline: " + user.toString());
                    if (user.getUid().equals(Id)) {
                        binding.tvStatus.setTextColor(getResources().getColor(android.R.color.white));
                        binding.tvStatus.setText(user.getStatus());
                    }
                }
            });
        } else {
            Log.e(TAG, "addUserListener: " + type);
        }
    }

    private void scrollToBottom() {
        if (messageAdapter != null && messageAdapter.getItemCount() > 0) {
            binding.rvMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);

        }
    }

    private void initMessageAdapter(List<BaseMessage> messageList) {
        if (messageAdapter == null) {
            messageAdapter = new MessageAdapter(context, messageList, type);
            binding.rvMessageList.setAdapter(messageAdapter);
            stickyHeaderDecoration = new StickyHeaderDecoration(messageAdapter);
            binding.rvMessageList.addItemDecoration(stickyHeaderDecoration, 0);
            scrollToBottom();
            messageAdapter.notifyDataSetChanged();
        } else {
            messageAdapter.updateList(messageList);

        }
      /*  if (!isBlockedByMe && rvSmartReply.getAdapter().getItemCount()==0&&rvSmartReply.getVisibility() == View.GONE) {
            BaseMessage lastMessage = messageAdapter.getLastMessage();
            checkSmartReply(lastMessage);
        }*/
    }

    private void checkSmartReply(BaseMessage lastMessage) {
        if (lastMessage != null && !lastMessage.getSender().getUid().equals(loggedInUser.getUid())) {
            if (lastMessage.getMetadata() != null) {
                getSmartReplyList(lastMessage);
            }
        }
    }

    private void getSmartReplyList(BaseMessage baseMessage) {

        HashMap<String, JSONObject> extensionList = Utils.extensionCheck(baseMessage);
        if (extensionList != null && extensionList.containsKey("smartReply")) {
            // rvSmartReply.setVisibility(View.VISIBLE);
            JSONObject replyObject = extensionList.get("smartReply");
            List<String> replyList = new ArrayList<>();
            try {
                replyList.add(replyObject.getString("reply_positive"));
                replyList.add(replyObject.getString("reply_neutral"));
                replyList.add(replyObject.getString("reply_negative"));
            } catch (Exception e) {
                Log.e(TAG, "onSuccess: " + e.getMessage());
            }
            setSmartReplyAdapter(replyList);
        } else {
            // rvSmartReply.setVisibility(View.GONE);
        }
    }

    private void setSmartReplyAdapter(List<String> replyList) {
        // rvSmartReply.setSmartReplyList(replyList);
        scrollToBottom();
    }

    private void markMessageAsRead(BaseMessage baseMessage) {
        if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            CometChat.markAsRead(baseMessage.getId(), baseMessage.getSender().getUid(), baseMessage.getReceiverType());
        } else {
            Log.e(TAG, "markMessageAsRead: " + baseMessage.getId() + "    " + baseMessage.getReceiverUid() + "    " + baseMessage.getReceiverType());
            CometChat.markAsRead(baseMessage.getId(), baseMessage.getReceiverUid(), baseMessage.getReceiverType());
        }
    }

    private void sendTypingIndicator(boolean isEnd) {
        if (isEnd) {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                CometChat.endTyping(new TypingIndicator(Id, CometChatConstants.RECEIVER_TYPE_USER));
            } else {
                CometChat.endTyping(new TypingIndicator(Id, CometChatConstants.RECEIVER_TYPE_GROUP));
            }
        } else {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                CometChat.startTyping(new TypingIndicator(Id, CometChatConstants.RECEIVER_TYPE_USER));
            } else {
                CometChat.startTyping(new TypingIndicator(Id, CometChatConstants.RECEIVER_TYPE_GROUP));
            }
        }
    }

    private void setComposeBoxListener() {
        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                    sendTypingIndicator(false);
                    binding.btnMic.setVisibility(View.GONE);
                    binding.btnSend.setVisibility(View.VISIBLE);
                } else {
                    sendTypingIndicator(true);
                    if (isRecordingGranted) {
                    binding.btnMic.setVisibility(View.VISIBLE);
                    binding.btnSend.setVisibility(View.GONE);
                    } else {
                        binding.btnMic.setVisibility(View.GONE);
                        binding.btnSend.setVisibility(View.VISIBLE);
                }
            }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (timer == null) {
                    timer = new Timer();
                }
                endTypingTimer();
            }
        });
    }

    private void endTypingTimer() {
        if (timer != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendTypingIndicator(true);
                }
            }, 2000);
        }
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            finish();
        }
    }

    private void setMessageReciept(MessageReceipt messageReceipt) {
        if (messageAdapter != null) {
            if (messageReceipt.getReceivertype().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                if (Id != null && messageReceipt.getSender().getUid().equals(Id)) {
                    if (messageReceipt.getReceiptType().equals(MessageReceipt.RECEIPT_TYPE_DELIVERED))
                        messageAdapter.setDeliveryReceipts(messageReceipt);
                    else
                        messageAdapter.setReadReceipts(messageReceipt);
                }
            }
        }
    }

    private void setTypingIndicator(TypingIndicator typingIndicator, boolean isShow) {
        if (typingIndicator.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
            Log.e(TAG, "onTypingStarted: " + typingIndicator);
            if (Id != null && Id.equalsIgnoreCase(typingIndicator.getSender().getUid()))
                typingIndicator(typingIndicator, isShow);
        } else {
            if (Id != null && Id.equalsIgnoreCase(typingIndicator.getReceiverId()))
                typingIndicator(typingIndicator, isShow);
        }
    }

    private void onMessageReceived(BaseMessage message) {
        // MediaUtils.playSendSound(context, com.cometchat.pro.uikit.R.raw.incoming_message);
        if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            if (Id != null && Id.equalsIgnoreCase(message.getSender().getUid())) {
                setMessage(message);
            }
        } else {
            if (Id != null && Id.equalsIgnoreCase(message.getReceiverUid())) {
                setMessage(message);
            }
        }
    }

    /**
     * This method is used to update edited message by calling <code>setEditMessage()</code> of adapter
     *
     * @param message is an object of BaseMessage and it will replace with old message.
     * @see BaseMessage
     */
    private void updateMessage(BaseMessage message) {
        messageAdapter.setUpdatedMessage(message);
    }


    /**
     * This method is used to mark message as read before adding them to list. This method helps to
     * add real time message in list.
     *
     * @param message is an object of BaseMessage, It is recieved from message listener.
     * @see BaseMessage
     */
    private void setMessage(BaseMessage message) {
        if (messageAdapter != null) {
            messageAdapter.addMessage(message);
            checkSmartReply(message);
            markMessageAsRead(message);
            if ((messageAdapter.getItemCount() - 1) - ((LinearLayoutManager) binding.rvMessageList.getLayoutManager()).findLastVisibleItemPosition() < 5)
                scrollToBottom();
        } else {
            messageList.add(message);
            initMessageAdapter(messageList);
        }
    }


    /**
     * This method is used to display typing status to user.
     *
     * @param show is boolean, If it is true then <b>is Typing</b> will be shown to user
     *             If it is false then it will show user status i.e online or offline.
     */
    private void typingIndicator(TypingIndicator typingIndicator, boolean show) {
        if (messageAdapter != null) {
            if (show) {
                if (typingIndicator.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER))
                    binding.tvStatus.setText("Escribiendo un mensaje...");
                else
                    binding.tvStatus.setText(typingIndicator.getSender().getName() + " escribiendo un mensaje...");
            } else {
                if (typingIndicator.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER))
                    binding.tvStatus.setText(status);
                else
                    binding.tvStatus.setText(memberNames);
            }

        }
    }

    /**
     * This method is used to remove message listener
     *
     * @see CometChat#removeMessageListener(String)
     */
    private void removeMessageListener() {
        CometChat.removeMessageListener(TAG);
    }

    /**
     * This method is used to remove user presence listener
     *
     * @see CometChat#removeUserListener(String)
     */
    private void removeUserListener() {
        CometChat.removeUserListener(TAG);
    }


    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        removeMessageListener();
        removeUserListener();
        removeGroupListener();
        timer = null;
        sendTypingIndicator(true);


    }

    private void removeGroupListener() {
        CometChat.removeGroupListener(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        binding.rvMessageList.removeItemDecoration(stickyHeaderDecoration);
        messageAdapter = null;
        messagesRequest = null;
        fetchMessage();
        addMessageListener();
        Intent intent3 = new Intent(AppConstants.BROADCAST);
        intent3.putExtra(AppConstants.UNREADCOUNT, AppConstants.FLAG);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent3);
        if (type != null) {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                addUserListener();
                binding.tvStatus.setText(status);
                new Thread(this::getUser).start();
            } else {
                if (Utility.isCheckEmptyOrNull(status) && status.equals(CometChatConstants.USER_STATUS_ONLINE)) {
                    binding.tvStatus.setTextColor(context.getResources().getColor(R.color.textColorWhite));
                    status = "En línea";
                    binding.tvStatus.setText(status);
                } else {
                    status = "Fuera de línea";
                    binding.tvStatus.setText(status);
                }
                new Thread(this::getGroupInfo).start();
            }
        }
    }

    private void sendMediaMessage(File file, String filetype) {
        MediaMessage mediaMessage;
        Log.e(TAG, "sendMediaMessage: " + file.getName() + "  " + filetype);
        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            mediaMessage = new MediaMessage(Id, file, filetype, CometChatConstants.RECEIVER_TYPE_USER);
        else
            mediaMessage = new MediaMessage(Id, file, filetype, CometChatConstants.RECEIVER_TYPE_GROUP);

        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                Log.e(TAG, "sendMediaMessage onSuccess: " + mediaMessage.toString());
                if (messageAdapter != null) {
                    messageAdapter.addMessage(mediaMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "sendMediaMessage onError: " + e.getMessage() + mediaMessage.toString());
                if (MessagesDetailActivity.this != null) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchMessage() {
        if (messagesRequest == null) {
            if (type != null) {
                if (type.equals(CometChatConstants.RECEIVER_TYPE_USER))
                    messagesRequest = new MessagesRequest.MessagesRequestBuilder().setLimit(LIMIT).setUID(Id).build();
                else
                    messagesRequest = new MessagesRequest.MessagesRequestBuilder().setLimit(LIMIT).setGUID(Id).hideMessagesFromBlockedUsers(true).build();
            }
        }
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {

            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                isInProgress = false;
                List<BaseMessage> filteredMessageList = filterBaseMessages(baseMessages);
                initMessageAdapter(filteredMessageList);
                if (baseMessages.size() != 0) {
                    stopHideShimmer();
                    BaseMessage baseMessage = baseMessages.get(baseMessages.size() - 1);
                    markMessageAsRead(baseMessage);

                }

                if (baseMessages.size() == 0) {
                    stopHideShimmer();
                    isNoMoreMessages = true;
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void getGroupInfo() {
        GroupMembersRequest groupMembersRequest = null;
        int limit = 3;

        groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(Id).setLimit(limit).build();

        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                String uuu = CometChat.getLoggedInUser().getUid();
                for (int i = 0; i < list.size(); i++) {
                    if (!uuu.equals(list.get(i).getUid()) && !"camaround_admin_1".equals(list.get(i).getUid())) {
                        avatarUrl = list.get(i).getAvatar();
                        name = list.get(i).getName();
                        binding.tvName.setText(Utility.capitalize(name));
                        if (list.get(i).getStatus().equals(CometChatConstants.USER_STATUS_ONLINE)) {
                            status = "En línea";
                            binding.tvStatus.setText(status);
                            binding.tvStatus.setTextColor(context.getResources().getColor(R.color.textColorWhite));
                        } else {
                            status = "Fuera de línea";
                            binding.tvStatus.setText(status);
                        }

                        setAvatar();
                    }
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "Group Member list fetching failed with exception: " + e.getMessage());
            }

        });
    }

    private void getUser() {
        CometChat.getUser(Id, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (this != null) {
                    if (user.isBlockedByMe()) {
                        isBlockedByMe = true;
                        // rvSmartReply.setVisibility(View.GONE);
                        // toolbar.setSelected(false);
                        //  blockedUserName.setText("You've blocked " + user.getName());
                        // blockUserLayout.setVisibility(View.VISIBLE);
                    } else {
                        isBlockedByMe = false;
                        // blockUserLayout.setVisibility(View.GONE);
                        avatarUrl = user.getAvatar();
                        if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE)) {
                            binding.tvStatus.setTextColor(context.getResources().getColor(com.cometchat.pro.uikit.R.color.green_600));
                        }
                        status = user.getStatus().toString();
                        setAvatar();
                        binding.tvStatus.setText(status);

                    }
                    name = user.getName();
                    binding.tvName.setText(name);
                    Log.d(TAG, "onSuccess: " + user.toString());
                }

            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAvatar() {
        if (avatarUrl != null && !avatarUrl.isEmpty() && !avatarUrl.equals("http://34.223.228.22/52a363e725235501a0fc865f1f628006.jpg"))
            binding.ivChatAvatar.setAvatar(avatarUrl);
        else {
            binding.ivChatAvatar.setInitials(name);
        }
    }

    private void addMessageListener() {
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                Log.d(TAG, "onTextMessageReceived: " + message.toString());
                onMessageReceived(message);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                Log.d(TAG, "onMediaMessageReceived: " + message.toString());
                onMessageReceived(message);
            }

            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                Log.e(TAG, "onTypingStarted: " + typingIndicator);
                setTypingIndicator(typingIndicator, true);
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                Log.d(TAG, "onTypingEnded: " + typingIndicator.toString());
                setTypingIndicator(typingIndicator, false);
            }

            @Override
            public void onMessagesDelivered(MessageReceipt messageReceipt) {
                Log.d(TAG, "onMessagesDelivered: " + messageReceipt.toString());
                setMessageReciept(messageReceipt);

            }

            @Override
            public void onMessagesRead(MessageReceipt messageReceipt) {
                Log.e(TAG, "onMessagesRead: " + messageReceipt.toString());
                setMessageReciept(messageReceipt);
            }

            @Override
            public void onMessageEdited(BaseMessage message) {
                Log.d(TAG, "onMessageEdited: " + message.toString());
                updateMessage(message);
            }

            @Override
            public void onMessageDeleted(BaseMessage message) {
                Log.d(TAG, "onMessageDeleted: ");
                updateMessage(message);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage message) {
                super.onCustomMessageReceived(message);
                Log.d(TAG, "onCustomMessageReceived: ");
            }
        });
    }

    private List<BaseMessage> filterBaseMessages(List<BaseMessage> baseMessages) {
        List<BaseMessage> tempList = new ArrayList<>();
        for (BaseMessage baseMessage : baseMessages) {
            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                Action action = ((Action) baseMessage);
                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                } else {
                    tempList.add(baseMessage);
                }
            } else {
                tempList.add(baseMessage);
            }
        }
        return tempList;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void bottomSheetDialog() {
        //View dialogView = getLayoutInflater().inflate(R.layout.bts_payment, null);
        final BsChooseImageBinding bsBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.bs_choose_image, (ViewGroup) binding.getRoot(), false);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        bsBinding.llFile.setVisibility(View.VISIBLE);
        bsBinding.llCam.setOnClickListener(v -> {
            dialog.dismiss();
            if (Utils.hasPermissions(context, CAMERA_PERMISSION)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = getPhotoFileUri(photoFileName);
                Uri fileProvider = FileProvider.getUriForFile(context, context.getResources().getString(R.string.file_provider_authorities), file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, StringContract.RequestCode.CAMERA);
                }
            } else {
                requestPermissions(CAMERA_PERMISSION, StringContract.RequestCode.CAMERA);
            }
        });
        bsBinding.llGallery.setOnClickListener(v -> {
            if (Utils.hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                startActivityForResult(MediaUtils.openGallery(this), StringContract.RequestCode.GALLERY);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.GALLERY);
            }
            dialog.dismiss();
        });
        bsBinding.llFile.setOnClickListener(v -> {
            if (Utils.hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                startActivityForResult(MediaUtils.getFileIntent(StringContract.IntentStrings.EXTRA_MIME_DOC), StringContract.RequestCode.FILE);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.FILE);
            }
            dialog.dismiss();
        });
        bsBinding.llLocation.setVisibility(View.VISIBLE);
        bsBinding.llLocation.setOnClickListener(v -> {
//            if (Utils.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                startActivityForResult(MediaUtils.getFileIntent(StringContract.IntentStrings.EXTRA_MIME_DOC), StringContract.RequestCode.FILE);
//            } else {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.FILE);
//            }
            locationShare();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(bsBinding.getRoot());
        dialog.show();
    }

    private void turnOnLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Turn On GPS");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), StringContract.RequestCode.LOCATION);
            }
        }).setNegativeButton(getString(com.cometchat.pro.uikit.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }


    private FusedLocationProviderClient fusedLocationProviderClient;

    void locationShare() {
        if (Utils.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            initLocation();
//                    locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
            boolean provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!provider) {
                turnOnLocation();
            } else {
                getLocation();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, StringContract.RequestCode.LOCATION);
        }
    }

    private void getLocation() {

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lon = location.getLongitude();
                    double lat = location.getLatitude();
                    String apiKey = getString(R.string.google_maps_key);
                    Intent locationPickerIntent = new Intent(context, LocationPickerActivity.class);
                    startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE);
//                    locationPickerIntent.putExtra(LocationPickerActivity.googlePlacesApiKey)
//                    Intent locationPickerIntent = new LocationPickerActivity().Builder()
//                            .withLocation(lat, lon)
//                            .withGeolocApiKey(apiKey)
//                            .withSearchZone("es_ES")
////                            .withSearchZone(SearchZoneRect(LatLng(26.525467, -18.910366), LatLng(43.906271, 5.394197)))
//                            .withDefaultLocaleSearchZone()
//                            .shouldReturnOkOnBackPressed()
//                            .withStreetHidden()
//                            .withCityHidden()
//                            .withZipCodeHidden()
//                            .withSatelliteViewHidden()
//                            .withGooglePlacesEnabled()
//                            .withGoogleTimeZoneEnabled()
//                            .withVoiceSearchHidden()
//                            .withUnnamedRoadHidden()
//                            .build(this);


//                    JSONObject customData = new JSONObject();
//                    try {
//                        customData.put("latitude", lat);
//                        customData.put("longitude", lon);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    initAlert(customData);
                } else {
                    Toast.makeText(context, "Unable to get your location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    double LATITUDE = 0;
    double LONGITUDE = 0;

    private void initAlert(JSONObject customData) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(context).inflate(R.layout.map_share_layout, null);
//        builder.setView(view);
//        try {
//            LATITUDE = customData.getDouble("latitude");
//            LONGITUDE = customData.getDouble("longitude");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        TextView address = view.findViewById(R.id.address);
//        address.setText("Address: " + Utils.getAddress(context, LATITUDE, LONGITUDE));
//        ImageView mapView = view.findViewById(R.id.map_vw);
//        String apiKey = getString(R.string.google_maps_key);
//        String mapUrl = StringContract.MapUrl.MAPS_URL + LATITUDE + "," + LONGITUDE + "&key=" + apiKey;
//        Glide.with(this)
//                .load(mapUrl)
//                .into(mapView);
//
//        builder.setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                sendCustomMessage(StringContract.IntentStrings.LOCATION, customData);
//            }
//        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create();
//        builder.show();
    }

    LocationListener locationListener;
    LocationManager locationManager;

    private void initLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;

    private void sendCustomMessage(String customType, JSONObject customData) {
        CustomMessage customMessage;

        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            customMessage = new CustomMessage(Id, CometChatConstants.RECEIVER_TYPE_USER, customType, customData);
        else
            customMessage = new CustomMessage(Id, CometChatConstants.RECEIVER_TYPE_GROUP, customType, customData);

        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage customMessage) {
                if (messageAdapter != null) {
                    messageAdapter.addMessage(customMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {

                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        switch (requestCode) {
            case MAP_BUTTON_REQUEST_CODE:
                if (data == null) break;
                double latitude = data.getDoubleExtra("latitude", 0.0);
                double longitude = data.getDoubleExtra("longitude", 0.0);
                JSONObject customData = new JSONObject();
                try {
                    customData.put("latitude", latitude);
                    customData.put("longitude", longitude);
                    Log.e(TAG, "lat = " + latitude + ", long " + longitude);

                    sendCustomMessage(StringContract.IntentStrings.LOCATION, customData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case StringContract.RequestCode.GALLERY:
                if (data != null) {
                    File file = MediaUtils.getRealPath(context, data.getData());
                    ContentResolver cr = getContentResolver();
                    String mimeType = cr.getType(data.getData());
                    if (mimeType != null && mimeType.contains("image")) {
                        if (file.exists()) {
                            try {
                                Image_Path = null;
                                if ((file.length() / 1024) < 1024) {
                                    Image_Path = file;
                                } else if ((file.length() / 1024) > 1024 && (file.length() / 1024) < 2048) {
                                    Image_Path = new Compressor(context).setQuality(50).compressToFile(file);
                                } else if ((file.length() / 1024) > 3072) {
                                    Image_Path = new Compressor(context).setQuality(75).compressToFile(file);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sendMediaMessage(Image_Path, CometChatConstants.MESSAGE_TYPE_IMAGE);
                        } else {
                            Log.e(TAG, "onActivityResult: " + com.cometchat.pro.uikit.R.string.file_not_exist);
                        }
                    } else {
                        if (file.exists()) {
                            try {
                                Image_Path = null;
                                if ((file.length() / 1024) < 1024) {
                                    Image_Path = file;
                                } else if ((file.length() / 1024) > 1024 && (file.length() / 1024) < 2048) {
                                    Image_Path = new Compressor(context).setQuality(50).compressToFile(file);
                                } else if ((file.length() / 1024) > 3072) {
                                    Image_Path = new Compressor(context).setQuality(75).compressToFile(file);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sendMediaMessage(Image_Path, CometChatConstants.MESSAGE_TYPE_VIDEO);
                        } else {
                            Log.e(TAG, "onActivityResult: " + com.cometchat.pro.uikit.R.string.file_not_exist);
                        }
                    }
                }

                break;
            case StringContract.RequestCode.CAMERA:
                if (file.exists()) {
                    try {
                        Image_Path = null;
                        if ((file.length() / 1024) < 1024) {
                            Image_Path = file;
                        } else if ((file.length() / 1024) > 1024 && (file.length() / 1024) < 2048) {
                            Image_Path = new Compressor(context).setQuality(50).compressToFile(file);
                        } else if ((file.length() / 1024) > 3072) {
                            Image_Path = new Compressor(context).setQuality(75).compressToFile(file);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendMediaMessage(Image_Path, CometChatConstants.MESSAGE_TYPE_IMAGE);
                } else {
                    Log.e(TAG, "onActivityResult: " + com.cometchat.pro.uikit.R.string.file_not_exist);
                }

                break;
            case StringContract.RequestCode.FILE:
                if (data != null)
                    sendMediaMessage(MediaUtils.getRealPath(context, data.getData()), CometChatConstants.MESSAGE_TYPE_FILE);
                break;
            case StringContract.RequestCode.BLOCK_USER:
                name = data.getStringExtra("");
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {

            case StringContract.RequestCode.CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = getPhotoFileUri(photoFileName);
                    Uri fileProvider = FileProvider.getUriForFile(context, context.getResources().getString(R.string.file_provider_authorities), file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, StringContract.RequestCode.CAMERA);
                    }
                }
                // startActivityForResult(MediaUtils.openCamera(context), StringContract.RequestCode.CAMERA);
                else
                    // showSnackBar(view.findViewById(com.cometchat.pro.uikit.R.id.message_box), getResources().getString(com.cometchat.pro.uikit.R.string.grant_camera_permission));
                    break;
            case StringContract.RequestCode.GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startActivityForResult(MediaUtils.openGallery(this), StringContract.RequestCode.GALLERY);
                else
                    // showSnackBar(view.findViewById(com.cometchat.pro.uikit.R.id.message_box), getResources().getString(com.cometchat.pro.uikit.R.string.grant_storage_permission));
                    break;
            case StringContract.RequestCode.FILE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startActivityForResult(MediaUtils.getFileIntent(StringContract.IntentStrings.EXTRA_MIME_DOC), StringContract.RequestCode.FILE);
                else
                    // showSnackBar(view.findViewById(com.cometchat.pro.uikit.R.id.message_box), getResources().getString(com.cometchat.pro.uikit.R.string.grant_storage_permission));
                    break;
//            case PersmissionUtils.REQUEST_RECORD_AUDIO_PERMISSION:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    startRecording();
//                else break;
            case StringContract.RequestCode.LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
//                    locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
                    boolean provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!provider) {
                        turnOnLocation();
                    } else {
                        getLocation();
                    }
                } else
//                    showSnackBar(view.findViewById(com.cometchat.pro.uikit.R.id.message_box), getResources().getString(com.cometchat.pro.uikit.R.string.grant_location_permission));
                    break;
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void stopHideShimmer() {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);
    }

    private void post_details() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        if (RealmController.getUser() != null) {
            hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        } else {
            hashMap.put(AppConstants.UID, "");
        }
        hashMap.put(AppConstants.JOB_ID, jobid);
        hashMap.put(AppConstants.USER_ID, userid);


        Log.e(TAG, "post_details : " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).post_details(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    postDetailModel = Controller.getGson().fromJson(JsonUtil.mainjson(response).toString(), PostDetailModel.class);
                    if (postDetailModel.getStatus() == 1) {
                        setDataFom(postDetailModel);
                    } else if (postDetailModel.getStatus() == 0) {
                        // Utility.toast(context, postDetailModel.getMessage());
                    } else if (postDetailModel.getStatus() == 4) { //Invalid Access Token
                        Utility.toast(context, postDetailModel.getMessage());
                        Utility.logout(MessagesDetailActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataFom(PostDetailModel postDetailModel) {
        binding.tvServiceName.setText(postDetailModel.getData().getServiceName());
        bidStatus = postDetailModel.getPostBidData().getBidStatus();
        bookingStatus = postDetailModel.getData().getBookingStatus();

        if (bookingStatus != null && bookingStatus.equals("completed")) {
            binding.etMessage.setEnabled(false);
            isCheck = false;
        } else if (bookingStatus.equals("inprogress") && bidStatus.equals("pending")) {
            binding.etMessage.setEnabled(false);
            isCheck = false;
        }else{
            binding.etMessage.setEnabled(true);
            isCheck = true;
        }
        if (postDetailModel.getPostBidData().getBidStatus().equals("awarded")
                || postDetailModel.getPostBidData().getBidStatus().equals("approve")
                || postDetailModel.getPostBidData().getBidStatus().equals("inprogress")
                || postDetailModel.getPostBidData().getBidStatus().equals("cancelled")
                || postDetailModel.getPostBidData().getBidStatus().equals("declined")
                || postDetailModel.getPostBidData().getBidStatus().equals("completed")
                || postDetailModel.getPostBidData().getBidStatus().equals("dispute")
                || postDetailModel.getPostBidData().getBidStatus().equals("incomplete")
                || postDetailModel.getPostBidData().getBidStatus().equals("closed")) {
            binding.btnModificar.setVisibility(View.GONE);
        } else {
            if (postDetailModel.getData().getBookingStatus() != null && postDetailModel.getData().getBookingStatus().equals("inprogress"))
            {
                binding.btnModificar.setVisibility(View.GONE);
            }else {
                binding.btnModificar.setVisibility(View.VISIBLE);
            }
            binding.btnModificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SearchDetailActivity.class);
                    intent.putExtra(AppConstants.JOB_ID, postDetailModel.getPostBidData().getJobId());
                    intent.putExtra(AppConstants.USER_ID, postDetailModel.getPostBidData().getUserId());
                    intent.putExtra(AppConstants.SERVICE_NAME, postDetailModel.getData().getServiceName());
                    startActivity(intent);
                }
            });
        }
    }

    String fileName;
    boolean isRecording = false;
    private Timer recordTimer = new Timer();

    private boolean checkPermission() {
        PersmissionUtils persmissionUtils = new PersmissionUtils(context, this);
        boolean isGranted = persmissionUtils.requestRecordAudioPermissions();
        if (!isGranted) {
            Toast.makeText(context, "Este camaround ya no está disponible.", Toast.LENGTH_SHORT).show();
        }
        isRecordingGranted = isGranted;
        return isGranted;
    }

    private void startRecording() {
        boolean isGranted = checkPermission();
        if (!isGranted) {
            return;
        }

        binding.recordTime.setBase(SystemClock.elapsedRealtime());
        binding.recordTime.start();
        recording_time_text.setBase(SystemClock.elapsedRealtime());
        recording_time_text.start();

        binding.recordLayout.setVisibility(View.GONE);
        recordingStatusPanel.setVisibility(View.VISIBLE);
        binding.etMessage.setVisibility(View.INVISIBLE);
        binding.ivAdd.setVisibility(View.INVISIBLE);
        binding.btnMic.setScaleX(1.0f);
        binding.btnMic.setScaleY(1.0f);

//        binding.btnMic.setVisibility(View.GONE);
//        binding.btnSend.setVisibility(View.GONE);
//        binding.btnStopRecord.setVisibility(View.VISIBLE);


        isRecording = true;
        fileName = getExternalCacheDir().getAbsolutePath() + "/" + UUID.randomUUID() + "_audio.mp3";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException | IllegalStateException e) {
            Log.e(TAG, "prepare() failed: " + e.getMessage());
        }
        recordTimer = new Timer();
        recordTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int currentMaxAmp = 0;
                try {
                    currentMaxAmp = recorder != null ? recorder.getMaxAmplitude() : 0;
                    binding.voiceMessageSeekbar.setMax(3 * 60);
                    long second = (SystemClock.elapsedRealtime() - binding.recordTime.getBase()) / 1000;
                    binding.voiceMessageSeekbar.setProgress((int) second);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 100);
        try {
        recorder.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, "recoder.start() failed: " + e.getMessage());
        }
        Log.e(TAG, "start recording " + fileName);
    }

    private void stopRecording(boolean isCancel) {
        if (!isRecording) {
            return;
        }
        isRecording = false;
        if (recordTimer != null)
        recordTimer.cancel();

        if (recorder != null) {
            try {
        recorder.stop();
            } catch (IllegalStateException ex) {
                Log.e(TAG, ex.getMessage());
            }
            recorder.reset();
        recorder.release();
        recorder = null;
    }
        binding.recordTime.stop();
        recording_time_text.stop();

        if (isCancel) {
            deleteRecord();
        } else {
            sendAudioMessage();
        }
    }

    void _initChatElement() {
        binding.recordLayout.setVisibility(View.GONE);

        if (isRecordingGranted) {
        binding.btnSend.setVisibility(View.GONE);
        binding.btnMic.setVisibility(View.VISIBLE);
        } else {
            binding.btnSend.setVisibility(View.VISIBLE);
            binding.btnMic.setVisibility(View.GONE);
        }
        binding.btnAudioDelete.setVisibility(View.INVISIBLE);
        binding.btnStopRecord.setVisibility(View.GONE);

        binding.etMessage.setVisibility(View.VISIBLE);
        binding.ivAdd.setVisibility(View.VISIBLE);

        recordingStatusPanel.setVisibility(View.GONE);
        binding.btnMic.setScaleX(1.0f);
        binding.btnMic.setScaleY(1.0f);
    }

    void deleteRecord() {
        this._initChatElement();
    }

    void sendAudioMessage() {
        sendMediaMessage(new File(fileName), CometChatConstants.MESSAGE_TYPE_AUDIO);
        this._initChatElement();
    }

    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }

}
