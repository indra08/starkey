package id.starkey.pelanggan.Kunci.TrxKunci;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Ratting.RattingActivity;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Utilities.SessionManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class TrxKunciActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    GoogleMap map;
    private TextView tDetail, tAlamatCust, tEstWaktu, tNoTrx, tNamaMitra, tStatusName;
    private ImageView iTelpon, iSms, iCancelTrx;
    private String inboxPayload, sUserLat, sUserLng, tokennyaUser;
    private double userLat, userLng, mitraLat, mitraLng;
    private String sStatusName, sId, sNamaMitra, sNoHpMitra, sMitraLat, sMitraLng, sAlamatUser, sProfilPhotoMitra;
    private double latMitraTracking, lngMitraTracking;
    private ProgressBar progressBar;
    private String sDialogLayananKunci, sDialogNamaJenisKunci, sDialogQty, sDialogKeterangan, sDialogTotal;
    private String sDialogJasa, sDialogTips, sDialogBiayaLain, sDialogGrandTotal;
    private CircularImageView circularImageView;

    //to kill
    static TrxKunciActivity trxKunciActivity;

    //interval
    Handler h = new Handler();
    int delay = 2*1000; //1 second=1000 milisecond, 5*1000=5seconds
    Runnable runnable;

    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";
    public static int mitraState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trx_kunci);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarTrxKunci);
        setSupportActionBar(toolbar);
        /*
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
         */
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        setTitle("Transaksi Kunci");
        //Toast.makeText(this, "Klik detail untuk melihat detail\nAtau Klik telepon untuk masuk ke halaman Ratting", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Anda sudah mendapatkan mitra", Toast.LENGTH_SHORT).show();

        //init circular iv
        circularImageView = findViewById(R.id.imgViewProfilMitra);

        //init pb
        progressBar = findViewById(R.id.pbTrxKunci);

        //get params from map
        Bundle bundle = getIntent().getExtras();
        inboxPayload = bundle.getString("message");

        //json string to json object
        try {
            JSONObject joDetailMitra = new JSONObject(inboxPayload);
            Log.d("payloadKunci", joDetailMitra.toString());
            sStatusName = joDetailMitra.getString("status_name");
            sId = joDetailMitra.getString("id");
            sStatusName = joDetailMitra.getString("status_name");
            sUserLat = joDetailMitra.getString("user_lat"); //string
            userLat = Double.parseDouble(sUserLat); //double
            sUserLng = joDetailMitra.getString("user_long"); //string
            userLng = Double.parseDouble(sUserLng); //double
            sAlamatUser = joDetailMitra.getString("user_location");

            //for dialog detail
            sDialogLayananKunci = joDetailMitra.getString("nama_layanan_kunci");
            sDialogNamaJenisKunci = joDetailMitra.getString("type_kunci");
            sDialogQty = joDetailMitra.getString("qty");
            sDialogKeterangan = joDetailMitra.getString("keterangan");
            sDialogTotal = joDetailMitra.getString("total_awal");



            JSONObject dataMitra = joDetailMitra.getJSONObject("detail");
            sNamaMitra = dataMitra.getString("nama_mitra");
            sNoHpMitra = dataMitra.getString("nohp_mitra");
            sMitraLat = dataMitra.getString("mitra_lat"); //string
            mitraLat = Double.parseDouble(sMitraLat); //double
            sMitraLng = dataMitra.getString("mitra_long"); //string
            mitraLng = Double.parseDouble(sMitraLng); //double
            sProfilPhotoMitra = dataMitra.getString("mitra_profile_photo");



        } catch (JSONException e) {
            e.printStackTrace();
        }

        //almtCust = bundle.getString("almt");
        //asalLat = bundle.getDouble("lemparLat");
        //asalLng = bundle.getDouble("lemparLng");

        //set circular iv
        Picasso.with(this)
                .load(sProfilPhotoMitra)
                .placeholder(R.drawable.progress_animation)
                .into(circularImageView);

        //init tv & iv
        tDetail = findViewById(R.id.detail);
        tDetail.setOnClickListener(this);
        tAlamatCust = findViewById(R.id.tvAlamatPengguna);
        tAlamatCust.setText(sAlamatUser);
        tEstWaktu = findViewById(R.id.estWaktu);

        tNoTrx = findViewById(R.id.txtNoTrx);
        tNoTrx.setText("Order ID: KC"+sId);
        tNamaMitra = findViewById(R.id.txtNamaMitra);
        tNamaMitra.setText(sNamaMitra);
        tStatusName = findViewById(R.id.txtStatusName);
        tStatusName.setText(sStatusName);

        iTelpon = findViewById(R.id.imgTelpon);
        iTelpon.setOnClickListener(this);
        iSms = findViewById(R.id.imgSms);
        iSms.setOnClickListener(this);
        iCancelTrx = findViewById(R.id.imgCancelTrxKunci);
        iCancelTrx.setOnClickListener(this);

        //init map
        initializeMap();

        //latlong posisi customer
        //LatLng custPos = new LatLng(asalLat, asalLng);
        getEstimasiWaktu(userLat, userLng, mitraLat, mitraLng);

        trxKunciActivity = this;

        //start interval
        h.postDelayed(new Runnable() {
            public void run() {
                //do something

                runnable=this;
                h.postDelayed(runnable, delay);
                statusTransaksiKunci(sId);
            }
        }, delay);

        //get token id user from preference
        getPref();

        statusTransaksiKunci(sId);

        mitraState = 0;
    }

    public static TrxKunciActivity getInstance(){
        return trxKunciActivity;
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    private void statusTransaksiKunci(String idTrans){

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

//        String STATUS_TRX_KUNCI = "https://api.starkey.id/api/status_transaksi_kunci/"+idTrans; //url api lama
        String STATUS_TRX_KUNCI = "https://api.starkey.id/api/v1.1/status_transaksi_kunci/"+idTrans;

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("lat", latnya);
        //params.put("long", lngnya);
        //params.put("available", avail);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, STATUS_TRX_KUNCI, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("trxkunci", response.toString());
                        try {
                            //Process os success response

                            JSONObject joData = response.getJSONObject("data");
                            String statusname = joData.getString("status_name");
                            String mitraLat = joData.getString("mitra_lat");
                            String mitraLng = joData.getString("mitra_long");
                            sDialogTotal = joData.getString("total_awal");
                            sDialogJasa = joData.getString("biaya_antar");
                            sDialogTips = joData.getString("tips");
                            sDialogBiayaLain = joData.getString("biaya_lain");
                            sDialogGrandTotal = joData.getString("total_akhir");

                            tStatusName.setText(statusname);
                            if (tStatusName.getText().toString().equals("menuju lokasi")){
                                mitraState = 2;
                                //iCancelTrx.setEnabled(false);
                            }
                            if (tStatusName.getText().toString().equals("sampai lokasi")){
                                tEstWaktu.setVisibility(View.INVISIBLE);
                            }
                            latMitraTracking = Double.parseDouble(mitraLat);
                            lngMitraTracking = Double.parseDouble(mitraLng);

                            progressBar.setIndeterminate(false);
                            progressBar.setVisibility(View.INVISIBLE);
                            //Toast.makeText(MainActivity.this, hasil, Toast.LENGTH_SHORT).show();
                            trackPositionMitra(latMitraTracking, lngMitraTracking);
                            //getEstimasiWaktu(userLat, userLng, mitraLat, mitraLng);
                            getEstimasiWaktu(userLat, userLng, latMitraTracking, lngMitraTracking);
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
                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.INVISIBLE);

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
                Toast.makeText(TrxKunciActivity.this,message, Toast.LENGTH_LONG).show();
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
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }


    private void getEstimasiWaktu(double latiUser, double longiUser, double latiMitra, double longiMitra) {
        //String urlMatrix = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=-7.01629,110.4631213&destinations=-7.039751,110.491264&key=AIzaSyD-N1rB8rGBmP_f2koq8bjdMTjYRLqBjwk";
        //String slat = String.valueOf(lat);
        //String slng = String.valueOf(lng);
        //String urlMatrix = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=-7.01629,110.4631213&destinations="+slat+","+slng+"&key=AIzaSyD-N1rB8rGBmP_f2koq8bjdMTjYRLqBjwk";
        String urlMatrix = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins="+latiMitra+","+longiMitra+"&destinations="+latiUser+","+longiUser+"&key=AIzaSyD-N1rB8rGBmP_f2koq8bjdMTjYRLqBjwk";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlMatrix,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonRespRouteDistance = null;
                        try {
                            jsonRespRouteDistance = new JSONObject(response)
                                    .getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray ("elements")
                                    .getJSONObject(0)
                                    .getJSONObject("duration");

                            String duration = jsonRespRouteDistance.get("text").toString();
                            //Toast.makeText(TrxKunciActivity.this, distance, Toast.LENGTH_SHORT).show();
                            tEstWaktu.setText("Estimated Times "+duration);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //String destination_addr = new JSONObject(httpResponse).get("destination_addresses").toString();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(TrxKunciActivity.this, "Error Listener", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void userCancelTransaction(){
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sId);
        params.put("cancel_on", "transaction");

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.user_cancel_transaction, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String hasil = response.toString();
                        Log.d("hasilcancel", hasil);
                        Intent main = new Intent(TrxKunciActivity.this, MainActivity.class);
                        startActivity(main);
                        finishAffinity();
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
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void trackPositionMitra(double mitraLatIcon, double mitraLngIcon){
        map.clear();

        //icon position mitra
        LatLng positionMitra = new LatLng(mitraLatIcon, mitraLngIcon);
        map.addMarker(new MarkerOptions().position(positionMitra).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_small)));

        LatLng from = new LatLng(userLat, userLng);
        map.addMarker(new MarkerOptions().position(from).title("Pengguna").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_cus_starkey_small)));
        //map.moveCamera(CameraUpdateFactory.newLatLng(from));

        //LatLng mitra = new LatLng(-7.01629, 110.4631213);
        LatLng mitra = new LatLng(mitraLat, mitraLng);
        map.addMarker(new MarkerOptions().position(mitra).title("Mitra").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker_biru)));
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

        /*
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(from);
        builder.include(mitra);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,width, height, padding));
         */


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //LatLng from = new LatLng(-7.0049918, 110.4551927);
        LatLng from = new LatLng(userLat, userLng);
        map.addMarker(new MarkerOptions().position(from).title("Pengguna").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_cus_starkey_small)));
        //map.moveCamera(CameraUpdateFactory.newLatLng(from));

        //LatLng mitra = new LatLng(-7.01629, 110.4631213);
        LatLng mitra = new LatLng(mitraLat, mitraLng);
        map.addMarker(new MarkerOptions().position(mitra).title("Mitra").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker_biru)));
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(from);
        builder.include(mitra);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,width, height, padding));
    }

    private void initializeMap(){
        if (map == null){
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapTrxKunci);
            if (mapFragment != null){
                mapFragment.getMapAsync(TrxKunciActivity.this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //finish();

        /*

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
         */
        //moveTaskToBack(true);
        //YourActivity.this.moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        if (v == tDetail){
            //Toast.makeText(this, "tDetail", Toast.LENGTH_SHORT).show();
            //Intent jump = new Intent(TrxKunciActivity.this, ReviewKunciActivity.class);
            //startActivity(jump);


            //to dialog
            dialogDetail();
        }
        if (v == iTelpon){
            Uri callMitra = Uri.parse("tel:" + sNoHpMitra);
            Intent intentCall = new Intent(Intent.ACTION_DIAL, callMitra);
            startActivity(intentCall);
        }
        if (v == iSms){
            Uri smsMitra = Uri.parse("sms:" + sNoHpMitra);
            Intent intentSms = new Intent(Intent.ACTION_VIEW, smsMitra);
            startActivity(intentSms);
        }
        if (v == iCancelTrx){
            if(mitraState == 2){

                AlertDialog dialog = new AlertDialog.Builder(TrxKunciActivity.this)
                        .setTitle("Peringatan")
                        .setMessage("Pesanan tidak dapat dibatalkan karena mitra menuju ke lokasi")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }else{
                askDeclinedTransaction();
            }

        }
    }

    private void askDeclinedTransaction(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan membatalkan order?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                showDialogBatal();
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

    private void showDialogBatal() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(TrxKunciActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_pembatalan_user, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final EditText edtKeterangan = (EditText) viewDialog.findViewById(R.id.edt_keterangan);
        final Button btnBatal = (Button) viewDialog.findViewById(R.id.btn_batal);
        final Button btnSimpan = (Button) viewDialog.findViewById(R.id.btn_simpan);

        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnBatal.setOnClickListener(new View.OnClickListener() {
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

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(edtKeterangan.getText().toString().isEmpty()){

                    edtKeterangan.setError("Keterangan harap diisi");
                    edtKeterangan.requestFocus();
                    return;
                }else{
                    edtKeterangan.setError(null);
                }

                // Post params to be sent to the server
                SessionManager session = new SessionManager(TrxKunciActivity.this);

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id_transaksi", sId);
                params.put("id_user", session.getID());
                params.put("jenis", "kunci");
                params.put("alasan", edtKeterangan.getText().toString());

                JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.savePembatalanOrder, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    String status = response.getString("status");
                                    String message = response.getString("message");
                                    if(status.equals("success")){

                                        Toast.makeText(TrxKunciActivity.this, message, Toast.LENGTH_LONG).show();
                                        userCancelTransaction();
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
                request_json.setRetryPolicy(policy);
                RequestHandler.getInstance(TrxKunciActivity.this).addToRequestQueue(request_json);
            }
        });

        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void dialogDetail(){
        final Dialog dialog = new Dialog(this);
        //dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_detail_kunci);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        dialog.show();

        TextView tLayananKunci = dialog.findViewById(R.id.labelLayananKunciDialog);
        tLayananKunci.setText(sDialogLayananKunci);

        TextView tNamaKunci = dialog.findViewById(R.id.labelJenisNama);
        tNamaKunci.setText(sDialogNamaJenisKunci);

        TextView tQtyKunci = dialog.findViewById(R.id.labelQty);
        tQtyKunci.setText(sDialogQty);

        TextView tKetKunci = dialog.findViewById(R.id.labelKeteranganReview);
        tKetKunci.setText(sDialogKeterangan);

        TextView alamatAntar = dialog.findViewById(R.id.tvAlamatAntarDetail);
        alamatAntar.setText(sAlamatUser);


        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiahTotalAwal = rupiahFormat.format(Double.parseDouble(sDialogTotal));
        String resulthargaawal = Rupiah + " " + rupiahTotalAwal;

        TextView tBiayaKunci = dialog.findViewById(R.id.labelBiayaValue); //harga awal
        tBiayaKunci.setText(resulthargaawal);

        String rupiahjasa = rupiahFormat.format(Double.parseDouble(sDialogJasa));
        String resultjasa = Rupiah + " " + rupiahjasa;

        TextView tJasa = dialog.findViewById(R.id.tvJasa);
        tJasa.setText(resultjasa);

        String rupiahTips = rupiahFormat.format(Double.parseDouble(sDialogTips));
        String resultTips = Rupiah + " " + rupiahTips;

        TextView tTips = dialog.findViewById(R.id.tvTips);
        tTips.setText(resultTips);

        String rupiahBiayalain = rupiahFormat.format(Double.parseDouble(sDialogBiayaLain));
        String resultBiayalain = Rupiah + " " + rupiahBiayalain;

        TextView tBiayaLain = dialog.findViewById(R.id.tvBiayaLain);
        tBiayaLain.setText(resultBiayalain);

        String rupiahGrandTotal = rupiahFormat.format(Double.parseDouble(sDialogGrandTotal));
        String resultGrandTotal = Rupiah + " " + rupiahGrandTotal;

        TextView tGrandTotal = dialog.findViewById(R.id.tvGrandTotal);
        tGrandTotal.setText(resultGrandTotal);


    }
}
