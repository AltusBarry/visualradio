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
 * Headless Fragment to carry data on state changes
 */
public class DataHandler extends Fragment implements MediaPlayer.OnPreparedListener {
    private static final int LOADER_ID = 1;
    private List<ModelBase> contents = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initMediaPlayer();

        FileSystemPoll fl = new FileSystemPoll();
        fl.start();
    }

    public void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(String.valueOf(Uri.parse("http://41.21.178.245:1935/jac-pri/jac-pri.stream/playlist.m3u8")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync();
        mediaPlayer.setVolume((10), (10));
    }

    public void onDetach() {
        super.onDetach();
    }

    public void setContents(List<ModelBase> contents) {
        this.contents = contents;
    }
    public List<ModelBase> getContents() {
        //Log.d("retainedContents", contents.toString());
            return this.contents;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i("Player Ready: ", "True");
         //mp.start();
        // return a true or something
    }

    public void play() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

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
