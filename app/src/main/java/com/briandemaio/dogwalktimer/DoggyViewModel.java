package com.briandemaio.dogwalktimer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class DoggyViewModel extends AndroidViewModel {
    private DoggyRepository mRepository;

    private LiveData<List<Doggy>> mAllDoggies;

    public DoggyViewModel(Application application) {
        super(application);
        mRepository = new DoggyRepository(application);
        mAllDoggies = mRepository.getmAllDoggies();
    }

    LiveData<List<Doggy>> getmAllDoggies() { return mAllDoggies; }

    public void insert(Doggy doggy) { mRepository.insert(doggy); }

    public void delete(Doggy doggy) { mRepository.delete(doggy); }

    public void update(Doggy doggy) { mRepository.update(doggy); }
}
