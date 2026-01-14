package com.design.Strategy.Elevator;

import com.design.Elevator.Direction;
import com.design.Elevator.ElevatorCar;
import com.design.Request;

public interface ElevatorSchedulingAlgorithm {

    void addRequest(Request request);
    Integer getNextFloor();
    boolean hasRequests();
    void setCurrentState(int currentFloor, Direction currentDirection);


}
