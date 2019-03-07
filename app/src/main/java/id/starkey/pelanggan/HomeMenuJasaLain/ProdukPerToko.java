package id.starkey.pelanggan.HomeMenuJasaLain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.HomeMenuJasaLain.Adapter.ListProdukAdapter;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ItemValidation;

public class ProdukPerToko extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private List<CustomItem> masterList = new ArrayList<>();
    private ListView lvProduk;
    private EditText edtSearch;
    private String keyword = "";
    private int start = 0, count = 10;
    private View footerList;
    private ListProdukAdapter adapter;
    private boolean isLoading = false;
    private String idToko;
    private String title;
    private Button btnOrder;
    private TextView tvTotal;
    private double totalHarga;
    private String lati = "", longi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_per_toko);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Daftar Produk");
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        context = this;
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        lvProduk = (ListView) findViewById(R.id.lv_produk);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        btnOrder = (Button) findViewById(R.id.btn_order);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        keyword = "";
        start = 0;
        masterList = new ArrayList<>();
        isLoading = false;

        lvProduk.addFooterView(footerList);
        adapter = new ListProdukAdapter((Activity) context, masterList);
        lvProduk.removeFooterView(footerList);
        lvProduk.setAdapter(adapter);

        lvProduk.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvProduk.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvProduk.getLastVisiblePosition() >= total - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        initData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            idToko = bundle.getString("id", "");
            title = bundle.getString("nama", "");
            lati = bundle.getString("lat", "");
            longi = bundle.getString("long", "");
            getSupportActionBar().setSubtitle(title);
            updateHarga();
        }
    }

    private void initEvent(){

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtSearch.getText().toString();
                    start = 0;
                    masterList.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DeskripsiOrder.class);
                DeskripsiOrder.listOrder.clear();

                List<CustomItem> itemUpdated = adapter.getItems();
                for(CustomItem item: itemUpdated){

                    if(item.getBool1()){
                        DeskripsiOrder.listOrder.add(new CustomItem(
                                item.getItem1()
                                ,item.getItem2()
                                ,item.getItem3()
                        ));
                    }
                }
                intent.putExtra("idtoko", idToko);
                intent.putExtra("toko", title);
                intent.putExtra("lat", lati);
                intent.putExtra("long", longi);
                startActivity(intent);
            }
        });
    }

    private void initData(){

        isLoading = true;
        lvProduk.addFooterView(footerList);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("keyword", keyword);
        params.put("start", String.valueOf(start));
        params.put("count", String.valueOf(count));
        params.put("id", "");
        params.put("id_toko", idToko);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.getprodukJasaLain
                , new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        lvProduk.removeFooterView(footerList);
                        isLoading = false;
                        String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                        if(start == 0) masterList.clear();

                        try {
                            String status = response.getJSONObject("metadata").getString("status");
                            message = response.getJSONObject("metadata").getString("message");

                            if (status.equals("200")){

                                JSONArray ja = response.getJSONArray("response");
                                for(int i = 0; i < ja.length(); i++){

                                    JSONObject jo = ja.getJSONObject(i);
                                    masterList.add(new CustomItem(
                                            jo.getString("id")
                                            ,jo.getString("produk")
                                            ,jo.getString("harga")
                                            ,jo.getString("path") + jo.getString("image")
                                            ,false
                                    ));
                                }
                            }else{
                                if(start == 0) Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
                lvProduk.removeFooterView(footerList);
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

    public void updateHarga(){

        List<CustomItem> itemUpdated = adapter.getItems();
        totalHarga = 0;
        for(CustomItem item: itemUpdated){

            if(item.getBool1()){

                totalHarga += iv.parseNullDouble(item.getItem3());
            }
        }

        tvTotal.setText(iv.ChangeToRupiahFormat(totalHarga));
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
