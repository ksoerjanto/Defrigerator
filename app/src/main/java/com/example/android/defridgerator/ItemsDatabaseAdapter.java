package com.example.android.defridgerator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ksoerjanto on 6/28/15.
 */
public class ItemsDatabaseAdapter {

    private ItemsOpenHelper itemsOpenHelper;

    public ItemsDatabaseAdapter(Context context) {
        itemsOpenHelper = new ItemsOpenHelper(context);
    }

    public long insertData(String name, String date, int amount) {

        //Gets a writable database
        SQLiteDatabase db = itemsOpenHelper.getWritableDatabase();

        //To insert the data
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemsOpenHelper.NAME, name);
        contentValues.put(ItemsOpenHelper.EXP_DATE, date);
        contentValues.put(ItemsOpenHelper.AMOUNT, amount);

        //id indicates the row id of the column that was successfully inserted.
        //id will be negative if something went wrong
        long id = db.insert(ItemsOpenHelper.TABLE_NAME, null, contentValues); //add to db
        return id;
    }

    public String getAllData() {

        //Gets the writable database
        SQLiteDatabase db = itemsOpenHelper.getWritableDatabase();

        //Specify the columns we want to get from
        String[] columns = {ItemsOpenHelper.UID, ItemsOpenHelper.NAME, ItemsOpenHelper.EXP_DATE,
                            ItemsOpenHelper.AMOUNT};
        Cursor cursor = db.query(ItemsOpenHelper.TABLE_NAME, columns, null, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        //Iterate through the rows using the cursor
        while(cursor.moveToNext()) {
            String name = cursor.getString(1);
            String exp_date = cursor.getString(2);
            int amount = cursor.getInt(3);
            buffer.append(name+" "+exp_date+" "+amount+"\n");
        }
        return buffer.toString();
    }

    //When an item is defrigerated
    public int deleteRow(String name)
    {
        //DELETE * FROM ItemsOpenHelper.TABLE_NAME where Name = 'name'
        SQLiteDatabase db = itemsOpenHelper.getWritableDatabase();
        String[] whereArgs = {name};
        int count = db.delete(ItemsOpenHelper.TABLE_NAME, ItemsOpenHelper.NAME+ "=?", whereArgs);
        return count;
    }

    public void updateAmount(String name, int newAmount)
    {
        SQLiteDatabase db = itemsOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemsOpenHelper.AMOUNT, newAmount);
        String[] whereArgs={name};
        db.update(ItemsOpenHelper.TABLE_NAME, contentValues, ItemsOpenHelper.NAME+" =? ", whereArgs);
    }


    private class ItemsOpenHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "itemsDatabase.db";
        private static final String TABLE_NAME = "ITEMS_TABLE";
        private static final String UID = "_id";
        private static final String NAME = "Name";
        private static final String EXP_DATE = "Expiry_Date";
        private static final String AMOUNT = "Amount";
        private static final int DATABASE_VERSION = 11;
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+UID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +NAME+" TEXT NOT NULL, "+EXP_DATE+" TEXT NOT NULL, "+AMOUNT+" INTEGER NOT NULL);";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;


        public ItemsOpenHelper(Context context) {
            //context, name of db, custom custor - set to null because we're not creating
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //Called when the database is created for the first time. Creation of the tables and
        //initial data inside tables should be put here
        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                //Create a table with a column _id (key) and Name that takes the type VARCHAR
                db.execSQL(CREATE_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Called when the database needs to be upgraded. Use this method to drop tables, add tables,
        //or do anything else it needs to upgrade to the new schema version
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            try {
                db.execSQL(DROP_TABLE);
                //Recreate new tables
                onCreate(db);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
