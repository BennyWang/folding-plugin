package com.dd.node;

import com.dd.SettingConfigurable;

public class GroupNavigation {
    private GroupNavigation parent;
    String directory;
    private String prefix;

    public GroupNavigation(String directory, String prefix) {
        this(directory, prefix, null);
    }

    public GroupNavigation(String directory, String prefix, GroupNavigation parent) {
        this.directory = directory;
        this.prefix = prefix;
        this.parent = parent;
    }

    public String getPrefix() {
        if (parent == null) {
            return prefix;
        }
        return parent.getPrefix() + SettingConfigurable.getFoldingDelimiter() + prefix;
    }
}
