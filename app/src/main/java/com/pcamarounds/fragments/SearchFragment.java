package com.pcamarounds.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.reflect.TypeToken;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.pcamarounds.R;
import com.pcamarounds.activities.GiveReviewActivity;
import com.pcamarounds.activities.LoginActivity;
import com.pcamarounds.activities.SearchDetailActivity;
import com.pcamarounds.activities.SearchDetailsCamaroundActivity;
import com.pcamarounds.activities.address.SearchLocationActivity;
import com.pcamarounds.activities.WebViewSignupActivity;
import com.pcamarounds.adapters.CategoriesAdapter;
import com.pcamarounds.adapters.CategoryAssignAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.FilterBottomSheetDialogBinding;
import com.pcamarounds.databinding.FragmentSearchBinding;
import com.pcamarounds.models.CategoryAssignModel;
import com.pcamarounds.models.SearchPostListModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.GPSTracker;
import com.pcamarounds.utils.InfiniteScroller;
import com.pcamarounds.utils.PersmissionUtils;
import com.pcamarounds.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private Context context;
    private FragmentSearchBinding binding;
    private CategoriesAdapter categoriesAdapter;
    private List<SearchPostListModel> categoriesModelList = new ArrayList<>();
    private List<CategoryAssignModel> categoryAssignModelList = new ArrayList<>();
    private Controller controller;
    private Dialog dialog;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private String isSearch = "", search_type = "current", jobType = "all", catAssignId = "";
    private int currentPage = 1;
    private BroadcastReceiver broadcastReceiver;
    private BottomSheetDialog bottomSheetDialog;
    private FilterBottomSheetDialogBinding sheetDialogBinding;
    private CategoryAssignAdapter categoryAssignAdapter;
    private int radius = 250;
    private PersmissionUtils persmissionUtils;
    private GPSTracker gps;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        context = getActivity();
        controller = (Controller) getActivity().getApplicationContext();
        Utility.hideKeyboardNew(((AppCompatActivity) getActivity()));
        persmissionUtils = new PersmissionUtils(context, SearchFragment.this);
        persmissionUtils.askPermissionAtStart();
        initView();
        return binding.getRoot();
    }

    private void initView() {
        showBottomSheetDialog();
        binding.etsearch.setText(controller.location);
        sheetDialogBinding.etsearch.setText(controller.location);
        setupRvCategory();

        binding.viewseach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchLocationActivity.class);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(AppConstants.KEY);
                if (message != null && message.equals(AppConstants.NOTIFICATION)) {
                    categoriesModelList.clear();
                    currentPage = 1;
                    post_list();
                }

            }
        };

        binding.rVCategories.setOnScrollListener(new InfiniteScroller() {
            @Override
            public void onLoadMore() {
                addDataToList();
            }
        });
        binding.ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (bottomSheetDialog != null) {
                        bottomSheetDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void addDataToList() {
        new Handler().postDelayed(() -> {
            currentPage++;
            post_list();
        }, 1000);
    }

    private void setupRvCategory() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rVCategories.setLayoutManager(linearLayoutManager1);
        // binding.rVCategories.addItemDecoration(new EqualSpacing(16,context));
        binding.rVCategories.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        categoriesAdapter = new CategoriesAdapter(context, categoriesModelList);
        binding.rVCategories.setAdapter(categoriesAdapter);
        categoriesAdapter.notifyDataSetChanged();
        categoriesAdapter.setOnItemClickListener(new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (RealmController.getUser() != null) {
                    if (categoriesModelList.get(position).getBidCount() > 0 && !categoriesModelList.get(position).getBidData().getBidStatus().equals("pending")) {
                        if (categoriesModelList.get(position).getBidData() != null
                                && Utility.isCheckEmptyOrNull(categoriesModelList.get(position).getBidData().getId())
                                && (categoriesModelList.get(position).getBidData().getBidStatus().equals("awarded")
                                || categoriesModelList.get(position).getBidData().getBidStatus().equals("approve")
                                || categoriesModelList.get(position).getBidData().getBidStatus().equals("cancelled")
                                || !categoriesModelList.get(position).getBidData().getBidStatus().equals("declined"))) {

                            if (categoriesModelList.get(position).getBookingStatus().equals("completed")) {

                                Intent intent = new Intent(context, GiveReviewActivity.class);
                                intent.putExtra(AppConstants.JOB_ID, categoriesModelList.get(position).getId());
                                intent.putExtra(AppConstants.USER_ID, categoriesModelList.get(position).getUserId());
                                intent.putExtra(AppConstants.SERVICE_NAME, categoriesModelList.get(position).getServiceName());
                                intent.putExtra(AppConstants.BID_STATUS, categoriesModelList.get(position).getBidData().getBidStatus());
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(context, SearchDetailsCamaroundActivity.class);
                                intent.putExtra(AppConstants.JOB_ID, categoriesModelList.get(position).getId());
                                intent.putExtra(AppConstants.USER_ID, categoriesModelList.get(position).getUserId());
                                intent.putExtra(AppConstants.SERVICE_NAME, categoriesModelList.get(position).getServiceName());
                                intent.putExtra(AppConstants.BID_STATUS, categoriesModelList.get(position).getBidData().getBidStatus());
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(context, SearchDetailActivity.class);
                            intent.putExtra(AppConstants.JOB_ID, categoriesModelList.get(position).getId());
                            intent.putExtra(AppConstants.USER_ID, categoriesModelList.get(position).getUserId());
                            intent.putExtra(AppConstants.SERVICE_NAME, categoriesModelList.get(position).getServiceName());
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(context, SearchDetailActivity.class);
                        intent.putExtra(AppConstants.JOB_ID, categoriesModelList.get(position).getId());
                        intent.putExtra(AppConstants.USER_ID, categoriesModelList.get(position).getUserId());
                        intent.putExtra(AppConstants.SERVICE_NAME, categoriesModelList.get(position).getServiceName());
                        startActivity(intent);
                    }
                } else {
                    loginSignupPopup();
                }

            }
        });

    }

    private void post_list() {
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
        hashMap.put(AppConstants.PAGE, currentPage + "");
        hashMap.put(AppConstants.SEARCH_TYPE, search_type);
        hashMap.put(AppConstants.ACTION, "all");
        hashMap.put(AppConstants.LOCATION, controller.location);
        hashMap.put(AppConstants.LATITUDE, controller.curlat + "");
        hashMap.put(AppConstants.LONGITUDE, controller.curlon + "");
        hashMap.put(AppConstants.CATEGORY_ID, catAssignId);
        hashMap.put(AppConstants.RADIOUS, radius + "");
        hashMap.put(AppConstants.JOB_STATUS, jobType);
        Dialog dialog = null;
        if (currentPage > 1) {
            dialog = null;
            binding.loadmoreProgress.setVisibility(View.VISIBLE);
            binding.llNoLising.setVisibility(View.GONE);
        }

        if (currentPage == 1) {
            dialog = new Dialog(context);
            binding.llNoLising.setVisibility(View.GONE);
        }
        Log.e(TAG, "post_list: " + hashMap);
        RetrofitClient.getContentData(dialog, RetrofitClient.service(context).post_list(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    // binding.swipeRefresh.setRefreshing(false);
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        if (currentPage == 1) {
                            categoriesModelList.clear();
                            categoriesModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                    new TypeToken<ArrayList<SearchPostListModel>>() {
                                    }.getType());

                            categoriesAdapter.setCategoriesModelList(categoriesModelList);
                            categoriesAdapter.notifyDataSetChanged();

                        } else if (currentPage > 1) {
                            List<SearchPostListModel> listItemModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                    new TypeToken<ArrayList<SearchPostListModel>>() {
                                    }.getType());
                            categoriesModelList.addAll(listItemModelList);
                            categoriesAdapter.notifyDataSetChanged();
                            binding.loadmoreProgress.setVisibility(View.GONE);
                        }

                    } else if (jsonObject.getInt("status") == 0) {
                        binding.loadmoreProgress.setVisibility(View.GONE);
                        if (currentPage == 1) {
                            categoriesModelList.clear();
                            categoriesAdapter.clearAdapter();
                        }
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(getActivity());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (categoriesModelList.size() > 0) {
                    binding.llNoLising.setVisibility(View.GONE);
                    binding.rVCategories.setVisibility(View.VISIBLE);
                } else {
                    binding.loadmoreProgress.setVisibility(View.GONE);
                    binding.rVCategories.setVisibility(View.GONE);
                    binding.llNoLising.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void category_assign() {
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

        Log.e(TAG, "category_assign : " + hashMap);

        RetrofitClient.getContentData(null, RetrofitClient.service(context).category_assign(Utility.getHeaderAuthentication(context), hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    // binding.swipeRefresh.setRefreshing(false);
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {

                        categoryAssignModelList.clear();
                        categoryAssignModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                new TypeToken<ArrayList<CategoryAssignModel>>() {
                                }.getType());

                        categoryAssignAdapter.setCategoryAssignModelList(categoryAssignModelList);
                        categoryAssignAdapter.notifyDataSetChanged();

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

    private void showBottomSheetDialog() {
        sheetDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.filter_bottom_sheet_dialog, null, false);
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(sheetDialogBinding.getRoot());


        sheetDialogBinding.viewseach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // isClick = "innerSearch";
                Intent intent = new Intent(context, SearchLocationActivity.class);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        sheetDialogBinding.recyclerViewCate.setLayoutManager(linearLayoutManager1);
        categoryAssignAdapter = new CategoryAssignAdapter(context, categoryAssignModelList);
        sheetDialogBinding.recyclerViewCate.setAdapter(categoryAssignAdapter);
        categoryAssignAdapter.notifyDataSetChanged();

        sheetDialogBinding.tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.hide();
            }
        });

        //price range seekbar
        sheetDialogBinding.seekbar.setIndicatorTextDecimalFormat("0");
        sheetDialogBinding.seekbar.setProgress(250, AppConstants.FILTER_MAX_PRICE);
        sheetDialogBinding.seekbar.setIndicatorText(radius + " km");
        sheetDialogBinding.seekbar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                radius = (int) leftValue;
                sheetDialogBinding.seekbar.setIndicatorText(radius + " km");
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
        sheetDialogBinding.btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobType = "all";
                sheetDialogBinding.btnAll.setTextColor(context.getResources().getColor(R.color.color_primary));
                sheetDialogBinding.btnAll.setBackground(context.getDrawable(R.drawable.bg_filter_selected));
                sheetDialogBinding.btnNoBid.setTextColor(context.getResources().getColor(R.color.colorGray));
                sheetDialogBinding.btnNoBid.setBackground(context.getDrawable(R.drawable.bg_filter_normal));
                sheetDialogBinding.btnBided.setTextColor(context.getResources().getColor(R.color.colorGray));
                sheetDialogBinding.btnBided.setBackground(context.getDrawable(R.drawable.bg_filter_normal));

            }
        });
        sheetDialogBinding.btnBided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobType = "bidded";
                sheetDialogBinding.btnAll.setTextColor(context.getResources().getColor(R.color.colorGray));
                sheetDialogBinding.btnAll.setBackground(context.getDrawable(R.drawable.bg_filter_normal));
                sheetDialogBinding.btnNoBid.setTextColor(context.getResources().getColor(R.color.colorGray));
                sheetDialogBinding.btnNoBid.setBackground(context.getDrawable(R.drawable.bg_filter_normal));
                sheetDialogBinding.btnBided.setTextColor(context.getResources().getColor(R.color.color_primary));
                sheetDialogBinding.btnBided.setBackground(context.getDrawable(R.drawable.bg_filter_selected));
            }
        });
        sheetDialogBinding.btnNoBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobType = "no_bid";
                sheetDialogBinding.btnAll.setTextColor(context.getResources().getColor(R.color.colorGray));
                sheetDialogBinding.btnAll.setBackground(context.getDrawable(R.drawable.bg_filter_normal));
                sheetDialogBinding.btnNoBid.setTextColor(context.getResources().getColor(R.color.color_primary));
                sheetDialogBinding.btnNoBid.setBackground(context.getDrawable(R.drawable.bg_filter_selected));
                sheetDialogBinding.btnBided.setTextColor(context.getResources().getColor(R.color.colorGray));
                sheetDialogBinding.btnBided.setBackground(context.getDrawable(R.drawable.bg_filter_normal));
            }
        });
        sheetDialogBinding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_type = "search";
                List listDay = categoryAssignAdapter.getSelectedItemlist();
                if (listDay.size() > 0) {
                    StringBuilder name = new StringBuilder();
                    for (int index = 0; index < categoryAssignAdapter.getSelectedItemlist().size(); index++) {
                        String namee = categoryAssignAdapter.getSelectedItemlist().get(index).getId();
                        for (int i = 0; i < AppConstants.daysShow.length; i++) {
                            if (namee.equals(AppConstants.daysShow[i])) {
                                namee = AppConstants.daysGet[i];
                            }
                        }
                        name.append(namee + ",");
                    }
                    catAssignId = name.substring(0, name.length() - ",".length());
                } else {
                    catAssignId = "";
                }
                Log.e(TAG, "onClick: " + jobType + "   " + catAssignId + "  " + radius);
                bottomSheetDialog.hide();
                if (Utility.isCheckEmptyOrNull(catAssignId)) {
                    binding.ivFilter.setImageTintList(context.getResources().getColorStateList(R.color.colorBlue));

                } else if (Utility.isCheckEmptyOrNull(jobType) && jobType.equals("bidded") || jobType.equals("no_bid")) {
                    binding.ivFilter.setImageTintList(context.getResources().getColorStateList(R.color.colorBlue));

                } else if (radius != 250) {
                    binding.ivFilter.setImageTintList(context.getResources().getColorStateList(R.color.colorBlue));

                } else {
                    if (persmissionUtils.checkLocationPermissions()) {
                        gps = new GPSTracker(context);
                        String aa = Utility.getAddressFromLatLong(context, gps.getLatitude(), gps.getLongitude());
                        if (Utility.isCheckEmptyOrNull(aa) && aa.equals(controller.location)) {
                            binding.ivFilter.setImageTintList(context.getResources().getColorStateList(R.color.colorWhite));

                        } else {
                            binding.ivFilter.setImageTintList(context.getResources().getColorStateList(R.color.colorBlue));

                        }

                    } else {
                        binding.ivFilter.setImageTintList(context.getResources().getColorStateList(R.color.colorWhite));
                    }

                }


                categoriesModelList.clear();
                currentPage = 1;
                post_list();

            }
        });
        sheetDialogBinding.tvreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialogBinding.seekbar.setProgress(250, AppConstants.FILTER_MAX_PRICE);
                sheetDialogBinding.seekbar.setIndicatorText(radius + " km");
                categoryAssignAdapter.setSelectedClear(catAssignId);
                catAssignId = "";
                radius = 250;
                jobType = "all";
                sheetDialogBinding.btnAll.setTextColor(context.getResources().getColor(R.color.color_primary));
                sheetDialogBinding.btnAll.setBackground(context.getDrawable(R.drawable.bg_filter_selected));
                sheetDialogBinding.btnNoBid.setTextColor(context.getResources().getColor(R.color.colorGray));
                sheetDialogBinding.btnNoBid.setBackground(context.getDrawable(R.drawable.bg_filter_normal));
                sheetDialogBinding.btnBided.setTextColor(context.getResources().getColor(R.color.colorGray));
                sheetDialogBinding.btnBided.setBackground(context.getDrawable(R.drawable.bg_filter_normal));
                if (persmissionUtils.checkLocationPermissions()) {
                    gps = new GPSTracker(context);
                    controller.curlat = gps.getLatitude();
                    controller.curlon = gps.getLongitude();
                    controller.location = Utility.getAddressFromLatLong(context, gps.getLatitude(), gps.getLongitude());
                    binding.etsearch.setText(controller.location);
                    sheetDialogBinding.etsearch.setText(controller.location);
                    // search_type = "current";
                }
                bottomSheetDialog.hide();
                search_type = "current";
                binding.ivFilter.setImageTintList(context.getResources().getColorStateList(R.color.colorWhite));
                categoriesModelList.clear();
                currentPage = 1;
                post_list();

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
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.BROADCAST));
        //categoriesModelList.clear();
        // currentPage = 1;
        post_list();
        category_assign();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (data != null) {
                String addd = data.getStringExtra("address");
                LatLng latLng = Utility.getLatLongFromAddress(context, addd);
                if (latLng != null) {
                    controller.curlat = latLng.latitude;
                    controller.curlon = latLng.longitude;

                }
                controller.location = addd;
                binding.etsearch.setText(addd);
                sheetDialogBinding.etsearch.setText(addd);
                currentPage = 1;
                search_type = "search";
                post_list();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bottomSheetDialog != null) {
            bottomSheetDialog.hide();
        }
    }
}
