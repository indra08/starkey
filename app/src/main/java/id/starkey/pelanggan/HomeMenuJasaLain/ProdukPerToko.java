package id.starkey.pelanggan.HomeMenuJasaLain;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.starkey.pelanggan.HomeMenuJasaLain.Adapter.ListProdukAdapter;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ItemValidation;

public class ProdukPerToko extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private List<CustomItem> masterList = new ArrayList<>();
    private ListView lvProduk;
    private EditText edtSearch;
    private String keyword = "";
    private int start = 0, count = 10;
    private View footerList;
    private ListProdukAdapter adapter;
    private boolean isLoading = false;
    private String idKategori;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_per_toko);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Kategori Jasa");
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        context = this;
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        lvProduk = (ListView) findViewById(R.id.lv_produk);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        keyword = "";
        start = 0;
        masterList = new ArrayList<>();
        isLoading = false;

        lvProduk.addFooterView(footerList);
        adapter = new ListProdukAdapter((Activity) context, masterList);
        lvProduk.removeFooterView(footerList);
        lvProduk.setAdapter(adapter);

        lvProduk.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvProduk.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvProduk.getLastVisiblePosition() >= total - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        initData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            idKategori = bundle.getString("id", "");
            title = bundle.getString("title", "");
            getSupportActionBar().setSubtitle(title);

        }
    }

    private void initEvent(){

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtSearch.getText().toString();
                    start = 0;
                    masterList.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });
    }

    private void initData(){

        masterList.add(new CustomItem("1", "Cukur 1", "20000", "https://i.dmarge.com/2017/06/hotoveli.jpg"));
        masterList.add(new CustomItem("2", "Cukur 2", "30000", "https://i.dmarge.com/2017/06/hotoveli.jpg"));
        masterList.add(new CustomItem("3", "Cukur 3", "40000", "https://i.dmarge.com/2017/06/hotoveli.jpg"));
        masterList.add(new CustomItem("4", "Cukur 4", "50000", "https://i.dmarge.com/2017/06/hotoveli.jpg"));

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
