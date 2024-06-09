package com.example.huang.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.huang.myapplication.getJsonString.GET;

public class PlaceListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    GlobalVariable global;
    SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<PlaceModel> placeModels;
    ListView listView;
    private static ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        global = (GlobalVariable) getApplicationContext();

        global.setMainActivity(this);
        global.setMainContext(this);

        if (global.getPlaces_list() == null) {
            queryResult();
        } else {
            showPlacesList(global.getPlaces_list());
            //showPlacesList(global.getTarget_places());
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors( //重整的圖示用藍色
                Color.BLUE,
                Color.GREEN,
                Color.RED
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void queryResult() {
        try {
            String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyCQSk-KxMLZJGkWO9iH72SU7hVUlpAGTrU&language=en-GB";
            url += "&query=attractions+in+Manchester";

            new PlaceListActivity.HttpAsync().execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HttpAsync extends AsyncTask<String, Void, String> {
        ArrayList<PlaceModel> placeModels=new ArrayList<>();

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            for (String url : urls) {
                result = GET(url);
                placeModels.addAll(new JSONparser().informList(result));
            }
            global.setPlaces_list(placeModels);
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            showPlacesList(global.getPlaces_list());
        }
    }

    @Override
    public void onRefresh() {
        //currentLocation.getCurrentLocation(); //下載並更新列表的資料
        showPlacesList(global.getPlaces_list());
        //showPlacesList(global.getTarget_places());
        //currentLocation.queryResult();
        swipeRefreshLayout.setRefreshing(false);
    }


    public void showPlacesList(ArrayList<PlaceModel> placeModel) {
        placeModels = new ArrayList<>(placeModel);
        placeModels.removeAll(global.getTarget_places());
        listView = (ListView) findViewById(R.id.list);
        final Context context = this;

        adapter = new ListAdapter(placeModels, this,this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceModel placeModel = placeModels.get(position);
                startNewPage(placeModel, context);
            }
        });
    }

    private void startNewPage(PlaceModel placeModel, Context context) {
        Intent intent = new Intent(context, PlaceDetailActivity.class);
        intent.putExtra("placeId", placeModel.getId());
        intent.putExtra("placeName", placeModel.getName());
        intent.putExtra("lat", placeModel.getLat());
        intent.putExtra("lng", placeModel.getLng());
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.i_map).setVisible(false);
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
                intent.setClass(this, categoryActivity.class);
                startActivity(intent);
                return true;
            case R.id.i_map:
                intent.putExtra("Name", "aaa");
                intent.putExtra("lat", "0");
                intent.putExtra("lng", "0");
                intent.setClass(this, MapsActivity.class);
                startActivity(intent);
                return true;
            case R.id.i_list:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
