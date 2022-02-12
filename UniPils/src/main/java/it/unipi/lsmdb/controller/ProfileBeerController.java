package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Review;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProfileBeerController implements Initializable {

    @FXML private TextField searchBar;
    @FXML private Button wishButton;
    @FXML private Button revButton;
    @FXML private Button cartButton;
    @FXML private VBox revSection;
    @FXML private AnchorPane beerInfoPane;
    @FXML TextField score;
    @FXML TextArea comment;
    @FXML Label beerName;
    @FXML Label brewName;
    @FXML Label abv;
    @FXML Label vol;
    @FXML Label style;
    @FXML Label price;
    @FXML Label country;
    @FXML Label state;
    @FXML ScrollPane scroll;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //manca da fare la barra di ricerca tramite query su neo4j che setta l'id della birra desiderata
        //manca da sistemare il cambio bottone ADD/REMOVE wish
        //manca da vietare l'inserimento di una seconda recensione sulla stessa birra da parte dello stesso utente
        //manca da sistemare lo scroll sostituendo un pane con uno scroll pane
        int beer_id = DataSession.getIdBeerToShow();
        Beer beer = MongoDriver.getBeerById(beer_id);
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  25);

        beerName.setText(beer.getName());
        beerName.setFont(font);
        brewName.setText("Brewery:  " + beer.getBrewery_name());
        style.setText("Style: " + beer.getStyle());
        abv.setText("ABV: " + beer.getAbv() + " %");
        price.setText("Price: " + beer.getPrice() + " USD");
        vol.setText("Vol. :" + beer.getVolume() + " cl");
        country.setText("Country: " + beer.getCountry());
        state.setText("State: " + beer.getState());

        if(Objects.equals(DataSession.getUserLogged(), "admin")) {
            Button cancel = new Button();
            cancel.setText("DELETE BEER");
            cancel.setOnAction(e -> deleteBeer(e, beer_id));
        }

        showBeerReviews(beer_id);

        if (DataSession.getUserLogged() != null){
            String usernameLogged = DataSession.getUserLogged();
            if(Objects.equals(wishButton.getText(), "ADD TO WISHLIST")) {
                wishButton.setOnAction(e -> addWishlist(e, usernameLogged, beer_id));
            }
            else
                wishButton.setOnAction(e->deleteWishlist(e, usernameLogged, beer_id));
            //cartButton.setOnAction();
            if(Objects.equals(revButton.getText(), "POST REVIEW"))
                revButton.setOnAction(e->writeReview(e, usernameLogged, beer_id));
            else
                revButton.setOnAction(e->modifyReview(e,usernameLogged,beer_id));
        }
        else
        {
            wishButton.setVisible(false);
            revButton.setVisible(false);
            cartButton.setVisible(false);
        }
    }

    private void showBeerReviews(int beer) {

        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        NeoDriver neo4j = NeoDriver.getInstance();
        ArrayList<String> authors = neo4j.getAuthorReview(beer);
        ArrayList<Review> reviews= neo4j.getBeerReviews(beer);

        for(int i=0, j=0 ; i< reviews.size() && j< authors.size() ; i++, j++){
                double space = 5;
                VBox rev = new VBox(space);
                rev.setMaxWidth(491);
                rev.setStyle("-fx-border-style: solid inside;"
                        + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                        + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

                Label author = new Label();
                author.setText("Publisher:  " + authors.get(j));
                author.setFont(font);

                Label comment = new Label();
                comment.setText("Text:  " + reviews.get(i).getComment());
                comment.setFont(font);

                Label score = new Label();
                score.setText("Score:  " + reviews.get(i).getSc());
                score.setFont(font);

                Label ts = new Label();
                ts.setText("Publication date:  " + reviews.get(i).getTs());
                ts.setFont(font);

                rev.getChildren().addAll(author,comment, score, ts); //attacco le label alla sezione della singola review
                revSection.getChildren().add(rev); //attacco la singola review al vbox globale
            }

    }

    private void writeReview(ActionEvent actionEvent, String usernameLogged, int beer) {
        if(Objects.equals(comment.getText(), "") || Objects.equals(score.getText(), "")) {
            Utils.showErrorAlert("You need to compile both fields");
            return;
        }
        int num = Integer.parseInt(score.getText());
        if(num > 10 || num < 1 ){
            Utils.showErrorAlert("You need to insert a valid score (0 < sc < 11)");
            return;
        }
        Review review = new Review(comment.getText(), Integer.parseInt(score.getText()));
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.addReview(review, usernameLogged, beer);
        //comment.setText(writtenReview.getComment());
        //score.setText(String.valueOf(writtenReview.getScore()));
        revButton.setText("MODIFY REVIEW");
        Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", actionEvent);
    }

    private void modifyReview(ActionEvent actionEvent, String usernameLogged, int beer) {
        if(comment.getText() == null || score.getText() == null) {
            Utils.showErrorAlert("You need to compile both fields");
            return;
        }
        Review review = new Review(comment.getText(), Integer.parseInt(score.getText()));
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.updateReview(review, usernameLogged, beer);
        Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", actionEvent);
    }

    @FXML
    private void addWishlist(ActionEvent actionEvent, String user, int beer) {
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.addHasInWishlist(user, beer);
        Utils.showInfoAlert("Added to wishlist");
        wishButton.setText("REMOVE FROM WISHLIST");
        //revSection.prefHeight(227.1);
        Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", actionEvent);
    }

    @FXML
    private void deleteWishlist(ActionEvent actionEvent, String user, int beer) {
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.deleteHasInWishlist(user, beer);
        Utils.showInfoAlert("Deleted from wishlist");
        wishButton.setText("ADD TO WISHLIST");
        //revSection.prefHeight(227.1);
        Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", actionEvent);
    }

    @FXML
    private void scroll(){
        scroll.setFitToWidth(true);
    }

    @FXML
    public static void deleteBeer(ActionEvent ae, int beerId){
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.deleteBeer(beerId);
        MongoDriver.deleteBeer(beerId);
        Utils.showInfoAlert("Beer " + beerId + " deleted from both DB");
        Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", ae);
    }


}