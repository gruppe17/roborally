/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * <p>This class represents a players view. I.e. something akin the player mat.
 * NOTICE: this is not the view for the robot.</p>
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 *
 */
public class PlayerView extends Tab implements ViewObserver {

    /**
     * The player which this is a view for.
     */
    private Player player;

    /**
     * <p>The top-level of this view. It contains
     * {@link #topTopHalf} and {@link #topBottomHalf}.</p>
     */
    private VBox top;

    /**
     * <p>The top-half of the {@link #top}. </p>
     */
    private GridPane topTopHalf;
    /**
     * <p>The root of the player-program view.</p>
     */
    private VBox programView;
    /**
     * <p>The label of the player-program.</p>
     */
    private Label programLabel;
    /**
     * <p>The programPane is where the player programs the robot.</p>
     */
    private GridPane programPane;

    /**
     * <p>The bottom-half of the {@link #top}. Contains {@link #playerHand}</p>
     */
    private Pane topBottomHalf;

    /**
     * <p>The view of the {@link #player}'s hand. Contains
     * {@link #playerHandLabel} and {@link #playerHandCardsPane}</p>
     */
    private VBox playerHand;

    /**
     * <p>The label of the {@link #player}'s hand.</p>
     */
    private Label playerHandLabel;

    /**
     * <p>Contains the {@link CardFieldView}s of the {@link #player}'s hand.</p>
     */
    private GridPane playerHandCardsPane;

    /**
     * <p>The views of each card currently in the {@link #player}'s registers.</p>
     */
    private CardFieldView[] programCardViews;

    /**
     * <p>The views of each card currently in the {@link #player}'s hand.</p>
     */
    private CardFieldView[] playerHandCardViews;

    /**
     * <p>Used to select and display possible {@link Command}s from the
     * a multi-command {@link Command}.</p>
     */
    private VBox playerInteractionPanel;


    private GameController gameController;


    //todo: move to PlayerView
    private VBox buttonPanel;
    private Button finishButton;
    private Button executeButton;
    private Button stepButton;


    public PlayerView(@NotNull GameController gameController, @NotNull Player player) {
        super(player.getName());
        this.setStyle("-fx-text-base-color: " + player.getColor() + ";");

        this.gameController = gameController;
        this.player = player;

        initTop();
        this.setContent(top);

        if (player.board != null) {
            player.board.attach(this);
            update(player.board);
        }
    }

    /**
     * <p>Initializes the {@link #top}. This includes creating its children.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initTop() {
        top = new VBox();
        //todo: determine how to determine size

        initTopTopHalf();
        RoboRally.bindSize(topTopHalf, top, 1, 0.5);
        top.getChildren().add(topTopHalf);

        initTopBottomHalf();
        RoboRally.bindSize(topBottomHalf, top, 1, 0.5);
        top.getChildren().add(topBottomHalf);
    }

    /**
     * <p>Initializes the {@link #topTopHalf}. This includes creating its children.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initTopTopHalf() {
        topTopHalf = new GridPane();
        double registerViewWidthPercentage = Player.NO_REGISTERS / (double) Player.NO_CARDS;
        initProgramView();
        //todo: the size of this should somehow be determined by the size of the cards in cardsPane
        RoboRally.bindSize(programView, topTopHalf, registerViewWidthPercentage, 1);
        topTopHalf.add(programView, 0, 0);

        //todo: make sure these are added to children correctly in updates
        initButtonPanel();
        RoboRally.bindSize(buttonPanel, topTopHalf, 0.2, 1);
        initPlayerInteractionPanel();
        RoboRally.bindSize(playerInteractionPanel, topTopHalf, 0.2, 1);
    }

    /**
     * <p>Initializes the {@link #topBottomHalf}. This includes creating its child.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initTopBottomHalf() {
        topBottomHalf = new Pane();

        initPlayerHand();
        RoboRally.bindSize(playerHand, topBottomHalf, 1, 1);
        topBottomHalf.getChildren().add(playerHand);
    }

    /**
     * <p>Initializes the {@link #programView}. This includes creating its children.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initProgramView() {
        //todo: maybe this and cardsPane should be a class of their own as they are very similar
        programView = new VBox();

        programLabel = new Label("Program");
        RoboRally.bindSize(programLabel, programView, 1, 0.01);
        programView.getChildren().add(programLabel);

        initProgramPane();
        RoboRally.bindSize(programPane, programView, 1, 0.99);
        programView.getChildren().add(programPane);
    }

    /**
     * <p>This method is responsible for initializing the {@link #programPane}.
     * This includes creating its the views for each non-null card in the
     * {@link #player}'s registers.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initProgramPane() {
        programPane = new GridPane();
        double hgapPercentageTimeNumCards = 0.01;

        programPane.setVgap(2.0);
        //programPane.setHgap(2.0);
        programPane.hgapProperty().bind(programPane.widthProperty().multiply(hgapPercentageTimeNumCards / Player.NO_REGISTERS));

        //double cardWidthPercent = (1d / Player.NO_REGISTERS) - (1d / (Player.NO_REGISTERS - 1) * hgapPercentage);
        double cardWidthPercent = (1d / Player.NO_REGISTERS) - (hgapPercentageTimeNumCards / (double) (Player.NO_REGISTERS - 1));
        programCardViews = new CardFieldView[Player.NO_REGISTERS];
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            CommandCardField cardField = player.getProgramField(i);
            if (cardField == null) continue;

            programCardViews[i] = new CardFieldView(gameController, cardField);
            programPane.add(programCardViews[i], i, 0);
            RoboRally.bindSize(programCardViews[i], programPane, cardWidthPercent, 1);
        }
    }

    /**
     * <p>Initializes the {@link #programView}. This includes creating its children.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initPlayerHand() {
        playerHand = new VBox();

        playerHandLabel = new Label("Command Cards");
        RoboRally.bindSize(playerHandLabel, playerHand, 1, 0.01); //todo: make these numbers constants
        playerHand.getChildren().add(playerHandLabel);

        initPlayerHandCardsPane();
        RoboRally.bindSize(playerHandCardsPane, playerHand, 1, 0.99);
        playerHand.getChildren().add(playerHandCardsPane);

    }

    /**
     * <p>This method is responsible for initializing the {@link #playerHandCardsPane}.
     * This includes drawing from the {@link #player}'s deck creating its the views for
     * each non-null card in the {@link #player}'s hand.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initPlayerHandCardsPane() {
        playerHandCardsPane = new GridPane();
        playerHandCardsPane.setVgap(2.0);
        double hgapPercentageTimeNumCards = 0.01;
        playerHandCardsPane.hgapProperty().bind(playerHandCardsPane.widthProperty().multiply(hgapPercentageTimeNumCards / Player.NO_CARDS));

        double cardWidthPercent = (1d / Player.NO_CARDS) - (hgapPercentageTimeNumCards / (double) (Player.NO_CARDS - 1));
        playerHandCardViews = new CardFieldView[Player.NO_CARDS];
        for (int i = 0; i < Player.NO_CARDS; i++) {
            CommandCardField cardField = player.getCardField(i);
            if (cardField != null) {
                playerHandCardViews[i] = new CardFieldView(gameController, cardField);
                playerHandCardsPane.add(playerHandCardViews[i], i, 0);
                RoboRally.bindSize(playerHandCardViews[i], playerHandCardsPane, cardWidthPercent, 1);
            }
        }
    }

    /**
     * <p>Initializes the {@link #buttonPanel}. This includes creating its children.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initButtonPanel() {
        finishButton = new Button("Finish Programming");
        finishButton.setOnAction(e -> gameController.finishProgrammingPhase());

        executeButton = new Button("Execute Program");
        executeButton.setOnAction(e -> gameController.executePrograms());

        stepButton = new Button("Execute Current Register");
        stepButton.setOnAction(e -> gameController.executeStep());

        buttonPanel = new VBox(finishButton, executeButton, stepButton);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        //todo: Do something dynamic here
        buttonPanel.setSpacing(3.0);
    }

    /**
     * <p>Initializes the {@link #playerInteractionPanel}.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initPlayerInteractionPanel() {
        playerInteractionPanel = new VBox();
        playerInteractionPanel.setAlignment(Pos.CENTER_LEFT);

        //todo: Do something dynamic here
        playerInteractionPanel.setSpacing(3.0);
    }


    @Override
    public void updateView(Subject subject) {
        if (subject != player.board) return;

        updateRegisters();

        if (player.board.getPhase() == Phase.PLAYER_INTERACTION) {
            updatePlayerInteractionPanel();
            return;
        }
        updateButtonPanel();


    }


    private void updateRegisters() {
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            CardFieldView cardFieldView = programCardViews[i];
            if (cardFieldView == null) continue;

            //The programming phase has just started; zero out all the registers.
            if (player.board.getPhase() == Phase.PROGRAMMING) {
                cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                continue;
            }

            //This card was executed last round
            if (i < player.board.getStep()) {
                cardFieldView.setBackground(CardFieldView.BG_DONE);
                continue;
            }

            if (i == player.board.getStep()) {
                //This card is about to be executed
                if (player.board.getCurrentPlayer() == player) {
                    cardFieldView.setBackground(CardFieldView.BG_ACTIVE);
                    continue;
                }

                //This card was executed earlier this round
                if (player.board.getPlayerNumber(player.board.getCurrentPlayer()) > player.board.getPlayerNumber(player)) {
                    cardFieldView.setBackground(CardFieldView.BG_DONE);
                    continue;
                }
            }
            //This card is to be executed in the future
            cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
        }
    }


    /**
     * <p>Updates the player interaction panel according to the {@link Command} of
     * the {@link CommandCardField} of the {@link #player}'s current register
     * and displays it. This is called by {@link #updateView(Subject)}</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void updatePlayerInteractionPanel() {
        if (player.board.getPhase() == Phase.PLAYER_INTERACTION
                && !topTopHalf.getChildren().contains(playerInteractionPanel)) {
            topTopHalf.getChildren().remove(buttonPanel);
            topTopHalf.add(playerInteractionPanel, Player.NO_REGISTERS, 0);
        }
        playerInteractionPanel.getChildren().clear();

        if (player.board.getCurrentPlayer() == player) {
            int registerNum = player.board.getStep();
            Command command = player.getProgramField(registerNum).getCard().command;
            if (!command.isInteractive()) assert false;
            Button optionBtn;
            for (Command option : command.getOptions()) {
                optionBtn = new Button(option.displayName);
                optionBtn.setOnAction(e -> gameController.executeCommandAndContinue(option));
                optionBtn.setDisable(false);
                playerInteractionPanel.getChildren().add(optionBtn);
            }
        }
    }

    /**
     * <p>Updates the button panel according to the current phase
     * and display the button panel. This is called by {@link #updateView(Subject)}</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void updateButtonPanel() {
        //If the button panel is already being displayed, remove the
        // player interaction panel and display this instead
        if (!topTopHalf.getChildren().contains(buttonPanel)) {
            topTopHalf.getChildren().remove(playerInteractionPanel);
            topTopHalf.add(buttonPanel, Player.NO_REGISTERS, 0);
        }
        switch (player.board.getPhase()) {
            case INITIALISATION:
                finishButton.setDisable(true);
                // XXX just to make sure that there is a way for the player to get
                //     from the initialization phase to the programming phase somehow!
                executeButton.setDisable(false);
                stepButton.setDisable(true);
                break;

            case PROGRAMMING:
                finishButton.setDisable(false);
                executeButton.setDisable(true);
                stepButton.setDisable(true);
                break;

            case ACTIVATION:
                finishButton.setDisable(true);
                executeButton.setDisable(false);
                stepButton.setDisable(false);
                break;

            default:
                finishButton.setDisable(true);
                executeButton.setDisable(true);
                stepButton.setDisable(true);
        }
    }

}
