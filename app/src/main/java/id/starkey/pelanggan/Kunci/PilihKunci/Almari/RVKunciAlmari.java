package id.starkey.pelanggan.Kunci.PilihKunci.Almari;

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
 * Created by Dani on 6/4/2018.
 */

public class RVKunciAlmari extends RecyclerView.Adapter<RVKunciAlmari.ViewHolder> {

    private List<ListItemKunciAlmari> listItemKunciAlmaris;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVKunciAlmari(List<ListItemKunciAlmari> listItemKunciAlmaris, Context context) {
        this.listItemKunciAlmaris = listItemKunciAlmaris;
        this.context = context;
    }

    @Override
    public RVKunciAlmari.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_kunci_almari, parent, false);
        return new ViewHolder(v);
    }

    int posisi = -1;

    @Override
    public void onBindViewHolder(RVKunciAlmari.ViewHolder holder, final int position) {

        ListItemKunciAlmari listItemKunciAlmari = listItemKunciAlmaris.get(position);

        String merkGabung = listItemKunciAlmari.getMerk();
        holder.tvTypeKunciAlmari.setText(listItemKunciAlmari.getNama_kunci()+" - "+merkGabung);
//        holder.tvMerkKunciAlmari.setText(listItemKunciAlmari.getMerk());
        holder.tTampungGbrKunciAlmari.setText(listItemKunciAlmari.getGambar());
        String kongbr = holder.tTampungGbrKunciAlmari.getText().toString();

        if (kongbr.equals("0")){
            holder.ivKunciAlmari.setImageResource(R.drawable.ic_default_kunci);
        } else {
            Picasso.with(context)
                    .load(kongbr)
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.ivKunciAlmari);
        }

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemKunciAlmari.getHarga()));
        String result = Rupiah + " " + rupiah;

        holder.tvHargaKunciAlmari.setText(result);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posisi = position;
                notifyDataSetChanged();
            }
        });

        if (position == posisi){
            holder.cKunciAlmari.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.cKunciAlmari.setBackgroundColor(context.getResources().getColor(R.color.colorPutih));
        }
    }

    public int getPosisi(){
        return posisi;
    }

    @Override
    public int getItemCount() {
        return listItemKunciAlmaris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTypeKunciAlmari, tvMerkKunciAlmari, tvHargaKunciAlmari, tTampungGbrKunciAlmari;
        public CardView cKunciAlmari;
        public ImageView ivKunciAlmari;

        public ViewHolder(View itemView){
            super(itemView);

            tvTypeKunciAlmari = itemView.findViewById(R.id.tvTypeKunciAlmari);
            tvMerkKunciAlmari = itemView.findViewById(R.id.tvMerkKunciAlmari);
            cKunciAlmari = itemView.findViewById(R.id.cardViewKunciAlmari);
            tvHargaKunciAlmari = itemView.findViewById(R.id.tvHargaKunciAlmari);
            ivKunciAlmari = itemView.findViewById(R.id.imgKunciAlmari);
            tTampungGbrKunciAlmari = itemView.findViewById(R.id.tvTampungGbrKunciAlmari);

        }
    }
}
