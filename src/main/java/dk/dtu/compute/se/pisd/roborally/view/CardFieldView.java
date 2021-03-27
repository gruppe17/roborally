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
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class CardFieldView extends GridPane implements ViewObserver {

	// This data format helps avoiding transfers of e.g. Strings from other
	// programs which can copy/paste Strings.
	final public static DataFormat ROBO_RALLY_CARD = new DataFormat("games/roborally/cards");

	final public static Border BORDER = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2)));

	final public static Background BG_DEFAULT = new Background(new BackgroundFill(Color.WHITE, null, null));
	final public static Background BG_DRAG = new Background(new BackgroundFill(Color.GRAY, null, null));
	final public static Background BG_DROP = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));

	final public static Background BG_ACTIVE = new Background(new BackgroundFill(Color.YELLOW, null, null));
	final public static Background BG_DONE = new Background(new BackgroundFill(Color.GREENYELLOW, null, null));

	private CommandCardField field;

	private Label label;

	private GameController gameController;

	public CardFieldView(@NotNull GameController gameController, @NotNull CommandCardField field) {
		this.gameController = gameController;
		this.field = field;

		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(5, 5, 5, 5));

		this.setBorder(BORDER);
		this.setBackground(BG_DEFAULT);

		label = new Label("This is a slightly longer text");
		label.setWrapText(true);
		label.setMouseTransparent(true);
		this.add(label, 0, 0);

		this.setOnDragDetected(new OnDragDetectedHandler());
		this.setOnDragOver(new OnDragOverHandler());
		this.setOnDragEntered(new OnDragEnteredHandler());
		this.setOnDragExited(new OnDragExitedHandler());
		this.setOnDragDropped(new OnDragDroppedHandler());
		this.setOnDragDone(new OnDragDoneHandler());

		field.attach(this);
		update(field);
	}

	private String cardFieldRepresentation(CommandCardField cardField) {
		if (cardField.player == null) {
			return null;
		}

		for (int i = 0; i < Player.NO_REGISTERS; i++) {
			CommandCardField other = cardField.player.getProgramField(i);
			if (other == cardField) {
				return "P," + i;
			}
		}

		for (int i = 0; i < Player.NO_CARDS; i++) {
			CommandCardField other = cardField.player.getHandField(i);
			if (other == cardField) {
				return "C," + i;
			}
		}
		return null;

	}

	private CommandCardField cardFieldFromRepresentation(String rep) {
		if (rep == null || field.player == null) {
			return null;
		}

		String[] strings = rep.split(",");
		if (strings.length != 2) {
			return null;
		}

		int i = Integer.parseInt(strings[1]);
		if ("P".equals(strings[0])) {
			if (i < Player.NO_REGISTERS) {
				return field.player.getProgramField(i);
			}
			return null;
		}
		if ("C".equals(strings[0])) {
			if (i < Player.NO_CARDS) {
				return field.player.getHandField(i);
			}
		}
		return null;
	}

	@Override
	public void updateView(Subject subject) {
		if (subject != field || subject == null) {
			return;
		}

		CommandCard card = field.getCard();
		if (card != null && field.isVisible()) {
			label.setText(card.getName());
			return;
		}
		label.setText("");
	}

	private class OnDragDetectedHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			event.consume();

			Object t = event.getTarget();
			if (!(t instanceof CardFieldView)) {
				return;
			}

			CardFieldView source = (CardFieldView) t;
			CommandCardField cardField = source.field;

			if ((cardField == null) || (cardField.getCard() == null)) {
				return;
			}
			if ((cardField.player == null) || (cardField.player.game == null) ||
					!cardField.player.game.getPhase().equals(Phase.PROGRAMMING)) {
				return;
			}

			Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
			Image image = source.snapshot(null, null);
			db.setDragView(image);

			ClipboardContent content = new ClipboardContent();
			content.put(ROBO_RALLY_CARD, cardFieldRepresentation(cardField));

			db.setContent(content);
			source.setBackground(BG_DRAG);
		}

	}

	private class OnDragOverHandler implements EventHandler<DragEvent> {

		@Override
		public void handle(DragEvent event) {
			event.consume();

			Object t = event.getTarget();
			if (!(t instanceof CardFieldView)) return;

			CardFieldView target = (CardFieldView) t;
			CommandCardField cardField = target.field;
			if (cardField == null || cardField.player == null || cardField.player.game == null)
				return; //If the cardField, player, or game do not exist
			if (cardField.getCard() != null && event.getGestureSource() != target)
				return; //If there already is another card there
			if (!event.getDragboard().hasContent(ROBO_RALLY_CARD)) return; //If we aren't dragging a card

			event.acceptTransferModes(TransferMode.MOVE);
		}

	}

	private class OnDragEnteredHandler implements EventHandler<DragEvent> {

		@Override
		public void handle(DragEvent event) {
			event.consume();
			Object t = event.getTarget();
			if (!(t instanceof CardFieldView)) {
				return;
			}

			CardFieldView target = (CardFieldView) t;
			CommandCardField cardField = target.field;

			if (cardField == null || cardField.getCard() != null ||
					cardField.player == null || cardField.player.game == null) return;

			if (event.getGestureSource() == target ||
					!event.getDragboard().hasContent(ROBO_RALLY_CARD)) return;

			target.setBackground(BG_DROP);
		}

	}

	private class OnDragExitedHandler implements EventHandler<DragEvent> {

		@Override
		public void handle(DragEvent event) {
			event.consume();
			Object t = event.getTarget();
			if (!(t instanceof CardFieldView)) return;

			CardFieldView target = (CardFieldView) t;
			CommandCardField cardField = target.field;

			if (cardField == null || cardField.getCard() != null ||
					cardField.player == null || cardField.player.game == null) return;
			if (event.getGestureSource() == target ||
					!event.getDragboard().hasContent(ROBO_RALLY_CARD)) return;

			target.setBackground(BG_DEFAULT);
		}
	}

	private class OnDragDroppedHandler implements EventHandler<DragEvent> {

		@Override
		public void handle(DragEvent event) {
			event.consume();

			Object t = event.getTarget();
			if (!(t instanceof CardFieldView)) return;

			CardFieldView target = (CardFieldView) t;
			CommandCardField cardField = target.field;
			target.setBackground(BG_DEFAULT);
			event.setDropCompleted(false);

			Dragboard db = event.getDragboard();
			if (cardField == null || cardField.getCard() != null ||
					cardField.player == null || cardField.player.game == null) {
				return;
			}

			if (event.getGestureSource() == target || !db.hasContent(ROBO_RALLY_CARD)) {
				return;
			}

			Object object = db.getContent(ROBO_RALLY_CARD);
			if (!(object instanceof String)) {
				return;
			}

			CommandCardField source = cardFieldFromRepresentation((String) object);
			if (source == null || !gameController.moveCards(source, cardField)) {
				return;
			}

			event.setDropCompleted(true);
		}

	}

	private class OnDragDoneHandler implements EventHandler<DragEvent> {

		@Override
		public void handle(DragEvent event) {
			event.consume();

			Object t = event.getTarget();
			if (!(t instanceof CardFieldView)) {
				return;
			}

			CardFieldView source = (CardFieldView) t;
			source.setBackground(BG_DEFAULT);
		}

	}

}




