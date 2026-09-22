package com.kyoo.mall.user.interfaces;

import com.kyoo.mall.common.Result;
import com.kyoo.mall.user.application.result.LoginResult;
import com.kyoo.mall.user.application.service.UserAppService;
import com.kyoo.mall.user.interfaces.dto.LoginRequest;
import com.kyoo.mall.user.interfaces.dto.LoginResponse;
import com.kyoo.mall.user.interfaces.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAppService userAppService;

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResult result = userAppService.register(request.username(), request.password(), request.nickname());
        return Result.ok(toResponse(result));
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = userAppService.login(request.username(), request.password());
        return Result.ok(toResponse(result));
    }

    private LoginResponse toResponse(LoginResult result) {
        return new LoginResponse(result.token(), result.user().getId(),
                result.user().getUsername(), result.user().getNickname());
    }
}
