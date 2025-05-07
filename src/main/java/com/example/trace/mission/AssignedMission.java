@Entity
public class AssignedMission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user; // 어떤 사용자에게 미션이 할당됐는지 저장

    @ManyToOne
    private Mission mission; // 어떤 미션이 할당됐는지 저장

    private LocalDate date; // 날짜 저장
}
