package com.hongzebin.gralley;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.davinci.Bean.DaVinci;
import com.example.davinci.Util.Authority;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Authority.requestAllPower(this);
        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.open_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DaVinci.from(MainActivity.this)
                        .choose()
                        .forResult(1);
            }
        });
    }


}
