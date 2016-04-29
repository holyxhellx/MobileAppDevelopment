package tingle.tingle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class FrontActivity extends AppCompatActivity {

    //Defining button
    private Button searchButton;
    private TextView newWhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);

        newWhat = (TextView) findViewById(R.id.what_text);

        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String what_text = newWhat.getText().toString();
                System.out.println("what_text: " +what_text);

                Intent i = new Intent(FrontActivity.this, ListActivity.class);
                i.putExtra("SEARCH_TEXT", what_text);
                startActivity(i);
            }
        });
    }

    /**
     * @param view
     * This method hides the softKeyboard if the user click outside the area in focus
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
