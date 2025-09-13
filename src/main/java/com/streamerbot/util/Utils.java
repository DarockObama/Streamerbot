package com.streamerbot.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.Text;
import org.jetbrains.annotations.NotNull;

@Slf4j
@UtilityClass
public class Utils {
    public String sanitize(String str) {
        if (str == null || str.isEmpty()) return "";
        return Text.removeTags(str.replace("<br>", "\n")).replace('\u00A0', ' ').trim();
    }
    /**
     * Converts text into "upper case first" form, as is used by OSRS for item names.
     *
     * @param text the string to be transformed
     * @return the text with only the first character capitalized
     */
    public String ucFirst(@NotNull String text) {
        if (text.length() < 2) return text.toUpperCase();
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }
}
