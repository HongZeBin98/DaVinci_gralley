package com.example.davinci;

import com.example.davinci.engine.ImageEngine;

public final class SelectionSpec {
    public int maxSelectable;
    public ImageEngine imageEngine;

    private SelectionSpec() {
    }

    public static SelectionSpec getInstance() {
        return SelectionSpec.InstanceHolder.INSTANCE;
    }

    private static final class InstanceHolder {
        private static final SelectionSpec INSTANCE = new SelectionSpec();

        private InstanceHolder() {
        }
    }

}
