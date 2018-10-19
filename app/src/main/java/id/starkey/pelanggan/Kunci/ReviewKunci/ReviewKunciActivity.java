package id.starkey.pelanggan.Kunci.ReviewKunci;

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
import id.starkey.pelanggan.Kunci.IsiDetailKunci.IsiDetailKunciActivity;
import id.starkey.pelanggan.Kunci.TrxKunci.TrxKunciActivity;
import id.starkey.pelanggan.Kunci.WaitingKunci.WaitingKunciActivity;
import id.starkey.pelanggan.MapsUbahLoc.MapsUbahActivity;
import id.starkey.pelanggan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReviewKunciActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bOrder, bUbahKet;
    private String detAlamat, sJenis, sQty, sKet, tokennyaUser, sEncodeImage;
    private TextView tAlamatAntar, tUbah, tJenisKunci, tQtyKunci, tKetKunci;
    private double latnya, lngnya;
    //string untuk estimasi harga
    private String id_layanan = "1";
    private String id_item, id_layanan_item, qty;
    private int totalfix;
    JSONObject jsonBody;

    //biaya
    private TextView tvBiayaLayanan, tvBiayaItem, tvTotalBiaya;

    //format rupiah
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_kunci);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarReviewKunci);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        setTitle("Review Kunci");

        //init tv biaya
        tvBiayaLayanan = findViewById(R.id.labelhargaReviewBawah);
        tvBiayaItem = findViewById(R.id.labelBiayaItemValue);
        tvTotalBiaya = findViewById(R.id.labelBiayaValue);

        //init btn
        bOrder = findViewById(R.id.btnOrder);
        bOrder.setOnClickListener(this);
        bUbahKet = findViewById(R.id.btnUbahReview);
        bUbahKet.setOnClickListener(this);

        //get params from map
        Bundle bundle = getIntent().getExtras();
        detAlamat = bundle.getString("alamatKunci");
        latnya = bundle.getDouble("latnya");
        lngnya = bundle.getDouble("lngnya");
        //get param from form kunci
        sJenis = bundle.getString("jenisKunci");
        sQty = bundle.getString("qtyKunci");
        sKet = bundle.getString("ketKunci");
        //get params from form kunci for estimasi harga
        qty = bundle.getString("qtyHarga");
        id_item = bundle.getString("id_itemHarga");
        id_layanan_item = bundle.getString("id_layanan_itemHarga");
        //get params image
        sEncodeImage = bundle.getString("stringGambar");
        //Log.d("jalImage", sEncodeImage);




        //init tv
        tAlamatAntar = findViewById(R.id.tvAlamatAntar);
        tAlamatAntar.setText(detAlamat);
        tUbah = findViewById(R.id.labelUbah);
        tUbah.setOnClickListener(this);
        tJenisKunci = findViewById(R.id.labelJenisKunci);
        tJenisKunci.setText(sJenis);
        tQtyKunci = findViewById(R.id.labelQtyKunci);
        tQtyKunci.setText(sQty+" Pcs");
        tKetKunci = findViewById(R.id.labelKeteranganKunci);
        tKetKunci.setText(sKet);


        getPref();
        estimasiHarga(id_layanan, id_item, id_layanan_item, qty);
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    private void estimasiHarga(String idLayanan, String idItem, String idLayananItem, String qtyItem){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        //String URL_ESTIMASI_HARGA = "https://api.starkey.id/api/estimasi_harga?id_layanan=1&id_item=31&id_layanan_item=1&qty=1";
        String URL_ESTIMASI_HARGA = "https://api.starkey.id/api/v1.1/estimasi_harga?id_layanan="+idLayanan+"&id_item="+idItem+"&id_layanan_item="+idLayananItem+"&qty="+qtyItem;
        Log.d("URLestimasi", URL_ESTIMASI_HARGA);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL_ESTIMASI_HARGA, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responNewEstimasiHarga", response.toString());
                        loading.dismiss();
                        try {
                            response = response.getJSONObject("data");
                            JSONObject datanya = response.getJSONObject("estimasi");
                            String layanan = datanya.getString("layanan");
                            String item = datanya.getString("item");
                            String total = datanya.getString("total");
                            totalfix = Integer.parseInt(total);
                            Log.d("asemik", String.valueOf(totalfix));

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


                            tvBiayaLayanan.setText(resultlayanan);
                            tvBiayaItem.setText(resultitem);
                            tvTotalBiaya.setText(resulttotal);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == bOrder){
            Intent intent = new Intent(ReviewKunciActivity.this, WaitingKunciActivity.class);
            intent.putExtra("idlayanankunci", id_layanan_item);
            intent.putExtra("idkunci", id_item);
            intent.putExtra("latitude", latnya);
            intent.putExtra("longitude", lngnya);
            intent.putExtra("alamatLengkap", detAlamat); //detail text alamat
            intent.putExtra("qty", sQty);
            intent.putExtra("keterangan",sKet);
            intent.putExtra("biayaEstimasiS", tvTotalBiaya.getText().toString());
            intent.putExtra("biayaEstimasi", totalfix);
            intent.putExtra("gambarByUser", sEncodeImage);

            startActivity(intent);
            finish();
        }
        if (v == bUbahKet){
            finish();
        }
        if (v == tUbah){
            Intent jump = new Intent(ReviewKunciActivity.this, MapsUbahActivity.class);
            jump.putExtra("lat", latnya);
            jump.putExtra("lnt", lngnya);
            startActivityForResult(jump, 123);
            //startActivityForResult(jumpJenis, 111);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123){
            if (resultCode == 131){
                String detail = data.getStringExtra("detailAlamat");
                tAlamatAntar.setText(detail);
            } else {
                Toast.makeText(this, "batal ubah alamat", Toast.LENGTH_SHORT).show();
            }
        }
        /*
        if (requestCode == 111){
            if (resultCode == 110){
                String nama_kunci = data.getStringExtra("kunci");
                etJenisKunci.setFocusable(true);
                etJenisKunci.setText(nama_kunci);
                etJenisKunci.setFocusable(false);
            } else {
                Toast.makeText(this, "Anda belum memilih jenis kunci", Toast.LENGTH_SHORT).show();
            }
        }
         */
    }
}
