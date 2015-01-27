package altus.visualradio.ListView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by altus on 2015/01/22.
 */

//TODO MAKE THIS A AsyncTask
public class ImageDownloader extends AsyncTask<Object, Void, Bitmap> {
    private Bitmap image;
    private CustomListViewAdapter.ViewHolder viewHolder;
    private int position;
    private ImageCache imageCache;

    @Override
        protected Bitmap doInBackground(Object... params) {
            // Assigns variable into their respective types for later use
            String directory = (String) params[0];
            String Url = (String) params[1];
            String fileName = (String) params[2];
            viewHolder = (CustomListViewAdapter.ViewHolder) params[3];
            position = (Integer) params[4];

            imageCache = new ImageCache();
            imageCache.setMemoryCache();
            // Downloads the images in async task and assigns them to a bitmap variable called image
            try {
                downLoadFile(directory, Url, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            // The returned image is set to the imageView at the correct position
            super.onPostExecute(result);
            if(viewHolder.position == position) {
                viewHolder.imageView.setImageBitmap(result);
            }
        }

        public Bitmap downLoadFile(String dir, String Url, String name) throws IOException {
            // Downloads and saves images to external storage
            File downloadedFile = new File(dir, name);
            image = imageCache.getBitmapFromMemCache(name);

            if (!downloadedFile.exists()) {
                URL url = new URL(Url);
                InputStream inputStream = url.openStream();
                OutputStream outputStream = new FileOutputStream(downloadedFile);
                URLConnection conn = url.openConnection();
                Log.d("Connection Status", conn.toString());
                conn.connect();
                int contentLength = conn.getContentLength();
                byte[] buffer = new byte[contentLength];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
            }
            if (image == null) {
                image = BitmapFactory.decodeFile(dir + "/" + name);
                imageCache.addBitmapToMemoryCache(name, image);
            }
            return image;
        }
    }

