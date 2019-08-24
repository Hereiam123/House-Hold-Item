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


