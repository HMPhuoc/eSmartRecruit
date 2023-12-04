package com.example.eSmartRecruit.models.enumModel;

public enum ApplicationStatus {
    Pending,
    Approved,
    Declined;

    // Thêm các phương thức kiểm tra trạng thái
    public boolean isPending() {
        return this == Pending;
    }

    public boolean isApproved() {
        return this == Approved;
    }

    public boolean isDeclined() {
        return this == Declined;
    }
}
