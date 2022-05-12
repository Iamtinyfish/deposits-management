package com.bank.depositsmanagement.service;

import com.bank.depositsmanagement.dao.AccountRepository;
import com.bank.depositsmanagement.entity.Account;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public UserService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username).orElse(null);

        if (account == null || !account.isActive()) {
            throw new UsernameNotFoundException("Not found account: " + username);
        }

        return account;
    }
}
