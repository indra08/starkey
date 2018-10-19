package id.starkey.pelanggan.Home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import id.starkey.pelanggan.ConfigLink;
import id.starkey.pelanggan.Kunci.TrxKunci.TrxKunciActivity;
import id.starkey.pelanggan.Login.LoginActivity;
import id.starkey.pelanggan.Maps.MapsActivity;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.RequestHandler;
import id.starkey.pelanggan.Stempel.TrxStempel.TrxStempelActivity;
import id.starkey.pelanggan.Utilities.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dani on 2/16/2018.
 */

public class FragmentHome extends Fragment implements View.OnClickListener {

    public FragmentHome(){}

    private ImageView bKunci, bLaundry, bStempel;
    private View vMenu;
    private Context mContext;
    private GPSTracker gps;
    double latPerm, lngPerm;
    private String tokennyaUser, sFirebaseToken;

    //interval
    //Handler h = new Handler();
    //int delay = 5*1000; //1 second=1000 milisecond, 5*1000=5seconds
    //Runnable runnable;

    //interval
    private Handler h = new Handler();
    private int delay = 5*1000; ////1 second=1000 milisecond, 5*1000=5seconds
    private Runnable runnable;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vMenu = inflater.inflate(R.layout.fragment_home, container, false);

        mContext = getActivity();

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            //Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
            gps = new GPSTracker(mContext, getActivity());

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                // \n is for new line
                //Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                latPerm = latitude;
                lngPerm = longitude;
                //Toast.makeText(getActivity(),"LOKASIMU" + latPerm + lngPerm, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }

        //init btn
        bKunci = vMenu.findViewById(R.id.btnKunci);
        bLaundry = vMenu.findViewById(R.id.btnLaundry);
        bStempel = vMenu.findViewById(R.id.btnStempel);
        bKunci.setOnClickListener(this);
        bLaundry.setOnClickListener(this);
        bStempel.setOnClickListener(this);

        //get token user from pref
        getPref();
        getFirebaseToken();

        updatePosisiUser(sFirebaseToken);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable = this;
                h.postDelayed(runnable, delay);
                updatePosisiUser(sFirebaseToken);
            }
        }, delay);

        return vMenu;
    }

    private void getPref() {
        SharedPreferences custDetails = getActivity().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    private void getFirebaseToken(){
        //SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences tokenfirebaseuser = getActivity().getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        sFirebaseToken = tokenfirebaseuser.getString("firebaseUser", "");
    }

    @Override
    public void onDestroy() {
        h.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == bKunci){
            //Toast.makeText(getActivity(), "Kunci", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            String sLat = String.valueOf(latPerm);
            String sLng = String.valueOf(lngPerm);
            intent.putExtra("parBtn","Kunci");
            intent.putExtra("lat", sLat);
            intent.putExtra("lng", sLng);
            startActivity(intent);
        } else if (view == bLaundry){
            Toast.makeText(getActivity(), "Laundry still under construction", Toast.LENGTH_SHORT).show();
            /*
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            String sLat = String.valueOf(latPerm);
            String sLng = String.valueOf(lngPerm);
            intent.putExtra("parBtn","Laundry");
            intent.putExtra("lat", sLat);
            intent.putExtra("lng", sLng);
            startActivity(intent);
             */

        } else {
            //Toast.makeText(getActivity(), "Stample still under construction", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), MapsActivity.class);
            String sLat = String.valueOf(latPerm);
            String sLng = String.valueOf(lngPerm);
            intent.putExtra("parBtn","Stempel");
            intent.putExtra("lat", sLat);
            intent.putExtra("lng", sLng);
            startActivity(intent);
        }
    }

    private void clearAttributeUser(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(ConfigLink.loginPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void updatePosisiUser(String fbaseToken){
        /*
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();
         */


        String latString = String.valueOf(latPerm);
        String lngString = String.valueOf(lngPerm);

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lat", latString);
        params.put("long", lngString);
        params.put("firebase_token", fbaseToken);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.PATCH, ConfigLink.update_location_user, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("posisiUser", response.toString());
                        //loading.dismiss();
                        try {
                            //Process os success response
                            String hasil = response.getString("message");
                            //Toast.makeText(mContext, hasil, Toast.LENGTH_SHORT).show();
                            if (hasil.equals("Akun tidak ditemukan")){
                                clearAttributeUser();
                                Intent login = new Intent(getActivity(), LoginActivity.class);
                                startActivity(login);
                                getActivity().finish();
                            } else if(hasil.equals("Maaf, akun anda belum diaktivasi")){ //status 0
                                clearAttributeUser();
                                Toast.makeText(mContext, hasil, Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(getActivity(), LoginActivity.class);
                                startActivity(login);
                                getActivity().finish();
                            } else if(hasil.equals("Maaf, user anda disuspend permanen")){ //status 2
                                clearAttributeUser();
                                Toast.makeText(mContext, hasil, Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(getActivity(), LoginActivity.class);
                                startActivity(login);
                                getActivity().finish();
                            } else if(hasil.equals("Akun anda belum diaktivasi")){
                                clearAttributeUser();
                                infoDeviceNotMatch(hasil);
                            } else if(hasil.equals("Akun anda dibanned permanen")){
                                clearAttributeUser();
                                infoDeviceNotMatch(hasil);
                            } else if(hasil.equals("Server sedang dalam proses pemeliharaan")){
                                infoDeviceNotMatch(hasil);
                            } else if (hasil.equals("Token tidak valid, harap login ulang")){
                                infoDeviceNotMatch(hasil);
                                h.removeCallbacksAndMessages(null);
                            } else {
                                cekTrxOutstandingUser();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                error.printStackTrace();

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
                Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();
            }
        }){
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
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request_json);
    }

    private void infoDeviceNotMatch(String titleMsg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(titleMsg)
                .setCancelable(false)
                .setPositiveButton("Oke",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                clearAttributeUser();
                                Intent aw = new Intent(mContext, LoginActivity.class);
                                startActivity(aw);
                                getActivity().finish();
                                //turnGPSOn();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    /*
    private void infoDeviceNotMatch(String titleMsg){

    }
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    gps = new GPSTracker(mContext, getActivity());

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line
                        //Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void cekTrxOutstandingUser(){
        /*
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();
         */


        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.transaksi_checker, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        Log.d("outUser", response.toString());
                        try {
                            JSONObject joData = response.getJSONObject("data");

                            if (joData.length() != 0){
                                String kondisiidlayanan = joData.getString("id_layanan");
                                if (kondisiidlayanan.equals("1")){// transaksi kunci

                                    Intent toTrxKunci = new Intent(getActivity(), TrxKunciActivity.class);
                                    toTrxKunci.putExtra("message", joData.toString());
                                    startActivity(toTrxKunci);
                                    getActivity().finish();

                                } else if (kondisiidlayanan.equals("2")){//transaksi stempel

                                    Intent toTrxStempel = new Intent(getActivity(), TrxStempelActivity.class);
                                    toTrxStempel.putExtra("messageStempel", joData.toString());
                                    startActivity(toTrxStempel);
                                    getActivity().finish();
                                }

                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                error.printStackTrace();

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
                Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();
            }
        }){
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
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request_json);
    }
}
