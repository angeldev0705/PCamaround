package com.pcamarounds.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.activities.AboutActivity;
import com.pcamarounds.activities.BankAccountActivity;
import com.pcamarounds.activities.CardListActivity;
import com.pcamarounds.activities.ChangePasswordActivity;
import com.pcamarounds.activities.ContactUsActivity;
import com.pcamarounds.activities.HelpCenterActivity;
import com.pcamarounds.activities.MyWalletActivity;
import com.pcamarounds.activities.NotificationActivity;
import com.pcamarounds.activities.PayNowActivity;
import com.pcamarounds.activities.PersonalInformationActivity;
import com.pcamarounds.activities.PhotoFullPopupWindow;
import com.pcamarounds.activities.ProfileActivity;
import com.pcamarounds.activities.TermAndConditionActivity;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.controller.SessionManager;
import com.pcamarounds.databinding.FragmentProfileBinding;
import com.pcamarounds.models.LoginModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private Context context;
    private FragmentProfileBinding binding;
    private RealmResults<LoginModel> loginModelRealmResults;
    private RealmChangeListener<RealmResults<LoginModel>> realmChangeListener = (LoginModel) -> {
        setDataProfile();
    };
    private ConversationsRequest conversationsRequest;

    private void setDataProfile() {
        if (RealmController.getUser() != null) {
            binding.tvName.setText(Utility.capitalize(RealmController.getUser().getFirstName()+" "+Utility.printInitials(RealmController.getUser().getLastName())));
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getProfileImage())) {
                Picasso.get().load(AppConstants.PROFILE_IMG + RealmController.getUser().getProfileImage()).placeholder(R.drawable.profile_default).into(binding.ivProfile);
                binding.ivProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = AppConstants.PROFILE_IMG + RealmController.getUser().getProfileImage();
                        new PhotoFullPopupWindow(context, R.layout.popup_photo_full_two, v, url, null);
                    }
                });
            }
            binding.rating.setRating(RealmController.getUser().getRating());
            binding.tvRating.setText(Utility.roundOffFloatWithDecimal(RealmController.getUser().getRating(), 1) + "");
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getWalletAmount()) && !RealmController.getUser().getWalletAmount().equals("0")) {
                double amount = Double.parseDouble(RealmController.getUser().getWalletAmount());
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                formatter.applyPattern("#,###0.00");
                if (amount >= 0) {
                    binding.tvAmount.setText("$" + formatter.format(amount));
                    binding.tvPaynow.setVisibility(View.GONE);
                } else {
                    binding.tvAmount.setText("-$" + formatter.format(amount).replace("-",""));
                    binding.tvPaynow.setVisibility(View.VISIBLE);
                }
            } else {
                binding.tvAmount.setText("$0.00");
            }

        }
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        context = getActivity();
        loginModelRealmResults = RealmController.realmControllerInIt().getRealm().where(LoginModel.class).findAllAsync();
        loginModelRealmResults.addChangeListener(realmChangeListener);
        initView();

        return binding.getRoot();
    }

    private void initView() {

        binding.tvShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.tvPaynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PayNowActivity.class);
                startActivity(intent);
            }
        });
        binding.cvPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonalInformationActivity.class);
                startActivity(intent);
            }
        });
        binding.cvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        binding.cvPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CardListActivity.class);
                intent.putExtra(AppConstants.FLAG,"add");
                startActivity(intent);
            }
        });

        binding.cvBackAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BankAccountActivity.class);
                startActivity(intent);
            }
        });
        binding.cvMyBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyWalletActivity.class);
                startActivity(intent);
            }
        });

        binding.cvNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
        binding.cvFreques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HelpCenterActivity.class);
                startActivity(intent);
            }
        });
        binding.cvContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContactUsActivity.class);
                startActivity(intent);
            }
        });

        binding.cvAboutComar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AboutActivity.class);
                startActivity(intent);
            }
        });
        binding.cvTermAndCondi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TermAndConditionActivity.class);
                startActivity(intent);
            }
        });


        binding.cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutapi();
            }
        });
    }

    private void get_my_profile() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        Log.e(TAG, "get_my_profile: " + hashMap);

        RetrofitClient.getContentData(null, RetrofitClient.service(context).get_my_profile(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        LoginModel loginModel = Controller.getGson().fromJson(jsonObject.getJSONObject("data").toString(), LoginModel.class);
                        RealmController.copyToRealmOrUpdate(loginModel);
                    } else if (jsonObject.getInt("status") == 0) {
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(getActivity());
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

    @Override
    public void onResume() {
        super.onResume();
        get_my_profile();
    }

    private void logoutapi() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS_TOKEN, SessionManager.getAccessToken(context));
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId() + "");

        // Log.e(TAG, "logoutapi: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).logout(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt(JsonUtil.STATUS) == 1) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" + SessionManager.getCometId(context));
                        makeConversationList();
                        logoutCometChat();
                        Utility.logout(getActivity());
                    } else if (jsonObject.getInt(JsonUtil.STATUS) == 0) {
                        Toast.makeText(context, jsonObject.getString(JsonUtil.MESSAGE), Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getInt(JsonUtil.STATUS) == 4) {
                        Utility.logout(getActivity());
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

    private void logoutCometChat() {
        CometChat.logout(new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "onSuccess: comet " + s);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: " + e);

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

                    setSubcribe(conversations);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void setSubcribe(List<Conversation> conversations) {
        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.AppDetails.APP_ID + "_" + conversations.get(i).getConversationId());
                Log.e(TAG, "setSubcribe: " + AppConfig.AppDetails.APP_ID + "_" + conversations.get(i).getConversationId());
            }
        }

    }
}
