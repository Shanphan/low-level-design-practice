package com.design.Button;

import com.design.Elevator.Direction;
import com.design.Elevator.ElevatorController;
import com.design.Request;

public class InternalButton {

    private final int elevatorId;
    private final ElevatorController controller;

    public InternalButton(int elevatorId, ElevatorController controller) {
        this.elevatorId = elevatorId;
        this.controller = controller;
    }

    public void pressButton(int destinationFloor) {
        System.out.println("\n>>> Internal Button Pressed: Elevator-" + elevatorId +
                " -> Floor " + destinationFloor);
        controller.submitRequest(new Request(destinationFloor, Direction.IDLE));
    }


}
