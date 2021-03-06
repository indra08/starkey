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
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.R;
import com.squareup.picasso.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class InputHpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bNext;
    private EditText eNomorHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_hp);

        //init btn
        bNext = findViewById(R.id.btnLanjutkanNoHp);
        bNext.setOnClickListener(this);

        //init et
        eNomorHp = findViewById(R.id.etNomorHp);
    }

    @Override
    public void onClick(View v) {
        if (v == bNext){
            String ceknomor = eNomorHp.getText().toString();
            if (ceknomor.isEmpty()){
                Toast.makeText(this, "Silahkan masukkan nomor hp", Toast.LENGTH_SHORT).show();
            } else {
                if (ceknomor.startsWith("0") || ceknomor.startsWith("62")){
                    Toast.makeText(this, "Nomor hp tanpa diawali angka 0 atau 62", Toast.LENGTH_SHORT).show();
                } else {
                    sendCode();
                }
            }
            //sendCode();

        }
    }

    private void sendCode(){

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        final String hp = eNomorHp.getText().toString();

        //final String URL = "http://103.28.52.123/api/send_code";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", hp);

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.sendCode, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String hasil = response.getString("message");
                            Toast.makeText(InputHpActivity.this, hasil, Toast.LENGTH_SHORT).show();

                            //jump to next
                            Intent intent = new Intent(InputHpActivity.this, InputKodeActivity.class);
                            intent.putExtra("hpne", hp);
                            startActivity(intent);
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
        id.starkey.pelanggan.RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }
}
