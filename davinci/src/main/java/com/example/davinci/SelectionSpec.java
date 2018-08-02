package com.example.davinci;

public final class SelectionSpec {
    public int maxSelectable;

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
