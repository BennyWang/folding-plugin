package com.dd.node;

import com.google.common.base.CaseFormat;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GroupNode extends ProjectViewNode<GroupNavigation> {
    private final String mName;
    private final String mDisplayName;
    private Map<String, AbstractTreeNode> mChildNodeList = new LinkedHashMap<>();

    public GroupNode(Project project, ViewSettings viewSettings, GroupNavigation value, String name) {
        super(project, value, viewSettings);
        mName = name;
        // if name contains _, we display it as upper camel style, for example story_board -> StoryBoard
        // else just display it
        mDisplayName = !name.contains("_")
                ? name
                : CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
    }

    @Override
    public String getName() {
        return mName;
    }

    public String getDisplayName() {
        GroupNode childNode = shouldCompactMiddleDirectories();
        return childNode == null ? mDisplayName : mDisplayName + "." + childNode.getDisplayName();
    }

    public GroupNode getGroupChild(String name) {
        return (GroupNode) mChildNodeList.get(name);
    }

    public void addChild(GroupNode node) {
        mChildNodeList.put(node.getName(), node);
    }

    public void addChild(FoldingNode node) {
        mChildNodeList.put(node.getName(), node);
    }

    private GroupNode shouldCompactMiddleDirectories() {
        if (mChildNodeList.size() == 1) {
            AbstractTreeNode node = mChildNodeList.values().iterator().next();
            if (node instanceof GroupNode) {
                return (GroupNode) node;
            }
        }
        return null;
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        for (final AbstractTreeNode childNode : mChildNodeList.values()) {
            ProjectViewNode treeNode = (ProjectViewNode) childNode;
            if (treeNode.contains(file)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        GroupNode childNode = shouldCompactMiddleDirectories();
        return (childNode != null) ? childNode.getChildren() : mChildNodeList.values();
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getDisplayName());
        presentation.setIcon(AllIcons.Nodes.Folder);
    }
}
