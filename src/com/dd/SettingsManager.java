package com.dd;

import com.google.gson.Gson;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SettingsManager {
    private static final String KEY_SETTINGS = "KEY_COMPOSED_FOLDERS";
    private static Settings gSettings;

    @NotNull
    private static Settings getSettings(@NotNull Project project) {
        if (gSettings == null) {
            String data = PropertiesComponent.getInstance(project).getValue(KEY_SETTINGS);
            gSettings = (data == null) ? new Settings() : new Gson().fromJson(data, Settings.class);
        }
        return gSettings;
    }

    private static void saveSettings(@NotNull Project project, Settings settings) {
        PropertiesComponent.getInstance(project).setValue(KEY_SETTINGS, new Gson().toJson(settings));
    }

    public static boolean isComposed(@NotNull String folder) {
        Project currentProject = Utils.getCurrentProject();
        if (currentProject != null) {
            Settings settings = getSettings(currentProject);
            return settings.composedFolders.contains(folder);
        }
        return false;
    }

    public static void toggleComposedFolder(@NotNull String folder) {
        if (isComposed(folder)) {
            removeComposedFolder(folder);
        }
        else {
            addComposedFolder(folder);
        }
    }

    public static void addComposedFolder(@NotNull String folder) {
        Project currentProject = Utils.getCurrentProject();
        if (currentProject != null) {
            Settings settings = getSettings(currentProject);
            settings.composedFolders.add(folder);
            saveSettings(currentProject, settings);
        }
    }

    public static void removeComposedFolder(@NotNull String folder) {
        Project currentProject = Utils.getCurrentProject();
        if (currentProject != null) {
            Settings settings = getSettings(currentProject);
            settings.composedFolders.remove(folder);
            saveSettings(currentProject, settings);
        }
    }
}
