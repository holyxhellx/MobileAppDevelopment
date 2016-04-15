package tingle.tingle;

import android.support.v4.app.*;
import android.os.Bundle;

public class TingleActivity extends FragmentActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment_tingle = manager.findFragmentById(R.id.fragmentContainer_tingle);
        ListFragment fragment_list = (myListFragment) manager.findFragmentById(R.id.fragmentContainer_list);

        if (fragment_tingle == null) {
            fragment_tingle = new TingleFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_tingle, fragment_tingle)
                    .commit();
        }

        if (fragment_list == null) {
            fragment_list = new myListFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_list, fragment_list)
                    .commit();
        }
    }
}