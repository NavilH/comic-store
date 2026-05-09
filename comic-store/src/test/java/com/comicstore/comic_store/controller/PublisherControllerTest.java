package com.comicstore.comic_store.controller;

import com.comicstore.comic_store.config.AppConfig;
import com.comicstore.comic_store.config.SecurityConfig;
import com.comicstore.comic_store.dto.PublisherRequest;
import com.comicstore.comic_store.dto.PublisherResponse;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.service.JwtService;
import com.comicstore.comic_store.service.PublisherService;
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

@WebMvcTest(PublisherController.class)
@Import({SecurityConfig.class, AppConfig.class})
@TestPropertySource(properties = "app.cors.allowed-origin=http://localhost:4200")
class PublisherControllerTest {

    @Autowired MockMvc mvc;
    @MockBean PublisherService service;
    @MockBean UserService userService;
    @MockBean JwtService jwtService;

    private PublisherResponse publisherResponse;

    @BeforeEach
    void setUp() {
        publisherResponse = new PublisherResponse(1L, "Marvel", "US");
    }

    @Test
    @WithMockUser
    void getAll_returns200WithList() throws Exception {
        when(service.findAll()).thenReturn(List.of(publisherResponse));

        mvc.perform(get("/api/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Marvel"));
    }

    @Test
    @WithMockUser
    void getById_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(publisherResponse);

        mvc.perform(get("/api/publishers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Marvel"));
    }

    @Test
    @WithMockUser
    void getById_returns404_whenNotFound() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Publisher not found: 99"));

        mvc.perform(get("/api/publishers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201_withValidBody() throws Exception {
        when(service.create(any(PublisherRequest.class))).thenReturn(publisherResponse);

        String body = """
                {
                  "name": "Marvel",
                  "country": "US"
                }
                """;

        mvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Marvel"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns400_whenNameMissing() throws Exception {
        String body = """
                {
                  "country": "US"
                }
                """;

        mvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_returns403_forNonAdmin() throws Exception {
        String body = """
                {
                  "name": "Marvel",
                  "country": "US"
                }
                """;

        mvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returns200() throws Exception {
        when(service.update(eq(1L), any(PublisherRequest.class))).thenReturn(publisherResponse);

        String body = """
                {
                  "name": "Marvel Comics",
                  "country": "US"
                }
                """;

        mvc.perform(put("/api/publishers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marvel"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mvc.perform(delete("/api/publishers/1"))
                .andExpect(status().isNoContent());
    }
}
