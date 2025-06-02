package com.example.trace.mission.service;

import com.example.trace.global.errorcode.MissionErrorCode;
import com.example.trace.global.exception.MissionException;
import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.gpt.service.PostVerificationService;
import com.example.trace.mission.dto.DailyMissionResponse;
import com.example.trace.mission.dto.SubmitDailyMissionDto;
import com.example.trace.mission.mission.DailyMission;
import com.example.trace.mission.repository.DailyMissionRepository;
import com.example.trace.mission.mission.Mission;
import com.example.trace.mission.repository.MissionRepository;
import com.example.trace.post.domain.PostType;
import com.example.trace.post.dto.post.PostCreateDto;
import com.example.trace.post.dto.post.PostDto;
import com.example.trace.post.service.PostService;
import com.example.trace.user.User;
import com.example.trace.user.UserService;
import com.example.trace.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DailyMissionService {

    private final MissionRepository missionRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PostVerificationService postVerificationService;
    private final PostService postService;
    
    private static final int MAX_CHANGES_PER_DAY = 10;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Scheduled(cron = "0 00 16 * * *")
    @Transactional
    public void assignDailyMissionsToAllUsers() {
        try {
            LocalDate today = LocalDate.now();
            List<User> users = userService.getAllUsers();

            for (User user : users) {
                Optional<DailyMission> existingMission = dailyMissionRepository.findByUserAndDate(user, today);

                if(existingMission.isPresent()){
                    continue;
                }

                Mission randomMission = missionRepository.findRandomMission();
                DailyMission dailyMission = DailyMission.builder()
                                                    .user(user)
                                                    .mission(randomMission)
                                                    .date(today)
                                                    .changeCount(0)
                                                    .isVerified(false)
                                                    .build();
                dailyMissionRepository.save(dailyMission);
            }
        } catch (Exception e) {
            System.err.println("자동 미션 할당 실패: " + e.getMessage());
        }
    }

    @Transactional
    public DailyMissionResponse assignDailyMissionsToUserForTest(String providerId){

        LocalDate today = LocalDate.now();
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(()->new UsernameNotFoundException("해당 user가 없습니다 : " + providerId));

        dailyMissionRepository.findByUserAndDate(user, today)
                .ifPresent(existingDailyMission -> dailyMissionRepository.delete(existingDailyMission));

        Mission randomMission = missionRepository.findRandomMission();
        DailyMission dailyMission = DailyMission.builder()
                                                .user(user)
                                                .mission(randomMission)
                                                .date(today)
                                                .changeCount(0)
                                                .isVerified(false)
                                                .build();
        return DailyMissionResponse.fromEntity(dailyMissionRepository.save(dailyMission));
    }

    @Transactional
    public DailyMissionResponse changeDailyMission(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new MissionException(MissionErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();

        DailyMission currentMission = dailyMissionRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new MissionException(MissionErrorCode.DAILYMISSION_NOT_FOUND));

        if(currentMission.isVerified()){
            throw new MissionException(MissionErrorCode.ALREADY_VERIFIED);
        }

        if (currentMission.getChangeCount() >= MAX_CHANGES_PER_DAY) {
            throw new MissionException(MissionErrorCode.MISSION_CREATION_LIMIT_EXCEEDED);
        }

        Long currentMissionId = currentMission.getMission().getId();

        Mission newMission = null;
        for (int i = 0; i < 5; i++) {
            Mission randomMission = missionRepository.findRandomMission();
            if (!randomMission.getId().equals(currentMissionId)) {
                newMission = randomMission;
                break;
            }
        }

        if (newMission == null) {
            throw new MissionException(MissionErrorCode.RANDOM_MISSION_NOT_FOUND);
        }

        currentMission.changeMission(newMission);
        dailyMissionRepository.save(currentMission);
        return DailyMissionResponse.fromEntity(currentMission);
    }
    
    /**
     * providerId로 오늘의 미션을 조회합니다.
     */
    public DailyMissionResponse getTodaysMissionByProviderId(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(()-> new MissionException(MissionErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();
        DailyMission todayDailyMission = dailyMissionRepository.findByUserAndDate(user,today)
                .orElseThrow(()->new MissionException(MissionErrorCode.DAILYMISSION_NOT_FOUND));

        return DailyMissionResponse.fromEntity(todayDailyMission);
    }

    public PostDto verifySubmissionAndCreatePost(String providerId, SubmitDailyMissionDto submitDto){
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(()->new MissionException(MissionErrorCode.USER_NOT_FOUND));
        LocalDate today = LocalDate.now();
        DailyMission assignedDailyMission = dailyMissionRepository.findByUserAndDate(user,today)
                .orElseThrow(()-> new MissionException(MissionErrorCode.DAILYMISSION_NOT_FOUND));

        VerificationDto verificationDto = postVerificationService.verifyDailyMission(submitDto, assignedDailyMission);
        if(!verificationDto.isImageResult() && !verificationDto.isTextResult()){
            throw new MissionException(MissionErrorCode.VERIFICATION_FAIL);
        }
        
        PostCreateDto postCreateDto = PostCreateDto.builder()
                .postType(PostType.MISSION)
                .title(submitDto.getTitle())
                .content(submitDto.getContent())
                .imageFiles(submitDto.getImageFiles())
                .missionContent(assignedDailyMission.getMission().getDescription())
                .build();
        
        PostDto postDto = postService.createPost(postCreateDto, providerId, verificationDto);
        
        // 미션 완료 처리
        assignedDailyMission.updateVerification(true);
        assignedDailyMission.updatePostId(postDto.getId());
        dailyMissionRepository.save(assignedDailyMission);
        
        return postDto;
    }

    public List<DailyMissionResponse> getCompletedMissions(String providerId, Long cursorId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new MissionException(MissionErrorCode.USER_NOT_FOUND));
                
        List<DailyMission> completedMissions = dailyMissionRepository
                .findByUserWithCursor(user, cursorId, DEFAULT_PAGE_SIZE);
        return completedMissions.stream()
                .map(DailyMissionResponse::fromEntity)
                .toList();
    }
}

