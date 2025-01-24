package com.example.myfantasy.character.exceptions;

import com.example.myfantasy.configuration.InputException;

public class CharacterNotFoundException extends InputException {
    public CharacterNotFoundException(String message) {
        super(message);
    }
}
