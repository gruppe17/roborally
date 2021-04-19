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
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.view.board.GameView;
import javafx.scene.control.TabPane;

/**
 * <p>The area of the {@link GameView} where the player mats
 * are displayed as well as the container for the
 * {@link PlayerMatView}s</p>
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class PlayerMatsView extends TabPane implements ViewObserver {

	private Game game;

	private PlayerMatView[] playerMatViews;

	public PlayerMatsView(GameController gameController) {
		game = gameController.game;

		this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		playerMatViews = new PlayerMatView[game.getNumPlayers()];
		for (int i = 0; i < game.getNumPlayers(); i++) {
			playerMatViews[i] = new PlayerMatView(gameController, game.getPlayer(i));
			this.getTabs().add(playerMatViews[i]);
		}
		game.attach(this);
		update(game);
	}

	@Override
	public void updateView(Subject subject) {
		if (subject != game) return;
		Player current = game.getCurrentPlayer();
		this.getSelectionModel().select(game.getPlayerNumber(current));
	}

}
