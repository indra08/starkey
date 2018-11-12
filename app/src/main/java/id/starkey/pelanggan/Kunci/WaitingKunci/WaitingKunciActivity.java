package id.starkey.pelanggan.Kunci.WaitingKunci;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import id.starkey.pelanggan.BuildConfig;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.Kunci.TrxKunci.TrxKunciActivity;
import id.starkey.pelanggan.RequestHandler;

import id.starkey.pelanggan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WaitingKunciActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView iCancel;
    private String alamatCust, tokennyaUser;
    private double latitude, longitude;
    private String sIdLayananKunci, sIdKunci, sQty, sKet, sBiayaEstimasi, sImageString, sIdTransaksi;
    private int iBiayaEstimasi;
    private String noWa = "", linkWa = "";

    //to kill from other
    static WaitingKunciActivity waitingKunciActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_kunci);

        iCancel = findViewById(R.id.imgCancel);
        iCancel.setOnClickListener(this);

        //get params from map
        Bundle bundle = getIntent().getExtras();
        alamatCust = bundle.getString("alamatLengkap");
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        sIdLayananKunci = bundle.getString("idlayanankunci");
        sIdKunci = bundle.getString("idkunci");
        sQty = bundle.getString("qty");
        sKet = bundle.getString("keterangan");
        sBiayaEstimasi = bundle.getString("biayaEstimasiS");
        iBiayaEstimasi = bundle.getInt("biayaEstimasi");
        sImageString = bundle.getString("gambarByUser");
        //Log.d("asemik1", String.valueOf(sBiayaEstimasi));
        //Log.d("asemik2", String.valueOf(iBiayaEstimasi));

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent jump = new Intent(WaitingKunciActivity.this, TrxKunciActivity.class);
                jump.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                jump.putExtra("almt", alamatCust);
                jump.putExtra("lemparLat", latitude);
                jump.putExtra("lemparLng", longitude);
                startActivity(jump);
                //finish();
                finishAffinity();
            }
        }, 3000);
         */
        getMitraKunci();

        //get id from pref
        getPref();

        waitingKunciActivity = this;
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    public static WaitingKunciActivity getInstance(){
        return waitingKunciActivity;
    }

    @Override
    public void onClick(View v) {
        if (v == iCancel){
            userCancelTransaction();
        }
    }

    private void getMitraKunci(){

        String versionName = BuildConfig.VERSION_NAME;
        //get time
        Calendar calendar = Calendar.getInstance();
        String timeUser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
        //Log.d("waktu", timeUser);

        String latMitra = String.valueOf(latitude);
        String lngMitra = String.valueOf(longitude);


        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_layanan_kunci", sIdLayananKunci);
        params.put("id_kunci", sIdKunci);
        params.put("user_lat", latMitra);
        params.put("user_long", lngMitra);
        params.put("nama_lokasi", alamatCust);
        params.put("qty", sQty);
        params.put("keterangan", sKet);
        //params.put("waktu_awal", "2018-03-26 12:02:02");
        params.put("waktu_awal", timeUser);
        params.put("biaya_estimasi", String.valueOf(iBiayaEstimasi));
        //params.put("biaya_estimasi", "48000");
        params.put("photo", "data:image/png;base64,"+sImageString);
        params.put("v_app", versionName);

        JSONObject formatpost = new JSONObject(params);
        //Log.d("cekformat", formatpost.toString());

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.getMitraKunciNew, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Process os success response
                            //loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                //String cobatoken = response.getString("token");

                                /*
                                JSONObject dataJO = new JSONObject();
                                dataJO = response.getJSONObject("data");
                                String id = dataJO.getString("id");
                                String nama = dataJO.getString("first_name");
                                String namablkg = dataJO.getString("last_name");
                                String phone = dataJO.getString("phone");
                                String email = dataJO.getString("email");
                                String token = dataJO.getString("token");
                                 */


                                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                //startActivity(intent);
                                //finish();
                                Toast.makeText(WaitingKunciActivity.this, "Sukses mendapatkan mitra", Toast.LENGTH_SHORT).show();
                                JSONObject jodata = response.getJSONObject("data");
                                JSONObject transaksidata = jodata.getJSONObject("transaksi");
                                String idnyatrans = transaksidata.getString("id");
                                sIdTransaksi = idnyatrans;
                                //Log.d("tangkapid", idnyatrans);
                            } else {

                                showDialogBatal();
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
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void showDialogBatal() {

        HashMap<String, String> params = new HashMap<String, String>();

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET ,ConfigLink.getWACS, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String status = response.getString("status");
                            if(status.equals("success")){

                                JSONArray ja = response.getJSONArray("data");
                                if(ja.length() > 0){

                                    JSONObject jo = ja.getJSONObject(0);

                                    noWa = jo.getString("nomor_wa");
                                    linkWa = jo.getString("link_wa");

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(WaitingKunciActivity.this);
                                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                    View viewDialog = inflater.inflate(R.layout.dialog_cs, null);
                                    builder.setView(viewDialog);
                                    builder.setCancelable(false);

                                    final Button btnHubungi = (Button) viewDialog.findViewById(R.id.btn_hubungi);
                                    final ImageView ivCancel = (ImageView) viewDialog.findViewById(R.id.iv_cancel);
                                    final TextView tvKeterangan = (TextView) viewDialog.findViewById(R.id.tv_keterangan);

                                    final AlertDialog alert = builder.create();
                                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                    tvKeterangan.setText("Mohon maaf kami belum dapat menyiapkan mitra yang tepat untuk anda. Tapi tidak perlu khawatir, anda tetap dapat menghubungi kami dengan menekan tautan dibawah ini atau hubungi "+noWa+".");

                                    ivCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view2) {

                                            if(alert != null){

                                                try {
                                                    alert.dismiss();
                                                    userCancelTransaction();
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });

                                    btnHubungi.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if(!linkWa.toLowerCase().contains("http://") && !linkWa.toLowerCase().contains("https://")) linkWa = "http://"+linkWa;
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkWa));
                                            startActivity(browserIntent);
                                        }
                                    });

                                    try {
                                        alert.show();
                                    }catch (Exception e){
                                        e.printStackTrace();
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

    private void userCancelTransaction(){
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTransaksi);
        params.put("cancel_on", "search");

        JSONObject formatpost = new JSONObject(params);
        //Log.d("cekformat", formatpost.toString());

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.user_cancel_transaction, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String hasil = response.toString();
                        Log.d("hasilcancel", hasil);
                        finish();
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
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }
}
