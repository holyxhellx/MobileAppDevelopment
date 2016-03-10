package tingle.tingle;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TingleFragment extends Fragment {

    //GUI variables
    private Button addThing, listThing, deleteThing; //import android.widget.Button;
    private TextView lastAdded; //import android.widget.TextView;
    private TextView newWhat, newWhere; //import android.widget.TextView;


    //private ArrayList<Thing> thingsDB;
    private static ThingsDB thingsDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thingsDB = ThingsDB.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tingle, parent, false);

        //thingsDB = new ArrayList<Thing>(); //Earlier version setup
        thingsDB = ThingsDB.get(getActivity());

        //Accessing GUI element
        lastAdded = (TextView) v.findViewById(R.id.last_thing);
        updateUI();

        // Textfields for describing a thing - should hide the keyboard if the user click outsite the edittext area
        newWhat = (EditText) v.findViewById(R.id.what_text);
        newWhat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        // Textfields for describing a thing - should hide the keyboard if the user click outsite the edittext area
        newWhere = (EditText) v.findViewById(R.id.where_text);
        newWhere.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        // Button
        listThing = (Button) v.findViewById(R.id.list_button);
        listThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListActivity.class);
                startActivity(i);
            }
        });

        addThing = (Button) v.findViewById(R.id.add_button);
        addThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean existIn = false;
                //Ensure the user has made enough inputs to add an element
                if ((newWhat.getText().toString().trim().length() > 0) && (newWhere.getText().toString().trim().length() > 0)) {
                    //Ensure the element does not exist in the thingsDB already
                    for (int i = 0 ; i < thingsDB.size() ; i++ ) {
                        if (newWhat.getText().toString().trim().equals((thingsDB.get(i)).getWhat().toString())) {
                            existIn = true;
                            ExistInThingDB(thingsDB.get(i));
                            break;
                        }
                    }
                    //Add element to ThingsDB:
                    if (existIn == false) {
                        addThingToThingDB();
                    }
                //Invalid input for this operation
                // TODO: Use "Toast" to tell the user which values does not meet the input-requirements
                } else {
                    invalidInput();
                    //Log.i("AddButton", "invalidInput Reach");
                }
            }
        });

        deleteThing = (Button) v.findViewById(R.id.delete_button);
        deleteThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ensure the user has made enough inputs to add an element
                if ((newWhat.getText().toString().trim().length() > 0) && (newWhere.getText().toString().trim().length() > 0)) {
                    for (int i = 0 ; i < thingsDB.size() ; i++ ) {
                        //Ensure the element does exist in the thingsDB already
                        boolean exist = newWhat.getText().toString().trim().equals((thingsDB.get(i)).getWhat().toString());
                        if (exist) {
                            deleteThingOfThingsDB(thingsDB.get(i));
                            break;
                        }
                    }
                //Invalid input for this operation
                // TODO: Use "Toast" to tell the user which values does not meet the input-requirements
                } else {
                    invalidInput();
                    Log.i("DeleteButton", "invalidInput Reach");
                }
            }
        });
        return v;
    }

    /**
     * @param view
     * This method hides the softKeyboard if the user click outside the area in focus
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * @param thing
     * This method shows a toast with the information "where" if the added item already exist
     */
    private void ExistInThingDB(Thing thing) {
        String message = "Item exist: " + thing.getWhat().toString() + "\n"+"location: " + thing.getWhere().toString();
        Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();  //Duration and show on screen
    }

    /**
     * @param thing
     * This method deletes a thing from the thingsDB
     */
    private void deleteThingOfThingsDB(Thing thing) {
        thingsDB.deleteThing(thing);
        String message = "Item delete: " + newWhat.getText().toString();
        Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();
        newWhat.setText("");
        newWhere.setText("");
        updateUI();
        updateFragments("list");
    }

    /**
     * This method adds a new thing to the thingDB
     */
    private void addThingToThingDB() {
        thingsDB.addThing(new Thing(newWhat.getText().toString().trim(), newWhere.getText().toString().trim()));
        String message = "Item added: " + newWhat.getText().toString() + "\n"+"location: " + newWhere.getText().toString();
        Toast.makeText(getActivity(), message ,Toast.LENGTH_SHORT).show();
        newWhat.setText("");
        newWhere.setText("");
        updateUI();
        updateFragments("list");
    }

    /**
     * This method shows a toast if the input is not valid
     */
    private void invalidInput() {
        Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method fill the lastAdded Textview with the data lastadded element from thingDB
     */
    private void updateUI() {
        int s = thingsDB.size();
        if(s > 0) {
            lastAdded.setText(thingsDB.get(s-1).toString());
        }
    }

    /**
     * @param string
     * This method update specific fragment(s), pending on string input value, and is done by
     * "remove" fragment and then again "add" fragment to the activity
     */
    public void updateFragments(String string) {
        //Get the SupportFragmentManager
        FragmentManager manager = getActivity().getSupportFragmentManager();

        //Get the fragment (fragment_list & fragment_tingle)
        Fragment fragment_tingle = manager.findFragmentById(R.id.fragmentContainer_tingle);
        Fragment fragment_list = (myListFragment) manager.findFragmentById(R.id.fragmentContainer_list);

        if (string.equals("tingle")) {
            manager.beginTransaction()
                    .remove(fragment_list)
                    .commit();

            fragment_tingle = new Fragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_tingle, fragment_tingle)
                    .commit();
        }
        else if(string.equals("list")) {
            //Use the manager to begin transaction, and remove the fragment
            manager.beginTransaction()
                    .remove(fragment_list)
                    .commit();

            //Create a new Fragment (myListFragment)
            fragment_list = new myListFragment();
            //Use the manager to begin transaction, and add the new(updated) fragment
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_list, fragment_list)
                    .commit();

        }
        else if(string.equals("all")) {
            manager.beginTransaction()
                    .remove(fragment_list)
                    .remove(fragment_tingle)
                    .commit();

            fragment_tingle = new Fragment();
            fragment_list = new myListFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_tingle, fragment_tingle)
                    .add(R.id.fragmentContainer_list, fragment_list)
                    .commit();
        }
    }
}