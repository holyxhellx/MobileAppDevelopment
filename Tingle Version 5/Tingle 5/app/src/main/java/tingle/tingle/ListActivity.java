package tingle.tingle;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ListActivity extends FragmentActivity {

    //Declaring GUI elements
    private Button createButton;

    //Search txt:
    String txt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Collect Intent from input (FrontActivity)
        if(getIntent() != null) txt = getIntent().getStringExtra("SEARCH_TEXT");
        else txt = null;

        /*
         *  FragmentManager
         */
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer_list);

        //insert args to fragment
        Bundle args = new Bundle();
        args.putString("SEARCH_TEXT", txt);

        if (fragment == null) {
            fragment = new myListFragment();
            fragment.setArguments(args);
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_list, fragment)
                    .commit();
        }

        /*
         *  Navigation
         */
        createButton = (Button) findViewById(R.id.create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String what_text = txt;
                Intent i = new Intent(ListActivity.this, TingleActivity.class);
                i.putExtra("SEARCH_TEXT", what_text);
                startActivity(i);
            }
        });
    }
}