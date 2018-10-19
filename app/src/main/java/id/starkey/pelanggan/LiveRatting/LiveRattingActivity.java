package id.starkey.pelanggan.LiveRatting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.starkey.pelanggan.RequestHandler;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class LiveRattingActivity extends AppCompatActivity implements View.OnClickListener {

    private String datapayload, tokennyaUser;
    private String sStatusOrder, sIdTransaksi, sNamaMitra, sAlamatCust, sTanggal, sNamaItem, sPhotoMitra;
    private TextView tStatus, tNomorOrder, tNamaItem, tNamaMitra, tTanggal, tAlamatCust;
    //private String sHargaItem, sBiayaLayanan, sBiayaAntar, sBiayaLain, sTips;
    //private TextView tHargaItem, tBiayaLayanan, tBiayaAntar, tBiayaLain, tTips;
    private String sBiaya, sJasa, sBiayaLain, sGrandTotal, sTips;
    private TextView tBiaya, tJasa, tBiayaLain, tGrandTotal, tTips;
    private EditText etKomentar;
    private MaterialRatingBar ratingBar;
    private CircularImageView circularImageView;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";
    private Button bSimpanLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_ratting);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarLiveRatting);
        setSupportActionBar(toolbar);
        setTitle("Detail");
        toolbar.setTitleTextColor(Color.parseColor("#000000"));

        //init ratingbar
        ratingBar = findViewById(R.id.ratingbarLive);

        //init et komentar
        etKomentar = findViewById(R.id.editTextKomentarLive);

        //init btn
        bSimpanLive = findViewById(R.id.btnLiveRatting);
        bSimpanLive.setOnClickListener(this);

        //init tv
        tStatus = findViewById(R.id.tvStatusTrxLive);
        tNomorOrder = findViewById(R.id.tvNoTrxLive);
        tNamaMitra = findViewById(R.id.tvNamaMitraLive);
        tNamaItem = findViewById(R.id.tvNamaItemLive);
        tTanggal = findViewById(R.id.tvTglLive);
        tAlamatCust = findViewById(R.id.tvAlamatLive);

        tBiaya = findViewById(R.id.tvBiayaLive);
        tJasa = findViewById(R.id.tvJasaLive);
        tBiayaLain = findViewById(R.id.tvBiayaLainLive);
        tGrandTotal = findViewById(R.id.tvGrandTotalLive);
        tTips = findViewById(R.id.tvTipsLive);

        //tHargaItem = findViewById(R.id.tvHargaItemLive);
        //tBiayaLayanan = findViewById(R.id.tvBiayaLayananLive);
        //tBiayaAntar = findViewById(R.id.tvBiayaAntar);
        //tBiayaLain = findViewById(R.id.tvBiayaLainLive);
        //tTips = findViewById(R.id.tvTipsLive);
        //tTotalBiaya = findViewById(R.id.tvTotalBiayaLive);


        //init circular imageview
        circularImageView = findViewById(R.id.imageViewMitra);

        //get params json object
        Bundle bundle = getIntent().getExtras();
        datapayload = bundle.getString("payloadtransaksiselesai");
        Log.d("payfromtrx", datapayload);

        //to json object
        try {
            JSONObject joDataPayload = new JSONObject(datapayload);
            sIdTransaksi = joDataPayload.getString("id");
            sStatusOrder = joDataPayload.getString("status_name");
            sAlamatCust = joDataPayload.getString("user_location");
            sTanggal = joDataPayload.getString("waktu_awal");
            sNamaItem = joDataPayload.getString("nama_item");

            sBiaya = joDataPayload.getString("total_awal");
            sJasa = joDataPayload.getString("biaya_antar");
            sBiayaLain = joDataPayload.getString("biaya_lain");
            sGrandTotal = joDataPayload.getString("total_akhir");
            sTips = joDataPayload.getString("tips");

            /*
            sHargaItem = joDataPayload.getString("harga_item");
            sBiayaLayanan = joDataPayload.getString("biaya_layanan");
            sBiayaAntar = joDataPayload.getString("biaya_antar");
            sBiayaLain = joDataPayload.getString("biaya_lain");
            sTips = joDataPayload.getString("tips");
            sTotalAkhir = joDataPayload.getString("total_akhir");
             */

            //detail mitra
            JSONObject detailMitra = joDataPayload.getJSONObject("detail");
            sNamaMitra = detailMitra.getString("nama_mitra");
            sPhotoMitra = detailMitra.getString("mitra_profile_photo");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //set image to circular
        Picasso.with(this)
                .load(sPhotoMitra)
                .placeholder(R.drawable.progress_animation)
                .into(circularImageView);

        //set text tv
        tNomorOrder.setText(sIdTransaksi);
        tStatus.setText(sStatusOrder);
        tNamaItem.setText(sNamaItem);
        tNamaMitra.setText(sNamaMitra);

        //tanggal
        String tglFormat = sTanggal.substring(0, Math.min(sTanggal.length(), 10));
        String [] formatbaruTgl = tglFormat.split("-");
        String fixtanggal = formatbaruTgl[2]+"-"+formatbaruTgl[1]+"-"+formatbaruTgl[0];
        tTanggal.setText(fixtanggal);

        tAlamatCust.setText(sAlamatCust);

        rupiahFormat =NumberFormat.getInstance(Locale.GERMANY);

        String rupiahBiaya = rupiahFormat.format(Double.parseDouble(sBiaya));
        String resultBiaya = Rupiah + " " + rupiahBiaya;
        tBiaya.setText(resultBiaya);

        String rupiahJasa = rupiahFormat.format(Double.parseDouble(sJasa));
        String resultJasa = Rupiah + " " + rupiahJasa;
        tJasa.setText(resultJasa);

        String rupiahBiayaLain = rupiahFormat.format(Double.parseDouble(sBiayaLain));
        String resultBiayaLain = Rupiah + " " + rupiahBiayaLain;
        tBiayaLain.setText(resultBiayaLain);

        String rupiahGrandTotal = rupiahFormat.format(Double.parseDouble(sGrandTotal));
        String resultGrandTotal = Rupiah + " " + rupiahGrandTotal;
        tGrandTotal.setText(resultGrandTotal);

        String rupiahTips = rupiahFormat.format(Double.parseDouble(sTips));
        String resultTips = Rupiah + " " + rupiahTips;
        tTips.setText(resultTips);

        /*
            sBiaya = joDataPayload.getString("total_awal");
            sJasa = joDataPayload.getString("biaya_antar");
            sBiayaLain = joDataPayload.getString("biaya_lain");
            sGrandTotal = joDataPayload.getString("total_akhir");
            sTips = joDataPayload.getString("tips");
         */

        /*
        String rupiahHargaItem = rupiahFormat.format(Double.parseDouble(sHargaItem));
        String resultHargaItem = Rupiah + " " + rupiahHargaItem;
        tHargaItem.setText(resultHargaItem);

        String rupiahBiayaLayanan = rupiahFormat.format(Double.parseDouble(sBiayaLayanan));
        String resultBiayaLayanan = Rupiah + " " + rupiahBiayaLayanan;
        tBiayaLayanan.setText(resultBiayaLayanan);

        String rupiahBiayaAntar = rupiahFormat.format(Double.parseDouble(sBiayaAntar));
        String resultBiayaAntar = Rupiah + " " + rupiahBiayaAntar;
        tBiayaAntar.setText(resultBiayaAntar);

        String rupiahBiayaLain = rupiahFormat.format(Double.parseDouble(sBiayaLain));
        String resultBiayaLain = Rupiah + " " + rupiahBiayaLain;
        tBiayaLain.setText(resultBiayaLain);

        String rupiahTips = rupiahFormat.format(Double.parseDouble(sTips));
        String resultTips = Rupiah + " " + rupiahTips;
        tTips.setText(resultTips);


        String rupiahTotalAkhir = rupiahFormat.format(Double.parseDouble(sTotalAkhir));
        String resultTotalAkhir = Rupiah + " " + rupiahTotalAkhir;
        tTotalBiaya.setText(resultTotalAkhir);


         */



        getPrefTokenBearer();
    }

    private void getPrefTokenBearer(){
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tohome = new Intent(LiveRattingActivity.this, MainActivity.class);
        startActivity(tohome);
        finish();
    }

    private void sendRatingMitra(){

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        String rattingbar = String.valueOf(ratingBar.getRating());
        String komen = etKomentar.getText().toString();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTransaksi);
        params.put("rating", rattingbar);
        params.put("review", komen);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.ratting_mitra, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msgsuc = response.getString("message");
                                Toast.makeText(LiveRattingActivity.this, msgsuc, Toast.LENGTH_SHORT).show();
                            } else {
                                //String konStatus = response.getString("status");
                                String msgelse = response.getString("message");
                                Toast.makeText(LiveRattingActivity.this, msgelse, Toast.LENGTH_SHORT).show();
                            }
                            Intent tohome = new Intent(LiveRattingActivity.this, MainActivity.class);
                            startActivity(tohome);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
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

    @Override
    public void onClick(View v) {
        if (v == bSimpanLive){
            sendRatingMitra();
        }
    }
}
