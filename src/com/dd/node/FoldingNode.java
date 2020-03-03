package com.dd.node;

import com.dd.SettingConfigurable;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class FoldingNode extends PsiFileNode {
    private String mShortName;
    private String mName;

    public FoldingNode(Project project, PsiFile value, ViewSettings viewSettings, String shortName) {
        super(project, value, viewSettings);
        mName = value.getName();
        mShortName = shortName;
    }

    @Override
    public String getName() {
        return mName;
    }

    public String getDisplayName() {
        return SettingConfigurable.isHidePrefix() ? mShortName : mName;
    }

    @Override
    protected void updateImpl(@NotNull PresentationData presentationData) {
        super.updateImpl(presentationData);
        presentationData.setPresentableText(getDisplayName());
    }
}
