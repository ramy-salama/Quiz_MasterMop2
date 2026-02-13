package com.example.quiz_mastermob.adapters;

import com.example.quiz_mastermob.models.ClassModel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz_mastermob.R;
import com.example.quiz_mastermob.models.ClassModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {

    private List<ClassModel> classes;
    private OnItemClickListener onItemClickListener;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(ClassModel classModel);
    }

    public interface OnEditClickListener {
        void onEditClick(ClassModel classModel);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(ClassModel classModel);
    }

    public ClassesAdapter(List<ClassModel> classes,
                          OnItemClickListener onItemClickListener,
                          OnEditClickListener onEditClickListener,
                          OnDeleteClickListener onDeleteClickListener) {
        this.classes = classes;
        this.onItemClickListener = onItemClickListener;
        this.onEditClickListener = onEditClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassModel classModel = classes.get(position);

        holder.tvClassName.setText(classModel.getName());
        holder.tvClassDescription.setText(classModel.getDescription() != null && !classModel.getDescription().isEmpty()
                ? classModel.getDescription() : "لا يوجد وصف");

        // تنسيق التاريخ
        try {
            String dateStr = classModel.getCreatedAt();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            Date date = inputFormat.parse(dateStr);
            holder.tvCreatedAt.setText("تم الإنشاء: " + outputFormat.format(date));
        } catch (Exception e) {
            holder.tvCreatedAt.setText("تم الإنشاء: " + classModel.getCreatedAt());
        }

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(classModel));
        holder.btnEdit.setOnClickListener(v -> onEditClickListener.onEditClick(classModel));
        holder.btnDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(classModel));
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName, tvClassDescription, tvCreatedAt;
        View btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassDescription = itemView.findViewById(R.id.tvClassDescription);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}