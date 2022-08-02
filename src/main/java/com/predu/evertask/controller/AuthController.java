package com.predu.evertask.controller;

import com.predu.evertask.config.security.CurrentUserId;
import com.predu.evertask.config.security.JwtTokenUtil;
import com.predu.evertask.domain.dto.auth.*;
import com.predu.evertask.domain.dto.user.UserAuthDto;
import com.predu.evertask.domain.dto.user.UserDto;
import com.predu.evertask.domain.mapper.UserViewMapper;
import com.predu.evertask.domain.model.User;
import com.predu.evertask.domain.model.VerificationToken;
import com.predu.evertask.event.OnSignupCompleteEvent;
import com.predu.evertask.exception.InvalidMFACodeException;
import com.predu.evertask.exception.InvalidTokenException;
import com.predu.evertask.repository.VerificationTokenRepository;
import com.predu.evertask.service.UserService;
import dev.samstevens.totp.exceptions.QrGenerationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Calendar;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserViewMapper userViewMapper;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthRequest request,
                                                 HttpServletResponse response) {

        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = (User) authenticate.getPrincipal();
        AuthResponseDto responseDto = userService.loginUser(user.getId(), response);

        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('ROLE_PRE_VERIFICATION_USER')")
    public ResponseEntity<AuthResponseDto> verifyCode(@RequestBody @Valid VerifyCodeDto dto,
                                                      @CurrentUserId UUID userId,
                                                      HttpServletResponse response) throws InvalidMFACodeException {

        AuthResponseDto responseDto = userService.verifyMFA(userId, dto, response);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/update_mfa")
    public ResponseEntity<MfaUpdateResponseDto> updateMfa(@RequestBody @Valid MfaUpdateRequestDto request,
                                                          @CurrentUserId UUID userID) throws QrGenerationException {

        MfaUpdateResponseDto responseDto = userService.updateMfa(userID, request);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/change_password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequestDto request,
                                               @CurrentUserId UUID userId) {

        userService.changePassword(userId, request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody @Valid CreateUserRequest request,
                                          HttpServletRequest servletRequest)
            throws ValidationException {

        User user = userService.create(request);

        String appUrl = servletRequest.getContextPath();
        eventPublisher.publishEvent(new OnSignupCompleteEvent(user, servletRequest.getLocale(), appUrl));

        return ResponseEntity.status(201).build();
    }

    @PutMapping("/confirm_signup")
    public ResponseEntity<UserDto> confirmSignup(@RequestParam("token") String token) throws InvalidTokenException {
        VerificationToken verificationToken = userService.getVerificationToken(token);

        if (verificationToken == null) {
            throw new InvalidTokenException("tokenInvalid");
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new InvalidTokenException("expired");
        }

        UserDto result = userService.updateEnabled(user.getId(), true);
        verificationTokenRepository.delete(verificationToken);

        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserAuthDto> refresh(@RequestParam("refreshToken") String refreshToken) throws InvalidTokenException {
        if (refreshToken == null) {
            throw new InvalidTokenException("tokenInvalid");
        }

        User user = userService.findByRefreshToken(refreshToken);

        if (user.getRefreshTokenExpiryDate().getTime() < System.currentTimeMillis()) {
            userService.updateRefreshToken(user, null, null);

            throw new InvalidTokenException("expired");
        }

        UserDto userDto = userViewMapper.toUserDto(user);
        String newAccessToken = jwtTokenUtil.generateAccessToken(user, true);
        String organisationId = user.getOrganisation() != null ? user.getOrganisation().getId().toString() : null;

        UserAuthDto userAuthDto = new UserAuthDto(userDto, newAccessToken, refreshToken, user.getAuthorities(), organisationId, user.isMfaEnabled());

        return ResponseEntity.status(200).body(userAuthDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) throws IllegalAccessException {
        if (authentication == null) {
            throw new IllegalAccessException("No user logged in.");
        }

        User user = (User) authentication.getPrincipal();

        userService.updateRefreshToken(user, null, null);

        return ResponseEntity.status(200).build();
    }
}
