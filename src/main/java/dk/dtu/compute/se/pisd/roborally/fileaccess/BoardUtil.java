package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BoardUtil {
    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    /**
     * <p>Load a {@link Board} from a file and returns it.</p>
     * @param boardName the name of the board which is to be loaded
     * @return a new board that was generated from a file
     * @throws IOException
     */
    public static Board loadBoardFromFile(@NotNull String boardName) throws IOException {
        if (boardName == null){ //Unnecessary. May be removed
            assert false;
            throw new IOException("loadBoardFromFile: Cannot load null");
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        JsonReader

    }

    /**
     * <p>Saves a {@link Board} to as a jason file.</p>
     * @param board The board to save
     * @param fileName the name of the file that should be written to
     */
    public static void saveBoardToFile(Board board, String fileName){

    }

}
