package id.starkey.pelanggan.Stempel.WaitingStempel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import id.starkey.pelanggan.BuildConfig;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.R;

import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Stempel.TrxStempel.TrxStempelActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WaitingStempelActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iCancelStempel;
    private String sJenisStemp, sUkuranStemp, sAlamatStemp, sQtyStemp, sKetStemp, sTimeStemp, sTotalStemp;
    private double latStemp, lngStemp;
    private String tokennyaUser, sIdTransaksiStempel;

    //to kill from other
    static WaitingStempelActivity waitingStempelActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_stempel);

        //init iv
        iCancelStempel = findViewById(R.id.imgCancelStempel);
        iCancelStempel.setOnClickListener(this);

        //get params from isi detail stempel
        Bundle bundle = getIntent().getExtras();
        sJenisStemp = bundle.getString("namastempel");
        sUkuranStemp = bundle.getString("ukuran");
        latStemp = bundle.getDouble("latUser");
        lngStemp = bundle.getDouble("lngUser");
        sAlamatStemp = bundle.getString("namalokasi");
        sQtyStemp = bundle.getString("qty");
        sKetStemp = bundle.getString("keterangan");
        sTimeStemp = bundle.getString("waktuawal");
        sTotalStemp = bundle.getString("biayaestimasi");


        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent jump = new Intent(WaitingStempelActivity.this, TrxStempelActivity.class);
                startActivity(jump);
                finish();
            }
        }, 3000);
         */


        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent jump = new Intent(WaitingLaundryActivity.this, TrxLaundryActivity.class);
                startActivity(jump);
                finish();
            }
        }, 3000);
         */

        //get id from pref
        getPref();
        getMitraStempel();

        waitingStempelActivity = this;

    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    public static WaitingStempelActivity getInstance(){
        return waitingStempelActivity;
    }

    @Override
    public void onClick(View v) {
        if (v == iCancelStempel){
            userCancelTransactionStempel();
            finish();
        }
    }

    private void getMitraStempel(){

        String versionName = BuildConfig.VERSION_NAME;

        String latUser = String.valueOf(latStemp);
        String lngUser = String.valueOf(lngStemp);

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nama_stempel", sJenisStemp.toLowerCase().replaceAll("%20", " "));
        params.put("ukuran", sUkuranStemp.toLowerCase());
        params.put("user_lat", latUser);
        params.put("user_long", lngUser);
        params.put("nama_lokasi", sAlamatStemp);
        params.put("qty", sQtyStemp);
        params.put("keterangan", sKetStemp);
        params.put("waktu_awal", sTimeStemp);
        params.put("biaya_estimasi", sTotalStemp);
        params.put("v_app", versionName);

        JSONObject testFormat = new JSONObject(params);
        //Log.d("formatSearch", testFormat.toString());

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.getMitraStempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Process os success response
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
                                Toast.makeText(WaitingStempelActivity.this, "Sukses mendapatkan mitra", Toast.LENGTH_SHORT).show();
                                JSONObject jodata = response.getJSONObject("data");
                                JSONObject transaksidata = jodata.getJSONObject("transaksi");
                                String idnyatrans = transaksidata.getString("id");
                                sIdTransaksiStempel = idnyatrans;
                                //Log.d("tangkapid", idnyatrans);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                Toast.makeText(WaitingStempelActivity.this, "Gagal mendapatkan mitra", Toast.LENGTH_SHORT).show();
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

    private void userCancelTransactionStempel(){
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTransaksiStempel);
        params.put("cancel_on", "search");

        JSONObject formatpost = new JSONObject(params);
        //Log.d("cekformat", formatpost.toString());

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.user_cancel_transaction_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String hasil = response.toString();
                        Log.d("hasilcancelwaiting", hasil);
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
