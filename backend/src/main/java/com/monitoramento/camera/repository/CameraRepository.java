package com.monitoramento.camera.repository;

import com.monitoramento.camera.entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {

    List<Camera> findByActiveTrue();
}
