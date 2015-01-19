package altus.visualradio.AsyncTasks;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by altus on 2015/01/13.
 */
public class UpdateViewAsync extends Fragment {

    private             FragmentUIAsyncTask fragmentUIAsyncTask;
    private             AsyncCallBacks textCallBack;

    public static interface AsyncCallBacks {
        void modifyUI(String string);
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            this.textCallBack = (AsyncCallBacks) activity;
        } catch (ClassCastException ex) {
            Log.e("Callback", "Casting the activity as a Callbacks listener failed"
                    + ex);
            textCallBack = null;
        }
    }

    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        this.setRetainInstance(true);
        fragmentUIAsyncTask = new FragmentUIAsyncTask();
        fragmentUIAsyncTask.execute("Async Fragment Callback UI Update");
    }

    class FragmentUIAsyncTask extends AsyncTask<String, String, String> {
        // Async task that is capable of manipulating UI
        // It needs to be inside the main Activity class
        // At this moment in time i am uncertain of how to change the UI in any other threads or
        // Async calls, without them being inside the main activity
        String string;
        protected String doInBackground(String... params) {
            Log.d("Fragment", "thread = " + Thread.currentThread().getName());
            string = params[0];
            long endTime = System.currentTimeMillis() + 5 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
            return string;
        }

        protected void onPostExecute(String results) {
            textCallBack.modifyUI(results);
        }
    }
}
