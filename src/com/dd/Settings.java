package com.dd;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;

public class Settings {
    @SerializedName("DecomposedFolders")
    public Set<String> composedFolders = new HashSet<>();
}
