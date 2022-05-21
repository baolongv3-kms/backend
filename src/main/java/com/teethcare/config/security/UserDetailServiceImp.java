package com.teethcare.config.security;

import com.teethcare.model.entity.Account;
import com.teethcare.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailServiceImp implements UserDetailsService {

    private AccountRepository accountRepository;

    @Autowired
    public UserDetailServiceImp(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByUsername(username);
        if (account != null) {
            return UserDetailsImpl.build(account);
        } else {
            throw new RuntimeException("Employee id not found " + username);
        }
    }
}
