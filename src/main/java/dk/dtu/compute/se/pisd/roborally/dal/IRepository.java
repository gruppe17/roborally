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
package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.model.Game;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public interface IRepository {
	
 	boolean createGameInDB(Game game, String name);
	
	boolean updateGameInDB(Game game);

	//TODO: Should maybe be saveGame, rather than create and update?
	// Shouldn't the repository be responsible for communicating with the database
	// and knowing whether the game should be created or updated?
	// But what if it already is saved and you want to make a new save without
	// overwriting the old save? Maybe it should take an argument allowing the
	// saver to force the creation of a new save. Then it should probably also
	// have a method that returns whether saving would overwrite an existing save
	// allowing the saver to ask the user if the want to overwrite the old save.
	
	Game loadGameFromDB(int id);
	
	List<GameInDB> getGames();

}
