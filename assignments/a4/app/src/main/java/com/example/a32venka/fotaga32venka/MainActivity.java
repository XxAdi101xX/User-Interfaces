package com.example.a32venka.fotaga32venka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goNextPage(View view){
        Intent intent = new Intent(this, PictureActivity.class);
//        intent.putExtra("version", version);
        startActivity(intent);

    }
}
