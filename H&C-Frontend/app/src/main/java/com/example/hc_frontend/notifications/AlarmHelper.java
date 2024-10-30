package com.example.hc_frontend.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hc_frontend.domain.Medicamento;

import java.util.Calendar;
import java.util.TimeZone;

public class AlarmHelper {

    public static void setAlarm(Context context, Medicamento medicamento) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("nomeMedicamento", medicamento.getNome());
        intent.putExtra("horarioTomar", medicamento.getHorarioTomar());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                medicamento.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Parse horário do medicamento e configurar o alarme
        String[] horario = medicamento.getHorarioTomar().split(":");
        int hour = Integer.parseInt(horario[0]);
        int minute = Integer.parseInt(horario[1]);

        // Configurar o calendário para o horário do medicamento no TimeZone de São Paulo
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Se o horário já passou hoje, programar para o mesmo horário amanhã
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Log para verificar o tempo do alarme
        Log.d("AlarmHelper", "Alarme definido para: " + calendar.getTime());

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}