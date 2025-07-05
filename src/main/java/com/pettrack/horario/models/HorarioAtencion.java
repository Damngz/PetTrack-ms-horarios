package com.pettrack.horario.models;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "horario_atencion")
public class HorarioAtencion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idHorario;

  private Long idUsuario;

  private String diaSemana; 

  private LocalTime horaInicio;

  private LocalTime horaFin;
  
  public Long getIdHorario() {
    return idHorario;
  }

  public void setIdHorario(Long idHorario) {
    this.idHorario = idHorario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public String getDiaSemana() {
    return diaSemana;
  }

  public void setDiaSemana(String diaSemana) {
    this.diaSemana = diaSemana;
  }

  public LocalTime getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(LocalTime horaInicio) {
    this.horaInicio = horaInicio;
  }

  public LocalTime getHoraFin() {
    return horaFin;
  }

  public void setHoraFin(LocalTime horaFin) {
    this.horaFin = horaFin;
  }
}
