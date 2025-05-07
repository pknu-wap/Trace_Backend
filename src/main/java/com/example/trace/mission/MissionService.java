@Service
public class MissionService {
    private final MissionRepository missionRepository; // Mission 테이블에서 데이터를 조회하고 가져옴
    private final AssignedMissionRepository assignedMissionRepository;

    public MissionService(MissionRepository missionRepository, AssignedMissionRepository assignedMissionRepository) {
        this.missionRepository = missionRepository;
        this.assignedMissionRepository = assignedMissionRepository;
    }

    public AssignedMission assignRandomMission(User user) {
        LocalDate today = LocalDate.now();

        // 오늘 이미 미션이 할당된 사용자가 있는지 확인
        return assignedMissionRepository.findByUserAndDate(user, today)
                .orElseGet(() -> {
                    // 오늘 미션이 없다면 랜덤 미션 할당
                    Mission randomMission = missionRepository.findRandomMission(PageRequest.of(0, 1)).get(0);
                    AssignedMission assigned = new AssignedMission();
                    assigned.setUser(user);
                    assigned.setMission(randomMission);
                    assigned.setDate(today);
                    return assignedMissionRepository.save(assigned);
                });
    }

    public Optional<AssignedMission> getTodaysMission(User user) {
        // 사용자가 오늘 할당받은 미션 조회
        return assignedMissionRepository.findByUserAndDate(user, LocalDate.now());
    }
}
