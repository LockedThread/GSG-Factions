package com.gameservergroup.gsgskyblock.enums;

public enum IslandRole {

    LEADER(2),
    OFFICER(1),
    MEMBER(0);

    private int ranking;

    IslandRole(int ranking) {
        this.ranking = ranking;
    }

    public int getRanking() {
        return ranking;
    }
}
