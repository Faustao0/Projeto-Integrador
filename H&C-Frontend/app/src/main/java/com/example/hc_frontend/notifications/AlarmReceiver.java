package com.example.hc_frontend.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hc_frontend.domain.Usuario;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String nomeMedicamento = intent.getStringExtra("nomeMedicamento");
        String horarioTomar = intent.getStringExtra("horarioTomar");

        if (nomeMedicamento == null || horarioTomar == null) {
            Log.e("AlarmReceiver", "Informações incompletas para o medicamento - Notificação não pode ser criada.");
            return;
        }

        Log.d("AlarmReceiver", "Alarme recebido para o medicamento: " + nomeMedicamento + " no horário: " + horarioTomar);
        Usuario usuario = (Usuario) intent.getSerializableExtra("usuario");

        if (usuario == null) {
            Log.e("AlarmReceiver", "Usuário não encontrado no Intent - Notificação não será enviada.");
            return;
        }

        NotificationHelper.createNotification(context, usuario, nomeMedicamento, horarioTomar);
    }
}