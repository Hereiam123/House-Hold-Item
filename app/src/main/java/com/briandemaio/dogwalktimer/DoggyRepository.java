package com.briandemaio.dogwalktimer;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class DoggyRepository {
    private DoggyDao mDoggyDao;

    private LiveData<List<Doggy>> mAllDoggies;
    DoggyRepository(Application application) {
        DoggyRoomDatabase db = DoggyRoomDatabase.getDatabase(application);
        mDoggyDao = db.doggyDao();
        mAllDoggies = mDoggyDao.getAllDoggies();
    }

    LiveData<List<Doggy>> getmAllDoggies() {
        return mAllDoggies;
    }

    public void insert (Doggy doggy) {
        new insertAsyncTask(mDoggyDao).execute(doggy);
    }

    public void delete(Doggy doggy)  {
        new deleteAsyncTask(mDoggyDao).execute(doggy);
    }

    public void update(Doggy doggy)  {
        new updateAsyncTask(mDoggyDao).execute(doggy);
    }

    private static class insertAsyncTask extends AsyncTask<Doggy, Void, Void> {

        private DoggyDao mAsyncTaskDao;

        insertAsyncTask(DoggyDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Doggy... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Doggy, Void, Void> {
        private DoggyDao mAsyncTaskDao;

        deleteAsyncTask(DoggyDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Doggy... params) {
            mAsyncTaskDao.deleteDoggy(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Doggy, Void, Void> {
        private DoggyDao mAsyncTaskDao;

        updateAsyncTask(DoggyDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Doggy... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
