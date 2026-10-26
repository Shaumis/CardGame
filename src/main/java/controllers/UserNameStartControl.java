package controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import models.cards.CardDeck;
import service.MainService;

public class UserNameStartControl {

	@FXML
	private TextField textField1;
	@FXML
	private Button button1;

	@FXML
	public void clickToStartGame() throws IOException {
		String username = textField1.getText();
		if (username.isEmpty())
			return;
		CardDeck deck = new CardDeck();
		MainService.gameDeck = deck;
		User mainUser = new User(username, deck);
		User botUser = new User("Bot", deck);
		mainUser.drawInHand(deck, 0);
		botUser.drawInHand(deck, 0);
		MainService.allUsersInGame.add(mainUser);
		MainService.allUsersInGame.add(botUser);
		Stage myStage = (Stage) button1.getScene().getWindow();
		Scene theGameScene = FXMLLoader.load(getClass().getResource("/views/TheGameUpdate.fxml"));
		myStage.setScene(theGameScene);
	}
}