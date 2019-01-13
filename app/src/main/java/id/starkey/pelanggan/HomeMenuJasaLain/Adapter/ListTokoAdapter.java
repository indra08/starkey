package id.starkey.pelanggan.HomeMenuJasaLain.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Utilities.CustomItem;
import id.starkey.pelanggan.Utilities.ImageUtils;
import id.starkey.pelanggan.Utilities.ItemValidation;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListTokoAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListTokoAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.cv_toko, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private ImageView ivIcon;
        private TextView tvItem1, tvItem2, tvItem3;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.cv_toko, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem2());
        holder.tvItem2.setText(itemSelected.getItem3());
        holder.tvItem3.setText(itemSelected.getItem4());

        ImageUtils iu = new ImageUtils();
        iu.LoadRealImage(context, itemSelected.getItem5(), holder.ivIcon);

        return convertView;

    }
}
