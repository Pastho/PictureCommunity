package de.hska.iwii.picturecommunity.controller;

import de.hska.iwii.picturecommunity.backend.dao.UserDAO;
import de.hska.iwii.picturecommunity.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: Thomas Pasberg
 * Created: 14.01.2015
 */


@Component
@Scope("session")
@Qualifier("registerController")
public class LogoutController extends SimpleUrlLogoutSuccessHandler {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    @Qualifier("org.springframework.security.authenticationManager")
    protected AuthenticationManager authenticationManager;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User lazyUser = userDAO.findUserByMailaddress(((User) principal).getEmail());
            if (lazyUser != null) {
                sessionRegistry.removeSessionInformation(lazyUser.getName());
            }
        }
        super.onLogoutSuccess(request, response, authentication);
    }
}
