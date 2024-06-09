package com.example.huang.myapplication;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class categoryActivity extends AppCompatActivity {
    Button btn_template;
    Button btn_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        btn_template = (Button) findViewById(R.id.btn_template);
        btn_create = (Button) findViewById(R.id.btn_create);

        submit_search();
    }

    public void submit_search() {
        btn_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextPage("template");
            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextPage("create");
            }
        });
    }

    private void startNextPage(String type) {
        Intent intent = new Intent();
        switch (type){
            case "template":
                intent.setClass(this, TemplateActivity.class);
                break;
            case "create":
                intent.setClass(this, SearchActivity.class);
                break;
        }
        startActivity(intent);
    }
}
