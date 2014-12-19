package de.hska.iwii.picturecommunity.controller;

import de.hska.iwii.picturecommunity.backend.dao.PictureDAO;
import de.hska.iwii.picturecommunity.backend.dao.UserDAO;
import de.hska.iwii.picturecommunity.backend.entities.User;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Author: Thomas Pasberg
 * Created: 10.12.2014
 */

@Component
@Scope("session")
@Qualifier("registerController")
public class AdminController {

    private BarChartModel barModel;
    private String searchString;
    private String resultText;

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

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getResult() {
        if (!searchString.isEmpty()) {
            User user = userDAO.findUserByName(searchString);

            if (user != null) {
                userDAO.deleteUser(user);
                return "Der Benutzer " + searchString + " wurde inkl. seiner Bilder gelöscht";
            } else {
                return "Der Benutzer konnte nicht gefunden werden";
            }
        } else {
            return "Die Suche konnten nicht ausgeführt werden.";
        }
    }

    public void setResult(String result) {
        this.resultText = result;
    }

    public String getResultText() {
        return this.resultText;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    // ====================================================
    // End of setter and getter methods
    // ====================================================

    @PostConstruct
    public void init() {
        createBarModels();
    }

    private BarChartModel initBarModel() {

        long pictureAmount;

        ChartSeries userChartSeries = new ChartSeries();
        BarChartModel model = new BarChartModel();
        List<Map.Entry<User, Long>> mostActiveUser = userDAO.getMostActiveUsers(10);

        userChartSeries.setLabel("Anzahl Bilder");

        for (Map.Entry<User, Long> entry : mostActiveUser) {
            User user = entry.getKey();

            // count the pictures of user and set the result
            pictureAmount = pictureDAO.getPictureCount(user);
            userChartSeries.set(user.getUsername(), pictureAmount);
        }

        model.addSeries(userChartSeries);

        return model;
    }

    private void createBarModels() {
        createBarModel();
    }

    private void createBarModel() {
        barModel = initBarModel();

        barModel.setTitle("Die 10 aktivsten Benutzer");
        barModel.setLegendPosition("ne");

        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Benutzer");

        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Bilder");
        yAxis.setMin(0);
        yAxis.setMax(20);
    }
}
