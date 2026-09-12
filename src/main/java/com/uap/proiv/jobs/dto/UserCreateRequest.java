package com.uap.proiv.jobs.dto;

public class UserCreateRequest {

    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
    private Integer jobId;

    public UserCreateRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    // Setter compatible con GraphQL "first_name"
    public void setFirst_name(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    // Setter compatible con GraphQL "last_name"
    public void setLast_name(String lastName) { this.lastName = lastName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getJobId() { return jobId; }
    public void setJobId(Integer jobId) { this.jobId = jobId; }
    // Setter compatible con GraphQL "job_id"
    public void setJob_id(Integer jobId) { this.jobId = jobId; }
}