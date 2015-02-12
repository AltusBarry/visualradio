package altus.visualradio.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;
import java.util.ArrayList;

import altus.visualradio.ListView.ModelBase;

/**
 * Created by altus on 2015/02/04.
 * @author  Altus Barry
 * @version 1.0
 *
 */
public class DataStoreLoader extends AsyncTaskLoader<List<ModelBase>> {
    private List<ModelBase> mContents;



    public DataStoreLoader(Context context) {
        super(context);
    }

    @Override
    public List<ModelBase> loadInBackground() {
        List<ModelBase> list = new ArrayList<ModelBase>();
        return list;
    }

    protected void onStartLoading() {
        if (mContents != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mContents);
        }

        // onContentChanged() call causes next call of takeContentChanged() to return true.
        if (takeContentChanged()) {
            forceLoad();
        }
    }

    public void deliverResult(List<ModelBase> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }
        mContents = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    private void releaseResources(List<ModelBase> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }
}
