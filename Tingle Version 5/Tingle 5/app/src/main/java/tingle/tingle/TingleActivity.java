package tingle.tingle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.*;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class TingleActivity extends FragmentActivity {

    //GUI variables
    private Button listThing, mapThing;

    //Search txt
    String txt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);

        // Button
        listThing = (Button) findViewById(R.id.list_button);
        listThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String what_text = txt;
                Intent i = new Intent(TingleActivity.this, ListActivity.class);
                i.putExtra("SEARCH_TEXT", what_text);
                startActivity(i);
            }
        });
        /*
        mapThing = (Button) findViewById(R.id.list_button);
        mapThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TingleActivity.this, TingleActivity.class);
                startActivity(i);
            }
        });
        */

        /*
        * FragmentManager:
        */
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment_tingle = manager.findFragmentById(R.id.fragmentContainer_tingle);
        ListFragment fragment_list = (myListFragment) manager.findFragmentById(R.id.fragmentContainer_list);

        //Collect Intent from input (FrontActivity)
        if(getIntent() != null) txt = getIntent().getStringExtra("SEARCH_TEXT");
        else txt = null;

        //insert args to fragment
        Bundle args = new Bundle();
        args.putString("SEARCH_TEXT", txt);

        if (fragment_tingle == null) {
            fragment_tingle = new TingleFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_tingle, fragment_tingle)
                    .commit();
        }

        if (fragment_list == null) {
            fragment_list = new myListFragment();
            fragment_list.setArguments(args);
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_list, fragment_list)
                    .commit();
        }
    }
}