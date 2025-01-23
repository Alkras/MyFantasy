package com.example.myfantasy.world.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "monsters")
public class Monster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private int hitPoints;
    private int armor;
    private int agility;
    private int attack;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "x"),
            @JoinColumn(name = "y")
    })
    private Location currentLocation;
}
