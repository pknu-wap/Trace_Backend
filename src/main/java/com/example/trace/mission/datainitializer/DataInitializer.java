package com.example.trace.mission.datainitializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.trace.mission.mission.MissionEntity;
import com.example.trace.mission.repository.MissionRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MissionRepository missionRepository;

    public DataInitializer(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (missionRepository.count() == 0) {
            missionRepository.save(new Mission("쓰레기 줍기"));
            missionRepository.save(new Mission("물 끄기"));
        }
    }
}

