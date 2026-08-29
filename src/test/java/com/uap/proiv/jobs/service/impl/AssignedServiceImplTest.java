package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.dto.AssignedResponse;
import com.uap.proiv.jobs.dto.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class AssignedServiceImplTest {

    @InjectMocks
    private AssignedServiceImpl assignedService;

    private List<Job> jobs;

    @BeforeEach
    void setup() {
        jobs = new ArrayList<>();

        Job job1 = new Job();
        job1.setId(101);
        job1.setName("Developer");
        job1.setResources(2);
        jobs.add(job1);

        Job job2 = new Job();
        job2.setId(102);
        job2.setName("QA Tester");
        job2.setResources(1);
        jobs.add(job2);
    }

    @Test
    @DisplayName("create() - Asigna usuarios a trabajos correctamente")
    void create_Success() {
        List<Integer> userIds = List.of(1, 2, 3);

        List<AssignedResponse> result = assignedService.create(jobs, userIds);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("create() - Retorna lista vacía cuando no hay usuarios para asignar")
    void create_EmptyUserList() {
        List<AssignedResponse> result = assignedService.create(jobs, Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("create() - Lanza excepción o maneja lista nula")
    void create_NullParameters() {
        assertThrows(NullPointerException.class, () -> assignedService.create(null, List.of(1, 2)));
    }
}