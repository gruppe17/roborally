package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.model.boardElement.BoardElement;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;


public class BoardElementView extends BorderPane {
    private ImageView imageView;

    public BoardElementView(BoardElement boardElement){
        Image img = new Image(getImagePath(boardElement));
        imageView = new ImageView(img);
    }

    private String getImagePath(){
    }

}
