package com.briandemaio.sheettimer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Task task);

    @Update
    void update(Task... task);

    @Delete
    void deleteItem(Task task);

    @Query("SELECT * from task_table ORDER BY task ASC")
    LiveData<List<Task>> getAllItems();
}
