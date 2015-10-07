package com.example.android.defridgerator;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.Calendar;

import static com.example.android.defridgerator.Message.message;


public class RefrigerateActivity extends ActionBarActivity implements DialogInterface.OnClickListener {

    private EditText name;
    private NumberPicker amountPicker;
    private ItemsDatabaseAdapter itemsOpenHelper;
    private Button btnDatePicker;
    private int itemAmount = 1;
    private int year_x, month_x, day_x;
    private static final int DIALOG_ID = 0;

    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            year_x = year;
            month_x = month + 1; //month by default starts at 0 so must + 1
            day_x = day;
            Toast.makeText(RefrigerateActivity.this, "Expiry date is: " + year_x+"/"+month_x+"/"+day_x,
                    Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refrigerate);

        //Get the input from the user
        name = (EditText)findViewById(R.id.edit_name);

        //Populate NumberPicker
        amountPicker = (NumberPicker) findViewById(R.id.amount_numberpicker);
        String[] numbers = new String[20];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.toString(i+1);
        }
        amountPicker.setMinValue(1);
        amountPicker.setMaxValue(20);
        amountPicker.setWrapSelectorWheel(false);
        amountPicker.setDisplayedValues(numbers);
        amountPicker.setValue(1);
        amountPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                itemAmount = newVal;
            }
        });

        //Get current date
        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);

        //Shows datepicker when button is clicked
        showDialogOnClick();
    }

    public void showDialogOnClick() {

        //Find the button and set it's click listener
        btnDatePicker = (Button) findViewById(R.id.button_exp_date);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG_ID) {
            return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);
        }
        return null;
    }



    @Override
    public void onClick(DialogInterface dialogInterface, int view) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refrigerate, menu);

        //Create the openHelper
        itemsOpenHelper = new ItemsDatabaseAdapter(this);

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


    //When button add to fridge is clicked
    public void OnClick_AddToFridge(View view) {

        //If the user didn't enter a name
        if (name.getText().toString().equals("")) {
            message(this, "Please enter a name");
            return;
        }

        //Get the user input
        String itemName = name.getText().toString();
        String itemExpDate = month_x + "/" + day_x + "/" + year_x;

        amountPicker.clearFocus();
        itemAmount = amountPicker.getValue();

        //Save into a database
        long id = itemsOpenHelper.insertData(itemName, itemExpDate, itemAmount);
        if (id < 0) {
            message(this, "Unsuccessful Insertion");
        } else {
            message(this, "Insert Successful!");
        }
    }
}
