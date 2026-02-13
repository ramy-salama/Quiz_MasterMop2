package com.example.quiz_mastermob;

public class QuestionModel {
    private int id;
    private int classId;
    private int unitId;
    private String type; // essay, mcq, truefalse
    private String text;
    private String answer;
    private String options; // JSON string for MCQ
    private String createdAt;

    public QuestionModel(int id, int classId, int unitId, String type, String text,
                         String answer, String options, String createdAt) {
        this.id = id;
        this.classId = classId;
        this.unitId = unitId;
        this.type = type;
        this.text = text;
        this.answer = answer;
        this.options = options;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getClassId() { return classId; }
    public int getUnitId() { return unitId; }
    public String getType() { return type; }
    public String getText() { return text; }
    public String getAnswer() { return answer; }
    public String getOptions() { return options; }
    public String getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setClassId(int classId) { this.classId = classId; }
    public void setUnitId(int unitId) { this.unitId = unitId; }
    public void setType(String type) { this.type = type; }
    public void setText(String text) { this.text = text; }
    public void setAnswer(String answer) { this.answer = answer; }
    public void setOptions(String options) { this.options = options; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}