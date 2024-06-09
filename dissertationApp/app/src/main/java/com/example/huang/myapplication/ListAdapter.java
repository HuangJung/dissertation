package com.example.huang.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.ArrayList;

/**
 * Created by huang on 2018/3/21.
 */

public class ListAdapter extends ArrayAdapter<PlaceModel> implements View.OnClickListener {

    private ArrayList<PlaceModel> dataSet;
    Context mContext;
    Activity mactivity;
    GlobalVariable global;
    private ModifyPlan modifyPlan;


    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtId;
        TextView txtLat;
        TextView txtVersion;
        TextView txtType;
        TextView txtOpennow;
        TextView txtRating;
        ImageView info;

    }

    public ListAdapter(ArrayList<PlaceModel> data, Context context, Activity activity) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.mactivity = activity;
        global = (GlobalVariable) activity.getApplicationContext();
    }


    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        PlaceModel placeModel = (PlaceModel) object;

        switch (v.getId()) {
            case R.id.item_info:
                global.getTarget_places().add(placeModel);
                modifyPlan = new ModifyPlan(mContext, mactivity);
                modifyPlan.optimizeRoute();
                break;
        }
    }


    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PlaceModel placeModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtId = (TextView) convertView.findViewById(R.id.id);
            viewHolder.txtLat = (TextView) convertView.findViewById(R.id.lat);
            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.lng);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.txtOpennow = (TextView) convertView.findViewById(R.id.opennow);
            viewHolder.txtRating = (TextView) convertView.findViewById(R.id.rating);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(placeModel.getName());
        viewHolder.txtId.setText(String.valueOf(placeModel.getId()));
        viewHolder.txtLat.setText(String.valueOf(placeModel.getLat()));
        viewHolder.txtVersion.setText(String.valueOf(placeModel.getLng()));
        viewHolder.txtType.setText(placeModel.getType());
        viewHolder.txtOpennow.setText(placeModel.getOpen_now());
        viewHolder.txtRating.setText(placeModel.getRating());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
