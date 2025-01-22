package com.example.myfantasy.world.model;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class LocationKey implements Serializable {

    private Long x;

    private Long y;
}
