package com.pcamarounds.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pcamarounds.R;
import com.pcamarounds.activities.LoginActivity;
import com.pcamarounds.activities.MainActivity;
import com.pcamarounds.activities.SearchDetailActivity;
import com.pcamarounds.activities.WebViewSignupActivity;
import com.pcamarounds.activities.address.LocationMapActivity;
import com.pcamarounds.adapters.AwardedComaroundAdapter;
import com.pcamarounds.adapters.LatestProjectAdapter;
import com.pcamarounds.constants.AppConfig;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.FragmentMenuBinding;
import com.pcamarounds.models.LoginModel;
import com.pcamarounds.models.dashboard.AwardedList;
import com.pcamarounds.models.dashboard.DashboardModel;
import com.pcamarounds.models.dashboard.InprogressList;
import com.pcamarounds.retrofit.Environment;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.EqualSpacing;
import com.pcamarounds.utils.GPSTracker;
import com.pcamarounds.utils.PersmissionUtils;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
public class MenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";
    private Context context;
    private FragmentMenuBinding binding;
    private AwardedComaroundAdapter awardedComaroundAdapter;
    private List<AwardedList> awardedComaroundModelList = new ArrayList<>();
    private LatestProjectAdapter latestProjectAdapter;
    private List<InprogressList> latestProjectModelList = new ArrayList<>();
    private RealmResults<LoginModel> loginModelRealmResults;
    private Controller controller;
    private Dialog dialog;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private double lat, longi;
    private PersmissionUtils persmissionUtils;
    private GPSTracker gps;
    private String from = "";
    private ConversationsRequest conversationsRequest;
    //private List<PendingReviewModel> pendingReviewModels = new ArrayList<>();
    private RealmChangeListener<RealmResults<LoginModel>> realmChangeListener = (LoginModel) -> {
        setDataProfile();
    };

    private void setDataProfile() {
        if (RealmController.getUser() != null) {
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getAddress())) {
                binding.llAddress.setVisibility(View.GONE);
            } else {
                binding.llNoLising.setVisibility(View.GONE);
                binding.llAddress.setVisibility(View.VISIBLE);
            }
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getLastName())) {
                binding.tvName.setText("Hola, " + Utility.capitalize(RealmController.getUser().getFirstName() + " " + Utility.printInitials(RealmController.getUser().getLastName())));
            } else {
                binding.tvName.setText("Hola, " + Utility.capitalize(RealmController.getUser().getFirstName()));
            }
            FirebaseMessaging.getInstance().subscribeToTopic(Environment.getTopicId() + RealmController.getUser().getId() + "");
            // tech_dashboard();
        } else {
            binding.tvName.setText("Bienvenido");
        }
    }

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false);
        context = getActivity();
        Utility.hideKeyboardNew((AppCompatActivity) getActivity());
        controller = (Controller) getActivity().getApplicationContext();
        loginModelRealmResults = RealmController.realmControllerInIt().getRealm().where(LoginModel.class).findAllAsync();
        loginModelRealmResults.addChangeListener(realmChangeListener);
        persmissionUtils = new PersmissionUtils(context, MenuFragment.this);
        persmissionUtils.askPermissionAtStart();
        initView();

        return binding.getRoot();
    }

    private void initView() {
        // tech_reviews_pending();
        Intent intent2 = new Intent(AppConstants.BROADCAST);
        intent2.putExtra(AppConstants.KEY, AppConstants.NOTIFICATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);

        Intent intent3 = new Intent(AppConstants.BROADCAST);
        intent3.putExtra(AppConstants.UNREADCOUNT, AppConstants.FLAG);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent3);

        if (RealmController.getUser() != null) {
            makeConversationList();
        }
        if (persmissionUtils.checkLocationPermissions()) {
            gps = new GPSTracker(context);
            controller.curlat = gps.getLatitude();
            controller.curlon = gps.getLongitude();
            controller.location = Utility.getAddressFromLatLong(context, gps.getLatitude(), gps.getLongitude());

        }
        setupRvAwarded();
        setupRvLatestProject();
      /*  if (RealmController.getUser() != null) {
            binding.llAddress.setVisibility(View.GONE);
            binding.etAddress.setText(RealmController.getUser().getAddress());
            binding.etRadius.setText("" + RealmController.getUser().getRadious());
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getLatitude())) {
                lat = Double.parseDouble(RealmController.getUser().getLatitude());
            }
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getLongitude())) {
                longi = Double.parseDouble(RealmController.getUser().getLongitude());
            }
            tech_dashboard();
        } else {
            binding.llAddress.setVisibility(View.GONE);
        }*/
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RealmController.getUser() != null) {
                    if (!binding.etAddress.getText().toString().trim().equals("")) {
                        if (!binding.etRadius.getText().toString().trim().equals("")) {
                            tech_address();
                        } else {
                            Utility.toast(context, "Please enter radius!");
                        }
                    } else {
                        Utility.toast(context, "Seleccione una dirección");
                    }
                } else {
                    loginSignupPopup();
                }
            }
        });

        binding.etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationMapActivity.class);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        binding.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(AppConstants.FRAGMENTFROM, "MenuFragment");
                startActivity(intent);
                getActivity().finishAffinity();

            }
        });
        binding.ivInfoSettle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.infoPopup(context, "Tu siguiente pago");
            }
        });
        binding.ivInfoCamarounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.infoPopup(context, "Número de trabajos en curso");
            }
        });

        binding.ivInfoIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.infoPopup(context, "Ganancias totales");
            }
        });

        binding.ivInfoPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.infoPopup(context, "Valor de los Camarounds en curso");
            }
        });

    }

    private void setupRvAwarded() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rVAwarded.setLayoutManager(linearLayoutManager1);
        binding.rVAwarded.addItemDecoration(new EqualSpacing(16, context));
        awardedComaroundAdapter = new AwardedComaroundAdapter(context, awardedComaroundModelList);
        binding.rVAwarded.setAdapter(awardedComaroundAdapter);


    }

    private void setupRvLatestProject() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rVLatestProject.setLayoutManager(linearLayoutManager1);
        binding.rVLatestProject.addItemDecoration(new EqualSpacing(16, context));
        latestProjectAdapter = new LatestProjectAdapter(context, latestProjectModelList);
        binding.rVLatestProject.setAdapter(latestProjectAdapter);

        latestProjectAdapter.setOnItemClickListener(new LatestProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, SearchDetailActivity.class);
                intent.putExtra(AppConstants.JOB_ID, latestProjectModelList.get(position).getJobId());
                intent.putExtra(AppConstants.USER_ID, latestProjectModelList.get(position).getTechId());
                intent.putExtra(AppConstants.SERVICE_NAME, latestProjectModelList.get(position).getBidData());
                startActivity(intent);
            }
        });

    }

    private void tech_dashboard() {
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

        Log.e(TAG, "tech_dashboard: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).tech_dashboard(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    DashboardModel dashboardModel = Controller.getGson().fromJson(JsonUtil.mainjson(response).toString(), DashboardModel.class);
                    if (dashboardModel.getStatus() == 1) {

                        setDashboardData(dashboardModel);

                    } else if (dashboardModel.getStatus() == 0) {
                    } else if (dashboardModel.getStatus() == 4) { //Invalid Access Token
                        Utility.toast(context, dashboardModel.getMessage());
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

    private void setDashboardData(DashboardModel dashboardModel) {
        if (Utility.isCheckEmptyOrNull(dashboardModel.getMybalance())) {
            double vc = Double.parseDouble(dashboardModel.getMybalance());
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            formatter.applyPattern("#,###0.00");
            if (vc >= 0) {
                binding.tvTotalEarnings.setText("$" + formatter.format(vc));
            } else {
                binding.tvTotalEarnings.setText("-$" + formatter.format(vc).replace("-", ""));
            }

        }
        if (Utility.isCheckEmptyOrNull(dashboardModel.getPendingamt())) {
            double vc = Double.parseDouble(dashboardModel.getPendingamt());
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            formatter.applyPattern("#,###0.00");
            if (vc >= 0) {
                binding.tvIncome.setText("$" + formatter.format(vc));
            } else {
                binding.tvIncome.setText("-$" + formatter.format(vc).replace("-", ""));
            }
        }
        if (Utility.isCheckEmptyOrNull(dashboardModel.getMyinprogress())) {
            double vc = Double.parseDouble(dashboardModel.getMyinprogress());
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            formatter.applyPattern("#,###0.00");
            if (vc >= 0) {
                binding.tvPending.setText("$" + formatter.format(vc));
            } else {
                binding.tvPending.setText("-$" + formatter.format(vc).replace("-", ""));
            }
        }
        if (Utility.isCheckEmptyOrNull(dashboardModel.getPendingjobs())) {
            binding.tvCamarounds.setText("" + dashboardModel.getPendingjobs());
        } else {
            binding.tvCamarounds.setText("0");
        }


        if (dashboardModel.getAwardedList().size() > 0) {
            binding.rVAwarded.setVisibility(View.VISIBLE);
            binding.tvAwarded.setVisibility(View.VISIBLE);
            awardedComaroundModelList.clear();
            awardedComaroundModelList = dashboardModel.getAwardedList();
            awardedComaroundAdapter.setAwardedComaroundModelList(awardedComaroundModelList);
            awardedComaroundAdapter.notifyDataSetChanged();
            binding.llAddress.setVisibility(View.GONE);
        } else {
            binding.rVAwarded.setVisibility(View.GONE);
            binding.tvAwarded.setVisibility(View.GONE);

        }
        if (dashboardModel.getInprogressList().size() > 0) {
            binding.rVLatestProject.setVisibility(View.VISIBLE);
            binding.tvMore.setVisibility(View.VISIBLE);
            binding.llNoLising.setVisibility(View.GONE);
            latestProjectModelList.clear();
            latestProjectModelList = dashboardModel.getInprogressList();
            latestProjectAdapter.setLatestProjectModelList(latestProjectModelList);
            latestProjectAdapter.notifyDataSetChanged();
            binding.llAddress.setVisibility(View.GONE);
        } else {
            binding.rVLatestProject.setVisibility(View.GONE);
            binding.tvMore.setVisibility(View.GONE);
            binding.rlLatest.setVisibility(View.GONE);
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getAddress())) {
                binding.llNoLising.setVisibility(View.GONE);
                // binding.llNoLising.setVisibility(View.VISIBLE);
            } else {
                binding.llNoLising.setVisibility(View.GONE);
            }
        }

        if (dashboardModel.getAwardedList().size() == 0 || dashboardModel.getInprogressList().size() == 0) {
            if (Utility.isCheckEmptyOrNull(RealmController.getUser().getAddress())) {
                binding.llAddress.setVisibility(View.GONE);
            } else {
                binding.llAddress.setVisibility(View.VISIBLE);
            }
        } else {
            binding.llAddress.setVisibility(View.GONE);

        }

    }

    private void tech_address() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.DEVICE_ID, Utility.getDeviceId(context));
        hashMap.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
        hashMap.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
        hashMap.put(AppConstants.ACCESS, AppConstants.TECH);
        hashMap.put(AppConstants.UID, RealmController.getUser().getId());
        hashMap.put(AppConstants.LATITUDE, lat + "");
        hashMap.put(AppConstants.LONGITUDE, longi + "");
        hashMap.put(AppConstants.LOCATION, binding.etAddress.getText().toString().trim());
        hashMap.put(AppConstants.RADIOUS, binding.etRadius.getText().toString().trim());
        Log.e(TAG, "tech_address: " + hashMap);

        RetrofitClient.getContentData(new Dialog(context), RetrofitClient.service(context).tech_address(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        get_my_profile();
                    } else if (jsonObject.getInt("status") == 0) {
                        Utility.toast(context, jsonObject.getString("message"));
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

    private void loginSignupPopup() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.login_signuo_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, WebViewSignupActivity.class);
                intent.putExtra(AppConstants.FLAGLOGIN, AppConstants.GUEST);
                startActivity(intent);
                // finish();
            }
        });
        dialog.findViewById(R.id.btnAccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra(AppConstants.FLAGLOGIN, AppConstants.GUEST);
                startActivity(intent);
                // finish();

            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (data != null) {
                String addd = data.getStringExtra("address");
                from = data.getStringExtra("From");
                LatLng latLng = Utility.getLatLongFromAddress(context, addd);
                if (latLng != null) {
                    lat = latLng.latitude;
                    longi = latLng.longitude;

                }
                binding.etAddress.setText(Utility.capitalize(addd));

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        onRequestPermissionsRes(requestCode, permissions, grantResults);
    }

    public void onRequestPermissionsRes(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (permissions.length == 0) {
            return;
        }
        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }
        if (!allPermissionsGranted) {
            boolean somePermissionsForeverDenied = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                    //denied
                    Log.e("denied", permission);
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                } else {
                    if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
                        Log.e("allowed", permission);
                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission);
                        somePermissionsForeverDenied = true;
                    }
                }
            }
            if (somePermissionsForeverDenied) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage(context.getString(R.string.permission_message))

                        .setPositiveButton(context.getString(R.string.settings), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        } else {
            switch (requestCode) {
                case PersmissionUtils.REQUEST_LOCATION_PERMISSION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

    }

    private void makeConversationList() {

        if (conversationsRequest == null) {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(50).build();
        }
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations.size() != 0) {
                    Intent intent2 = new Intent(AppConstants.BROADCAST);
                    intent2.putExtra(AppConstants.UNREADCOUNT, AppConstants.FLAG);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
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
                FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.AppDetails.APP_ID + "_" + conversations.get(i).getConversationId());
                Log.e(TAG, "setSubcribe: " + AppConfig.AppDetails.APP_ID + "_" + conversations.get(i).getConversationId());
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent2 = new Intent(AppConstants.BROADCAST);
        intent2.putExtra(AppConstants.UNREADCOUNT, AppConstants.FLAG);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
        if (!Utility.isCheckEmptyOrNull(from)) {
            if (RealmController.getUser() != null) {
                binding.llAddress.setVisibility(View.GONE);
                binding.etAddress.setText(RealmController.getUser().getAddress());
                binding.etRadius.setText("" + RealmController.getUser().getRadious());
                if (Utility.isCheckEmptyOrNull(RealmController.getUser().getLatitude())) {
                    lat = Double.parseDouble(RealmController.getUser().getLatitude());
                }
                if (Utility.isCheckEmptyOrNull(RealmController.getUser().getLongitude())) {
                    longi = Double.parseDouble(RealmController.getUser().getLongitude());
                }
                tech_dashboard();
            } else {
                binding.llAddress.setVisibility(View.GONE);
            }
        }else {
            from = "";
        }
    }
}
