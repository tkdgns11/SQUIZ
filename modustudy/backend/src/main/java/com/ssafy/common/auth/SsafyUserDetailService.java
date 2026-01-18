package com.ssafy.common.auth;

import com.ssafy.domain.user.entity.User;
import com.ssafy.domain.user.service.UserService;  // ← import 수정!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SsafyUserDetailService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userService.getUserByUserId(username);  // ← 수정!

        if (userOpt.isPresent()) {
            return new SsafyUserDetails(userOpt.get());
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}