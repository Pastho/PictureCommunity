package de.hska.iwii.picturecommunity.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


/**
 * Author: Thomas Pasberg
 * Created: 16.10.2014
 */

@Component
@Scope("session")
@Qualifier("registerController")
public class RegistrationController {

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
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Welcome " + inputUsername + " !"));
    }

}
