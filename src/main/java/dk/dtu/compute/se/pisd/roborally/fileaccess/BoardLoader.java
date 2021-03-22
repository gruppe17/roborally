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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;

import java.io.*;
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
    private static final String JSON_EXT = "json";

    /**
     * <p>Load a {@link Board} from a file and returns it.</p>
     * @param boardName the name of the board which is to be loaded
     * @return a new board that was generated from a file
     * @throws IOException
     */
    public static Board loadBoard(String boardName) {
        if (boardName == null) {
            boardName = DEFAULTBOARD;
        }

        ClassLoader classLoader = BoardLoader.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardName + "." + JSON_EXT);
        if (inputStream == null) {
            // TODO these constants should be defined somewhere
            return new Board(8,8);
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

			result = new Board(template.width, template.height);
			for (SpaceTemplate spaceTemplate: template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space == null) continue;
                //todo add all method in space?
                for (BoardElement boardElement : spaceTemplate.boardElements) {
                    space.addBoardElement(boardElement);
                }
            }
			reader.close();
			return result;
		} catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {}
            }
            if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e2) {}
			}
		}
		return null;
    }


    /**
     * <p>Saves a {@link Board} to as a jason file.</p>
     * @param board The board to save
     * @param name the name of the file that should be written to
     */
    public static void saveBoard(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;

        for (int i=0; i<board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space space = board.getSpace(i, j);
                if (space.getElements().length < 1) continue;
                SpaceTemplate spaceTemplate = new SpaceTemplate();
                spaceTemplate.x = space.x;
                spaceTemplate.y = space.y;
                spaceTemplate.boardElements.addAll(Arrays.asList(space.getElements()));
                template.spaces.add(spaceTemplate);
            }
        }

        ClassLoader classLoader = BoardLoader.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename = classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;
                //BOARDSFOLDER + "/" + name  + "." + JSON_EXT;

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
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            assert false;
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

}