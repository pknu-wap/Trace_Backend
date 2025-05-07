public interface AssignedMissionRepository extends JpaRepository<AssignedMission, Long> {
    Optional<AssignedMission> findByUserAndDate(User user, LocalDate date);
}
