package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FollowingController implements Initializable {

    NeoDriver neo4j = NeoDriver.getInstance();

    @FXML private VBox followingInfoPane;
    @FXML private Label totalFollowing;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String usernameLogged = DataSession.getUserLogged();
        ArrayList<String> followers = neo4j.getFollowing(usernameLogged);

        int count=0;
        Font font = Font.font("Comic Sans", FontWeight.BOLD, 18);
        if(!followers.isEmpty() || !(followers==null) ) {
            for (String follow : followers) {
                HBox hb = new HBox();

                Button userFollow = new Button();
                userFollow.setText(follow);
                userFollow.setFont(font);
                userFollow.setOnAction(actionEvent -> {
                    DataSession.setUserView(follow);
                    Utils.changeScene("profile-user.fxml", actionEvent);
                });

                Button btnDelete = new Button();
                btnDelete.setText("delete follow");
                btnDelete.setPadding(new Insets(5, 5, 5, 5));
                btnDelete.setFont(font);
                btnDelete.setOnAction(actionEvent -> {
                    if(neo4j.deleteFollows(usernameLogged,follow))
                        Utils.changeScene("following-page.fxml", actionEvent);
                });

                hb.getChildren().addAll(userFollow, btnDelete);
                followingInfoPane.getChildren().add(hb);
                count++;
            }
        }
        totalFollowing.setText(String.valueOf(count));
    }
}