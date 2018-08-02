package com.hongzebin.gralley;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.davinci.bean.DaVinci;
import com.example.davinci.util.Authority;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Authority.requestAllPower(this);
        setContentView(R.layout.main);
        Button button = findViewById(R.id.open_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DaVinci.from(MainActivity.this)
                        .choose()
                        .maxSelectable(9)
                        .forResult(1);
            }
        });
    }


}
