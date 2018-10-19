package id.starkey.pelanggan.Kunci.PilihKunci.Pintu;

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

public class RVKunciPintu extends RecyclerView.Adapter<RVKunciPintu.ViewHolder> {

    private List<ListItemKunciPintu> listItemKunciPintus;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVKunciPintu(List<ListItemKunciPintu> listItemKunciPintus, Context context) {
        this.listItemKunciPintus = listItemKunciPintus;
        this.context = context;
    }

    @Override
    public RVKunciPintu.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_kunci_pintu, parent, false);
        return new ViewHolder(v);
    }


    int posisi = -1;

    @Override
    public void onBindViewHolder(RVKunciPintu.ViewHolder holder, final int position) {

        ListItemKunciPintu listItemKunciPintu = listItemKunciPintus.get(position);

        String merkGabung = listItemKunciPintu.getMerk();

        holder.tvTypeKunciPintu.setText(listItemKunciPintu.getNama_kunci()+" - "+merkGabung);
//        holder.tvMerkKunciPintu.setText(listItemKunciPintu.getMerk());
        holder.tTampungGbrKunciPintu.setText(listItemKunciPintu.getGambar());
        String kongbr = holder.tTampungGbrKunciPintu.getText().toString();

        if (kongbr.equals("0")){
            holder.ivKunciPintu.setImageResource(R.drawable.ic_default_kunci);
        } else {
            Picasso.with(context)
                    .load(kongbr)
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.ivKunciPintu);
        }

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemKunciPintu.getHarga()));
        String result = Rupiah + " " + rupiah;

        holder.tvHargaKunciPintu.setText(result);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posisi = position;
                notifyDataSetChanged();
            }
        });

        if (position == posisi){
            holder.cKunciPintu.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.cKunciPintu.setBackgroundColor(context.getResources().getColor(R.color.colorPutih));
        }
    }

    public int getPosisi(){
        return posisi;
    }

    @Override
    public int getItemCount() {
        return listItemKunciPintus.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTypeKunciPintu, tvMerkKunciPintu, tvHargaKunciPintu, tTampungGbrKunciPintu;
        public CardView cKunciPintu;
        public ImageView ivKunciPintu;

        public ViewHolder(View itemView){
            super(itemView);

            tvTypeKunciPintu = itemView.findViewById(R.id.tvTypeKunciPintu);
            tvMerkKunciPintu = itemView.findViewById(R.id.tvMerkKunciPintu);
            cKunciPintu = itemView.findViewById(R.id.cardViewKunciPintu);
            tvHargaKunciPintu = itemView.findViewById(R.id.tvHargaKunciPintu);
            ivKunciPintu = itemView.findViewById(R.id.imgKunciPintu);
            tTampungGbrKunciPintu = itemView.findViewById(R.id.tvTampungGbrKunciPintu);

        }
    }
}
