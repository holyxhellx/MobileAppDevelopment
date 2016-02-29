package tingle.tingle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class TingleActivity extends AppCompatActivity {

    //GUI variables
    private Button addThing; //import android.widget.Button;
    private TextView lastAdded; //import android.widget.TextView;
    private TextView newWhat, newWhere; //import android.widget.TextView;

    //database "as for now it is just a list"
    private List<Thing> thingsDB; //import java.util.List

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);
        thingsDB = new ArrayList<Thing>();
        fillThingsDB();

        System.out.println("inside the oncreate");

        //Accessing GUI element
        lastAdded = (TextView) findViewById(R.id.last_thing);
        updateUI();

        // Button
        addThing = (Button) findViewById(R.id.add_button);
        updateUI();

        // Textfields for describing a thing
        newWhat = (TextView) findViewById(R.id.what_text);
        newWhere = (TextView) findViewById(R.id.where_text);

        // view products click event
        addThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean existIn = false;

                if ((newWhat.getText().length() > 0) && (newWhere.getText().length() == 0)) {
                    for (int i = 0 ; i < thingsDB.size() ; i++ ) {
                        Thing thing = thingsDB.get(i);
                        boolean exist = newWhat.getText().toString().equals(thing.getWhat().toString());
                        if (exist) {
                            ExistInThingDB(thing);
                            break;
                        }
                    }
                }

                if ((newWhat.getText().length() > 0) && (newWhere.getText().length() > 0)) {
                    for (int i = 0 ; i < thingsDB.size() ; i++ ) {
                        Thing thing = thingsDB.get(i);  //Collect the object associated with the index number.
                                                        //thing.getWhat know call this object to collect the mWhat.
                                                        //Remember .toString() not (String) cast
                        boolean exist = newWhat.getText().toString().equals(thing.getWhat().toString());
                        if (exist) {
                            existIn = true;
                            break;
                        }
                    }
                    if (existIn == false) {
                        addThingToThingDB();
                    }
                } else {
                    invalidInput();
                }
            }
        });
    }

    //This method shows a toast with the information "where" if the added item already exist
    private void ExistInThingDB(Thing thing) {
        //String mWhere = thing.getWhere();
        Toast.makeText( TingleActivity.this,
                        thing.getWhere().toString(),//Collect where
                        Toast.LENGTH_LONG).show();  //Duration and show on screen
    }
    //This method adds a new thing to the thingDB
    private void addThingToThingDB() {
        thingsDB.add(new Thing( newWhat.getText().toString(),
                                newWhere.getText().toString()));
                                newWhat.setText("");
                                newWhere.setText("");
                                updateUI();
    }
    //This method shows a toast if the input is not valid
    private void invalidInput() {
        Toast.makeText( TingleActivity.this,
                        "Invalid input",
                        Toast.LENGTH_SHORT).show();
    }

    //This method fill a few things into ThingsDB for testing
    private void fillThingsDB() {
        thingsDB.add(new Thing("Android Phone", "Desk"));
        thingsDB.add(new Thing("a", "Desk"));
    }
    //This method fill the lastAdded Textview with the data lastadded element from thingDB
    private void updateUI() {
        int s = thingsDB.size();
        if(s > 0) {
            lastAdded.setText(thingsDB.get(s-1).toString());
        }
    }
}
