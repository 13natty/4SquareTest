package com.nattysoft.a4squaretest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ImageView iv = (ImageView) findViewById(R.id.full_image);
        TextView tv = (TextView) findViewById(R.id.details);

        Intent intent = getIntent();

        String url = intent.getExtras().getString("url");
        Picasso.with(this)
                .load(url)
                .into(iv);
        tv.setText(intent.getExtras().getString("details"));
    }
}
