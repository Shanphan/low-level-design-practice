package com.design.Button;

import com.design.Elevator.Direction;
import com.design.Request;

public class ExternalButton {

    private final int floor;
    private final Direction direction;
    private final ElevatorDispatcher dispatcher;

    public ExternalButton(int floor, Direction direction, ElevatorDispatcher dispatcher) {
        this.floor = floor;
        this.direction = direction;
        this.dispatcher = dispatcher;
    }

    public void press() {
        System.out.println("\n>>> External Button Pressed: Floor " + floor + " | Direction: " + direction);
        dispatcher.requestElevator(new Request(floor, direction));
    }
}
