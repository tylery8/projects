package fanduel;

public enum Position {
	
	QB, RB, WR, TE, K, DST;
	
	public static Position stringToPosition(String s) {
		if (s.equals("D"))
			return DST;
		return valueOf(s);
	}

	public static int positionToInt(Position position) {
		for (int i = 0; i < values().length; i++)
			if (values()[i] == position)
				return i;
		throw new IllegalArgumentException();
	}
}