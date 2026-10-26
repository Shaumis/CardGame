package models.cards;

public class Card {
	private CardSuite suite;
	private CardValue value;
	private byte numericValue;

	public CardSuite getSuite() {
		return suite;
	}

	public CardValue getValue() {
		return value;
	}

	public byte getNumericValue() {
		return numericValue;
	}

	public void setValue(CardValue value) {
		this.value = value;
	}

	public void setSuite(CardSuite suite) {
		this.suite = suite;
	}

	public void setNumericValue(byte numericValue) {
		this.numericValue = numericValue;
	}

	public Card() {
		setSuite(CardSuite.Diamonds);
		setValue(CardValue.Queen);
		setNumericValue((byte) 12);
	}

	public Card(CardSuite inputCardSuite, CardValue inputCardValue) {
		setSuite(inputCardSuite);
		setValue(inputCardValue);
		setNumericValue((byte) inputCardValue.point);
	}

	public String toString() {
		String result = value + "_" + suite;
		return result;
	}
}