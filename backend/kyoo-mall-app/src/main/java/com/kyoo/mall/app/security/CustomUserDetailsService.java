package com.kyoo.mall.app.security;

import com.kyoo.mall.user.domain.model.SysUser;
import com.kyoo.mall.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 组装层的安全适配：把身份上下文的用户仓储桥接为 Spring Security 的 UserDetailsService。
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return new User(user.getUsername(), user.getPassword(),
                user.isEnabled(), true, true, true,
                Collections.emptyList());
    }
}
