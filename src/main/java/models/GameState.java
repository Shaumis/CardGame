package models;

public class GameState {
	public enum TurnRole {
		ATTACK, DEFEND
	}

	private TurnRole currentRole;

	public TurnRole getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(TurnRole currentRole) {
		this.currentRole = currentRole;
	}

	public void switchTurn() {
		if (currentRole == null) {
			currentRole = TurnRole.ATTACK;
		} else if (currentRole == TurnRole.ATTACK) {
			currentRole = TurnRole.DEFEND;
		} else {
			currentRole = TurnRole.ATTACK;
		}
	}
}