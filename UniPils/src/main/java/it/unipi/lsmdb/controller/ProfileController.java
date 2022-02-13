package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Review;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    NeoDriver neo4j = NeoDriver.getInstance();

    @FXML private VBox profilePage;
    @FXML private AnchorPane userDataPane;
    @FXML private VBox beersInfoPane;

    @FXML private Label userLabel;
    @FXML private Label firstLabel;
    @FXML private Label lastLabel;
    @FXML private Label emailLabel;
    @FXML private Label cellLabel;
    @FXML private Label genderLabel;
    @FXML private Label addressLabel;
    @FXML private ScrollPane scroll;
    @FXML private Button addDelButton;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String usernameLogged;

        if(DataSession.getUserView()==null)
            usernameLogged=DataSession.getUserLogged();
        else
            usernameLogged=DataSession.getUserView();

        /*if(Objects.equals(usernameLogged, "admin")){
            Button cancel = new Button();
            cancel.setText("DELETE BEER");
            cancel.setOnAction(e -> deleteUser(e, beer_id));
        }*/

        User user= MongoDriver.getUserFromUsername(usernameLogged);
        Font font_u = Font.font("Comic Sans", FontWeight.BOLD,  25);

        userLabel.setText(user.getUsername());
        userLabel.setFont(font_u);
        firstLabel.setText(user.getFirst());
        lastLabel.setText(user.getLast());
        emailLabel.setText(user.getEmail());
        cellLabel.setText(user.getCell());
        genderLabel.setText(user.getGender());
        //addressLabel.setText(user.getAddress());

        if(DataSession.getUserView()!=null){
            if(neo4j.getFollower(DataSession.getUserLogged(),DataSession.getUserView()).isEmpty()){
                addDelButton.setText("Add follower");
                addDelButton.setOnAction(actionEvent -> {
                    if(neo4j.addFollows(DataSession.getUserLogged(),DataSession.getUserView())){
                        Utils.changeScene("profile-user.fxml", actionEvent);
                    }
                });

            }else{
                addDelButton.setText("Delete follower");
                addDelButton.setOnAction(actionEvent -> {
                    if(neo4j.deleteFollows(DataSession.getUserLogged(),DataSession.getUserView())){
                        Utils.changeScene("profile-user.fxml", actionEvent);
                    }

                });
            }
        }else{
            addDelButton.setVisible(false);
        }

        ArrayList<Beer> beers = neo4j.getBeersUser(usernameLogged);

        System.out.println(beers);
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        for (Beer b : beers) {
            AnchorPane oneBeer = new AnchorPane();
            oneBeer.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 1;" +"-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;");
            VBox boxBeer = new VBox();
            boxBeer.setMaxWidth(491);
            boxBeer.setPadding(new Insets(5,5,5,5));

            HBox hb=new HBox();

            Label nameBeer = new Label();
            nameBeer.setText("Beer:  ");
            nameBeer.setFont(font);

            Button btn=new Button();
            btn.setText(b.getName());
            btn.setPadding(new Insets(5,5,5,5));
            btn.setFont(font);
            btn.setOnAction(actionEvent -> {
                DataSession.setIdBeerToShow(b.get_id());
                Utils.changeScene("profile-beer.fxml", actionEvent);});

            hb.getChildren().addAll(nameBeer,btn);


            Label style = new Label();
            style.setText("Style:  " + b.getStyle());
            style.setFont(font);

            Label brewName = new Label();
            brewName.setText("Brewery:  " + b.getBrewery_name());
            brewName.setFont(font);

            ArrayList<Review> reviews=neo4j.getReviewsUser(usernameLogged,b.get_id());
            TextArea comment = new TextArea();
            Label score = new Label();
            score.setFont(font);

            if(!reviews.isEmpty()){
                score.setText("Score:  " + reviews.get(0).getScore());
                comment.setText("Review:  " + reviews.get(0).getComment());
                comment.setFont(font);
                comment.setDisable(true);
            }else{
                score.setText("No score");
                comment.setText("No comment");
                comment.setFont(font);
                comment.setDisable(true);
            }

            Separator sep=new Separator();

            boxBeer.getChildren().addAll(hb, style,brewName,score,comment);
            oneBeer.getChildren().addAll(boxBeer,sep);
            beersInfoPane.getChildren().add(oneBeer);
        }
    }

    @FXML private void onClickFollower(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("follower-page.fxml", ae);

    }

    @FXML private void onClickFollowing(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("following-page.fxml", ae);

    }



}
