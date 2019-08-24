package com.briandemaio.sheettimer;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "task_table")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "task")
    private String task;

    private String imageResource;

    public Task(@NonNull String task, @NonNull String imageResource) {
        this.task = task;
        this.imageResource = imageResource;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTask(@NonNull String task) {
        this.task = task;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

}
