package altus.visualradio;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import altus.visualradio.ListView.ModelBase;

/**
 * Created by altus on 2015/02/13.
 * Headless Fragment to delete old files
 */
public class DataHandler extends Fragment {
    private List<ModelBase> contents = new ArrayList<>();

    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
                    Thread.currentThread().sleep(36000000);
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
