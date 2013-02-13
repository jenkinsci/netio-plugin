package com.tngtech.internal.plug;

public enum Plug {
    PLUG1(1),
    PLUG2(2),
    PLUG3(3),
    PLUG4(4);

    private final int plugNumber;

    private Plug(int plugNumber) {
        this.plugNumber = plugNumber;
    }

    public Integer getPlugNumber() {
        return plugNumber;
    }
}