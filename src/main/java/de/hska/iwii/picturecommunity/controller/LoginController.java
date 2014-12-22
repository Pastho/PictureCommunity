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
 * Created: 10.12.2014
 */

@Component
@Scope("session")
@Qualifier("registerController")
public class LoginController {

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

    // ====================================================
    // End of setter and getter methods
    // ====================================================

    /**
     * Registers an user
     *
     * @param actionEvent
     */
    public void login(ActionEvent actionEvent) {

        User user = null;
        FacesMessage message = null;

        user = userDAO.findUserByName(inputUsername, inputPassword);

        // get user from database if its not an admin
        if (user != null) {

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

                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Wilkommen", inputUsername);
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anmeldefehler", "Ein Fehler ist Aufgetreten.");
            }
        } else {
            if (inputUsername.trim().equals("admin") && inputPassword.trim().equals("admin")) {
                user = new User("admin@admin.com", inputPassword, inputUsername, User.ROLE_ADMIN);

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

                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Willkommen", inputUsername);
                    }
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anmeldefehler", "Admin konnte nicht erstellt werden.");
                }
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehlerhafte Anmeldedaten");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
