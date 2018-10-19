package id.starkey.pelanggan.Kunci.IsiDetailKunci;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.Kunci.PilihKunci.PilihKunciActivity;
import id.starkey.pelanggan.Kunci.ReviewKunci.ReviewKunciActivity;
import id.starkey.pelanggan.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.himanshusoni.quantityview.QuantityView;

public class IsiDetailKunciActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img1, img2;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private String userChoosenTask, alamat1, tokennyaUser;
    private Bitmap bitmap;
    private Button bLanjutReview;
    private EditText etJenisKunci, etQty, etKeterangan;
    private double lat1, lng1;
    //param for estimasi harga
    private String id_item, qty;
    private String id_layanan_item = "1";

    JSONObject jsonBody;

    //Array untuk spinner
    private ArrayList<String> jenisLayanan;
    //Array for caption
    private ArrayList<String> captionJenisLayanan;

    //Deklarasi spinner
    private MaterialSpinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_detail_kunci);

        //init et
        etJenisKunci = findViewById(R.id.edittextJenisKunci);
        etJenisKunci.setHint("Pilih Kunci");
        etJenisKunci.setFocusable(false);
        etJenisKunci.setOnClickListener(this);

        etQty = findViewById(R.id.edittextQty);

        etKeterangan = findViewById(R.id.editTextKeterangan);

        //init bitmap
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add);

        //init btn
        bLanjutReview = findViewById(R.id.btnLanjutReview);
        bLanjutReview.setOnClickListener(this);

        //init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarIsiDetailKunci);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        setTitle("Kunci");

        jenisLayanan = new ArrayList<>();
        captionJenisLayanan = new ArrayList<>();

        mSpinner = findViewById(R.id.spinnerLayananKunci);

        mSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                int indexnya = position + 1;
                id_layanan_item = String.valueOf(indexnya);
                clearIdLayanan();
                saveIdLayanan(id_layanan_item);
                Log.d("jalidlayanane", id_layanan_item);
            }
        });


        //init iv
        img1 = findViewById(R.id.iv1);
        //img2 = findViewById(R.id.iv2);
        img1.setOnClickListener(this);
        //img2.setOnClickListener(this);

        //ActivityCompat.requestPermissions(IsiDetailKunciActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        //save default value idlayanan to pref
        saveIdLayanan(id_layanan_item);

        //get params from map
        Bundle bundle = getIntent().getExtras();
        alamat1 = bundle.getString("alamatnya");
        lat1 = bundle.getDouble("latnya");
        lng1 = bundle.getDouble("lngnya");

        //method get rak
        getLayananKunci();

        //get id from pref
        getPref();

    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    private void saveIdLayanan(String idlayanane){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.idLayananPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idlayananuser", idlayanane);
        editor.commit();
    }

    private void clearIdLayanan(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.idLayananPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void getLayananKunci() {

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ConfigLink.jenisLayanan, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        try{
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int x=0; x<jsonArray.length(); x++){

                                JSONObject obj = jsonArray.getJSONObject(x);
                                String nama = obj.getString("nama");
                                String caption = obj.getString("caption");
                                //jenisLayanan.add(nama);
                                captionJenisLayanan.add(caption);
                            }
                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                        //mSpinner.setItems(jenisLayanan);
                        mSpinner.setItems(captionJenisLayanan);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                //Log.e("TAG", error.getMessage(), error);
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

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }

        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    @Override
    public void onClick(View v) {
        if (v == img1){
            selectImage();
        }
        if (v == img2){
            selectImage();
        }
        if (v == bLanjutReview){
            String cekJenisKunci = etJenisKunci.getText().toString();
            String cekQty = etQty.getText().toString().trim();
            if (cekJenisKunci.isEmpty()){
                Toast.makeText(this, "Silahkan pilih jenis kunci", Toast.LENGTH_SHORT).show();
            } else if (cekQty.isEmpty() || cekQty.equals("0")){
                Toast.makeText(this, "Silahkan masukkan jumlah dengan benar", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "lanjut", Toast.LENGTH_SHORT).show();

                qty = etQty.getText().toString();
                //Log.d("cobaestimasi", id_layanan+" "+id_item+" "+id_layanan_item+" "+qty);

                //edit text keteranagn
                String cekKet = etKeterangan.getText().toString();

                String encodeImageData = getEncoded64ImageStringFromBitmap(bitmap);


                Intent in = new Intent(this, ReviewKunciActivity.class);
                in.putExtra("stringGambar", encodeImageData);
                in.putExtra("alamatKunci", alamat1);
                in.putExtra("latnya", lat1);
                in.putExtra("lngnya", lng1);
                //param layout
                in.putExtra("jenisKunci", cekJenisKunci);
                in.putExtra("qtyKunci", cekQty);
                in.putExtra("ketKunci", cekKet);
                //param estimasi harga
                in.putExtra("qtyHarga", qty);
                in.putExtra("id_itemHarga",id_item);
                in.putExtra("id_layanan_itemHarga", id_layanan_item);
                startActivity(in);

            }
        }
        if (v == etJenisKunci){

            Intent jumpJenis = new Intent(IsiDetailKunciActivity.this, PilihKunciActivity.class);
            //startActivity(jumpJenis);
            startActivityForResult(jumpJenis, 111);
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(IsiDetailKunciActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result= PermissionChecker.checkPermission(IsiDetailKunciActivity.this, CAMERA_SERVICE, 1,1);
                //boolean bSDisAvalaible = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                // boolean result=Utility.checkPermission(EmotionActivity.this);
                //boolean result = ActivityCompat.checkSelfPermission(IsiDetailKunciActivity.this,CAMERA_SERVICE);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    //if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    //if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }


            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //imageView.setImageBitmap(bm);
        img1.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //imageView.setImageBitmap(thumbnail);
        img1.setImageBitmap(thumbnail);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
                bitmap = ((BitmapDrawable)img1.getDrawable()).getBitmap();
                img1.setImageBitmap(bitmap);
            }
            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
                bitmap = ((BitmapDrawable)img1.getDrawable()).getBitmap();
                img1.setImageBitmap(bitmap);
            }
        }

        if (requestCode == 111){
            if (resultCode == 110){
                String nama_kunci = data.getStringExtra("kuncimobil");
                String id_itemnya = data.getStringExtra("iditemnya");
                etJenisKunci.setFocusable(true);
                etJenisKunci.setText(nama_kunci);
                etJenisKunci.setFocusable(false);
                id_item = id_itemnya;
            } else {
                Toast.makeText(this, "Anda belum memilih jenis kunci", Toast.LENGTH_SHORT).show();
            }
        }


        //flow awal
        /*
        if (requestCode == 111){
            if (resultCode == 110){
                String nama_kunci = data.getStringExtra("kunci");
                etJenisKunci.setFocusable(true);
                etJenisKunci.setText(nama_kunci);
                etJenisKunci.setFocusable(false);
            } else {
                Toast.makeText(this, "Anda belum memilih jenis kunci", Toast.LENGTH_SHORT).show();
            }
        }
         */

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
                    Toast.makeText(IsiDetailKunciActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(IsiDetailKunciActivity.this, "Permission denied to access your camera", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
