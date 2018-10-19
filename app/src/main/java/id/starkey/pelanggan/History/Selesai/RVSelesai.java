package id.starkey.pelanggan.History.Selesai;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Ratting.RattingActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dani on 4/3/2018.
 */

public class RVSelesai extends RecyclerView.Adapter<RVSelesai.ViewHolder> {


    private List<ListItemSelesai> listItemSelesais;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVSelesai(List<ListItemSelesai> listItemSelesais, Context context) {
        this.listItemSelesais = listItemSelesais;
        this.context = context;
    }

    @Override
    public RVSelesai.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history_selesai, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RVSelesai.ViewHolder holder, int position) {

        ListItemSelesai listItemSelesai = listItemSelesais.get(position);

        holder.tNamaLayananKunciSelesai.setText(listItemSelesai.getNama_layanan());

        holder.tNamaKunciSelesai.setText(listItemSelesai.getJenis_item());

        holder.tRated.setText(listItemSelesai.getIs_rated()); //cek rated
        holder.tTrxId.setText(listItemSelesai.getId()); //get id trx
        holder.tStatusCode.setText(listItemSelesai.getStatus_code());
        holder.tStatus.setText(listItemSelesai.getStatus());
        String statusnya = holder.tStatus.getText().toString();

        if (statusnya.equals("selesai")){
            holder.ivStatusSelesai.setBackgroundResource(R.drawable.ic_success);
        } else {
            holder.ivStatusSelesai.setBackgroundResource(R.drawable.ic_cancel);
        }

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemSelesai.getBiaya()));
        String result = Rupiah + "" + rupiah;

        holder.tBiayaSelesai.setText(result);


        String tgl = listItemSelesai.getTanggal();
        String formatTgl = tgl.substring(0, Math.min(tgl.length(), 10));
        String [] formatbaruTgl = formatTgl.split("-");
        String fixtanggal = formatbaruTgl[2]+"-"+formatbaruTgl[1]+"-"+formatbaruTgl[0];
        holder.tTglSelesai.setText(fixtanggal);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cekRate = holder.tRated.getText().toString();
                String idTrx = holder.tTrxId.getText().toString();
                String statusCode = holder.tStatusCode.getText().toString();
                Intent toRate = new Intent(context, RattingActivity.class);
                toRate.putExtra("konRate", cekRate);
                toRate.putExtra("idtransaksi", idTrx);
                toRate.putExtra("statuscode", statusCode);
                context.startActivity(toRate);


                /*
                String cekRate = holder.tRated.getText().toString();
                String idTrx = holder.tTrxId.getText().toString();
                String statusCode = holder.tStatusCode.getText().toString();
                if (cekRate.equals("0") && statusCode.equals("6")){
                    Intent toRate = new Intent(context, RattingActivity.class);
                    toRate.putExtra("idtransaksi", idTrx);
                    context.startActivity(toRate);
                } else {
                    Toast.makeText(context, "Detail activity", Toast.LENGTH_SHORT).show();
                }
                 */

            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelesais.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tNamaKunciSelesai, tNamaLayananKunciSelesai, tBiayaSelesai, tRated, tTrxId, tStatusCode, tStatus, tTglSelesai;
        public ImageView ivStatusSelesai;

        public ViewHolder(View itemView){
            super(itemView);

            tNamaKunciSelesai = itemView.findViewById(R.id.tvNamaKunciSelesai);
            tNamaLayananKunciSelesai = itemView.findViewById(R.id.tvNamaLayananKunciSelesai);
            tBiayaSelesai = itemView.findViewById(R.id.tvBiayaSelesai);
            tRated = itemView.findViewById(R.id.tvTampungIsRated);
            tTrxId = itemView.findViewById(R.id.tvTampungTrxId);
            tStatusCode = itemView.findViewById(R.id.tvTampungStatusCode);
            tStatus = itemView.findViewById(R.id.tvTampungStatus);
            tTglSelesai = itemView.findViewById(R.id.tvTanggalSelesai);
            ivStatusSelesai = itemView.findViewById(R.id.imageViewSelesai);

        }
    }
}
