package tingle.tingle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.util.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class ListActivity extends AppCompatActivity {
    //Defining button
    private Button backButton, searchButton;
    private TextView newWhat;

    //Defining ListView
    ListView mListView ;

    //Element that will be displayed in ListView
    String[] mWhat = new String[0];

    //Adaptor for the list of String
    ArrayAdapter<String> mWhatItem;

    //
    private static ThingsDB thingsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Accessing GUI elements
        newWhat = (TextView) findViewById(R.id.what_text);
        mListView = (ListView) findViewById(R.id.list);

        //Accessing Singleton class
        thingsDB = ThingsDB.get(this);

        //Collecting all element from the ThingsDB
        for (int i = 0 ; i < thingsDB.size() ; i++ ) {
            Thing thing = thingsDB.get(i);
            String What = thing.getWhat();
            mWhat = appendToList(mWhat, What);
        }

        //Declaring Array adapter
        mWhatItem = new ArrayAdapter<String>(ListActivity.this,
                                             android.R.layout.simple_list_item_1,
                                             mWhat);

        //Setting the android ListView´s adaptor to the newly created adaptor
        mListView.setAdapter(mWhatItem);

        //Setting the OnItemClicListener and action
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //Display toast message
                Toast.makeText(getApplicationContext(),
                    "Item located: " + thingsDB.get(position).getWhere().toString(),
                    Toast.LENGTH_LONG).show();
            }
        });

        //This sets the Back action
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListActivity.this, TingleActivity.class);
                startActivity(i);
            }
        });

        //This is the Search action
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset the list of items
                mWhat = new String[0];

                if (newWhat.getText().length() == 0) {
                    //Collecting all element from the ThingsDB
                    for (int i = 0 ; i < thingsDB.size() ; i++ ) {
                        Thing thing = thingsDB.get(i);
                        String What = thing.getWhat();
                        mWhat = appendToList(mWhat, What);
                    }
                    //Declaring Array adapter
                    mWhatItem = new ArrayAdapter<String>(ListActivity.this,
                            android.R.layout.simple_list_item_1,
                            mWhat);
                    //Setting the android ListView´s adaptor to the newly created adaptor
                    mListView.setAdapter(mWhatItem);
                }

                //Collect the searched items
                for (int i = 0 ; i < thingsDB.size() ; i++ ) {
                    Thing thing = thingsDB.get(i);
                    boolean exist = newWhat.getText().toString().equals(thing.getWhat().toString());
                    if (exist) {
                        String What = thing.getWhat();
                        mWhat = appendToList(mWhat, What);
                    }
                }
                //Declaring Array adapter
                mWhatItem = new ArrayAdapter<String>(ListActivity.this,
                        android.R.layout.simple_list_item_1,
                        mWhat);
                //Setting the android ListView´s adaptor to the newly created adaptor
                mListView.setAdapter(mWhatItem);
            }
        });
    }

    /**
     *
     * @param set
     * @param element
     * @return
     */
    private String[] appendToList(String[] set, String element) {
        //create an array containing the element
        String[] appendElement = new String[]{element};

        //TODO: Look for duplicates (multiable locations)

        //add the String element and current
        String[] Set = merge(set, appendElement);
        return Set;
    }

    /**
     *
     * @param set
     * @param appendElement
     * @return
     */
    private String[] merge(String[] set, String[] appendElement) {
        int lenSet              = set.length;           //Collect the length of the array (set)
        int lenAppendElement    = appendElement.length; //Collect the length of the array (appendElement)

        //Create an array with the total length of both StringSet
        String[] merged = new String[lenSet + lenAppendElement];
        // Copy the first array (arr1) to merge from index 0 to len1.2
        System.arraycopy(set,0,merged,0,lenSet);
        // Copy the second array (arr2) to merge from index len1 to len 2.
        System.arraycopy(appendElement,0,merged,lenSet,lenAppendElement);

        return merged;
    }

}
