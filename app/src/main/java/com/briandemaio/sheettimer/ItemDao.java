package com.briandemaio.sheettimer;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Item item);

    @Update
    void update(Item... item);

    @Delete
    void deleteItem(Item item);

    @Query("SELECT * from item_table ORDER BY item ASC")
    LiveData<List<Item>> getAllItems();
}


