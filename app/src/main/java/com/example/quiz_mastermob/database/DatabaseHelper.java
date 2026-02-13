package com.example.quiz_mastermob;

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
    private static final String TABLE_CLASSES = "classes";
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_UNITS = "units";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_TEAMS = "teams";
    private static final String TABLE_TEAM_MEMBERS = "team_members";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // إنشاء جدول classes
        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_CLASSES_TABLE);

        // إنشاء جدول students
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "class_id INTEGER,"
                + "name TEXT NOT NULL,"
                + "attendance INTEGER DEFAULT 0,"
                + "completed_8 INTEGER DEFAULT 0,"
                + "FOREIGN KEY(class_id) REFERENCES classes(id) ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_STUDENTS_TABLE);

        // إنشاء جدول units
        String CREATE_UNITS_TABLE = "CREATE TABLE " + TABLE_UNITS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "class_id INTEGER,"
                + "name TEXT NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(class_id) REFERENCES classes(id) ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_UNITS_TABLE);

        // إنشاء جدول questions
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "class_id INTEGER,"
                + "unit_id INTEGER,"
                + "type TEXT CHECK(type IN ('essay','mcq','truefalse')),"
                + "text TEXT NOT NULL,"
                + "answer TEXT,"
                + "options TEXT,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(class_id) REFERENCES classes(id) ON DELETE CASCADE,"
                + "FOREIGN KEY(unit_id) REFERENCES units(id) ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_QUESTIONS_TABLE);

        // إنشاء جدول teams
        String CREATE_TEAMS_TABLE = "CREATE TABLE " + TABLE_TEAMS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "class_id INTEGER,"
                + "name TEXT NOT NULL,"
                + "color TEXT DEFAULT '#4a90e2',"
                + "score INTEGER DEFAULT 0,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(class_id) REFERENCES classes(id) ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_TEAMS_TABLE);

        // إنشاء جدول team_members
        String CREATE_TEAM_MEMBERS_TABLE = "CREATE TABLE " + TABLE_TEAM_MEMBERS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "team_id INTEGER,"
                + "student_id INTEGER UNIQUE,"
                + "FOREIGN KEY(team_id) REFERENCES teams(id) ON DELETE CASCADE,"
                + "FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE" + ")";
        db.execSQL(CREATE_TEAM_MEMBERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAM_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        onCreate(db);
    }

    // ========== دوال الصفوف ==========
    public long addClass(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        return db.insert(TABLE_CLASSES, null, values);
    }

    public List<ClassModel> getAllClasses() {
        List<ClassModel> classList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CLASSES + " ORDER BY created_at DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ClassModel classModel = new ClassModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                classList.add(classModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classList;
    }

    public int updateClass(int id, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        return db.update(TABLE_CLASSES, values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASSES, "id = ?", new String[]{String.valueOf(id)});
    }

    // ========== دوال الطلاب ==========
    public long addStudent(int classId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("class_id", classId);
        values.put("name", name);
        values.put("attendance", 0);
        values.put("completed_8", 0);
        return db.insert(TABLE_STUDENTS, null, values);
    }

    public List<StudentModel> getStudentsByClass(int classId) {
        List<StudentModel> studentList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE class_id = ? ORDER BY name";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId)});

        if (cursor.moveToFirst()) {
            do {
                StudentModel student = new StudentModel(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4)
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
        values.put("name", name);
        return db.update(TABLE_STUDENTS, values, "id = ?", new String[]{String.valueOf(studentId)});
    }

    public void deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, "id = ?", new String[]{String.valueOf(studentId)});
    }

    public int updateStudentAttendance(int studentId, int newAttendance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("attendance", newAttendance);
        return db.update(TABLE_STUDENTS, values, "id = ?", new String[]{String.valueOf(studentId)});
    }

    public void checkAndAddCompleted8(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_STUDENTS + " SET completed_8 = completed_8 + 1 WHERE id = ? AND attendance = 8",
                new String[]{String.valueOf(studentId)});
    }

    public void removeCompleted8(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_STUDENTS + " SET completed_8 = CASE WHEN completed_8 > 0 THEN completed_8 - 1 ELSE 0 END WHERE id = ?",
                new String[]{String.valueOf(studentId)});
    }

    // ========== دوال الوحدات ==========
    public long addUnit(int classId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("class_id", classId);
        values.put("name", name);
        return db.insert(TABLE_UNITS, null, values);
    }

    public List<UnitModel> getUnitsByClass(int classId) {
        List<UnitModel> unitList = new ArrayList<>();
        String query = "SELECT u.*, (SELECT COUNT(*) FROM " + TABLE_QUESTIONS + " WHERE unit_id = u.id) as question_count "
                + "FROM " + TABLE_UNITS + " u WHERE u.class_id = ? ORDER BY u.created_at";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId)});

        if (cursor.moveToFirst()) {
            do {
                UnitModel unit = new UnitModel(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
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
        values.put("name", name);
        return db.update(TABLE_UNITS, values, "id = ?", new String[]{String.valueOf(unitId)});
    }

    public void deleteUnit(int unitId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_UNITS, "id = ?", new String[]{String.valueOf(unitId)});
    }

    // ========== دوال الأسئلة ==========
    public long addQuestion(int classId, int unitId, String type, String text, String answer, String options) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("class_id", classId);
        values.put("unit_id", unitId);
        values.put("type", type);
        values.put("text", text);
        values.put("answer", answer);
        values.put("options", options);
        return db.insert(TABLE_QUESTIONS, null, values);
    }

    public List<QuestionModel> getQuestions(int classId, int unitId, String type) {
        List<QuestionModel> questionList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE class_id = ? AND unit_id = ? AND type = ? ORDER BY created_at DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId), String.valueOf(unitId), type});

        if (cursor.moveToFirst()) {
            do {
                QuestionModel question = new QuestionModel(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                questionList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

    public QuestionModel getQuestion(int questionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, null, "id = ?", new String[]{String.valueOf(questionId)}, null, null, null);
        QuestionModel question = null;
        if (cursor.moveToFirst()) {
            question = new QuestionModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7)
            );
        }
        cursor.close();
        return question;
    }

    public int updateQuestion(int questionId, String text, String answer, String options) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("text", text);
        values.put("answer", answer);
        values.put("options", options);
        return db.update(TABLE_QUESTIONS, values, "id = ?", new String[]{String.valueOf(questionId)});
    }

    public void deleteQuestion(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, "id = ?", new String[]{String.valueOf(questionId)});
    }

    public int[] getQuestionStats(int classId, int unitId) {
        int[] stats = new int[3]; // [essay, mcq, truefalse]
        String query = "SELECT type, COUNT(*) FROM " + TABLE_QUESTIONS + " WHERE class_id = ? AND unit_id = ? GROUP BY type";
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
        values.put("class_id", classId);
        values.put("name", name);
        values.put("color", color);
        values.put("score", 0);
        return db.insert(TABLE_TEAMS, null, values);
    }

    public List<TeamModel> getTeams(int classId) {
        List<TeamModel> teamList = new ArrayList<>();
        String query = "SELECT t.*, COUNT(tm.id) as member_count FROM " + TABLE_TEAMS + " t "
                + "LEFT JOIN " + TABLE_TEAM_MEMBERS + " tm ON t.id = tm.team_id "
                + "WHERE t.class_id = ? GROUP BY t.id ORDER BY t.created_at";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId)});

        if (cursor.moveToFirst()) {
            do {
                TeamModel team = new TeamModel(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getInt(6)
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
        values.put("name", name);
        values.put("color", color);
        return db.update(TABLE_TEAMS, values, "id = ?", new String[]{String.valueOf(teamId)});
    }

    public void deleteTeam(int teamId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEAM_MEMBERS, "team_id = ?", new String[]{String.valueOf(teamId)});
        db.delete(TABLE_TEAMS, "id = ?", new String[]{String.valueOf(teamId)});
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
                + "JOIN " + TABLE_TEAM_MEMBERS + " tm ON s.id = tm.student_id "
                + "WHERE tm.team_id = ? ORDER BY s.name";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(teamId)});

        if (cursor.moveToFirst()) {
            do {
                StudentModel student = new StudentModel(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4)
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
                + "WHERE s.class_id = ? AND s.id NOT IN ("
                + "SELECT student_id FROM " + TABLE_TEAM_MEMBERS + " tm "
                + "JOIN " + TABLE_TEAMS + " t ON tm.team_id = t.id "
                + "WHERE t.class_id = ?) ORDER BY s.name";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId), String.valueOf(classId)});

        if (cursor.moveToFirst()) {
            do {
                StudentModel student = new StudentModel(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4)
                );
                studentList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return studentList;
    }

    public int updateTeamScore(int teamId, int scoreChange) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_TEAMS + " SET score = score + ? WHERE id = ?",
                new String[]{String.valueOf(scoreChange), String.valueOf(teamId)});
        return scoreChange;
    }

    public void resetTeamScore(int teamId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("score", 0);
        db.update(TABLE_TEAMS, values, "id = ?", new String[]{String.valueOf(teamId)});
    }
}