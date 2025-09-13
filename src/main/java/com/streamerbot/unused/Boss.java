package com.streamerbot.unused;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Boss {
    ABYSSAL_SIRE("Abyssal Sire"),
    ARAXXOR("Araxxor"),
    CERBERUS("Cerberus"),
    COMMANDER_ZILYANA("Commander Zilyana"),
    CORP("Corporeal Beast"),
    DKS("Dagannoth Kings"),
    DUKE_SUCELLUS("Duke Sucellus"),
    FRAGMENT_OF_SEREN("Fragment of Seren"),
    GALVEK("Galvek"),
    GENERAL_GRAARDOR("General Graardor"),
    GIANT_MOLE("Giant Mole"),
    GROTESQUE_GUARDIANS("Grotesque Guardians"),
    HESPORI("Hespori"),
    HYDRA("Alchemical Hydra"),
    KALPHITE_QUEEN("Kalphite Queen"),
    KRAKEN("Kraken"),
    KREEARRA("Kree'arra"),
    KRIL_TSUTSAROTH("K'ril Tsutsaroth"),
    NEX("Nex"),
    NIGHTMARE("Nightmare of Ashihama"),
    PHANTOM_MUSPAH("Phantom Muspah"),
    SARACHNIS("Sarachnis"),
    SKOTIZO("Skotizo"),
    SMOKE_DEVIL("Thermonuclear smoke devil"),
    TEMPOROSS("Tempoross"),
    THE_LEVIATHAN("The Leviathan"),
    THE_ROYAL_TITANS("The Royal Titans"),
    THE_WHISPERER("The Whisperer"),
    VARDORVIS("Vardorvis"),
    VORKATH("Vorkath"),
    WINTERTODT("Wintertodt"),
    BOSS_YAMA("Yama"),
    BOSS_ZALCANO("Zalcano"),
    BOSS_ZULRAH("Zulrah");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
