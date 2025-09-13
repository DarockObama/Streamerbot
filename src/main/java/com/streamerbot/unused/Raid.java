package com.streamerbot.unused;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Raid {
    COX("Chambers of Xeric"),
    TOB("Theatre of Blood"),
    TOA("Tombs of Amascut");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
