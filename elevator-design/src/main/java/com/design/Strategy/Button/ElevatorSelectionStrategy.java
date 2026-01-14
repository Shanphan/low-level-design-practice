package com.design.Strategy.Button;

import com.design.Elevator.ElevatorCar;
import com.design.Elevator.ElevatorController;
import com.design.Request;

import java.util.List;

public interface ElevatorSelectionStrategy {

    ElevatorController selectElevator(List<ElevatorController> controllers, Request request);
}
