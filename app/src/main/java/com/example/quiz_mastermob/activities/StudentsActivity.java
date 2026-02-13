package com.example.quiz_mastermob.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.quiz_mastermob.R;
import com.example.quiz_mastermob.adapters.StudentsAdapter;
import com.example.quiz_mastermob.database.DatabaseHelper;
import com.example.quiz_mastermob.models.StudentModel;
import java.util.List;

public class StudentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewStudents;
    private StudentsAdapter studentsAdapter;
    private DatabaseHelper dbHelper;
    private TextView tvClassName;
    private View btnAddStudent;
    private int classId;
    private String className;

    public static void start(AppCompatActivity activity, int classId, String className) {
        Intent intent = new Intent(activity, StudentsActivity.class);
        intent.putExtra("class_id", classId);
        intent.putExtra("class_name", className);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        classId = getIntent().getIntExtra("class_id", -1);
        className = getIntent().getStringExtra("class_name");

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupListeners();
        loadStudents();
    }

    private void initViews() {
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        tvClassName = findViewById(R.id.tvClassName);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvClassName.setText(className);
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        btnAddStudent.setOnClickListener(v -> showStudentDialog(false, null));
    }

    private void loadStudents() {
        List<StudentModel> students = dbHelper.getStudentsByClass(classId);
        studentsAdapter = new StudentsAdapter(students,
                this::onStudentClick,
                this::onEditStudent,
                this::onDeleteStudent,
                this::onAttendanceChange,
                this::onRemoveCompleted8);
        recyclerViewStudents.setAdapter(studentsAdapter);
    }

    private void onStudentClick(StudentModel student) {
        // يمكن إضافة تفاصيل الطالب هنا
    }

    private void onEditStudent(StudentModel student) {
        showStudentDialog(true, student);
    }

    private void onDeleteStudent(StudentModel student) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("حذف الطالب")
                .setMessage("هل أنت متأكد من حذف هذا الطالب؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    dbHelper.deleteStudent(student.getId());
                    loadStudents();
                    Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("لا", null)
                .show();
    }

    private void onAttendanceChange(StudentModel student, int change) {
        int newAttendance = student.getAttendance() + change;
        if (newAttendance < 0) newAttendance = 0;
        if (newAttendance > 8) newAttendance = 8;

        dbHelper.updateStudentAttendance(student.getId(), newAttendance);

        if (newAttendance == 8) {
            dbHelper.checkAndAddCompleted8(student.getId());
            dbHelper.updateStudentAttendance(student.getId(), 0);
        }

        loadStudents();
    }

    private void onRemoveCompleted8(StudentModel student) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("حذف 8 مكتملة")
                .setMessage("هل أنت متأكد من حذف واحدة من الـ8 المكتملة؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    dbHelper.removeCompleted8(student.getId());
                    loadStudents();
                })
                .setNegativeButton("لا", null)
                .show();
    }

    private void showStudentDialog(boolean isEdit, StudentModel student) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_student, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView etName = dialogView.findViewById(R.id.etStudentName);

        if (isEdit && student != null) {
            tvTitle.setText("تعديل الطالب");
            etName.setText(student.getName());
        } else {
            tvTitle.setText("إضافة طالب");
        }

        builder.setView(dialogView)
                .setPositiveButton(isEdit ? "تحديث" : "إضافة", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "الرجاء إدخال اسم الطالب", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isEdit && student != null) {
                        dbHelper.updateStudent(student.getId(), name);
                        Toast.makeText(this, "تم التحديث", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.addStudent(classId, name);
                        Toast.makeText(this, "تمت الإضافة", Toast.LENGTH_SHORT).show();
                    }
                    loadStudents();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
}