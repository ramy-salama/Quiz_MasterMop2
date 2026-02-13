package com.example.quiz_mastermob.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.quiz_mastermob.R;
import com.example.quiz_mastermob.database.DatabaseHelper;
import com.example.quiz_mastermob.models.ClassModel;
import com.example.quiz_mastermob.models.StudentModel;
import com.example.quiz_mastermob.models.UnitModel;
import java.util.List;

public class ClassDetailsActivity extends AppCompatActivity {

    private ClassModel classModel;
    private DatabaseHelper dbHelper;
    private TextView tvClassName, tvTotalStudents, tvAvgAttendance, tvCompleted8;
    private TextView tvTotalQuestions, tvEssayCount, tvMcqCount, tvTrueFalseCount;
    private CardView cardStudents, cardQuestions, cardQuizDisplay, cardCompetitions;

    public static void start(AppCompatActivity activity, ClassModel classModel) {
        Intent intent = new Intent(activity, ClassDetailsActivity.class);
        intent.putExtra("class_id", classModel.getId());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        dbHelper = new DatabaseHelper(this);
        getClassData();
        initViews();
        setupListeners();
        loadStats();
    }

    private void getClassData() {
        int classId = getIntent().getIntExtra("class_id", -1);

        // جلب كائن ClassModel كامل من قاعدة البيانات
        classModel = dbHelper.getClassById(classId);

        if (classModel == null) {
            Toast.makeText(this, "خطأ في تحميل بيانات الصف", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        tvClassName = findViewById(R.id.tvClassName);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvAvgAttendance = findViewById(R.id.tvAvgAttendance);
        tvCompleted8 = findViewById(R.id.tvCompleted8);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvEssayCount = findViewById(R.id.tvEssayCount);
        tvMcqCount = findViewById(R.id.tvMcqCount);
        tvTrueFalseCount = findViewById(R.id.tvTrueFalseCount);

        cardStudents = findViewById(R.id.cardStudents);
        cardQuestions = findViewById(R.id.cardQuestions);
        cardQuizDisplay = findViewById(R.id.cardQuizDisplay);
        cardCompetitions = findViewById(R.id.cardCompetitions);

        if (classModel != null) {
            tvClassName.setText(classModel.getName());
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        if (classModel == null) return;

        cardStudents.setOnClickListener(v ->
                StudentsActivity.start(this, classModel.getId(), classModel.getName()));

        cardQuestions.setOnClickListener(v ->
                QuestionsActivity.start(this, classModel.getId(), classModel.getName()));

        cardQuizDisplay.setOnClickListener(v ->
                QuizDisplayActivity.start(this, classModel.getId(), classModel.getName()));

        cardCompetitions.setOnClickListener(v ->
                CompetitionsActivity.start(this, classModel.getId(), classModel.getName()));
    }

    private void loadStats() {
        if (classModel == null) return;

        // Student stats
        List<StudentModel> students = dbHelper.getStudentsByClass(classModel.getId());
        int totalStudents = students.size();
        int totalAttendance = 0;
        int totalCompleted8 = 0;

        for (StudentModel s : students) {
            totalAttendance += s.getAttendance();
            totalCompleted8 += s.getCompleted8();
        }

        int avgAttendance = totalStudents > 0 ? (totalAttendance * 100) / (totalStudents * 8) : 0;

        tvTotalStudents.setText(String.valueOf(totalStudents));
        tvAvgAttendance.setText(avgAttendance + "%");
        tvCompleted8.setText(String.valueOf(totalCompleted8));

        // Question stats
        List<UnitModel> units = dbHelper.getUnitsByClass(classModel.getId());
        int totalQuestions = 0;
        int essayCount = 0;
        int mcqCount = 0;
        int truefalseCount = 0;

        for (UnitModel unit : units) {
            int[] stats = dbHelper.getQuestionStats(classModel.getId(), unit.getId());
            essayCount += stats[0];
            mcqCount += stats[1];
            truefalseCount += stats[2];
        }

        totalQuestions = essayCount + mcqCount + truefalseCount;

        tvTotalQuestions.setText(String.valueOf(totalQuestions));
        tvEssayCount.setText(String.valueOf(essayCount));
        tvMcqCount.setText(String.valueOf(mcqCount));
        tvTrueFalseCount.setText(String.valueOf(truefalseCount));
    }
}