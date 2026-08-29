package com.uap.proiv.jobs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uap.proiv.jobs.dto.AssignRequest;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.JobService;
import com.uap.proiv.jobs.service.UserJobAssignedService;
import com.uap.proiv.jobs.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JobService jobService;

    @Mock
    private UserJobAssignedService userJobAssignedService;

    @InjectMocks
    private JobController jobController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserApiResponse userApiResponse;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
        objectMapper = new ObjectMapper();

        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1);
        user1.setFirstName("Juan");
        user1.setLastName("Garcia");
        users.add(user1);

        userApiResponse = new UserApiResponse();
        userApiResponse.setPage(1);
        userApiResponse.setPerPage(1);
        userApiResponse.setTotal(1);
        userApiResponse.setTotalPages(1);
        userApiResponse.setData(users);
    }

    @Test
    @DisplayName("GET /api/job/users/{page} - Retorna 200 OK con usuarios")
    void getUsers_Success() throws Exception {
        when(userService.search(1)).thenReturn(userApiResponse);

        mockMvc.perform(get("/api/job/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].first_name").value("Juan"));

        verify(userService, times(1)).search(1);
    }

    @Test
    @DisplayName("GET /api/job/users/{page} - Retorna 500 cuando el servicio falla")
    void getUsers_ServiceException() throws Exception {
        when(userService.search(anyInt())).thenThrow(new RuntimeException("Error en servicio de usuarios"));

        mockMvc.perform(get("/api/job/users/5"))
                .andExpect(status().is5xxServerError());

        verify(userService, times(1)).search(5);
    }

    @Test
    @DisplayName("POST /api/job/assign - Retorna 200 OK con asignación completa")
    void postAssign_Success() throws Exception {
        AssignRequest request = new AssignRequest();
        request.setRequestNumber(999);
        request.setClientName("Empresa Test");

        Job job = new Job();
        job.setId(1);
        job.setName("Developer");

        List<UserJobAssigned> assignedList = List.of(new UserJobAssigned(userApiResponse.getData(), job));
        when(userJobAssignedService.assign()).thenReturn(assignedList);

        mockMvc.perform(post("/api/job/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Request_Number").value(999))
                .andExpect(jsonPath("$.Client").value("Empresa Test"))
                .andExpect(jsonPath("$.Assign[0].job.name").value("Developer"));

        verify(userJobAssignedService, times(1)).assign();
    }

    @Test
    @DisplayName("POST /api/job/assign - Retorna 500 si falla el proceso de asignación")
    void postAssign_ServiceException() throws Exception {
        AssignRequest request = new AssignRequest();
        request.setRequestNumber(999);
        request.setClientName("Empresa Test");

        when(userJobAssignedService.assign()).thenThrow(new RuntimeException("Fallo al calcular asignaciones"));

        mockMvc.perform(post("/api/job/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());

        verify(userJobAssignedService, times(1)).assign();
    }
}

    //adelantar los test unitarios de userService y assignedService.
    //ademas AssignedServiceImpl y UserServiceImpl deberian ser testeados 
    //con test unitarios, para que el controller sea testeado de manera aislada. 
    //hacer casos satisfactorios y de error, para que el controller sea testeado de manera aislada.
