package com.gameservergroup.gsgoutpost.utils;

import com.gameservergroup.gsgoutpost.GSGOutpost;

public class PercentageUtil {

    // Sent by another developer. 
    // Credits to @Nathan.

    public static String getPercentage(int percentage) {
        return GSGOutpost.getInstance().getConfig().getBoolean("outpost.notifications.use-square-update") ? getPercentageSquare(percentage) : getPercentageLine(percentage);
    }

    private static String getPercentageSquare(int percentage) {
        if (percentage <= 0) return "&c&l██████████";
        if (percentage <= 10) return "&a&l█&c&l█████████";
        if (percentage <= 20) return "&a&l██&c&l████████";
        if (percentage <= 30) return "&a&l███&c&l███████";
        if (percentage <= 40) return "&a&l████&c&l██████";
        if (percentage <= 50) return "&a&l█████&c&l█████";
        if (percentage <= 60) return "&a&l██████&c&l████";
        if (percentage <= 70) return "&a&l███████&c&l███";
        if (percentage <= 80) return "&a&l████████&c&l██";
        if (percentage <= 90) return "&a&l█████████&c&l█";
        if (percentage <= 100) return "&a&l██████████";

        return "&7&l██████████";
    }

    private static String getPercentageLine(int percentage) {
        if (percentage <= 0) return "&c&l||||||||||";
        if (percentage <= 10) return "&a&l|&c&l|||||||||";
        if (percentage <= 20) return "&a&l||&c&l||||||||";
        if (percentage <= 30) return "&a&l|||&c&l|||||||";
        if (percentage <= 40) return "&a&l||||&c&l||||||";
        if (percentage <= 50) return "&a&l|||||&c&l|||||";
        if (percentage <= 60) return "&a&l||||||&c&l||||";
        if (percentage <= 70) return "&a&l|||||||&c&l|||";
        if (percentage <= 80) return "&a&l||||||||&c&l||";
        if (percentage <= 90) return "&a&l|||||||||&c&l|";
        if (percentage <= 100) return "&a&l||||||||||";

        return "&7&l||||||||||";
    }
}