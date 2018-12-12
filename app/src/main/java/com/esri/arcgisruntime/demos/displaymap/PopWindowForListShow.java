package com.esri.arcgisruntime.demos.displaymap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PopWindowForListShow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_window_for_list_show);
        String data = getIntent().getStringExtra("data");
    }
}
