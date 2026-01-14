package com.design.Elevator;

import com.design.Strategy.Elevator.ElevatorSchedulingAlgorithm;
import com.design.Request;

public class ElevatorController {

    private final ElevatorCar elevator;
    private final ElevatorSchedulingAlgorithm algorithm;
    private final Thread workerThread;
    private volatile boolean running = true;

    public ElevatorController(ElevatorCar elevator, ElevatorSchedulingAlgorithm algorithm) {
        this.elevator = elevator;
        this.algorithm = algorithm;

        // Start background worker thread to process requests
        this.workerThread = new Thread(this::processRequests);
        this.workerThread.start();
    }

    public void submitRequest(Request request) {
        System.out.println("[Controller-" + elevator.getId() + "] Received request: " + request);
        algorithm.addRequest(request);

    }

    private void processRequests() {

        if(running) {
            if(algorithm.hasRequests() && elevator.getStatus() == Status.IDLE) {

                algorithm.setCurrentState(elevator.getCurrentFloor(), elevator.getCurrentDirection());
                Integer nextFloor = algorithm.getNextFloor();
                if (nextFloor != null) {
                    elevator.moveToFloor(nextFloor);
                }
            }
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }
    }

    public void shutdown() {
        running = false;
        workerThread.interrupt();
    }

    public ElevatorCar getElevator() { return elevator; }

}
