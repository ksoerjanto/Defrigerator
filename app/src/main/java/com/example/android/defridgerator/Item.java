package com.example.android.defridgerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by ksoerjanto on 6/26/15.
 */
public class Item {

    private String name;
    private Date expDate;
    private int amount;
    private boolean expired;

    public Item(String name, String expDate, int amount) {
        this.name = name;
        this.amount = amount;

        //date is in format of MM/DD/YYYY
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        try
        {
            Date date = formatter.parse(expDate);
            this.expDate = formatter.parse(formatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isExpired() {

        //Get the current date
        final Calendar cal = Calendar.getInstance();
        Date today = new Date(cal.get(Calendar.YEAR)-1900, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        //Check if item is expired
        if (this.expDate.before(today)) {
            expired = true;

            /*Randomly add adjective for rotten foods
            Random random = new Random();
            int choose = random.nextInt(4);

            if (choose == 0) { this.name = "Rotten " + name; }

            else if (choose == 1) { this.name = "Spoiled " + name; }

            else if (choose == 2) { this.name = "Moldy " + name; }

            else if (choose == 3) { this.name = "Bad " + name; } */

        }
        return expired;
    }

    //Returns true if item will expire soon (3 days)
    public boolean needReminder() {

        //Get the current date
        final Calendar cal = Calendar.getInstance();

        //Check for year
        if (expDate.getYear() == cal.get(Calendar.YEAR)-1900 &&
            expDate.getMonth() == cal.get(Calendar.MONTH) &&
            expDate.getDay() - 3 == cal.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public long differenceInDays() {

        final Calendar cal = Calendar.getInstance();
        Date today = new Date(cal.get(Calendar.YEAR)-1900, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        long diff = this.getDate().getTime() - today.getTime();

        //Convert to days
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public String getName() {
        return name;
    }

    public Date getDate() { return expDate; }

    public int getAmount() { return amount; }

    public void setAmount(int newAmount) {
        this.amount = newAmount;
    }

    public String toString() {
        return amount + " " + name;
    }
}
