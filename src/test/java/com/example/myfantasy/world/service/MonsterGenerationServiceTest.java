package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.LocationBiome;
import com.example.myfantasy.world.model.LocationType;
import com.example.myfantasy.world.model.Monster;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static com.example.myfantasy.world.model.LocationBiome.*;
import static com.example.myfantasy.world.model.LocationType.MONSTER;
import static com.example.myfantasy.world.model.LocationType.SHOP;
import static org.assertj.core.api.Assertions.assertThat;

class MonsterGenerationServiceTest {
    private final MonsterGenerationService monsterGenerationService = new MonsterGenerationService();

    public static Stream<Arguments> generateMonsterSource() {
        return Stream.of(
                Arguments.of(MONSTER, CASTLE, 2, createMonster("Castle monster", 6, 6, 6, 60)),
                Arguments.of(MONSTER, CAVE, 2, createMonster("Cave monster", 6, 2, 10, 20)),
                Arguments.of(MONSTER, DESERT, 2, createMonster("Desert monster", 2, 6, 6, 20)),
                Arguments.of(MONSTER, FOREST, 3, createMonster("Forest monster", 15, 9, 3, 90))
        );
    }

    private static Monster createMonster(String caveMonster, int armor, int agility, int attack, int hitPoints) {
        return Monster.builder()
                .name(caveMonster)
                .armor(armor)
                .agility(agility)
                .attack(attack)
                .hitPoints(hitPoints)
                .build();
    }

    @ParameterizedTest
    @MethodSource("generateMonsterSource")
    void generateMonster(LocationType locationType,
                         LocationBiome locationBiome,
                         int threatLvl,
                         Monster expectedResult) {
        Optional<Monster> generatedMonster = monsterGenerationService.generateMonster(locationType, locationBiome, threatLvl);

        assertThat(generatedMonster)
                .isNotEmpty()
                .get()
                .isNotNull()
                .satisfies(
                        x -> assertThat(x.getId()).isNull(),
                        x -> assertThat(x.getName()).isEqualTo(expectedResult.getName()),
                        x -> assertThat(x.getAttack()).isEqualTo(expectedResult.getAttack()),
                        x -> assertThat(x.getAgility()).isEqualTo(expectedResult.getAgility()),
                        x -> assertThat(x.getArmor()).isEqualTo(expectedResult.getArmor()),
                        x -> assertThat(x.getHitPoints()).isEqualTo(expectedResult.getHitPoints()),
                        x -> assertThat(x.getCurrentLocation()).isNull()
                );

    }

    public static Stream<Arguments> generateMonsterSourceForEmptyResults() {
        return Stream.of(
                Arguments.of(SHOP, VILLAGE, 0, Optional.empty()),
                Arguments.of(MONSTER, VILLAGE, 0, Optional.empty()),
                Arguments.of(MONSTER, VILLAGE, 1, Optional.empty())
        );
    }

    @ParameterizedTest
    @MethodSource("generateMonsterSourceForEmptyResults")
    void generateMonsterWillProduceEmptyOptional(LocationType locationType,
                                                 LocationBiome locationBiome,
                                                 int threatLvl) {
        Optional<Monster> generatedMonster = monsterGenerationService.generateMonster(locationType, locationBiome, threatLvl);

        assertThat(generatedMonster).isEmpty();
    }
}