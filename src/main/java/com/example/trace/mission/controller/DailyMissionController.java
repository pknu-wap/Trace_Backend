package com.example.trace.mission.controller;

import com.example.trace.mission.dto.AssignMissionRequest;
import com.example.trace.mission.dto.CompletedMissionRequest;
import com.example.trace.mission.dto.SubmitDailyMissionDto;
import com.example.trace.mission.dto.DailyMissionResponse;
import com.example.trace.mission.dto.CompletedMissionResponse;
import com.example.trace.mission.service.DailyMissionService;
import com.example.trace.mission.service.CompletedMissionService;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.post.dto.post.PostDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "mission", description = "미션 API")
public class DailyMissionController {

    private final DailyMissionService missionService;
    private final CompletedMissionService completedMissionService;

    /**
     * 오늘 할당된 미션을 사용자에게 반환합니다.
     */
    @GetMapping("/today")
    public ResponseEntity<DailyMissionResponse> getTodayMission(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        DailyMissionResponse response = missionService.getTodaysMissionByProviderId(providerId);
        return ResponseEntity.ok(response);
    }

    /**
     * 일일 미션을 변경합니다. 하루 최대 10번까지 변경 가능합니다.
     */
    @PostMapping("/change")
    public ResponseEntity<DailyMissionResponse> changeDailyMission(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        DailyMissionResponse response = missionService.changeDailyMission(providerId);
        return ResponseEntity.ok(response);
    }

    /**
     * 테스트용 미션 할당 엔드포인트
     */
    @PostMapping("/assign/test")
    public ResponseEntity<DailyMissionResponse> assignDailyMissionsToUserForTest(@RequestBody AssignMissionRequest request){
        String providerId = request.getProviderId();
        return ResponseEntity.ok(missionService.assignDailyMissionsToUserForTest(providerId));
    }

    /**
     * 미션 제출 시 선행 인증 후 게시글 등록
     */
    @PostMapping(value = "/submit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "미션 제출 시, 선행 인증", description = "미션 제출 내용이 선행과 관련있는지 인증합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "인증된 미션 게시글 작성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
    ))
    public ResponseEntity<PostDto> submitDailyMission(
            @RequestPart("request") SubmitDailyMissionDto submitDto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (imageFiles != null && !imageFiles.isEmpty()) {
            // 이미지 파일 최대 5개 제한
            int maxImages = Math.min(imageFiles.size(), 5);
            submitDto.setImageFiles(imageFiles.subList(0, maxImages));
        }
        String providerId = principalDetails.getUser().getProviderId();
        PostDto postDto = missionService.verifySubmissionAndCreatePost(providerId, submitDto);
        return ResponseEntity.ok(postDto);
    }

    /**
     * 사용자의 완료된 미션 목록을 조회합니다.
     * 커서 기반 페이지네이션을 사용합니다.
     */
    @PostMapping("/completed")
    public ResponseEntity<List<CompletedMissionResponse>> getCompletedMissions(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody(required = false) CompletedMissionRequest request) {
        try {
            String providerId = principalDetails.getUser().getProviderId();
            Long cursorId = (request != null) ? request.getCursorId() : null;
            List<CompletedMissionResponse> completedMissions =
                    completedMissionService.getUserCompletedMissions(providerId, cursorId);
            return ResponseEntity.ok(completedMissions);
        } catch (Exception e) {
            log.error("완료된 미션 조회 오류", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
