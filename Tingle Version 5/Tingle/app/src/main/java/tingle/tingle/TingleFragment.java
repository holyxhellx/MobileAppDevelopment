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
    private static ThingsDB thingsDB;
    private Thing mThing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id = getArguments().getInt("THING_ID");
        mThing = ThingsDB.get(getActivity()).getThingsDB().get(id);
    }
    @Override
    public void onPause() {
        super.onPause();
        ThingsDB.get(getActivity()).updateThing(mThing);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tingle, parent, false);

        //thingsDB = new ArrayList<Thing>(); //Earlier version setup
        thingsDB = ThingsDB.get(getActivity());

        //Accessing GUI element
        lastAdded = (TextView) v.findViewById(R.id.last_thing);

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
                thingsDB.addThing(new Thing(newWhat.getText().toString().trim(), newWhere.getText().toString().trim()));
                System.out.println("Add is done");
                System.out.println("what: " + newWhat.getText().toString().trim());
                System.out.println("where: " + newWhere.getText().toString().trim());
            }

        });

        deleteThing = (Button) v.findViewById(R.id.delete_button);
        /* delete code goes here */
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
}