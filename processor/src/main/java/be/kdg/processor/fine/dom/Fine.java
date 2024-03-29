package be.kdg.processor.fine.dom;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract model class that holds information about a Fine. Gets stored in an H2 in-memory database in a table named 'tblFine'.
 * Can be expanded in the future by creating a class that extends Fine. A new Fine calculation method can be made in the FineCalculationService,
 * along with a method in the FineDetectionService on how to detect the new fine type from a list of CameraMessages. This new method can then be placed
 * in the processMessages method (FineDetectionService) under the part "Process different Fine types". If only messages from a certain CameraType need to be processed,
 * the new CameraType can be added to the enum. You can now filter out messages from the cameras having the new CameraType using the getMessagesFromTypes from the CameraServiceAdapter.
 *
 * @author C&eacute;dric Goffin
 * @see EmissionFine
 * @see SpeedingFine
 * @see be.kdg.processor.fine.services.FineCalculationService
 * @see be.kdg.processor.fine.services.FineDetectionService
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblFine")
public abstract class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fineId;
    private double amount;
    private LocalDateTime timestamp;
    private LocalDateTime paymentDeadline;
    private String licenseplateId;
    @ManyToOne(targetEntity = Licenseplate.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Licenseplate licenseplate;
    @ManyToMany(targetEntity = Camera.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "tbl_fine_camera", joinColumns = @JoinColumn(name = "fineId"), inverseJoinColumns = @JoinColumn(name = "cameraId"))
    private List<Camera> cameras = new ArrayList<>();
    private boolean isAccepted = false;
    private String motivation;

    /**
     * Fine constructor used by classes extending from Fine.
     *
     * @param amount          amount of the fine
     * @param timestamp       the timestamp the fine was made
     * @param paymentDeadline the deadline for when the fine must be paid
     * @param licenseplate    the licenseplate of the owner that got fined
     * @param cameras         a list of cameras that hold information about the fine
     */
    public Fine(double amount, LocalDateTime timestamp, LocalDateTime paymentDeadline, Licenseplate licenseplate, List<Camera> cameras) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.paymentDeadline = paymentDeadline;
        this.licenseplate = licenseplate;
        licenseplateId = licenseplate.getPlateId();
        this.cameras.addAll(cameras);

        licenseplate.getFines().add(this);
        cameras.forEach(c -> c.getFines().add(this));
    }

    /**
     * Fine constructor used by classes extending from Fine.
     *
     * @param amount          amount of the fine
     * @param timestamp       the timestamp the fine was made
     * @param paymentDeadline the deadline for when the fine must be paid
     * @param licenseplate    the licenseplate of the owner that got fined
     * @param camera          a Camera object that hold information about the fine
     */
    public Fine(double amount, LocalDateTime timestamp, LocalDateTime paymentDeadline, Licenseplate licenseplate, Camera camera) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.paymentDeadline = paymentDeadline;
        this.licenseplate = licenseplate;
        licenseplateId = licenseplate.getPlateId();
        this.cameras.add(camera);

        licenseplate.getFines().add(this);
        camera.getFines().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fine fine = (Fine) o;

        if (!timestamp.toLocalDate().equals(fine.timestamp.toLocalDate())) return false;
        return licenseplate.equals(fine.licenseplate);
    }

    @Override
    public int hashCode() {
        int result = timestamp.toLocalDate().hashCode();
        result = 31 * result + licenseplate.hashCode();
        return result;
    }
}
