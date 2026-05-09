package com.comicstore.comic_store.controller;

import com.comicstore.comic_store.config.AppConfig;
import com.comicstore.comic_store.config.SecurityConfig;
import com.comicstore.comic_store.dto.ComicResponse;
import com.comicstore.comic_store.dto.InventoryResponse;
import com.comicstore.comic_store.dto.PublisherResponse;
import com.comicstore.comic_store.entity.Genre;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.service.InventoryService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@Import({SecurityConfig.class, AppConfig.class})
@TestPropertySource(properties = "app.cors.allowed-origin=http://localhost:4200")
class InventoryControllerTest {

    @Autowired MockMvc mvc;
    @MockBean InventoryService service;
    @MockBean UserService userService;
    @MockBean JwtService jwtService;

    private InventoryResponse inventoryResponse;

    @BeforeEach
    void setUp() {
        PublisherResponse publisher = new PublisherResponse(1L, "Marvel", "US");
        ComicResponse comic = new ComicResponse(
                1L, "Spider-Man", 1, publisher, List.of(), Genre.SUPERHERO, new BigDecimal("3.99"), "Classic");
        inventoryResponse = new InventoryResponse(1L, comic, 10, LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void getAll_returns200WithList() throws Exception {
        when(service.findAll()).thenReturn(List.of(inventoryResponse));

        mvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quantity").value(10));
    }

    @Test
    @WithMockUser
    void getByComicId_returns200() throws Exception {
        when(service.findByComicId(1L)).thenReturn(inventoryResponse);

        mvc.perform(get("/api/inventory/comic/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @WithMockUser
    void getByComicId_returns404_whenNotFound() throws Exception {
        when(service.findByComicId(99L)).thenThrow(new ResourceNotFoundException("Inventory not found for comic: 99"));

        mvc.perform(get("/api/inventory/comic/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201_withValidBody() throws Exception {
        when(service.create(any())).thenReturn(inventoryResponse);

        String body = """
                {
                  "comicId": 1,
                  "quantity": 10
                }
                """;

        mvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns400_whenComicIdMissing() throws Exception {
        String body = """
                {
                  "quantity": 10
                }
                """;

        mvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_returns403_forNonAdmin() throws Exception {
        String body = """
                {
                  "comicId": 1,
                  "quantity": 10
                }
                """;

        mvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void restock_returns200() throws Exception {
        when(service.restock(eq(1L), eq(5))).thenReturn(inventoryResponse);

        String body = """
                {
                  "quantity": 5
                }
                """;

        mvc.perform(patch("/api/inventory/1/restock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void restock_returns400_whenQuantityZero() throws Exception {
        String body = """
                {
                  "quantity": 0
                }
                """;

        mvc.perform(patch("/api/inventory/1/restock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
