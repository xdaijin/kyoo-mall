package com.kyoo.mall.user.application;

import com.kyoo.mall.common.BusinessException;
import com.kyoo.mall.common.Result;
import com.kyoo.mall.user.domain.model.SysUser;
import com.kyoo.mall.user.domain.repository.UserRepository;
import com.kyoo.mall.user.domain.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户应用服务：注册、登录认证等用例编排。
 */
@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResult register(String username, String rawPassword, String nickname) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("用户名已被注册");
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setNickname(StringUtils.hasText(nickname) ? nickname : username);
        user.setStatus(SysUser.STATUS_ENABLED);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return issueToken(user);
    }

    /**
     * 校验用户名密码并签发令牌，失败抛 401 业务异常。
     */
    public LoginResult login(String username, String rawPassword) {
        SysUser user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(Result.CODE_UNAUTHORIZED, "用户名或密码错误");
        }
        if (!user.isEnabled()) {
            throw new BusinessException(Result.CODE_FORBIDDEN, "账号已被禁用");
        }
        return issueToken(user);
    }

    private LoginResult issueToken(SysUser user) {
        return new LoginResult(user, tokenProvider.generateToken(user.getId(), user.getUsername()));
    }
}
