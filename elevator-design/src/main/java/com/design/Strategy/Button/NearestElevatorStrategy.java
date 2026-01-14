package com.design.Strategy.Button;

import com.design.Elevator.ElevatorCar;
import com.design.Elevator.ElevatorController;
import com.design.Elevator.Status;
import com.design.Request;

import java.util.List;

/**
 * Nearest Elevator Strategy
 * - Prioritizes IDLE elevators
 * - Selects closest elevator
 * - Falls back to any available elevator
 */

public class NearestElevatorStrategy implements ElevatorSelectionStrategy {

    @Override
    public ElevatorController selectElevator(List<ElevatorController> controllers, Request request) {

        ElevatorController nearest = null;
        int minDistance = Integer.MAX_VALUE;


        //Find nearest elevator
        for(ElevatorController ec : controllers) {
            ElevatorCar car = ec.getElevator();
            if(car.getStatus() == Status.IDLE) {
                int distance = Math.abs(request.getFloor()) - car.getCurrentFloor();
                if(minDistance > distance) {
                    minDistance = distance;
                    nearest = ec;
                }
            }
        }

        //Find minDistance elevator
        for(ElevatorController ec : controllers) {
            ElevatorCar car = ec.getElevator();
            int distance = Math.abs(request.getFloor()) - car.getCurrentFloor();
            if(minDistance > distance) {
                minDistance = distance;
                nearest = ec;
            }
        }

        return nearest;
    }
}
