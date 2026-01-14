package com.design.Strategy.Elevator;

import com.design.Elevator.Direction;
import com.design.Request;

import java.util.TreeSet;

/**
 * SCAN (LOOK) Algorithm Implementation
 * - Elevator continues in current direction until no more requests
 * - Then reverses direction
 * - More efficient than FCFS for multiple requests
 */

public class ScanAlgorithm implements ElevatorSchedulingAlgorithm {

    private final TreeSet<Integer> upRequests = new TreeSet<>();
    private final TreeSet<Integer> downRequests = new TreeSet<>();
    private Direction currentDirection = Direction.UP;
    private int currentFloor = 0;

    @Override
    public void addRequest(Request request) {
        if (request.getDirection() == Direction.UP ||
                (request.getDirection() == Direction.IDLE && request.getFloor() > currentFloor)) {
            upRequests.add(request.getFloor());
        } else {
            downRequests.add(request.getFloor());
        }
        System.out.println("  Added to queue: " + request + " (UP: " + upRequests + ", DOWN: " + downRequests + ")");
    }

    @Override
    public Integer getNextFloor() {
        if (currentDirection == Direction.UP) {
            // Get next floor in upward direction
            Integer next = upRequests.ceiling(currentFloor);
            if (next != null) {
                upRequests.remove(next);
                return next;
            }
            // No more up requests, switch to down
            currentDirection = Direction.DOWN;
        }
        if (currentDirection == Direction.DOWN) {
            // Get next floor in downward direction
            Integer next = downRequests.floor(currentFloor);
            if (next != null) {
                downRequests.remove(next);
                return next;
            }
            // No more down requests, switch to up
            currentDirection = Direction.UP;
        }

        return null;
    }

    @Override
    public boolean hasRequests() {
        return !upRequests.isEmpty() || !downRequests.isEmpty();
    }

    @Override
    public void setCurrentState(int currentFloor, Direction currentDirection) {
        this.currentFloor = currentFloor;
        this.currentDirection = currentDirection;
    }
}
