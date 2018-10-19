package id.starkey.pelanggan.History;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import id.starkey.pelanggan.History.DalamProses.DalamProsesFragment;
import id.starkey.pelanggan.History.Selesai.SelesaiFragment;
import id.starkey.pelanggan.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //init toolbar
        /*
        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
         */
        setTitle("History");

        // Setting ViewPager for each Tabs
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new DalamProsesFragment(), "Dalam Proses");
        adapter.addFragment(new SelesaiFragment(), "Selesai");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
