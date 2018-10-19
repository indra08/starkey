package id.starkey.pelanggan.Kunci.PilihKunci.Mobil;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import id.starkey.pelanggan.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dani on 3/15/2018.
 */

public class RVKunciMobil extends RecyclerView.Adapter<RVKunciMobil.ViewHolder> {

    private List<ListItemKunciMobil> listItemKunciMobils;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVKunciMobil(List<ListItemKunciMobil> listItemKunciMobils, Context context) {
        this.listItemKunciMobils = listItemKunciMobils;
        this.context = context;
    }

    @Override
    public RVKunciMobil.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_kunci_mobil, parent, false);
        return new ViewHolder(v);
    }

    int posisi = -1;

    @Override
    public void onBindViewHolder(RVKunciMobil.ViewHolder holder, final int position) {

        ListItemKunciMobil listItemKunciMobil = listItemKunciMobils.get(position);

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemKunciMobil.getHarga()));
        String result = Rupiah + " " + rupiah;

        holder.tvHargaKunciMobil.setText(result);

        /*
        String merkGabung = listItemKunciAlmari.getMerk();
        holder.tvTypeKunciAlmari.setText(listItemKunciAlmari.getNama_kunci()+" - "+merkGabung);
//        holder.tvMerkKunciAlmari.setText(listItemKunciAlmari.getMerk());
         */

        String merkGabung = listItemKunciMobil.getMerk();

        holder.tvTypeKunciMobil.setText(listItemKunciMobil.getNama_kunci()+" - "+merkGabung);
//        holder.tvMerkKunciMobil.setText(listItemKunciMobil.getMerk());
        holder.tTampungGbrKunciMobil.setText(listItemKunciMobil.getGambar());
        String kongbr = holder.tTampungGbrKunciMobil.getText().toString();
        if (kongbr.equals("0")){
            holder.imgKunciMobil.setImageResource(R.drawable.ic_default_kunci);
        } else {
            Picasso.with(context)
                    .load(kongbr)
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.imgKunciMobil);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posisi = position;
                notifyDataSetChanged();
            }
        });

        if (position == posisi){
            holder.cvKunciMobil.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.cvKunciMobil.setBackgroundColor(context.getResources().getColor(R.color.colorPutih));
        }
    }

    public int getPosisi(){
        return posisi;
    }

    @Override
    public int getItemCount() {
        return listItemKunciMobils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgKunciMobil;
        public TextView tvTypeKunciMobil, tvMerkKunciMobil, tvHargaKunciMobil, tTampungGbrKunciMobil;
        public CardView cvKunciMobil;

        public ViewHolder(View itemView){
            super(itemView);
            imgKunciMobil = itemView.findViewById(R.id.imgKunciMobil);
            tvTypeKunciMobil = itemView.findViewById(R.id.tvTypeKunciMobil);
            tvMerkKunciMobil = itemView.findViewById(R.id.tvMerkKunciMobil);
            cvKunciMobil = itemView.findViewById(R.id.cardViewKunciMobil);
            tvHargaKunciMobil = itemView.findViewById(R.id.tvHargaKunciMobil);
            tTampungGbrKunciMobil = itemView.findViewById(R.id.tvTampungGbrKunciMobil);
        }
    }
}
