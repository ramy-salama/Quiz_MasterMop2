package com.example.quiz_mastermob.adapters;

import com.example.quiz_mastermob.models.UnitModel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz_mastermob.R;
import com.example.quiz_mastermob.models.UnitModel;
import java.util.List;

public class UnitsAdapter extends RecyclerView.Adapter<UnitsAdapter.ViewHolder> {

    private List<UnitModel> units;
    private OnItemClickListener onItemClickListener;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(UnitModel unit);
    }

    public interface OnEditClickListener {
        void onEditClick(UnitModel unit);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(UnitModel unit);
    }

    public UnitsAdapter(List<UnitModel> units,
                        OnItemClickListener onItemClickListener,
                        OnEditClickListener onEditClickListener,
                        OnDeleteClickListener onDeleteClickListener) {
        this.units = units;
        this.onItemClickListener = onItemClickListener;
        this.onEditClickListener = onEditClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_unit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UnitModel unit = units.get(position);

        holder.tvUnitName.setText(unit.getName());
        holder.tvQuestionCount.setText(String.valueOf(unit.getQuestionCount()));

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(unit));
        holder.btnEdit.setOnClickListener(v -> onEditClickListener.onEditClick(unit));
        holder.btnDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(unit));
    }

    @Override
    public int getItemCount() {
        return units.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUnitName, tvQuestionCount, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUnitName = itemView.findViewById(R.id.tvUnitName);
            tvQuestionCount = itemView.findViewById(R.id.tvQuestionCount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
