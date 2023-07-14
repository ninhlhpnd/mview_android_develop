package com.example.sensorcollection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.example.sensorcollection.R;
import com.example.sensorcollection.ultis.Uuid;


import java.util.List;

public class tbScanAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<BleDevice> tbScanList;

    public tbScanAdapter(Context context, int layout, List<BleDevice> tbScanList) {
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
            BleDevice tbScan= (BleDevice) getItem(position);
            holder.txtTen.setText(tbScan.getDevice().getName());
            int vitridaugach=tbScan.getDevice().getName().indexOf('-');
            String tenCamBien=tbScan.getDevice().getName().substring(0,vitridaugach);
            for (int i = 0; i< Uuid.camBiens.size(); i++){
                if(Uuid.camBiens.get(i).getId().equals(tenCamBien)) {
                    holder.imageView.setImageResource(Uuid.camBiens.get(i).getIcon());
                }
            }
            return convertView;
    }
}
