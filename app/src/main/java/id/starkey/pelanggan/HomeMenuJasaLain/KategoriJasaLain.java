package id.starkey.pelanggan.HomeMenuJasaLain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import id.starkey.pelanggan.HomeMenuJasaLain.Adapter.KategoriJasaAdapter;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ItemValidation;
import id.starkey.pelanggan.Utilities.SessionManager;

public class KategoriJasaLain extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private RecyclerView rvKategoriJasa;
    private List<CustomItem> masterList = new ArrayList<>();
    private int columnTable = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori_jasa_lain);

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
    }

    private void initUI() {

        rvKategoriJasa = (RecyclerView) findViewById(R.id.rv_kategori);
        masterList = new ArrayList<>();

        masterList.add(new CustomItem("1", "Laundry", "http://chittagongit.com//images/washing-machine-icon-png/washing-machine-icon-png-28.jpg"));
        masterList.add(new CustomItem("2", "Barber", "https://png.pngtree.com/png_detail/20181009/cartoons-depicting-barber-png-clipart_2820272.png"));
        masterList.add(new CustomItem("3", "Ledeng", "https://png2.kisspng.com/sh/700a271b19fc109bca35e353f5b99729/L0KzQYm3VcE1N6d3iZH0aYP2gLBuTfNwdaF6jNd7LYPydsXAggJmNZDzhNt3ZT32eLF3kPlvb154feRBaXPoPYbohsllaZc7SapvZkW7Poe5UMY6P2c7Sac7NkO1Q4q8WMExOmUziNDw/kisspng-computer-software-online-shopping-service-5af9daf618ff58.6206976615263239581024.png"));

        masterList.add(new CustomItem("2", "Barber", "https://png.pngtree.com/png_detail/20181009/cartoons-depicting-barber-png-clipart_2820272.png"));
        masterList.add(new CustomItem("1", "Laundry", "http://chittagongit.com//images/washing-machine-icon-png/washing-machine-icon-png-28.jpg"));
        masterList.add(new CustomItem("3", "Ledeng", "https://png2.kisspng.com/sh/700a271b19fc109bca35e353f5b99729/L0KzQYm3VcE1N6d3iZH0aYP2gLBuTfNwdaF6jNd7LYPydsXAggJmNZDzhNt3ZT32eLF3kPlvb154feRBaXPoPYbohsllaZc7SapvZkW7Poe5UMY6P2c7Sac7NkO1Q4q8WMExOmUziNDw/kisspng-computer-software-online-shopping-service-5af9daf618ff58.6206976615263239581024.png"));

        masterList.add(new CustomItem("3", "Ledeng", "https://png2.kisspng.com/sh/700a271b19fc109bca35e353f5b99729/L0KzQYm3VcE1N6d3iZH0aYP2gLBuTfNwdaF6jNd7LYPydsXAggJmNZDzhNt3ZT32eLF3kPlvb154feRBaXPoPYbohsllaZc7SapvZkW7Poe5UMY6P2c7Sac7NkO1Q4q8WMExOmUziNDw/kisspng-computer-software-online-shopping-service-5af9daf618ff58.6206976615263239581024.png"));
        masterList.add(new CustomItem("1", "Laundry", "http://chittagongit.com//images/washing-machine-icon-png/washing-machine-icon-png-28.jpg"));
        masterList.add(new CustomItem("2", "Barber", "https://png.pngtree.com/png_detail/20181009/cartoons-depicting-barber-png-clipart_2820272.png"));

        setKategoriAdapter();
    }

    private void setKategoriAdapter(){

        rvKategoriJasa.setAdapter(null);

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            // this is why the minimal sdk must be JB
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(size);
            }else {
                display.getSize(size);
            }
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }

        int menuWidth = 0;
        double menuFloat = (size.x ) / columnTable;
        menuWidth = (int) menuFloat;

        int jmlBaris = (int)(Math.ceil((double) masterList.size() / columnTable));
        KategoriJasaAdapter menuAdapter = new KategoriJasaAdapter(context, masterList, menuWidth);

        rvKategoriJasa.setLayoutParams(new RelativeLayout.LayoutParams(rvKategoriJasa.getLayoutParams().width, (((size.x) / columnTable * jmlBaris))));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, columnTable);
        rvKategoriJasa.setLayoutManager(mLayoutManager);
        rvKategoriJasa.setItemAnimator(new DefaultItemAnimator());
        rvKategoriJasa.setAdapter(menuAdapter);
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
