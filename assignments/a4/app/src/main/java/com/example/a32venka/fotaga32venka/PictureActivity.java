package com.example.a32venka.fotaga32venka;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.InputStream;

public class PictureActivity extends AppCompatActivity {
    int id;
    String url;
    RatingBar ratingBar;
    ImageView image;
    float pictureRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        image = findViewById(R.id.imageView);
        ratingBar = findViewById(R.id.activityPictureRatingBar);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        url = intent.getStringExtra("url");
        pictureRating = intent.getFloatExtra("rating", 0);

        // get image
        new DownloadImageTask(image).execute(url);

        // get rating value and set onclick listener
        ratingBar.setRating(pictureRating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            pictureRating = rating;
            }
        });

        // go to main activity on picture click
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParentActivity();
            }
        });
    }

    static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        private DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error with getting image from URL: ", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void goToParentActivity() {
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("rating", pictureRating);
        setResult(RESULT_OK, intent);
        finish();
    }
}
