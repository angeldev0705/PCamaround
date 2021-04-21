package com.pcamarounds.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.reflect.TypeToken;
import com.pcamarounds.R;
import com.pcamarounds.activities.SearchDetailActivity;
import com.pcamarounds.activities.SearchDetailsCamaroundActivity;
import com.pcamarounds.adapters.MyCamaroundsAdapter;
import com.pcamarounds.adapters.StatusAdapter;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.databinding.FragmentCamaroundBinding;
import com.pcamarounds.databinding.ProjectFilterBottomsheetBinding;
import com.pcamarounds.models.MyCamaroundsModel;
import com.pcamarounds.models.StatusModel;
import com.pcamarounds.retrofit.JsonUtil;
import com.pcamarounds.retrofit.RetrofitClient;
import com.pcamarounds.retrofit.WebResponse;
import com.pcamarounds.utils.InfiniteScroller;
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
public class CamaroundFragment extends Fragment {
    private static final String TAG = "CamaroundFragment";
    private Context context;
    private FragmentCamaroundBinding binding;
    private MyCamaroundsAdapter myCamaroundsAdapter;
    private List<MyCamaroundsModel> myCamaroundsModelList = new ArrayList<>();
    private StatusAdapter statusAdapter;
    private List<StatusModel> statusModelList = new ArrayList<>();
    private String action = "all";
    private Controller controller;
    private BottomSheetDialog bottomSheetDialog;
    private ProjectFilterBottomsheetBinding sheetDialogBinding;
    private int currentPage = 1;
    private BroadcastReceiver broadcastReceiver;
    public CamaroundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camaround, container, false);
        context = getActivity();
        controller = (Controller) getActivity().getApplicationContext();
        initView();
        return binding.getRoot();
    }

    private void initView() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(AppConstants.KEY);
                if (message != null && message.equals(AppConstants.NOTIFICATION)) {
                    currentPage = 1;
                    myCamaroundsModelList.clear();
                    my_job_list();
                }
            }
        };

        setUpToolbar();
        setupRvStatus();
        showBottomSheetDialog();
        setupRvMyCamaround();

        binding.rVMyCamaround.setOnScrollListener(new InfiniteScroller() {
            @Override
            public void onLoadMore() {
                addDataToList();
            }
        });

    }

    private void addDataToList() {
        new Handler().postDelayed(() -> {
            currentPage++;
            my_job_list();
        }, 1000);
    }

    private void setupRvStatus() {
        statusModelList = new ArrayList<>();
        statusModelList.clear();
        statusModelList.add(new StatusModel("Todos"));
        statusModelList.add(new StatusModel("Adjudicada"));
        statusModelList.add(new StatusModel("Completada"));
        statusModelList.add(new StatusModel("Canceladas"));
        statusModelList.add(new StatusModel("En curso"));
        statusModelList.add(new StatusModel("En revisión"));
       // statusModelList.add(new StatusModel("Cerrada"));

    }

    private void setupRvMyCamaround() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        binding.rVMyCamaround.setLayoutManager(linearLayoutManager1);
        binding.rVMyCamaround.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
      //  binding.rVMyCamaround.addItemDecoration(new EqualSpacing(16, context));
        myCamaroundsAdapter = new MyCamaroundsAdapter(context, myCamaroundsModelList);
        binding.rVMyCamaround.setAdapter(myCamaroundsAdapter);

        myCamaroundsAdapter.setOnItemClickListener(new MyCamaroundsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (myCamaroundsModelList.get(position).getBidData() != null
                        && Utility.isCheckEmptyOrNull(myCamaroundsModelList.get(position).getBidData().getId())
                        && (myCamaroundsModelList.get(position).getBidData().getBidStatus().equals("awarded")
                        || myCamaroundsModelList.get(position).getBidData().getBidStatus().equals("inprogress")
                        || myCamaroundsModelList.get(position).getBidData().getBidStatus().equals("completed")
                        || myCamaroundsModelList.get(position).getBidData().getBidStatus().equals("cancelled")
                        || myCamaroundsModelList.get(position).getBidData().getBidStatus().equals("declined")
                        || myCamaroundsModelList.get(position).getBidData().getBidStatus().equals("dispute")
                        || myCamaroundsModelList.get(position).getBidData().getBidStatus().equals("incomplete")
                        || myCamaroundsModelList.get(position).getBidData().getBidStatus().equals("closed"))) {

                    Intent intent = new Intent(context, SearchDetailsCamaroundActivity.class);
                    intent.putExtra(AppConstants.JOB_ID, myCamaroundsModelList.get(position).getId());
                    intent.putExtra(AppConstants.USER_ID, myCamaroundsModelList.get(position).getUserId());
                    intent.putExtra(AppConstants.SERVICE_NAME, myCamaroundsModelList.get(position).getServiceName());
                    intent.putExtra(AppConstants.BID_STATUS, myCamaroundsModelList.get(position).getBidData().getBidStatus());
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(context, SearchDetailActivity.class);
                    intent.putExtra(AppConstants.JOB_ID, myCamaroundsModelList.get(position).getId());
                    intent.putExtra(AppConstants.USER_ID, myCamaroundsModelList.get(position).getUserId());
                    intent.putExtra(AppConstants.SERVICE_NAME, myCamaroundsModelList.get(position).getServiceName());
                    startActivity(intent);
                }

            }
        });
    }

    private void my_job_list() {
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
        hashMap.put(AppConstants.ACTION, action);
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
        Log.e(TAG, "my_job_list : " + hashMap);

        RetrofitClient.getContentData(dialog, RetrofitClient.service(context).my_job_list(Utility.getHeaderAuthentication(context),hashMap), new WebResponse() {
            @Override
            public void onResponseSuccess(Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = JsonUtil.mainjson(response);
                    if (jsonObject.getInt("status") == 1) {
                        if (currentPage == 1) {
                            myCamaroundsModelList.clear();
                            myCamaroundsModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                    new TypeToken<ArrayList<MyCamaroundsModel>>() {
                                    }.getType());

                            myCamaroundsAdapter.setMyCamaroundsModelList(myCamaroundsModelList);
                            myCamaroundsAdapter.notifyDataSetChanged();

                        } else if (currentPage > 1) {
                            List<MyCamaroundsModel> listItemModelList = Controller.getGson().fromJson(jsonObject.getJSONArray("data").toString(),
                                    new TypeToken<ArrayList<MyCamaroundsModel>>() {
                                    }.getType());
                            myCamaroundsModelList.addAll(listItemModelList);
                            myCamaroundsAdapter.notifyDataSetChanged();
                            binding.loadmoreProgress.setVisibility(View.GONE);
                        }

                    } else if (jsonObject.getInt("status") == 0) {
                        binding.loadmoreProgress.setVisibility(View.GONE);
                        if (currentPage == 1)
                        {
                            myCamaroundsModelList.clear();
                            myCamaroundsAdapter.clearAdapter();
                        }
                    } else if (jsonObject.getInt("status") == 4) { //Invalid Access Token
                        Utility.toast(context, jsonObject.getString("message"));
                        Utility.logout(getActivity());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (myCamaroundsModelList.size() > 0) {
                    binding.llNoLising.setVisibility(View.GONE);
                    binding.rVMyCamaround.setVisibility(View.VISIBLE);
                } else {
                    binding.rVMyCamaround.setVisibility(View.GONE);
                    binding.llNoLising.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onResponseFailed(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBottomSheetDialog() {
        sheetDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.project_filter_bottomsheet,null, false);
        bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(sheetDialogBinding.getRoot());

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        sheetDialogBinding.rvStatus.setLayoutManager(linearLayoutManager1);
        sheetDialogBinding.rvStatus.addItemDecoration(new DividerItemDecoration(sheetDialogBinding.rvStatus.getContext(), DividerItemDecoration.VERTICAL));
        //sheetDialogBinding.rvStatus.addItemDecoration(new EqualSpacing(10, context));
        statusAdapter = new StatusAdapter(context, statusModelList);
        sheetDialogBinding.rvStatus.setAdapter(statusAdapter);
        statusAdapter.setStatusModelList(statusModelList);

        sheetDialogBinding.ivcross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.hide();
            }
        });

/*
        statusAdapter.setOnItemClickListener(new StatusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                bottomSheetDialog.hide();
                String ss = statusModelList.get(statusAdapter.getCurrentPos()).getStatusName();
                switch (view.getId()) {
                    case R.id.ll:
                    case R.id.rb:
                        switch (ss) {
                            case "Todos":
                                action = "all";
                                currentPage = 1;
                                my_job_list();
                                break;
                            case "Adjudicada":
                                action = "awarded";
                                currentPage = 1;
                                my_job_list();
                                break;
                            case "En curso":
                                action = "inprogress";
                                currentPage = 1;
                                my_job_list();
                                break;
                            case "Completada":
                                action = "completed";
                                currentPage = 1;
                                my_job_list();
                                break;
                            case "En revisión":
                                action = "dispute";
                                currentPage = 1;
                                my_job_list();
                                break;
                            case "Canceladas":
                                action = "cancelled";
                                currentPage = 1;
                                my_job_list();
                                break;
                        }
                        break;
                }
            }
        });
*/

        sheetDialogBinding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.hide();
                String ss = statusModelList.get(statusAdapter.getCurrentPos()).getStatusName();
                switch (ss) {
                    case "Todos":
                        action = "all";
                        currentPage = 1;
                        my_job_list();
                        break;
                    case "Adjudicada":
                        action = "awarded";
                        currentPage = 1;
                        my_job_list();
                        break;
                    case "En curso":
                        action = "inprogress";
                        currentPage = 1;
                        my_job_list();
                        break;
                    case "Completada":
                        action = "completed";
                        currentPage = 1;
                        my_job_list();
                        break;
                    case "En revisión":
                        action = "dispute";
                        currentPage = 1;
                        my_job_list();
                        break;
                    case "Canceladas":
                        action = "cancelled";
                        currentPage = 1;
                        my_job_list();
                        break;
                }
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.BROADCAST));
        currentPage = 1;
        myCamaroundsModelList.clear();
        my_job_list();
    }
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }
    private void setUpToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.myToolbar.myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.my_camarounds));
        binding.myToolbar.ivFilter.setVisibility(View.VISIBLE);
        binding.myToolbar.ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

}
