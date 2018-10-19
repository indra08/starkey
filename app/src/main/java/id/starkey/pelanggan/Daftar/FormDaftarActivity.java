package id.starkey.pelanggan.Daftar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.Login.LoginActivity;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.RequestHandler;

import id.starkey.pelanggan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FormDaftarActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bRegis;
    private EditText etNamaDepan, etNamaBelakang, etEmail, etPass, etCPass;
    private String idUsernya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_daftar);

        //init btn
        bRegis = findViewById(R.id.submitRegis);
        bRegis.setOnClickListener(this);

        //init et
        etNamaDepan = findViewById(R.id.editTextNamaDepan);
        etNamaBelakang = findViewById(R.id.editTextNamaBelakang);
        etEmail = findViewById(R.id.editTextEmail);
        etPass = findViewById(R.id.editTextPassword);
        etCPass = findViewById(R.id.editTextCPassword);

        //get params
        Bundle bundle = getIntent().getExtras();
        idUsernya = bundle.getString("iduser");
    }

    @Override
    public void onClick(View v) {
        /*
        if (v == bRegis){
            Intent a = new Intent(FormDaftarActivity.this, InputFotoActivity.class);
            startActivity(a);
            finish();
        }
         */
        registrationDetail();
    }

    private void registrationDetail(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        final String namadepan = etNamaDepan.getText().toString();
        final String namabelakang = etNamaBelakang.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPass.getText().toString();
        final String cpassword = etCPass.getText().toString();

        //final String URL = "http://103.28.52.123/api/registration_detail";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id",idUsernya);
        params.put("first_name",namadepan);
        params.put("last_name",namabelakang);
        params.put("email",email);
        params.put("password",password);
        params.put("c_password",cpassword);

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.registrationDetail, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String hasil = response.getString("message");
                                Toast.makeText(FormDaftarActivity.this, hasil, Toast.LENGTH_SHORT).show();

                                JSONObject dataJO = new JSONObject();
                                dataJO = response.getJSONObject("data");
                                String id = dataJO.getString("id");
                                String nama = dataJO.getString("first_name");
                                String namabelakang = dataJO.getString("last_name");
                                String phone = dataJO.getString("phone");
                                String status = dataJO.getString("status");
                                String tokenId = dataJO.getString("token");

                                //saveAttributeUser(id, nama, namabelakang, phone, status, tokenId);
                                Intent toHome = new Intent(FormDaftarActivity.this, LoginActivity.class);
                                startActivity(toHome);
                                finishAffinity();


                                //flow lama
                                /*
                                //Intent a = new Intent(FormDaftarActivity.this, InputFotoActivity.class);
                                //startActivity(a);
                                //finish();
                                 */



                            } else {
                                String msg = response.getString("message");
                                Toast.makeText(FormDaftarActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    private void saveAttributeUser(String id, String nama, String namablkg, String phone, String status, String tokenId){
        //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.loginFromRegister, MODE_PRIVATE);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idUser", id);
        editor.putString("namaUser", nama);
        editor.putString("namablkgUser", namablkg);
        editor.putString("phoneUser", phone);
        editor.putString("statusUser", status);
        editor.putString("tokenIdUser", tokenId);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent b = new Intent(FormDaftarActivity.this, LoginActivity.class);
        startActivity(b);
        finish();
    }
}
