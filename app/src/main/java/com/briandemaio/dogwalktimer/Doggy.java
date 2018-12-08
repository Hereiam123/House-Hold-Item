package com.briandemaio.dogwalktimer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "doggy_table")
public class Doggy {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "doggy")
    private String name;

    @ColumnInfo(name = "expiryTime")
    private long expiryTime;

    private int imageResource;

    public Doggy(@NonNull String name, @NonNull int imageResource, @NonNull long expiryTime) {
        this.name = name;
        this.imageResource = imageResource;
        this.expiryTime = expiryTime;
    }

    @Ignore
    public Doggy(int id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    @Ignore
    public Doggy(@NonNull String name, @NonNull int imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }
}
