package models;

import java.util.ArrayList;
import java.util.List;

import models.cards.Card;
import models.cards.CardDeck;

public class Opponent {

	public static Card playTurn(List<Card> hand, Card playerCard, CardDeck deck) {

		Card smallestSameSuit = null;
		Card smallestTrump = null;

		for (Card card : hand) {

			if (card.getSuite().equals(playerCard.getSuite())
					&& card.getValue().getPoint() > playerCard.getValue().getPoint()) {

				if (smallestSameSuit == null || card.getValue().getPoint() < smallestSameSuit.getValue().getPoint()) {
					smallestSameSuit = card;
				}
			}

			if (card.getSuite().equals(deck.getTrumpCard().getSuite())) {
				if (playerCard.getSuite().equals(deck.getTrumpCard().getSuite())) {
					if (card.getValue().getPoint() > playerCard.getValue().getPoint()) {
						if (smallestTrump == null || card.getValue().getPoint() < smallestTrump.getValue().getPoint()) {
							smallestTrump = card;
						}
					}
				} else {
					if (smallestTrump == null || card.getValue().getPoint() < smallestTrump.getValue().getPoint()) {
						smallestTrump = card;
					}
				}
			}
		}

		Card chosen = null;
		if (smallestSameSuit != null) {
			chosen = smallestSameSuit;
		} else if (smallestTrump != null) {
			chosen = smallestTrump;
		}

		if (chosen == null) {
			System.out.println("Pretiniekam nav piemērotas kārtis. Pretinieks nevar spēlēt un ņemt kārtis.");
			return null;
		}
		return chosen;

	}

	public static Card chooseAttackCard(ArrayList<Card> botCards, ArrayList<Card> tableCards, CardDeck deck) {
		if (tableCards.isEmpty()) {
			return getSmallestCard(botCards);
		}

		List<Integer> allowedRanks = new ArrayList<>();
		for (Card tableCard : tableCards) {
			int rankPoint = tableCard.getValue().getPoint();
			if (!allowedRanks.contains(rankPoint)) {
				allowedRanks.add(rankPoint);
			}
		}

		List<Card> matchingCards = new ArrayList<>();
		for (Card card : botCards) {
			if (allowedRanks.contains(card.getValue().getPoint())) {
				matchingCards.add(card);
			}
		}

		if (!matchingCards.isEmpty()) {
			return getSmallestCardFromList(matchingCards);
		}

		return null;
	}

	private static Card getSmallestCard(ArrayList<Card> hand) {
		if (hand.isEmpty())
			return null;

		Card smallest = hand.get(0);
		for (Card card : hand) {
			if (card.getValue().getPoint() < smallest.getValue().getPoint()) {
				smallest = card;
			}
		}
		return smallest;
	}

	private static Card getSmallestCardFromList(List<Card> cards) {
		if (cards.isEmpty())
			return null;

		Card smallest = cards.get(0);
		for (Card card : cards) {
			if (card.getValue().getPoint() < smallest.getValue().getPoint()) {
				smallest = card;
			}
		}
		return smallest;
	}
}