package altus.visualradio.AsyncTasks;

import android.os.AsyncTask;
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
public class IndexFileDownloadAsync extends AsyncTask<String, String, String> {
    private String filePath;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected String doInBackground(String... fileUrl) {
        Log.d("Fragment", "thread = " + Thread.currentThread().getName());
        try {
            File downloadedFile = new File(filePath, "FILE");
            URL url = new URL(fileUrl[0]);
            Log.d("URL", url.toString());
            URLConnection conn = url.openConnection();
            Log.d("Connection Status", conn.toString());
            conn.connect();
            int contentLength = conn.getContentLength();
            DataInputStream stream = new DataInputStream(url.openStream());
            byte[] buffer = new byte[contentLength];

            stream.readFully(buffer);
            stream.close();
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(downloadedFile));
            dataOutputStream.write(buffer);
            dataOutputStream.close();

        } catch(FileNotFoundException e) {
            Log.e("FilenotFound", "File to write to not found");
        } catch (IOException e) {
            Log.e("URL not found", "URL path incorrect");
        }
        return null;
    }

    protected void onPostExecute(String string) {
        // doInBackground result returned to the onPostExecute method
        // Async is the only safe way to update UI outside of MAIN activity

        // Update UI here cannot be done in new class file it seems, class must be in main activity
        // Assuming that main Activity will need setter methods to handle UI changes from other class files
    }
}
