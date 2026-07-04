package com.kirigenplatform.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kirigenplatform.R;
import com.kirigenplatform.data.model.Job;
import java.util.ArrayList;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    
    private List<Job> jobs = new ArrayList<>();
    private OnJobClickListener listener;
    
    public interface OnJobClickListener {
        void onJobClick(Job job);
        void onApplyClick(Job job);
    }
    
    public JobAdapter(OnJobClickListener listener) {
        this.listener = listener;
    }
    
    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
        notifyDataSetChanged();
    }
    
    public void addJob(Job job) {
        jobs.add(job);
        notifyItemInserted(jobs.size() - 1);
    }
    
    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.bind(job, listener);
    }
    
    @Override
    public int getItemCount() {
        return jobs.size();
    }
    
    static class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvCompany, tvLocation, tvJobType, tvSalary;
        private Button btnApply;
        
        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompany = itemView.findViewById(R.id.tv_company);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvJobType = itemView.findViewById(R.id.tv_job_type);
            tvSalary = itemView.findViewById(R.id.tv_salary);
            btnApply = itemView.findViewById(R.id.btn_apply);
        }
        
        public void bind(Job job, OnJobClickListener listener) {
            tvTitle.setText(job.getTitle() != null ? job.getTitle() : "Job Title");
            tvCompany.setText(job.getCompany() != null ? job.getCompany() : "Company");
            tvLocation.setText(job.getLocation() != null ? job.getLocation() : "Location");
            tvJobType.setText(job.getJobType() != null ? job.getJobType() : "Full Time");
            tvSalary.setText(job.getSalary() != null ? job.getSalary() : "Not specified");
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJobClick(job);
                }
            });
            
            btnApply.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApplyClick(job);
                }
            });
        }
    }
}
