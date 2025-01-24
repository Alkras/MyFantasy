package com.example.myfantasy.character.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "item_templates")
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class ItemTemplate {

    @Id
    private String name;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    private BigDecimal price;

    private int strength;

}
