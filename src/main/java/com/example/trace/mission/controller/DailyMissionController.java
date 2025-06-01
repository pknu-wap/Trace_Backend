package com.example.trace.mission.controller;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.gpt.service.PostVerificationService;
import com.example.trace.mission.dto.AssignMissionRequest;
import com.example.trace.mission.dto.SubmitDailyMissionDto;
import com.example.trace.mission.service.DailyMissionService;
import com.example.trace.mission.dto.DailyMissionResponse;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.mission.util.MissionDateUtil;
import com.example.trace.post.dto.post.PostDto;
import com.example.trace.post.service.PostService;
import com.example.trace.user.User;
import com.example.trace.user.UserService;
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

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "mission", description = "미션 API")
public class DailyMissionController {

    private final DailyMissionService missionService;
    private final UserService userService;

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

    @PostMapping("/assign/test")
    public ResponseEntity<DailyMissionResponse> assignDailyMissionsToUserForTest(@RequestBody AssignMissionRequest request){
        String providerId = request.getProviderId();
        User user = userService.getUser(providerId);
        LocalDate missionDate = MissionDateUtil.getMissionDate();
        return ResponseEntity.ok(missionService.assignDailyMissionsToUser(user,missionDate));
    }

    @PostMapping(value ="/submit",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
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
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        if (imageFiles != null && !imageFiles.isEmpty()) {
            // Limit to 5 images
            int maxImages = Math.min(imageFiles.size(), 5);
            submitDto.setImageFiles(imageFiles.subList(0, maxImages));
        }
        String providerId = principalDetails.getUser().getProviderId();
        PostDto postDto = missionService.verifySubmissionAndCreatePost(providerId,submitDto);
        return ResponseEntity.ok(postDto);
    }

}
