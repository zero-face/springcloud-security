package security;

import entity.SecurityUserDetail;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author Zero
 * @Date 2021/7/15 20:24
 * @Since 1.8
 * @Description
 **/
@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        SecurityUserDetail securityUserDetails = new SecurityUserDetail(
                "user",
                passwordEncoder.encode("user"),
                true,
                true,
                true,
                true, new ArrayList<>(),
                1L
        );
        return Mono.just(securityUserDetails);
    }
}
