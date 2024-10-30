package com.example.hc_frontend.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String nomeMedicamento = intent.getStringExtra("nomeMedicamento");
        String horarioTomar = intent.getStringExtra("horarioTomar");

        Log.d("AlarmReceiver", "Nome do Medicamento: " + nomeMedicamento);
        Log.d("AlarmReceiver", "Horário de tomar: " + horarioTomar);

        NotificationHelper.createNotification(context, nomeMedicamento, horarioTomar);
    }
}