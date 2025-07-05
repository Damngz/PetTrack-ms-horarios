package com.pettrack.horario.repositories;

import com.pettrack.horario.models.HorarioAtencion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, Long> {
  List<HorarioAtencion> findByIdUsuario(Long idUsuario);
  void deleteByIdUsuario(Long idUsuario);
}
