package com.unipar.H_C_backend.controller;

import com.unipar.H_C_backend.domain.Consulta;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.service.ConsultaService;
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
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @GetMapping
    public List<Consulta> getAllConsultas() {
        return consultaService.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Consulta.class)) }),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Exception.class)) }) })
    @GetMapping("/{id}")
    public ResponseEntity<Consulta> getConsultaById(@PathVariable("id") Long id) throws BusinessException {
        Consulta consulta = consultaService.findById(id);
        return ResponseEntity.ok(consulta);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Consulta createConsulta(@Valid @RequestBody Consulta consulta) {
        return consultaService.save(consulta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consulta> updateConsulta(@PathVariable Long id, @Valid @RequestBody Consulta consultaDetails) throws BusinessException {
        Consulta updatedConsulta = consultaService.update(id, consultaDetails);
        return ResponseEntity.ok(updatedConsulta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsulta(@PathVariable Long id) throws BusinessException {
        consultaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}