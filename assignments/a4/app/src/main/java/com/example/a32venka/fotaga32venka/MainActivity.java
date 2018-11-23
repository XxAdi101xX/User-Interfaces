package com.example.a32venka.fotaga32venka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GridView gv;
    RatingBar filterRatingBar;
    float globalFilterRating = 0;

    ArrayList<ImageInfo> allAnimalImages;
    ArrayList<ImageInfo> visibleAnimalImages;
    ArrayAdapter<ImageInfo> adapter;

    final String baseUrl = "https://www.student.cs.uwaterloo.ca/~cs349/f18/assignments/images/";
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

        // initialize filter values
        filterRatingBar = findViewById(R.id.main_filter_rating);
        filterRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                modifyGlobalFilterRating(rating);
                recalculateFilteredImages();
            }
        });

        // initialize imageInfo arraylists
        gv = findViewById(R.id.gridview);
        allAnimalImages = new ArrayList<>();
        visibleAnimalImages = new ArrayList<>();

        // set the adapter to the gridView
        adapter = new imageArrayAdapter(this, 0, visibleAnimalImages);
        gv.setAdapter(adapter);

        if (savedInstanceState != null) {
            float[] allRatings = savedInstanceState.getFloatArray("allRatings");
            modifyGlobalFilterRating(savedInstanceState.getFloat("globalFilterRating"));

            if (allRatings.length != 0) {
                for (int i = 0; i < imageFileNames.length; ++i) {
                    ImageInfo newImageInfo = new ImageInfo(i,baseUrl + imageFileNames[i], allRatings[i]);
                    allAnimalImages.add(newImageInfo);

                    if (allRatings[i] >= globalFilterRating) {
                        visibleAnimalImages.add(newImageInfo);
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        float[] allRatings = new float[allAnimalImages.size()];

        for (ImageInfo info: allAnimalImages) {
            allRatings[info.getId()] = info.getRating();
        }

        savedInstanceState.putFloatArray("allRatings", allRatings);
        savedInstanceState.putFloat("globalFilterRating", globalFilterRating);
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
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void loadImages() {
        for (int i = 0; i < imageFileNames.length; ++i) {
            ImageInfo newImageInfo = new ImageInfo(i,baseUrl + imageFileNames[i], 0);
            allAnimalImages.add(newImageInfo);
            visibleAnimalImages.add(newImageInfo);
        }
    }

    public void handleReload(View v) {
        if (!visibleAnimalImages.isEmpty()) {
            handleClear(v);
        }

        modifyGlobalFilterRating(0);
        loadImages();
        adapter.notifyDataSetChanged();
    }

    public void handleClear(View v) {
        allAnimalImages.clear();
        visibleAnimalImages.clear();
        adapter.notifyDataSetChanged();
    }

    public void openImageActivity(int id, String imageUrl, float pictureRating){
        Intent intent = new Intent(this, PictureActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("url", imageUrl);
        intent.putExtra("rating", pictureRating);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                int id = data.getIntExtra("id", 0);
                float rating = data.getFloatExtra("rating", 5);

                modifyPictureFilterRating(id, rating, true);
            }
        }
    }

    private void modifyGlobalFilterRating(float rating) {
        globalFilterRating = rating;
        filterRatingBar.setRating(rating);
    }

    private void modifyPictureFilterRating(int id, float rating, boolean updateView) {
        visibleAnimalImages.get(id).setRating(rating);

        if (rating < globalFilterRating) {
            visibleAnimalImages.remove(id);
            updateView = true;
        }

        if (updateView) {
            adapter.notifyDataSetChanged();
        }
    }

    private void recalculateFilteredImages() {
        if (allAnimalImages.isEmpty()) return;

        visibleAnimalImages.clear();
        for (ImageInfo info: allAnimalImages) {
            if (info.getRating() >= globalFilterRating) {
                visibleAnimalImages.add(info);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class ImageInfo {
        private int id;
        private String imageUrl;
        private float rating;

        private ImageInfo(int id, String imageUrl, float rating) {
            this.id = id;
            this.imageUrl = imageUrl;
            this.rating = rating;
        }

        public int getId() {
            return id;
        }

        private String getUrl() {
            return imageUrl;
        }

        private float getRating() {
            return rating;
        }

        private void setRating(float newRating) {
            this.rating = newRating;
        }
    }

    // custom ArrayAdapter for images imported from the UW online directory
    class imageArrayAdapter extends ArrayAdapter<ImageInfo> {

        private Context context;
        private List<ImageInfo> animalPictures;

        //constructor, call on creation
        private imageArrayAdapter(Context context, int resource, ArrayList<ImageInfo> objects) {
            super(context, resource, objects);

            this.context = context;
            this.animalPictures = objects;
        }

        //called when rendering the list
        public View getView(final int position, View convertView, ViewGroup parent) {
            //get the inflater and inflate the XML layout for each item
            final ImageInfo imageInfo = animalPictures.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.picture_layout, null);

            ImageView image = view.findViewById(R.id.grid_image);
            new DownloadImageTask(image).execute(imageInfo.getUrl());

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImageActivity(position, imageInfo.getUrl(), imageInfo.getRating());
                }
            });

            RatingBar ratingBar = view.findViewById(R.id.picture_rating);
            ratingBar.setRating(imageInfo.getRating());

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    modifyPictureFilterRating(position, rating, !fromUser);
                }
            });

            return view;
        }
    }
}
