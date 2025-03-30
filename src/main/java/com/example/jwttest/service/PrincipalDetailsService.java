package com.example.jwttest.service;

import com.example.jwttest.PrincipalDetails;
import com.example.jwttest.domain.User;
import com.example.jwttest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public PrincipalDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userEntity = userRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            User user = userEntity.get();
            return new PrincipalDetails(user);
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
