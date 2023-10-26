package com.example.smarttracker.Model;

public class TaskModel {
    private long id;
    private String name;
    private String date;
    private String time;
    private long categoryId;
    private int status;


    public TaskModel() {
    }

    public TaskModel(String name, String date, String time, long categoryId) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return name;
    }

}
