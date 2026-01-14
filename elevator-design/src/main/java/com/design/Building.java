package com.design;

import com.design.Button.ElevatorDispatcher;
import com.design.Button.InternalButton;
import com.design.Elevator.ElevatorCar;
import com.design.Elevator.ElevatorController;
import com.design.Strategy.Button.ElevatorSelectionStrategy;
import com.design.Strategy.Button.NearestElevatorStrategy;
import com.design.Strategy.Elevator.ElevatorSchedulingAlgorithm;
import com.design.Strategy.Elevator.ScanAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class Building {
    private final List<Floor> floors;
    private final List<ElevatorController> elevatorControllers;
    private final ElevatorDispatcher dispatcher;

    public Building(int numFloors, int numElevators) {
        this.floors = new ArrayList<>();
        this.elevatorControllers = new ArrayList<>();

        // Create elevators
        for (int i = 0; i < numElevators; i++) {
            ElevatorCar car = new ElevatorCar(i);
            ElevatorSchedulingAlgorithm algorithm = new ScanAlgorithm();
            ElevatorController controller = new ElevatorController(car, algorithm);
            elevatorControllers.add(controller);
        }

        // Create dispatcher
        ElevatorSelectionStrategy strategy = new NearestElevatorStrategy();
        this.dispatcher = new ElevatorDispatcher(elevatorControllers, strategy);

        // Create floors with external buttons
        for (int i = 0; i < numFloors; i++) {
            floors.add(new Floor(i, dispatcher));
        }

        System.out.println("Building initialized: " + numFloors + " floors, " + numElevators + " elevators\n");
    }

    public Floor getFloor(int floorNumber) {
        return floors.get(floorNumber);
    }

    public InternalButton getInternalButton(int elevatorId) {
        return new InternalButton(elevatorId, elevatorControllers.get(elevatorId));
    }

    public void shutdown() {
        for (ElevatorController controller : elevatorControllers) {
            controller.shutdown();
        }
    }
}
