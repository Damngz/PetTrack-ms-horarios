package com.pettrack.horario.services;

import com.pettrack.horario.models.HorarioAtencion;
import com.pettrack.horario.repositories.HorarioAtencionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HorarioAtencionService {

  private final HorarioAtencionRepository horarioRepo;

  public HorarioAtencionService(HorarioAtencionRepository horarioRepo) {
    this.horarioRepo = horarioRepo;
  }

  public List<HorarioAtencion> obtenerHorarios(Long idUsuario) {
    return horarioRepo.findByIdUsuario(idUsuario);
  }

  public List<HorarioAtencion> guardarHorarios(List<HorarioAtencion> horarios) {
    return horarioRepo.saveAll(horarios);
  }

  public void eliminarHorarios(Long idUsuario) {
    horarioRepo.deleteByIdUsuario(idUsuario);
  }
}
