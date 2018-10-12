package be.kdg.processor.camera.repository;

import be.kdg.processor.camera.dom.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CameraRepository extends JpaRepository<Camera, Integer> {
    Optional<Camera> findByCameraId(int cameraId);
    boolean existsByCameraId(int cameraId);

    @Override
    <S extends Camera> S save(S entity);
}