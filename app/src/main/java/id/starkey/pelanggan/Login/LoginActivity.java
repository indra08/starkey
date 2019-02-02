package id.starkey.pelanggan.Login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.Daftar.InputHpActivity;
import id.starkey.pelanggan.Firebase.MyFirebaseInstanceIdService;
import id.starkey.pelanggan.Firebase.SharedPrefManager;
import id.starkey.pelanggan.LupaPassword.NomorHpActivity;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.R;

import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Test.CobaSocketActivity;
import id.starkey.pelanggan.Utilities.RuntimePermissionsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends RuntimePermissionsActivity implements View.OnClickListener {

    private Button bLogin;
    private TextView tDaftar, tampungToken, tLupaPass;
    private EditText etEmail, etPass;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private static final int REQUEST_PERMISSIONS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ProgressDialog loadToken = new ProgressDialog(this);
        loadToken.setMessage("Otentifikasi...");
        loadToken.setCancelable(false);
        loadToken.show();

        //init textview
        tLupaPass = findViewById(R.id.txtLupaPassword);
        tLupaPass.setOnClickListener(this);
        tampungToken = findViewById(R.id.tvTampungTokenFb);
        context = this;
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                try {
                    tampungToken.setText(SharedPrefManager.getInstance(LoginActivity.this).getToken());

                }catch (Exception e){
                    e.printStackTrace();
                }
                loadToken.dismiss();
            }
        };

        if (SharedPrefManager.getInstance(this).getToken() != null) {
            tampungToken.setText(SharedPrefManager.getInstance(LoginActivity.this).getToken());
            loadToken.dismiss();
            Log.d("JALTOKENBRO",SharedPrefManager.getInstance(this).getToken());
        }

        FirebaseApp.initializeApp(context);

        try {

            unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {

            registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseInstanceIdService.TOKEN_BROADCAST));
        }catch (Exception e){
            e.printStackTrace();
        }

        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {

            LoginActivity.super.requestAppPermissions(new
                            String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WAKE_LOCK}, R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        //init btn
        bLogin = findViewById(R.id.btnLogin);
        bLogin.setOnClickListener(this);
        tDaftar = findViewById(R.id.txtDaftar);
        tDaftar.setOnClickListener(this);

        //init edit text
        etEmail = findViewById(R.id.edtUsername);
        etPass = findViewById(R.id.edtPassword);

        getPref();
        prefNotNull();


    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == bLogin){
            cekLogin();
        }
        if (v == tDaftar){
            //Toast.makeText(this, "Daftar Clicked", Toast.LENGTH_SHORT).show();
            Intent intDaftar = new Intent(LoginActivity.this, InputHpActivity.class);
            startActivity(intDaftar);
            finish();
        }
        if (v == tLupaPass){
            Intent intLupaPass = new Intent(LoginActivity.this, NomorHpActivity.class);
            startActivity(intLupaPass);
        }
    }

    private void getPref(){
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        String email = custDetails.getString("emailUser", "");
        etEmail.setText(email);
    }

    private void prefNotNull(){
        String cek1 = etEmail.getText().toString().trim();

        if (cek1.length() > 0){
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void cekLogin() {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        //String email = etEmail.getText().toString(); // user
        String usernya = etEmail.getText().toString();
        //String awaluser = usernya.substring(1);
        if (usernya.startsWith("0")){
            usernya = usernya.substring(1);
            Log.d("cekawal", usernya);
        }
        String pass = etPass.getText().toString();
        final String tokenfirebase = tampungToken.getText().toString();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user", usernya);
        params.put("password", pass);
        params.put("firebase_token", tokenfirebase);

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.login, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                //String cobatoken = response.getString("token");

                                JSONObject dataJO = new JSONObject();
                                dataJO = response.getJSONObject("data");
                                String id = dataJO.getString("id");
                                String nama = dataJO.getString("first_name");
                                String namablkg = dataJO.getString("last_name");
                                String phone = dataJO.getString("phone");
                                String email = dataJO.getString("email");
                                String profilfoto = dataJO.getString("profile_photo");
                                String token = dataJO.getString("token");

                                saveAttUser(id, nama, namablkg, phone, email, profilfoto, token);
                                saveFirebaseToken(tokenfirebase);


                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    private void saveAttUser(String id, String nama, String namablkg, String phone, String email, String profilfoto, String tokenId){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idUser", id);
        editor.putString("namaUser", nama);
        editor.putString("namablkgUser", namablkg);
        editor.putString("phoneUser", phone);
        editor.putString("emailUser", email);
        editor.putString("fotoprofilUser", profilfoto);
        editor.putString("tokenIdUser", tokenId);
        editor.commit();
    }

    private void saveFirebaseToken(String firebaseToken){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firebaseUser", firebaseToken);
        editor.commit();
    }
}
