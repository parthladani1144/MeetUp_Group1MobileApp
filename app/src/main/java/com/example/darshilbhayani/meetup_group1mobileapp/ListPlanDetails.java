package com.example.darshilbhayani.meetup_group1mobileapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ListPlanDetails extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mtoggle;
    private ListPlanDetails curr;

    private ListView rowDataView;
    private rowDataAdapter adapter;
    private List<rowData> rowDataList;

    private FirebaseDatabase mFireBaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private DatabaseReference mDatabase;

    HashMap<String,Event> event = new HashMap<>();
    HashMap<String, Integer> drawableImg = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_plans);

        //-- Drawer button --
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout_planHistory);
        mtoggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mtoggle);
        mtoggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //-- Drawer Button --

        //----Navigation Drawer---

        Intent i = new Intent(this, MapsActivity.class);
        curr = this;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_bar_planHistory);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                int id = item.getItemId();
                Intent i;

                if (id == R.id.nav_Map) {
                    i = new Intent(curr, MapsActivity.class);
                    startActivity(i);

                } else if (id == R.id.nav_settings) {
                    i=new Intent(curr,Settings_main.class);
                    startActivity(i);
                }

                else if (id == R.id.nav_Addplan) {
                    i = new Intent(curr, CreatePage1.class);
                    startActivity(i);
                }
                else if (id == R.id.nav_myPlan) {
                    i = new Intent(curr, MyPlanHistory.class);
                    startActivity(i);
                }
                else if (id == R.id.nav_planHistory) {
                    i = new Intent(curr, ListPlanDetails.class);
                    startActivity(i);
                }
                else if (id == R.id.nav_profile) {
                    i = new Intent(curr, ProfilePage.class);
                    startActivity(i);
                }
                else if (id == R.id.nav_logout) {
                    i = new Intent(curr, LoginDemo.class);
                    SharedPreferences.Editor editor = getSharedPreferences(LoginDemo.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("logged_in", "No");
                    editor.putString("Email_ID","");
                    editor.apply();
                    startActivity(i);
                }

                return true;
            }

        });

        //----Navigation Drawer---


        drawableImg.put("a",R.drawable.a);
        drawableImg.put("b",R.drawable.b);
        drawableImg.put("c",R.drawable.c);
        drawableImg.put("d",R.drawable.d);
        drawableImg.put("e",R.drawable.e);
        drawableImg.put("f",R.drawable.f);
        drawableImg.put("g",R.drawable.g);
        drawableImg.put("h",R.drawable.h);
        drawableImg.put("i",R.drawable.i);
        drawableImg.put("j",R.drawable.j);
        drawableImg.put("k",R.drawable.k);
        drawableImg.put("l",R.drawable.l);
        drawableImg.put("m",R.drawable.m);
        drawableImg.put("n",R.drawable.n);
        drawableImg.put("o",R.drawable.o);
        drawableImg.put("p",R.drawable.p);
        drawableImg.put("q",R.drawable.q);
        drawableImg.put("r",R.drawable.r);
        drawableImg.put("s",R.drawable.s);
        drawableImg.put("t",R.drawable.t);
        drawableImg.put("u",R.drawable.u);
        drawableImg.put("v",R.drawable.v);
        drawableImg.put("w",R.drawable.w);
        drawableImg.put("x",R.drawable.x);
        drawableImg.put("y",R.drawable.y);
        drawableImg.put("z",R.drawable.z);

        rowDataView = findViewById(R.id.listViewProduct);
        rowDataList = new ArrayList<>();

        fetchData();

        Button btnAll = findViewById(R.id.button_all);
        Button btnFood = findViewById(R.id.Button_food);
        Button btnEnter = findViewById(R.id.Button_Enter);
        Button btnSports = findViewById(R.id.Button_sport);
        Button btnStudy = findViewById(R.id.Button_reading);
        Button btnTravel = findViewById(R.id.Button_travel);
        Button btnOthers = findViewById(R.id.button_others);


        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowDataList.clear();
                rowDataView.setAdapter(null);

                int i=1;
                for(Map.Entry<String,Event> evnData : event.entrySet()) {

                    Event e1 = evnData.getValue();
                    rowDataList.add(new rowData(i, e1.getEvent_name(),
                            drawableImg.get(String.valueOf(e1.getEvent_name().charAt(0)).toLowerCase()),
                            e1.getEvent_date(), e1.getEvent_time(), e1.getEvent_duration()));
                }

                adapter = new rowDataAdapter(getApplicationContext(), rowDataList);
                rowDataView.setAdapter(adapter);
            }
        });

        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowDataList.clear();
                rowDataView.setAdapter(null);

                int i=1;
                for(Map.Entry<String,Event> evnData : event.entrySet()) {

                    Event e1 = evnData.getValue();
                    if(e1.getEvent_type().equals("Food")) {
                        rowDataList.add(new rowData(i, e1.getEvent_name(),
                                drawableImg.get(String.valueOf(e1.getEvent_name().charAt(0)).toLowerCase()),
                                e1.getEvent_date(), e1.getEvent_time(), e1.getEvent_duration()));
                    }
                }

                adapter = new rowDataAdapter(getApplicationContext(), rowDataList);
                rowDataView.setAdapter(adapter);
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowDataList.clear();
                rowDataView.setAdapter(null);

                int i=1;
                for(Map.Entry<String,Event> evnData : event.entrySet()) {

                    Event e1 = evnData.getValue();
                    if(e1.getEvent_type().equals("Entertainment")) {
                        rowDataList.add(new rowData(i, e1.getEvent_name(),
                                drawableImg.get(String.valueOf(e1.getEvent_name().charAt(0)).toLowerCase()),
                                e1.getEvent_date(), e1.getEvent_time(), e1.getEvent_duration()));
                    }
                }

                adapter = new rowDataAdapter(getApplicationContext(), rowDataList);
                rowDataView.setAdapter(adapter);
            }
        });

        btnSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowDataList.clear();
                rowDataView.setAdapter(null);

                int i=1;
                for(Map.Entry<String,Event> evnData : event.entrySet()) {

                    Event e1 = evnData.getValue();
                    if(e1.getEvent_type().equals("Sports")) {
                        rowDataList.add(new rowData(i, e1.getEvent_name(),
                                drawableImg.get(String.valueOf(e1.getEvent_name().charAt(0)).toLowerCase()),
                                e1.getEvent_date(), e1.getEvent_time(), e1.getEvent_duration()));
                    }
                }

                adapter = new rowDataAdapter(getApplicationContext(), rowDataList);
                rowDataView.setAdapter(adapter);
            }
        });

        btnStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowDataList.clear();
                rowDataView.setAdapter(null);

                int i=1;
                for(Map.Entry<String,Event> evnData : event.entrySet()) {

                    Event e1 = evnData.getValue();
                    if(e1.getEvent_type().equals("Study")) {
                        rowDataList.add(new rowData(i, e1.getEvent_name(),
                                drawableImg.get(String.valueOf(e1.getEvent_name().charAt(0)).toLowerCase()),
                                e1.getEvent_date(), e1.getEvent_time(), e1.getEvent_duration()));
                    }
                }

                adapter = new rowDataAdapter(getApplicationContext(), rowDataList);
                rowDataView.setAdapter(adapter);
            }
        });

        btnTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowDataList.clear();
                rowDataView.setAdapter(null);

                int i=1;
                for(Map.Entry<String,Event> evnData : event.entrySet()) {

                    Event e1 = evnData.getValue();
                    if(e1.getEvent_type().equals("Carpool")) {
                        rowDataList.add(new rowData(i, e1.getEvent_name(),
                                drawableImg.get(String.valueOf(e1.getEvent_name().charAt(0)).toLowerCase()),
                                e1.getEvent_date(), e1.getEvent_time(), e1.getEvent_duration()));
                    }
                }

                adapter = new rowDataAdapter(getApplicationContext(), rowDataList);
                rowDataView.setAdapter(adapter);
            }
        });

        btnOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowDataList.clear();
                rowDataView.setAdapter(null);

                int i=1;
                for(Map.Entry<String,Event> evnData : event.entrySet()) {

                    Event e1 = evnData.getValue();
                    if(e1.getEvent_type().equals("Other")) {
                        rowDataList.add(new rowData(i, e1.getEvent_name(),
                                drawableImg.get(String.valueOf(e1.getEvent_name().charAt(0)).toLowerCase()),
                                e1.getEvent_date(), e1.getEvent_time(), e1.getEvent_duration()));
                    }
                }

                adapter = new rowDataAdapter(getApplicationContext(), rowDataList);
                rowDataView.setAdapter(adapter);
            }
        });

        rowDataView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event edit_event_data = event.get(String.valueOf(view.getTag()));

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
               /* int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);*/

                int year = 2018;
                int month = 6;
                int date = 8;

                String[] event_date = edit_event_data.getEvent_date().split("-");
                Log.d("today date",year+"-"+month+"-"+date);
                Log.d("event_date",event_date[0]+"-"+event_date[1]+"-"+event_date[2]);

                Boolean flag = false;

                SharedPreferences editor = getApplicationContext().getSharedPreferences(LoginDemo.MY_PREFS_NAME, MODE_PRIVATE);
                String loogedInUser = editor.getString("Email_ID","darshilbhayani1992@gmail.com");

                if(edit_event_data.getEmail_id().equals(loogedInUser)) {
                    flag = true;
                }else {
                    Toast.makeText(ListPlanDetails.this,"You are not authorised to edit the event!",Toast.LENGTH_LONG).show();
                    return;
                }

                if(Integer.valueOf(event_date[0])>=year){

                    if(Integer.valueOf(event_date[1])>=month){

                        if(Integer.valueOf(event_date[2])>=date){
                            Log.d("date","Event can be edited");
                        }else{
                            Log.d("date",date+"-"+event_date[2]);
                            Toast.makeText(ListPlanDetails.this,"Event Expired. Can not be Edited.",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else{
                        Log.d("month",month+"-"+event_date[1]);
                        Toast.makeText(ListPlanDetails.this,"Event Expired. Can not be Edited.",Toast.LENGTH_LONG).show();
                        return;
                    }
                }else {
                    Log.d("year",year+"-"+event_date[0]);
                    Toast.makeText(ListPlanDetails.this,"Event Expired. Can not be Edited.",Toast.LENGTH_LONG).show();
                    return;
                }

                try {

                    if(flag) {
                        Intent i = new Intent(getApplicationContext(), EditPlan.class);
                        i.putExtra("EVENT", edit_event_data);
                        i.putExtra("ID", String.valueOf(view.getTag()));
                        startActivity(i);
                    }
                }
                catch (Exception e){

                }
            }
        });
    }

    private void fetchData() {

        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();
        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFireBaseDatabase.getReference();

        FirebaseUser user = mAuth.getCurrentUser();
        //uId = user.getUid();

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d("User!",user.getUid());
                }else{
                    Log.d("User","User is null!");
                }
            }
        };

        mDatabase.child("event").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Total Record...!!",dataSnapshot.getChildrenCount()+"");
                for(DataSnapshot eventIDDataSet : dataSnapshot.getChildren())
                {
                    // Log.i(eventIDDataSet.getKey(),eventIDDataSet.getChildrenCount() + "");
                    Event e1 = new Event();
                    for(DataSnapshot eventDatalocal : eventIDDataSet.getChildren())
                    {
                        /*if(eventDatalocal.getKey().equals("ppl_joined"))
                             Log.i(eventDatalocal.getKey()+"**","--"+eventDatalocal.getValue().toString());*/

                        DataSnapshot eventData = eventDatalocal;

                        if(eventData.getKey().equals("email_id"))
                            e1.setEmail_id(eventData.getValue().toString());
                        if(eventData.getKey().equals("event_date"))
                            e1.setEvent_date(eventData.getValue().toString());
                        if(eventData.getKey().equals("event_dest"))
                            e1.setEvent_dest(eventData.getValue().toString());
                        if(eventData.getKey().equals("event_duration"))
                            e1.setEvent_duration(eventData.getValue().toString());
                        if(eventData.getKey().equals("event_name"))
                            e1.setEvent_name(eventData.getValue().toString());
                        if(eventData.getKey().equals("event_source"))
                            e1.setEvent_source(eventData.getValue().toString());
                        if(eventData.getKey().equals("event_time"))
                            e1.setEvent_time(eventData.getValue().toString());
                        if(eventData.getKey().equals("event_type"))
                            e1.setEvent_type(eventData.getValue().toString());
                        if(eventData.getKey().equals("lan_dest"))
                            e1.setLan_dest(eventData.getValue().toString());
                        if(eventData.getKey().equals("lan_source"))
                            e1.setLan_source(eventData.getValue().toString());
                        if(eventData.getKey().equals("lat_dest"))
                            e1.setLat_dest(eventData.getValue().toString());
                        if(eventData.getKey().equals("lat_source"))
                            e1.setLat_source(eventData.getValue().toString());
                        if(eventData.getKey().equals("ppl_joined"))
                            e1.setppl_joined(eventData.getValue().toString());
                    }
                    event.put(eventIDDataSet.getKey(),e1);
                }

                Log.i("event",event+"");

                int i=1;
                for(Map.Entry<String,Event> evnData : event.entrySet()) {

                    Event e1 = evnData.getValue();
                        rowDataList.add(new rowData(Integer.parseInt(evnData.getKey()), e1.getEvent_name(),
                                drawableImg.get(String.valueOf(e1.getEvent_name().charAt(0)).toLowerCase()),
                                e1.getEvent_date(), e1.getEvent_time(), e1.getEvent_duration()));
                }
                adapter = new rowDataAdapter(getApplicationContext(), rowDataList);
                rowDataView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //-- Drawer Button --
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mtoggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // -- Drawer Button --

}