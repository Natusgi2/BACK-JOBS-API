package com.uap.proiv.jobs.controller;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uap.proiv.jobs.client.UserApiRepository;
import com.uap.proiv.jobs.dto.AssignRequest;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest
@AutoConfigureMockMvc
class AssignEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    // 12 usuarios simulados para cubrir todas las posiciones de jobs.json
    private static final String USER_PAGE_RESPONSE = """
            {
                "page": 1,
                "per_page": 12,
                "total": 12,
                "total_pages": 1,
                "data": [
                    {"id": 1, "email": "user1@reqres.in", "first_name": "George", "last_name": "Bluth", "avatar": "https://reqres.in/img/faces/1-image.jpg"},
                    {"id": 2, "email": "user2@reqres.in", "first_name": "Janet", "last_name": "Weaver", "avatar": "https://reqres.in/img/faces/2-image.jpg"},
                    {"id": 3, "email": "user3@reqres.in", "first_name": "Emma", "last_name": "Wong", "avatar": "https://reqres.in/img/faces/3-image.jpg"},
                    {"id": 4, "email": "user4@reqres.in", "first_name": "Eve", "last_name": "Holt", "avatar": "https://reqres.in/img/faces/4-image.jpg"},
                    {"id": 5, "email": "user5@reqres.in", "first_name": "Charles", "last_name": "Morris", "avatar": "https://reqres.in/img/faces/5-image.jpg"},
                    {"id": 6, "email": "user6@reqres.in", "first_name": "Tracey", "last_name": "Ramos", "avatar": "https://reqres.in/img/faces/6-image.jpg"},
                    {"id": 7, "email": "user7@reqres.in", "first_name": "Michael", "last_name": "Lawson", "avatar": "https://reqres.in/img/faces/7-image.jpg"},
                    {"id": 8, "email": "user8@reqres.in", "first_name": "Lindsay", "last_name": "Ferguson", "avatar": "https://reqres.in/img/faces/8-image.jpg"},
                    {"id": 9, "email": "user9@reqres.in", "first_name": "Tobias", "last_name": "Funke", "avatar": "https://reqres.in/img/faces/9-image.jpg"},
                    {"id": 10, "email": "user10@reqres.in", "first_name": "Byron", "last_name": "Fields", "avatar": "https://reqres.in/img/faces/10-image.jpg"},
                    {"id": 11, "email": "user11@reqres.in", "first_name": "George", "last_name": "Edwards", "avatar": "https://reqres.in/img/faces/11-image.jpg"},
                    {"id": 12, "email": "user12@reqres.in", "first_name": "Rachel", "last_name": "Howell", "avatar": "https://reqres.in/img/faces/12-image.jpg"}
                ]
            }
            """;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(USER_PAGE_RESPONSE);
            }
        });
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        UserApiRepository userApiRepository(ObjectMapper objectMapper) {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            String baseUrl = mockWebServer.url("/").toString();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            return new UserApiRepository(httpClient, objectMapper, baseUrl, "test_key");
        }
    }

    @Test
    @DisplayName("E2E POST /api/assign - Flujo completo real desde endpoint hasta repositorio")
    void postAssign_EndToEndSuccess() throws Exception {
        AssignRequest request = new AssignRequest();
        request.setRequestNumber(555);
        request.setClientName("E2E Test Client");

        mockMvc.perform(post("/api/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Request_Number").value(555))
                .andExpect(jsonPath("$.Client").value("E2E Test Client"))
                .andExpect(jsonPath("$.Assign").isArray())
                .andExpect(jsonPath("$.Assign[0].job").exists());
    }
}