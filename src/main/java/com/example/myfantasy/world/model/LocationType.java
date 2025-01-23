package com.example.myfantasy.world.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public enum LocationType {
    MONSTER(8),
    SHOP(1),
    EMPTY(1);

    private final int chance;
    private static final List<LocationType> lookupWeight;
    private static final int totalWeight;

    LocationType(int chance) {
        this.chance = chance;
    }

    static {
        lookupWeight = Arrays.stream(LocationType.values())
                .map(locationType -> IntStream.range(0, locationType.chance).mapToObj(x -> locationType).toList())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        totalWeight = lookupWeight.size();
    }

    public static LocationType getRandomWeightedLocationType(){
        return lookupWeight.get(new Random().nextInt(totalWeight));
    }

}