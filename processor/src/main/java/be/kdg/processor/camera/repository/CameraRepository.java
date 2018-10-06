package be.kdg.processor.camera.repository;

import be.kdg.processor.camera.Camera;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CameraRepository extends CrudRepository<Camera, Long> {
    Camera findByCameraId(int id);
    List<Camera> findAllByCameraId(int id);

}