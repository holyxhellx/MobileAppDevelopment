package tingle.tingle;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class myListFragment extends ListFragment {
    //Defining button
    private Button searchButton;
    private TextView newWhat;

    //Declaring Array Adapter
    ArrayAdapter<String>  mWhatItem;

    //Defining ListView (Search-mode)
    private ListView mListView;

    //Element that will be displayed in ListView
    String[] mWhat = new String[0];

    //Collect Singleton Class
    private static ThingsDB thingsDB;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Declaring Array adapter
        ArrayAdapter<String>  mWhatItem = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mWhat);

        //Setting the android ListView´s adaptor to the newly created adaptor
        setListAdapter(mWhatItem);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, parent, false);

        //Accessing GUI elements
        mListView = (ListView) v.findViewById(android.R.id.list);
        newWhat = (EditText) v.findViewById(R.id.what_text);

        // Textfields for describing a thing - should hide the keyboard if the user click outsite the edittext area
        newWhat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        /*
        //Accessing Singleton class
        thingsDB = ThingsDB.get(getActivity());

        //Collecting all element from the ThingsDB
        for (int i = 0 ; i < thingsDB.size() ; i++ ) {
            Thing thing = thingsDB.get(i);
            String What = thing.getWhat();
            mWhat = appendToList(mWhat, What);
        }
        */
        return v;
    }

    /**
     *
     * @param set
     * @param element
     * @return
     * This method revives the total collection "set" and the element for which to append,
     * creates a new stringSet containing the element, and send the two sets to the method: merge()
     */
    private String[] appendToList(String[] set, String element) {
        //create an array containing the element
        String[] appendElement = new String[]{element};

        //add the String element and current
        String[] Set = merge(set, appendElement);
        return Set;
    }

    /**
     *
     * @param set
     * @param appendElement
     * @return
     * This method revives two stringSets and merges both together.
     */
    private String[] merge(String[] set, String[] appendElement) {
        int lenSet              = set.length;           //Collect the length of the array (set)
        int lenAppendElement    = appendElement.length; //Collect the length of the array (appendElement)

        //Create an array with the total length of both StringSet
        String[] merged = new String[lenSet + lenAppendElement];
        // Copy the first array (arr1) to merge from index 0 to len1.2
        System.arraycopy(set, 0, merged, 0, lenSet);
        // Copy the second array (arr2) to merge from index len1 to len 2.
        System.arraycopy(appendElement, 0, merged, lenSet, lenAppendElement);

        return merged;
    }
}
