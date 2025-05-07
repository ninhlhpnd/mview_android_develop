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
import com.mtsc.mview.model.ConnectedDevice;
import com.mtsc.mview.model.Run;
import com.mtsc.mview.ultis.Uuid;

import java.util.List;

public class LichsuAdapter extends BaseAdapter {
    private Context context;
    private List<Run> lichsuList;
    private int layout;

    public interface OnItemActionListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    public LichsuAdapter(Context context, int layout, List<Run> lichsuList) {
        this.context = context;
        this.layout = layout;
        this.lichsuList = lichsuList;
    }
    private OnItemActionListener listener;
    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }
    @Override
    public int getCount() {
        return lichsuList.size();
    }

    @Override
    public Object getItem(int i) {
        return lichsuList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    private class ViewHolder{
        TextView txtLichsu;
        ImageView imgDisconnect;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(layout,null);
            viewHolder=new ViewHolder();
            viewHolder.txtLichsu=(TextView)view.findViewById(R.id.textview_donglichsu);
            viewHolder.imgDisconnect=(ImageView)view.findViewById(R.id.imageDis_lichsu);
            view.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) view.getTag();
        }

        Run currentRun=lichsuList.get(i);
        viewHolder.txtLichsu.setText("Chạy " + currentRun.getRunNumber());

        view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(i);
            }
        });

        // Click vào icon xoá
        viewHolder.imgDisconnect.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(i);
            }
        });
        return view;
    }
}
