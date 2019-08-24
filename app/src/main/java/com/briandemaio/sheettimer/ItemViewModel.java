package com.briandemaio.sheettimer;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {
    private ItemRepository mRepository;

    private LiveData<List<Item>> mAllItems;

    public ItemViewModel(Application application) {
        super(application);
        mRepository = new ItemRepository(application);
        mAllItems = mRepository.getmAllItems();
    }

    LiveData<List<Item>> getmAllItems() { return mAllItems; }

    public void insert(Item item) { mRepository.insert(item); }

    public void delete(Item item) { mRepository.delete(item); }

    public void update(Item item) { mRepository.update(item); }
}
