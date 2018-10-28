package id.starkey.pelanggan.OMCustomerService;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import id.starkey.pelanggan.BuildConfig;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Utilities.ItemValidation;
import id.starkey.pelanggan.Utilities.SessionManager;

public class OMCustomerService extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private EditText edtNama, edtNotelp, edtKeterangan;
    private Button btnKirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omcustomer_service);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        context = this;
        session = new SessionManager(context);

        initUI();
        initEvent();
    }

    private void initUI() {

        edtNama = (EditText) findViewById(R.id.edt_nama);
        edtNotelp = (EditText) findViewById(R.id.edt_notelp);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);
        btnKirim = (Button) findViewById(R.id.btn_kirim);

        edtNama.setText(session.getNama());
        edtNotelp.setText(session.getPhone());
    }

    private void initEvent(){

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtNama.getText().toString().isEmpty()){

                    edtNama.setError("Nama harap diisi");
                    edtNama.requestFocus();
                    return;
                }else{

                    edtNama.setError(null);
                }

                if(edtNotelp.getText().toString().isEmpty()){

                    edtNotelp.setError("Telepon harap diisi");
                    edtNotelp.requestFocus();
                    return;
                }else{

                    edtNotelp.setError(null);
                }

                if(edtKeterangan.getText().toString().isEmpty()){

                    edtKeterangan.setError("Keterangan harap diisi");
                    edtKeterangan.requestFocus();
                    return;
                }else{

                    edtKeterangan.setError(null);
                }

                saveData();
            }
        });
    }

    private void saveData(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Menyimpan...");
        loading.setCancelable(false);
        loading.show();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_user", session.getID());
        params.put("nama", edtNama.getText().toString());
        params.put("nomor", edtNotelp.getText().toString());
        params.put("keterangan", edtKeterangan.getText().toString());

        String versionName = BuildConfig.VERSION_NAME;
        String jenisApp = "customer";

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.saveCustomerService
                , new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();

                        String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                        try {
                            String status = response.getString("status");
                            message = response.getString("message");

                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            if (status.equals("success")){

                                onBackPressed();
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
        });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
