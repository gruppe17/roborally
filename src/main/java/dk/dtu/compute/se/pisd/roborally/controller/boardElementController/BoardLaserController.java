package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;

import dk.dtu.compute.se.pisd.roborally.interfaces.ILaser;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.boardElement.activationElements.BoardLaser;

public class BoardLaserController implements IBoardElementController, ILaser {
    private BoardLaser model;
    public BoardLaserController(BoardLaser model){
        this.model = model;
    }

    /**
     * <p>Fire in the direction the owner
     * robot is facing. Their range has no
     * limit. Any robot in the line of sight is
     * shot. Robot lasers cannot fire through
     * walls or shoot more than one robot.</p>
     * @author Tobias Maneschijn, s205422@student.dtu.dk
     */
    @Override
    public void fire() {
        Space currentSpace = model.getSpace();
        Space lastSpace = currentSpace;

        /* Abort if player is not in a space*/
        if (lastSpace == null)
            return;
        while (true) {
            /* get next space */
            lastSpace = currentSpace.board.getNeighbour(lastSpace, model.getDirection());
            Player playerAtSpace = lastSpace.getPlayer();
            /* remember to add the right elements to prevent hitting walls and stuff here */
           /* if(lastSpace.element == WallBoardElement || lastSpace.element == PriorityAntennaBoardElement){
                break;
            }*/

            /* exit if nothing is hit */
            if (lastSpace == null) {
                break;
            }
            /* We should add fx to the spaces that are hit*/

            /* If player is hit, then damage it and do stuff. */
            if (playerAtSpace != null) {

                // Change this to SPAM Command Card
                playerAtSpace.playerController.addCard(new CommandCard(Command.FORWARD));

                break;
            }
        }

        /* Reset last space*/
        currentSpace = model.getSpace();
        lastSpace = currentSpace;

        /*Maybe do some cleanup of fx here?*/
        while (true) {
            /* get next space */
            lastSpace = currentSpace.board.getNeighbour(lastSpace, model.getDirection());
            if (lastSpace == null) {
                break;
            }
        }
    }
}
