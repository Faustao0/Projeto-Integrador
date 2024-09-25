package com.unipar.H_C_backend.controller;




import com.unipar.H_C_backend.domain.Medico;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.service.MedicoService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public List<Medico> getAllMedicos() {
        return medicoService.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Medico.class)) }),
            @ApiResponse(responseCode = "404", description = "Medico não encontrado"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = Exception.class)) }) })
    @GetMapping("/{id}")
    public ResponseEntity<Medico> getMedicoById(@PathVariable("id") Long id) throws BusinessException {
        Medico medico = medicoService.findById(id);
        return ResponseEntity.ok(medico);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medico createMedico(@Valid @RequestBody Medico medico) {
        return medicoService.save(medico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medico> updateMedico(@PathVariable Long id, @Valid @RequestBody Medico medicoDetails) throws BusinessException {
        Medico updatedMedico = medicoService.update(id, medicoDetails);
        return ResponseEntity.ok(updatedMedico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedico(@PathVariable Long id) throws BusinessException {
        medicoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/crm/{crm}")
    public ResponseEntity<Medico> getMedicoByCrm(@PathVariable("crm") String crm) {
        try {
            Medico medico = medicoService.findByName(crm);
            return ResponseEntity.ok(medico);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}


