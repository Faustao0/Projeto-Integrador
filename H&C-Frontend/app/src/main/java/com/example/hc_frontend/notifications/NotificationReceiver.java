package com.example.hc_frontend.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.hc_frontend.domain.Usuario;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Usuario usuario = (Usuario) intent.getSerializableExtra("usuario");
        String nomeMedicamento = intent.getStringExtra("nomeMedicamento");

        if (usuario != null && nomeMedicamento != null) {
            NotificationHelper.createNotification(context, usuario, nomeMedicamento, null);
        }
    }
}
