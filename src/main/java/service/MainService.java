package service;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import models.GameState;
import models.User;
import models.cards.Card;
import models.cards.CardDeck;
import javafx.scene.Scene;

public class MainService extends Application {

	public static GameState state = new GameState();

	public static ArrayList<User> allUsersInGame = new ArrayList<User>();

	public static CardDeck gameDeck;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = FXMLLoader.load(getClass().getResource("/views/UserNameStart.fxml"));
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static Card retrieveCardByTitle(int userIndex, String cardTitle) {
		ArrayList<Card> allCardsForUserIndex = allUsersInGame.get(userIndex).getCurrentGameCardsInHand();

		for (Card tempC : allCardsForUserIndex) {
			if (tempC.toString().equals(cardTitle)) {
				return tempC;
			}
		}

		return null;
	}
}