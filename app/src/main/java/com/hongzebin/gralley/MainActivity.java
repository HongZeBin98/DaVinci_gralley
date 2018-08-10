package com.hongzebin.gralley;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.davinci.bean.DaVinci;
import com.example.davinci.engine.InnerEngine;
import com.example.davinci.util.Authority;

import java.util.List;

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
                        .imageEngine(InnerEngine.getInstance())
                        .forResult(1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    List<String> returnList = data.getStringArrayListExtra("data_return");
                    StringBuilder builder = new StringBuilder();
                    for(String x: returnList){
                        builder.append(x+"\n");
                    }
                    ((TextView)findViewById(R.id.show_selection)).setText(builder.toString());
                }
                break;
            default:
        }
    }
}
