package id.starkey.pelanggan.HomeMenuJasaLain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.widget.RelativeLayout;
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
import id.starkey.pelanggan.HomeMenuJasaLain.Adapter.KategoriJasaAdapter;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ItemValidation;
import id.starkey.pelanggan.Utilities.SessionManager;

public class KategoriJasaLain extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private RecyclerView rvKategoriJasa;
    private List<CustomItem> masterList = new ArrayList<>();
    private int columnTable = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori_jasa_lain);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Kategori Jasa");
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        context = this;
        initUI();
    }

    private void initUI() {

        rvKategoriJasa = (RecyclerView) findViewById(R.id.rv_kategori);
        masterList = new ArrayList<>();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("keyword","");
        params.put("start", "");
        params.put("count", "");

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.getKategoriJasaLain
                , new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                        masterList.clear();
                        try {
                            String status = response.getJSONObject("metadata").getString("status");
                            message = response.getJSONObject("metadata").getString("message");

                            if (status.equals("200")){

                                JSONArray ja = response.getJSONArray("response");
                                for(int i = 0; i < ja.length(); i++){

                                    JSONObject jo = ja.getJSONObject(i);
                                    masterList.add(new CustomItem(
                                            jo.getString("id")
                                            ,jo.getString("kategori")
                                            ,jo.getString("path") + jo.getString("image")));
                                }
                            }else{
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException ex){
                            ex.printStackTrace();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }

                        setKategoriAdapter();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                setKategoriAdapter();
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

    private void setKategoriAdapter(){

        rvKategoriJasa.setAdapter(null);

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            // this is why the minimal sdk must be JB
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(size);
            }else {
                display.getSize(size);
            }
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }

        int menuWidth = 0;
        double menuFloat = (size.x ) / columnTable;
        menuWidth = (int) menuFloat;

        int jmlBaris = (int)(Math.ceil((double) masterList.size() / columnTable));
        KategoriJasaAdapter menuAdapter = new KategoriJasaAdapter(context, masterList, menuWidth);

        rvKategoriJasa.setLayoutParams(new RelativeLayout.LayoutParams(rvKategoriJasa.getLayoutParams().width, (((size.x) / columnTable * jmlBaris))));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, columnTable);
        rvKategoriJasa.setLayoutManager(mLayoutManager);
        rvKategoriJasa.setItemAnimator(new DefaultItemAnimator());
        rvKategoriJasa.setAdapter(menuAdapter);
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
