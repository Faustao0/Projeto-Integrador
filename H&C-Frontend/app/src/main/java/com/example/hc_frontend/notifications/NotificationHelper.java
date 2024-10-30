package com.example.hc_frontend.notifications;

import static android.content.Intent.getIntent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.view.ListaMedicamentosActivity;
import com.example.hc_frontend.view.MainActivity;

import java.io.Serializable;

public class NotificationHelper {

    private static final String CHANNEL_ID = "medicine_reminder_channel";
    private static final String CHANNEL_NAME = "Medicine Reminders";
    private static final String CHANNEL_DESCRIPTION = "Notifies the user to take their medication";
    private static Usuario usuario;

    public static void createNotification(Context context, String nomeMedicamento, String horarioTomar) {
        createNotificationChannel(context);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, ListaMedicamentosActivity.class);
        intent.putExtra("usuario", (Serializable) usuario);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_notification)
                .setContentTitle("Hora de tomar o medicamento")
                .setContentText("Está na hora de tomar o medicamento " + nomeMedicamento)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}