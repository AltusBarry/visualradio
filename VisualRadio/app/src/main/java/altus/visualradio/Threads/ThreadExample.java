package altus.visualradio.Threads;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Handler;

import altus.visualradio.R;

/**
 * Created by altus on 2015/01/15.
 */
public class ThreadExample extends Fragment{

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

        public void wasteSomeTime(){
            Runnable runnable = new Runnable() {
                public void run() {
                    long endTime = System.currentTimeMillis() + 20 * 1000;
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
                }
            };

            Thread myThread = new Thread(runnable);
            myThread.start();

        }


}
