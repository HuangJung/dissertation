package com.example.huang.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TemplateActivity extends AppCompatActivity {
    Button btn_relax;
    Button btn_family;
    Button btn_culture;
    Button btn_food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        btn_relax = (Button) findViewById(R.id.btn_relax);
        btn_family = (Button) findViewById(R.id.btn_family);
        btn_culture = (Button) findViewById(R.id.btn_culture);
        btn_food = (Button) findViewById(R.id.btn_food);

        submit_search();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void submit_search() {
        btn_relax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextPage("relax");
            }
        });
        btn_family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextPage("family");
            }
        });
        btn_culture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextPage("culture");
            }
        });
        btn_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextPage("food");
            }
        });
    }

    private void startNextPage(String type) {
        Intent intent = new Intent(this, TempDetailActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.i_map).setVisible(false);
        menu.findItem(R.id.i_list).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = new Intent();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.i_home:
                finish();
                return true;
            case R.id.i_map:
                intent.putExtra("Name", "aaa");
                intent.putExtra("lat", "0");
                intent.putExtra("lng", "0");
                intent.setClass(this, MapsActivity.class);
                startActivity(intent);
                return true;
            case R.id.i_list:
                Toast.makeText(this, "This is Plan page.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
