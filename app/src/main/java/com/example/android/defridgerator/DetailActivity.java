package com.example.android.defridgerator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailActivityFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        setTitle("Defrigerate");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void defrigerate(View view) {

        //Delete from database
        ItemsDatabaseAdapter databaseAdapter = new ItemsDatabaseAdapter(this);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Name")) {
            String name = intent.getStringExtra("Name");
            databaseAdapter.deleteRow(name);
        }
        NavUtils.navigateUpFromSameTask(this);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailActivityFragment extends Fragment {

        private NumberPicker amountPicker;
        private ItemsDatabaseAdapter databaseAdapter;

        private String name;
        private String date;
        private int amount;
        private boolean expired;

        public DetailActivityFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
            databaseAdapter = new ItemsDatabaseAdapter(getActivity());
            final Intent intent = getActivity().getIntent();

            amountPicker = (NumberPicker)rootView.findViewById(R.id.detail_amount_numberpicker);
            //Populate NumberPicker
            String[] numbers = new String[20];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = Integer.toString(i+1);
            }
            amountPicker.setMinValue(1);
            amountPicker.setMaxValue(20);
            amountPicker.setWrapSelectorWheel(false);
            amountPicker.setDisplayedValues(numbers);
            amountPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    databaseAdapter.updateAmount(intent.getStringExtra("Name"), newVal);
                }
            });

            if (intent != null && intent.hasExtra("Name") && intent.hasExtra("Date")
                    && intent.hasExtra("Amount") && intent.hasExtra("Expired")) {
                name = intent.getStringExtra("Name");
                date = intent.getStringExtra("Date");
                String[] dates = date.split(" ");
                amount = intent.getIntExtra("Amount", 1);
                amountPicker.setValue(amount);
                expired = intent.getBooleanExtra("Expired", false);
                ((TextView) rootView.findViewById(R.id.detail_name_text)).setText(name);
                ((TextView) rootView.findViewById(R.id.detail_date_text)).setText("Expiry Date: " + dates[0]);
                ((TextView) rootView.findViewById(R.id.detail_amount_text))
                        .setText("How many you have left:");

                TextView expiredText = ((TextView)rootView.findViewById(R.id.expired_textview));

                if (expired) {
                    expiredText.setTextColor(Color.RED);
                    expiredText.setText("This item is expired!");
                }
            }
            return rootView;
        }
    }

}
