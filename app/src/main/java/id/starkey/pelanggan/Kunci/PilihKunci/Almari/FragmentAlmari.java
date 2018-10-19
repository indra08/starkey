package id.starkey.pelanggan.Kunci.PilihKunci.Almari;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Dani on 6/4/2018.
 */

public class FragmentAlmari extends Fragment implements View.OnClickListener {
    public FragmentAlmari (){}

    View rootView;
    private String tokennyaUser, defaultValueCombo, id_layanan;
    private JSONObject jsonBody;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private List<ListItemKunciAlmari> listItemKunciAlmaris;
    private Button bKunciAlmari;
    private RVKunciAlmari mAdapter;

    //array for spinner
    private ArrayList<String> merkalmari;

    private MaterialSpinner materialSpinner;
    private RelativeLayout relativeLayoutKosong, relativeLayoutAda;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_almari_kunci, container, false);

        //init rv kunci pintu
        recyclerView = rootView.findViewById(R.id.recyclerViewKunciAlmari);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //init btn
        bKunciAlmari = rootView.findViewById(R.id.btnPilihKunciAlmari);
        bKunciAlmari.setOnClickListener(this);

        //init rlayout
        relativeLayoutKosong = rootView.findViewById(R.id.layoutKosongAlmari);
        relativeLayoutAda = rootView.findViewById(R.id.layoutAdaAlmari);

        listItemKunciAlmaris = new ArrayList<>();
        merkalmari = new ArrayList<>();
        materialSpinner = rootView.findViewById(R.id.spinnerMerkAlmari);

        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String almarimerk = item.toString().replaceAll(" ", "%20");
                getDetailAlmari(almarimerk, id_layanan);
            }
        });

        getIdLayananFromPref();

        defaultValueCombo = "all";
        getPrefTokenBearer();
        getComboAlmari();
        getDetailAlmari(defaultValueCombo, id_layanan);

        return rootView;
    }

    private void getPrefTokenBearer(){
        SharedPreferences custDetails = getActivity().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    private void getIdLayananFromPref(){
        SharedPreferences idlayanane = getActivity().getSharedPreferences(ConfigLink.idLayananPref, MODE_PRIVATE);
        id_layanan = idlayanane.getString("idlayananuser", "");
    }

    private void getComboAlmari(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ConfigLink.combo_almari, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            response = response.getJSONObject("data");
                            JSONArray jsonArray = response.getJSONArray("merk");
                            for (int w = 0; w<jsonArray.length(); w++){

                                JSONObject obj= jsonArray.getJSONObject(w);
                                String nama = obj.getString("name");
                                //int id   = parseInt(obj.getString("id"));
                                merkalmari.add(nama);
                            }
                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                        materialSpinner.setItems(merkalmari);
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

    private void getDetailAlmari(String merknya, String layananid){
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        //String URLMerkLain = "https://api.starkey.id/api/kunci?category=almari&merk="+merknya+"&sort=asc&sub_category=all&keyword=";
        String URLMerkLain = "https://api.starkey.id/api/v1.1/kunci?category=almari&merk="+merknya+"&sort=asc&sub_category=all&id_layanan_kunci="+layananid;
        Log.d("urlDetailAlmari", URLMerkLain);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URLMerkLain, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listItemKunciAlmaris.clear();
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
                                    ListItemKunciAlmari itemKunciAlmari = new ListItemKunciAlmari(
                                            obj.getString("id"),
                                            obj.getString("nama_kunci"),
                                            obj.getString("merk"),
                                            obj.getString("harga_kunci"),
                                            obj.getString("biaya_layanan"),
                                            obj.getString("harga"),
                                            obj.getString("gambar")
                                    );
                                    listItemKunciAlmaris.add(itemKunciAlmari);
                                }
                                //adapter = new RVKunciMotor(listItemKunciMotors, getContext());
                                //recyclerView.setAdapter(adapter);

                                mAdapter = new RVKunciAlmari(listItemKunciAlmaris, getContext());
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
        if (v == bKunciAlmari){
            int posisi = mAdapter.getPosisi();
            if (posisi != -1){
                Intent resultIntent = new Intent();
                String first_name_key = listItemKunciAlmaris.get(posisi).getMerk();
                String last_name_key = listItemKunciAlmaris.get(posisi).getNama_kunci();
                String full_key = first_name_key+ " " +last_name_key;
                resultIntent.putExtra("kuncimobil", full_key);

                //put for id
                String iditem = listItemKunciAlmaris.get(posisi).getId();
                resultIntent.putExtra("iditemnya", iditem);
                getActivity().setResult(110, resultIntent);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Silahkan pilih salah satu gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
