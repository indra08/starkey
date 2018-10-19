package id.starkey.pelanggan.Kunci.PilihKunci.Lainnya;

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
 * Created by Dani on 3/18/2018.
 */

public class RVKunciLain extends RecyclerView.Adapter<RVKunciLain.ViewHolder> {

    private List<ListItemKunciLain> listItemKunciLains;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVKunciLain(List<ListItemKunciLain> listItemKunciLains, Context context) {
        this.listItemKunciLains = listItemKunciLains;
        this.context = context;
    }

    @Override
    public RVKunciLain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_kunci_lain, parent, false);
        return new ViewHolder(v);
    }

    int posisi = -1;

    @Override
    public void onBindViewHolder(RVKunciLain.ViewHolder holder, final int position) {

        ListItemKunciLain listItemKunciLain = listItemKunciLains.get(position);

        String merkGabung = listItemKunciLain.getMerk();

        holder.tvTypeKunciLain.setText(listItemKunciLain.getNama_kunci()+" - "+merkGabung);
//        holder.tvMerkKunciLain.setText(listItemKunciLain.getMerk());
        holder.tTampungGbrKunciLain.setText(listItemKunciLain.getGambar());
        String kongbr = holder.tTampungGbrKunciLain.getText().toString();
        if (kongbr.equals("0")){
            holder.ivKunciLain.setImageResource(R.drawable.ic_default_kunci);
        } else {
            Picasso.with(context)
                    .load(kongbr)
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.ivKunciLain);
        }

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemKunciLain.getHarga()));
        String result = Rupiah + " " + rupiah;

        holder.tvHargaKunciLain.setText(result);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posisi = position;
                notifyDataSetChanged();
            }
        });

        if (position == posisi){
            holder.cKunciLain.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.cKunciLain.setBackgroundColor(context.getResources().getColor(R.color.colorPutih));
        }
    }

    public int getPosisi(){
        return posisi;
    }

    @Override
    public int getItemCount() {
        return listItemKunciLains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTypeKunciLain, tvMerkKunciLain, tvHargaKunciLain, tTampungGbrKunciLain;
        public CardView cKunciLain;
        public ImageView ivKunciLain;

        public ViewHolder(View itemView){
            super(itemView);

            tvTypeKunciLain = itemView.findViewById(R.id.tvTypeKunciLain);
            tvMerkKunciLain = itemView.findViewById(R.id.tvMerkKunciLain);
            cKunciLain = itemView.findViewById(R.id.cardViewKunciLain);
            tvHargaKunciLain = itemView.findViewById(R.id.tvHargaKunciLain);
            ivKunciLain = itemView.findViewById(R.id.imgKunciLain);
            tTampungGbrKunciLain = itemView.findViewById(R.id.tvTampungGbrKunciLain);
        }
    }
}
