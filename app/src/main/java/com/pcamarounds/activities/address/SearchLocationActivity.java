package com.pcamarounds.activities.address;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.pcamarounds.R;
import com.pcamarounds.adapters.PlaceAutoCompletetwoAdapter;
import com.pcamarounds.databinding.ActivitySearchLocationBinding;
import com.pcamarounds.models.PlaceAutoCompleteModel;
import com.pcamarounds.retrofit.APIClient;
import com.pcamarounds.retrofit.ApiInterface;
import com.pcamarounds.utils.PersmissionUtils;
import com.pcamarounds.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.facebook.appevents.AppEventsLogger;

public class SearchLocationActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppEventsLogger logger;

    private static final String TAG = "AddAddressActivity";
    private Context context;
    ActivitySearchLocationBinding binding;
    private PersmissionUtils persmissionUtils;
    private double curlat, curlon;

    //place search
    private static final int AUTOCOMPLETE_REQUEST_CODE = 23487;
    private PlacesClient placesClient;
    private List<Place.Field> fields;
    private String keyword = "";
    private String address = "", Add = "";
    private List<PlaceAutoCompleteModel> autoCompleteModelList;
    private PlaceAutoCompletetwoAdapter placeAutoCompletetwoAdapter;
    private boolean writing = false, selected = false;
    ApiInterface apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logger = AppEventsLogger.newLogger(this);

        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_location);
        context = this;
        Utility.hideKeyboardNew(SearchLocationActivity.this);
        persmissionUtils = new PersmissionUtils(context, SearchLocationActivity.this);
        persmissionUtils.askLocationPermissions();
        apiService = APIClient.getClient().create(ApiInterface.class);
        initView();
    }

    private void initView() {

        setUpToolbar();
        //placess code
        String apiKey = getString(R.string.google_maps_key);
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        placesClient = Places.createClient(context);
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        binding.etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    writing = true;
                    if (writing){
                        try {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    /*binding.etLocation.clearFocus();*/
                                    //getPredictionstwo(s.toString());
                                    queryPlaceSearch(s.toString());
                                }
                            }, 1000);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setupSearchADap();

        binding.etLocation.setSelectAllOnFocus(true);
        binding.etLocation.requestFocus();

        //todo-- keyboard collapse on ok click

        binding.etLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    Utility.hideKeyboard(SearchLocationActivity.this);

                }
                return false;
            }
        });

    }

    private void setupSearchADap() {
        autoCompleteModelList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        binding.searchplace.setLayoutManager(linearLayoutManager);
        placeAutoCompletetwoAdapter = new PlaceAutoCompletetwoAdapter(context, autoCompleteModelList);
        binding.searchplace.setAdapter(placeAutoCompletetwoAdapter);
        placeAutoCompletetwoAdapter.notifyDataSetChanged();

        placeAutoCompletetwoAdapter.setOnItemClickListener(new PlaceAutoCompletetwoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //String place = placeAutoCompletetwoAdapter.getPlaceAutoCompleteModel().get(position).getPlaceId();
                String place = placeAutoCompletetwoAdapter.getPlaceAutoCompleteModel().get(position).getPlaceText();
                //Log.e(TAG, "onItemClick: " + position +" "+ place );
                selected = true;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("address",place);
                setResult(1,returnIntent);
                finish();
               // getPlaceDetails(place);
                Utility.hideKeyboard(SearchLocationActivity.this);

            }
        });
    }

    private void queryPlaceSearch(String query){

        Map<String, String> data = new HashMap<>();
        data.put("input",query);
        data.put("key", APIClient.GOOGLE_PLACE_API_KEY);
        data.put("language","es");
        data.put("components","country:PA");

        Log.e(TAG, "queryPlaceSearch: "+data );

        Call<ResponseBody> call =apiService.doPlaces(data);
        System.out.print(call);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String resp = response.body().string().trim();
                    final ArrayList<PlaceAutoCompleteModel> resultList = new ArrayList<>();
                    //  Log.e(TAG, "onResponse: ============="+resp );
                    JSONObject jsonObject = new JSONObject(resp);
                    JSONArray predictionsArray = jsonObject.getJSONArray("predictions");

                    String status = jsonObject.getString("status");

                    if (status.equals("OK")){
                        for (int i = 0 ;i< predictionsArray.length();i++){
                            String placeId = predictionsArray.getJSONObject(i).getString("place_id");
                            String description = predictionsArray.getJSONObject(i).getString("description");
                            // String description = predictionsArray.getJSONObject(i).getJSONArray("terms").getJSONObject(0).getString("value");
                            resultList.add(new PlaceAutoCompleteModel(placeId,description));
                        }

                        if (selected) {
                            selected = false;
                        } else {
                            placeAutoCompletetwoAdapter.setPlaceAutoCompleteModelList(resultList);
                            placeAutoCompletetwoAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText("Búsqueda de dirección");
        binding.myToolbar.myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        binding.myToolbar.myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordSearchUsage();
                onBackPressed();
            }
        });
    }
    private void recordSearchUsage() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("search_usage", bundle);
        logger.logEvent("search_usage");

    }

}
