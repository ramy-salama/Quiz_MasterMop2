package com.example.quiz_mastermob.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.quiz_mastermob.models.ClassModel;
import com.example.quiz_mastermob.models.StudentModel;
import com.example.quiz_mastermob.models.UnitModel;
import com.example.quiz_mastermob.models.QuestionModel;
import com.example.quiz_mastermob.models.TeamModel;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quizmaster.db";
    private static final int DATABASE_VERSION = 1;

    // أسماء الجداول
    public static final String TABLE_CLASSES = "classes";
    public static final String TABLE_STUDENTS = "students";
    public static final String TABLE_UNITS = "units";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_TEAMS = "teams";
    public static final String TABLE_TEAM_MEMBERS = "team_members";

    // أسماء الأعمدة لجدول classes
    public static final String COLUMN_CLASS_ID = "id";
    public static final String COLUMN_CLASS_NAME = "name";
    public static final String COLUMN_CLASS_DESCRIPTION = "description";
    public static final String COLUMN_CLASS_CREATED_AT = "created_at";

    // أسماء الأعمدة لجدول students
    public static final String COLUMN_STUDENT_ID = "id";
    public static final String COLUMN_STUDENT_CLASS_ID = "class_id";
    public static final String COLUMN_STUDENT_NAME = "name";
    public static final String COLUMN_STUDENT_ATTENDANCE = "attendance";
    public static final String COLUMN_STUDENT_COMPLETED_8 = "completed_8";

    // أسماء الأعمدة لجدول units
    public static final String COLUMN_UNIT_ID = "id";
    public static final String COLUMN_UNIT_CLASS_ID = "class_id";
    public static final String COLUMN_UNIT_NAME = "name";
    public static final String COLUMN_UNIT_CREATED_AT = "created_at";

    // أسماء الأعمدة لجدول questions
    public static final String COLUMN_QUESTION_ID = "id";
    public static final String COLUMN_QUESTION_CLASS_ID = "class_id";
    public static final String COLUMN_QUESTION_UNIT_ID = "unit_id";
    public static final String COLUMN_QUESTION_TYPE = "type";
    public static final String COLUMN_QUESTION_TEXT = "text";
    public static final String COLUMN_QUESTION_ANSWER = "answer";
    public static final String COLUMN_QUESTION_OPTIONS = "options";
    public static final String COLUMN_QUESTION_CREATED_AT = "created_at";

    // أسماء الأعمدة لجدول teams
    public static final String COLUMN_TEAM_ID = "id";
    public static final String COLUMN_TEAM_CLASS_ID = "class_id";
    public static final String COLUMN_TEAM_NAME = "name";
    public static final String COLUMN_TEAM_COLOR = "color";
    public static final String COLUMN_TEAM_SCORE = "score";
    public static final String COLUMN_TEAM_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // إنشاء جدول classes
        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES + "("
                + COLUMN_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CLASS_NAME + " TEXT NOT NULL,"
                + COLUMN_CLASS_DESCRIPTION + " TEXT,"
                + COLUMN_CLASS_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_CLASSES_TABLE);

        // إنشاء جدول students
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + COLUMN_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_STUDENT_CLASS_ID + " INTEGER,"
                + COLUMN_STUDENT_NAME + " TEXT NOT NULL,"
                + COLUMN_STUDENT_ATTENDANCE + " INTEGER DEFAULT 0,"
                + COLUMN_STUDENT_COMPLETED_8 + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COLUMN_STUDENT_CLASS_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COLUMN_CLASS_ID + ") ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_STUDENTS_TABLE);

        // إنشاء جدول units
        String CREATE_UNITS_TABLE = "CREATE TABLE " + TABLE_UNITS + "("
                + COLUMN_UNIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UNIT_CLASS_ID + " INTEGER,"
                + COLUMN_UNIT_NAME + " TEXT NOT NULL,"
                + COLUMN_UNIT_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_UNIT_CLASS_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COLUMN_CLASS_ID + ") ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_UNITS_TABLE);

        // إنشاء جدول questions
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_QUESTION_CLASS_ID + " INTEGER,"
                + COLUMN_QUESTION_UNIT_ID + " INTEGER,"
                + COLUMN_QUESTION_TYPE + " TEXT CHECK(type IN ('essay','mcq','truefalse')),"
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL,"
                + COLUMN_QUESTION_ANSWER + " TEXT,"
                + COLUMN_QUESTION_OPTIONS + " TEXT,"
                + COLUMN_QUESTION_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_QUESTION_CLASS_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COLUMN_CLASS_ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + COLUMN_QUESTION_UNIT_ID + ") REFERENCES " + TABLE_UNITS + "(" + COLUMN_UNIT_ID + ") ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_QUESTIONS_TABLE);

        // إنشاء جدول teams
        String CREATE_TEAMS_TABLE = "CREATE TABLE " + TABLE_TEAMS + "("
                + COLUMN_TEAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TEAM_CLASS_ID + " INTEGER,"
                + COLUMN_TEAM_NAME + " TEXT NOT NULL,"
                + COLUMN_TEAM_COLOR + " TEXT DEFAULT '#4a90e2',"
                + COLUMN_TEAM_SCORE + " INTEGER DEFAULT 0,"
                + COLUMN_TEAM_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_TEAM_CLASS_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COLUMN_CLASS_ID + ") ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_TEAMS_TABLE);

        // إنشاء جدول team_members
        String CREATE_TEAM_MEMBERS_TABLE = "CREATE TABLE " + TABLE_TEAM_MEMBERS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "team_id INTEGER,"
                + "student_id INTEGER UNIQUE,"
                + "FOREIGN KEY(team_id) REFERENCES " + TABLE_TEAMS + "(" + COLUMN_TEAM_ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(student_id) REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_STUDENT_ID + ") ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_TEAM_MEMBERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            // هنا هنضيف ALTER statements لكل جدول حسب الإصدار الجديد
            // مثال: db.execSQL("ALTER TABLE " + TABLE_STUDENTS + " ADD COLUMN new_column INTEGER DEFAULT 0");

            // حالياً مش بنضيف أعمدة جديدة، فنسيبها فاضية
        }
    }

    // ========== دوال الصفوف ==========
    public long addClass(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, name);
        values.put(COLUMN_CLASS_DESCRIPTION, description);
        return db.insert(TABLE_CLASSES, null, values);
    }

    public List<ClassModel> getAllClasses() {
        List<ClassModel> classList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CLASSES + " ORDER BY " + COLUMN_CLASS_CREATED_AT + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ClassModel classModel = new ClassModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_CREATED_AT))
                );
                classList.add(classModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classList;
    }

    public ClassModel getClassById(int classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, null, COLUMN_CLASS_ID + " = ?",
                new String[]{String.valueOf(classId)}, null, null, null);
        ClassModel classModel = null;
        if (cursor.moveToFirst()) {
            classModel = new ClassModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_CREATED_AT))
            );
        }
        cursor.close();
        return classModel;
    }

    public int updateClass(int id, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, name);
        values.put(COLUMN_CLASS_DESCRIPTION, description);
        return db.update(TABLE_CLASSES, values, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASSES, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // ========== دوال الطلاب ==========
    public long addStudent(int classId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_CLASS_ID, classId);
        values.put(COLUMN_STUDENT_NAME, name);
        values.put(COLUMN_STUDENT_ATTENDANCE, 0);
        values.put(COLUMN_STUDENT_COMPLETED_8, 0);
        return db.insert(TABLE_STUDENTS, null, values);
    }

    public List<StudentModel> getStudentsByClass(int classId) {
        List<StudentModel> studentList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_STUDENT_CLASS_ID + " = ? ORDER BY " + COLUMN_STUDENT_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId)});

        if (cursor.moveToFirst()) {
            do {
                StudentModel student = new StudentModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ATTENDANCE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_COMPLETED_8))
                );
                studentList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return studentList;
    }

    public int updateStudent(int studentId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, name);
        return db.update(TABLE_STUDENTS, values, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
    }

    public void deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
    }

    public int updateStudentAttendance(int studentId, int newAttendance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_ATTENDANCE, newAttendance);
        return db.update(TABLE_STUDENTS, values, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
    }

    public void checkAndAddCompleted8(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_STUDENTS + " SET " + COLUMN_STUDENT_COMPLETED_8 + " = " + COLUMN_STUDENT_COMPLETED_8 + " + 1 WHERE " + COLUMN_STUDENT_ID + " = ? AND " + COLUMN_STUDENT_ATTENDANCE + " = 8",
                new String[]{String.valueOf(studentId)});
    }

    public void removeCompleted8(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_STUDENTS + " SET " + COLUMN_STUDENT_COMPLETED_8 + " = CASE WHEN " + COLUMN_STUDENT_COMPLETED_8 + " > 0 THEN " + COLUMN_STUDENT_COMPLETED_8 + " - 1 ELSE 0 END WHERE " + COLUMN_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)});
    }

    // ========== دوال الوحدات ==========
    public long addUnit(int classId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UNIT_CLASS_ID, classId);
        values.put(COLUMN_UNIT_NAME, name);
        return db.insert(TABLE_UNITS, null, values);
    }

    public List<UnitModel> getUnitsByClass(int classId) {
        List<UnitModel> unitList = new ArrayList<>();
        String query = "SELECT u.*, (SELECT COUNT(*) FROM " + TABLE_QUESTIONS + " WHERE " + COLUMN_QUESTION_UNIT_ID + " = u." + COLUMN_UNIT_ID + ") as question_count "
                + "FROM " + TABLE_UNITS + " u WHERE u." + COLUMN_UNIT_CLASS_ID + " = ? ORDER BY u." + COLUMN_UNIT_CREATED_AT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId)});

        if (cursor.moveToFirst()) {
            do {
                UnitModel unit = new UnitModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UNIT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UNIT_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT_CREATED_AT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow("question_count"))
                );
                unitList.add(unit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return unitList;
    }

    public int updateUnit(int unitId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UNIT_NAME, name);
        return db.update(TABLE_UNITS, values, COLUMN_UNIT_ID + " = ?", new String[]{String.valueOf(unitId)});
    }

    public void deleteUnit(int unitId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_UNITS, COLUMN_UNIT_ID + " = ?", new String[]{String.valueOf(unitId)});
    }

    // ========== دوال الأسئلة ==========
    public long addQuestion(int classId, int unitId, String type, String text, String answer, String options) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION_CLASS_ID, classId);
        values.put(COLUMN_QUESTION_UNIT_ID, unitId);
        values.put(COLUMN_QUESTION_TYPE, type);
        values.put(COLUMN_QUESTION_TEXT, text);
        values.put(COLUMN_QUESTION_ANSWER, answer);
        values.put(COLUMN_QUESTION_OPTIONS, options);
        return db.insert(TABLE_QUESTIONS, null, values);
    }

    public List<QuestionModel> getQuestions(int classId, int unitId, String type) {
        List<QuestionModel> questionList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + COLUMN_QUESTION_CLASS_ID + " = ? AND " + COLUMN_QUESTION_UNIT_ID + " = ? AND " + COLUMN_QUESTION_TYPE + " = ? ORDER BY " + COLUMN_QUESTION_CREATED_AT + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId), String.valueOf(unitId), type});

        if (cursor.moveToFirst()) {
            do {
                QuestionModel question = new QuestionModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_CLASS_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_UNIT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ANSWER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_OPTIONS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_CREATED_AT))
                );
                questionList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

    public QuestionModel getQuestion(int questionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, null, COLUMN_QUESTION_ID + " = ?", new String[]{String.valueOf(questionId)}, null, null, null);
        QuestionModel question = null;
        if (cursor.moveToFirst()) {
            question = new QuestionModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_CLASS_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_UNIT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ANSWER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_OPTIONS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_CREATED_AT))
            );
        }
        cursor.close();
        return question;
    }

    public int updateQuestion(int questionId, String text, String answer, String options) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION_TEXT, text);
        values.put(COLUMN_QUESTION_ANSWER, answer);
        values.put(COLUMN_QUESTION_OPTIONS, options);
        return db.update(TABLE_QUESTIONS, values, COLUMN_QUESTION_ID + " = ?", new String[]{String.valueOf(questionId)});
    }

    public void deleteQuestion(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, COLUMN_QUESTION_ID + " = ?", new String[]{String.valueOf(questionId)});
    }

    public int[] getQuestionStats(int classId, int unitId) {
        int[] stats = new int[3]; // [essay, mcq, truefalse]
        String query = "SELECT " + COLUMN_QUESTION_TYPE + ", COUNT(*) FROM " + TABLE_QUESTIONS + " WHERE " + COLUMN_QUESTION_CLASS_ID + " = ? AND " + COLUMN_QUESTION_UNIT_ID + " = ? GROUP BY " + COLUMN_QUESTION_TYPE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId), String.valueOf(unitId)});

        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(0);
                int count = cursor.getInt(1);
                switch (type) {
                    case "essay": stats[0] = count; break;
                    case "mcq": stats[1] = count; break;
                    case "truefalse": stats[2] = count; break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stats;
    }

    // ========== دوال الفرق ==========
    public long addTeam(int classId, String name, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEAM_CLASS_ID, classId);
        values.put(COLUMN_TEAM_NAME, name);
        values.put(COLUMN_TEAM_COLOR, color);
        values.put(COLUMN_TEAM_SCORE, 0);
        return db.insert(TABLE_TEAMS, null, values);
    }

    public List<TeamModel> getTeams(int classId) {
        List<TeamModel> teamList = new ArrayList<>();
        String query = "SELECT t.*, COUNT(tm.id) as member_count FROM " + TABLE_TEAMS + " t "
                + "LEFT JOIN " + TABLE_TEAM_MEMBERS + " tm ON t." + COLUMN_TEAM_ID + " = tm.team_id "
                + "WHERE t." + COLUMN_TEAM_CLASS_ID + " = ? GROUP BY t." + COLUMN_TEAM_ID + " ORDER BY t." + COLUMN_TEAM_CREATED_AT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId)});

        if (cursor.moveToFirst()) {
            do {
                TeamModel team = new TeamModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEAM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEAM_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEAM_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEAM_COLOR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEAM_SCORE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEAM_CREATED_AT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow("member_count"))
                );
                teamList.add(team);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return teamList;
    }

    public int updateTeam(int teamId, String name, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEAM_NAME, name);
        values.put(COLUMN_TEAM_COLOR, color);
        return db.update(TABLE_TEAMS, values, COLUMN_TEAM_ID + " = ?", new String[]{String.valueOf(teamId)});
    }

    public void deleteTeam(int teamId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEAM_MEMBERS, "team_id = ?", new String[]{String.valueOf(teamId)});
        db.delete(TABLE_TEAMS, COLUMN_TEAM_ID + " = ?", new String[]{String.valueOf(teamId)});
    }

    public long addTeamMember(int teamId, int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("team_id", teamId);
        values.put("student_id", studentId);
        return db.insert(TABLE_TEAM_MEMBERS, null, values);
    }

    public void removeTeamMember(int teamId, int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEAM_MEMBERS, "team_id = ? AND student_id = ?",
                new String[]{String.valueOf(teamId), String.valueOf(studentId)});
    }

    public List<StudentModel> getTeamMembers(int teamId) {
        List<StudentModel> memberList = new ArrayList<>();
        String query = "SELECT s.* FROM " + TABLE_STUDENTS + " s "
                + "JOIN " + TABLE_TEAM_MEMBERS + " tm ON s." + COLUMN_STUDENT_ID + " = tm.student_id "
                + "WHERE tm.team_id = ? ORDER BY s." + COLUMN_STUDENT_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(teamId)});

        if (cursor.moveToFirst()) {
            do {
                StudentModel student = new StudentModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ATTENDANCE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_COMPLETED_8))
                );
                memberList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return memberList;
    }

    public List<StudentModel> getAvailableStudents(int classId) {
        List<StudentModel> studentList = new ArrayList<>();
        String query = "SELECT s.* FROM " + TABLE_STUDENTS + " s "
                + "WHERE s." + COLUMN_STUDENT_CLASS_ID + " = ? AND s." + COLUMN_STUDENT_ID + " NOT IN ("
                + "SELECT tm.student_id FROM " + TABLE_TEAM_MEMBERS + " tm "
                + "JOIN " + TABLE_TEAMS + " t ON tm.team_id = t." + COLUMN_TEAM_ID + " "
                + "WHERE t." + COLUMN_TEAM_CLASS_ID + " = ?) ORDER BY s." + COLUMN_STUDENT_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId), String.valueOf(classId)});

        if (cursor.moveToFirst()) {
            do {
                StudentModel student = new StudentModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ATTENDANCE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_COMPLETED_8))
                );
                studentList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return studentList;
    }

    public int updateTeamScore(int teamId, int scoreChange) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEAM_SCORE, scoreChange);
        return db.update(TABLE_TEAMS, values, COLUMN_TEAM_ID + " = ?", new String[]{String.valueOf(teamId)});
    }

    public void resetTeamScore(int teamId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEAM_SCORE, 0);
        db.update(TABLE_TEAMS, values, COLUMN_TEAM_ID + " = ?", new String[]{String.valueOf(teamId)});
    }
}