package com.ksn.handle;

import com.ksn.entity.User;
import com.ksn.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;



/**
 *
 * 自定义验证器，就是验证用户对不对的地方
 * @author admin
 */
@Slf4j
@Component
public class AgileUserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private UserService userService;
//    @Autowired
//    private UserCache userCache;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private static final String USER_NOT_FOUND_PASS_WORD = "userNotFoundPassword";

    private volatile String userNotFoundEncodedPass_word;


    /**
     * 校验密码有效性.
     *
     * @param userDetails
     * @param authentication
     * @throws AuthenticationException
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();
        User account = (User) userDetails;
        if (account == null) {
            throw new RuntimeException("PASSWORD_NOT_MATCH");
        }

        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        if (details != null) {
            // 根据平台key加载平台信息
            UserDetails clientDetails = userService.loadUserByUsername(account.getUsername());
            if (clientDetails == null) {
                throw new RuntimeException("ExceptionStatus.CLIENT_DETAILS_EMPTY");
            }

        }
        log.debug("Account 【{}】 Checks has been through！", authentication.getName());
    }

    /**
     * 认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 继承AbstractUserDetailsAuthenticationProvider这个类实现这个方法可以实现自定义二次校验规则，第一次是检查账号密码，在这里又做了一次校验
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                () -> messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only UsernamePasswordAuthenticationToken is supported"));
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
                : authentication.getName();

//        User user = (User) userCache.getUserFromCache(username);
        User user = null;
        boolean cacheWasUsed = true;
        if (user == null) { // user 为null 进行第二次校验
            cacheWasUsed = false;

            try {
                user = (User) retrieveUser(username,
                        (UsernamePasswordAuthenticationToken) authentication);
            } catch (UsernameNotFoundException notFound) {
                logger.debug("User '" + username + "' not found");

                if (hideUserNotFoundExceptions) {
                    throw new BadCredentialsException(messages.getMessage(
                            "AbstractUserDetailsAuthenticationProvider.badCredentials",
                            "Bad credentials"));
                } else {
                    throw notFound;
                }
            }

            Assert.notNull(user,
                    "retrieveUser returned null - a violation of the interface contract");
        }

        try {
            super.getPreAuthenticationChecks().check(user);
            additionalAuthenticationChecks(user,
                    (UsernamePasswordAuthenticationToken) authentication);
        } catch (AuthenticationException exception) {
            if (cacheWasUsed) {
                // There was a problem, so try again after checking
                // we're using latest data (i.e. not from the cache)
                cacheWasUsed = false;
                user = (User) retrieveUser(username,
                        (UsernamePasswordAuthenticationToken) authentication);
                super.getPreAuthenticationChecks().check(user);
                additionalAuthenticationChecks(user,
                        (UsernamePasswordAuthenticationToken) authentication);
            } else {
                throw exception;
            }
        }
        super.getPostAuthenticationChecks().check(user);
        if (!cacheWasUsed) {
//            userCache.putUserInCache(user);
        }

        Object principalToReturn = user;

        if (super.isForcePrincipalAsString()) {
            principalToReturn = user.getUsername();
        }

        return createSuccessAuthentication(principalToReturn, authentication, user);
    }

    /**
     * 获取用户
     *
     * @param username
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        prepareTimingAttackProtection();
        try {
            User loadedUser = (User) userService.loadUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (UsernameNotFoundException ex) {
            mitigateAgainstTimingAttack(authentication);
            throw ex;
        } catch (InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPass_word);
        }
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPass_word == null) {
            this.userNotFoundEncodedPass_word = this.passwordEncoder.encode(USER_NOT_FOUND_PASS_WORD);
        }
    }
}
