package com.castler.castler;

/**
 * Created by Paul on 6/19/2015.
 */
public class ChessSession {
    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionCode) {
        this.sessionID = sessionCode;
    }

    String clubName = "";
    String sessionID = "";
}
