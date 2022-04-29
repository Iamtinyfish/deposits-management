package com.bank.depositsmanagement.service;

import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !user.isActive()) {
            throw new UsernameNotFoundException("User not found! " + username);
        }

        String role = user.getRole();

        List<GrantedAuthority> grantList = new ArrayList<>();

        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        grantList.add(authority);

        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),grantList);
    }
}
