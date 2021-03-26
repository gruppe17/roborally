package dk.dtu.compute.se.pisd.roborally.dal.repository;

public class DatabaseConstants {
    static final String GAME_GAMEID = "gameID";
    static final String GAME_NAME = "name";
    static final String GAME_CURRENTPLAYER = "currentPlayer";
    static final String GAME_PHASE = "phase";
    static final String GAME_STEP = "step";
    static final String PLAYER_PLAYERID = "playerID";
    static final String PLAYER_NAME = "name";
    static final String PLAYER_COLOUR = "colour";
    static final String PLAYER_GAMEID = "gameID";
    static final String PLAYER_POSITION_X = "positionX";
    static final String PLAYER_POSITION_Y = "positionY";
    static final String PLAYER_HEADING = "heading";
    static final String ACTIVATION_QUEUE_GAMEID = "gameID";
    static final String ACTIVATION_QUEUE_PLAYERID = "playerID";
    static final String ACTIVATION_QUEUE_PRIORITY = "priority";
    static final String CARD_ID = "cardID";
    static final String CARD_TYPE = "type";
    static final String CARD_POSITION = "position";
    static final String CARD_COMMAND = "command";

    //Should be enum?
    static final int CARD_TYPE_PROGRAM = 0;
    static final int CARD_TYPE_HAND = 1;
    static final int CARD_TYPE_DECK = 2;
    static final int CARD_TYPE_DISCARD = 3;
    static final int CARD_TYPE_UPGRADE = 4;
}