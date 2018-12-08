package com.briandemaio.dogwalktimer;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DoggyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Doggy doggy);

    @Update
    void update(Doggy... doggy);

    @Delete
    void deleteDoggy(Doggy doggy);

    @Query("SELECT * from doggy_table ORDER BY doggy ASC")
    LiveData<List<Doggy>> getAllDoggies();
}


