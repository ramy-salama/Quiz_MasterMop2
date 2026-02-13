package com.example.quiz_mastermob.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz_mastermob.R;
import com.example.quiz_mastermob.models.QuestionModel;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private List<QuestionModel> questions;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnEditClickListener {
        void onEditClick(QuestionModel question);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(QuestionModel question);
    }

    public QuestionsAdapter(List<QuestionModel> questions,
                            OnEditClickListener onEditClickListener,
                            OnDeleteClickListener onDeleteClickListener) {
        this.questions = questions;
        this.onEditClickListener = onEditClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionModel question = questions.get(position);

        holder.tvQuestionText.setText(question.getText());

        // Display answer based on type
        String answerText = "";
        switch (question.getType()) {
            case "essay":
                answerText = holder.itemView.getContext().getString(R.string.answer_prefix) + " " + 
                        (question.getAnswer() != null ? question.getAnswer() : "");
                break;

            case "mcq":
                try {
                    JSONArray options = new JSONArray(question.getOptions());
                    int answerIndex = 0;
                    try {
                        answerIndex = Integer.parseInt(question.getAnswer()) - 1;
                    } catch (NumberFormatException e) {
                        answerIndex = 0;
                    }

                    String[] letters = {"أ", "ب", "ج", "د"};
                    if (answerIndex >= 0 && answerIndex < options.length()) {
                        answerText = holder.itemView.getContext().getString(R.string.answer_prefix) + " " + 
                                letters[answerIndex] + " - " + options.getString(answerIndex);
                    }
                } catch (JSONException e) {
                    answerText = holder.itemView.getContext().getString(R.string.answer_prefix) + " " + 
                            (question.getAnswer() != null ? question.getAnswer() : "");
                }
                break;

            case "truefalse":
                answerText = holder.itemView.getContext().getString(R.string.answer_prefix) + " " +
                        (question.getAnswer().equals("true") ? 
                         holder.itemView.getContext().getString(R.string.true_answer) : 
                         holder.itemView.getContext().getString(R.string.false_answer));
                break;
        }

        holder.tvAnswer.setText(answerText);

        // Type badge
        String typeText = "";
        switch (question.getType()) {
            case "essay":
                typeText = holder.itemView.getContext().getString(R.string.essay);
                break;
            case "mcq":
                typeText = holder.itemView.getContext().getString(R.string.mcq);
                break;
            case "truefalse":
                typeText = holder.itemView.getContext().getString(R.string.truefalse);
                break;
        }
        holder.tvType.setText(typeText);

        holder.btnEdit.setOnClickListener(v -> onEditClickListener.onEditClick(question));
        holder.btnDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(question));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText, tvAnswer, tvType, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            tvType = itemView.findViewById(R.id.tvType);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}