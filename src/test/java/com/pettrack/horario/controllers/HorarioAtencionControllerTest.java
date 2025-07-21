package com.pettrack.horario.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pettrack.horario.models.HorarioAtencion;
import com.pettrack.horario.services.HorarioAtencionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HorarioAtencionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private HorarioAtencionService horarioService;

    @InjectMocks
    private HorarioAtencionController horarioController;

    @BeforeEach
    void setUp() {
        // Inicializa y configura el ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(horarioController)
                .setControllerAdvice(horarioController)
                .defaultRequest(post("/").contentType(MediaType.APPLICATION_JSON))
                .build();
    }

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
    void handleRuntimeException_shouldReturnInternalServerError() {
        // Preparar una excepción genérica
        RuntimeException ex = new RuntimeException("Error desconocido");

        // Ejecutar el manejador de excepciones directamente
        ResponseEntity<String> response = horarioController.handleRuntime(ex);

        // Verificar
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error desconocido", response.getBody());
    }

    @Test
    void handleRuntimeException_shouldReturnConflictWhenDuplicate() {
        // Preparar una excepción de duplicado
        RuntimeException ex = new RuntimeException("El horario ya existe");

        // Ejecutar el manejador de excepciones
        ResponseEntity<String> response = horarioController.handleRuntime(ex);

        // Verificar
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El horario ya existe", response.getBody());
    }

    // Pruebas adicionales para los endpoints normales
    @Test
    void obtenerHorarios_shouldReturnOk() {
        // Configurar mock
        when(horarioService.obtenerHorarios(anyLong()))
                .thenReturn(Collections.emptyList());

        // Ejecutar
        ResponseEntity<List<HorarioAtencion>> response = horarioController.obtenerHorarios(1L);

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
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
                .content(objectMapper.writeValueAsString(lista)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idUsuario").value(100L));
    }

    @Test
    void eliminarHorarios_deberiaRetornar204() throws Exception {
        mockMvc.perform(delete("/horarios/100"))
                .andExpect(status().isNoContent());
    }

    @Test
    void guardarHorarios_diaDuplicado_deberiaRetornarConflict() throws Exception {
        HorarioAtencion h = ejemploHorario();
        List<HorarioAtencion> lista = List.of(h, h);

        when(horarioService.guardarHorarios(any()))
                .thenThrow(new RuntimeException("Ya existe un horario para ese día"));

        mockMvc.perform(post("/horarios")
                .content(objectMapper.writeValueAsString(lista)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya existe un horario para ese día"));
    }

}
