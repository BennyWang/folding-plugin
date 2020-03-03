package com.dd.action;

import com.dd.node.GroupNavigation;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class NewFoldingFileAction extends AnAction {
    private static final String DESC = "Resource File";

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        GroupNavigation nav = getTarget(actionEvent, GroupNavigation.class);
        if (nav != null) {

        }
    }

    private <T> T getTarget(@NotNull AnActionEvent e, @NotNull Class<T> clazz) {
        Object[] items = (Object[])e.getData(PlatformDataKeys.SELECTED_ITEMS);
        return items != null && items.length == 1 ? ObjectUtils.tryCast(items[0], clazz) : null;
    }

    @Override
    public void update(AnActionEvent actionEvent) {
        boolean enabledAndVisible = false;
        Project project = actionEvent.getData(CommonDataKeys.PROJECT);
        if (project != null) {
            GroupNavigation nav = getTarget(actionEvent, GroupNavigation.class);
            if (nav != null) {
                actionEvent.getPresentation().setText(DESC);
                actionEvent.getPresentation().setIcon(AllIcons.Nodes.ResourceBundle);
                enabledAndVisible = true;
            }
        }
        actionEvent.getPresentation().setEnabledAndVisible(enabledAndVisible);
    }
}
