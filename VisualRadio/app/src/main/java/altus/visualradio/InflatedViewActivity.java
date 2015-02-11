package altus.visualradio;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import altus.visualradio.ListView.ModelBase;

/**
 * Created by altus on 2015/01/12.
 * Handles the layout for the inflated views
 */
public class InflatedViewActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inflated_view_layout);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        expandedTextFill();
    }

    private void expandedTextFill() {
        ModelBase item = (ModelBase) getIntent().getSerializableExtra("itemData");
        TextView headerText = (TextView) findViewById(R.id.text_title);
        TextView textBody = (TextView) findViewById(R.id.text_body);
        headerText.setText(item.title);
        textBody.setText(item.imageUrl);

        // TODO needs to access list Image cache and be able to call a download if the file does not exist
        Bitmap image = BitmapFactory.decodeFile(item.imageDir + "/" + item.imageName);
        ImageView imageView = (ImageView) findViewById(R.id.index_picture);
        imageView.setImageBitmap(image);
    }

}
