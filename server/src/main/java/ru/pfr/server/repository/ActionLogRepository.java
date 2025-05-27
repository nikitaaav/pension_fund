package ru.pfr.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pfr.server.entity.ActionLogEntity;
import java.util.List;
 
public interface ActionLogRepository extends JpaRepository<ActionLogEntity, Long> {
    List<ActionLogEntity> findByUserIdOrderByTimestampDesc(Long userId);
    List<ActionLogEntity> findAllByOrderByTimestampDesc();
} 