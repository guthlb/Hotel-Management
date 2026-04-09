package com.hotel.util;

import javafx.scene.Scene;

public class ThemeManager {

    private static boolean darkMode = false;

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void toggleTheme() {
        darkMode = !darkMode;
    }

    public static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();

        if (darkMode) {
            scene.getStylesheets().add(ThemeManager.class.getResource("/styles/dark.css").toExternalForm());
        } else {
            scene.getStylesheets().add(ThemeManager.class.getResource("/styles/light.css").toExternalForm());
        }
    }
}
