package com.uap.proiv.jobs.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.uap.proiv.jobs.client.JobApiRepository;
import com.uap.proiv.jobs.dto.Job;

// Solo mantenemos las aserciones de JUnit 5 (Jupiter)
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobsApplicationTests {

    @Mock
    JobApiRepository jobApiRepository;

    @InjectMocks
    JobServiceImpl jobServiceImpl;

    List<Job> jobs;

    @BeforeEach
    void setup(){
        jobs = new ArrayList<>();
        
        Job job1 = new Job();
        job1.setId(1);
        job1.setName("Developer");
        job1.setSalary(5000);
        job1.setHours(2000);
        job1.setResources(3);
        jobs.add(job1);

        Job job2 = new Job();
        job2.setId(2);
        job2.setName("Designer");
        job2.setSalary(4500);
        job2.setHours(1500);
        job2.setResources(1);
        jobs.add(job2);

        Job job3 = new Job();
        job3.setId(3);
        job3.setName("Ceo");
        job3.setSalary(60000);
        job3.setHours(45);
        job3.setResources(1);
        jobs.add(job3);
    }

    @Test 
    @DisplayName("Verifica que el metodo getAllJobs() retorne la lista completa")
    void getAllJob_Success(){
        when(jobApiRepository.getAllJobs()).thenReturn(jobs);

        List<Job> result = jobServiceImpl.getAllJobs();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Developer", result.get(0).getName());

        verify(jobApiRepository, times(1)).getAllJobs();
    }

    @Test
    @DisplayName("Verifica una exception en jobApiRepository")
    void getAllJob_Exception(){
        when(jobApiRepository.getAllJobs()).thenReturn(new ArrayList<>());

        // Lambda simplificado (sin llaves) o usando punto y coma interno
        assertThrows(NoSuchElementException.class, 
            () -> jobServiceImpl.getJobById(1));

        verify(jobApiRepository, times(1)).getAllJobs();
    }
}