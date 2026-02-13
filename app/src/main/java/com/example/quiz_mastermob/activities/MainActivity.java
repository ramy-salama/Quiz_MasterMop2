package com.example.quiz_mastermob.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.quiz_mastermob.R;
import com.example.quiz_mastermob.adapters.ClassesAdapter;
import com.example.quiz_mastermob.database.DatabaseHelper;
import com.example.quiz_mastermob.models.ClassModel;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewClasses;
    private ClassesAdapter classesAdapter;
    private DatabaseHelper dbHelper;
    private Button btnAddClass;
    private LinearLayout layoutThemeToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupThemeToggle();
        loadClasses();
        setupListeners();
    }

    private void initViews() {
        recyclerViewClasses = findViewById(R.id.recyclerViewClasses);
        btnAddClass = findViewById(R.id.btnAddClass);
        layoutThemeToggle = findViewById(R.id.layoutThemeToggle);

        recyclerViewClasses.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupThemeToggle() {
        layoutThemeToggle.setOnClickListener(v -> {
            // سيتم إضافة منطق تغيير الثيم لاحقاً
            Toast.makeText(this, "Toggle Theme", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadClasses() {
        List<ClassModel> classes = dbHelper.getAllClasses();
        classesAdapter = new ClassesAdapter(classes, this::onClassClick, this::onEditClass, this::onDeleteClass);
        recyclerViewClasses.setAdapter(classesAdapter);
    }

    private void onClassClick(ClassModel classModel) {
        // فتح تفاصيل الصف
        ClassDetailsActivity.start(this, classModel);
    }

    private void onEditClass(ClassModel classModel) {
        showClassDialog(true, classModel);
    }

    private void onDeleteClass(ClassModel classModel) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("حذف الصف")
                .setMessage("هل أنت متأكد من حذف هذا الصف؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    dbHelper.deleteClass(classModel.getId());
                    loadClasses();
                    Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("لا", null)
                .show();
    }

    private void setupListeners() {
        btnAddClass.setOnClickListener(v -> showClassDialog(false, null));
    }

    private void showClassDialog(boolean isEdit, ClassModel classModel) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_class, null);

        EditText etName = dialogView.findViewById(R.id.etClassName);
        EditText etDescription = dialogView.findViewById(R.id.etClassDescription);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);

        if (isEdit && classModel != null) {
            tvTitle.setText("تعديل الصف");
            etName.setText(classModel.getName());
            etDescription.setText(classModel.getDescription());
        } else {
            tvTitle.setText("إضافة صف جديد");
        }

        builder.setView(dialogView)
                .setPositiveButton(isEdit ? "تحديث" : "إضافة", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "الرجاء إدخال اسم الصف", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isEdit && classModel != null) {
                        dbHelper.updateClass(classModel.getId(), name, description);
                        Toast.makeText(this, "تم التحديث", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.addClass(name, description);
                        Toast.makeText(this, "تمت الإضافة", Toast.LENGTH_SHORT).show();
                    }
                    loadClasses();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
}