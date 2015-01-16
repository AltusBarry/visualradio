package altus.visualradio.Threads;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import altus.visualradio.MainListingActivity;

import static altus.visualradio.MainListingActivity.*;

/**
 * Created by altus on 2015/01/15.
 */
public class ThreadExample extends Fragment {
    private CallBacks textCallBack;

    public static interface CallBacks {
        void modifyUI(String string);
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            this.textCallBack = (CallBacks) activity;
        } catch (ClassCastException ex) {
            Log.e("Callback", "Casting the activity as a Callbacks listener failed"
                    + ex);
            textCallBack = null;
        }
    }

    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        // This fragment is now used to run a different thread.
        // It will also not recreate itself when the app changes states
        // Meaning the process can continue running uninterrupted
        this.setRetainInstance(true);
        wasteSomeTime();

    }

    public View onCreateView(){
        return null;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            textCallBack.modifyUI("Updated From Thread Callback Ya'll");
        }
    };

    public void wasteSomeTime(){
        Runnable runnable = new Runnable() {
            public void run() {
                long endTime = System.currentTimeMillis() + 10 * 1000;
                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try {
                            wait(endTime - System.currentTimeMillis());
                        } catch (Exception e) {
                        }
                    }
                }
                Log.d("Time wasted: ", Long.toString(System.currentTimeMillis()));
                Log.d("Fragment", "thread = " + Thread.currentThread().getName());
                handler.sendEmptyMessage(0);
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }
}



