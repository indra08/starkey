package id.starkey.pelanggan.EditFotoProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.RequestHandler;

import id.starkey.pelanggan.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditFotoActivity extends AppCompatActivity implements View.OnClickListener{

    private String tokennyaUser, tampungUrlImageUser;
    private int REQUEST_CAMERA = 0;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Bitmap bitmap;
    private Uri filePath;
    private CircularImageView circularImageView;
    private Button bUpload, bSelesai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_foto);

        //init btn
        bUpload = findViewById(R.id.btnUpload);
        bUpload.setOnClickListener(this);
        bSelesai = findViewById(R.id.btnDoneEditFoto);
        bSelesai.setOnClickListener(this);

        //init circularimage
        circularImageView = findViewById(R.id.imageViewProfil);
        circularImageView.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        //get url image from pref
        //getPrefPicture();
        //get token from pref
        getPref();
    }

    private void getPref(){
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        //idnyaUser = custDetails.getString("idUser", "");
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    /*
    private void getPrefPicture(){
        SharedPreferences imageUser = getSharedPreferences(ConfigLink.imagePref, MODE_PRIVATE);
        String urlImageUser = imageUser.getString("imgUser","");

        tampungUrlImageUser = urlImageUser;
        if (tampungUrlImageUser.equals("")){
            circularImageView.setBackgroundResource(R.drawable.ic_add_large);
        } else {
            Picasso.with(EditFotoActivity.this).load(tampungUrlImageUser)
                    .into(circularImageView);
        }
    }
     */


    @Override
    public void onClick(View v) {
        if (v == bUpload){
            uploadFoto();
        }

        if (v == bSelesai){
            Intent jumpMain = new Intent(EditFotoActivity.this, MainActivity.class);
            startActivity(jumpMain);
            finishAffinity();
        }

        if (v == circularImageView){
            showFileChooser();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                circularImageView.setImageBitmap(bitmap);
                bUpload.setVisibility(View.VISIBLE);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    private void uploadFoto(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        String encodedImageData =getEncoded64ImageStringFromBitmap(bitmap);
        //String image = getStringImage(bitmap);
        //Log.d("stringGambar", encodedImageData);
        //Log.d("stringGambar", "data:image/png;base64,"+image);


        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("profile_photo", "data:image/png;base64,"+imageFix);
        params.put("profile_photo", "data:image/png;base64,"+encodedImageData);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.PATCH, ConfigLink.upload_foto, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String hasil = response.getString("message");
                            Toast.makeText(EditFotoActivity.this, hasil, Toast.LENGTH_SHORT).show();

                            JSONObject dataImage = response.getJSONObject("data");
                            String urlGambar = dataImage.getString("profile_photo");
                            Log.d("urlPic", urlGambar);

                            saveImageUser(urlGambar);
                            bUpload.setVisibility(View.INVISIBLE);
                            bSelesai.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                error.printStackTrace();
                bUpload.setVisibility(View.INVISIBLE);
                bSelesai.setVisibility(View.VISIBLE);

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
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }
        };

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

    private void saveImageUser(String image_url){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString("imgUser", image_url);
        editor.putString("fotoprofilUser", image_url);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backHome = new Intent(EditFotoActivity.this, MainActivity.class);
        startActivity(backHome);
        finishAffinity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(EditFotoActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(EditFotoActivity.this, "Permission denied to access your camera", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
