package com.design;

public class Door {

    private DoorStatus status;

    public Door() {
        this.status = DoorStatus.CLOSED;
    }

    public void open() {
        if (status == DoorStatus.CLOSED) {
            status = DoorStatus.OPEN;
            System.out.println("  [Door Opening]");
        }
    }

    public void close() {
        if (status == DoorStatus.OPEN) {
            status = DoorStatus.CLOSED;
            System.out.println("  [Door Closing]");
        }
    }

    public DoorStatus getStatus() { return status; }

}
