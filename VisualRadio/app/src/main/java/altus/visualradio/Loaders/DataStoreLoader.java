package altus.visualradio.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

import altus.visualradio.ListView.DataDownloader;
import altus.visualradio.ListView.ModelBase;

/**
 * Created by altus on 2015/02/04.
 */
public class DataStoreLoader extends AsyncTaskLoader<List<ModelBase>> {
    private DataDownloader dl = new DataDownloader();
    private List<ModelBase> mContents;

    public DataStoreLoader(Context context) {
        super(context);
        dl.initialize((context.getExternalFilesDir(null)).toString());
        Log.i("Extrenal File directory", (context.getExternalFilesDir(null)).toString());
    }

    @Override
    public List<ModelBase> loadInBackground() {
        List<ModelBase> list = new ArrayList<ModelBase>(dl.getContent());
        return list;
    }

    protected void onStartLoading() {
        if (mContents != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mContents);
        }

        if (takeContentChanged()) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    public void deliverResult(List<ModelBase> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<ModelBase> oldData = mContents;
        mContents = data;

        // if (isStarted()) {
        // If the Loader is in a started state, deliver the results to the
        // client. The superclass method does this for us.
        super.deliverResult(data);
        // }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    private void releaseResources(List<ModelBase> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }
}
