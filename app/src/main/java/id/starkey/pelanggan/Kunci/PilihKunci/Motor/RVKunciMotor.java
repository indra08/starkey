package id.starkey.pelanggan.Kunci.PilihKunci.Motor;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
 * Created by Dani on 3/17/2018.
 */

public class RVKunciMotor extends RecyclerView.Adapter<RVKunciMotor.ViewHolder> {

    private List<ListItemKunciMotor> listItemKunciMotors;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";


    public RVKunciMotor(List<ListItemKunciMotor> listItemKunciMotors, Context context) {
        this.listItemKunciMotors = listItemKunciMotors;
        this.context = context;
    }

    @Override
    public RVKunciMotor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_kunci_motor, parent, false);
        return new ViewHolder(v);
    }

    int posisi = -1;

    @Override
    public void onBindViewHolder(RVKunciMotor.ViewHolder holder, final int position) {
        ListItemKunciMotor listItemKunciMotor = listItemKunciMotors.get(position);

        String merkGabung = listItemKunciMotor.getMerk();

        holder.tvTypeKunciMotor.setText(listItemKunciMotor.getNama_kunci()+" - "+merkGabung);
//        holder.tvMerkKunciMotor.setText(listItemKunciMotor.getMerk());
        holder.tvTampungGbrKunciMotor.setText(listItemKunciMotor.getGambar());
        String kongbr = holder.tvTampungGbrKunciMotor.getText().toString();

        if (kongbr.equals("0")){
            holder.ivGbrKunci.setImageResource(R.drawable.ic_default_kunci);
        } else {
            Picasso.with(context)
                    .load(kongbr)
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.ivGbrKunci);
        }


        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemKunciMotor.getHarga()));
        String result = Rupiah + " " + rupiah;

        holder.tvHargaKunciMotor.setText(result);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posisi = position;
                notifyDataSetChanged();
            }
        });

        if (position == posisi){
            holder.cvKunciMotor.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.cvKunciMotor.setBackgroundColor(context.getResources().getColor(R.color.colorPutih));
        }
    }

    public int getPosisi(){
        return posisi;
    }

    @Override
    public int getItemCount() {
        return listItemKunciMotors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTypeKunciMotor, tvMerkKunciMotor, tvHargaKunciMotor, tvTampungGbrKunciMotor;
        public CardView cvKunciMotor;
        public ImageView ivGbrKunci;

        public ViewHolder(View itemView){
            super(itemView);

            tvTypeKunciMotor = itemView.findViewById(R.id.tvTypeKunciMotor);
            tvMerkKunciMotor = itemView.findViewById(R.id.tvMerkKunciMotor);
            tvHargaKunciMotor = itemView.findViewById(R.id.tvHargaKunciMotor);
            tvTampungGbrKunciMotor = itemView.findViewById(R.id.tvTampungGbrKunciMotor);
            cvKunciMotor = itemView.findViewById(R.id.cardViewKunciMotor);
            ivGbrKunci = itemView.findViewById(R.id.imgKunciMotor);

        }
    }
}
