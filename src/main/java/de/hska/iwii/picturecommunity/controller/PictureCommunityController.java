package de.hska.iwii.picturecommunity.controller;

import de.hska.iwii.picturecommunity.backend.dao.PictureDAO;
import de.hska.iwii.picturecommunity.backend.dao.UserDAO;
import de.hska.iwii.picturecommunity.backend.entities.Picture;
import de.hska.iwii.picturecommunity.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Thomas Pasberg
 * Created: 16.10.2014
 */

@Component
@Scope("session")
@Qualifier("pictureCommunityController")
public class PictureCommunityController {


    // gespeicherter Wert
    private String value;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PictureDAO pictureDAO;

    @Autowired
    @Qualifier("org.springframework.security.authenticationManager")
    protected AuthenticationManager authenticationManager;

    @PostConstruct
    public void wakeup() {

        Logger logger = Logger.getLogger(this.getClass().getName());

        logger.log(Level.INFO, "Log object initialized");

        // Get all public pictures from user
        List<Map.Entry<User, Long>> users = userDAO.getMostActiveUsers(1000);

        if (!users.isEmpty()) {
            logger.log(Level.INFO, "Amount of users" + users.size());
        } else {
            logger.log(Level.INFO, "No users were found in database");
        }

    }

    public List<Picture> getImages() {
        User user = userDAO.findUserByMailaddress("holger.vogelsang1@web.de");
        return pictureDAO.getPictures(user, 0, Integer.MAX_VALUE, false);
    }

}
