package com.example.myfantasy.character.model.request;

import com.example.myfantasy.character.model.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class CreateCharacterRequest {
    private String name;
    private Type type;
}
