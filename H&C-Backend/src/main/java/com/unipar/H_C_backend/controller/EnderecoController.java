package com.unipar.H_C_backend.controller;



import com.unipar.H_C_backend.domain.Endereco;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.service.EnderecoService;
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
@RequestMapping("/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping
    public List<Endereco> getAllEnderecos() {
        return enderecoService.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Endereco.class)) }),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Exception.class)) }) })
    @GetMapping("/{id}")
    public ResponseEntity<Endereco> getEnderecoById(@PathVariable("id") Long id) throws BusinessException {
        Endereco endereco = enderecoService.findById(id);
        return ResponseEntity.ok(endereco);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Endereco createEndereco(@Valid @RequestBody Endereco endereco) {
        return enderecoService.save(endereco);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Endereco> updateEndereco(@PathVariable Long id, @Valid @RequestBody Endereco enderecoDetails) throws BusinessException {
        Endereco updatedEndereco = enderecoService.update(id, enderecoDetails);
        return ResponseEntity.ok(updatedEndereco);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEndereco(@PathVariable Long id) throws BusinessException {
        enderecoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cep/{cep}")
    public ResponseEntity<Endereco> getEnderecoByCep(@PathVariable("cep") String cep) {
        try {
            Endereco endereco = enderecoService.findByCep(cep);
            return ResponseEntity.ok(endereco);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}

