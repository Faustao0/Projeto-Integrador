package com.unipar.H_C_backend.controller;


import com.unipar.H_C_backend.domain.Exame;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.service.ExameService;
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
@RequestMapping("/exames")
public class ExameController {

    @Autowired
    private ExameService exameService;

    @GetMapping
    public List<Exame> getAllExames() {
        return exameService.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Exame.class)) }),
            @ApiResponse(responseCode = "404", description = "Exame não encontrado"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Exception.class)) }) })
    @GetMapping("/{id}")
    public ResponseEntity<Exame> getExameById(@PathVariable("id") Long id) throws BusinessException {
        Exame exame = exameService.findById(id);
        return ResponseEntity.ok(exame);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Exame createExame(@Valid @RequestBody Exame exame) {
        return exameService.save(exame);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exame> updateExame(@PathVariable Long id, @Valid @RequestBody Exame exameDetails) throws BusinessException {
        Exame updatedExame = exameService.update(id, exameDetails);
        return ResponseEntity.ok(updatedExame);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExame(@PathVariable Long id) throws BusinessException {
        exameService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Exame> getExameByName(@PathVariable("nome") String nome) {
        try {
            Exame exame = exameService.findByName(nome);
            return ResponseEntity.ok(exame);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
