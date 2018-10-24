package be.kdg.processor.camera.repository;

import be.kdg.processor.camera.dom.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository that gets used to persist Cameras to an H2 in-memory database.
 *
 * @author C&eacute;dric Goffin
 * @see Camera
 */
@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {
    Optional<Camera> findByCameraId(int cameraId);

    boolean existsByCameraId(int cameraId);

    List<Camera> findAllByEuroNorm(int euronorm);

    @Override
    <S extends Camera> S save(S entity);
}