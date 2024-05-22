package spring.group.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import spring.group.spring.models.User;
import spring.group.spring.repositories.UserRepository;

@Service
public class MyUserDetailsService implements
        UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user
                = userRepository.findUserByUsername(username).orElse(null);
        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRoles())
                        .build();
        return userDetails;
    }
}
