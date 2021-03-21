package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;

import dk.dtu.compute.se.pisd.roborally.model.boardElement.EnergySpace;

public class EnergySpaceController implements IBoardElementController {

    private EnergySpace model;
    public EnergySpaceController(EnergySpace model){
        this.model = model;
    }


    @Override
    public void activate() {

    }
}
