package com.artezio.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * User: araigorodskiy
 * Date: 7/24/12
 * Time: 10:43 AM
 */
public abstract class AbstractProgressAsyncTask<T> extends AsyncTask<T, Void, String> {
    private ProgressDialog progress;
    private Context context;

    public AbstractProgressAsyncTask(Context context, String msg) {
        this.context = context;
        progress = new ProgressDialog(context);
        progress.setIndeterminate(true);
        progress.setMessage(msg);
//        mainActivity.getResources().getString(R.string.label, intent.getStringExtra(Constants.CODE))

    }

    public Context getContext() {
        return context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        getProgress().show();
    }

    public ProgressDialog getProgress() {
        return progress;
    }

    @Override
    protected void onPostExecute(String jsonObject) {
        super.onPostExecute(jsonObject);
        try {
            getProgress().dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        intent.putExtra("item", jsonObject);
//        mainActivity.startActivity(intent);
    }
}
