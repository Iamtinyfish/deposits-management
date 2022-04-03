package com.bank.depositsmanagement.service;

import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null || user.isActive() != true) {
            System.out.println("User not found! " + username);
            throw new UsernameNotFoundException("User not found! " + username);
        }

        String role = user.getRole();

        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();

        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        grantList.add(authority);

        UserDetails userDetails = (UserDetails) new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),grantList);

        return userDetails;
    }
}
