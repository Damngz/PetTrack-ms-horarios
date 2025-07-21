package com.pettrack.horario.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.pettrack.horario.models.HorarioAtencion;
import com.pettrack.horario.repositories.HorarioAtencionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class HorarioAtencionServiceTest {
    @Mock
    private HorarioAtencionRepository horarioRepo;

    @InjectMocks
    private HorarioAtencionService horarioService;

    private HorarioAtencion horario1;
    private HorarioAtencion horario2;
    private HorarioAtencion horario3;

    @BeforeEach
    void setUp() {
        horario1 = new HorarioAtencion();
        horario1.setIdHorario(1L);
        horario1.setIdUsuario(100L);
        horario1.setDiaSemana("Lunes");
        horario1.setHoraInicio(LocalTime.of(9, 0));
        horario1.setHoraFin(LocalTime.of(13, 0));

        horario2 = new HorarioAtencion();
        horario2.setIdHorario(2L);
        horario2.setIdUsuario(100L);
        horario2.setDiaSemana("Martes");
        horario2.setHoraInicio(LocalTime.of(14, 0));
        horario2.setHoraFin(LocalTime.of(18, 0));

        horario3 = new HorarioAtencion();
        horario3.setIdHorario(3L);
        horario3.setIdUsuario(100L);
        horario3.setDiaSemana("Miércoles");
        horario3.setHoraInicio(LocalTime.of(10, 0));
        horario3.setHoraFin(LocalTime.of(16, 0));
    }

    @Test
    void obtenerHorarios_deberiaRetornarListaDeHorarios() {
        when(horarioRepo.findByIdUsuario(100L))
                .thenReturn(Arrays.asList(horario1, horario2));

        List<HorarioAtencion> result = horarioService.obtenerHorarios(100L);

        assertEquals(2, result.size());
        verify(horarioRepo, times(1)).findByIdUsuario(100L);
    }

    @Test
    void guardarHorarios_conListaVacia_deberiaRetornarListaVacia() {
        List<HorarioAtencion> result = horarioService.guardarHorarios(Collections.emptyList());

        assertTrue(result.isEmpty());
        verify(horarioRepo, never()).deleteAll(any());
        verify(horarioRepo, never()).save(any());
    }

    @Test
    void guardarHorarios_conNuevosHorarios_deberiaGuardarCorrectamente() {
        // Configura horarios existentes
        when(horarioRepo.findByIdUsuario(100L))
                .thenReturn(Arrays.asList(horario1, horario2));

        // Nuevo horario para actualizar y otro para crear
        HorarioAtencion horarioActualizado = new HorarioAtencion();
        horarioActualizado.setIdUsuario(100L);
        horarioActualizado.setDiaSemana("Lunes"); // Existente
        horarioActualizado.setHoraInicio(LocalTime.of(10, 0));
        horarioActualizado.setHoraFin(LocalTime.of(14, 0));

        HorarioAtencion horarioNuevo = new HorarioAtencion();
        horarioNuevo.setIdUsuario(100L);
        horarioNuevo.setDiaSemana("Jueves"); // Nuevo
        horarioNuevo.setHoraInicio(LocalTime.of(11, 0));
        horarioNuevo.setHoraFin(LocalTime.of(15, 0));

        // Configura repositorio para búsqueda por día
        when(horarioRepo.findByIdUsuarioAndDiaSemana(100L, "Lunes"))
                .thenReturn(Optional.of(horario1));
        when(horarioRepo.findByIdUsuarioAndDiaSemana(100L, "Jueves"))
                .thenReturn(Optional.empty());

        // Configura guardado
        when(horarioRepo.save(any(HorarioAtencion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<HorarioAtencion> nuevosHorarios = Arrays.asList(horarioActualizado, horarioNuevo);
        List<HorarioAtencion> result = horarioService.guardarHorarios(nuevosHorarios);

        assertEquals(2, result.size());
        verify(horarioRepo, times(1)).deleteAll(any());
        verify(horarioRepo, times(2)).save(any());
    }

    @Test
    void eliminarHorarios_deberiaEliminarPorIdUsuario() {
        doNothing().when(horarioRepo).deleteByIdUsuario(100L);

        horarioService.eliminarHorarios(100L);

        verify(horarioRepo, times(1)).deleteByIdUsuario(100L);
    }
}
