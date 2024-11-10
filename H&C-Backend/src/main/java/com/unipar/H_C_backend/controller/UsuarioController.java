package com.unipar.H_C_backend.controller;



import com.unipar.H_C_backend.domain.Usuario;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Usuario.class)) }),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Exception.class)) }) })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable("id") Long id) throws BusinessException {
        Usuario usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario createUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuarioDetails) throws BusinessException {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        Usuario updatedUsuario = usuarioService.update(id, usuarioDetails);
        return ResponseEntity.ok(updatedUsuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) throws BusinessException {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Usuario> getUsuarioByName(@PathVariable("nome") String nome) {
        try {
            Usuario usuario = usuarioService.findByName(nome);
            return ResponseEntity.ok(usuario);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/login")
    public ResponseEntity<Usuario> login(@RequestParam String email, @RequestParam String senha) {
        try {
            Usuario usuario = usuarioService.authenticate(email, senha);
            return ResponseEntity.ok(usuario);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/{usuarioId}/desvincular-consulta/{consultaId}")
    public ResponseEntity<Usuario> desvincularConsulta(@PathVariable Long usuarioId, @PathVariable Long consultaId) {
        try {
            Usuario usuarioAtualizado = usuarioService.desvincularConsulta(usuarioId, consultaId);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/buscarSenha")
    public ResponseEntity<Usuario> buscarSenhaPorEmail(@RequestParam String email) {
        try {
            Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
