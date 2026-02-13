package com.example.quiz_mastermob;

public class UnitModel {
    private int id;
    private int classId;
    private String name;
    private String createdAt;
    private int questionCount;

    public UnitModel(int id, int classId, String name, String createdAt, int questionCount) {
        this.id = id;
        this.classId = classId;
        this.name = name;
        this.createdAt = createdAt;
        this.questionCount = questionCount;
    }

    public int getId() { return id; }
    public int getClassId() { return classId; }
    public String getName() { return name; }
    public String getCreatedAt() { return createdAt; }
    public int getQuestionCount() { return questionCount; }

    public void setId(int id) { this.id = id; }
    public void setClassId(int classId) { this.classId = classId; }
    public void setName(String name) { this.name = name; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
}