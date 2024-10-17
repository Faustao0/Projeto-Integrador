package com.example.hc_frontend.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hc_frontend.R;
import com.example.hc_frontend.domain.Endereco;
import com.example.hc_frontend.domain.Usuario;
import com.example.hc_frontend.repositories.UsuarioRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AtualizarUsuarioActivity extends AppCompatActivity {

    private EditText etNome, etTelefone, etSenha, etCpf, etRua, etNumero, etCidade, etEstado, etCep, etBairro;
    private Button btnAtualizar;
    private Usuario usuario;
    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_usuario);

        etNome = findViewById(R.id.etNome);
        etTelefone = findViewById(R.id.etTelefone);
        etSenha = findViewById(R.id.etSenha);
        etCpf = findViewById(R.id.etCpf);
        etRua = findViewById(R.id.etRua);
        etNumero = findViewById(R.id.etNumero);
        etCidade = findViewById(R.id.etCidade);
        etEstado = findViewById(R.id.etEstado);
        etCep = findViewById(R.id.etCep);
        etBairro = findViewById(R.id.etBairro);
        btnAtualizar = findViewById(R.id.btnAtualizar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usuarioRepository = retrofit.create(UsuarioRepository.class);

        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        if (usuario != null) {
            preencherCampos(usuario);
        } else {
            Toast.makeText(this, "Erro ao carregar usuário", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarAtualizacao();
            }
        });
    }

    private void preencherCampos(Usuario usuario) {
        etNome.setText(usuario.getNome());
        etTelefone.setText(usuario.getTelefone());
        etSenha.setText(usuario.getSenha());
        etCpf.setText(usuario.getCpf());

        if (usuario.getEndereco() != null && !usuario.getEndereco().isEmpty()) {
            Endereco endereco = usuario.getEndereco().get(0);
            etRua.setText(endereco.getRua());
            etBairro.setText(endereco.getBairro());
            etNumero.setText(endereco.getNumero());
            etCidade.setText(endereco.getCidade());
            etEstado.setText(endereco.getEstado());
            etCep.setText(endereco.getCep());
        }
    }

    private void confirmarAtualizacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Alterações");
        builder.setMessage("Você realmente deseja salvar as alterações dos dados?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                atualizarInformacoesUsuario();
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void atualizarInformacoesUsuario() {
        usuario.setNome(etNome.getText().toString());
        usuario.setTelefone(etTelefone.getText().toString());
        usuario.setSenha(etSenha.getText().toString());
        usuario.setCpf(etCpf.getText().toString());

        if (usuario.getEndereco() != null && !usuario.getEndereco().isEmpty()) {
            Endereco endereco = usuario.getEndereco().get(0);
            endereco.setRua(etRua.getText().toString());
            endereco.setBairro(etBairro.getText().toString());
            endereco.setNumero(etNumero.getText().toString());
            endereco.setCidade(etCidade.getText().toString());
            endereco.setEstado(etEstado.getText().toString());
            endereco.setCep(etCep.getText().toString());
        }

        Call<Usuario> call = usuarioRepository.atualizarUsuario(usuario.getId(), usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AtualizarUsuarioActivity.this, "Informações atualizadas com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("usuario_atualizado", usuario);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(AtualizarUsuarioActivity.this, "Falha ao atualizar informações", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(AtualizarUsuarioActivity.this, "Erro de comunicação com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}