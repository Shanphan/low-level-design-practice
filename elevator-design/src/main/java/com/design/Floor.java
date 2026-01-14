package com.design;

import com.design.Button.ElevatorDispatcher;
import com.design.Button.ExternalButton;
import com.design.Elevator.Direction;

public class Floor {

    private final int floorNumber;
    private final ExternalButton upButton;
    private final ExternalButton downButton;

    public Floor(int floorNumber,  ElevatorDispatcher dispatcher) {
        this.floorNumber = floorNumber;
        this.upButton = new ExternalButton(floorNumber, Direction.UP, dispatcher);
        this.downButton = new ExternalButton(floorNumber, Direction.DOWN, dispatcher);
    }

    public void pressUpButton() {
        upButton.press();
    }

    public void pressDownButton() {
        downButton.press();
    }

    public int getFloorNumber() { return floorNumber; }

}
