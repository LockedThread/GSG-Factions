package com.gameservergroup.gsgcore.utils;

import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

public class SoundBuilder {

    private Triple<Sound, Float, Float> soundFloatFloatTriple;

    private SoundBuilder(Sound sound, float pitch, float volume) {
        this.soundFloatFloatTriple = Triple.of(sound, pitch, volume);
    }

    public static Sound getSound(String soundString) {
        soundString = soundString.toUpperCase().replace("-", "_").replace(" ", "_");
        try {
            return Sound.valueOf(soundString);
        } catch (IllegalArgumentException ignore) {
            for (Sound sound : Sound.values()) {
                if (sound.name().equalsIgnoreCase(soundString)) {
                    return sound;
                }
            }
        }
        return null;
    }

    public static SoundBuilder of(ConfigurationSection section) {
        Sound sound;
        float pitch = 1f, volume = 1f;
        if (section.isSet("sound")) {
            if ((sound = getSound(section.getString("sound"))) == null) {
                throw new RuntimeException("Unable to parse " + section.getString("sound") + " as a Sound");
            }
        } else {
            throw new RuntimeException("Unable to find sound in section \"" + section.toString() + "\"");
        }
        if (section.isSet("volume")) {
            volume = section.getFloat("volume");
        }
        if (section.isSet("pitch")) {
            pitch = section.getFloat("pitch");
        }
        return new SoundBuilder(sound, pitch, volume);
    }

    public Triple<Sound, Float, Float> build() {
        return soundFloatFloatTriple;
    }


}
