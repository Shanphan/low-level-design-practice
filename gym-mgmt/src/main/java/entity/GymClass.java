package entity;

import java.time.LocalTime;

public class GymClass {

    private String id;
    private String gymId;
    private ClassType classType;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxOccupancy;

    public GymClass(String gymId, ClassType classType, LocalTime startTime, LocalTime endTime, int maxOccupancy) {
        this.id = EntityIdGenerator.getId("CLASS-");
        this.gymId = gymId;
        this.classType = classType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxOccupancy = maxOccupancy;
    }

    public String getId() {
        return id;
    }

    public String getGymId() {
        return gymId;
    }

    public void setGymId(String gymId) {
        this.gymId = gymId;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }
}
