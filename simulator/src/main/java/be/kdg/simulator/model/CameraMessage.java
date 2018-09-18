package be.kdg.simulator.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class CameraMessage {
    private int id;
    private String licenceplate;
    private LocalDateTime timestamp;


    public CameraMessage(){

    }

    public CameraMessage(int id, String licenceplate, LocalDateTime timestamp) {
        this.id = id;
        this.licenceplate = licenceplate;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicenceplate() {
        return licenceplate;
    }

    public void setLicenceplate(String licenceplate) {
        this.licenceplate = licenceplate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CameraMessage that = (CameraMessage) o;
        return id == that.id &&
                Objects.equals(licenceplate, that.licenceplate) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, licenceplate, timestamp);
    }

    @Override
    // TODO: datum formateren
    public String toString() {
        return String.format("Camera Message %d %s %s", id, licenceplate, timestamp);

    }
}

