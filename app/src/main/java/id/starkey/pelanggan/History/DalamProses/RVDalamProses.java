package id.starkey.pelanggan.History.DalamProses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import id.starkey.pelanggan.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dani on 4/2/2018.
 */

public class RVDalamProses extends RecyclerView.Adapter<RVDalamProses.ViewHolder> {

    private List<ListItemDalamProses> listItemDalamProsess;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVDalamProses(List<ListItemDalamProses> listItemDalamProses, Context context) {
        this.listItemDalamProsess = listItemDalamProses;
        this.context = context;
    }

    @Override
    public RVDalamProses.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history_dalamproses, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RVDalamProses.ViewHolder holder, int position) {

        ListItemDalamProses listItemDalamProses = listItemDalamProsess.get(position);

        holder.tNamaItemDalamProses.setText(listItemDalamProses.getNama_kunci());
        holder.tNamaLayananKunciDalamProses.setText(listItemDalamProses.getNama_layanan_kunci());


        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemDalamProses.getBiaya()));
        String result = Rupiah + "" + rupiah;

        holder.tBiayaDalamProses.setText(result);

        /*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //notifyDataSetChanged();
                Toast.makeText(v.getContext(), "Next layout", Toast.LENGTH_SHORT).show();
            }
        });
         */

    }

    @Override
    public int getItemCount() {
        return listItemDalamProsess.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tNamaItemDalamProses, tNamaLayananKunciDalamProses, tBiayaDalamProses;

        public ViewHolder(View itemView){
            super(itemView);

            tNamaItemDalamProses = itemView.findViewById(R.id.tvNamaItemDalamProses);
            tNamaLayananKunciDalamProses = itemView.findViewById(R.id.tvNamaLayananKunciDalamProses);
            tBiayaDalamProses = itemView.findViewById(R.id.tvBiayaDalamProses);
        }
    }
}
