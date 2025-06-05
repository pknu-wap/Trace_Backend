package com.example.trace.user;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.file.FileType;
import com.example.trace.file.S3UploadService;
import com.example.trace.post.dto.cursor.CursorResponse;
import com.example.trace.post.dto.cursor.PostCursorRequest;
import com.example.trace.post.dto.post.PostFeedDto;
import com.example.trace.post.service.PostService;
import com.example.trace.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.trace.user.dto.UpdateNickNameRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 정보 API")
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final S3UploadService s3UploadService;
    private final PostService postService;


    @Operation(summary = "유저 정보 조회", description = "유저 정보를 가져옵니다.")
    @ApiResponse(
            responseCode = "200",
            description = "User information retrieved successfully.",
            content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = UserDto.class)
            )
    )
    @GetMapping
    public ResponseEntity<UserDto> getUserInfo(HttpServletRequest request) {
        String token = jwtUtil.resolveAccessToken(request);
        String providerId = jwtUtil.getProviderId(token);
        UserDto userDto = userService.getUserInfo(providerId);
        return ResponseEntity.ok(userDto);
    }


    @Operation(summary = "유저 프로필 이미지 수정", description = "프로필 이미지를 수정합니다.")
    @PutMapping(value = "/profile/image",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserDto> updateUserProfileImage(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {

        User user = principalDetails.getUser();
        String providerId = user.getProviderId();

        // 프로필 이미지 S3 업로드 후 URL 획득
        String imageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            imageUrl = s3UploadService.saveFile(profileImage, FileType.PROFILE, providerId);
        }

        return ResponseEntity.ok(userService.updateUserProfileImage(providerId, imageUrl));
    }

    @Operation(summary = "유저 닉네임 수정", description = "유저 닉네임을 수정합니다.")
    @PutMapping("/profile/nickname")
    public ResponseEntity<UserDto> updateUserNickName(
            @Valid @RequestBody UpdateNickNameRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        return ResponseEntity.ok(userService.updateUserNickName(user, request));
    }




    @Operation(summary = "로그아웃", description = "로그아웃 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "Logout successful.",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String accessToken = jwtUtil.resolveAccessToken(request);
        userService.logout(accessToken);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "account deletion successful.",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        String accessToken = jwtUtil.resolveAccessToken(request);
        userService.deleteUser(accessToken);
        return ResponseEntity.ok().build();
    }

}
