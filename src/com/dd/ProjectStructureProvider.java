package com.dd;

import com.dd.node.GroupNode;
import com.dd.node.FoldingNode;
import com.dd.node.GroupNavigation;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ProjectStructureProvider implements com.intellij.ide.projectView.TreeStructureProvider {
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent, @NotNull Collection<AbstractTreeNode> children, ViewSettings viewSettings) {
        if (parent.getValue() instanceof PsiDirectory) {
            PsiDirectory directory = (PsiDirectory) parent.getValue();
            String path = directory.getVirtualFile().getPath();
            if (SettingsManager.isComposed(path)) {
                return createComposedFiles(path, children, viewSettings);
            }
        }
        return children;
    }

    private Collection<AbstractTreeNode> createComposedFiles(String directory, @NotNull Collection<AbstractTreeNode> fileNodes, ViewSettings viewSettings) {
        LinkedHashMap<String, AbstractTreeNode> result = new LinkedHashMap<>();
        Project project = Utils.getCurrentProject();
        if (project != null) {
            String delimiter = SettingConfigurable.getFoldingDelimiter();
            int maxFoldingDepth = SettingConfigurable.getMaxFoldingDepth();
            for (AbstractTreeNode node : fileNodes) {
                Object nodeValue = node.getValue();
                if (nodeValue instanceof PsiDirectory) {
                    result.put(((PsiDirectory) nodeValue).getVirtualFile().getName(), node);
                }
                else if (nodeValue instanceof PsiFile) {
                    PsiFile psiFile = (PsiFile) nodeValue;
                    String fileName = psiFile.getName();
                    String[] nameArr = splitFileName(fileName, delimiter, maxFoldingDepth);
                    if (nameArr.length == 1) {
                        result.put(fileName, node);
                    }
                    else {
                        // create root node
                        GroupNode directoryNode = (GroupNode) result.get(nameArr[0]);
                        if (directoryNode == null) {
                            directoryNode = new GroupNode(project, viewSettings, new GroupNavigation(directory, nameArr[0]), nameArr[0]);
                            result.put(nameArr[0], directoryNode);
                        }

                        // create subgroup node
                        for (int i = 1; i < nameArr.length - 1; ++i) {
                            GroupNode childNode = directoryNode.getGroupChild(nameArr[i]);
                            if (childNode == null) {
                                childNode = new GroupNode(project, viewSettings, new GroupNavigation(directory, nameArr[i], directoryNode.getValue()), nameArr[i]);
                                directoryNode.addChild(childNode);
                            }
                            directoryNode = childNode;
                        }
                        // create file node
                        directoryNode.addChild(new FoldingNode(project, psiFile, viewSettings, nameArr[nameArr.length - 1]));
                    }
                }
            }
        }
        return result.values();
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> collection, String s) {
        return null;
    }

    private String[] removeEmptyStrings(String[] arr) {
        List<String> result = new ArrayList<>();
        for (String it : arr) {
            if (!it.isEmpty()) {
                result.add(it);
            }
        }
        return result.toArray(new String[0]);
    }

    private String[] splitFileName(String fileName, String splitter, int maxSplitCount) {
        String[] fileNameArr = removeEmptyStrings(fileName.split(splitter));

        if (fileNameArr.length <= maxSplitCount) {
            return fileNameArr;
        }

        String[] result = new String[maxSplitCount];
        int restIndex = 0;
        for (int i = 0; i < maxSplitCount - 1; ++i) {
            result[i] = fileNameArr[i];
            restIndex += fileNameArr[i].length() + splitter.length();
        }
        result[maxSplitCount - 1] = fileName.substring(restIndex);
        return result;
    }
}
