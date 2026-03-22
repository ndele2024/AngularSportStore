package com.sportstore.backend.security;

import com.sportstore.backend.domain.AppUser;
import com.sportstore.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new AuthenticatedUser(
      user.getId(),
      user.getUsername(),
      user.getPassword(),
      user.getRole()
    );
  }
}
