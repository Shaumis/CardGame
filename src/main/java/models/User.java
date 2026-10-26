package models;

import java.util.ArrayList;
import models.cards.Card;
import models.cards.CardDeck;

public class User {
	private String username;
	private ArrayList<Card> currentGameCardsInHand = new ArrayList<Card>();

	public String getUsername() {
		return username;
	}

	public ArrayList<Card> getCurrentGameCardsInHand() {
		return currentGameCardsInHand;
	}

	public void setUsername(String inputUsername) {
		username = inputUsername;
	}

	public void drawInHand(CardDeck deck, int numberOfCards) {
		for (int i = 0; i < numberOfCards; i++) {
			currentGameCardsInHand.add(deck.giveMeOneCard());
		}
	}

	public User() {
		setUsername("testa lietotajs");
	}

	public User(String inputUsername, CardDeck deck) {
		setUsername(inputUsername);
		drawInHand(deck, 6);
	}

	@Override
	public String toString() {
		String result = username + "  Hand: " + currentGameCardsInHand;
		return result;
	}
}
