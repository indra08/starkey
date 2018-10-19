package id.starkey.pelanggan.Ratting;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.starkey.pelanggan.RequestHandler;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RattingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bSimpan;
    private CardView cDetailTrx, cRatingdanKet;
    private String sIdTrx, sNamaLayananKunci, sTypeKunci, sQty, sTotalAkhir, sNamaMitra, sStatusOrder, sLokasiUser, sKet,
                    sBiaya, sBiayaAntar, sHargaItem, sBiayaDasar, sBiayaLayanan,  sBiayaPerKm, sBiayaLain, sTips;
    private String sStatusCode, sIsRated;
    private JSONObject jsonBody;
    private String tokennyaUser;
    private MaterialRatingBar ratingBar;
    private EditText eKomenUser;
    private TextView tOrderNo, tNamaMitra, tStatusOrder;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratting);

        //init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRatting);
        setSupportActionBar(toolbar);
        /*
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
         */
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        setTitle("Detail");

        //init edittext komen
        eKomenUser = findViewById(R.id.etKomenUser);

        //init ratingbar
        ratingBar = findViewById(R.id.library_decimal_ratingbar);
        ratingBar.setOnClickListener(this);

        //init btn
        bSimpan = findViewById(R.id.btnSimpan);
        bSimpan.setOnClickListener(this);
        
        //init cv
        cDetailTrx = findViewById(R.id.cvDetailTransaksi);
        cDetailTrx.setOnClickListener(this);
        cRatingdanKet = findViewById(R.id.cardViewRatingdanKet);

        //get params from bundle
        Bundle bundle = getIntent().getExtras();
        sIdTrx = bundle.getString("idtransaksi");
        sStatusCode = bundle.getString("statuscode");
        sIsRated = bundle.getString("konRate");

        if (sStatusCode.equals("6") && sIsRated.equals("0") || sStatusCode.equals("8") && sIsRated.equals("0")){
            cRatingdanKet.setVisibility(View.VISIBLE);
            bSimpan.setVisibility(View.VISIBLE);
        }

        //init tv
        tOrderNo = findViewById(R.id.tvNoTrx);
        tOrderNo.setText("Order No: "+sIdTrx);
        tNamaMitra = findViewById(R.id.tvNamaMitra);
        tStatusOrder = findViewById(R.id.tvStatusOrder);

        getPrefTokenBearer();
        getDetailTrxUser(sIdTrx);
    }

    private void getPrefTokenBearer(){
        SharedPreferences custDetails = RattingActivity.this.getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent jump = new Intent(RattingActivity.this, MainActivity.class);
        startActivity(jump);
        finish();
    }
     */


    @Override
    public void onClick(View v) {
        if (v == bSimpan){
            //Toast.makeText(this, String.valueOf(ratingBar.getRating()), Toast.LENGTH_SHORT).show();
            sendRatingMitra();

            //Intent in = new Intent(RattingActivity.this, MainActivity.class);
            //startActivity(in);
            //finish();
        }
        if (v == cDetailTrx){
            dialogDetail();
        }
    }

    private void dialogDetail(){
        final Dialog dialog = new Dialog(this);
        //dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_detail_inratting);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        dialog.show();

        TextView alamatAntar = dialog.findViewById(R.id.tvAlamatAntarDetail);
        alamatAntar.setText(sLokasiUser);
        TextView ket = dialog.findViewById(R.id.labelKeteranganReview);
        ket.setText(sKet);
        TextView qty = dialog.findViewById(R.id.labelQty);
        qty.setText(sQty);
        TextView jenisTrx = dialog.findViewById(R.id.labelJenisNama);
        jenisTrx.setText(sNamaLayananKunci+ " " +sTypeKunci);

        //rincian harga
        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);

        TextView tBiaya = dialog.findViewById(R.id.tvBiayaInratting);
        TextView tJasa = dialog.findViewById(R.id.tvJasaInratting);
        TextView tBiayaLain = dialog.findViewById(R.id.tvBiayaLainInratting);
        TextView tGrandTotal = dialog.findViewById(R.id.tvGrandTotalInratting);
        TextView tTips = dialog.findViewById(R.id.tvTipsInratting);

        String rupiahBiaya = rupiahFormat.format(Double.parseDouble(sBiaya));
        String resultBiaya = Rupiah + " " + rupiahBiaya;
        tBiaya.setText(resultBiaya);

        String rupiahJasa = rupiahFormat.format(Double.parseDouble(sBiayaAntar));
        String resultJasa = Rupiah + " " + rupiahJasa;
        tJasa.setText(resultJasa);

        String rupiahBiayaLain = rupiahFormat.format(Double.parseDouble(sBiayaLain));
        String resultBiayaLain = Rupiah + " " + rupiahBiayaLain;
        tBiayaLain.setText(resultBiayaLain);

        String rupiahGrandTotal = rupiahFormat.format(Double.parseDouble(sTotalAkhir));
        String resultGrandTotal = Rupiah + " " + rupiahGrandTotal;
        tGrandTotal.setText(resultGrandTotal);

        String rupiahTips = rupiahFormat.format(Double.parseDouble(sTips));
        String resultTips = Rupiah + " " + rupiahTips;
        tTips.setText(resultTips);

        /*
        TextView hargaItem = dialog.findViewById(R.id.tvHargaItem);
        TextView biayaDasar = dialog.findViewById(R.id.tvBiayaDasar);
        TextView biayaLayanan = dialog.findViewById(R.id.tvBiayaLayanan);
        TextView biayaPerKm = dialog.findViewById(R.id.tvBiayaPerKm);
        TextView biayaLain = dialog.findViewById(R.id.tvBiayaLain);
        TextView tips = dialog.findViewById(R.id.tvTips);

        String rupiahHargaItem = rupiahFormat.format(Double.parseDouble(sHargaItem));
        String resultHargaItem = Rupiah + " " + rupiahHargaItem;
        hargaItem.setText(resultHargaItem);

        String rupiahBiayaDasar = rupiahFormat.format(Double.parseDouble(sBiayaDasar));
        String resultBiayaDasar = Rupiah + " " + rupiahBiayaDasar;
        biayaDasar.setText(resultBiayaDasar);

        String rupiahBiayaLayanan = rupiahFormat.format(Double.parseDouble(sBiayaLayanan));
        String resultBiayaLayanan = Rupiah + " " + rupiahBiayaLayanan;
        biayaLayanan.setText(resultBiayaLayanan);

        String rupiahBiayaPerKm = rupiahFormat.format(Double.parseDouble(sBiayaPerKm));
        String resultBiayaPerKm = Rupiah + " " + rupiahBiayaPerKm;
        biayaPerKm.setText(resultBiayaPerKm);

        String rupiahBiayaLain = rupiahFormat.format(Double.parseDouble(sBiayaLain));
        String resultBiayaLain = Rupiah + " " + rupiahBiayaLain;
        biayaLain.setText(resultBiayaLain);

        String rupiahTips = rupiahFormat.format(Double.parseDouble(sTips));
        String resultTips = Rupiah + " " + rupiahTips;
        tips.setText(resultTips);

        String rupiah = rupiahFormat.format(Double.parseDouble(sTotalAkhir));
        String result = Rupiah + " " + rupiah;

        TextView grandTotalakhir = dialog.findViewById(R.id.labelBiayaValue);
        grandTotalakhir.setText(result);
         */


    }

    private void getDetailTrxUser(String id){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        String URL_DETAIL_TRX = "https://api.starkey.id/api/transaction/user/"+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_DETAIL_TRX, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("logratting", response.toString());
                        try{
                            JSONObject dataObject = response.getJSONObject("data");
                            Log.d("cekrat", dataObject.toString());
                            //String msg = response.getString("message");
                            //Toast.makeText(RattingActivity.this, msg, Toast.LENGTH_SHORT).show();


                            sNamaLayananKunci = dataObject.getString("type_item");
                            sTypeKunci = dataObject.getString("jenis");
                            sQty = dataObject.getString("qty");
                            sTotalAkhir = dataObject.getString("total_akhir");
                            sNamaMitra = dataObject.getString("nama_mitra");
                            sStatusOrder = dataObject.getString("status");
                            sLokasiUser = dataObject.getString("nama_lokasi");
                            sKet = dataObject.getString("keterangan");

                            sBiaya = dataObject.getString("total_awal");

                            //biaya antar = biaya_dasar + tambahan_biaya_antar
                            String biayadasar = dataObject.getString("biaya_dasar");
                            String tambahanBiayaDasar = dataObject.getString("tambahan_biaya_antar");

                            int biayadasarint = Integer.parseInt(biayadasar);
                            int tambahanbiayaint = Integer.parseInt(tambahanBiayaDasar);
                            int biayaantar = biayadasarint + tambahanbiayaint;


                            sBiayaAntar = String.valueOf(biayaantar);

                            sHargaItem = dataObject.getString("harga_item");
                            sBiayaDasar = dataObject.getString("biaya_dasar");
                            sBiayaLayanan = dataObject.getString("biaya_layanan");
                            sBiayaPerKm = dataObject.getString("biaya_per_km");
                            sBiayaLain = dataObject.getString("biaya_lain");
                            sTips = dataObject.getString("tips");


                            tNamaMitra.setText(sNamaMitra);
                            tStatusOrder.setText(sStatusOrder);
                            //Toast.makeText(RattingActivity.this, sNamaMitra, Toast.LENGTH_SHORT).show();

                            //adapter = new RVSelesai(listItemSelesais, getContext());
                            //recyclerView.setAdapter(adapter);


                        }catch(JSONException ex){
                            ex.printStackTrace();
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
                Toast.makeText(RattingActivity.this, message, Toast.LENGTH_LONG).show();
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

    private void sendRatingMitra(){

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        String rattingbar = String.valueOf(ratingBar.getRating());
        String komen = eKomenUser.getText().toString();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrx);
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
                                Toast.makeText(RattingActivity.this, msgsuc, Toast.LENGTH_SHORT).show();
                            } else {
                                //String konStatus = response.getString("status");
                                String msgelse = response.getString("message");
                                Toast.makeText(RattingActivity.this, msgelse, Toast.LENGTH_SHORT).show();
                            }
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
}
