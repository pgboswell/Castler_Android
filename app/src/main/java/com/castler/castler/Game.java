package com.castler.castler;

/**
 * Created by Paul on 6/22/2015.
 */
public class Game {

    private Player whitePlayer = null;
    private Player blackPlayer = null;
    private int result = 0;

    public static final int GAME_RESULT_WHITE_WIN = 0;
    public static final int GAME_RESULT_BLACK_WIN = 1;
    public static final int GAME_RESULT_DRAW = 2;

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
