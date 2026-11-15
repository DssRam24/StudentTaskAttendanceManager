package com.example.attendancemanager;

public class StudentModel {
    private String rollNo;
    private String name;
    private double attendance;
    private String task;

    public StudentModel(String rollNo, String name, double attendance, String task) {
        this.rollNo = rollNo;
        this.name = name;
        this.attendance = attendance;
        this.task = task;
    }

    public String getRollNo() { return rollNo; }
    public String getName() { return name; }
    public double getAttendance() { return attendance; }
    public String getTask() { return task; }
}
