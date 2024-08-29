package com.unipar.H_C_backend.controller;

import com.unipar.H_C_backend.domain.Medicamento;
import com.unipar.H_C_backend.domain.Paciente;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.service.MedicamentoService;
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
@RequestMapping("/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @GetMapping
    public List<Medicamento> getAllMedicamentos() {
        return medicamentoService.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Medicamento.class)) }),
            @ApiResponse(responseCode = "404", description = "Medicamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Exception.class)) }) })
    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> getMedicamentoById(@PathVariable("id") Long id) throws BusinessException {
        Medicamento medicamento = medicamentoService.findById(id);
        return ResponseEntity.ok(medicamento);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medicamento createMedicamento(@Valid @RequestBody Medicamento medicamento) {
        return medicamentoService.save(medicamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> updateMedicamento(@PathVariable Long id, @Valid @RequestBody Medicamento medicamentoDetails) throws BusinessException {
        Medicamento updatedMedicamento = medicamentoService.update(id, medicamentoDetails);
        return ResponseEntity.ok(updatedMedicamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicamento(@PathVariable Long id) throws BusinessException {
        medicamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Medicamento> getMedicamentoByName(@PathVariable("nome") String nome) {
        try {
            Medicamento medicamento = medicamentoService.findByName(nome);
            return ResponseEntity.ok(medicamento);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
