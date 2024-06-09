package com.example.huang.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import simplenlg.aggregation.ClauseCoordinationRule;
import simplenlg.aggregation.ForwardConjunctionReductionRule;
import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

import static java.util.Arrays.asList;

public class SummaryActivity extends AppCompatActivity {
    GlobalVariable global;
    private CustomizedPlan currentLocation;

    //SimpleNLG
    Lexicon lexicon = Lexicon.getDefaultLexicon();
    NLGFactory nlgFactory = new NLGFactory(lexicon);
    Realiser realiser = new Realiser(lexicon);
    ForwardConjunctionReductionRule fcr = new ForwardConjunctionReductionRule();
    ClauseCoordinationRule coord = new ClauseCoordinationRule();
    String json;
    JSONObject obj = null;

    private SummaryActivity.MyAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        global = (GlobalVariable) getApplicationContext();
        global.setSummaryActivity(this);
        global.setSummaryContext(this);

        String modify = (getIntent().getStringExtra("modify") != null) ? getIntent().getStringExtra("modify") : "";

        if (modify.matches("true") || global.getPlanType().matches("template")) {
            showPlan(global.getTarget_places(), this, this);
        } else {
            currentLocation = new CustomizedPlan(this, this);
            currentLocation.queryResult();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setRecyclerView(Activity activity) {
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setAdapter(String type) {
        ArrayList<PlaceModel> placeModels = global.getTarget_places();
        ArrayList<TransportModel> transportModels = global.getTarget_transport();
        ArrayList<String> list = new ArrayList<>();
        if (type.matches("create")) {
            list.add(global.getStartPoint().getName());
            for (int i = 0; i < transportModels.size(); i++) {
                String time = String.valueOf(transportModels.get(i).getDuration());
                list.add(getEmoji(transportModels.get(i).getTravel_mode()) + " " + time + " " + "mins");
                if (i < transportModels.size() - 1) {
                    list.add(placeModels.get(i).getName());
                }
            }
            list.add(global.getEndPoint().getName());

        } else if (type.matches("template")) {
            for (int i = 0; i < placeModels.size(); i++) {
                if (i == placeModels.size() - 1) {
                    list.add(placeModels.get(i).getName());
                } else {
                    list.add(placeModels.get(i).getName());
                }
                if (i < placeModels.size() - 1) {
                    String time = String.valueOf(transportModels.get(i).getDuration());
                    list.add(getEmoji(transportModels.get(i).getTravel_mode()) + " " + time + " " + "mins");
                }
            }
        }
        mAdapter = new SummaryActivity.MyAdapter(list);
    }

    public class MyAdapter extends RecyclerView.Adapter<SummaryActivity.MyAdapter.ViewHolder> {
        private ArrayList<String> mData = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView plan_num;
            public TextView mTextView;
            public ImageView delete;

            public ViewHolder(View v) {
                super(v);
                plan_num = (TextView) v.findViewById(R.id.plan_num);
                mTextView = (TextView) v.findViewById(R.id.info_text);
                delete = (ImageView) v.findViewById(R.id.item_delete);
            }
        }

        public MyAdapter(ArrayList<String> list) {
            for (String list_string : list) {
                mData.add(list_string);
            }
        }

        @Override
        public SummaryActivity.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            SummaryActivity.MyAdapter.ViewHolder vh = new SummaryActivity.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final SummaryActivity.MyAdapter.ViewHolder holder, final int position) {
            for (PlaceModel placeModel : global.getTarget_places()) {
                if (mData.get(position).matches(placeModel.getName())) {
                    holder.plan_num.setText(mData.get(position));
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.mTextView.setText(placeModel.getType());
                    break;
                }
            }
            if (position == 0) {
                holder.plan_num.setText(mData.get(position));
                holder.mTextView.setText("(start)");
            } else if (position == getItemCount() - 1) {
                holder.plan_num.setText(mData.get(position));
                holder.mTextView.setText("(end)");
            }else if(position%2==1) {
                holder.mTextView.setText(mData.get(position));
                holder.plan_num.setVisibility(View.GONE);
            }



            //region item click
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (PlaceModel placeModel : global.getTarget_places()) {
                        if (mData.get(position).matches(placeModel.getName())) {
                            Intent intent = new Intent();
                            intent.putExtra("placeId", placeModel.getId());
                            intent.putExtra("placeName", placeModel.getName());
                            intent.putExtra("lat", placeModel.getLat());
                            intent.putExtra("lng", placeModel.getLng());
                            intent.setClass(global.getSummaryContext(), PlaceDetailActivity.class);
                            global.getSummaryContext().startActivity(intent);
                            break;
                        }
                    }
                }
            });
            //endregion

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (PlaceModel placeModel : global.getTarget_places()) {
                        if (mData.get(position).matches(placeModel.getName())) {
                            //creating a popup menu
                            PopupMenu popup = new PopupMenu(global.getSummaryContext(), holder.itemView);
                            //inflating menu from xml resource
                            popup.inflate(R.menu.menu_summary);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.action_delete:
                                            //Toast.makeText(global.getSummaryContext(), "Item " + position + " is long clicked.", Toast.LENGTH_SHORT).show();
                                            for (PlaceModel placeModel : global.getTarget_places()) {
                                                if (mData.get(position).matches(placeModel.getName())) {
                                                    global.getTarget_places().remove(placeModel);
                                                    ModifyPlan modifyPlan = new ModifyPlan(global.getSummaryContext(), global.getSummaryActivity());
                                                    modifyPlan.optimizeRoute();
                                                    break;
                                                }
                                            }
                                            break;
                                    }
                                    return false;
                                }
                            });
                            //displaying the popup
                            popup.show();
                        }
                    }
                }
            });
            //region item long click
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    for (PlaceModel placeModel : global.getTarget_places()) {
                        if (mData.get(position).matches(placeModel.getName())) {
                            //creating a popup menu
                            PopupMenu popup = new PopupMenu(global.getSummaryContext(), holder.itemView);
                            //inflating menu from xml resource
                            popup.inflate(R.menu.menu_summary);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.action_delete:
                                            //Toast.makeText(global.getSummaryContext(), "Item " + position + " is long clicked.", Toast.LENGTH_SHORT).show();
                                            for (PlaceModel placeModel : global.getTarget_places()) {
                                                if (mData.get(position).matches(placeModel.getName())) {
                                                    global.getTarget_places().remove(placeModel);
                                                    ModifyPlan modifyPlan = new ModifyPlan(global.getSummaryContext(), global.getSummaryActivity());
                                                    modifyPlan.optimizeRoute();
                                                    break;
                                                }
                                            }
                                            break;
                                    }
                                    return false;
                                }
                            });
                            //displaying the popup
                            popup.show();
                            return true;
                        }
                    }
                    return false;
                }
            });
            //endregion
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    public void showPlan(ArrayList<PlaceModel> placeModel, Context c, Activity a) {
        global = (GlobalVariable) a.getApplicationContext();
        ArrayList<PlaceModel> placeModels = new ArrayList<>(placeModel);
        final Context context = c;
        final Activity activity = a;
        json = new JSONparser().JSONfileReader(context, "NLGtemplate.json");
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setAdapter(global.getPlanType());
        setRecyclerView(activity);

        TextView txt_summaryDetail = (TextView) activity.findViewById(R.id.txt_summaryDetail);
        txt_summaryDetail.setText(getSummaryDetail(global.getPlanType(), placeModels, global.getTarget_transport()));

        set_btn(activity, context);
    }

    private void set_btn(final Activity activity, final Context context) {
        FloatingActionButton fab_replan = (FloatingActionButton) activity.findViewById(R.id.fab_replan);

        fab_replan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (global.getPlanType().matches("create")) {
                    currentLocation = new CustomizedPlan(context, activity);
                    currentLocation.queryResult();
                } else if (global.getPlanType().matches("template")) {
                    /** random 0 [include] - global.getTemplateList().size() [exclude] **/
                    Random r = new Random();
                    int num = r.nextInt(global.getTemplateList().size());
                    TemplateModel templateModel = global.getTemplateList().get(num);
                    global.setTarget_places(templateModel.getPlaces());
                    global.setTarget_transport(templateModel.getTransports());
                    showPlan(global.getTarget_places(), context, activity);
                }

            }
        });

        Button btn_add = (Button) activity.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaceListActivity.class);
                context.startActivity(intent);
            }
        });

    }

    public void noPlanMessage(Activity a, Context c) {
        final Context context = c;
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent intent = new Intent(context, SearchActivity.class);
                //intent.setClass(context, SearchActivity.class);
                context.startActivity(intent);
            }
        });
        builder.setMessage("Please change preferences!")
                .setTitle("No trip plan available. ");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                if (global.getPlanType().matches("create")) {
                    intent.setClass(this, PlaceListActivity.class);
                    startActivity(intent);
                } else if (global.getPlanType().matches("template")) {
                    //finish();
                    intent.setClass(this, PlaceListActivity.class);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String simpleNLGtest(ArrayList<PlaceModel> placeModels) {
        NLGElement s1 = nlgFactory.createSentence("my dog is happy");
        String output = realiser.realiseSentence(s1);
        System.out.println(output);

        SPhraseSpec p = nlgFactory.createClause();
        p.setSubject("Mary");
        p.setVerb("chase");
        p.setObject("the monkey");
        p.setFeature(Feature.TENSE, Tense.PAST); //past tense
        p.setFeature(Feature.NEGATED, true); //negative form
        p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO); // Y/N question
        p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT); //W question
        p.addComplement("very quickly"); // Adverb phrase, passed as a string
        p.addComplement("despite her exhaustion"); // Prepositional phrase, string
        String output2 = realiser.realiseSentence(p); // Realiser created earlier.
        System.out.println(output2);

        SPhraseSpec p1 = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("Mary");
        NPPhraseSpec object = nlgFactory.createNounPhrase("the monkey");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("chase");
        subject.addModifier("fast");
        verb.addModifier("quickly");
        p1.setSubject(subject);
        p1.setObject(object);
        p1.setVerb(verb);
        String output3 = realiser.realiseSentence(p1); // Realiser created earlier.
        System.out.println(output3);

        SPhraseSpec p2 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("Mary");
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("your", "giraffe");
        CoordinatedPhraseElement subj = nlgFactory.createCoordinatedPhrase(subject1, subject2);
        // may revert to nlgFactory.createCoordinatedPhrase( subject1, subject2 ) ;
        p2.setSubject(subj);
        p2.setVerb(verb);
        NPPhraseSpec object1 = nlgFactory.createNounPhrase("the monkey");
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("George");
        CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase(object1, object2);
        // may revert to nlgFactory.createCoordinatedPhrase( subject1, subject2 ) ;
        coord.addCoordinate("Martha");
        coord.setFeature(Feature.CONJUNCTION, "or");
        p2.setObject(coord);
        String output4 = realiser.realiseSentence(p2);
        System.out.println(output4);

        SPhraseSpec p3 = nlgFactory.createClause();
        p3.setSubject("Mary");
        p3.setVerb("chase");
        p3.setObject("the monkey");
        //method1
        //p3.addComplement("in the zoo");
        //method2
        //PPPhraseSpec pp = nlgFactory.createPrepositionPhrase("in","the park");
        //method3
        NPPhraseSpec place = nlgFactory.createNounPhrase("garden");
        place.setDeterminer("the");
        place.addPreModifier("leafy");
        PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
        pp.addComplement(place);
        pp.setPreposition("in");
        p3.addComplement(pp);
        String output5 = realiser.realiseSentence(p3); // Realiser created earlier.
        System.out.println(output5);

        SPhraseSpec p4 = nlgFactory.createClause("my cat", "like", "fish");
        SPhraseSpec p5 = nlgFactory.createClause("my dog", "like", "big bones");
        SPhraseSpec p6 = nlgFactory.createClause("my horse", "like", "grass");
        CoordinatedPhraseElement c = nlgFactory.createCoordinatedPhrase();
        c.addCoordinate(p4);
        c.addCoordinate(p5);
        c.addCoordinate(p6);
        c.setConjunction("or"); //default is "and"
        String output6 = realiser.realiseSentence(c); // Realiser created earlier.
        System.out.println(output6);

        SPhraseSpec p7 = nlgFactory.createClause("I", "be", "happy");
        SPhraseSpec p8 = nlgFactory.createClause("I", "eat", "fish");
        p8.setFeature(Feature.COMPLEMENTISER, "because"); //e.g. while, because
        p8.setFeature(Feature.TENSE, Tense.PAST);
        p7.addComplement(p8);
        String output7 = realiser.realiseSentence(p7);  //Realiser created earlier
        System.out.println(output7);

        DocumentElement d1 = nlgFactory.createSentence(p);
        DocumentElement d2 = nlgFactory.createSentence(p1);
        DocumentElement d3 = nlgFactory.createSentence(p2);
        DocumentElement d4 = nlgFactory.createSentence(p3);
        DocumentElement d5 = nlgFactory.createSentence(c);
        DocumentElement d6 = nlgFactory.createSentence(p7);
        DocumentElement par1 = nlgFactory.createParagraph(asList(d1, d2, d3, d4, d5, d6));
        DocumentElement section = nlgFactory.createSection("The Test Summary");
        section.addComponent(par1);
        String output8 = realiser.realise(section).getRealisation();
        System.out.println(output8);

        return output8;
        //placeModels.add(new PlaceModel(output8, 0, 0, "test","type","true","0"));
    }

    public DocumentElement getActList(ArrayList<PlaceModel> placeModels) {
        ArrayList<String> act_list = new ArrayList<>();
        Set set = new LinkedHashSet<String>();

        for (int i = 0; i < placeModels.size(); i++) {
            String place_type = placeModels.get(i).getType();
            if (obj.has(place_type)) {
                set.add(place_type);
            } else {
                set.add("others");
            }
        }
        act_list.addAll(set);

        DocumentElement par = nlgFactory.createParagraph();
        SPhraseSpec p = nlgFactory.createClause();
        p.setSubject("you");
        p.setVerb("can");
        CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
        for (int i = 0; i < act_list.size(); i++) {
            String place_type = act_list.get(i);
            try {
                JSONObject typeObj = obj.getJSONObject(place_type);
                JSONArray listOArry = typeObj.getJSONArray("list_object");
                Random random = new Random();
                int num = random.nextInt(listOArry.length());
                String o = listOArry.getString(num);
                coord.addCoordinate(o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        p.setObject(coord);
        String duration = (global.getDuration() != 0) ? String.valueOf(global.getDuration() / 60) + " hour" : "all day";
        p.addComplement("in this " + duration + " trip");
        String rSentence = realiser.realiseSentence(p);
        DocumentElement cSentence = nlgFactory.createSentence(rSentence);
        par.addComponent(cSentence);

        return par;
    }

    public String getSummaryDetail(String type, ArrayList<PlaceModel> placeModels, ArrayList<TransportModel> transportModels) {
        String result = "";
        String s;
        DocumentElement section = null;
        DocumentElement par;
        SPhraseSpec p;
        String sentence;
        if (type.matches("create")) {
            section = nlgFactory.createSection("CUSTOMIZED PLAN\n");
            par = getActList(placeModels);
            section.addComponent(par);

            //region startPoint
            DocumentElement start_section = nlgFactory.createSection("start: " + getEmoji("clock") + global.getStartTime());
            par = nlgFactory.createParagraph();
            DocumentElement start_place = nlgFactory.createSentence(getEmoji("spot_se") + "<<" + global.getStartPoint().getName() + ">>");
            par.addComponent(start_place);
            start_section.addComponent(par);
            section.addComponent(start_section);
            //endregion

            for (int i = 0; i < transportModels.size(); i++) {
                par = nlgFactory.createParagraph();
                ArrayList<Steps> steps = transportModels.get(i).getSteps();
                NLGElement transport;

                //region transport
                if (steps != null) {
                    for (int j = 0; j < steps.size(); j++) {
                        String duration = String.valueOf(steps.get(j).getDuration());
                        if (steps.get(j).getTransitDetails() != null) {
                            String departure_stop = steps.get(j).getTransitDetails().getDeparture_stop();
                            String arrival_stop = steps.get(j).getTransitDetails().getArrival_stop();
                            String vehicle_name = steps.get(j).getTransitDetails().getVehicle_name();
                            String vehicle_type = steps.get(j).getTransitDetails().getVehicle_type();
                            s = "\n" + getEmoji(vehicle_type) + "take " + vehicle_type + "[" + vehicle_name + "] to " + arrival_stop + " <" + duration + " mins>";
                        } else {
                            String instruction = steps.get(j).getHtml_instructions();
                            s = "\n" + getEmoji("walking") + instruction + " <" + duration + " mins>";
                        }
                        transport = nlgFactory.createSentence(s);
                        par.addComponent(transport);
                    }
                } else {
                    String mode = transportModels.get(i).getTravel_mode();
                    String time = String.valueOf(transportModels.get(i).getDuration());
                    s = getEmoji(mode) + mode + " " + time + " " + "mins";
                    transport = nlgFactory.createSentence(s);
                    par.addComponent(transport);
                }
                section.addComponent(par);
                //endregion

                //region spots
                if (i < transportModels.size() - 1) {
                    DocumentElement spot_section = nlgFactory.createSection(getEmoji("spot") + (i + 1) + ":<<" + placeModels.get(i).getName() + ">>");

                    par = getIntro(placeModels.get(i));

                    spot_section.addComponent(par);
                    section.addComponent(spot_section);
                }
                //endregion
            }

            //region endPoint
            DocumentElement end_section = nlgFactory.createSection(getEmoji("spot_se") + "<<" + global.getEndPoint().getName() + ">>");
            par = nlgFactory.createParagraph();
            DocumentElement end_place = nlgFactory.createSentence("end: " + getEmoji("clock") + global.getEndTime());
            par.addComponent(end_place);
            end_section.addComponent(par);
            section.addComponent(end_section);
            //endregion

        } else if (type.matches("template")) {
            section = nlgFactory.createSection(global.getTemplateList().get(0).getPlanType() + " PLAN\n");
            par = getActList(placeModels);
            section.addComponent(par);

            for (int i = 0; i < placeModels.size(); i++) {
                //region spots
                DocumentElement spot_section = nlgFactory.createSection(getEmoji("spot") + (i + 1) + ":<<" + placeModels.get(i).getName() + ">>");

                par = getIntro(placeModels.get(i));

                spot_section.addComponent(par);
                section.addComponent(spot_section);
                //endregion

                //region transport
                if (i < placeModels.size() - 1) {
                    par = nlgFactory.createParagraph();
                    ArrayList<Steps> steps = transportModels.get(i).getSteps();
                    NLGElement transport;
                    if (steps != null) {
                        for (int j = 0; j < steps.size(); j++) {
                            String duration = String.valueOf(steps.get(j).getDuration());
                            if (steps.get(j).getTransitDetails() != null) {
                                String departure_stop = steps.get(j).getTransitDetails().getDeparture_stop();
                                String arrival_stop = steps.get(j).getTransitDetails().getArrival_stop();
                                String vehicle_name = steps.get(j).getTransitDetails().getVehicle_name();
                                String vehicle_type = steps.get(j).getTransitDetails().getVehicle_type();
                                s = "\n" + getEmoji(vehicle_type) + "take " + vehicle_type + "[" + vehicle_name + "] to " + arrival_stop + " <" + duration + " mins>";
                            } else {
                                String instruction = steps.get(j).getHtml_instructions();
                                s = "\n" + getEmoji("walking") + instruction + " <" + duration + " mins>";
                            }
                            transport = nlgFactory.createSentence(s);
                            par.addComponent(transport);
                        }
                    } else {
                        String mode = transportModels.get(i).getTravel_mode();
                        String time = String.valueOf(transportModels.get(i).getDuration());
                        s = getEmoji(mode) + mode + " " + time + " " + "mins";
                        transport = nlgFactory.createSentence(s);
                        par.addComponent(transport);
                    }
                    section.addComponent(par);
                }
                //endregion
            }
        }

        result = realiser.realise(section).getRealisation();
        return result;
    }

    public String getEmoji(String transport) {
        String mode = transport;
        switch (transport.toLowerCase()) {
            case "bus":
                mode = new String(Character.toChars(0x1F68C));
                break;
            case "rail":
            case "train":
            case "tram":
                mode = new String(Character.toChars(0x1F686));
                break;
            case "driving":
                mode = new String(Character.toChars(0x1F698));
                break;
            case "walking":
                mode = new String(Character.toChars(0x1F463));
                break;
            case "bicycling":
                mode = new String(Character.toChars(0x1F6B4));
                break;
            case "spot":
                mode = new String(Character.toChars(0x1F6A9));
                break;
            case "spot_se":
                mode = new String(Character.toChars(0x26F3));
                break;
            case "down":
                mode = new String(Character.toChars(0x2B07));
                break;
            case "clock":
                mode = new String(Character.toChars(0x1F55C));
                break;
        }
        return mode;
    }

    public DocumentElement getIntro(PlaceModel placeModel) {
        DocumentElement par = nlgFactory.createParagraph();
        try {
            String type = placeModel.getType();

            //region set type category
            /**
             switch (placeModel.getType()) {
             case "shopping_mall":
             type = "shopping_mall";
             break;
             case "spa":
             case "beauty_salon":
             type = "spa";
             break;
             case "cafe":
             case "bar":
             case "restaurant":
             type = "restaurant";
             break;
             case "casino":
             type = "casino";
             break;
             case "night_club":
             type = "night_club";
             break;
             case "park":
             type = "park";
             break;
             case "museum":
             case "art_gallery":
             case "library":
             type = "museum";
             break;
             case "zoo":
             case "aquarium":
             type = "zoo";
             break;
             case "church":
             type = "church";
             break;
             default:
             type = "others";
             }
             **/
            //endregion

            JSONObject typeObj;
            if (obj.has(type)) {
                typeObj = obj.getJSONObject(type);
            } else {
                typeObj = obj.getJSONObject("others");
            }
            SPhraseSpec p1;
            SPhraseSpec p2;
            String rSentence;
            DocumentElement cSentence;
            Random random;

            //region 1st sentence: Y/N question or WHO question
            JSONArray firstArry = typeObj.getJSONArray("first_sentence");
            random = new Random();
            //random choose sentence
            int s_num = random.nextInt(firstArry.length());
            //random choose question type
            int q_num = random.nextInt(2);

            String s = (firstArry.getJSONObject(s_num).has("subject")) ? firstArry.getJSONObject(s_num).getString("subject") : null;
            String v = (firstArry.getJSONObject(s_num).has("verb")) ? firstArry.getJSONObject(s_num).getString("verb") : null;
            String o = (firstArry.getJSONObject(s_num).has("object")) ? firstArry.getJSONObject(s_num).getString("object") : null;
            String c = (firstArry.getJSONObject(s_num).has("complement")) ? firstArry.getJSONObject(s_num).getString("complement") : null;

            p1 = nlgFactory.createClause(s, v, o);
            if (c != null) {
                p1.addComplement(c);
            }
            if (q_num == 0) {
                p1.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT); //WHO question
            } else {
                p1.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO); // Y/N question
            }
            rSentence = realiser.realiseSentence(p1);
            cSentence = nlgFactory.createSentence(rSentence);
            par.addComponent(cSentence);
            //endregion

            //region 2nd sentence: describe the place type (+ rating score)
            p1 = nlgFactory.createClause();
            p1.setSubject(nlgFactory.createNounPhrase(placeModel.getName()));
            p1.setVerbPhrase(nlgFactory.createVerbPhrase("be"));
            NPPhraseSpec object = nlgFactory.createNounPhrase("a", placeModel.getType());

            //region random choose place_modifier
            JSONArray modifierArry = typeObj.getJSONArray("modifier");
            random = new Random();
            int num = random.nextInt(modifierArry.length());
            object.addModifier(modifierArry.getString(num));
            //endregion

            p1.setObject(object);
            //if rating is not null -> add which complement
            if (placeModel.getRating() != null && !placeModel.getRating().matches("")) {
                p2 = nlgFactory.createClause(null, "get", placeModel.getRating() + " rating scores");
                p2.addComplement("from google");
                p2.setFeature(Feature.COMPLEMENTISER, "which");
                p1.addComplement(p2);

                //NLGElement aggregated = (NLGElement) this.fcr.apply(p1, p2);
                //rSentence = realiser.realiseSentence(aggregated);
            }
            rSentence = realiser.realiseSentence(p1);
            cSentence = nlgFactory.createSentence(rSentence);
            par.addComponent(cSentence);
            //endregion

            //region 3rd sentence: canned sentence
            JSONArray thirdArry = typeObj.getJSONArray("third_sentence");
            random = new Random();
            num = random.nextInt(thirdArry.length());
            NLGElement third = nlgFactory.createSentence(thirdArry.getString(num));
            par.addComponent(third);
            //endregion

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return par;
    }
}
