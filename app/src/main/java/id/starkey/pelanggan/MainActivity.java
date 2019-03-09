package id.starkey.pelanggan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
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
import com.android.volley.toolbox.Volley;
import id.starkey.pelanggan.Bantuan.BantuanActivity;
import id.starkey.pelanggan.Daftar.InputFotoActivity;
import id.starkey.pelanggan.EditFotoProfile.EditFotoActivity;
import id.starkey.pelanggan.History.HistoryActivity;
import id.starkey.pelanggan.Home.FragmentHome;
import id.starkey.pelanggan.HomeMenuJasaLain.TransaksiJasaLain;
import id.starkey.pelanggan.Hubungi.HubungiActivity;
import id.starkey.pelanggan.InfoTerbaru.InfoTerbaruActivity;
import id.starkey.pelanggan.Kunci.ReviewKunci.ReviewKunciActivity;
import id.starkey.pelanggan.Kunci.TrxKunci.TrxKunciActivity;
import id.starkey.pelanggan.Kunci.WaitingKunci.WaitingKunciActivity;
import id.starkey.pelanggan.Login.LoginActivity;
import id.starkey.pelanggan.Maps.MapsActivity;
import id.starkey.pelanggan.OMCustomerService.OMCustomerService;
import id.starkey.pelanggan.Pengaturan.PengaturanActivity;
import id.starkey.pelanggan.Stempel.ReviewStempel.ReviewStempelActivity;
import id.starkey.pelanggan.Stempel.TrxStempel.TrxStempelActivity;
import id.starkey.pelanggan.Stempel.WaitingStempel.WaitingStempelActivity;
import id.starkey.pelanggan.SyaratdanK.SyaratdanActivity;
import id.starkey.pelanggan.Utilities.GPSTracker;
import id.starkey.pelanggan.Utilities.ItemValidation;
import id.starkey.pelanggan.Utilities.LastOrder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static String TAG_MENU;
    private NavigationView navigationView;
    Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);

    private Context mContext;
    //private GPSTracker gps;
    private ItemValidation iv = new ItemValidation();

    //google gps
    protected static final String TAG = "LocationOnOff";

    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    final static int CANCEL_REQUEST_LOCATION = 188;

    private TextView headerName, headerEmail, headerPhone;
    private String tampungUrlImageUser, tokennyaUser;
    private CircularImageView headerImageView;
    JSONObject jsonBody;
    private boolean dialogActive = false;
    public static int stateOrder = 0;
    private String version = "", latestVersion = "", link = "";
    private AlertDialog builderVersion;
    private boolean updateRequired = false;
    public static boolean isBatalByMitra = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));

        mContext = this;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            //Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
            /*gps = new GPSTracker(mContext, MainActivity.this);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                // \n is for new line
                // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }*/
        }

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
               //         .setAction("Action", null).show();

                //Intent in = new Intent(MainActivity.this, MapsActivity.class);
                //startActivity(in);

            }
        });
         */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //tv versioning
        String versionName = BuildConfig.VERSION_NAME;
        TextView tvVersion = navigationView.findViewById(R.id.lblVersionMain);
        tvVersion.setText("Versi "+versionName);

        //backlight selected item nav bar no color
        navigationView.setItemBackground(transparentDrawable);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };
        int[] colors = new int[] {
                Color.BLACK,
                Color.RED,
                Color.GREEN,
                Color.BLUE
        };
        ColorStateList myList = new ColorStateList(states, colors);
        navigationView.setItemTextColor(myList);
        navigationView.setItemIconTintList(myList);


        //click navdraw header
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

                Intent foto = new Intent(MainActivity.this, EditFotoActivity.class);
                startActivity(foto);
            }
        });

        displaySelectedScreen(R.id.nav_home);

        //init tv
        headerName = headerview.findViewById(R.id.namaHeader);
        headerEmail = headerview.findViewById(R.id.emailHeader);
        headerPhone = headerview.findViewById(R.id.phoneHeader);

        //get detail from preference
        getPref();
        //get url image from pref
        //getPrefPicture();
        //getFotoProfil();

        //init circular image view in header
        headerImageView = headerview.findViewById(R.id.gambarHeader);
        //headerImageView.setBackgroundResource(R.drawable.ic_add_large);
        Picasso.with(mContext)
                .load(tampungUrlImageUser)
                .placeholder(R.drawable.progress_animation)
                .into(headerImageView);



        //checkGPS();

        //new check gps google
        /*final LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
            //Toast.makeText(MainActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            //finish();
        }

        // Todo Location Already on  ... end

        if(!hasGPSDevice(MainActivity.this)){
            Toast.makeText(MainActivity.this,"Gps not Supported",Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
            Log.e("keshav","Gps already enabled");
            //Toast.makeText(MainActivity.this,"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            Log.e("keshav","Gps already enabled");
            //Toast.makeText(MainActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
        }*/

        dialogActive = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getFotoProfil();
        getPrefPicture();
    }

    private void getFotoProfil() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,ConfigLink.getFotoProfil, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("getfoto", response.toString());
                        //tampungUrlImageUser
                        try {
                            JSONObject joData = response.getJSONObject("data");
                            String image = joData.getString("profile_photo");
                            Log.d("urlfoto", image);

                            //save url image to pref
                            replaceUrlImagePref(image);
                        } catch (JSONException ex){
                            ex.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //Log.e("TAG", error.getMessage(), error);
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }

        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(jsonObjectRequest);
    }

    private void replaceUrlImagePref(String urlimgnew){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fotoprofilUser", urlimgnew);
        editor.commit();
    }

    private void getPrefPicture(){
        SharedPreferences imageUser = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        String urlImageUser = imageUser.getString("fotoprofilUser","");
        Log.d("urlImageUser", urlImageUser);

        tampungUrlImageUser = urlImageUser;
    }

    private void getPref(){
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        String nm = custDetails.getString("namaUser", "");
        String nmblkg = custDetails.getString("namablkgUser", "");
        String email = custDetails.getString("emailUser", "");
        String hp = custDetails.getString("phoneUser", "");

        headerName.setText(nm+" "+nmblkg);
        headerEmail.setText(email);
        headerPhone.setText(hp);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
        tampungUrlImageUser = custDetails.getString("fotoprofilUser", "");
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000 / 2);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                                //finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        //checkGPS();
        locationAccessChecker();
        checkVersion();

        if(isBatalByMitra){
            isBatalByMitra = false;
            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setTitle("Pesanan Dibatalkan")
                    .setMessage("Mitra telah membatalkan pesanan anda, silahkan batalkan order atau order kembali.")
                    .setCancelable(false)
                    .setPositiveButton("Batalkan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("Lanjutkan Order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            lanjutCariMitra();
                        }
                    })
                    .show();
        }
    }

    private void lanjutCariMitra() {

        if(stateOrder == 1){

            try {

                LastOrder lastOrder = ReviewKunciActivity.lastOrder;
                Intent intent = new Intent( MainActivity.this, WaitingKunciActivity.class);
                intent.putExtra("idlayanankunci", lastOrder.getIdLayanan());
                intent.putExtra("idkunci", lastOrder.getIdKunci());
                intent.putExtra("latitude", lastOrder.getLatitude());
                intent.putExtra("longitude", lastOrder.getLongitude());
                intent.putExtra("alamatLengkap", lastOrder.getAlamat());
                intent.putExtra("qty", lastOrder.getQty());
                intent.putExtra("keterangan", lastOrder.getKeterangan());
                intent.putExtra("biayaEstimasiS", lastOrder.getTotal());
                intent.putExtra("biayaEstimasi", lastOrder.getTotalEstimasi());
                intent.putExtra("gambarByUser", lastOrder.getGambar());
                stateOrder = 1;
                startActivity(intent);
                //finish();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(stateOrder == 2){

            try {

                LastOrder lastOrder = ReviewStempelActivity.lastOrder;
                Intent toOrderStempel = new Intent(MainActivity.this, WaitingStempelActivity.class);
                toOrderStempel.putExtra("namastempel", lastOrder.getNamaStemp());
                toOrderStempel.putExtra("ukuran", lastOrder.getUkuranStemp());
                toOrderStempel.putExtra("latUser", lastOrder.getLatitude());
                toOrderStempel.putExtra("lngUser", lastOrder.getLongitude());
                toOrderStempel.putExtra("namalokasi", lastOrder.getAlamat());
                toOrderStempel.putExtra("qty", lastOrder.getQty());
                toOrderStempel.putExtra("keterangan", lastOrder.getKeterangan());
                toOrderStempel.putExtra("waktuawal", lastOrder.getWaktuAwal());
                toOrderStempel.putExtra("biayaestimasi", lastOrder.getTotal());
                stateOrder = 2;

                startActivity(toOrderStempel);
                //finish();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void locationAccessChecker() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void checkVersion(){

        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = pInfo.versionName;
        //getSupportActionBar().setSubtitle(getResources().getString(R.string.app_name) + " v "+ version);
        //tvVersion.setText(getResources().getString(R.string.app_name) + " v "+ version);
        latestVersion = "";
        link = "";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("flag_app", "user");

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST ,ConfigLink.getVersion, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if(builderVersion != null){
                            if(builderVersion.isShowing()) builderVersion.dismiss();
                        }

                        try {

                            String status = response.getString("status");
                            if(status.equals("success")){

                                JSONArray ja = response.getJSONArray("data");
                                if(ja.length() > 0){

                                    JSONObject jo = ja.getJSONObject(0);
                                    latestVersion = jo.getString("versi");
                                    link = jo.getString("link");
                                    updateRequired = (iv.parseNullInteger(jo.getString("status")) == 1) ? true : false;
                                    if(!version.trim().equals(latestVersion.trim()) && link.length() > 0){

                                        if(updateRequired){

                                            builderVersion = new AlertDialog.Builder(mContext)
                                                    .setIcon(R.mipmap.ic_starkey_customer_curved)
                                                    .setTitle("Update")
                                                    .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon update ke versi terbaru.")
                                                    .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                            startActivity(browserIntent);
                                                        }
                                                    })
                                                    .setCancelable(false)
                                                    .show();
                                        }else{

                                            builderVersion = new AlertDialog.Builder(mContext)
                                                    .setIcon(R.mipmap.ic_starkey_customer_curved)
                                                    .setTitle("Update")
                                                    .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon update ke versi terbaru.")
                                                    .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                            startActivity(browserIntent);
                                                        }
                                                    })
                                                    .setNegativeButton("Update Nanti", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            //dialogInterface.dismiss();
                                                        }
                                                    }).show();
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //VolleyLog.e("Err Volley: ", error.getMessage());
                //error.printStackTrace();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        });

        int socketTimeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void buildAlertMessageNoGps() {
        if(!dialogActive){
            dialogActive = true;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mohon Hidupkan Akses Lokasi (GPS) Anda.")
                    .setCancelable(false)
                    .setPositiveButton("Hidupkan", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialogActive = false;
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                //Toast.makeText(mContext, "user Oke location", Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == Activity.RESULT_CANCELED){
            //Toast.makeText(mContext, "Anda harus mengaktifkan GPS", Toast.LENGTH_SHORT).show();
            //finish();
        }
    }

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

                    /*gps = new GPSTracker(mContext, MainActivity.this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line
                        //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }*/

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void checkGPS(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    private void askLogout(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                clearAttributeUser();
                                clearIdLayanan();
                                Intent aw = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(aw);
                                finish();
                                //turnGPSOn();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Batal",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void clearAttributeUser(){
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigLink.loginPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void clearIdLayanan(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.idLayananPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Untuk melanjutkan silahkan aktifkan GPS")
                .setCancelable(false)
                .setPositiveButton("Aktifkan",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                                //turnGPSOn();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Batal",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce){
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_cs) {
            Intent intent = new Intent(mContext, OMCustomerService.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);

        return true;
    }

    private void displaySelectedScreen(int id){
        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (id){
            case R.id.nav_home:
                TAG_MENU = "TAG_HOME";
                fragment = new FragmentHome();
                break;
            case R.id.nav_history:
                TAG_MENU = "TAG_HISTORY";
                //Toast.makeText(this, "History", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                //fragment = new category();
                //Intent i = new Intent(this, CategoryAllActivity.class);
                //startActivity(i);
                break;
            case R.id.nav_transaksi_jl:
                TAG_MENU = "TAG_HISTORY";
                //Toast.makeText(this, "History", Toast.LENGTH_SHORT).show();
                Intent intentJL = new Intent(MainActivity.this, TransaksiJasaLain.class);
                startActivity(intentJL);
                //fragment = new category();
                //Intent i = new Intent(this, CategoryAllActivity.class);
                //startActivity(i);
                break;
            case R.id.nav_news:
                TAG_MENU = "TAG_NEWS";
                Intent intentNews = new Intent(MainActivity.this, InfoTerbaruActivity.class);
                startActivity(intentNews);
                break;
            case R.id.nav_bantuan:
                TAG_MENU = "TAG_BANTUAN";
                Intent intenBan = new Intent(MainActivity.this, BantuanActivity.class);
                startActivity(intenBan);
                //Toast.makeText(this, "Bantuan", Toast.LENGTH_SHORT).show();
                //fragment = new help();
                break;

            case R.id.nav_syaratdank:
                TAG_MENU = "TAG_SYARAT";
                Intent intenSyarat = new Intent(MainActivity.this, SyaratdanActivity.class);
                startActivity(intenSyarat);
                //Toast.makeText(this, "Syarat dan K", Toast.LENGTH_SHORT).show();
                //fragment = new help();
                break;

            case R.id.nav_pengaturan:
                TAG_MENU = "TAG_PENGATURAN";
                Intent intenPenga = new Intent(MainActivity.this, PengaturanActivity.class);
                startActivity(intenPenga);
                //Toast.makeText(this, "Pengaturan", Toast.LENGTH_SHORT).show();
                //fragment = new help();
                break;

            case R.id.nav_hubungikami:
                TAG_MENU = "TAG_HUBUNGI";
                Intent intentHub = new Intent(MainActivity.this, HubungiActivity.class);
                startActivity(intentHub);
                //Toast.makeText(this, "Hubungi", Toast.LENGTH_SHORT).show();
                //fragment = new help();
                break;

            case R.id.nav_logout:
                TAG_MENU = "TAG_LOGOUT";
                //Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                askLogout();
                break;
        }


        if (fragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment, TAG_MENU);
            fragmentTransaction.commit();
            //fragmentTransaction.commitAllowingStateLoss();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
