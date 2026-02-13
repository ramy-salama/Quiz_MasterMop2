package com.example.quiz_mastermob;

public class StudentModel {
    private int id;
    private int classId;
    private String name;
    private int attendance;
    private int completed8;

    public StudentModel(int id, int classId, String name, int attendance, int completed8) {
        this.id = id;
        this.classId = classId;
        this.name = name;
        this.attendance = attendance;
        this.completed8 = completed8;
    }

    public int getId() { return id; }
    public int getClassId() { return classId; }
    public String getName() { return name; }
    public int getAttendance() { return attendance; }
    public int getCompleted8() { return completed8; }

    public void setId(int id) { this.id = id; }
    public void setClassId(int classId) { this.classId = classId; }
    public void setName(String name) { this.name = name; }
    public void setAttendance(int attendance) { this.attendance = attendance; }
    public void setCompleted8(int completed8) { this.completed8 = completed8; }
}