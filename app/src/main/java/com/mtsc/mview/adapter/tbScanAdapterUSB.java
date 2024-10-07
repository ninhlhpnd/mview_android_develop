package com.mtsc.mview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.mtsc.mview.R;
import com.mtsc.mview.model.CamBienUSB;
import com.mtsc.mview.ultis.Uuid;

import java.util.List;

public class tbScanAdapterUSB extends BaseAdapter {
    private Context context;
    private int layout;
    private List<CamBienUSB> tbScanList;

    public tbScanAdapterUSB(Context context, int layout, List<CamBienUSB> tbScanList) {
        this.context = context;
        this.layout = layout;
        this.tbScanList = tbScanList;
    }

    @Override
    public int getCount() {
        return tbScanList.size();
    }

    @Override
    public Object getItem(int position) {
        return tbScanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class ViewHolder{
        TextView txtTen;
        ImageView imageView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder();
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(layout,null);
            holder.txtTen=(TextView) convertView.findViewById(R.id.textviewTen);
            holder.imageView=(ImageView) convertView.findViewById(R.id.imageIconDevice);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();

        }
            CamBienUSB tbScan= (CamBienUSB) getItem(position);
            holder.txtTen.setText(tbScan.getCamBien().getName() + " - " + tbScan.getId());
            holder.imageView.setImageResource(tbScan.getCamBien().getIcon());


            return convertView;
    }
}
