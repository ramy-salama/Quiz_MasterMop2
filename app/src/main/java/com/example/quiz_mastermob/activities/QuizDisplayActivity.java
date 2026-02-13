package com.example.quiz_mastermob.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quiz_mastermob.R;
import com.example.quiz_mastermob.database.DatabaseHelper;
import com.example.quiz_mastermob.models.QuestionModel;
import com.example.quiz_mastermob.models.UnitModel;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizDisplayActivity extends AppCompatActivity {

    private TextView tvClassName, tvUnitName, tvTimer, tvTotalQuestions;
    private GridLayout gridQuestions;
    private View btnBack;
    private LinearLayout unitsList;

    private DatabaseHelper dbHelper;
    private int classId;
    private String className;
    private int currentUnitId = -1;
    private List<QuestionModel> currentQuestions = new ArrayList<>();
    private boolean[] openedQuestions;

    private Handler timerHandler = new Handler();
    private int seconds = 0;
    private boolean timerRunning = false;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            updateTimerDisplay();
            timerHandler.postDelayed(this, 1000);
        }
    };

    public static void start(AppCompatActivity activity, int classId, String className) {
        Intent intent = new Intent(activity, QuizDisplayActivity.class);
        intent.putExtra("class_id", classId);
        intent.putExtra("class_name", className);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_display);

        classId = getIntent().getIntExtra("class_id", -1);
        className = getIntent().getStringExtra("class_name");

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadUnits();
    }

    @Override
    protected void onDestroy() {
        // إيقاف المؤقت لمنع تسرب الذاكرة
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
        super.onDestroy();
    }

    private void initViews() {
        tvClassName = findViewById(R.id.tvClassName);
        tvUnitName = findViewById(R.id.tvUnitName);
        tvTimer = findViewById(R.id.tvTimer);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        gridQuestions = findViewById(R.id.gridQuestions);
        btnBack = findViewById(R.id.btnBack);
        unitsList = findViewById(R.id.unitsList);

        tvClassName.setText(className);
        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnStartTimer).setOnClickListener(v -> startTimer());
        findViewById(R.id.btnPauseTimer).setOnClickListener(v -> pauseTimer());
        findViewById(R.id.btnResetTimer).setOnClickListener(v -> resetTimer());
    }

    private void loadUnits() {
        List<UnitModel> units = dbHelper.getUnitsByClass(classId);
        unitsList.removeAllViews();

        for (UnitModel unit : units) {
            TextView unitView = createUnitView(unit);
            unitsList.addView(unitView);
        }
    }

    private TextView createUnitView(UnitModel unit) {
        TextView tv = new TextView(this);
        tv.setText(unit.getName());
        tv.setPadding(16, 12, 16, 12);
        tv.setTextSize(14);
        tv.setBackgroundColor(0xFFF5F5F5);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.MATCH_PARENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(0, 0, 0, 4);
        tv.setLayoutParams(params);

        tv.setOnClickListener(v -> selectUnit(unit));

        return tv;
    }

    private void selectUnit(UnitModel unit) {
        currentUnitId = unit.getId();
        tvUnitName.setText(unit.getName());
        loadQuestions();
    }

    private void loadQuestions() {
        currentQuestions.clear();

        // Load all types
        currentQuestions.addAll(dbHelper.getQuestions(classId, currentUnitId, "essay"));
        currentQuestions.addAll(dbHelper.getQuestions(classId, currentUnitId, "mcq"));
        currentQuestions.addAll(dbHelper.getQuestions(classId, currentUnitId, "truefalse"));

        // Shuffle
        Collections.shuffle(currentQuestions);

        openedQuestions = new boolean[currentQuestions.size()];
        tvTotalQuestions.setText(String.valueOf(currentQuestions.size()));

        createQuestionGrid();
    }

    private void createQuestionGrid() {
        gridQuestions.removeAllViews();

        for (int i = 0; i < currentQuestions.size(); i++) {
            TextView box = createQuestionBox(i);
            gridQuestions.addView(box);
        }
    }

    private TextView createQuestionBox(int index) {
        TextView box = new TextView(this);
        box.setText(String.valueOf(index + 1));
        box.setPadding(12, 12, 12, 12);
        box.setTextSize(16);
        box.setGravity(android.view.Gravity.CENTER);
        box.setBackgroundColor(openedQuestions[index] ? 0xFFF58A38 : 0xFF4A90E2);
        box.setTextColor(0xFFFFFFFF);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 80;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(4, 4, 4, 4);
        box.setLayoutParams(params);

        box.setOnClickListener(v -> {
            if (!openedQuestions[index]) {
                showQuestionDialog(currentQuestions.get(index), index);
            }
        });

        return box;
    }

    private void showQuestionDialog(QuestionModel question, int index) {
        openedQuestions[index] = true;
        updateQuestionGrid();

        // Show question in dialog
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_question_display, null);

        TextView tvQuestion = dialogView.findViewById(R.id.tvQuestion);
        TextView tvAnswer = dialogView.findViewById(R.id.tvAnswer);
        View btnReveal = dialogView.findViewById(R.id.btnReveal);

        tvQuestion.setText(question.getText());

        builder.setView(dialogView)
                .setPositiveButton("إغلاق", null)
                .show();

        btnReveal.setOnClickListener(v -> {
            String answer = "";

            if (question.getType().equals("essay")) {
                answer = question.getAnswer() != null ? question.getAnswer() : "";
            } else if (question.getType().equals("mcq")) {
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
                        answer = letters[answerIndex] + " - " + options.getString(answerIndex);
                    }
                } catch (JSONException e) {
                    answer = question.getAnswer() != null ? question.getAnswer() : "";
                }
            } else {
                answer = question.getAnswer().equals("true") ? "صح ✓" : "خطأ ✗";
            }

            tvAnswer.setText(answer);
            tvAnswer.setVisibility(View.VISIBLE);
            btnReveal.setVisibility(View.GONE);
        });
    }

    private void updateQuestionGrid() {
        gridQuestions.removeAllViews();
        for (int i = 0; i < currentQuestions.size(); i++) {
            TextView box = createQuestionBox(i);
            gridQuestions.addView(box);
        }
    }

    private void startTimer() {
        if (!timerRunning) {
            timerRunning = true;
            timerHandler.post(timerRunnable);
            findViewById(R.id.btnStartTimer).setVisibility(View.GONE);
            findViewById(R.id.btnPauseTimer).setVisibility(View.VISIBLE);
        }
    }

    private void pauseTimer() {
        timerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
        findViewById(R.id.btnStartTimer).setVisibility(View.VISIBLE);
        findViewById(R.id.btnPauseTimer).setVisibility(View.GONE);
    }

    private void resetTimer() {
        pauseTimer();
        seconds = 0;
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time = String.format("%02d:%02d:%02d", hours, minutes, secs);
        tvTimer.setText(time);
    }
}