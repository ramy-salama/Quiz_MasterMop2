package com.example.quiz_mastermob;

public class TeamModel {
    private int id;
    private int classId;
    private String name;
    private String color;
    private int score;
    private String createdAt;
    private int memberCount;

    public TeamModel(int id, int classId, String name, String color, int score,
                     String createdAt, int memberCount) {
        this.id = id;
        this.classId = classId;
        this.name = name;
        this.color = color;
        this.score = score;
        this.createdAt = createdAt;
        this.memberCount = memberCount;
    }

    public int getId() { return id; }
    public int getClassId() { return classId; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public int getScore() { return score; }
    public String getCreatedAt() { return createdAt; }
    public int getMemberCount() { return memberCount; }

    public void setId(int id) { this.id = id; }
    public void setClassId(int classId) { this.classId = classId; }
    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
    public void setScore(int score) { this.score = score; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
}