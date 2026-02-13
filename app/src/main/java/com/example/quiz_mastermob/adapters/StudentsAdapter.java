package com.example.quiz_mastermob.adapters;

import com.example.quiz_mastermob.models.StudentModel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz_mastermob.R;
import com.example.quiz_mastermob.models.StudentModel;
import java.util.List;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {

    private List<StudentModel> students;
    private OnItemClickListener onItemClickListener;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private OnAttendanceChangeListener onAttendanceChangeListener;
    private OnRemoveCompleted8Listener onRemoveCompleted8Listener;

    public interface OnItemClickListener {
        void onItemClick(StudentModel student);
    }

    public interface OnEditClickListener {
        void onEditClick(StudentModel student);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(StudentModel student);
    }

    public interface OnAttendanceChangeListener {
        void onAttendanceChange(StudentModel student, int change);
    }

    public interface OnRemoveCompleted8Listener {
        void onRemoveCompleted8(StudentModel student);
    }

    public StudentsAdapter(List<StudentModel> students,
                           OnItemClickListener onItemClickListener,
                           OnEditClickListener onEditClickListener,
                           OnDeleteClickListener onDeleteClickListener,
                           OnAttendanceChangeListener onAttendanceChangeListener,
                           OnRemoveCompleted8Listener onRemoveCompleted8Listener) {
        this.students = students;
        this.onItemClickListener = onItemClickListener;
        this.onEditClickListener = onEditClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
        this.onAttendanceChangeListener = onAttendanceChangeListener;
        this.onRemoveCompleted8Listener = onRemoveCompleted8Listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentModel student = students.get(position);

        holder.tvStudentName.setText(student.getName());
        holder.tvAttendance.setText(student.getAttendance() + "/8");

        // Completed 8 badges
        holder.layoutCompleted8.removeAllViews();
        int completed8 = student.getCompleted8();

        for (int i = 0; i < completed8; i++) {
            TextView badge = new TextView(holder.itemView.getContext());
            badge.setText("8");
            badge.setBackgroundColor(0xFFE74C3C);
            badge.setTextColor(0xFFFFFFFF);
            badge.setPadding(12, 6, 12, 6);
            badge.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) badge.getLayoutParams();
            params.setMarginEnd(8);
            badge.setLayoutParams(params);

            final int badgeIndex = i;
            badge.setOnClickListener(v -> onRemoveCompleted8Listener.onRemoveCompleted8(student));

            holder.layoutCompleted8.addView(badge);
        }

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(student));
        holder.btnEdit.setOnClickListener(v -> onEditClickListener.onEditClick(student));
        holder.btnDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(student));
        holder.btnPlus.setOnClickListener(v -> onAttendanceChangeListener.onAttendanceChange(student, 1));
        holder.btnMinus.setOnClickListener(v -> onAttendanceChangeListener.onAttendanceChange(student, -1));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvAttendance, btnEdit, btnDelete, btnPlus, btnMinus;
        LinearLayout layoutCompleted8;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvAttendance = itemView.findViewById(R.id.tvAttendance);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            layoutCompleted8 = itemView.findViewById(R.id.layoutCompleted8);
        }
    }
}