package com.uap.proiv.jobs.controller;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uap.proiv.jobs.dto.AssignRequest;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.UserJobAssignedService;

@SpringBootTest
@AutoConfigureMockMvc
class AssignControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserJobAssignedService userJobAssignedService;

    @Test
    @DisplayName("POST /api/assign - Integración controller y service mockeado")
    void assign_IntegrationSuccess() throws Exception {
        AssignRequest request = new AssignRequest();
        request.setRequestNumber(1001);
        request.setClientName("Google Inc");

        Job job = new Job();
        job.setId(1);
        job.setName("Developer");

        User user = new User();
        user.setId(10);
        user.setFirstName("Ana");

        List<UserJobAssigned> assignedList = List.of(new UserJobAssigned(List.of(user), job));
        when(userJobAssignedService.assign()).thenReturn(assignedList);

        mockMvc.perform(post("/api/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Request_Number").value(1001))
                .andExpect(jsonPath("$.Client").value("Google Inc"))
                .andExpect(jsonPath("$.Assign[0].job.name").value("Developer"))
                .andExpect(jsonPath("$.Assign[0].users[0].first_name").value("Ana"));
    }

    @Test
    @DisplayName("POST /api/assign - Manejo de error cuando el servicio falla")
    void assign_IntegrationError() throws Exception {
        AssignRequest request = new AssignRequest();
        request.setRequestNumber(1002);
        request.setClientName("Error Client");

        when(userJobAssignedService.assign()).thenThrow(new RuntimeException("Fallo en asignación"));

        mockMvc.perform(post("/api/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }
}