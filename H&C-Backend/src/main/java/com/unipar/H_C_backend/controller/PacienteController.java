package com.unipar.H_C_backend.controller;

import com.unipar.H_C_backend.domain.Paciente;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.service.PacienteService;
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
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> getAllPacientes() {
        return pacienteService.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Paciente.class)) }),
            @ApiResponse(responseCode = "400", description = "ID inválido informado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Exception.class)) }) })

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> getPacienteById(@PathVariable("id") Long id) {
        try {
            Paciente paciente = pacienteService.findById(id);
            return ResponseEntity.ok(paciente);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente createPaciente(@Valid @RequestBody Paciente paciente) {
        return pacienteService.save(paciente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> updatePaciente(@PathVariable Long id, @Valid @RequestBody Paciente pacienteDetails) throws BusinessException {
        Paciente updatedPaciente = pacienteService.update(id, pacienteDetails);
        return ResponseEntity.ok(updatedPaciente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable Long id) throws BusinessException {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Paciente> getPacienteByName(@PathVariable("nome") String nome) {
        try {
            Paciente paciente = pacienteService.findByName(nome);
            return ResponseEntity.ok(paciente);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
