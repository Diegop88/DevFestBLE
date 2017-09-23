package com.honeywell.hts.devfestblegatt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private OnDeviceSelectedListener listener;
    private Map<String, String> devices;

    public ListAdapter(OnDeviceSelectedListener listener) {
        this.listener = listener;
        devices = new HashMap<>();
    }

    public void setDevices(Map<String, String> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind((String) devices.values().toArray()[position]);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView deviceName;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeviceSelected((String) devices.keySet().toArray()[getAdapterPosition()]);
                }
            });
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
        }

        void bind(String s) {
            deviceName.setText(s);
        }
    }

    interface OnDeviceSelectedListener {
        void onDeviceSelected(String macAddress);
    }
}
