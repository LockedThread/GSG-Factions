package com.massivecraft.factions.zcore.factionmissions;

import com.massivecraft.factions.zcore.factionmissions.enums.MissionTaskType;

import java.util.Map;
import java.util.Objects;

public class MissionTask {

    private final MissionTaskType missionTaskType;
    private final Map<String, Object> metadata;
    private int hash;

    public MissionTask(MissionTaskType missionTaskType, Map<String, Object> metadata) {
        this.missionTaskType = missionTaskType;
        this.metadata = metadata;
        this.hash = missionTaskType != null ? missionTaskType.hashCode() : 0;
        this.hash = 31 * hash + (metadata != null ? metadata.hashCode() : 0);
    }

    public void setMeta(String key, Object value) {
        metadata.put(key, value);
    }

    public String getMetaString(String key) {
        return String.valueOf(metadata.get(key));
    }

    public int getMetaInteger(String key) {
        return (int) metadata.get(key);
    }

    public double getMetaDouble(String key) {
        return (double) metadata.get(key);
    }

    public boolean getMetaBoolean(String key) {
        return (boolean) metadata.get(key);
    }

    public boolean isMetaBoolean(String key) {
        return metadata.get(key) instanceof Boolean;
    }

    public boolean isMetaInteger(String key) {
        return metadata.get(key) instanceof Integer;
    }

    public boolean isMetaDouble(String key) {
        return metadata.get(key) instanceof Double;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public MissionTaskType getMissionTaskType() {
        return missionTaskType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MissionTask that = (MissionTask) o;

        return missionTaskType == that.missionTaskType && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
