package com.streamerbot.unused;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Minigame {

    BARBARIAN_ASSAULT("Barbarian Assault"),
    BARROWS("Barrows"),
    BLAST_FURNACE("Blast Furnace"),
    BRIMHAVEN_AGILITY_ARENA("Brimhaven Agility Arena"),
    BOUNTY_HUNTER("Bounty Hunter"),
    BURTHORPE_GAMES_ROOM("Burthorpe Games Room"),
    CASTLE_WARS("Castle Wars"),
    CLAN_WARS("Clan Wars"),
    PVP_ARENA("PvP Arena"),
    FISHING_TRAWLER("Fishing Trawler"),
    FORTIS_COLOSSEUM("Fortis Colosseum"),
    GAUNTLET("The Gauntlet"),
    CORRUPTED_GAUNTLET("Corrupted Gauntlet"),
    GIANTS_FOUNDRY("Giants' Foundry"),
    GUARDIANS_OF_THE_RIFT("Guardians of the Rift"),
    HALLOWED_SEPULCHRE("Hallowed Sepulchre"),
    INFERNO("The Inferno"),
    LAST_MAN_STANDING("Last Man Standing"),
    MAGE_TRAINING_ARENA("Mage Training Arena"),
    NIGHTMARE_ZONE("Nightmare Zone"),
    PEST_CONTROL("Pest Control"),
    PYRAMID_PLUNDER("Pyramid Plunder"),
    RAT_PITS("Rat Pits"),
    ROGUES_DEN("Rogues' Den"),
    SORCERESS_GARDEN("Sorceress's Garden"),
    SOUL_WARS("Soul Wars"),
    TEMPLE_TREKKING("Temple Trekking"),
    TITHE_FARM("Tithe Farm"),
    TROUBLE_BREWING("Trouble Brewing"),
    TZHAAR_FIGHT_CAVES("TzHaar Fight Caves"),
    TZHAAR_FIGHT_PITS("TzHaar Fight Pits"),
    TZHAAR_KET_RAK_CHALLENGES("TzHaar-Ket-Rak's Challenges"),
    VOLCANIC_MINE("Volcanic Mine");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
