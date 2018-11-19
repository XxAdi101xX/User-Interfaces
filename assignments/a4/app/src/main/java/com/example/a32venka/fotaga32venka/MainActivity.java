package com.example.a32venka.fotaga32venka;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GridView gv;
    RatingBar filterRatingBar;
    float currentFilterRating;

    // Initializing a new String Array
    final String[] imageFileNames = {
            "bunny.jpg",
            "chinchilla.jpg",
            "doggo.jpg",
            "hamster.jpg",
            "husky.jpg",
            "kitten.png",
            "loris.jpg",
            "puppy.jpg",
            "redpanda.jpg",
            "sleepy.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        gv = (GridView) findViewById(R.id.gridview);
        filterRatingBar = findViewById(R.id.rb_rating);
        setFilterRating(5);

        filterRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setFilterRating(rating);
                Log.d("Filter", Float.toString(rating));

            }
        });

        final List<String> plantsList = new ArrayList<>(Arrays.asList(imageFileNames));

        // Create a new ArrayAdapter
        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<>
                (this,android.R.layout.simple_list_item_1, plantsList);

        // Data bind GridView with ArrayAdapter (String Array elements)
//        gv.setAdapter(gridViewArrayAdapter);
        gv.setAdapter(new ImageAdapter(this));
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imageFileNames.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mImageView;

            if (convertView == null) {
                mImageView = new ImageView(mContext);
                mImageView.setLayoutParams(new GridView.LayoutParams(130, 130));
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageView.setPadding(16, 16, 16, 16);
            } else {
                mImageView = (ImageView) convertView;
            }


//            mImageView.setImageDrawable(LoadImageFromWebOperations("https://www.student.cs.uwaterloo.ca/~cs349/f18/assignments/images/" + imageFiles[position]));
            mImageView.setImageResource(R.drawable.ic_action_clear);
            return mImageView;
        }
    }

    public void handleReload(View v) {
        Log.d("Icons", "clicked reload");
        openImageActivity(currentFilterRating);
    }

    public void handleClear(View v) {
        Log.d("Icons", "clicked clear");
        try {
            getInfo();
        } catch (Exception ex) {
            Log.d("GetInfo", "EXCEPTION!!!!!!!!!!");
        }
    }

    public void getInfo() throws Exception {
        URL oracle = new URL("https://www.student.cs.uwaterloo.ca/~cs349/f18/assignments/images");
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        Log.d("URL", "kkk");

        String inputLine;
        Log.d("MAYBE GETING PICC", "UHHH");
        while ((inputLine = in.readLine()) != null) Log.d("PICTUREEEEEE", inputLine);
        in.close();
    }

    public void openImageActivity(float pictureRating){
        Intent intent = new Intent(this, PictureActivity.class);
        intent.putExtra("rating", pictureRating);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                float rating = data.getFloatExtra("rating", 5);
                setFilterRating(rating); // TODO remove this later
                filterRatingBar.setRating(rating); // TODO remove this later
            }
        }
    }

    private void setFilterRating(float rating) {
        currentFilterRating = rating;
    }
}
