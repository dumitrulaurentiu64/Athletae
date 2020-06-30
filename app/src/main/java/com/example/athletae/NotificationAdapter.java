package com.example.athletae;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public List<Notification> notif_list;

    public NotificationAdapter(List<Notification> notif_list){
        this.notif_list = notif_list;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        String notifData =  notif_list.get(position).getUsername();
        holder.setNotifText(notifData);
    }

    @Override
    public int getItemCount() {
        return notif_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private View mView;

        private TextView notifView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNotifText(String notifText){
            notifView = mView.findViewById(R.id.notif_text);
            notifView.setText(notifText);
        }
    }
}
