package com.example.a32venka.fotaga32venka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GridView gv;
    RatingBar filterRatingBar;
    ArrayList<ImageInfo> animalImages;
    float currentFilterRating;
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
        setFilterRating(0);

        filterRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!fromUser) {
                    setFilterRating(rating);
                }
            }
        });

        // initialize images
        gv = findViewById(R.id.gridview);
        animalImages = new ArrayList<>();

        ArrayAdapter<ImageInfo> adapter = new imageArrayAdapter(this, 0, animalImages);
        gv.setAdapter(adapter);

        loadImages();
    }

    private void loadImages() {
        for (int i = 0; i < imageFileNames.length; ++i) {
            animalImages.add(new ImageInfo(baseUrl + imageFileNames[i], 0));
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        Bitmap bitmap;
        private DownloadImageTask(ImageView bmImage, Bitmap bitmap) {
            this.bmImage = bmImage;
            this.bitmap = bitmap;
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
            bitmap = result.copy(result.getConfig(), true);
        }
    }

    private class ImageAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;

        private ImageAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(c);
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
                mImageView = new ImageView(context);
                mImageView.setLayoutParams(new GridView.LayoutParams(180, 180));
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageView.setPadding(16, 16, 16, 16);
            } else {
                mImageView = (ImageView) convertView;
            }

//            String url = "https://www.student.cs.uwaterloo.ca/~cs349/f18/assignments/images/" + imageFileNames[position];
//            new DownloadImageTask(mImageView).execute(url);

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
        filterRatingBar.setRating(rating);
    }

    private class ImageInfo {
        private String imageUrl;
        private float rating;

        public ImageInfo(String imageUrl, float rating) {
            this.imageUrl = imageUrl;
            this.rating = rating;
        }

        String getUrl() {
            return imageUrl;
        }

        float getRating() {
            return rating;
        }

        void setRating(float newRating) {
            System.out.println("New rating issss" + newRating);
            this.rating = newRating;
        }
    }

    //custom ArrayAdapter
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
        public View getView(int position, View convertView, ViewGroup parent) {
            //get the inflater and inflate the XML layout for each item
            final ImageInfo imageInfo = animalPictures.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.picture_layout, null);

            ImageView image = view.findViewById(R.id.grid_image);
            Bitmap imageBitmap = null;
            new DownloadImageTask(image, imageBitmap).execute(imageInfo.getUrl());
//            imageView.setImageBitmap(imageInfo.bitmap);
//            Bitmap bitmap = ((BitmapDrawable)(imageInfo.image).getDrawable()).getBitmap();
//            imageView.setImageBitmap(bitmap);

            RatingBar ratingBar = view.findViewById(R.id.picture_rating);
            ratingBar.setRating(imageInfo.getRating());

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    imageInfo.setRating(rating);
                }
            });

            return view;
        }
    }
}
