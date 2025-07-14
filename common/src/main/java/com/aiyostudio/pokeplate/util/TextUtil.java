package com.aiyostudio.pokeplate.util;

import de.tr7zw.nbtapi.utils.MinecraftVersion;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Blank038
 */
public class TextUtil {
    private static final Pattern PATTERN = Pattern.compile("#[A-f0-9]{6}");

    public static String colorify(String message) {
        String copy = message;
        if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_16_R1)) {
            Matcher matcher = PATTERN.matcher(copy);
            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                copy = copy.replace(color, String.valueOf(net.md_5.bungee.api.ChatColor.of(color)));
            }
        }
        return ChatColor.translateAlternateColorCodes('&', copy);
    }
}