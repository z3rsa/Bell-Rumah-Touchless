package com.example.bel_touchless;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bel_touchless.db.Data;

import java.util.ArrayList;
import java.util.List;

public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Data> dataList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public DataListAdapter(List<Data> dataList){
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DataListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logs_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataListAdapter.ViewHolder holder, int position) {
        Data data = dataList.get(position);

        holder.nJarak.setText("Jarak : ");
        holder.nSuhu.setText("Suhu : ");
        holder.waktu.setText(this.dataList.get(position).waktu);
        holder.jarak.setText(this.dataList.get(position).jarak);
        holder.suhu.setText(this.dataList.get(position).suhu);

        holder.btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onItemClickListener.deleteData(data);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView btnDelete;
        TextView jarak, suhu, nSuhu, nJarak, waktu;

        public ViewHolder(View view){
            super(view);
            waktu = view.findViewById(R.id.waktu);
            nSuhu = view.findViewById(R.id.nSuhu);
            nJarak = view.findViewById(R.id.nJarak);
            btnDelete = view.findViewById(R.id.btn_delete);
            jarak = view.findViewById(R.id.jarak);
            suhu = view.findViewById(R.id.suhu);
        }
    }

    interface OnItemClickListener {
        void deleteData(Data data);
    }
}
