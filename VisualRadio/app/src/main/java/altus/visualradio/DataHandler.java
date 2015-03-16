package altus.visualradio;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.Date;

import altus.visualradio.ListView.ModelBase;

/**
 * Created by altus on 2015/02/13.
 * Headless Fragment to delete old files
 */
public class DataHandler extends Fragment {
    public handlerCallbacks callback;
    private Bundle dataBundle;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (handlerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    public interface handlerCallbacks {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        FileSystemPoll fl = new FileSystemPoll();
        fl.start();
    }

    public void onDetach() {
        super.onDetach();
    }

    public void setData(Bundle bundle) {
        dataBundle = bundle;
    }

    public Bundle getData(){
        return dataBundle;
    }

    /**
     * Polls the image directory and deletes old files
     */
    class FileSystemPoll extends Thread {
        File directory = new File((String.valueOf(getActivity().getApplication().getExternalFilesDir(null)))+"/images");

        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                Date current = new Date();
                current.getTime();
                iterate(directory, current.getTime());
                try {
                    Thread.sleep(36000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void iterate (File dir, Long current) {
            if (dir.exists()) {
                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; ++i) {
                    if(files[i].lastModified() < (current-36000000)) {
                        Log.i("File to Delete", files[i].getName());
                        files[i].delete();
                        Log.i("File Delted", "index Number" + i);
                    }
                }
            }
        }
    }
}
