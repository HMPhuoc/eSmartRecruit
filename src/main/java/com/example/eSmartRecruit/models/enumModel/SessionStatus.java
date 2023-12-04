package com.example.eSmartRecruit.models.enumModel;

public enum SessionStatus {
    NotOnSchedule,
    Yet,
    Already;
    public boolean isNotOnSchedule() {
        return this == NotOnSchedule;
    }

    public boolean isYet() {
        return this == Yet;
    }

    public boolean isAlready() {
        return this == Already;
    }
}
