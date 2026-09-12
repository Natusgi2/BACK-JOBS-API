package com.uap.proiv.jobs.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.uap.proiv.jobs.dto.AssignRequest;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.JobRequest;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserCreateRequest;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.JobService;
import com.uap.proiv.jobs.service.UserJobAssignedService;
import com.uap.proiv.jobs.service.UserService;

@Controller
public class GraphqlController {
    private final UserService userService;
    private final JobService jobService;
    private final UserJobAssignedService userJobAssignedService;

    @Autowired
    public GraphqlController(UserService userService,
                             JobService jobService,
                             UserJobAssignedService userJobAssignedService) {
        this.userService = userService;
        this.jobService = jobService;
        this.userJobAssignedService = userJobAssignedService;
    }

    @QueryMapping
    public List<UserJobAssigned> assigneds(@Argument AssignRequest request) {
        return userJobAssignedService.assign();
    }

    @QueryMapping
    public User userById(@Argument int id) {
        return userService.searchById(id);
    }

    @QueryMapping
    public Job JobById(@Argument int id) {
        return jobService.getJobById(id);
    }

    @SchemaMapping(typeName = "User", field = "job")
    public Job job(User user) {
        return jobService.getJobById(user.getJobId());
    }

    @MutationMapping
    public Job addJob(@Argument JobRequest request) {
        return jobService.add(request);
    }
    // realizar una mutacion de usuario para dar de alta un usuario y  
    // asignarle un trabajo, para eso se debe crear un DTO que contenga 
    // los datos del usuario y el id del trabajo al que se le va a asignar

    @MutationMapping
    public User createUser(@Argument("request") UserCreateRequest request) {
        return userService.createUser(request);
    }
}
