package tingle.tingle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TingleActivity extends AppCompatActivity {

    //GUI variables
    private Button addThing, listThing; //import android.widget.Button;
    private TextView lastAdded; //import android.widget.TextView;
    private TextView newWhat, newWhere; //import android.widget.TextView;

    //database "as for now it is just a list"
    //private ArrayList<Thing> thingsDB; //import java.util.List //Earlier version setup
    private static ThingsDB thingsDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);
        //thingsDB = new ArrayList<Thing>(); //Earlier version setup
        thingsDB = ThingsDB.get(this);

        //Accessing GUI element
        lastAdded = (TextView) findViewById(R.id.last_thing);
        updateUI();

        // Textfields for describing a thing
        newWhat = (TextView) findViewById(R.id.what_text);
        newWhere = (TextView) findViewById(R.id.where_text);

        // Button
        listThing = (Button) findViewById(R.id.list_button);
        listThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TingleActivity.this, ListActivity.class);
                startActivity(i);
            }
        });

        addThing = (Button) findViewById(R.id.add_button);
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
                else if ((newWhat.getText().length() > 0) && (newWhere.getText().length() > 0)) {
                    for (int i = 0 ; i < thingsDB.size() ; i++ ) {
                        Thing thing = thingsDB.get(i);
                        boolean exist = newWhat.getText().toString().equals(thing.getWhat().toString());
                        if (exist) {
                            existIn = true;
                            itemExist(thing);
                            break;
                        }
                    }
                    if (existIn == false) {
                        addThingToThingDB();
                    }
                } else {
                    invalidInput();
                    Log.i("invalidInput Reach", "invalidInput Reach");
                }
            }


        });

    }

    //This method shows a toast with the information "where" if the added item already exist
    private void ExistInThingDB(Thing thing) {
        Toast.makeText( TingleActivity.this,
                        thing.getWhere().toString(),//Collect where
                        Toast.LENGTH_LONG).show();  //Duration and show on screen
    }

    private void itemExist(Thing thing) {
        String message = "Item allready exist: " + thing.getWhat().toString() + " location: " + thing.getWhere().toString();
        Toast.makeText(TingleActivity.this,
                message,
                Toast.LENGTH_SHORT).show();
    }

    //This method adds a new thing to the thingDB
    private void addThingToThingDB() {
        thingsDB.addThing(new Thing( newWhat.getText().toString(),
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

    //This method fill the lastAdded Textview with the data lastadded element from thingDB
    private void updateUI() {
        int s = thingsDB.size();
        if(s > 0) {
            lastAdded.setText(thingsDB.get(s-1).toString());
        }
    }
}
