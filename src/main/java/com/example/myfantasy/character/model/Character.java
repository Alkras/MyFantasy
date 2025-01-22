package com.example.myfantasy.character.model;

import com.example.myfantasy.world.model.Location;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private int level;
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "x"),
            @JoinColumn(name = "y")
    })
    private Location currentLocation;

    private BigDecimal money;

    @OneToMany(mappedBy="character", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIdentityReference
    private List<Item> inventory;

}