package com.pettrack.horario.services;

import com.pettrack.horario.models.HorarioAtencion;
import com.pettrack.horario.repositories.HorarioAtencionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HorarioAtencionService {

  private final HorarioAtencionRepository horarioRepo;

  public HorarioAtencionService(HorarioAtencionRepository horarioRepo) {
    this.horarioRepo = horarioRepo;
  }

  public List<HorarioAtencion> obtenerHorarios(Long idUsuario) {
    return horarioRepo.findByIdUsuario(idUsuario);
  }

  public List<HorarioAtencion> guardarHorarios(List<HorarioAtencion> nuevosHorarios) {
    if (nuevosHorarios.isEmpty()) {
      return List.of(); // Nada que hacer
    }

    Long idUsuario = nuevosHorarios.get(0).getIdUsuario();

    // Trae los horarios actuales del usuario
    List<HorarioAtencion> actuales = horarioRepo.findByIdUsuario(idUsuario);

    // Obtiene los días que vienen en la nueva solicitud
    List<String> nuevosDias = nuevosHorarios.stream()
        .map(HorarioAtencion::getDiaSemana)
        .collect(Collectors.toList());

    // Filtra los días que ya no están en la nueva lista → se deben eliminar
    List<HorarioAtencion> aEliminar = actuales.stream()
        .filter(actual -> !nuevosDias.contains(actual.getDiaSemana()))
        .collect(Collectors.toList());

    // Borra los que ya no deben existir
    horarioRepo.deleteAll(aEliminar);

    // Inserta o actualiza los que vienen nuevos
    return nuevosHorarios.stream().map(nuevo -> {
      Optional<HorarioAtencion> existenteOpt = horarioRepo.findByIdUsuarioAndDiaSemana(
          nuevo.getIdUsuario(),
          nuevo.getDiaSemana());

      if (existenteOpt.isPresent()) {
        HorarioAtencion existente = existenteOpt.get();
        existente.setHoraInicio(nuevo.getHoraInicio());
        existente.setHoraFin(nuevo.getHoraFin());
        return horarioRepo.save(existente);
      } else {
        return horarioRepo.save(nuevo);
      }
    }).collect(Collectors.toList());
  }

  public void eliminarHorarios(Long idUsuario) {
    horarioRepo.deleteByIdUsuario(idUsuario);
  }
}
