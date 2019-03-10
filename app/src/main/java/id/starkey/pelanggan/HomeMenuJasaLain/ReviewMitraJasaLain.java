package id.starkey.pelanggan.HomeMenuJasaLain;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.HomeMenuJasaLain.Adapter.ListOrderJLAdapter;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ItemValidation;
import id.starkey.pelanggan.Utilities.SessionManager;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewMitraJasaLain extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private ListView lvOrder;
    private TextView tvToko, tvSubtotal, tvBiayaJemput, tvTotal, tvStatus, tvId, tvMitra, tvAlamat;
    private List<CustomItem> listOrder = new ArrayList<>();
    private ListOrderJLAdapter adapter;
    private MaterialRatingBar rbMitra;
    private EditText edtKomentar;
    private Button btnSimpan;
    private String idOrder = "", statusOrder = "";
    private String idToko = "";
    private CardView cvRating;
    private CircularImageView ciMitra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_mitra_jasa_lain);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Detail Order");
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        context = this;
        session = new SessionManager(context);
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvId = (TextView) findViewById(R.id.tv_id);
        tvMitra = (TextView) findViewById(R.id.tv_mitra);
        tvToko = (TextView) findViewById(R.id.tv_toko);
        tvAlamat = (TextView) findViewById(R.id.tv_alamat);
        lvOrder = (ListView) findViewById(R.id.lv_order);
        tvSubtotal = (TextView) findViewById(R.id.tv_subtotal);
        tvBiayaJemput = (TextView) findViewById(R.id.tv_biaya_jemput);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        adapter = new ListOrderJLAdapter((Activity) context, listOrder);
        lvOrder.setAdapter(adapter);
        rbMitra = (MaterialRatingBar) findViewById(R.id.rb_mitra);
        edtKomentar = (EditText) findViewById(R.id.edt_komentar);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        cvRating = (CardView) findViewById(R.id.cardViewRatingLive);
        ciMitra = (CircularImageView) findViewById(R.id.imageViewMitra);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idOrder = bundle.getString("id", "");
            statusOrder = bundle.getString("status", "");

            if(statusOrder.equals("5")){

                cvRating.setVisibility(View.VISIBLE);
                btnSimpan.setVisibility(View.VISIBLE);
            }else{
                cvRating.setVisibility(View.GONE);
                btnSimpan.setVisibility(View.GONE);
            }
        }
    }

    private void initEvent() {

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin myimpan reivew ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

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
        });
    }

    private void saveData() {

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Menyimpan data...");
        loading.setCancelable(false);
        loading.show();

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("rating", String.valueOf(rbMitra.getRating()));
            jBody.put("review", edtKomentar.getText().toString());
            jBody.put("username", session.getPhone());
            jBody.put("id_order", idOrder);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.saveReview
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

    private void initData() {


        HashMap<String, String> params = new HashMap<String, String>();

        params.put("id", idOrder);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.getTransaksi
                , new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                        listOrder.clear();

                        try {
                            String status = response.getJSONObject("metadata").getString("status");
                            message = response.getJSONObject("metadata").getString("message");

                            if (status.equals("200")){

                                JSONObject jHeader = response.getJSONObject("response").getJSONObject("header");
                                tvId.setText(jHeader.getString("id"));
                                tvStatus.setText(jHeader.getString("keterangan"));
                                if(!jHeader.getString("profile_photo").isEmpty()){
                                    Picasso.with(context)
                                            .load(jHeader.getString("profile_photo"))
                                            .placeholder(R.drawable.progress_animation)
                                            .into(ciMitra);
                                }
                                //tvMitra.setText(jHeader.getString(""));
                                tvToko.setText(jHeader.getString("nama_toko"));
                                tvAlamat.setText(jHeader.getString("state"));
                                tvSubtotal.setText(iv.ChangeToCurrencyFormat(jHeader.getString("subtotal")));
                                tvBiayaJemput.setText(iv.ChangeToCurrencyFormat(jHeader.getString("subtotal")));
                                tvSubtotal.setText(iv.ChangeToCurrencyFormat(jHeader.getString("total_ongkir")));
                                tvTotal.setText(iv.ChangeToCurrencyFormat(jHeader.getString("total")));

                                // Detail
                                JSONArray ja = response.getJSONObject("response").getJSONArray("detail");
                                for(int i = 0; i < ja.length(); i++){

                                    JSONObject jo = ja.getJSONObject(i);
                                    listOrder.add(new CustomItem(
                                            idOrder
                                            ,jo.getString("nama_produk")
                                            ,jo.getString("total")
                                    ));
                                }
                            }else{
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException ex){
                            ex.printStackTrace();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }

                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                adapter.notifyDataSetChanged();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
