package com.streamerbot.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum IgnoreSafeDeathPolicy {
    NONE("None"),
    SAFE_FOR_HCGIM("Safe for HCGIM"),
    SAFE_FOR_SOLO_HCIM("Safe for Solo HCIM");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
