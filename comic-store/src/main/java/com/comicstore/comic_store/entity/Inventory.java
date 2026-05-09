package com.comicstore.comic_store.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "comic_id", nullable = false, unique = true)
    private Comic comic;

    @Min(0)
    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    private LocalDateTime lastRestocked;
}
