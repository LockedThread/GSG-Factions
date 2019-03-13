package com.massivecraft.factions.struct;

public class StrikeInfo {

    private final long issuedAt;
    private final String issuerName;
    private final String tagAtTheTime;
    private String description;

    public StrikeInfo(long issuedAt, String issuerName, String tagAtTheTime) {
        this.issuedAt = issuedAt;
        this.issuerName = issuerName;
        this.tagAtTheTime = tagAtTheTime;
    }

    public StrikeInfo(long issuedAt, String issuerName, String tagAtTheTime, String description) {
        this.issuedAt = issuedAt;
        this.issuerName = issuerName;
        this.tagAtTheTime = tagAtTheTime;
        this.description = description;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getTagAtTheTime() {
        return tagAtTheTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "StrikeInfo{" +
                "issuedAt=" + issuedAt +
                ", issuerName='" + issuerName + '\'' +
                ", tagAtTheTime='" + tagAtTheTime + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
