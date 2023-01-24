package com.example.iot_sensehat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Sensor_RecyclerViewAdapter extends RecyclerView.Adapter<Sensor_RecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<SensorModel> sensorModels;

    public Sensor_RecyclerViewAdapter(Context context,
                                      ArrayList<SensorModel> sensorModels,
                                      RecyclerViewInterface recyclerViewInterface){
        this.context=context;
        this.sensorModels=sensorModels;
        this.recyclerViewInterface=recyclerViewInterface;

    }

    @NonNull
    @Override
    public Sensor_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new Sensor_RecyclerViewAdapter.MyViewHolder(view,recyclerViewInterface);
    }



    @Override
    public void onBindViewHolder(@NonNull Sensor_RecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvName.setText(sensorModels.get(position).getSensorName());
        holder.tvDescription.setText(sensorModels.get(position).getSensorDescribe());
        holder.tvMeasurement.setText(sensorModels.get(position).getSensorMeasurement());
    }


    @Override
    public int getItemCount() {
        return sensorModels.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView tvName, tvDescription, tvMeasurement;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            tvName=itemView.findViewById(R.id.SensorTextView);
            tvDescription=itemView.findViewById(R.id.SensorDescriptionTextView);
            tvMeasurement=itemView.findViewById(R.id.SensorMeasurementTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface!=null){
                        int pos=getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos,view);
                        }

                    }

                }
            });

        }
    }
}
