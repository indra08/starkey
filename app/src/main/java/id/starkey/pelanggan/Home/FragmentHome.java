package id.starkey.pelanggan.Home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.HomeMenuJasaLain.KategoriJasaLain;
import id.starkey.pelanggan.Kunci.TrxKunci.TrxKunciActivity;
import id.starkey.pelanggan.Login.LoginActivity;
import id.starkey.pelanggan.Maps.MapsActivity;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Stempel.TrxStempel.TrxStempelActivity;
import id.starkey.pelanggan.Utilities.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dani on 2/16/2018.
 */

public class FragmentHome extends Fragment implements View.OnClickListener, LocationListener {

    private ImageView btnJasaLain;

    public FragmentHome(){}

    private ImageView bKunci, bLaundry, bStempel;
    private View vMenu;
    private Context mContext;
    //private GPSTracker gps;
    double latPerm, lngPerm;
    private String tokennyaUser, sFirebaseToken;

    //interval
    //Handler h = new Handler();
    //int delay = 5*1000; //1 second=1000 milisecond, 5*1000=5seconds
    //Runnable runnable;

    //interval
    private Handler h = new Handler();
    private int delay = 5*1000; ////1 second=1000 milisecond, 5*1000=5seconds
    private Runnable runnable;

    // Location
    private double latitude, longitude;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private Location location;
    private final int REQUEST_PERMISSION_COARSE_LOCATION=2;
    private final int REQUEST_PERMISSION_FINE_LOCATION=3;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private boolean isUpdateLocation = false;
    private String TAG = "HOME";
    private boolean isLoading = false;

    public static boolean isActive = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vMenu = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getContext();

        isActive = true;

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            initLocationUtils();

            //Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
            /*gps = new GPSTracker(mContext, getActivity());

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                // \n is for new line
                //Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                latPerm = latitude;
                lngPerm = longitude;
                //Toast.makeText(getActivity(),"LOKASIMU" + latPerm + lngPerm, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }*/
        }

        //init btn
        bKunci = vMenu.findViewById(R.id.btnKunci);
        bLaundry = vMenu.findViewById(R.id.btnLaundry);
        bStempel = vMenu.findViewById(R.id.btnStempel);
        btnJasaLain = (ImageView) vMenu.findViewById(R.id.btnJasaLain);

        bKunci.setOnClickListener(this);
        bLaundry.setOnClickListener(this);
        bStempel.setOnClickListener(this);

        //get token user from pref
        getPref();
        getFirebaseToken();

        updatePosisiUser(sFirebaseToken);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable = this;
                h.postDelayed(runnable, delay);
                updatePosisiUser(sFirebaseToken);
            }
        }, delay);

        initUI();

        return vMenu;
    }

    private void initUI() {

        btnJasaLain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, KategoriJasaLain.class);
                String sLat = String.valueOf(latPerm);
                String sLng = String.valueOf(lngPerm);
                intent.putExtra("lat", sLat);
                intent.putExtra("lng", sLng);
                startActivity(intent);
            }
        });
    }

    private void getPref() {
        SharedPreferences custDetails = mContext.getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    private void getFirebaseToken(){
        //SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences tokenfirebaseuser = mContext.getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        sFirebaseToken = tokenfirebaseuser.getString("firebaseUser", "");
    }

    @Override
    public void onDestroy() {
        h.removeCallbacksAndMessages(null);
        isActive = false;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == bKunci){
            //Toast.makeText(getActivity(), "Kunci", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, MapsActivity.class);
            String sLat = String.valueOf(latPerm);
            String sLng = String.valueOf(lngPerm);
            intent.putExtra("parBtn","Kunci");
            intent.putExtra("lat", sLat);
            intent.putExtra("lng", sLng);
            startActivity(intent);
        } else if (view == bLaundry){
            Toast.makeText(mContext, "Laundry still under construction", Toast.LENGTH_SHORT).show();
            /*
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            String sLat = String.valueOf(latPerm);
            String sLng = String.valueOf(lngPerm);
            intent.putExtra("parBtn","Laundry");
            intent.putExtra("lat", sLat);
            intent.putExtra("lng", sLng);
            startActivity(intent);
             */

        } else {
            //Toast.makeText(getActivity(), "Stample still under construction", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(mContext, MapsActivity.class);
            String sLat = String.valueOf(latPerm);
            String sLng = String.valueOf(lngPerm);
            intent.putExtra("parBtn","Stempel");
            intent.putExtra("lat", sLat);
            intent.putExtra("lng", sLng);
            startActivity(intent);
        }
    }

    private void clearAttributeUser(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(ConfigLink.loginPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void updatePosisiUser(String fbaseToken){
        /*
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();
         */


        String latString = String.valueOf(latPerm);
        String lngString = String.valueOf(lngPerm);

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lat", latString);
        params.put("long", lngString);
        params.put("firebase_token", fbaseToken);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.PATCH, ConfigLink.update_location_user, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("posisiUser", response.toString());
                        //loading.dismiss();
                        try {
                            //Process os success response
                            String hasil = response.getString("message");
                            //Toast.makeText(mContext, hasil, Toast.LENGTH_SHORT).show();
                            if (hasil.equals("Akun tidak ditemukan")){
                                clearAttributeUser();
                                Intent login = new Intent(mContext, LoginActivity.class);
                                startActivity(login);
                                ((Activity)mContext).finish();
                            } else if(hasil.equals("Maaf, akun anda belum diaktivasi")){ //status 0
                                clearAttributeUser();
                                Toast.makeText(mContext, hasil, Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(mContext, LoginActivity.class);
                                startActivity(login);
                                ((Activity)mContext).finish();
                            } else if(hasil.equals("Maaf, user anda disuspend permanen")){ //status 2
                                clearAttributeUser();
                                Toast.makeText(mContext, hasil, Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(mContext, LoginActivity.class);
                                startActivity(login);
                                ((Activity)mContext).finish();
                            } else if(hasil.equals("Akun anda belum diaktivasi")){
                                clearAttributeUser();
                                infoDeviceNotMatch(hasil);
                            } else if(hasil.equals("Akun anda dibanned permanen")){
                                clearAttributeUser();
                                infoDeviceNotMatch(hasil);
                            } else if(hasil.equals("Server sedang dalam proses pemeliharaan")){
                                infoDeviceNotMatch(hasil);
                            } else if (hasil.equals("Token tidak valid, harap login ulang")){
                                infoDeviceNotMatch(hasil);
                                h.removeCallbacksAndMessages(null);
                            } else {
                                cekTrxOutstandingUser();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                error.printStackTrace();

                String message = null;
                if (error instanceof NetworkError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ServerError) {
                    message = "Server tidak ditemukan";
                } else if (error instanceof AuthFailureError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ParseError) {
                    message = "Parsing data Error";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut";
                }
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }
        };

        int socketTimeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(mContext).addToRequestQueue(request_json);
    }

    private void infoDeviceNotMatch(String titleMsg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(titleMsg)
                .setCancelable(false)
                .setPositiveButton("Oke",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                clearAttributeUser();
                                Intent aw = new Intent(mContext, LoginActivity.class);
                                startActivity(aw);
                                ((Activity)mContext).finish();
                                //turnGPSOn();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    /*
    private void infoDeviceNotMatch(String titleMsg){

    }
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    /*gps = new GPSTracker(mContext, getActivity());

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line
                        //Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }*/

                    initLocationUtils();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void cekTrxOutstandingUser(){
        /*
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();
         */


        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.transaksi_checker, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        Log.d("outUser", response.toString());
                        try {
                            JSONObject joData = response.getJSONObject("data");

                            if (joData.length() != 0){
                                String kondisiidlayanan = joData.getString("id_layanan");
                                if (kondisiidlayanan.equals("1")){// transaksi kunci

                                    Intent toTrxKunci = new Intent(mContext, TrxKunciActivity.class);
                                    toTrxKunci.putExtra("message", joData.toString());
                                    startActivity(toTrxKunci);
                                    ((Activity)mContext).finish();

                                } else if (kondisiidlayanan.equals("2")){//transaksi stempel

                                    Intent toTrxStempel = new Intent(mContext, TrxStempelActivity.class);
                                    toTrxStempel.putExtra("messageStempel", joData.toString());
                                    startActivity(toTrxStempel);
                                    ((Activity)mContext).finish();
                                }

                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                error.printStackTrace();

                String message = null;
                if (error instanceof NetworkError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ServerError) {
                    message = "Server tidak ditemukan";
                } else if (error instanceof AuthFailureError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ParseError) {
                    message = "Parsing data Error";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut";
                }
                Toast.makeText(mContext ,message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }
        };

        int socketTimeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(mContext).addToRequestQueue(request_json);
    }

    //region location

    private void initLocationUtils() {

        // getLocation update by google
        isLoading = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mSettingsClient = LocationServices.getSettingsClient(mContext);
        mRequestingLocationUpdates = false;

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        setCriteria();

        // Initial to semarang
        latitude = -7.0160395;
        longitude = 110.4630368;

        latPerm = latitude;
        lngPerm = longitude;
        location = new Location("set");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        //location = getLocation();
        updateAllLocation();
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                onLocationChanged(mCurrentLocation);
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener((AppCompatActivity) mContext, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.

        isUpdateLocation = true;
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((AppCompatActivity) mContext, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        isUpdateLocation = false;
                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener((AppCompatActivity)mContext, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location clocation) {

                                        mRequestingLocationUpdates = true;
                                        if (clocation != null) {

                                            onLocationChanged(clocation);
                                        }else{
                                            location = getLocation();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener((AppCompatActivity) mContext, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((AppCompatActivity)mContext, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                                //refreshMode = false;
                        }

                        //get Location
                        isUpdateLocation = false;
                        location = getLocation();
                    }
                });
    }

    private void updateAllLocation(){
        mRequestingLocationUpdates = true;
        startLocationUpdates();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHECK_SETTINGS){

            if(resultCode == Activity.RESULT_CANCELED){

                mRequestingLocationUpdates = false;
            }else if(resultCode == Activity.RESULT_OK){

                startLocationUpdates();
            }

        }
    }

    public Location getLocation() {

        isUpdateLocation = true;
        try {

            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
                Toast.makeText(mContext, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) mContext,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) mContext,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        }
                        isUpdateLocation = false;
                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");

                    if (locationManager != null) {

                        Location locationBuffer = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if(locationBuffer != null) location = locationBuffer;

                        if (location != null) {
                            //Changed(location);
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");

                    if (locationManager != null) {

                        Location locationBuffer = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if(locationBuffer != null) location = locationBuffer;

                        if (location != null) {
                            //onLocationChanged(location);
                        }
                    }
                }else{
                    //Toast.makeText(context, "Turn on your GPS for better accuracy", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        isUpdateLocation = false;
        if(location != null){
            onLocationChanged(location);
        }

        return location;
    }

    public void setCriteria() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        provider = locationManager.getBestProvider(criteria, true);
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions((AppCompatActivity) mContext,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onLocationChanged(Location clocation) {

        this.location = clocation;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        if(!isUpdateLocation/* && !editMode*/){
            //Log.d(TAG, "onLocationChanged: "+ String.valueOf(latitude) + " "+String.valueOf(longitude));
            latPerm = latitude;
            lngPerm = longitude;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

        //location = getLocation();
    }

    @Override
    public void onProviderEnabled(String s) {

        //location = getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {

    }
    //endregion
}
