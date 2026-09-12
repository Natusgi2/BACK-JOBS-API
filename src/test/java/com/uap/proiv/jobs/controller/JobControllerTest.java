package com.uap.proiv.jobs.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.service.JobService;

@ExtendWith(MockitoExtension.class)
class JobControllerTest {

    @Mock
    private JobService jobService;

    @InjectMocks
    private JobController jobController;

    private MockMvc mockMvc;
    private List<Job> jobs;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();

        Job job1 = new Job();
        job1.setId(1);
        job1.setName("Developer");
        job1.setSalary(5000);
        job1.setHours(2000);
        job1.setResources(3);

        jobs = List.of(job1);
    }

    @Test
    @DisplayName("GET /api/job/all - Retorna lista completa de trabajos")
    void getAllJobs_Success() throws Exception {
        when(jobService.getAllJobs()).thenReturn(jobs);

        mockMvc.perform(get("/api/job/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Developer"));

        verify(jobService, times(1)).getAllJobs();
    }

    @Test
    @DisplayName("GET /api/job/{id} - Retorna trabajo por ID")
    void getJobById_Success() throws Exception {
        when(jobService.getJobById(1)).thenReturn(jobs.get(0));

        mockMvc.perform(get("/api/job/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Developer"))
                .andExpect(jsonPath("$.salary").value(5000));

        verify(jobService, times(1)).getJobById(1);
    }

    @Test
    @DisplayName("GET /api/job/all - Retorna 500 cuando el servicio falla")
    void getAllJobs_ServiceException() throws Exception {
        when(jobService.getAllJobs()).thenThrow(new RuntimeException("Error en base de datos"));

        mockMvc.perform(get("/api/job/all"))
                .andExpect(status().is5xxServerError());

        verify(jobService, times(1)).getAllJobs();
    }
}
    //adelantar los test unitarios de userService y assignedService.
    //ademas AssignedServiceImpl y UserServiceImpl deberian ser testeados 
    //con test unitarios, para que el controller sea testeado de manera aislada. 
    //hacer casos satisfactorios y de error, para que el controller sea testeado de manera aislada.
