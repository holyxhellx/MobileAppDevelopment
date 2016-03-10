package tingle.tingle;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ListActivity extends FragmentActivity {

    //Declaring GUI elements
    private Button backButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer_list);

        if (fragment == null) {
            fragment = new myListFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_list, fragment)
                    .commit();
        }

        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListActivity.this, TingleActivity.class);
                startActivity(i);
            }
        });
    }
}