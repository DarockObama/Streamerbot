package com.streamerbot.domain;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DeathType {
    DANGEROUS("Dangerous"),
    SAFE_FOR_SOLO_HCIM("Safe for Solo HCIM"),
    SAFE_FOR_HCGIM("Safe for HCGIM"),
    RESURRECTION("Resurrection");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
