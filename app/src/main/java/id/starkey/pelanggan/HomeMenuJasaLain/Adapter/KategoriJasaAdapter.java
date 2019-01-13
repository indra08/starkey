package id.starkey.pelanggan.HomeMenuJasaLain.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import id.starkey.pelanggan.HomeMenuJasaLain.TokoPerKategori;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ImageUtils;
import id.starkey.pelanggan.Utilities.ItemValidation;

/**
 * Created by Shin on 3/1/2017.
 */

public class KategoriJasaAdapter extends RecyclerView.Adapter<KategoriJasaAdapter.MyViewHolder> {

    private Context context;
    private List<CustomItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private int menuWidth;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llContainer;
        public RelativeLayout cvContainer;
        public ImageView ivIcon;
        public TextView tvTitle;
        public LinearLayout llKatNotif;

        public MyViewHolder(View view) {
            super(view);
            cvContainer = (RelativeLayout) view.findViewById(R.id.rl_container);
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            llKatNotif = (LinearLayout) view.findViewById(R.id.ll_kat_notif);
            llContainer = (LinearLayout) view.findViewById(R.id.ll_container);
        }
    }

    public KategoriJasaAdapter(Context context, List<CustomItem> masterList, int menuWidth){
        this.context = context;
        this.masterList = masterList;
        this.menuWidth = menuWidth;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_katergori_jasa, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CustomItem kategori = masterList.get(position);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(menuWidth , menuWidth);
        holder.llContainer.setLayoutParams(lp);
        holder.tvTitle.setText(kategori.getItem2());
        // loading image using Picasso library
        ImageUtils iu = new ImageUtils();
        iu.LoadCategoryImage(context, kategori.getItem3(), holder.ivIcon);

        holder.cvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TokoPerKategori.class);
                intent.putExtra("id", kategori.getItem1());
                intent.putExtra("title", kategori.getItem2());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}
