package com.mtsc.mview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsc.mview.R;
import com.mtsc.mview.model.thanhcongcuClass;
import com.mtsc.mview.my_interface.ItemClickListener;

import java.util.ArrayList;

public class thanhcongcuAdapter extends RecyclerView.Adapter<thanhcongcuAdapter.viewHolder> {
    private Context context;
    ArrayList<thanhcongcuClass> thanhconcuClasses;
    private ItemClickListener itemClickListener;

    public thanhcongcuAdapter(Context context, ArrayList<thanhcongcuClass> thanhconcuClasses, ItemClickListener itemClickListener) {
        this.context = context;
        this.thanhconcuClasses = thanhconcuClasses;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_thanhcongcu,parent,false);
        viewHolder viewHolder=new viewHolder(v);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        thanhcongcuClass thanhcongcu= thanhconcuClasses.get(position);
        holder.imageView.setImageResource(thanhcongcu.getHinh());
        holder.ten.setText(thanhcongcu.getTen());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(thanhcongcu,view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return thanhconcuClasses.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView ten;
        public CardView cardView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.imageview_dongthanhcongcu);
            ten= (TextView) itemView.findViewById(R.id.textviewTen_dongthanhcongcu);
            cardView=(CardView) itemView.findViewById(R.id.cardview_dongthanhcongcu);
        }

    }
}
