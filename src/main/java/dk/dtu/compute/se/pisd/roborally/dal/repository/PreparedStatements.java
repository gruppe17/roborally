package dk.dtu.compute.se.pisd.roborally.dal.repository;

import java.sql.*;

public class PreparedStatements {
    private Connection connection = Connector.getInstance().getConnection();

    /**
     * <p>The prepared statement for inserting a
     * commands associated with a given card. Setting
     * parameter 1 will set the cardID</p>
     *
     * @see #getSelectCardCommandStatement()
     */
    PreparedStatement insertCardCommandStatement = null;
    /**
     * <p>The prepared statement for getting the
     * commands associated with a given card. Setting
     * parameter 1 will set the cardID</p>
     *
     * @see #getSelectCardCommandStatement()
     */
    PreparedStatement selectCardCommandStatement = null;
    /**
     * <p>The prepared statement for getting and updating the
     * activation queue associated with a given gameID. Setting
     * parameter 1, will set the gameID.</p>
     *
     * @see #getSelectActivationQueueStatementUpdatable()
     */
    PreparedStatement selectActivationQueueStatement = null;
    PreparedStatement insert_game_stmt = null;
    PreparedStatement select_game_stmt = null;
    PreparedStatement select_players_stmt = null;
    PreparedStatement select_players_asc_stmt = null;
    PreparedStatement select_games_stmt = null;

    /**
     * <p>The prepared statement for getting and updating the
     * cards associated with a given gameID and playerID. Setting
     * parameter 1 will set the gameID and setting 2 will set
     * playerID.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see #getSelectCardStatementUpdatable()
     */
    private PreparedStatement selectCardStatement = null;


    /**
     * @return
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    PreparedStatement getSelectCardStatementUpdatable() {
        if (selectCardStatement != null) return selectCardStatement;
        try {
            selectCardStatement = connection.prepareStatement(DatabaseConstants.SQL_SELECT_CARD_STATEMENT,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectActivationQueueStatement;
    }

    /**
     * <p>Initializes, if not already initialized, and returns the
     * {@link #selectActivationQueueStatement}. Executing the
     * statement will return an updatable {@link ResultSet}.</p>
     *
     * @return the prepared statement for getting and updating
     * the player activation associated with a given gameID
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see DatabaseConstants#SQL_SELECT_ACTIVATION_QUEUE
     * @see #selectActivationQueueStatement
     */
    PreparedStatement getInsertCardCommandStatement() {
        if (insertCardCommandStatement != null) return insertCardCommandStatement;
        try {
            insertCardCommandStatement = connection.prepareStatement(DatabaseConstants.SQL_INSERT_CARD_COMMAND_STATEMENT,
                    ResultSet.TYPE_FORWARD_ONLY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertCardCommandStatement;
    }

    /**
     * @return
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    PreparedStatement getSelectCardCommandStatement() {
        if (selectCardCommandStatement != null) return selectCardCommandStatement;
        try {
            selectCardCommandStatement = connection.prepareStatement(DatabaseConstants.SQL_SELECT_CARD_COMMAND_STATEMENT,
                    ResultSet.TYPE_FORWARD_ONLY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectCardCommandStatement;
    }

    /**
     * <p>Initializes, if not already initialized, and returns the
     * {@link #selectActivationQueueStatement}. Executing the
     * statement will return an updatable {@link ResultSet}.</p>
     *
     * @return the prepared statement for getting and updating
     * the player activation associated with a given gameID
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see DatabaseConstants#SQL_SELECT_ACTIVATION_QUEUE
     * @see #selectActivationQueueStatement
     */
    PreparedStatement getSelectActivationQueueStatementUpdatable() {
        if (selectActivationQueueStatement != null) return selectActivationQueueStatement;
        try {
            selectActivationQueueStatement = connection.prepareStatement(DatabaseConstants.SQL_SELECT_ACTIVATION_QUEUE,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectActivationQueueStatement;
    }

    PreparedStatement getInsertGameStatementRGK() {
        if (insert_game_stmt != null) return insert_game_stmt;
        try {
            insert_game_stmt = connection.prepareStatement(
                    DatabaseConstants.SQL_INSERT_GAME,
                    Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            // TODO error handling
            e.printStackTrace();
        }
        return insert_game_stmt;
    }

    PreparedStatement getSelectGameStatementU() {
        if (select_game_stmt != null) return select_game_stmt;
        try {
            select_game_stmt = connection.prepareStatement(
                    DatabaseConstants.SQL_SELECT_GAME,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            // TODO error handling
            e.printStackTrace();
        }
        return select_game_stmt;
    }

    PreparedStatement getSelectPlayersStatementU() {
        if (select_players_stmt != null) return select_players_stmt;
        try {
            select_players_stmt = connection.prepareStatement(
                    DatabaseConstants.SQL_SELECT_PLAYERS,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            // TODO error handling
            e.printStackTrace();
        }
        return select_players_stmt;
    }

    PreparedStatement getSelectPlayersASCStatement() {
        if (select_players_asc_stmt != null) return select_players_asc_stmt;
        try {
            // This statement does not need to be updatable
            select_players_asc_stmt = connection.prepareStatement(
                    DatabaseConstants.SQL_SELECT_PLAYERS_ASC);
        } catch (SQLException e) {
            // TODO error handling
            e.printStackTrace();
        }
        return select_players_asc_stmt;
    }

    PreparedStatement getSelectGameIdsStatement() {
        if (select_games_stmt != null) return select_games_stmt;
        try {
            select_games_stmt = connection.prepareStatement(
                    DatabaseConstants.SQL_SELECT_GAMES);
        } catch (SQLException e) {
            // TODO error handling
            e.printStackTrace();
        }

        return select_games_stmt;
    }
}