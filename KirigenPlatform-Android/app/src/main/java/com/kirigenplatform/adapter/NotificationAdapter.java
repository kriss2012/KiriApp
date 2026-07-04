package com.kirigenplatform.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kirigenplatform.R;
import com.kirigenplatform.data.model.Notification;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    
    private List<Notification> notifications = new ArrayList<>();
    
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }
    
    @Override
    public int getItemCount() {
        return notifications.size();
    }
    
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvMessage, tvTime;
        
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvTime = itemView.findViewById(R.id.tv_notification_time);
        }
        
        public void bind(Notification notification) {
            tvTitle.setText(notification.getTitle() != null ? notification.getTitle() : "Notification");
            tvMessage.setText(notification.getMessage() != null ? notification.getMessage() : "No message");
            tvTime.setText(notification.getCreatedAt() != null ? notification.getCreatedAt() : "Just now");
        }
    }
}
