package com.dd;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

/**
 *
 * Created by skyrylyuk on 10/15/15.
 */
public class SettingConfigurable implements Configurable {
    public static final String PREFIX_HIDE = "folding_plugin_prefix_hide";
    public static final String APPLY_TO_PATH = "folding_plugin_apply_to_path";
    public static final String MAX_FOLDING_DEPTH = "folding_plugin_max_folding_depth";
    public static final String FOLDING_DELIMITER = "folding_plugin_max_folding_delimiter";
    public static final String DEFAULT_APPLY_TO_PATH = ".+/src/main/res/.+";

    private static Pattern composablePattern;

    private JPanel mPanel;
    private JCheckBox vHideFoldingPrefix;
    private JTextField vApplyToPath;
    private JTextField vMaxFoldingDepth;
    private JTextField vFoldingDelimiter;
    private boolean isModified = false;

    public static boolean isPathComposable(String path) {
        String applyToPath = getApplyToPath();
        if (applyToPath.isEmpty()) {
            // empty means all path composable
            return true;
        }

        if (composablePattern == null) {
            composablePattern = Pattern.compile(applyToPath);
        }
        return composablePattern.matcher(path).matches();
    }

    public static boolean isHidePrefix() {
        return PropertiesComponent.getInstance().getBoolean(PREFIX_HIDE, true);
    }

    public static String getApplyToPath() {
        return PropertiesComponent.getInstance().getValue(APPLY_TO_PATH, DEFAULT_APPLY_TO_PATH);
    }

    public static int getMaxFoldingDepth() {
        return PropertiesComponent.getInstance().getInt(MAX_FOLDING_DEPTH, 2);
    }

    public static String getFoldingDelimiter() {
        return PropertiesComponent.getInstance().getValue(FOLDING_DELIMITER, "_");
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Android Folding";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "null:";
    }

    @Override
    public boolean isModified() {
        return isModified;
    }

    @Override
    public void disposeUIResources() {
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isModified = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isModified = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                isModified = true;
            }
        };

        vApplyToPath.getDocument().addDocumentListener(documentListener);
        vMaxFoldingDepth.getDocument().addDocumentListener(documentListener);
        vFoldingDelimiter.getDocument().addDocumentListener(documentListener);
        vHideFoldingPrefix.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                isModified = true;
            }
        });

        reset();
        return mPanel;
    }

    @Override
    public void apply() {
        PropertiesComponent.getInstance().setValue(APPLY_TO_PATH, vApplyToPath.getText());
        PropertiesComponent.getInstance().setValue(PREFIX_HIDE, Boolean.valueOf(vHideFoldingPrefix.isSelected()).toString());
        PropertiesComponent.getInstance().setValue(MAX_FOLDING_DEPTH, Integer.parseInt(vMaxFoldingDepth.getText()), 2);
        PropertiesComponent.getInstance().setValue(FOLDING_DELIMITER, vFoldingDelimiter.getText());

        if (isModified) {
            // update pattern
            composablePattern = Pattern.compile(getApplyToPath());

            Project currentProject = Utils.getCurrentProject();
            if (currentProject != null) {
                ProjectView.getInstance(currentProject).refresh();
            }
        }
        isModified = false;
    }

    @Override
    public void reset() {
        vApplyToPath.setText(getApplyToPath());
        vHideFoldingPrefix.getModel().setSelected(isHidePrefix());
        vMaxFoldingDepth.setText(String.valueOf(getMaxFoldingDepth()));
        vFoldingDelimiter.setText(getFoldingDelimiter());
    }
}
