package com.streamerbot.unused;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SpecialEncounter {
    COX("Chambers of Xeric"),
    TOB("Theatre of Blood"),
    TOA("Tombs of Amascut"),
    COLOSSEUM("Fortis Colosseum"),
    FIGHT_CAVES("Fight Caves"),
    INFERNO("The Inferno"),
    JAD_CHALLENGES("Jad Challenges"),
    GAUNTLET("The Gauntlet"),
    CG("Corrupted Gauntlet");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
