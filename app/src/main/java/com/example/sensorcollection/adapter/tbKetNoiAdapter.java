package com.example.sensorcollection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.example.sensorcollection.MainActivity;
import com.example.sensorcollection.R;
import com.example.sensorcollection.ultis.Uuid;

import java.util.List;

public class tbKetNoiAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<BleDevice> tbKetnoisList;

    public tbKetNoiAdapter(Context context, int layout, List<BleDevice> tbKetnoisList) {
        this.context = context;
        this.layout = layout;
        this.tbKetnoisList = tbKetnoisList;
    }

    @Override
    public int getCount() {
        return tbKetnoisList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder{
        TextView txtTen;
        ImageView imgDisconnect, imgIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(layout,null);
            viewHolder=new ViewHolder();
            viewHolder.txtTen=(TextView)convertView.findViewById(R.id.tenThietBi);
            viewHolder.imgDisconnect=(ImageView)convertView.findViewById(R.id.imgDisconnect);
            viewHolder.imgIcon=(ImageView)convertView.findViewById(R.id.imageIconPairDevice);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        BleDevice thietbi=tbKetnoisList.get(position);
        viewHolder.txtTen.setText(thietbi.getDevice().getName());
        int vitridaugach=thietbi.getDevice().getName().indexOf('-');
        String tenCamBien=thietbi.getDevice().getName().substring(0,vitridaugach);
        for (int i = 0; i< Uuid.camBiens.size(); i++){
            if(Uuid.camBiens.get(i).getId().equals(tenCamBien)) {
                viewHolder.imgIcon.setImageResource(Uuid.camBiens.get(i).getIcon());
            }
        }
        viewHolder.imgDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null){
                    mListener.onDisConnect(thietbi);
                }
            }
        });
        return convertView;
    }
    public interface OnDeviceClickListener{
        void onDisConnect(BleDevice bleDevice);
    }

    private OnDeviceClickListener mListener;
    public void setOnDeviceClickListener(OnDeviceClickListener mListener) {
        this.mListener = mListener;
    }
}
