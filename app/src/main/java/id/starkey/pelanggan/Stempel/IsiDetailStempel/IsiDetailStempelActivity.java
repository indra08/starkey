package id.starkey.pelanggan.Stempel.IsiDetailStempel;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Stempel.EnlargeImgStempel.EnlargeImgStempelActivity;
import id.starkey.pelanggan.Stempel.ReviewStempel.ReviewStempelActivity;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.himanshusoni.quantityview.QuantityView;

public class IsiDetailStempelActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bLanjutStempel;
    private String tokennyaUser, gambarnyaStempel = "";
    private QuantityView quantityView;
    private MaterialSpinner spinnerStempel, spinnerUkuranStempel;
    private String sValueJenis, sValueUkuran, sAlamatCust;
    private EditText etKet;
    private ImageView imgStempel;
    private double latUs, lngUs;

    //Array spinner jenis stempel
    private ArrayList<String> jenisLayananStempel;

    //array for gambar stempel
    private ArrayList<String> urlGambarStempel;

    //Array spinner ukuran stempel
    private ArrayList<String> ukuranStempel;

    JSONObject jsonBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_detail_stempel);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarIsiDetailStempel);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        setTitle("Stempel");

        //get params from maps (alamat)
        Bundle bundle = getIntent().getExtras();
        sAlamatCust = bundle.getString("alamatStemp");
        latUs = bundle.getDouble("latnyaStemp");
        lngUs = bundle.getDouble("lngnyaStemp");

        //init imageview
        imgStempel = findViewById(R.id.imgViewStempel);
        imgStempel.setOnClickListener(this);

        //init qt view
        quantityView = findViewById(R.id.qvStempel);

        //init btn
        bLanjutStempel = findViewById(R.id.btnLanjutkanStempel);
        bLanjutStempel.setOnClickListener(this);

        //init edit text
        etKet = findViewById(R.id.etKeteranganStemp);

        jenisLayananStempel = new ArrayList<>();
        urlGambarStempel = new ArrayList<>();
        ukuranStempel = new ArrayList<>();

        //init spinner
        spinnerStempel = findViewById(R.id.spinnerJenisStempel);
        spinnerUkuranStempel = findViewById(R.id.spinnerUkuranStempel);

        spinnerStempel.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String valuename = item.toString();
                sValueJenis = valuename.replaceAll(" ", "%20");

                // for image
                int indxGambar = spinnerStempel.getSelectedIndex();
                //String gambarnya = urlGambarStempel.get(indxGambar);
                gambarnyaStempel = urlGambarStempel.get(indxGambar);
                Log.d("cobagambarstempel", gambarnyaStempel);
                if (gambarnyaStempel.isEmpty()){
                    //imgStempel.setBackgroundResource(R.drawable.default_stamp);
                } else {
                    Picasso.with(IsiDetailStempelActivity.this)
                            .load(gambarnyaStempel)
                            .placeholder(R.drawable.progress_animation)
                            .into(imgStempel);
                }
            }
        });

        spinnerUkuranStempel.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                sValueUkuran = item.toString();
            }
        });

        //get id from pref
        getPref();
        getJenisStempel();
        getUkuranStempel();

    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    private void getJenisStempel(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ConfigLink.jenis_stempel, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        try{
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int x=0; x<jsonArray.length(); x++){

                                JSONObject obj = jsonArray.getJSONObject(x);
                                //int id   = parseInt(obj.getString("id"));
                                String nama = obj.getString("name");
                                String urlGambar = obj.getString("gambar");
                                //Log.d("RESPONE", nama);
                                jenisLayananStempel.add(nama);
                                urlGambarStempel.add(urlGambar);
                            }
                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                        spinnerStempel.setItems(jenisLayananStempel);
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

    private void getUkuranStempel(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ConfigLink.ukuran_stempel, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        try{
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int x=0; x<jsonArray.length(); x++){

                                JSONObject obj = jsonArray.getJSONObject(x);
                                //int id   = parseInt(obj.getString("id"));
                                String nama = obj.getString("name");
                                //Log.d("RESPONE", nama);
                                ukuranStempel.add(nama);
                            }
                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                        spinnerUkuranStempel.setItems(ukuranStempel);
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
        if (v == bLanjutStempel){

            //params
            String namastemp = sValueJenis;
            String ukstemp = sValueUkuran;
            int qtystemp = quantityView.getQuantity();
            String qty = String.valueOf(qtystemp);
            String keterangan = etKet.getText().toString();

            if (namastemp == null || ukstemp == null){
                Toast.makeText(this, "Silahkan pilih jenis dan ukuran stempel", Toast.LENGTH_SHORT).show();
            } else {
                //intent
                Intent toreview = new Intent(IsiDetailStempelActivity.this, ReviewStempelActivity.class);
                toreview.putExtra("jenisstemp", namastemp);
                toreview.putExtra("ukuranstemp", ukstemp);
                toreview.putExtra("latStemp", latUs);
                toreview.putExtra("lngStemp", lngUs);
                toreview.putExtra("qtystemp", qty);
                toreview.putExtra("ketstemp", keterangan);
                toreview.putExtra("alamatStemp", sAlamatCust);
                startActivity(toreview);
            }
            //Log.d("lanjutstemp", sAlamatCust);
        }
        if (v == imgStempel){
            if (gambarnyaStempel.isEmpty()){

            } else {
                //Toast.makeText(this, gambarnyaStempel, Toast.LENGTH_SHORT).show();
                Intent toEnlargeImg = new Intent(IsiDetailStempelActivity.this, EnlargeImgStempelActivity.class);
                toEnlargeImg.putExtra("gambarstempel", gambarnyaStempel);
                startActivity(toEnlargeImg);
            }

        }
    }
}
