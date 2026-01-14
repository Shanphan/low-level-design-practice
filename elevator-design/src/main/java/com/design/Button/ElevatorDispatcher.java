package com.design.Button;

import com.design.Elevator.ElevatorController;
import com.design.Request;
import com.design.Strategy.Button.ElevatorSelectionStrategy;

import java.util.List;

public class ElevatorDispatcher {

    private final List<ElevatorController> controllers;
    private final ElevatorSelectionStrategy strategy;

    public ElevatorDispatcher(List<ElevatorController> controllers, ElevatorSelectionStrategy strategy) {
        this.controllers = controllers;
        this.strategy = strategy;
    }
    public void requestElevator(Request request) {
        ElevatorController selected = strategy.selectElevator(controllers, request);
        if (selected != null) {
            System.out.println("[Dispatcher] Assigned Elevator-" + selected.getElevator().getId() +
                    " to request: " + request);
            selected.submitRequest(request);
        } else {
            System.out.println("[Dispatcher] No elevator available!");
        }
    }

}
