package id.starkey.pelanggan.Stempel.ReviewStempel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Stempel.WaitingStempel.WaitingStempelActivity;
import id.starkey.pelanggan.Utilities.LastOrder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReviewStempelActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bUbahStempel, bOrderStempel;
    private String sJenisStemp, sUkuranStemp, sQtyStemp, sKetStemp, sAlamatStemp, sTotalStemp;
    private TextView tJenisStemp, tUkuranStemp, tQtyStemp, tKetStemp, tAlamatStemp;
    private String tokennyaUser;
    JSONObject jsonBody;
    private TextView tLayanan, tItem, tTotal; //detail harga
    private double latStemp, lngStemp;

    //format rupiah
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";
    public static LastOrder lastOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_stempel);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarReviewStempel);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        setTitle("Review Stempel");

        //get params from isi detail stempel
        Bundle bundle = getIntent().getExtras();
        sJenisStemp = bundle.getString("jenisstemp");
        sUkuranStemp = bundle.getString("ukuranstemp");
        latStemp = bundle.getDouble("latStemp");
        lngStemp = bundle.getDouble("lngStemp");
        sAlamatStemp = bundle.getString("alamatStemp");
        sQtyStemp = bundle.getString("qtystemp");
        sKetStemp = bundle.getString("ketstemp");



        //init textview
        tJenisStemp = findViewById(R.id.tvStempel);
        tUkuranStemp = findViewById(R.id.tvUkuranStempel);
        tQtyStemp = findViewById(R.id.tvJumlahStempel);
        tKetStemp = findViewById(R.id.tvKeteranganStempel);
        tAlamatStemp = findViewById(R.id.tvAlamatAntarStempel);
        //set tv
        tJenisStemp.setText(sJenisStemp.replaceAll("%20", " "));
        tUkuranStemp.setText(sUkuranStemp);
        tQtyStemp.setText(sQtyStemp);
        tKetStemp.setText(sKetStemp);
        tAlamatStemp.setText(sAlamatStemp);

        //init tv detail harga
        tLayanan = findViewById(R.id.tvLayananStempel);
        tItem = findViewById(R.id.tvItemStempel);
        tTotal = findViewById(R.id.tvTotalStempel);

        //init btn
        bUbahStempel = findViewById(R.id.btnUbahReviewStempel);
        bUbahStempel.setOnClickListener(this);
        bOrderStempel = findViewById(R.id.btnOrderStempel);
        bOrderStempel.setOnClickListener(this);

        getPref();
        getEstimasiHarga(sJenisStemp,sUkuranStemp,sQtyStemp);
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == bUbahStempel){
            finish();
        }
        if (v == bOrderStempel){

            //get time
            Calendar calendar = Calendar.getInstance();
            String timeUser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

            Intent toOrderStempel = new Intent(ReviewStempelActivity.this, WaitingStempelActivity.class);
            toOrderStempel.putExtra("namastempel", sJenisStemp);
            toOrderStempel.putExtra("ukuran", sUkuranStemp);
            toOrderStempel.putExtra("latUser", latStemp);
            toOrderStempel.putExtra("lngUser", lngStemp);
            toOrderStempel.putExtra("namalokasi", sAlamatStemp);
            toOrderStempel.putExtra("qty", sQtyStemp);
            toOrderStempel.putExtra("keterangan", sKetStemp);
            toOrderStempel.putExtra("waktuawal", timeUser);
            toOrderStempel.putExtra("biayaestimasi", sTotalStemp);

            lastOrder = new LastOrder();
            lastOrder.createStampelOrder(sJenisStemp, sUkuranStemp, latStemp, lngStemp, sAlamatStemp,
                    sQtyStemp, sKetStemp, timeUser, sTotalStemp);

            MainActivity.stateOrder = 2;

            startActivity(toOrderStempel);

        }
    }

    private void getEstimasiHarga(String namaStempel, String ukuranStempel, String qtyStemp){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        String URL_ESTIMASI_HARGA = "https://api.starkey.id/api/stempel/estimasi_harga?nama_stempel="+namaStempel.toLowerCase()+"&ukuran="+ukuranStempel.toLowerCase()+"&qty="+qtyStemp.toLowerCase();
        Log.d("urlstemp", URL_ESTIMASI_HARGA);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL_ESTIMASI_HARGA, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("eststemp", response.toString());
                        loading.dismiss();
                        try {
                            response = response.getJSONObject("data");
                            JSONObject datanya = response.getJSONObject("estimasi");
                            String layanan = datanya.getString("layanan");
                            String item = datanya.getString("item");
                            String total = datanya.getString("total");
                            sTotalStemp = total;

                            //rp layanan
                            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
                            String rupiahlayanan = rupiahFormat.format(Double.parseDouble(layanan));
                            String resultlayanan = Rupiah + "" + rupiahlayanan;

                            //rp item
                            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
                            String rupiahitem = rupiahFormat.format(Double.parseDouble(item));
                            String resultitem = Rupiah + "" + rupiahitem;

                            //rp total
                            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
                            String rupiahtotal = rupiahFormat.format(Double.parseDouble(total));
                            String resulttotal = Rupiah + "" + rupiahtotal;

                            tLayanan.setText(resultlayanan);
                            tItem.setText(resultitem);
                            tTotal.setText(resulttotal);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
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
}
