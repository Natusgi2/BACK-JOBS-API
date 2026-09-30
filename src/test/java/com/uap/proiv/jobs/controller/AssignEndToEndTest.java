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

    private static final String USER_PAGE_RESPONSE = """
            {
                "page": 1,
                "per_page": 6,
                "total": 12,
                "total_pages": 2,
                "data": [
                    {
                        "id": 1,
                        "email": "george.bluth@reqres.in",
                        "first_name": "George",
                        "last_name": "Bluth",
                        "avatar": "https://reqres.in/img/faces/1-image.jpg"
                    },
                    {
                        "id": 2,
                        "email": "janet.weaver@reqres.in",
                        "first_name": "Janet",
                        "last_name": "Weaver",
                        "avatar": "https://reqres.in/img/faces/2-image.jpg"
                    },
                    {
                        "id": 3,
                        "email": "emma.wong@reqres.in",
                        "first_name": "Emma",
                        "last_name": "Wong",
                        "avatar": "https://reqres.in/img/faces/3-image.jpg"
                    }
                ]
            }
            """;

    private static final String SINGLE_USER_RESPONSE = """
            {
                "data": {
                    "id": 1,
                    "email": "george.bluth@reqres.in",
                    "first_name": "George",
                    "last_name": "Bluth",
                    "avatar": "https://reqres.in/img/faces/1-image.jpg"
                }
            }
            """;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        
        // El Dispatcher responde a cualquier petición sin agotarse ni bloquearse jamás
        mockWebServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String path = request.getPath();
                if (path != null && path.contains("/id/")) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody(SINGLE_USER_RESPONSE);
                }
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
            // CORREGIDO: Usar la URL raíz sin /api/users duplicado
            String baseUrl = mockWebServer.url("/").toString();
            // Quitar barra final si la tiene para evitar dobles barras //
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            String apiKey = "test_key";
            return new UserApiRepository(httpClient, objectMapper, baseUrl, apiKey);
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