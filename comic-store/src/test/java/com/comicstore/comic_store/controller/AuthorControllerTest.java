package com.comicstore.comic_store.controller;

import com.comicstore.comic_store.config.AppConfig;
import com.comicstore.comic_store.config.SecurityConfig;
import com.comicstore.comic_store.dto.AuthorRequest;
import com.comicstore.comic_store.dto.AuthorResponse;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.service.AuthorService;
import com.comicstore.comic_store.service.JwtService;
import com.comicstore.comic_store.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@Import({SecurityConfig.class, AppConfig.class})
@TestPropertySource(properties = "app.cors.allowed-origin=http://localhost:4200")
class AuthorControllerTest {

    @Autowired MockMvc mvc;
    @MockBean AuthorService service;
    @MockBean UserService userService;
    @MockBean JwtService jwtService;

    private AuthorResponse authorResponse;

    @BeforeEach
    void setUp() {
        authorResponse = new AuthorResponse(1L, "Stan Lee");
    }

    @Test
    @WithMockUser
    void getAll_returns200WithList() throws Exception {
        when(service.findAll()).thenReturn(List.of(authorResponse));

        mvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Stan Lee"));
    }

    @Test
    @WithMockUser
    void getById_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(authorResponse);

        mvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Stan Lee"));
    }

    @Test
    @WithMockUser
    void getById_returns404_whenNotFound() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Author not found: 99"));

        mvc.perform(get("/api/authors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201_withValidBody() throws Exception {
        when(service.create(any(AuthorRequest.class))).thenReturn(authorResponse);

        String body = """
                {
                  "name": "Stan Lee"
                }
                """;

        mvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Stan Lee"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns400_whenNameMissing() throws Exception {
        String body = """
                {
                }
                """;

        mvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_returns403_forNonAdmin() throws Exception {
        String body = """
                {
                  "name": "Stan Lee"
                }
                """;

        mvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returns200() throws Exception {
        when(service.update(eq(1L), any(AuthorRequest.class))).thenReturn(authorResponse);

        String body = """
                {
                  "name": "Stan Lee Updated"
                }
                """;

        mvc.perform(put("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Stan Lee"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());
    }
}
