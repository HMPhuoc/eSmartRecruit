package com.example.eSmartRecruit.models.enumModel;

public enum SessionResult {
    NotYet,
    Good,
    Bad;
    public boolean isNotYet() {
        return this == NotYet;
    }

    public boolean isGood() {
        return this == Good;
    }

    public boolean isBad() {
        return this == Bad;
    }
}
