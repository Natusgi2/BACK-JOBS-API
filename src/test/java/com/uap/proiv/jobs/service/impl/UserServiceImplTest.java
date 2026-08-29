package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.client.UserApiRepository;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserApiRepository userApiRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserApiResponse userApiResponse;

    @BeforeEach
    void setup() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1);
        user.setFirstName("Juan");
        user.setLastName("Perez");
        user.setEmail("juan.perez@test.com");
        users.add(user);

        userApiResponse = new UserApiResponse();
        userApiResponse.setPage(1);
        userApiResponse.setPerPage(1);
        userApiResponse.setTotal(1);
        userApiResponse.setTotalPages(1);
        userApiResponse.setData(users);
    }

    @Test
    @DisplayName("search() - Retorna página de usuarios exitosamente")
    void search_Success() {
        // CORREGIDO: Llamar a getUsers(1) que es el método real de UserApiRepository
        when(userApiRepository.getUsers(1)).thenReturn(userApiResponse);

        UserApiResponse result = userService.search(1);

        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(1, result.getData().size());
        assertEquals("Juan", result.getData().get(0).getFirstName());
        
        verify(userApiRepository, times(1)).getUsers(1);
    }

    @Test
    @DisplayName("search() - Lanza excepción cuando el repositorio de usuarios falla")
    void search_RepositoryThrowsException() {
        // CORREGIDO: Llamar a getUsers(2) que es el método real de UserApiRepository
        when(userApiRepository.getUsers(2)).thenThrow(new RuntimeException("Error en API externa de usuarios"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.search(2));

        assertEquals("Error en API externa de usuarios", exception.getMessage());
        verify(userApiRepository, times(1)).getUsers(2);
    }
}

// arreglar maven, probar y subir