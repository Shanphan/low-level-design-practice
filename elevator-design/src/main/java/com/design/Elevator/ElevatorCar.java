package com.design.Elevator;

import com.design.Button.InternalButton;
import com.design.Door;

public class ElevatorCar {

    private final int id;
    private int currentFloor;
    private Direction currentDirection;
    private Status status;
    private final Display display;
    private final Door door;

    public ElevatorCar(int id) {
        this.id = id;
        this.currentFloor = 0;
        this.currentDirection = Direction.IDLE;
        this.status = Status.IDLE;
        this.display = new Display();
        this.door = new Door();

    }

    public void moveToFloor(int targetFloor) {
        if (targetFloor == currentFloor) {
            stopAtFloor();
            return;
        }

        status = Status.MOVING;
        currentDirection = (targetFloor > currentFloor) ? Direction.UP : Direction.DOWN;

        System.out.println("\n[Elevator-" + id + "] Moving " + currentDirection + " from floor " +
                currentFloor + " to floor " + targetFloor);


        // Simulate movement
        while (currentFloor != targetFloor) {
            if (currentDirection == Direction.UP) {
                currentFloor++;
            } else {
                currentFloor--;
            }
            display.update(currentFloor, currentDirection);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

        }

        stopAtFloor();
    }

    public void stopAtFloor() {
        status = Status.STOPPED;
        currentDirection = Direction.IDLE;
        System.out.println("[Elevator-" + id + "] Arrived at floor " + currentFloor);
        door.open();
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        door.close();
        status = Status.IDLE;
    }

    // Getters
    public int getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public Direction getCurrentDirection() { return currentDirection; }
    public Status getStatus() { return status; }
    public Display getDisplay() { return display; }
}
