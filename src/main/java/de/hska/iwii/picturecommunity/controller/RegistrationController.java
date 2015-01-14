package de.hska.iwii.picturecommunity.controller;

import de.hska.iwii.picturecommunity.backend.dao.UserDAO;
import de.hska.iwii.picturecommunity.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Author: Thomas Pasberg
 * Created: 16.10.2014
 */

@Component
@Scope("session")
@Qualifier("registerController")
public class RegistrationController {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    @Qualifier("org.springframework.security.authenticationManager")
    protected AuthenticationManager authenticationManager;

    private String inputUsername;
    private String inputPassword;
    private String inputEmail;

    // ====================================================
    // Begin of setter and getter methods
    // ====================================================

    public String getInputUsername() {
        return inputUsername;
    }

    public void setInputUsername(String inputUsername) {
        this.inputUsername = inputUsername;
    }

    public String getInputPassword() {
        return inputPassword;
    }

    public void setInputPassword(String inputPassword) {
        this.inputPassword = inputPassword;
    }

    public String getInputEmail() {
        return inputEmail;
    }

    public void setInputEmail(String inputEmail) {
        this.inputEmail = inputEmail;
    }

    // ====================================================
    // End of setter and getter methods
    // ====================================================

    /**
     * Registers an user
     *
     * @param actionEvent
     */
    public void registerUser(ActionEvent actionEvent) {

        // create user
        User user = new User(inputEmail, inputPassword, inputUsername, User.ROLE_USER);
        if (userDAO.createUser(user)) {

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, inputPassword);
            Authentication authUser = authenticationManager.authenticate(token);

            if (authUser.isAuthenticated()) {
                SecurityContext sc = SecurityContextHolder.getContext();
                sc.setAuthentication(authUser);

                // create session
                FacesContext fc = FacesContext.getCurrentInstance();
                ExternalContext ec = fc.getExternalContext();
                ((HttpSession) ec.getSession(true)).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

                // redirect to private zone
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/faces/pages/private/pictureCenter.xhtml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Willkommen " + inputUsername));
            sessionRegistry.registerNewSession(user.getName(), user);
        }
    }

}
