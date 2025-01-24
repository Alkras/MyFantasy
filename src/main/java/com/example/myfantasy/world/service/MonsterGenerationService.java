package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.LocationBiome;
import com.example.myfantasy.world.model.LocationType;
import com.example.myfantasy.world.model.Monster;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@NoArgsConstructor
@Service
public class MonsterGenerationService {
    private static final int MID_STAT_BOOST = 3;
    private static final int HIGH_STAT_BOOST = 5;
    private static final int HIT_POINTS_STAT_BOOST = 10;

    Optional<Monster> generateMonster(LocationType locationType, LocationBiome locationBiome, int threatLvl) {
        if (!LocationType.MONSTER.equals(locationType) || threatLvl == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(
                switch (locationBiome) {
                    case FOREST -> createForestMonster(threatLvl);
                    case CASTLE -> createCastleMonster(threatLvl);
                    case VILLAGE -> createVillageMonster();
                    case CAVE -> createCaveMonster(threatLvl);
                    case DESERT -> createDesertMonster(threatLvl);
                });
    }

    private Monster createCaveMonster(int threatLvl) {
        return Monster.builder()
                .name("Cave monster")
                .armor(threatLvl * MID_STAT_BOOST)
                .agility(threatLvl)
                .attack(threatLvl * HIGH_STAT_BOOST)
                .hitPoints(threatLvl * HIT_POINTS_STAT_BOOST)
                .build();
    }

    private Monster createCastleMonster(int threatLvl) {
        return Monster.builder()
                .name("Castle monster")
                .armor(threatLvl * MID_STAT_BOOST)
                .agility(threatLvl * MID_STAT_BOOST)
                .attack(threatLvl * MID_STAT_BOOST)
                .hitPoints(threatLvl * HIT_POINTS_STAT_BOOST * MID_STAT_BOOST)
                .build();
    }

    private Monster createDesertMonster(int threatLvl) {
        return Monster.builder()
                .name("Desert monster")
                .armor(threatLvl)
                .agility(threatLvl * MID_STAT_BOOST)
                .attack(threatLvl * MID_STAT_BOOST)
                .hitPoints(threatLvl * HIT_POINTS_STAT_BOOST)
                .build();
    }

    private Monster createForestMonster(int threatLvl) {
        return Monster.builder()
                .name("Forest monster")
                .armor(threatLvl * HIGH_STAT_BOOST)
                .agility(threatLvl * MID_STAT_BOOST)
                .attack(threatLvl)
                .hitPoints(threatLvl * HIT_POINTS_STAT_BOOST * MID_STAT_BOOST)
                .build();
    }

    private Monster createVillageMonster() {
        return null;
    }
}
