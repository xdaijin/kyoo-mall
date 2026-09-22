package com.kyoo.mall.user.interfaces.dto;

public record LoginResponse(String token, Long userId, String username, String nickname) {
}
