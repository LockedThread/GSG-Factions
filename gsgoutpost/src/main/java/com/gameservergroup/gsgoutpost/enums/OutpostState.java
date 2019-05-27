package com.gameservergroup.gsgoutpost.enums;

public enum OutpostState {
    DISABLED, NEUTRALIZE_WAITING, NEUTRALIZING, NEUTRALIZING_PAUSED, NEUTRALIZED, CAPTURE_WAITING, CAPTURING, CAPTURING_PAUSED, CAPTURED;

    public String toString() {
        switch (this) {
            case NEUTRALIZE_WAITING:
            case CAPTURE_WAITING:
                return "Waiting";
            case NEUTRALIZING_PAUSED:
            case CAPTURING_PAUSED:
                return "Multiple Factions";
            case NEUTRALIZING:
                return "Neutralizing";
            case CAPTURING:
                return "Capturing";
            case NEUTRALIZED:
                return "Neutralized";
            case CAPTURED:
                return "Captured";
            case DISABLED:
                return "Disabled";
            default:
                return "N/A";
        }
    }
}
