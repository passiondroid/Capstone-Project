package plus.health.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;
import plus.health.app.R;
import plus.health.app.activity.LauncherActivity;
import plus.health.app.database.DataContract;
import plus.health.app.model.Doctor;

/**
 * Created by arifkhan on 13/11/16.
 */

public class HealthAppAppWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            List<Doctor> doctorList = new ArrayList<>();
            Cursor cursorDoctor = context.getContentResolver().query(DataContract.Doctors.CONTENT_URI,null,null,null,null);
            if (cursorDoctor.moveToFirst()) {
                do {
                    Doctor doctor = new Doctor();
                    doctor.setName(cursorDoctor.getString(cursorDoctor.getColumnIndex(DataContract.Doctors.NAME)));
                    doctor.setPhoneNumber(cursorDoctor.getString(cursorDoctor.getColumnIndex(DataContract.Doctors.PHONE_NUMBER)));
                    doctor.setEmail(cursorDoctor.getString(cursorDoctor.getColumnIndex(DataContract.Doctors.EMAIL)));
                    doctor.setId(cursorDoctor.getInt(cursorDoctor.getColumnIndex(DataContract.Doctors.ID)));
                    doctorList.add(doctor);
                } while (cursorDoctor.moveToNext());
            }
            cursorDoctor.close();

            if(doctorList.size() >0) {
                // Create an Intent to launch ExampleActivity
                Intent intent = new Intent(context, LauncherActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
                views.setTextViewText(R.id.nameTV1, doctorList.get(0).getName());
                if(doctorList.size()>1 && doctorList.get(1) != null)
                    //views.setTextViewText(R.id.nameTV2, doctorList.get(1).getName());
                if(doctorList.size()>2 && doctorList.get(2) != null)
                    //views.setTextViewText(R.id.nameTV3, doctorList.get(2).getName());

                views.setOnClickPendingIntent(R.id.layout, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }
}
