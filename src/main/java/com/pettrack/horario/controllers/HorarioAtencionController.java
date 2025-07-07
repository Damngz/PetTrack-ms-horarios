package com.pettrack.horario.controllers;

import com.pettrack.horario.models.HorarioAtencion;
import com.pettrack.horario.services.HorarioAtencionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/horarios")
public class HorarioAtencionController {
  private final HorarioAtencionService horarioService;

  public HorarioAtencionController(HorarioAtencionService horarioService) {
    this.horarioService = horarioService;
  }

  @GetMapping("/{idUsuario}")
  public ResponseEntity<List<HorarioAtencion>> obtenerHorarios(@PathVariable Long idUsuario) {
    return ResponseEntity.ok(horarioService.obtenerHorarios(idUsuario));
  }

  @PostMapping
  public ResponseEntity<List<HorarioAtencion>> guardarHorarios(@RequestBody List<HorarioAtencion> horarios) {
    return ResponseEntity.ok(horarioService.guardarHorarios(horarios));
  }

  @DeleteMapping("/{idUsuario}")
  public ResponseEntity<Void> eliminarHorarios(@PathVariable Long idUsuario) {
    horarioService.eliminarHorarios(idUsuario);
    return ResponseEntity.noContent().build();
  }
}
