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

        //ListView OnItemClicListener and action
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                for (int j = 0; j < thingsDB.size(); j++) {
                    Thing thing = thingsDB.get(j);
                    boolean isTrue = mWhat[position].toString().equals(thing.getWhat().toString());
                    if (isTrue) {
                        Toast.makeText(getActivity(), "Item located: " + thing.getWhere().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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

        //Accessing Singleton class
        thingsDB = ThingsDB.get(getActivity());

        //Collecting all element from the ThingsDB
        for (int i = 0 ; i < thingsDB.size() ; i++ ) {
            Thing thing = thingsDB.get(i);
            String What = thing.getWhat();
            mWhat = appendToList(mWhat, What);
        }

        //This is the Search action
        searchButton = (Button) v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset the list of items
                mWhat = new String[0];
                int count = 0;

                if (newWhat.getText().toString().trim().length() == 0) {
                    //Collecting all element from the ThingsDB
                    // TODO: Since the ThingsDB in this version only accepts unique things from the add method, this is a bit overkill!
                    for (int i = 0; i < thingsDB.size(); i++) {
                        Thing thing = thingsDB.get(i);
                        String What = thing.getWhat();
                        mWhat = appendToList(mWhat, What);
                        count++;
                    }
                }

                //Collect the searched items
                for (int i = 0; i < thingsDB.size(); i++) {
                    Thing thing = thingsDB.get(i);
                    boolean exist = newWhat.getText().toString().trim().toLowerCase().equals(thing.getWhat().toString().toLowerCase());
                    if (exist) {
                        String What = thing.getWhat();
                        mWhat = appendToList(mWhat, What);
                        count++;
                    }
                }

                if (count == 0) count = collectSimilarWords(1, count);

                if (count == 0) count = collectSimilarWords(2,count);

                if (count == 0) count = collectSimilarWords(3,count);

                if (count == 0) mWhat = appendToList(mWhat, "Non words found:");

                //Declaring Array adapter
                mWhatItem = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mWhat);
                //Setting the android ListView´s adaptor to the newly created adaptor
                mListView.setAdapter(mWhatItem);
            }
        });
        return v;
    }

    /**
     *
     * @param dist
     * @param count
     * @return
     * This method collects similar words and increase the count if any match are find with the distance equal to dist.
     */
    private int collectSimilarWords(int dist, int count) {
        //Collect the newWhat from user input
        String a = newWhat.getText().toString().trim();
        //Collect all newWhat from DB
        for (int i = 0; i < thingsDB.size(); i++) {
            Thing thing = thingsDB.get(i);
            String b = thing.getWhat().toString();
            if (distance(a, b) == dist) {
                mWhat = appendToList(mWhat, b);
                count++;
            }
        }
        return count;
    }

    /**
     * @param a
     * @param b
     * This method uses the Levenshtein method to find the distance between two words
     * - This algorithm is borrowed from [http://rosettacode.org/wiki/Levenshtein_distance#Java]
     * - which is based on [http://www.codeproject.com/Articles/13525/Fast-memory-efficient-Levenshtein-algorithm]
     */
    private static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();

        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
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
