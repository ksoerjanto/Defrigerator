package com.example.android.defridgerator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<Item> itemsAdapter;
    private ItemsDatabaseAdapter databaseAdapter;
    private ArrayList<Item> listItems = new ArrayList<Item>();

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();


    public MainActivityFragment() {
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Task task = new Task(getActivity());
        task.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menufragment_main, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        itemsAdapter = new ArrayAdapter<Item>(
                getActivity(), R.layout.list_item,
                R.id.list_item_textview, listItems);

        //Get a reference to the listview
        ListView listView = (ListView)rootView.findViewById(R.id.listview_items);
        listView.setAdapter(itemsAdapter); //Connects adapter to listview

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Item item = itemsAdapter.getItem(position);
                boolean expired = item.isExpired();

                //Get the string of just the date
                SimpleDateFormat formatter = new SimpleDateFormat();
                String justDate = formatter.format(item.getDate());

                //Go to detail activity
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("Amount", item.getAmount())
                        .putExtra("Name", item.getName())
                        .putExtra("Date", justDate)
                        .putExtra("Expired", expired);
                startActivity(intent);
            }
        });
        return rootView;

    }


    public class Task extends AsyncTask<Void, Void, ArrayList<Item>>
    {
        private Context context;
        private TextView text;

        public Task(Context context)
        {
            this.context = context;
        }


        @Override
        public ArrayList<Item> doInBackground(Void...params)
        {
            databaseAdapter = new ItemsDatabaseAdapter(getActivity());
            String data = databaseAdapter.getAllData();

            //Read through the data outputted
            Scanner scanner = new Scanner(data);

            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();
                String[] words = line.split(" ");

                //Get name and exp date of each item
                String itemName = words[0];
                String expDate = words[1];
                int amount = Integer.parseInt(words[2]);

                Item item = new Item(itemName, expDate, amount);

                //Add to arraylist
                listItems.add(item);
            }

            return listItems;
        }

    }


}

