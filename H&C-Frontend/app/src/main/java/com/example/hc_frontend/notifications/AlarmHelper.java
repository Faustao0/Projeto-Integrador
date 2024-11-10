package com.example.hc_frontend.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hc_frontend.domain.Medicamento;
import com.example.hc_frontend.domain.Usuario;

import java.util.Calendar;
import java.util.TimeZone;

public class AlarmHelper {

    public static void setAlarm(Context context, Medicamento medicamento, Usuario usuario) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Log.e("AlarmHelper", "AlarmManager é null - Alarme não pôde ser configurado.");
            return;
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("nomeMedicamento", medicamento.getNome());
        intent.putExtra("horarioTomar", medicamento.getHorarioTomar());
        intent.putExtra("usuario", usuario);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                medicamento.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String[] horario = medicamento.getHorarioTomar().split(":");
        int hour = Integer.parseInt(horario[0]);
        int minute = Integer.parseInt(horario[1]);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Bahia"));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Log.d("AlarmHelper", "Alarme para o medicamento " + medicamento.getNome() + " definido para: " + calendar.getTime());

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (Exception e) {
            Log.e("AlarmHelper", "Erro ao configurar o alarme: " + e.getMessage());
        }
    }
}