package com.example.quiz_mastermob.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.example.quiz_mastermob.R;
import com.example.quiz_mastermob.adapters.QuestionsAdapter;
import com.example.quiz_mastermob.adapters.UnitsAdapter;
import com.example.quiz_mastermob.database.DatabaseHelper;
import com.example.quiz_mastermob.models.QuestionModel;
import com.example.quiz_mastermob.models.UnitModel;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUnits, recyclerViewQuestions;
    private UnitsAdapter unitsAdapter;
    private QuestionsAdapter questionsAdapter;
    private DatabaseHelper dbHelper;
    private TextView tvClassName, tvCurrentType;
    private View btnAddUnit, btnAddQuestion;
    private TabLayout tabLayout;
    private LinearLayout layoutQuestionTypes;

    private int classId;
    private String className;
    private int currentUnitId = -1;
    private String currentUnitName = "";
    private String currentQuestionType = "";

    public static void start(AppCompatActivity activity, int classId, String className) {
        Intent intent = new Intent(activity, QuestionsActivity.class);
        intent.putExtra("class_id", classId);
        intent.putExtra("class_name", className);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        classId = getIntent().getIntExtra("class_id", -1);
        className = getIntent().getStringExtra("class_name");

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupListeners();
        loadUnits();
    }

    private void initViews() {
        recyclerViewUnits = findViewById(R.id.recyclerViewUnits);
        recyclerViewQuestions = findViewById(R.id.recyclerViewQuestions);
        tvClassName = findViewById(R.id.tvClassName);
        tvCurrentType = findViewById(R.id.tvCurrentType);
        btnAddUnit = findViewById(R.id.btnAddUnit);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        tabLayout = findViewById(R.id.tabLayout);
        layoutQuestionTypes = findViewById(R.id.layoutQuestionTypes);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvClassName.setText(className);

        recyclerViewUnits.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));

        setupTabs();
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.essay)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.mcq)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.truefalse)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (currentUnitId != -1) {
                    switch (tab.getPosition()) {
                        case 0: selectQuestionType("essay"); break;
                        case 1: selectQuestionType("mcq"); break;
                        case 2: selectQuestionType("truefalse"); break;
                    }
                } else {
                    Toast.makeText(QuestionsActivity.this, 
                        getString(R.string.select_unit_first), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupListeners() {
        btnAddUnit.setOnClickListener(v -> showUnitDialog(false, null));
        btnAddQuestion.setOnClickListener(v -> {
            if (currentUnitId == -1) {
                Toast.makeText(this, getString(R.string.select_unit_first), Toast.LENGTH_SHORT).show();
            } else if (currentQuestionType.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_question_type_first), Toast.LENGTH_SHORT).show();
            } else {
                showQuestionDialog(false, null);
            }
        });
    }

    private void loadUnits() {
        List<UnitModel> units = dbHelper.getUnitsByClass(classId);
        unitsAdapter = new UnitsAdapter(units, this::onUnitClick, this::onEditUnit, this::onDeleteUnit);
        recyclerViewUnits.setAdapter(unitsAdapter);
    }

    private void onUnitClick(UnitModel unit) {
        currentUnitId = unit.getId();
        currentUnitName = unit.getName();
        tvCurrentType.setText(getString(R.string.select_question_type));
        layoutQuestionTypes.setVisibility(View.VISIBLE);
        loadQuestionStats();
    }

    private void onEditUnit(UnitModel unit) {
        showUnitDialog(true, unit);
    }

    private void onDeleteUnit(UnitModel unit) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.delete_unit))
                .setMessage(getString(R.string.confirm_delete_unit))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    dbHelper.deleteUnit(unit.getId());
                    if (currentUnitId == unit.getId()) {
                        currentUnitId = -1;
                        currentQuestionType = "";
                        layoutQuestionTypes.setVisibility(View.GONE);
                    }
                    loadUnits();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void loadQuestionStats() {
        int[] stats = dbHelper.getQuestionStats(classId, currentUnitId);
        tabLayout.getTabAt(0).setText(getString(R.string.essay) + " (" + stats[0] + ")");
        tabLayout.getTabAt(1).setText(getString(R.string.mcq) + " (" + stats[1] + ")");
        tabLayout.getTabAt(2).setText(getString(R.string.truefalse) + " (" + stats[2] + ")");
    }

    private void selectQuestionType(String type) {
        currentQuestionType = type;
        String typeName = "";
        switch (type) {
            case "essay": typeName = getString(R.string.essay); break;
            case "mcq": typeName = getString(R.string.mcq); break;
            case "truefalse": typeName = getString(R.string.truefalse); break;
        }
        tvCurrentType.setText(typeName);
        loadQuestions();
    }

    private void loadQuestions() {
        List<QuestionModel> questions = dbHelper.getQuestions(classId, currentUnitId, currentQuestionType);
        questionsAdapter = new QuestionsAdapter(questions, this::onEditQuestion, this::onDeleteQuestion);
        recyclerViewQuestions.setAdapter(questionsAdapter);
    }

    private void onEditQuestion(QuestionModel question) {
        showQuestionDialog(true, question);
    }

    private void onDeleteQuestion(QuestionModel question) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.delete_question))
                .setMessage(getString(R.string.confirm_delete_question))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    dbHelper.deleteQuestion(question.getId());
                    loadQuestions();
                    loadQuestionStats();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void showUnitDialog(boolean isEdit, UnitModel unit) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_unit, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView etName = dialogView.findViewById(R.id.etUnitName);

        if (isEdit && unit != null) {
            tvTitle.setText(getString(R.string.edit_unit));
            etName.setText(unit.getName());
        } else {
            tvTitle.setText(getString(R.string.add_unit));
        }

        builder.setView(dialogView)
                .setPositiveButton(isEdit ? getString(R.string.update) : getString(R.string.add), 
                    (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, getString(R.string.field_required), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isEdit && unit != null) {
                        dbHelper.updateUnit(unit.getId(), name);
                        Toast.makeText(this, getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.addUnit(classId, name);
                        Toast.makeText(this, getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();
                    }
                    loadUnits();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void showQuestionDialog(boolean isEdit, QuestionModel question) {
        if (currentQuestionType.equals("essay")) {
            showEssayDialog(isEdit, question);
        } else if (currentQuestionType.equals("mcq")) {
            showMCQDialog(isEdit, question);
        } else if (currentQuestionType.equals("truefalse")) {
            showTrueFalseDialog(isEdit, question);
        }
    }

    private void showEssayDialog(boolean isEdit, QuestionModel question) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_essay, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView etQuestion = dialogView.findViewById(R.id.etQuestion);
        TextView etAnswer = dialogView.findViewById(R.id.etAnswer);

        tvTitle.setText(isEdit ? getString(R.string.edit_essay) : getString(R.string.add_essay));

        if (isEdit && question != null) {
            etQuestion.setText(question.getText());
            etAnswer.setText(question.getAnswer());
        }

        builder.setView(dialogView)
                .setPositiveButton(isEdit ? getString(R.string.update) : getString(R.string.add), 
                    (dialog, which) -> {
                    String qText = etQuestion.getText().toString().trim();
                    String answer = etAnswer.getText().toString().trim();

                    if (qText.isEmpty()) {
                        Toast.makeText(this, getString(R.string.field_required), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isEdit && question != null) {
                        dbHelper.updateQuestion(question.getId(), qText, answer, null);
                    } else {
                        dbHelper.addQuestion(classId, currentUnitId, "essay", qText, answer, null);
                    }
                    loadQuestions();
                    loadQuestionStats();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void showMCQDialog(boolean isEdit, QuestionModel question) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_mcq, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView etQuestion = dialogView.findViewById(R.id.etQuestion);
        TextView etOption1 = dialogView.findViewById(R.id.etOption1);
        TextView etOption2 = dialogView.findViewById(R.id.etOption2);
        TextView etOption3 = dialogView.findViewById(R.id.etOption3);
        TextView etOption4 = dialogView.findViewById(R.id.etOption4);

        tvTitle.setText(isEdit ? getString(R.string.edit_mcq) : getString(R.string.add_mcq));

        if (isEdit && question != null) {
            etQuestion.setText(question.getText());
            try {
                JSONArray options = new JSONArray(question.getOptions());
                etOption1.setText(options.optString(0));
                etOption2.setText(options.optString(1));
                etOption3.setText(options.optString(2));
                etOption4.setText(options.optString(3));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        builder.setView(dialogView)
                .setPositiveButton(isEdit ? getString(R.string.update) : getString(R.string.add), 
                    (dialog, which) -> {
                    String qText = etQuestion.getText().toString().trim();
                    String opt1 = etOption1.getText().toString().trim();
                    String opt2 = etOption2.getText().toString().trim();
                    String opt3 = etOption3.getText().toString().trim();
                    String opt4 = etOption4.getText().toString().trim();

                    if (qText.isEmpty() || opt1.isEmpty() || opt2.isEmpty() || opt3.isEmpty() || opt4.isEmpty()) {
                        Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray options = new JSONArray();
                    options.put(opt1);
                    options.put(opt2);
                    options.put(opt3);
                    options.put(opt4);

                    if (isEdit && question != null) {
                        dbHelper.updateQuestion(question.getId(), qText, "1", options.toString());
                    } else {
                        dbHelper.addQuestion(classId, currentUnitId, "mcq", qText, "1", options.toString());
                    }
                    loadQuestions();
                    loadQuestionStats();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void showTrueFalseDialog(boolean isEdit, QuestionModel question) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_truefalse, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView etQuestion = dialogView.findViewById(R.id.etQuestion);
        View btnTrue = dialogView.findViewById(R.id.btnTrue);
        View btnFalse = dialogView.findViewById(R.id.btnFalse);

        tvTitle.setText(isEdit ? getString(R.string.edit_truefalse) : getString(R.string.add_truefalse));

        final String[] selectedAnswer = {"true"};

        if (isEdit && question != null) {
            etQuestion.setText(question.getText());
            selectedAnswer[0] = question.getAnswer();
            if (selectedAnswer[0].equals("true")) {
                btnTrue.setBackgroundColor(0xFF2ECC71);
                btnFalse.setBackgroundColor(0xFFE0E0E0);
            } else {
                btnTrue.setBackgroundColor(0xFFE0E0E0);
                btnFalse.setBackgroundColor(0xFFE74C3C);
            }
        }

        btnTrue.setOnClickListener(v -> {
            selectedAnswer[0] = "true";
            btnTrue.setBackgroundColor(0xFF2ECC71);
            btnFalse.setBackgroundColor(0xFFE0E0E0);
        });

        btnFalse.setOnClickListener(v -> {
            selectedAnswer[0] = "false";
            btnTrue.setBackgroundColor(0xFFE0E0E0);
            btnFalse.setBackgroundColor(0xFFE74C3C);
        });

        builder.setView(dialogView)
                .setPositiveButton(isEdit ? getString(R.string.update) : getString(R.string.add), 
                    (dialog, which) -> {
                    String qText = etQuestion.getText().toString().trim();

                    if (qText.isEmpty()) {
                        Toast.makeText(this, getString(R.string.field_required), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isEdit && question != null) {
                        dbHelper.updateQuestion(question.getId(), qText, selectedAnswer[0], null);
                    } else {
                        dbHelper.addQuestion(classId, currentUnitId, "truefalse", qText, selectedAnswer[0], null);
                    }
                    loadQuestions();
                    loadQuestionStats();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
}