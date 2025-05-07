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
        // 미션 데이터가 없을 경우에만 초기 데이터를 등록
        if (missionRepository.count() == 0) {
            missionRepository.save(new MissionEntity("쓰레기 줍기"));
            missionRepository.save(new MissionEntity("물 끄기"));
        }
    }
}
