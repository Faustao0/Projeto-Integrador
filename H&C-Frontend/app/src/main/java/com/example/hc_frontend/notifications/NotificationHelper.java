package com.example.hc_frontend.notifications;

import static android.media.AudioAttributes.USAGE_NOTIFICATION;
import static com.example.hc_frontend.R.raw.android_notification_sound;
import static com.example.hc_frontend.R.raw.sound;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.view.ConfirmarMedicamentoActivity;

import java.io.Serializable;

public class NotificationHelper {

    private static final String CHANNEL_ID = "medicine_reminder_channel_v9";
    private static final String CHANNEL_NAME = "Medicine Reminders";
    private static final String CHANNEL_DESCRIPTION = "Notifies the user to take their medication";
    private static final int REPEATING_INTERVAL = 30000;

    public static void createNotification(Context context, Usuario usuario, String nomeMedicamento, String horarioTomar) {
        createNotificationChannel(context);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Log.e("NotificationHelper", "NotificationManager is null - Cannot create notification.");
            return;
        }

        Intent intent = new Intent(context, ConfirmarMedicamentoActivity.class);
        intent.putExtra("usuario", (Serializable) usuario);
        intent.putExtra("nomeMedicamento", nomeMedicamento);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + sound);
        Log.d("NotificationHelper", "URI do som da notificação: " + soundUri);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_notification)
                .setContentTitle("Hora de tomar o medicamento")
                .setContentText("Está na hora de tomar o medicamento " + nomeMedicamento)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(new long[]{0, 500, 100, 500});

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
        Log.d("NotificationHelper", "Notificação criada com ID: " + notificationId);

        // Inicia a primeira repetição
        scheduleReNotification(context, usuario, nomeMedicamento);
    }

    private static void scheduleReNotification(Context context, Usuario usuario, String nomeMedicamento) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("usuario", (Serializable) usuario);
        notificationIntent.putExtra("nomeMedicamento", nomeMedicamento);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            long triggerTime = System.currentTimeMillis() + REPEATING_INTERVAL;
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, alarmIntent);
            Log.d("NotificationHelper", "Notificação exata configurada para 30 segundos.");
        } else {
            Log.e("NotificationHelper", "AlarmManager is null - Cannot schedule repeating notification.");
        }
    }

    public static void cancelReNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
            Log.d("NotificationHelper", "Alarme de notificação repetitiva cancelado.");
        } else {
            Log.e("NotificationHelper", "AlarmManager is null - Cannot cancel repeating notification.");
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + sound);

            if (soundUri == null) {
                Log.e("NotificationHelper", "Erro: URI de som personalizado é null. Verifique o arquivo de som em res/raw.");
            } else {
                Log.d("NotificationHelper", "URI de som personalizada carregada: " + soundUri);
            }

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            channel.setSound(soundUri, audioAttributes);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 100, 500});

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d("NotificationHelper", "Notification Channel criado com som personalizado.");
            } else {
                Log.e("NotificationHelper", "NotificationManager é null - Canal de notificação não pôde ser criado.");
            }
        }
    }
}
