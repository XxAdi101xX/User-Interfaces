package com.example.a32venka.fotaga32venka;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import java.io.InputStream;

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

        filterRatingBar = findViewById(R.id.rb_rating);
        setFilterRating(5);

        filterRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setFilterRating(rating);
            }
        });

        gv = findViewById(R.id.gridview);
        gv.setAdapter(new ImageAdapter(this));
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class ImageAdapter extends BaseAdapter {
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

            String url = "https://www.student.cs.uwaterloo.ca/~cs349/f18/assignments/images/" + imageFileNames[position];
            new DownloadImageTask(mImageView).execute(url);

            return mImageView;
        }
    }

    public void handleReload(View v) {
        Log.d("Icons", "clicked reload");
        openImageActivity(currentFilterRating);
    }

    public void handleClear(View v) {
        Log.d("Icons", "clicked clear");
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
