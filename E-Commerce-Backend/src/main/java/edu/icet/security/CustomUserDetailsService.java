package edu.icet.security;

import edu.icet.dto.UserDto;
import edu.icet.entity.User;
import edu.icet.exception.NotFoundException;
import edu.icet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userRepository.findByEmail(username).orElseThrow(()->new NotFoundException("User/Emal Not Found...!"));

        return AuthUser.builder().user(user).build();
    }
}
