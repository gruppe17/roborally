package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.model.boardElement.BoardElement;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;


public class BoardElementView extends BorderPane {
    final private static String BASE_DIRECTORY = "images/tiles/boardElements";

    private ImageView imageView;

    public BoardElementView(BoardElement boardElement){
        imageView = createImageView(boardElement);
        fitImageSize();
        this.setCenter(imageView);
    }

    protected void fitImageSize(){
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
        imageView.setPreserveRatio(true);
    }

    protected String getImagePath(BoardElement boardElement){
        return BASE_DIRECTORY + "/priorityAntenna.png";
    }

    protected ImageView createImageView(BoardElement boardElement){
        return new ImageView(new Image(getImagePath(boardElement)));
    }

}
