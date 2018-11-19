package com.example.a32venka.fotaga32venka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;

public class PictureActivity extends AppCompatActivity {
    RatingBar ratingBar;
    float pictureRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        ratingBar = findViewById(R.id.activityPictureRatingBar);

        Intent intent = getIntent();
        // extract the intent value in int
        pictureRating = intent.getFloatExtra("rating", 5);

        ratingBar.setRating(pictureRating);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                pictureRating = rating;
                Log.d("Filter", Float.toString(rating));
                goToParentActivity();
            }
        });
    }

    public void goToParentActivity() {
        Intent intent = new Intent();
        intent.putExtra("rating", pictureRating);
        setResult(RESULT_OK, intent);
        finish();
    }
}
