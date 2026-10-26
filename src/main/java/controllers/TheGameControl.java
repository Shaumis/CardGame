package controllers;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import models.Opponent;
import models.User;
import models.cards.Card;
import models.cards.CardDeck;
import service.MainService;

public class TheGameControl {
	private User activeUser;

	@FXML
	private ImageView trumpCard;

	@FXML
	private ImageView Card1, Card2, Card3, Card4, Card5, Card6, Card7, Card8, Card9, Card10, Card11, Card12, Card13,
			Card14, Card15, Card16, Card17, Card18;

	@FXML
	private Button buttonEnd;
	@FXML
	private Label GameName;

	@FXML
	private ImageView CardPlace1, CardPlace2, CardPlace3, CardPlace4, CardPlace5, CardPlace6;

	@FXML
	private ImageView CardHit1, CardHit2, CardHit3, CardHit4, CardHit5, CardHit6;

	@FXML
	private Label CardRemain;

	@FXML
	private ImageView CardOnDeck1, CardOnDeck2, CardOnDeck3;
	@FXML
	private ImageView TrumpCard;

	private CardDeck deck;
	private User user1;
	private User user2;
	private ArrayList<ImageView> playerCardViews;
	private ArrayList<ImageView> tableCardViews;
	private ArrayList<ImageView> tableHitViews;

	private ArrayList<Card> tableCards = new ArrayList<>();

	private int freeIndexOnTable = 0;
	private Card theFirstCardOnTable = null;
	private boolean isDefendingPhase = false;
	private boolean isBotAttacking = false;
	private boolean playerCanGiveUpDefense = false;
	private boolean playerTurnToAttack = false;

	public void initialize() {
		deck = MainService.gameDeck;
		user1 = MainService.allUsersInGame.get(0);
		user2 = MainService.allUsersInGame.get(1);
		activeUser = user1;
		GameName.setText(user1.getUsername());

		CardOnDeck1.setImage(new Image("/cards/PlayingCardBack.png"));
		CardOnDeck2.setImage(new Image("/cards/PlayingCardBack.png"));
		CardOnDeck3.setImage(new Image("/cards/PlayingCardBack.png"));
		TrumpCard.setImage(new Image("/cards/" + deck.getTrumpCard() + ".png"));

		playerCardViews = new ArrayList<>(Arrays.asList(Card1, Card2, Card3, Card4, Card5, Card6, Card7, Card8, Card9,
				Card10, Card11, Card12, Card13, Card14, Card15, Card16, Card17, Card18));

		tableCardViews = new ArrayList<>(
				Arrays.asList(CardPlace1, CardPlace2, CardPlace3, CardPlace4, CardPlace5, CardPlace6));

		tableHitViews = new ArrayList<>(Arrays.asList(CardHit1, CardHit2, CardHit3, CardHit4, CardHit5, CardHit6));

		drawPlayerHand();
		registerCardClicks();
		buttonEnd.setOnAction(e -> endTurn());
		CardRemain.setText("Atlikušās kārtis: " + deck.getDeck().size());
		playerTurnToAttack = true;
		isBotAttacking = false;
		isDefendingPhase = false;
		activeUser = user1;
		GameName.setText("Tava kārta dot kārtis sist!");
		updateButtonState();
	}

	private void drawPlayerHand() {
		ArrayList<Card> cards = user1.getCurrentGameCardsInHand();
		for (ImageView view : playerCardViews) {
			view.setImage(null);
			view.setUserData(null);
		}
		for (int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);
			ImageView view = playerCardViews.get(i);
			view.setImage(new Image("/cards/" + card.toString() + ".png"));
			view.setUserData(card);
		}

		updateButtonState();
	}

	private void registerCardClicks() {
		for (ImageView view : playerCardViews) {
			view.setOnMouseClicked(e -> {
				if (activeUser != user1)
					return;

				if (isBotAttacking || isDefendingPhase) {
					handlePlayerDefense(view);
				} else {
					handlePlayerAttack(view);
				}
			});
		}
	}

	private void handlePlayerAttack(ImageView view) {
		Card clickedCard = (Card) view.getUserData();
		if (clickedCard == null)
			return;

		boolean canPlace = false;

		if (freeIndexOnTable == 0) {
			canPlace = true;
			theFirstCardOnTable = clickedCard;
		} else {
			if (clickedCard.getValue().equals(theFirstCardOnTable.getValue())) {
				canPlace = true;
			}

			for (int i = 0; i < freeIndexOnTable; i++) {
				Card tableAttack = (Card) tableCardViews.get(i).getUserData();
				Card tableDefend = (Card) tableHitViews.get(i).getUserData();

				if ((tableAttack != null && clickedCard.getValue().equals(tableAttack.getValue()))
						|| (tableDefend != null && clickedCard.getValue().equals(tableDefend.getValue()))) {
					canPlace = true;
					break;
				}
			}
		}

		if (!canPlace) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Nesakrīt kārtis, nevar likt galdā!");
			alert.show();
			return;
		}

		placeCardOnTable(view, clickedCard);

		activeUser = user2;
		isDefendingPhase = true;
		GameName.setText("Pretinieks sit kārtis!");
		updateButtonState();
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(ev -> botDefend());
		pause.play();
	}

	private void handlePlayerDefense(ImageView view) {
		if (freeIndexOnTable == 0 || freeIndexOnTable >= tableCardViews.size())
			return;

		Card clickedCard = (Card) view.getUserData();
		if (clickedCard == null)
			return;

		int defenseIndex = -1;
		for (int i = 0; i < freeIndexOnTable; i++) {
			if (tableCardViews.get(i).getUserData() != null && tableHitViews.get(i).getUserData() == null) {
				defenseIndex = i;
				break;
			}
		}

		if (defenseIndex == -1) {
			return;
		}

		Card attackCard = (Card) tableCardViews.get(defenseIndex).getUserData();

		boolean canDefend = false;
		if (clickedCard.getSuite().equals(attackCard.getSuite())
				&& clickedCard.getValue().getPoint() > attackCard.getValue().getPoint()) {
			canDefend = true;
		} else if (clickedCard.getSuite().equals(deck.getTrumpCard().getSuite())
				&& !attackCard.getSuite().equals(deck.getTrumpCard().getSuite())) {
			canDefend = true;
		} else if (clickedCard.getSuite().equals(deck.getTrumpCard().getSuite())
				&& attackCard.getSuite().equals(deck.getTrumpCard().getSuite())
				&& clickedCard.getValue().getPoint() > attackCard.getValue().getPoint()) {
			canDefend = true;
		}

		if (!canDefend) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Nepareiza kārts!");
			alert.show();
			return;
		}

		ImageView defenseSlot = tableHitViews.get(defenseIndex);
		defenseSlot.setImage(view.getImage());
		defenseSlot.setUserData(clickedCard);

		user1.getCurrentGameCardsInHand().remove(clickedCard);
		view.setImage(null);
		view.setUserData(null);

		boolean allDefended = true;
		for (int i = 0; i < freeIndexOnTable; i++) {
			if (tableCardViews.get(i).getUserData() != null && tableHitViews.get(i).getUserData() == null) {
				allDefended = false;
				break;
			}
		}

		if (allDefended) {
			isDefendingPhase = false;
			playerCanGiveUpDefense = false;
			updateButtonState();

			if (activeUser == user2) {
				activeUser = user2;
				GameName.setText("Pretinieks dod kārtis sist!");
				PauseTransition pause = new PauseTransition(Duration.seconds(1));
				pause.setOnFinished(ev -> botContinueAttack());
				pause.play();
			} else {
				activeUser = user2;
				PauseTransition pause = new PauseTransition(Duration.seconds(1));
				pause.setOnFinished(ev -> botContinueAttack());
				pause.play();
			}
		}
	}

	private void placeCardOnTable(ImageView sourceView, Card card) {
		ImageView tableSlot = tableCardViews.get(freeIndexOnTable);
		tableSlot.setImage(sourceView.getImage());
		tableSlot.setUserData(card);
		tableCards.add(card);
		freeIndexOnTable++;

		user1.getCurrentGameCardsInHand().remove(card);
		sourceView.setImage(null);
		sourceView.setUserData(null);

		updateButtonState();
	}

	private void startBotAttack() {
		isBotAttacking = true;
		playerTurnToAttack = false;
		activeUser = user2;
		GameName.setText("Pretinieks dod sist kārtis!");
		updateButtonState();

		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(ev -> {
			botFirstAttack();
		});
		pause.play();
	}

	private void botFirstAttack() {
		ArrayList<Card> botCards = user2.getCurrentGameCardsInHand();
		ArrayList<Card> tableCardsList = new ArrayList<>();

		for (ImageView slot : tableCardViews) {
			if (slot.getUserData() != null) {
				tableCardsList.add((Card) slot.getUserData());
			}
		}

		for (ImageView slot : tableHitViews) {
			if (slot.getUserData() != null) {
				tableCardsList.add((Card) slot.getUserData());
			}
		}

		Card attackCard = Opponent.chooseAttackCard(botCards, tableCardsList, deck);

		if (attackCard != null) {
			placeBotCardOnTable(attackCard);
			theFirstCardOnTable = attackCard;

			isDefendingPhase = true;
			playerCanGiveUpDefense = true;
			activeUser = user1;
			GameName.setText("Tava kārta sist kārtis!");
			updateButtonState();
		} else {
			completeTurn();
		}
	}

	private void botContinueAttack() {
		ArrayList<Card> botCards = user2.getCurrentGameCardsInHand();
		ArrayList<Card> tableCardsList = new ArrayList<>();

		for (ImageView slot : tableCardViews) {
			if (slot.getUserData() != null) {
				tableCardsList.add((Card) slot.getUserData());
			}
		}

		for (ImageView slot : tableHitViews) {
			if (slot.getUserData() != null) {
				tableCardsList.add((Card) slot.getUserData());
			}
		}

		if (freeIndexOnTable < tableCardViews.size()) {
			Card attackCard = Opponent.chooseAttackCard(botCards, tableCardsList, deck);

			if (attackCard != null) {
				placeBotCardOnTable(attackCard);

				isDefendingPhase = true;
				playerCanGiveUpDefense = true;
				activeUser = user1;
				GameName.setText("Tava kārta sist kārtis!");
				updateButtonState();
			} else {
				completeTurn();
			}
		} else {
			completeTurn();
		}
	}

	private void placeBotCardOnTable(Card card) {
		ImageView tableSlot = tableCardViews.get(freeIndexOnTable);
		tableSlot.setImage(new Image("/cards/" + card.toString() + ".png"));
		tableSlot.setUserData(card);
		tableCards.add(card);
		freeIndexOnTable++;

		user2.getCurrentGameCardsInHand().remove(card);
	}

	private void botDefend() {
		ArrayList<Card> botCards = user2.getCurrentGameCardsInHand();

		for (int i = 0; i < freeIndexOnTable; i++) {
			ImageView attackSlot = tableCardViews.get(i);
			ImageView defendSlot = tableHitViews.get(i);
			Card attackCard = (Card) attackSlot.getUserData();
			if (attackCard == null)
				continue;

			if (defendSlot.getUserData() != null)
				continue;

			Card botDefend = Opponent.playTurn(botCards, attackCard, deck);
			if (botDefend != null) {
				defendSlot.setImage(new Image("/cards/" + botDefend + ".png"));
				defendSlot.setUserData(botDefend);
				botCards.remove(botDefend);
			} else {
				botTakeAllCardsFromTable();
				return;
			}
		}

		isDefendingPhase = false;
		isBotAttacking = false;
		updateButtonState();

		activeUser = user1;
		updateButtonState();
	}

	private void botTakeAllCardsFromTable() {
		ArrayList<Card> botCards = user2.getCurrentGameCardsInHand();

		for (int j = 0; j < freeIndexOnTable; j++) {
			Card attackCard = (Card) tableCardViews.get(j).getUserData();
			if (attackCard != null)
				botCards.add(attackCard);

			Card defenseCard = (Card) tableHitViews.get(j).getUserData();
			if (defenseCard != null)
				botCards.add(defenseCard);

			tableCardViews.get(j).setImage(null);
			tableCardViews.get(j).setUserData(null);
			tableHitViews.get(j).setImage(null);
			tableHitViews.get(j).setUserData(null);
		}
		tableCards.clear();
		freeIndexOnTable = 0;
		theFirstCardOnTable = null;
		isDefendingPhase = false;
		playerCanGiveUpDefense = false;

		playerTurnToAttack = true;
		isBotAttacking = false;

		drawCardsForPlayers();
		drawPlayerHand();

		activeUser = user1;
		GameName.setText("Tava kārta sist kārtis!");
		updateButtonState();

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Pretinieks paņēma kārtis!");
		alert.setHeaderText("Pretinieks paņēma visas kārtis no galda!");
		alert.setContentText("Tava kārta likt kārtis ko sist!");
		alert.show();
	}

	private void playerTakeAllCardsFromTable() {
		ArrayList<Card> playerCards = user1.getCurrentGameCardsInHand();

		for (int j = 0; j < freeIndexOnTable; j++) {
			Card attackCard = (Card) tableCardViews.get(j).getUserData();
			if (attackCard != null)
				playerCards.add(attackCard);

			Card defenseCard = (Card) tableHitViews.get(j).getUserData();
			if (defenseCard != null)
				playerCards.add(defenseCard);

			tableCardViews.get(j).setImage(null);
			tableCardViews.get(j).setUserData(null);
			tableHitViews.get(j).setImage(null);
			tableHitViews.get(j).setUserData(null);
		}
		tableCards.clear();
		freeIndexOnTable = 0;
		theFirstCardOnTable = null;
		isDefendingPhase = false;
		isBotAttacking = false;
		playerCanGiveUpDefense = false;

		playerTurnToAttack = false;

		drawCardsForPlayers();
		drawPlayerHand();

		activeUser = user1;
		GameName.setText(user1.getUsername());
		updateButtonState();

		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(ev -> startBotAttack());
		pause.play();

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Paņēmi kārtis!");
		alert.setHeaderText("Tu paņēmi visas kārtis no galda!");
		alert.setContentText("Tagad pretinieks uzbruks atkal!");
		alert.show();
	}

	private void updateButtonState() {
		if (activeUser != user1) {
			buttonEnd.setDisable(true);
			return;
		}

		if (isDefendingPhase && playerCanGiveUpDefense) {
			buttonEnd.setDisable(false);
		} else if (!isDefendingPhase && !isBotAttacking && playerTurnToAttack) {
			buttonEnd.setDisable(false);
		} else {
			buttonEnd.setDisable(true);
		}
	}

	private void completeTurn() {
		for (ImageView tableSlot : tableCardViews) {
			tableSlot.setImage(null);
			tableSlot.setUserData(null);
		}
		for (ImageView defendSlot : tableHitViews) {
			defendSlot.setImage(null);
			defendSlot.setUserData(null);
		}

		tableCards.clear();
		freeIndexOnTable = 0;
		theFirstCardOnTable = null;
		isDefendingPhase = false;
		isBotAttacking = false;
		playerCanGiveUpDefense = false;

		drawCardsForPlayers();
		drawPlayerHand();

		if (playerTurnToAttack) {
			playerTurnToAttack = false;
			activeUser = user1;
			GameName.setText(user1.getUsername());
			updateButtonState();

			PauseTransition pause = new PauseTransition(Duration.seconds(1));
			pause.setOnFinished(ev -> startBotAttack());
			pause.play();
		} else {
			playerTurnToAttack = true;
			activeUser = user1;
			GameName.setText("Tava kārta dot kārtis ko sist!");
			updateButtonState();
		}

		shouldGameContinue();
	}

	private void endTurn() {
		if (activeUser != user1 && !isBotAttacking)
			return;

		buttonEnd.setDisable(true);

		if (isDefendingPhase && playerCanGiveUpDefense) {
			playerTakeAllCardsFromTable();
		} else {
			completeTurn();
		}

		buttonEnd.setDisable(false);
		CardRemain.setText("Atlikušās kārtis: " + deck.getDeck().size());
		updateButtonState();
	}

	private void drawCardsForPlayers() {
		ArrayList<Card> playerCards = user1.getCurrentGameCardsInHand();
		while (playerCards.size() < 6 && !deck.isEmpty()) {
			playerCards.add(deck.giveMeOneCard());
		}

		ArrayList<Card> botCards = user2.getCurrentGameCardsInHand();
		while (botCards.size() < 6 && !deck.isEmpty()) {
			botCards.add(deck.giveMeOneCard());
		}
	}

	private boolean shouldGameContinue() {
		if (user1.getCurrentGameCardsInHand().isEmpty() || user2.getCurrentGameCardsInHand().isEmpty()) {
			determineWinner();
			return false;
		}
		return true;
	}

	private void determineWinner() {
		String winner;
		if (user1.getCurrentGameCardsInHand().isEmpty() && user2.getCurrentGameCardsInHand().isEmpty()) {
			winner = "Neizšķirts!";
		} else if (user1.getCurrentGameCardsInHand().isEmpty()) {
			winner = "Tu uzvarēji!";
		} else {
			winner = "Pretinieks uzvarēja!";
		}

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Spēle beigusies!");
		alert.setHeaderText(winner);
		alert.setContentText("Spēle pabeigta!");
		alert.show();
	}
}