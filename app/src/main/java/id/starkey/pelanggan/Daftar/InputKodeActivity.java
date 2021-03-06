package id.starkey.pelanggan.Daftar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.Login.LoginActivity;
import id.starkey.pelanggan.RequestHandler;

import id.starkey.pelanggan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InputKodeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bNext;
    private String noHp;
    private EditText eVerifKode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_kode);

        //init btn
        bNext = findViewById(R.id.submit);
        bNext.setOnClickListener(this);

        //init et
        eVerifKode = findViewById(R.id.etVerifKode);

        //get params
        Bundle bundle = getIntent().getExtras();
        noHp = bundle.getString("hpne");
    }

    @Override
    public void onClick(View v) {
        /*
        if (v == bNext){
            Intent intent = new Intent(InputKodeActivity.this, FormDaftarActivity.class);
            startActivity(intent);
            finish();
        }
         */
        cekVerifSms();

    }

    private void cekVerifSms() {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        final String verifKode = eVerifKode.getText().toString();

        //final String URL = "http://103.28.52.123/api/verify_code";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", noHp);
        params.put("verify_code", verifKode);

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.verifyCode, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                JSONObject dataJO = new JSONObject();
                                dataJO = response.getJSONObject("data");
                                String idnya = dataJO.getString("user_id");
                                //Toast.makeText(InputKodeActivity.this, idnya, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(InputKodeActivity.this, FormDaftarActivity.class);
                                intent.putExtra("iduser",idnya );
                                startActivity(intent);
                                finish();
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(InputKodeActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                //error.printStackTrace();
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
        });

        int socketTimeout = 30000; //30 detik
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentback = new Intent(InputKodeActivity.this, LoginActivity.class);
        startActivity(intentback);
        finish();
    }
}
