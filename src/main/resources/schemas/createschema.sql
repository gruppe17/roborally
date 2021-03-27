/* Need to switch of FK check for MySQL since there are crosswise FK references */
SET FOREIGN_KEY_CHECKS = 0;;

CREATE TABLE IF NOT EXISTS Game (
    gameID int NOT NULL UNIQUE AUTO_INCREMENT,
  
    name varchar(255),

    phase tinyint,
    step tinyint,
    currentPlayer tinyint NULL,
  
    PRIMARY KEY (gameID),
    FOREIGN KEY (gameID, currentPlayer) REFERENCES Player(gameID, playerID)
);;
  
CREATE TABLE IF NOT EXISTS Player (
    gameID int NOT NULL,
    playerID tinyint NOT NULL,

    name varchar(255),
    colour varchar(31),
  
    positionX int,
    positionY int,
    heading tinyint,
  
    PRIMARY KEY (gameID, playerID),
    FOREIGN KEY (gameID) REFERENCES Game(gameID) ON DELETE CASCADE
);;

CREATE TABLE IF NOT EXISTS ActivationQueue (
    gameID int NOT NULL,
    playerID tinyint NOT NULL,
    priority tinyint NOT NULL,

    PRIMARY KEY (gameID, priority),
    FOREIGN KEY (gameID) REFERENCES Game(gameID) ON DELETE CASCADE,
    FOREIGN KEY (gameID, playerID) REFERENCES Player(gameID, playerID) ON DELETE CASCADE
);;

CREATE TABLE IF NOT EXISTS Card (
    cardID int NOT NULL UNIQUE,
    gameID int NOT NULL,
    playerID tinyint NOT NULL,
    type tinyint NOT NULL,
    position tinyint NOT NULL,

    PRIMARY KEY (cardID),
    FOREIGN KEY (gameID) REFERENCES Game(gameID) ON DELETE CASCADE,
    FOREIGN KEY (gameID, playerID) REFERENCES Player(gameID, playerID) ON DELETE CASCADE
);;

CREATE TABLE IF NOT EXISTS CardCommand(
    cardID int NOT NULL,
    command tinyint NOT NULL,

    PRIMARY KEY (cardID, command),
    FOREIGN KEY (cardID) REFERENCES Card(cardID) ON DELETE CASCADE
);;

SET FOREIGN_KEY_CHECKS = 1;;

/* TODO still some stuff missing here */