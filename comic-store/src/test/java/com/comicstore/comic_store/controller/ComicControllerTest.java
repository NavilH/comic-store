package com.comicstore.comic_store.controller;

import com.comicstore.comic_store.config.AppConfig;
import com.comicstore.comic_store.config.SecurityConfig;
import com.comicstore.comic_store.dto.ComicResponse;
import com.comicstore.comic_store.dto.PageResponse;
import com.comicstore.comic_store.dto.PublisherResponse;
import com.comicstore.comic_store.entity.Genre;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.service.ComicService;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComicController.class)
@Import({SecurityConfig.class, AppConfig.class})
@TestPropertySource(properties = "app.cors.allowed-origin=http://localhost:4200")
class ComicControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ComicService service;
    @MockBean UserService userService;
    @MockBean JwtService jwtService;

    private ComicResponse comicResponse;

    @BeforeEach
    void setUp() {
        PublisherResponse publisher = new PublisherResponse(1L, "Marvel", "US");
        comicResponse = new ComicResponse(
                1L, "Spider-Man", 1, publisher, List.of(), Genre.SUPERHERO, new BigDecimal("3.99"), "Classic");
    }

    @Test
    @WithMockUser
    void getAll_returns200WithPagedList() throws Exception {
        PageResponse<ComicResponse> page = new PageResponse<>(List.of(comicResponse), 0, 20, 1L, 1);
        when(service.findAll(any())).thenReturn(page);

        mvc.perform(get("/api/comics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Spider-Man"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    void getAll_byPublisherId_returnsFilteredList() throws Exception {
        when(service.findByPublisher(1L)).thenReturn(List.of(comicResponse));

        mvc.perform(get("/api/comics").param("publisherId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser
    void getById_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(comicResponse);

        mvc.perform(get("/api/comics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Spider-Man"));
    }

    @Test
    @WithMockUser
    void getById_returns404_whenNotFound() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Comic not found: 99"));

        mvc.perform(get("/api/comics/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201_withValidBody() throws Exception {
        when(service.create(any())).thenReturn(comicResponse);

        String body = """
                {
                  "title": "Spider-Man",
                  "issueNumber": 1,
                  "publisherId": 1,
                  "authorIds": [],
                  "genre": "SUPERHERO",
                  "price": 3.99,
                  "description": "Classic"
                }
                """;

        mvc.perform(post("/api/comics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Spider-Man"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns400_whenTitleMissing() throws Exception {
        String body = """
                {
                  "issueNumber": 1,
                  "publisherId": 1,
                  "price": 3.99
                }
                """;

        mvc.perform(post("/api/comics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_returns403_forNonAdmin() throws Exception {
        String body = """
                {
                  "title": "Spider-Man",
                  "issueNumber": 1,
                  "publisherId": 1,
                  "authorIds": [],
                  "genre": "SUPERHERO",
                  "price": 3.99,
                  "description": "Classic"
                }
                """;

        mvc.perform(post("/api/comics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returns200() throws Exception {
        when(service.update(eq(1L), any())).thenReturn(comicResponse);

        String body = """
                {
                  "title": "Spider-Man Updated",
                  "issueNumber": 2,
                  "publisherId": 1,
                  "authorIds": [],
                  "genre": "SUPERHERO",
                  "price": 4.99,
                  "description": "Updated"
                }
                """;

        mvc.perform(put("/api/comics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mvc.perform(delete("/api/comics/1"))
                .andExpect(status().isNoContent());
    }
}
