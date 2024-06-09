package com.example.huang.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TempDetailActivity extends AppCompatActivity {
    private MyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    public GlobalVariable global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        global = (GlobalVariable) getApplicationContext();
        String type = getIntent().getStringExtra("type");
        switch (type) {
            case "relax":
                setTitle("relax & entertainment");
                break;
            case "family":
                setTitle("family & kid");
                break;
            case "culture":
                setTitle("culture & knowledge");
                break;
            case "food":
                setTitle("food & drink");
                break;
        }
        setAdapter(type);
        setRecyclerView();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setAdapter(String type) {
        ArrayList<TemplateModel> templates = new ArrayList<>();
        String json = new JSONparser().JSONfileReader(this,"PlanTemplate.json");
        templates.addAll(new JSONparser().getTemplatePlans(type, json));
        mAdapter = new MyAdapter(templates);
        global.setTemplateList(templates);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        ArrayList<TemplateModel> templateList;
        private ArrayList<String> mData = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView plan_num;
            public TextView mTextView;

            public ViewHolder(View v) {
                super(v);
                plan_num = (TextView) v.findViewById(R.id.plan_num);
                mTextView = (TextView) v.findViewById(R.id.info_text);
            }
        }

        public MyAdapter(ArrayList<TemplateModel> templates) {
            templateList = new ArrayList<>(templates);
            for (TemplateModel template : templates) {
                ArrayList<PlaceModel> places = template.getPlaces();
                String place_string = "";
                for (int i = 0; i < places.size(); i++) {
                    if (i == places.size() - 1) {
                        place_string += places.get(i).getName();
                    } else {
                        place_string += places.get(i).getName() + " -> ";
                    }
                }
                mData.add(place_string);
            }
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.plan_num.setText("Plan " + (position + 1));
            holder.mTextView.setText(mData.get(position));
            //region item click
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TemplateModel templateModel = templateList.get(position);
                    global.setTarget_places(templateModel.getPlaces());
                    global.setTarget_transport(templateModel.getTransports());

                    startNextPage();
                }
            });
            //endregion
            //region item long click
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(TempDetailActivity.this, "Item " + position + " is long clicked.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            //endregion
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private void startNextPage() {
        global.setPlanType("template");
        Intent intent = new Intent();
        intent.setClass(this, SummaryActivity.class);
        startActivity(intent);
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
