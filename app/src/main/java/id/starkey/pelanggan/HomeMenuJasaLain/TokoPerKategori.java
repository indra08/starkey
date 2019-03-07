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
import android.widget.AdapterView;
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
import id.starkey.pelanggan.HomeMenuJasaLain.Adapter.ListTokoAdapter;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ItemValidation;

public class TokoPerKategori extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private List<CustomItem> masterList = new ArrayList<>();
    private ListView lvToko;
    private EditText edtSearch;
    private String keyword = "";
    private int start = 0, count = 10;
    private View footerList;
    private ListTokoAdapter adapter;
    private boolean isLoading = false;
    private String idKategori;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toko_per_kategori);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Kategori Jasa");
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        context = this;
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        lvToko = (ListView) findViewById(R.id.lv_toko);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        keyword = "";
        start = 0;
        masterList = new ArrayList<>();
        isLoading = false;

        lvToko.addFooterView(footerList);
        adapter = new ListTokoAdapter((Activity) context, masterList);
        lvToko.removeFooterView(footerList);
        lvToko.setAdapter(adapter);

        lvToko.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvToko.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvToko.getLastVisiblePosition() >= total - threshold && !isLoading) {

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

        lvToko.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, ProdukPerToko.class);
                intent.putExtra("id", item.getItem1());
                intent.putExtra("nama", item.getItem2());
                intent.putExtra("lat", item.getItem6());
                intent.putExtra("long", item.getItem7());
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            idKategori = bundle.getString("id", "");
            title = bundle.getString("title", "");
            getSupportActionBar().setSubtitle(title);

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
    }

    private void initData(){

        isLoading = true;
        lvToko.addFooterView(footerList);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("keyword",keyword);
        params.put("start", String.valueOf(start));
        params.put("count", String.valueOf(count));
        params.put("kategori", idKategori);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.getTokoJasaLain
                , new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        lvToko.removeFooterView(footerList);
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
                                            ,jo.getString("nama")
                                            ,jo.getString("alamat")
                                            ,jo.getString("telpon")
                                            ,jo.getString("image")
                                            ,jo.getString("latitude")
                                            ,jo.getString("longitude")
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
                lvToko.removeFooterView(footerList);
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
