package id.starkey.pelanggan.HomeMenuJasaLain;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.HomeMenuJasaLain.Adapter.ListOrderJLAdapter;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ItemValidation;

public class DeskripsiOrder extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveStartedListener{

    private GoogleMap map;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    private GoogleApiClient googleApiClient;
    private PlaceAutocompleteFragment placeAutoComplete;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 13;
    private TextView tNama, tDetail;
    private String paramLat, paramLng, detAlamat;
    double fromMainLat, fromMainLng;
    private ProgressBar progressBar;
    private double latIdle, lngIdle;
    private ImageView ivCenter, ivTop;
    public static List<CustomItem> listOrder = new ArrayList<>();
    private ListOrderJLAdapter adapter;
    private String currentLatitude = "", currentLongitude = "";
    private ItemValidation iv = new ItemValidation();

    //bias
    private static final LatLngBounds BOUNDS_SEMARANG = new LatLngBounds(new LatLng(-6.966667, 110.416664),
            new LatLng(-6.466667, 110.446664));
    private ListView lvOrder;
    private TextView tvSubtotal, tvBiayaJemput, tvTotal, tvStatus;
    private Button btnOrder, btnBatal;
    private int status = 0;
    private Context context;
    private FloatingActionMenu materialDesignFAM;
    private FloatingActionButton floatingActionButton1, floatingActionButton2;
    private String idToko = "", namaToko = "", latToko = "", longToko = "";
    private boolean isLoading = false;
    private double subTotal = 0, totalOngkir = 0, totalAll = 0;
    private String idUser = "", phoneUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deskripsi_order);

        context = this;
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //init text view
        tNama = findViewById(R.id.txtNama);
        tDetail = findViewById(R.id.txtDetail);

        //init image view
        ivCenter = findViewById(R.id.markerCenter);
        ivTop = findViewById(R.id.markerTop);

        //get params from menu
        fromMainLat = -6.466667;
        fromMainLng = 110.446664;
        isLoading = false;

        //cek condistion for icon center maps and top
        ivCenter.setImageResource(R.mipmap.ic_pin);
        ivTop.setImageResource(R.drawable.ic_search_black_24dp);

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setBoundsBias(BOUNDS_SEMARANG);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Maps", "Place selected: " + place.getName());
                //LatLng myPos = new LatLng(latti, longi);
                map.clear();
                LatLng myPos = place.getLatLng();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                //getDescLoc(latti, longi);
                tNama.setText(place.getName());
                tDetail.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        progressBar = findViewById(R.id.pb);
        lvOrder = (ListView) findViewById(R.id.lv_order);
        tvSubtotal = (TextView) findViewById(R.id.tv_subtotal);
        tvBiayaJemput = (TextView) findViewById(R.id.tv_biaya_jemput);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        btnOrder = (Button) findViewById(R.id.btn_order);
        btnBatal = (Button) findViewById(R.id.btn_batal);
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        status = 0;
        materialDesignFAM.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idToko = bundle.getString("idtoko","");
            namaToko = bundle.getString("toko","");
            latToko = bundle.getString("lat","");
            longToko = bundle.getString("long","");
        }

        adapter = new ListOrderJLAdapter((Activity) context, listOrder);
        lvOrder.setAdapter(adapter);

        // hitung subtotal
        subTotal = 0;
        for(CustomItem item: listOrder){

            subTotal += iv.parseNullDouble(item.getItem3());
        }
        tvSubtotal.setText(iv.ChangeToCurrencyFormat(subTotal));

        getPref();
    }

    private void initEvent(){

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked

            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status == 0) {//baru
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Konfirmasi")
                            .setMessage("Anda yakin ingin melakukan order ?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                        /*final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                        View viewDialog = inflater.inflate(R.layout.dialog_search_mitra, null);
                                        builder.setView(viewDialog);
                                        builder.setCancelable(false);

                                        final Button btnHubungi = (Button) viewDialog.findViewById(R.id.btn_hubungi);
                                        final ImageView ivCancel = (ImageView) viewDialog.findViewById(R.id.iv_cancel);
                                        final TextView tvKeterangan = (TextView) viewDialog.findViewById(R.id.tv_keterangan);

                                        final AlertDialog alert = builder.create();
                                        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                        ivCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view2) {

                                                if(alert != null){

                                                    try {
                                                        alert.dismiss();
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });

                                        try {
                                            alert.show();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }*/

                                        saveData();
                                    }
                                })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }
            }
        });

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
    }

    private void initData() {


    }

    private void saveData() {

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Menyimpan data...");
        loading.setCancelable(false);
        loading.show();

        isLoading = true;

        JSONArray jProduk = new JSONArray();

        for (CustomItem item: listOrder){

            JSONObject jo = new JSONObject();

            try {

                jo.put("id", item.getItem1());
                jo.put("qty", "1");
                jProduk.put(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("id_user", idUser);
            jBody.put("id_toko", idToko);
            jBody.put("username", phoneUser);
            jBody.put("state", detAlamat);
            jBody.put("latitude", currentLatitude);
            jBody.put("longitude", currentLongitude);
            jBody.put("produk", jProduk);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.saveOrderLain
                , jBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        loading.dismiss();
                        String message = "Terjadi kesalahan saat memuat data, harap ulangi";

                        try {

                            String status = response.getJSONObject("metadata").getString("status");
                            message = response.getJSONObject("metadata").getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                            if (status.equals("200")){

                                MainActivity.isOrderLain = true;
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }

                        }catch (JSONException ex){
                            ex.printStackTrace();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                loading.dismiss();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ServerError) {
                    message = "Server tidak ditemukan";
                } else if (error instanceof AuthFailureError) {
                    message = "Authentification Failed";
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
                params.put("Client-Service", "starkey");
                params.put("Auth-Key", "44b7eb3bbdccdfdaa202d5bfd3541458");
                return params;
            }
        };

        int socketTimeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void getHargaJemput() {

        isLoading = true;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_toko", idToko);
        params.put("latitude", currentLatitude);
        params.put("longitude", currentLongitude);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.hitungOngkir
                , new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        isLoading = false;
                        String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                        totalOngkir = 0;

                        try {
                            String status = response.getJSONObject("metadata").getString("status");
                            message = response.getJSONObject("metadata").getString("message");

                            totalAll = 0;
                            if (status.equals("200")){

                                String total = response.getJSONObject("response").getString("total");
                                totalOngkir = iv.parseNullDouble(total);
                            }else{

                            }

                            totalAll = totalOngkir + subTotal;
                            tvBiayaJemput.setText(iv.ChangeToCurrencyFormat(totalOngkir));
                            tvTotal.setText(iv.ChangeToCurrencyFormat(totalAll));

                        }catch (JSONException ex){
                            ex.printStackTrace();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading = false;
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ServerError) {
                    message = "Server tidak ditemukan";
                } else if (error instanceof AuthFailureError) {
                    message = "Authentification Failed";
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
                params.put("Client-Service", "starkey");
                params.put("Auth-Key", "44b7eb3bbdccdfdaa202d5bfd3541458");
                return params;
            }
        };

        int socketTimeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //Toast.makeText(this, "map ready", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        2);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
            return;
        }

        map.setMyLocationEnabled(true);
        //map.getUiSettings().setCompassEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);

        getLocation();

    }

    private void getPref() {
        SharedPreferences custDetails = context.getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        idUser = custDetails.getString("idUser", "");
        phoneUser = custDetails.getString("phoneUser", "");
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null) location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                map.clear();
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                LatLng myPos = new LatLng(latti, longi);

                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                getDescLoc(latti, longi);

            } else {
                map.clear();
                double lattiElse = fromMainLat;
                double longiElse = fromMainLng;

                LatLng myPos = new LatLng(lattiElse, longiElse);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                getDescLoc(lattiElse, longiElse);
            }
        }
    }

    private void getDescLoc(double latitude, double longitude) {

        currentLatitude = iv.doubleToStringFull(latitude);
        currentLongitude = iv.doubleToStringFull(longitude);

        if(!currentLongitude.isEmpty()){
            if(!isLoading) getHargaJemput();
        }

        try {
            Geocoder geo = new Geocoder(DeskripsiOrder.this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                Toast.makeText(this, "Waiting for Location", Toast.LENGTH_SHORT).show();
                map.clear();
            } else {
                if (addresses.size() > 0) {
                    tNama.setText(addresses.get(0).getFeatureName());
                    tDetail.setText(addresses.get(0).getAddressLine(0));
                    detAlamat = tDetail.getText().toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }

        if(!latToko.isEmpty()){

            LatLng latLng = new LatLng(iv.parseNullDouble(latToko), iv.parseNullDouble(longToko));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(namaToko);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_store));
            markerOptions.getPosition();
            map.addMarker(markerOptions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null){
            map.clear();
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            getDescLoc(lat, lng);
        } else {
            map.clear();
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(locationNet != null){

                double latNet = locationNet.getLatitude();
                double lngNet = locationNet.getLongitude();
                getDescLoc(latNet, lngNet);
            }
        }

        //double lat = fromMainLat;
        //double lng = fromMainLng;
        //getDescLoc(lat, lng);
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCameraIdle() {
        //LatLng midLatLng = map.getCameraPosition().target;
        LatLng midLatLng = map.getCameraPosition().target;
        getDescLoc(midLatLng.latitude, midLatLng.longitude);
        latIdle = midLatLng.latitude;
        lngIdle = midLatLng.longitude;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

}
