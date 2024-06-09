package com.example.huang.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.huang.myapplication.getJsonString.GET;
import static com.example.huang.myapplication.getJsonString.GETheader;

public class PlaceDetailActivity extends AppCompatActivity {
    String placeId;
    String placeName;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        placeId = getIntent().getStringExtra("placeId");
        placeName = getIntent().getStringExtra("placeName");
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);


        TextView txt_placeName = (TextView) findViewById(R.id.txt_placeName);
        txt_placeName.setText(placeName);

        queryResult();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void queryResult() {
        try {
            String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&language=en-GB&key=AIzaSyCRPQ9IzVW0DUnGZdXuXiKh20jC4_Ry8lg";
            String url2 = "https://api.sygictravelapi.com/1.0/en/places/list?query=" + URLEncoder.encode(placeName, "UTF-8"); //Sygic
            String url3 = "https://api.foursquare.com/v2/venues/search?ll=" + lat + "," + lng + "&query=" + URLEncoder.encode(placeName, "UTF-8")
                    + "&oauth_token=GMOWBRUDSJLDF4YKKK5PXQN4231KIAKPQC5LPX3NVUBFCH1Q&v=20180711"; //foursquare
            new HttpAsync().execute(url, url2, url3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HttpAsync extends AsyncTask<String, Void, String> {
        PlaceDetailModel placeDetailModel;

        @Override
        protected String doInBackground(String... urls) {
            String result = GET(urls[0]);
            PlaceDetailModel pm = new JSONparser().getPlaceDetail(result);
            result = GETheader(urls[1], "x-api-key", "j6f2FNAUxT6Edxzb2H9vP5IhD0eq0aF61eT190A2"); //Sygit
            PlaceDetailModel pm2 = new JSONparser().getPlaceDetailbySygic(result, pm);
            result = GET(urls[2]); //foursquare get id
            String id = new JSONparser().getPlaceIdbyFS(result);
            if(id!=null){
                result = GET("https://api.foursquare.com/v2/venues/"+id+"?&oauth_token=GMOWBRUDSJLDF4YKKK5PXQN4231KIAKPQC5LPX3NVUBFCH1Q&v=20180711");
                placeDetailModel = new JSONparser().getPlaceDetailbyFS(result, pm2);
            }else{
                placeDetailModel = pm2;
            }

            return GET(urls[0]);
        }

        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        private ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbHeaderProgress);
        private TextView txtProgress = (TextView) findViewById(R.id.txtProgress);
        private int pStatus = 0;
        private Handler handler = new Handler();

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (pStatus <= 100) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(pStatus);
                                //txtProgress.setText(pStatus + " %");
                                txtProgress.setText("loading...");
                            }
                        });
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pStatus++;
                    }
                }
            }).start();
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //placeDetailModel = new JSONparser().getPlaceDetail(result);
            setText(placeDetailModel);
            // HIDE THE SPINNER AFTER LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    public void setText(PlaceDetailModel placeDetailModel) {
        TextView txt_placeAddress = (TextView) findViewById(R.id.txt_placeAddress);
        TextView txt_placePhone = (TextView) findViewById(R.id.txt_placePhone);
        TextView txt_placeWeekday = (TextView) findViewById(R.id.txt_placeWeekday);
        TextView txt_placeDuration = (TextView) findViewById(R.id.txt_placeDuration);
        TextView txt_placeDescription = (TextView) findViewById(R.id.txt_placeDescription);
        TextView txt_placeVisitsCount = (TextView) findViewById(R.id.txt_placeVisitsCount);
        TextView txt_placeRating = (TextView) findViewById(R.id.txt_placeRating);
        TextView txt_placeLikes = (TextView) findViewById(R.id.txt_placeLikes);
        txt_placeAddress.setText(placeDetailModel.getAddress());
        txt_placePhone.setText(placeDetailModel.getPhone());
        String weekday = "";
        for (int i = 0; i < placeDetailModel.getWeekday().size(); i++) {
            weekday += placeDetailModel.getWeekday().get(i) + "\n";
        }
        if (weekday.matches("")) {
            txt_placeWeekday.setVisibility(View.GONE);
        } else {
            txt_placeWeekday.setText(weekday);
        }

        if (placeDetailModel.getDuration() != null && !placeDetailModel.getDuration().matches("")) {
            txt_placeDuration.setText("visitors usually stay " + placeDetailModel.getDuration());
        } else {
            txt_placeDuration.setVisibility(View.GONE);
        }

        if (placeDetailModel.getDescription() == null || placeDetailModel.getDescription().matches("null")) {
            txt_placeDescription.setVisibility(View.GONE);
        } else {
            txt_placeDescription.setText(placeDetailModel.getDescription());
        }

        if (placeDetailModel.getVisitsCount() != null) {
            txt_placeVisitsCount.setText(placeDetailModel.getVisitsCount());
        } else {
            txt_placeVisitsCount.setVisibility(View.GONE);
        }

        if (placeDetailModel.getRating() != null) {
            txt_placeRating.setText(placeDetailModel.getRating());
        } else {
            txt_placeRating.setVisibility(View.GONE);
        }

        if (placeDetailModel.getLikes() != null) {
            txt_placeLikes.setText(placeDetailModel.getLikes());
        } else {
            txt_placeLikes.setVisibility(View.GONE);
        }

        for (Reviews review : placeDetailModel.getReviews()) {
            TextView txt_review = new TextView(this);
            String r = "";
            r += "author: " + review.getAuthor_name() + "\n";
            r += "rating: " + review.getRating() + " (" + review.getRelative_time() + ")\n";
            r += "\n" + review.getText();
            txt_review.setText(r);
            LinearLayout layout = findViewById(R.id.reviews_layout);
            layout.addView(txt_review);
        }

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
