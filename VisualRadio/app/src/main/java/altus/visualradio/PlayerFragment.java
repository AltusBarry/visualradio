package altus.visualradio;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import java.io.IOException;

/**
 * Created by altus on 2015/03/03.
 *
 * Fragment that houses teh media player for audio streaming purposes
 * Retains instance across state changes to ensure the media player dos not lose its context
 */
public class PlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener{
    private MediaPlayer mediaPlayer;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMediaPlayer();
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.player_fragment, container, false);
    }

    /**
     * Sets the media player to stream music
     * Gives it the url to stream from
     * Initialises the media player asynchronously
     * Sets left and right volume
     */
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
}
