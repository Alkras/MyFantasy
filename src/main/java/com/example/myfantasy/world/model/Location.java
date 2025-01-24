package com.example.myfantasy.world.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "locations")
@Builder
@AllArgsConstructor
@IdClass(LocationKey.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Location {
    @Id
    @EqualsAndHashCode.Include
    private Long x;

    @Id
    @EqualsAndHashCode.Include
    private Long y;

    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    @Enumerated(EnumType.STRING)
    private LocationBiome locationBiome;

    private int locationThreatLevel;
}