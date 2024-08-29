package com.example.hc_frontend.domainViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.hc_frontend.controller.UsuarioController;
import com.example.hc_frontend.domain.Usuario;

public class UsuarioViewModel extends ViewModel {

    private UsuarioController usuarioController;
    private MutableLiveData<Usuario> usuario;

    public UsuarioViewModel() {
        usuarioController = new UsuarioController();
        usuario = new MutableLiveData<>();
    }

    public MutableLiveData<Usuario> getUsuario() {
        return usuario;
    }

    public void login(String email, String senha) {
        usuarioController.login(email, senha).observeForever(usuario::setValue);
    }
}