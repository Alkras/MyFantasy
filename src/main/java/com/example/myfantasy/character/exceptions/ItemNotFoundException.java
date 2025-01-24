package com.example.myfantasy.character.exceptions;

import com.example.myfantasy.configuration.InputException;

public class ItemNotFoundException extends InputException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
