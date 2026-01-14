package com.design.Elevator;

public class Display {

    int currentFloor;
    Direction direction;

    public Display() {
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
    }

    public void update(int floor, Direction direction) {
        this.currentFloor = floor;
        this.direction = direction;
        showDisplay();
    }

    private void showDisplay() {
        System.out.println("Display: Floor " + currentFloor + " | Direction: " + direction);
    }

    public int getFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }
}
