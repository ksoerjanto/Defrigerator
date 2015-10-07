package com.example.android.defridgerator;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by ksoerjanto on 7/3/15.
 */
public class Alarm extends BroadcastReceiver
{
    private ItemsDatabaseAdapter databaseAdapter;
    private ArrayList<Item> itemsList;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        databaseAdapter = new ItemsDatabaseAdapter(context);
        itemsList = new ArrayList<Item>();
        String data = databaseAdapter.getAllData();

        //Iterate through the items in the database
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
            itemsList.add(item);
        }

        for (int i = 0; i < itemsList.size(); i++) {
            if (itemsList.get(i).isExpired()) {
                sendSingleNotification(context, itemsList.get(i).getName());
            }

            else if (itemsList.get(i).needReminder()) {
                sendReminder(context, itemsList.get(i).getName());
            }
        }
    }

    public void setAlarm(Context context)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }


    private void sendSingleNotification(Context context, String name)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_fridge)
                .setContentTitle("Fridge says:")
                .setContentText(name + " is expired!");

        //Notification is automatically removed when user opens
        builder.setAutoCancel(true);
        int mId = 1; //so notification can be updated

        //Creates an explicit intent for an Activity
        Intent resultIntent = new Intent(context, DetailActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DetailActivity.class);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        NotificationManager manager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //mId allows you to update the notification later on
        manager.notify(mId, builder.build());
    }

    private void sendReminder(Context context, String name)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_fridge)
                .setContentTitle("Fridge says:")
                .setContentText(name + " is expiring soon!");

        int mId = 1;

        //Creates an explicit intent for an Activity
        Intent resultIntent = new Intent(context, DetailActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DetailActivity.class);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        NotificationManager manager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //mId allows you to update the notification later on
        manager.notify(mId, builder.build());
    }
}
