package com.comicstore.comic_store.controller;

import com.comicstore.comic_store.config.AppConfig;
import com.comicstore.comic_store.config.SecurityConfig;
import com.comicstore.comic_store.dto.ComicResponse;
import com.comicstore.comic_store.dto.PageResponse;
import com.comicstore.comic_store.dto.PublisherResponse;
import com.comicstore.comic_store.dto.SaleItemResponse;
import com.comicstore.comic_store.dto.SaleResponse;
import com.comicstore.comic_store.entity.Genre;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.service.JwtService;
import com.comicstore.comic_store.service.SaleService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
@Import({SecurityConfig.class, AppConfig.class})
@TestPropertySource(properties = "app.cors.allowed-origin=http://localhost:4200")
class SaleControllerTest {

    @Autowired MockMvc mvc;
    @MockBean SaleService service;
    @MockBean UserService userService;
    @MockBean JwtService jwtService;

    private SaleResponse saleResponse;

    @BeforeEach
    void setUp() {
        PublisherResponse publisher = new PublisherResponse(1L, "Marvel", "US");
        ComicResponse comic = new ComicResponse(
                1L, "Spider-Man", 1, publisher, List.of(), Genre.SUPERHERO, new BigDecimal("3.99"), "Classic");
        SaleItemResponse item = new SaleItemResponse(1L, comic, 2, new BigDecimal("3.99"));
        saleResponse = new SaleResponse(1L, LocalDateTime.now(), new BigDecimal("7.98"), List.of(item));
    }

    @Test
    @WithMockUser
    void getAll_returns200WithPagedList() throws Exception {
        PageResponse<SaleResponse> page = new PageResponse<>(List.of(saleResponse), 0, 20, 1L, 1);
        when(service.findAll(any())).thenReturn(page);

        mvc.perform(get("/api/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    void getById_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(saleResponse);

        mvc.perform(get("/api/sales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @WithMockUser
    void getById_returns404_whenNotFound() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Sale not found: 99"));

        mvc.perform(get("/api/sales/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201_withValidBody() throws Exception {
        when(service.create(any())).thenReturn(saleResponse);

        String body = """
                {
                  "items": [
                    { "comicId": 1, "quantity": 2 }
                  ]
                }
                """;

        mvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAmount").value(7.98));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns400_whenItemsEmpty() throws Exception {
        String body = """
                {
                  "items": []
                }
                """;

        mvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_returns403_forNonAdmin() throws Exception {
        String body = """
                {
                  "items": [
                    { "comicId": 1, "quantity": 2 }
                  ]
                }
                """;

        mvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }
}
