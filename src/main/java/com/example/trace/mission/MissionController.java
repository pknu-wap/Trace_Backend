@RestController
@RequestMapping("/missions")
public class MissionController {

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @PostMapping("/assign/{userId}")
    public ResponseEntity<AssignedMission> assignMission(@PathVariable Long userId) {
        User user = new User();  // 실제로는 userService에서 가져와야 함
        user.setId(userId);
        AssignedMission mission = missionService.assignRandomMission(user);
        return ResponseEntity.ok(mission);
    }

    @GetMapping("/today/{userId}")
    public ResponseEntity<AssignedMission> getTodayMission(@PathVariable Long userId) {
        User user = new User();  // 실제로는 userService에서 가져와야 함
        user.setId(userId);
        return missionService.getTodaysMission(user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
