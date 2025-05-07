public interface MissionRepository extends JpaRepository<Mission, Long> {
    @Query("SELECT m FROM Mission m ORDER BY function('RAND')")
    List<Mission> findRandomMission(Pageable pageable);
}
