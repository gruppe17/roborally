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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.BoardLaserController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.IBoardElementController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.BoardLaser;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class BoardLoader {

	private static final String BOARDSFOLDER = "boards";
	private static final String DEFAULTBOARD = "defaultboard";
	private static final String JSON_EXT = ".json";

	/**
	 * <p>Loads a {@link Board} from a file and returns it.</p>
	 *
	 * @param boardName the name of the board which is to be loaded
	 * @return a new board that was generated from a file
	 * @throws IOException
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public static Board loadBoard(String boardName) {
		if (boardName == null) {
			boardName = DEFAULTBOARD;
		}

		ClassLoader classLoader = BoardLoader.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardName + JSON_EXT);
		if (inputStream == null) {
			if (!boardName.equals(DEFAULTBOARD)) return loadBoard(DEFAULTBOARD);
			// TODO these constants should be defined somewhere
			return new Board(8, 8);
		}

		// In simple cases, we can create a Gson object with: new Gson()
		GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(
				BoardElement.class, new Adapter<BoardElement>());
		//new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
		Gson gson = simpleBuilder.create();


		Board result;
		// FileReader fileReader = null;
		JsonReader reader = null;
		try {
			// fileReader = new FileReader(filename);
			reader = gson.newJsonReader(new InputStreamReader(inputStream));
			BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

			result = boardFromBoardTemplate(template);
			reader.close();
			return result;
		} catch (IOException e1) {
			if (reader != null) {
				try {
					reader.close();
					inputStream = null;
				} catch (IOException e2) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e2) {
				}
			}
		}
		return null;
	}


	/**
	 * <p>Saves a {@link Board} to as a jason file.</p>
	 *
	 * @param board The board to save
	 * @param name  the name of the file that should be written to
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public static void saveBoard(@NotNull Board board, String name) {
		detachAllLasers(board); //Todo: this really should not be necessary. Find a proper solution.
		BoardTemplate template = boardToBoardTemplate(board, name);

		ClassLoader classLoader = BoardLoader.class.getClassLoader();
		// TODO: this is not very defensive, and will result in a NullPointerException
		//       when the folder "resources" does not exist! But, it does not need
		//       the file "simpleCards.json" to exist!
		String filename = classLoader.getResource(BOARDSFOLDER + "/" + name  + JSON_EXT).getPath();

		// In simple cases, we can create a Gson object with new:
		//
		//   Gson gson = new Gson();
		//
		// But, if you need to configure it, it is better to create it from
		// a builder (here, we want to configure the JSON serialisation with
		// a pretty printer):
		GsonBuilder simpleBuilder = new GsonBuilder().
				registerTypeAdapter(BoardElement.class, new Adapter<BoardElement>()).
				setPrettyPrinting();
		Gson gson = simpleBuilder.create();

		FileWriter fileWriter = null;
		JsonWriter writer = null;
		try {
			//filename = new URI(filename).getPath();
			filename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
			fileWriter = new FileWriter(filename);
			writer = gson.newJsonWriter(fileWriter);
			gson.toJson(template, template.getClass(), writer);
		} catch (IOException e1) {
			assert false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e2) {
				}
			}
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e2) {
				}
			}
		}
		attachAllLasers(board); //Todo: this really should not be necessary. Find a proper solution.
	}

	/**
	 * <p>Creates a new {@link Board} from a {@link BoardTemplate}.</p>
	 *
	 * @param template the template from which to create a new board
	 * @return a new board created from the template
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	@NotNull
	private static Board boardFromBoardTemplate(BoardTemplate template) {
		Board board = new Board(template.width, template.height, template.name);
		board.setCheckpointAmount(template.checkpointCount);
		for (SpaceTemplate spaceTemplate : template.spaces) {
			Space space = board.getSpace(spaceTemplate.x, spaceTemplate.y);
			if (space == null) continue;
			//todo add all method in space?
			for (BoardElement boardElement : spaceTemplate.boardElements) {
				space.addBoardElement(boardElement);
			}

		}

		attachAllLasers(board); //Todo: this really should not be necessary. Find a proper solution.
		return board;
	}

	/**
	 * <p>Creates a new {@link BoardTemplate} from a {@link Board}.</p>
	 *
	 * @param board the board of which to create a template
	 * @return a new board template of the specified board
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	@NotNull
	private static BoardTemplate boardToBoardTemplate(@NotNull Board board, String name) {
		//Todo: maybe should just be the constructor of BoardTemplate?
		BoardTemplate template = new BoardTemplate();
		template.name = name;
		template.width = board.width;
		template.height = board.height;
		template.checkpointCount = board.getCheckpointAmount();
		if (name == null) template.name = board.getBoardName();

		for (int i = 0; i < board.width; i++) {
			for (int j = 0; j < board.height; j++) {
				Space space = board.getSpace(i, j);
				if (space.getElements().length < 1) continue;
				template.spaces.add(spaceTemplateFromSpace(space));
			}
		}
		return template;
	}

	/**
	 * <p>Creates a new {@link SpaceTemplate} of a specified {@link Space}.</p>
	 *
	 * @param space the space of which to create a template
	 * @return a new SpaceTemplate of a specified space
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private static SpaceTemplate spaceTemplateFromSpace(@NotNull Space space) {
		SpaceTemplate spaceTemplate = new SpaceTemplate();
		spaceTemplate.x = space.x;
		spaceTemplate.y = space.y;
		spaceTemplate.boardElements.addAll(Arrays.asList(space.getElements()));
		return spaceTemplate;
	}

	/**
	 * This method detaches all lasers from their
	 * board laser. <b>Very icky!</b> and really should
	 * not be necessary. Find a better way!
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private static void detachAllLasers(Board board){
		for (int x = 0; x < board.width; x++) {
			for (int y = 0; y < board.height; y++) {
				Space space = board.getSpace(x, y);
				if (space == null) continue;
				for (IBoardElementController aEController: space.getActivationElementControllers()) {
					if (!(aEController instanceof BoardLaserController)) continue;
					aEController.getBoardElement().detach(((BoardLaser)aEController.getBoardElement()).getLaser());
				}
			}
		}
	}

	/**
	 * This method attaches all lasers to their
	 * board laser. <b>Very icky!</b> and really should
	 * not be necessary. Find a better way!
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private static void attachAllLasers(Board board){
		for (int x = 0; x < board.width; x++) {
			for (int y = 0; y < board.height; y++) {
				Space space = board.getSpace(x, y);
				if (space == null) continue;
				for (IBoardElementController aEController: space.getActivationElementControllers()) {
					if (!(aEController instanceof BoardLaserController)) continue;
					aEController.getBoardElement().attach(((BoardLaser)aEController.getBoardElement()).getLaser());
				}
			}
		}
	}


}
