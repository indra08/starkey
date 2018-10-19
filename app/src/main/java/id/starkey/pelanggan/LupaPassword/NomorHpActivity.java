package id.starkey.pelanggan.LupaPassword;

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
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.RequestHandler;

import id.starkey.pelanggan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NomorHpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText tPhone;
    private Button bLanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomor_hp);

        //init et
        tPhone = findViewById(R.id.etNoHp);

        //init btn
        bLanjut = findViewById(R.id.btnLanjut);
        bLanjut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == bLanjut){
            String ceknomor = tPhone.getText().toString();
            if (ceknomor.isEmpty()){
                Toast.makeText(this, "Silahkan masukkan nomor hp", Toast.LENGTH_SHORT).show();
            } else {
                if (ceknomor.startsWith("0") || ceknomor.startsWith("62")){
                    Toast.makeText(this, "Nomor hp tanpa diawali angka 0 atau 62", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(this, "eksekusi API", Toast.LENGTH_SHORT).show();
                    resetCode();
                }
            }

        }
    }

    private void resetCode(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        final String hp = tPhone.getText().toString();

        //final String URL = "http://103.28.52.123/api/send_code";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", hp);

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.password_reset_code, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responSMS", response.toString());
                        try {
                            //Process os success response
                            loading.dismiss();
                            String hasil = response.getString("message");
                            Toast.makeText(NomorHpActivity.this, hasil, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(NomorHpActivity.this, VerifCodeActivity.class);
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
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }
}
