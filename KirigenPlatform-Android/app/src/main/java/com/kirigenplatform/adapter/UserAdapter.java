package com.kirigenplatform.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kirigenplatform.R;
import com.kirigenplatform.data.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    
    private List<User> users = new ArrayList<>();
    private OnUserClickListener listener;
    
    public interface OnUserClickListener {
        void onUserClick(User user);
        void onConnectClick(User user);
    }
    
    public UserAdapter(OnUserClickListener listener) {
        this.listener = listener;
    }
    
    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }
    
    public void addUser(User user) {
        users.add(user);
        notifyItemInserted(users.size() - 1);
    }
    
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, listener);
    }
    
    @Override
    public int getItemCount() {
        return users.size();
    }
    
    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvEmail, tvRole, tvDepartment;
        private Button btnConnect;
        
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvRole = itemView.findViewById(R.id.tv_user_role);
            tvDepartment = itemView.findViewById(R.id.tv_department);
            btnConnect = itemView.findViewById(R.id.btn_connect);
        }
        
        public void bind(User user, OnUserClickListener listener) {
            tvName.setText(user.getFullName() != null ? user.getFullName() : "User Name");
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "email@example.com");
            tvRole.setText(user.getRole() != null ? user.getRole() : user.getUserCategory());
            tvDepartment.setText(user.getDepartment() != null ? user.getDepartment() : "Department");
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });
            
            btnConnect.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConnectClick(user);
                }
            });
        }
    }
}
