package ru.pfr.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pfr.server.entity.ActionLogEntity;
import ru.pfr.server.repository.ActionLogRepository;
import java.util.List;

@Service
public class ActionLogService {
    private final ActionLogRepository actionLogRepository;

    @Autowired
    public ActionLogService(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    public ActionLogEntity logAction(Long userId, String username, String action, String details) {
        ActionLogEntity log = new ActionLogEntity(userId, username, action, details);
        return actionLogRepository.save(log);
    }

    public List<ActionLogEntity> getUserLogs(Long userId) {
        return actionLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<ActionLogEntity> getAllLogs() {
        return actionLogRepository.findAllByOrderByTimestampDesc();
    }
} 