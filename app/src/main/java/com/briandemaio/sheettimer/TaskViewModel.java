package com.briandemaio.sheettimer;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository mRepository;

    private LiveData<List<Task>> mAllItems;

    public TaskViewModel(Application application) {
        super(application);
        mRepository = new TaskRepository(application);
        mAllItems = mRepository.getAllTask();
    }

    LiveData<List<Task>> getAllTask() { return mAllItems; }

    public void insert(Task task) { mRepository.insert(task); }

    public void delete(Task task) { mRepository.delete(task); }

    public void update(Task task) { mRepository.update(task); }
}
