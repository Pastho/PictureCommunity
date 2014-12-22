package de.hska.iwii.picturecommunity.controller;

import de.hska.iwii.picturecommunity.backend.dao.PictureDAO;
import de.hska.iwii.picturecommunity.backend.dao.UserDAO;
import de.hska.iwii.picturecommunity.backend.entities.Picture;
import de.hska.iwii.picturecommunity.backend.entities.User;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: Thomas Pasberg
 * Created: 10.12.2014
 */

@Component
@Scope("session")
@Qualifier("registerController")
public class PictureController {

    private String value;
    private boolean publicPicture;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PictureDAO pictureDAO;

    @Autowired
    @Qualifier("org.springframework.security.authenticationManager")
    protected AuthenticationManager authenticationManager;

    // ====================================================
    // Begin of setter and getter methods
    // ====================================================

    public boolean isPublicPicture() {
        return this.publicPicture;
    }

    public void setPublicPicture(boolean publicPicture) {
        this.publicPicture = publicPicture;
    }

    // ====================================================
    // End of setter and getter methods
    // ====================================================

    /**
     * Returns the user object of the current user
     *
     * @return The user object of the current user
     */
    private User getCurrentUser() {
        User user = null;

        // read user from context
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            // create user object
            user = userDAO.findUserByMailaddress(((User) principal).getEmail());
        }

        return user;
    }

    private byte[] getFileContents(InputStream in) throws IOException {
        byte[] bytes = null;
        // write the inputStream to a FileOutputStream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int read = 0;
        bytes = new byte[1024];

        while ((read = in.read(bytes)) != -1) {
            bos.write(bytes, 0, read);
        }
        bytes = bos.toByteArray();
        in.close();
        in = null;
        bos.flush();
        bos.close();
        bos = null;
        return bytes;
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {

        User user = getCurrentUser();

        System.out.println("User: " + user.getName());

        FacesMessage msg = new FacesMessage("Erfolgreich", event.getFile().getFileName() + " wurde hochgeladen.");
        FacesContext.getCurrentInstance().addMessage(null, msg);

        Picture pic = new Picture();
        pic.setData(getFileContents(event.getFile().getInputstream()));
        pic.setMimeType(event.getFile().getContentType());
        pic.setName(event.getFile().getFileName());
        pic.setPublicVisible(publicPicture);
        pic.setDescription(" " + event.getFile().getFileName());

        pictureDAO.createPicture(user, pic);
    }

    public List<Picture> getImages() {
        return pictureDAO.getPictures(getCurrentUser(), 0, Integer.MAX_VALUE, false);
    }

    public List<Picture> getFriendsImages() {
        List<User> friends = null;
        List<Picture> picturesOfFriends = null;

        User user = getCurrentUser();
        Set<User> friendsOfUser = user.getFriendsOf();
        if (friendsOfUser != null) {
            for (User friend : friendsOfUser) {
                if (picturesOfFriends == null) {
                    picturesOfFriends = pictureDAO.getPictures(friend, 0, Integer.MAX_VALUE, false);
                } else {
                    picturesOfFriends.addAll(pictureDAO.getPictures(friend, 0, Integer.MAX_VALUE, false));
                }
            }
        }

        return picturesOfFriends;
    }

    public List<Picture> getPublicImages() {

        List<Picture> pictures = null;
        List<Map.Entry<User, Long>> entries = userDAO.getMostActiveUsers(Integer.MAX_VALUE);

        for (Map.Entry<User, Long> entry : entries) {
            User user = entry.getKey();

            // collect the public pictures
            if (pictures == null) {
                pictures = pictureDAO.getPictures(user, 0, Integer.MAX_VALUE, true);
            } else {
                pictures.addAll(pictureDAO.getPictures(user, 0, Integer.MAX_VALUE, true));
            }
        }

        return pictures;
    }

    public StreamedContent getImage() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            String picId = context.getExternalContext().getRequestParameterMap().get("id");
            Picture pic = pictureDAO.getPicture(Integer.parseInt(picId));
            return new DefaultStreamedContent(new ByteArrayInputStream(pic.getData()));
        }
    }

    /**
     * Meldung mit Wert auslesen.
     *
     * @return Meldung
     */
    public String getMessage() {
        return "Hallo mit Wert " + value;
    }

    /**
     * Neuen Wert speichern.
     */
    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Simuliertes Speichern des Textes.
     */
    public Object update() {
        return null;
    }
}
