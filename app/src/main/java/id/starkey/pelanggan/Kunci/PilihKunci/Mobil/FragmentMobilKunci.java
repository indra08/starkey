package id.starkey.pelanggan.Kunci.PilihKunci.Mobil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import id.starkey.pelanggan.Kunci.IsiDetailKunci.IsiDetailKunciActivity;
import id.starkey.pelanggan.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dani on 3/15/2018.
 */

public class FragmentMobilKunci extends Fragment implements View.OnClickListener {
    public FragmentMobilKunci(){}

    View vmenu;
    private String tokennyaUser, defaultValueCombo, id_layanan;
    JSONObject jsonBody;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private List<ListItemKunciMobil> listItemKunciMobils;
    private Button bKunciMobil;
    private RVKunciMobil mAdapter;

    //Array untuk spinner
    private ArrayList<String> merkmobil;
    //Deklarasi spinner
    private MaterialSpinner mSpinner;

    private RelativeLayout relativeLayoutKosong, relativeLayoutAda;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vmenu = inflater.inflate(R.layout.fragment_mobil_kunci, container, false);

        //init rv kunci mobil
        recyclerView = vmenu.findViewById(R.id.recyclerViewKunciMobil);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        //init rlayout
        relativeLayoutKosong = vmenu.findViewById(R.id.layoutKosong);
        relativeLayoutAda = vmenu.findViewById(R.id.layoutAda);

        //init btn
        bKunciMobil = vmenu.findViewById(R.id.btnPilihKunciMobil);
        bKunciMobil.setOnClickListener(this);

        listItemKunciMobils = new ArrayList<>();

        merkmobil = new ArrayList<>();

        mSpinner = vmenu.findViewById(R.id.spinnerMerkMobil);

        mSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                //Snackbar.make(view, "Anda memilih merk "+item, Snackbar.LENGTH_SHORT).show();

                String mobilmerk = item.toString().replaceAll(" ", "%20");;
                getDetailMobil(mobilmerk, id_layanan);
            }
        });

        getIdLayananFromPref();

        defaultValueCombo = "all";
        getDetailMobil(defaultValueCombo, id_layanan);
        getComboMobil();
        getPrefTokenBearer();
        return vmenu;
    }

    private void getPrefTokenBearer(){
        SharedPreferences custDetails = getActivity().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    private void getIdLayananFromPref(){
        SharedPreferences idlayanane = getActivity().getSharedPreferences(ConfigLink.idLayananPref, MODE_PRIVATE);
        id_layanan = idlayanane.getString("idlayananuser", "");
    }

    private void getComboMobil(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ConfigLink.combo_mobil, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() == 0){

                        } else {

                            try{
                                response = response.getJSONObject("data");
                                JSONArray jsonArray = response.getJSONArray("merk");
                                for (int w = 0; w<jsonArray.length(); w++){

                                    JSONObject obj= jsonArray.getJSONObject(w);
                                    String nama = obj.getString("name");
                                    //int id   = parseInt(obj.getString("id"));
                                    merkmobil.add(nama);
                                }
                            }catch(JSONException ex){
                                ex.printStackTrace();
                            }
                            mSpinner.setItems(merkmobil);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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


        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //Adding request to the queue
        requestQueue.add(jsonObjectRequest);
    }

    private void getDetailMobil(String merknya, String layananid){
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

//        String URLMerkMobil = "https://api.starkey.id/api/kunci?category=mobil&merk="+merknya+"&sort=asc&keyword=";
        String URLMerkMobil = "https://api.starkey.id/api/v1.1/kunci?category=mobil&merk="+merknya+"&sort=asc&sub_category=all&id_layanan_kunci="+layananid;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URLMerkMobil, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("jalapianyar", response.toString());
                        listItemKunciMobils.clear();
                        loading.dismiss();
                            try{
                                JSONArray jsonArray = response.getJSONArray("data");
                                if (jsonArray.length() == 0){
                                    relativeLayoutKosong.setVisibility(View.VISIBLE);
                                    relativeLayoutAda.setVisibility(View.INVISIBLE);
                                } else {
                                    relativeLayoutKosong.setVisibility(View.INVISIBLE);
                                    relativeLayoutAda.setVisibility(View.VISIBLE);
                                    for (int x=0; x<jsonArray.length(); x++){

                                        JSONObject obj = jsonArray.getJSONObject(x);
                                        //String nama = obj.getString("type_kunci");
                                        ListItemKunciMobil itemKunciMobil = new ListItemKunciMobil(
                                                obj.getString("id"),
                                                obj.getString("nama_kunci"),
                                                obj.getString("merk"),
                                                obj.getString("harga_kunci"),
                                                obj.getString("biaya_layanan"),
                                                obj.getString("harga"),
                                                obj.getString("gambar")
                                        );
                                        listItemKunciMobils.add(itemKunciMobil);
                                    }
                                    //adapter = new RVKunciMobil(listItemKunciMobils, getContext());
                                    //recyclerView.setAdapter(adapter);

                                    mAdapter = new RVKunciMobil(listItemKunciMobils, getContext());
                                    recyclerView.setAdapter(mAdapter);
                                }

                            }catch(JSONException ex){
                                ex.printStackTrace();
                            }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                error.printStackTrace();
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


        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //Adding request to the queue
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == bKunciMobil){
            int posisi = mAdapter.getPosisi();
            //Toast.makeText(getActivity(), String.valueOf(posisi), Toast.LENGTH_SHORT).show();
            if (posisi != -1){
                Intent resultIntent = new Intent();
                String first_name_key = listItemKunciMobils.get(posisi).getMerk();
                String last_name_key = listItemKunciMobils.get(posisi).getNama_kunci();
                String full_key = first_name_key+" "+last_name_key;
                resultIntent.putExtra("kuncimobil", full_key);

                //put for id
                String iditem = listItemKunciMobils.get(posisi).getId();
                resultIntent.putExtra("iditemnya", iditem);
                getActivity().setResult(110, resultIntent);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Silahkan pilih salah satu gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            //Toast.makeText(getActivity(), tokennyaUser, Toast.LENGTH_SHORT).show();

        }
    }
     */

}
