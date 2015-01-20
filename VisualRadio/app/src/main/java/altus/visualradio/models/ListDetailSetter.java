package altus.visualradio.models;

import android.graphics.Bitmap;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by altus on 2015/01/12.
 */
public class ListDetailSetter {
    private String title;
    private String imageURL;
    private Bitmap albumArtwork;
    private long publishOn;

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getImageURL() { return this.imageURL; }

    public void setPublishOn(long publishOn) {
        this.publishOn = publishOn;
    }

    public String getPublishOn() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(publishOn*1000);
        String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
        return date;
    }

    /* TODO TO be implemented at a later stage */
    /*
    public void albumArt(long id, String imageURL) {
        this.id = id;
        this.imageURL = imageURL;
        this.albumArtwork = null;
    }
    public Bitmap getAlbumArtwork() {
        downloadAlbumArt();
        return albumArtwork;
    }

    public void downloadAlbumArt() {
        // Download art into a folder.
        // Use a Headless fragment
        // Empty folder on application quit
        Runnable runnable = new Runnable() {
            public void run() {
                albumArtwork = getBitmapFromURL(imageURL);
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    public Bitmap getBitmapFromURL(String imageURL) {
        URL url;
        HttpURLConnection connection;
        InputStream input = null;
        Bitmap albumArt = null;
        try{
            url = new URL(imageURL);
            try{
                connection = (HttpURLConnection) url.openConnection();
                try{
                    connection.setDoInput(true);
                } catch (IllegalAccessError e){
                    Log.e("Illegal Access Error", "Input cannot be changed after init");
                }
                try{
                    connection.connect();
                } catch (IOException e){
                    Log.e("Connection Issue", "Could Not Connect");
                }
                try{
                    input = connection.getInputStream();
                } catch (IOException e) {
                    Log.e("I/O Exception", "Input Stream could not be created");
                }
                albumArt = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Log.e("I/O Exception", "Could not open connection");
            }
        }catch (IOException e) {
            Log.e("URL Issue", "URL not found");
            return null;
        }
        return albumArt;
    }*/
}

