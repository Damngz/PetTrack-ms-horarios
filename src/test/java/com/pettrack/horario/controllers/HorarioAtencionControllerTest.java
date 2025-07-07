package com.pettrack.horario.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettrack.horario.models.HorarioAtencion;
import com.pettrack.horario.services.HorarioAtencionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HorarioAtencionController.class)
public class HorarioAtencionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HorarioAtencionService horarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private HorarioAtencion ejemploHorario() {
        HorarioAtencion h = new HorarioAtencion();
        h.setIdHorario(1L);
        h.setIdUsuario(100L);
        h.setDiaSemana("Lunes");
        h.setHoraInicio(LocalTime.of(9, 0));
        h.setHoraFin(LocalTime.of(13, 0));
        return h;
    }

    @Test
    void obtenerHorarios_deberiaRetornarLista() throws Exception {
        HorarioAtencion h = ejemploHorario();
        when(horarioService.obtenerHorarios(100L)).thenReturn(List.of(h));

        mockMvc.perform(get("/horarios/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].diaSemana").value("Lunes"));
    }

    @Test
    void guardarHorarios_deberiaRetornarHorariosGuardados() throws Exception {
        HorarioAtencion h = ejemploHorario();
        List<HorarioAtencion> lista = List.of(h);

        when(horarioService.guardarHorarios(anyList())).thenReturn(lista);

        mockMvc.perform(post("/horarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lista)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idUsuario").value(100L));
    }

    @Test
    void eliminarHorarios_deberiaRetornar204() throws Exception {
        doNothing().when(horarioService).eliminarHorarios(100L);

        mockMvc.perform(delete("/horarios/100"))
                .andExpect(status().isNoContent());
    }


    @Test
    void guardarHorarios_diaDuplicado_deberiaRetornarConflict() throws Exception {
        HorarioAtencion h = new HorarioAtencion();
        h.setIdUsuario(100L);
        h.setDiaSemana("Lunes");
        h.setHoraInicio(LocalTime.of(8, 0));
        h.setHoraFin(LocalTime.of(12, 0));

        List<HorarioAtencion> lista = List.of(h, h); // Duplicado

        when(horarioService.guardarHorarios(any()))
                .thenThrow(new RuntimeException("Ya existe un horario para ese día"));

        mockMvc.perform(post("/horarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lista)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya existe un horario para ese día"));
    }

}
