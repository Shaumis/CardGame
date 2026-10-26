package models.cards;

import java.util.ArrayList;
import java.util.Random;

import models.User;

public class CardDeck {
    private ArrayList<Card> deck = new ArrayList<Card>();

    public ArrayList<Card> getDeck() {
        return deck;
    }

    private Card trumpCard;

    public Card getTrumpCard() {
        return trumpCard;
    }

    public CardDeck() {
        for (CardSuite tempS : CardSuite.values()) {
            for (CardValue tempV : CardValue.values()) {
                Card newCard = new Card(tempS, tempV);
                deck.add(newCard);
            }
        }

        shuffle();
        trumpCard = deck.remove(0);
    }

    public void shuffle() {
        Random myRandom = new Random();
        for (int i = 0; i < 400; i++) {
            int cardIndex = myRandom.nextInt(deck.size());
            Card tempCard = deck.remove(cardIndex);
            deck.add(tempCard);
        }
    }

    public Card giveMeOneCard() {
    	Card tempCard = deck.remove(0);
		return tempCard;
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    public void dealCards(User user, int amount) {
        for (int i = 0; i < amount && !deck.isEmpty(); i++) {
            user.getCurrentGameCardsInHand().add(giveMeOneCard());
        }
    }
}