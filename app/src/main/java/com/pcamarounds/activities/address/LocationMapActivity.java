package com.pcamarounds.activities.address;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.pcamarounds.R;
import com.pcamarounds.controller.Controller;
import com.pcamarounds.databinding.ActivityLocationMapBinding;
import com.pcamarounds.utils.GPSTracker;
import com.pcamarounds.utils.PersmissionUtils;
import com.pcamarounds.utils.Utility;


public class LocationMapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "LocationMapActivity";
    private Context context;
    private ActivityLocationMapBinding binding;
    private GPSTracker gps;
    private GoogleMap mMap;
    private PersmissionUtils persmissionUtils;
    private double curlat, curlon;
    private double lat, longi;
    private String country = "";
    private View mapView;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private String addressid = "", addresstype = "";
    private Controller controller;
    private String addd;
    private boolean isupdate = true;
    public LocationMapActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_map);
        context = this;
        controller = (Controller) getApplicationContext();
        persmissionUtils = new PersmissionUtils(context, LocationMapActivity.this);
        persmissionUtils.checkLocationPermissions();
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        setUpToolbar();
        binding.ivMyLocation.setOnClickListener(this);

        if (persmissionUtils.checkLocationPermissions()) {
            gps = new GPSTracker(context);
            curlat = gps.getLatitude();
            curlon = gps.getLongitude();
            Log.e(TAG, "initView: else inner if " + curlat + "  " + curlon);
        } else {
            Log.e(TAG, "onClick: elseee ");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        binding.tvFindAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchLocationActivity.class);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        binding.btnConfirmAndSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("address",addd);
                returnIntent.putExtra("From","LocationMapActivity");
                setResult(1,returnIntent);
                finish();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady: " + curlat + "   " + curlon);
        mMap = googleMap;

        LatLng sydney = new LatLng(curlat, curlon);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        updateMapCamera(curlat, curlon);

        mMap.setOnCameraChangeListener((CameraPosition cameraPosition) -> {
            if (isupdate) {
                LatLng latLng = cameraPosition.target;
                Log.e(TAG, "onCameraIdle: " + latLng);
                curlat = latLng.latitude;
                curlon = latLng.longitude;
                addd = Utility.getAddressFromLatLong(context, latLng.latitude, latLng.longitude);
                binding.tvFindAddress.setText(addd);

            }else {
                isupdate = true;
            }
        });

        if (mapView != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 0, 150);
        }
    }

    private void updateMapCamera(double lat, double lon) {
        LatLng latLng = new LatLng(lat, lon);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        if (mMap != null) {
            mMap.animateCamera(cameraUpdate);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        Location location = mMap.getMyLocation();
        if (location != null) {
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition position = mMap.getCameraPosition();
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.select_your_location));
        binding.myToolbar.myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        binding.myToolbar.myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (data != null) {
                isupdate = false;
                addd = data.getStringExtra("address");
                LatLng latLng = Utility.getLatLongFromAddress(context, addd);
                if (latLng != null) {
                    curlat = latLng.latitude;
                    curlon = latLng.longitude;

                }
                binding.tvFindAddress.setText(Utility.capitalize(addd));
                updateMapCamera(curlat, curlon);
            }
        }

    }
}
