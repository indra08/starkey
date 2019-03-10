package id.starkey.pelanggan.HomeMenuJasaLain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import id.starkey.pelanggan.HomeMenuJasaLain.Adapter.ListHistoryJLAdapter;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ItemValidation;
import id.starkey.pelanggan.Utilities.SessionManager;

public class TransaksiJasaLain extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private ListView lvHistory;
    private ProgressBar pbLoading;
    private List<CustomItem> masterList = new ArrayList<>();
    private ListHistoryJLAdapter adapter;
    private int start = 0, count = 10;
    private boolean isLoading = false;
    private View footerList;
    private String idUser = "";
    private SessionManager session;
    private TabLayout tbStatus;
    private JSONArray jStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_jasa_lain);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Transaksi Jasa Lain");
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        context = this;
        session = new SessionManager(context);

        initUI();
        initEvent();
        getData();
    }

    private void initUI() {

        lvHistory = (ListView) findViewById(R.id.lv_history);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        tbStatus = (TabLayout) findViewById(R.id.tb_status);

        masterList = new ArrayList<>();
        isLoading = false;
        start = 0;
        idUser = session.getID();
        jStatus = new JSONArray();
        jStatus.put("1");
        jStatus.put("2");
        jStatus.put("3");
        jStatus.put("4");

        lvHistory.addFooterView(footerList);
        adapter = new ListHistoryJLAdapter((Activity) context, masterList);
        lvHistory.removeFooterView(footerList);

        lvHistory.setAdapter(adapter);

        lvHistory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvHistory.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvHistory.getLastVisiblePosition() >= total - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        getData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, ReviewMitraJasaLain.class);
                intent.putExtra("id", item.getItem1());
                intent.putExtra("status", item.getItem7());
                startActivity(intent);
            }
        });
    }

    private void initEvent() {

        tbStatus.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //proses
                if(tab.getPosition() == 0) {

                    jStatus = new JSONArray();
                    jStatus.put("1");
                    jStatus.put("2");
                    jStatus.put("3");
                    jStatus.put("4");
                    start = 0;
                    getData();
                }else if(tab.getPosition() == 1){

                    start = 0;
                    jStatus = new JSONArray();
                    jStatus.put("5");
                    jStatus.put("6");
                    jStatus.put("7");
                    getData();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getData(){

        isLoading = true;
        lvHistory.addFooterView(footerList);

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("keyword","");
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", "");
            jBody.put("id_toko", "");
            jBody.put("id_user", idUser);
            jBody.put("datestart", "");
            jBody.put("dateend", "");
            jBody.put("status", jStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.getTransaksi
                , jBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        lvHistory.removeFooterView(footerList);
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
                                            ,jo.getString("insert_at")
                                            ,jo.getString("nama_toko")
                                            ,jo.getString("total")
                                            ,iv.parseNullString(jo.getString("state"))
                                            ,jo.getString("keterangan")
                                            ,jo.getString("status")
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
                lvHistory.removeFooterView(footerList);
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
