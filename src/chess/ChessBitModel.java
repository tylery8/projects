package chess;

import java.util.ArrayList;
import java.util.List;

public class ChessBitModel implements TwoPlayerAi {
	
	private int _turn;
	private long _castle;
	private long _last_from;
	private long _last_to;
	private long _P, _N, _B, _R, _Q, _K;
	private long _p, _n, _b, _r, _q, _k;
	private long _white;
	private long _black;
	private long _open;
	private boolean _not_capture;
	
	private static List<ChessBitModel> _history = new ArrayList<ChessBitModel>();
	
	private static final long[] RANK = new long[] {
			0b0000000000000000000000000000000000000000000000000000000011111111L,
			0b0000000000000000000000000000000000000000000000001111111100000000L,
			0b0000000000000000000000000000000000000000111111110000000000000000L,
			0b0000000000000000000000000000000011111111000000000000000000000000L,
			0b0000000000000000000000001111111100000000000000000000000000000000L,
			0b0000000000000000111111110000000000000000000000000000000000000000L,
			0b0000000011111111000000000000000000000000000000000000000000000000L,
			0b1111111100000000000000000000000000000000000000000000000000000000L
			};

	private static final long[] FILE = new long[] {
			0b0000000100000001000000010000000100000001000000010000000100000001L,
			0b0000001000000010000000100000001000000010000000100000001000000010L,
			0b0000010000000100000001000000010000000100000001000000010000000100L,
			0b0000100000001000000010000000100000001000000010000000100000001000L,
			0b0001000000010000000100000001000000010000000100000001000000010000L,
			0b0010000000100000001000000010000000100000001000000010000000100000L,
			0b0100000001000000010000000100000001000000010000000100000001000000L,
			0b1000000010000000100000001000000010000000100000001000000010000000L
			};
	
	static public final double[][] PAWN_OPENING = new double[][] {
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, -.10, 0.20, 0.20, -.15, 0.00, 0.00},
		new double[] {0.00, -.10, 0.15, 0.30, 0.30, -.15, -.10, 0.00},
		new double[] {0.05, 0.05, 0.00, 0.10, 0.10, -.10, 0.05, 0.05},
		new double[] {0.00, 0.10, 0.10, -.60, -.60, 0.10, 0.10, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
	};
	
	static public final double[][] PAWN_MIDDLE = new double[][] {
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.20, 0.20, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.05, 0.30, 0.30, 0.00, 0.00, 0.00},
		new double[] {0.05, 0.00, -.10, 0.10, 0.10, -.10, 0.00, 0.05},
		new double[] {0.00, 0.10, 0.10, -.60, -.60, 0.10, 0.10, -.05},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
	};
	
	static public final double[][] PAWN_END = new double[][] {
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {1.00, 1.20, 1.20, 1.20, 1.20, 1.20, 1.20, 1.00},
		new double[] {0.40, 0.50, 0.50, 0.50, 0.50, 0.50, 0.50, 0.40},
		new double[] {0.10, 0.15, 0.15, 0.15, 0.15, 0.15, 0.15, 0.10},
		new double[] {0.00, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
	};
	
	static public final double[][] KNIGHT_OPENING = new double[][] {
		new double[] {-.50, -.40, -.30, -.30, -.30, -.30, -.40, -.50},
		new double[] {-.40, -.20, 0.00, 0.00, 0.00, 0.00, -.20, -.40},
		new double[] {-.30, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, -.30},
		new double[] {-.20, 0.00, 0.05, 0.05, 0.05, 0.05, 0.00, -.20},
		new double[] {-.20, 0.00, 0.15, 0.05, 0.05, 0.15, 0.00, -.20},
		new double[] {-.30, 0.05, 0.15, 0.10, 0.10, 0.15, 0.05, -.30},
		new double[] {-.40, -.10, 0.05, 0.10, 0.10, 0.05, -.10, -.40},
		new double[] {-.50, -.40, -.30, -.30, -.30, -.30, -.40, -.50},
	};
	
	static public final double[][] KNIGHT_MIDDLE = new double[][] {
		new double[] {-.50, -.40, -.30, -.30, -.30, -.30, -.40, -.50},
		new double[] {-.40, -.20, 0.00, 0.00, 0.00, 0.00, -.20, -.40},
		new double[] {-.30, 0.00, 0.10, 0.15, 0.15, 0.10, 0.00, -.30},
		new double[] {-.30, 0.05, 0.15, 0.20, 0.20, 0.15, 0.05, -.30},
		new double[] {-.30, 0.00, 0.15, 0.20, 0.20, 0.15, 0.00, -.30},
		new double[] {-.30, 0.05, 0.10, 0.15, 0.15, 0.10, 0.05, -.30},
		new double[] {-.40, -.20, 0.00, 0.05, 0.05, 0.00, -.20, -.40},
		new double[] {-.50, -.40, -.30, -.30, -.30, -.30, -.40, -.50},
	};
	
	static public final double[][] KNIGHT_END = new double[][] {
		new double[] {-.10, -.10, 0.00, 0.00, 0.00, 0.00, -.10, -.10},
		new double[] {-.10, -.05, 0.00, 0.05, 0.05, 0.00, -.05, -.10},
		new double[] {0.00, 0.00, 0.05, 0.10, 0.10, 0.05, 0.00, 0.00},
		new double[] {0.00, 0.05, 0.10, 0.15, 0.15, 0.10, 0.05, 0.00},
		new double[] {0.00, 0.05, 0.10, 0.15, 0.15, 0.10, 0.05, 0.00},
		new double[] {0.00, 0.00, 0.05, 0.10, 0.10, 0.05, 0.00, 0.00},
		new double[] {-.10, -.05, 0.00, 0.05, 0.05, 0.00, -.05, -.10},
		new double[] {-.10, -.10, 0.00, 0.00, 0.00, 0.00, -.10, -.10},
	};
	
	static public final double[][] BISHOP_OPENING = new double[][] {
		new double[] {-.20, -.10, -.10, -.10, -.10, -.10, -.10, -.20},
		new double[] {-.10, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, -.10},
		new double[] {-.10, 0.00, 0.05, 0.10, 0.10, 0.05, 0.00, -.10},
		new double[] {-.10, 0.05, 0.05, 0.10, 0.10, 0.05, 0.05, -.10},
		new double[] {-.10, 0.00, 0.15, 0.10, 0.10, 0.15, 0.00, -.10},
		new double[] {-.10, 0.10, 0.10, 0.10, 0.10, 0.10, 0.10, -.10},
		new double[] {-.10, 0.10, 0.00, 0.15, 0.15, 0.00, 0.10, -.10},
		new double[] {-.25, -.25, -.25, -.20, -.20, -.25, -.25, -.25},
	};
	
	static public final double[][] BISHOP_MIDDLE = new double[][] {
		new double[] {-.10, -.10, 0.00, 0.00, 0.00, 0.00, -.10, -.10},
		new double[] {-.10, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, -.10},
		new double[] {0.00, 0.05, 0.10, 0.10, 0.10, 0.10, 0.05, 0.00},
		new double[] {0.00, 0.05, 0.10, 0.15, 0.15, 0.10, 0.05, 0.00},
		new double[] {0.00, 0.05, 0.10, 0.15, 0.15, 0.10, 0.05, 0.00},
		new double[] {0.00, 0.05, 0.10, 0.10, 0.10, 0.10, 0.05, 0.00},
		new double[] {-.10, 0.05, 0.00, 0.00, 0.00, 0.00, 0.05, -.10},
		new double[] {-.10, -.10, 0.00, 0.00, 0.00, 0.00, -.10, -.10},
	};
	
	static public final double[][] BISHOP_END = new double[][] {
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
	};
	
	static public final double[][] ROOK_OPENING = new double[][] {
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {-.10, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, -.10},
		new double[] {0.00, -.05, 0.00, 0.05, 0.05, 0.00, -.05, 0.00},
	};
	
	static public final double[][] ROOK_MIDDLE = new double[][] {
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {-.10, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, -.10},
		new double[] {0.00, 0.00, 0.05, 0.10, 0.10, 0.05, 0.00, 0.00},
	};
	
	static public final double[][] ROOK_END = new double[][] {
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
		new double[] {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
	};
	
	static public final double[][] QUEEN_OPENING = new double[][] {
		new double[] {-.20, -.20, -.20, -.20, -.20, -.20, -.20, -.20},
		new double[] {-.20, -.20, -.20, -.20, -.20, -.20, -.20, -.20},
		new double[] {-.20, -.20, -.20, -.20, -.20, -.20, -.20, -.20},
		new double[] {-.10, -.20, -.20, -.10, -.10, -.20, -.20, -.10},
		new double[] {-.10, 0.00, 0.00, 0.00, 0.00, 0.00, -.10, -.10},
		new double[] {-.10, 0.00, 0.00, 0.05, 0.00, 0.00, -.10, -.10},
		new double[] {-.10, 0.00, 0.05, 0.05, 0.05, 0.00, -.10, -.10},
		new double[] {-.20, 0.00, 0.05, 0.15, 0.05, 0.00, -.10, -.20},
	};
	
	static public final double[][] QUEEN_MIDDLE = new double[][] {
		new double[] {-.20, -.10, -.10, -.05, -.05, -.10, -.10, -.20},
		new double[] {-.10, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, -.10},
		new double[] {-.10, 0.00, 0.05, 0.05, 0.05, 0.05, 0.00, -.10},
		new double[] {-.05, 0.00, 0.05, 0.05, 0.05, 0.05, 0.00, -.05},
		new double[] {0.00, 0.00, 0.05, 0.05, 0.05, 0.05, 0.00, -.05},
		new double[] {-.10, 0.05, 0.05, 0.05, 0.05, 0.05, 0.00, -.10},
		new double[] {-.10, 0.00, 0.05, 0.00, 0.00, 0.00, 0.00, -.10},
		new double[] {-.20, -.10, -.10, -.05, -.05, -.10, -.10, -.20},
	};
	
	static public final double[][] QUEEN_END = new double[][] {
		new double[] {-.30, -.20, -.10, -.10, -.10, -.10, -.20, -.30},
		new double[] {-.20, -.10, 0.00, 0.05, 0.05, 0.00, -.10, -.20},
		new double[] {-.10, 0.00, 0.10, 0.10, 0.10, 0.10, 0.00, -.10},
		new double[] {-.10, 0.05, 0.10, 0.15, 0.15, 0.10, 0.05, -.10},
		new double[] {-.10, 0.05, 0.10, 0.15, 0.15, 0.10, 0.05, -.10},
		new double[] {-.10, 0.00, 0.10, 0.10, 0.10, 0.10, 0.00, -.10},
		new double[] {-.20, -.10, 0.00, 0.05, 0.05, 0.00, -.10, -.20},
		new double[] {-.30, -.20, -.10, -.10, -.10, -.10, -.20, -.30},
	};
	
	static public final double[][] KING_OPENING = new double[][] {
		new double[] {-.30, -.40, -.40, -.50, -.50, -.40, -.40, -.30},
		new double[] {-.30, -.40, -.40, -.50, -.50, -.40, -.40, -.30},
		new double[] {-.30, -.40, -.40, -.50, -.50, -.40, -.40, -.30},
		new double[] {-.30, -.40, -.40, -.50, -.50, -.40, -.40, -.30},
		new double[] {-.20, -.30, -.30, -.40, -.40, -.30, -.30, -.20},
		new double[] {-.10, -.20, -.20, -.20, -.20, -.20, -.20, -.10},
		new double[] {0.10, 0.10, 0.00, -.30, -.20, -.30, 0.00, 0.10},
		new double[] {0.70, 0.80, 0.60, -.20, 0.00, -.20, 0.80, 0.70},
	};
	
	static public final double[][] KING_MIDDLE = new double[][] {
		new double[] {-.30, -.40, -.40, -.50, -.50, -.40, -.40, -.30},
		new double[] {-.30, -.40, -.40, -.50, -.50, -.40, -.40, -.30},
		new double[] {-.30, -.40, -.40, -.50, -.50, -.40, -.40, -.30},
		new double[] {-.30, -.40, -.40, -.50, -.50, -.40, -.40, -.30},
		new double[] {-.20, -.30, -.30, -.40, -.40, -.30, -.30, -.20},
		new double[] {-.10, -.20, -.20, -.20, -.20, -.20, -.20, -.10},
		new double[] {0.10, 0.10, -.10, -.20, -.20, -.10, 0.10, 0.10},
		new double[] {0.70, 0.70, 0.50, -.20, -.20, -.20, 0.60, 0.60},
	};
	
	static public final double[][] KING_END = new double[][] {
		new double[] {-.80, -.60, -.30, -.30, -.30, -.30, -.60, -.80},
		new double[] {-.60, -.20, 0.00, 0.00, 0.00, 0.00, -.20, -.60},
		new double[] {-.30, 0.00, 0.05, 0.10, 0.10, 0.05, 0.00, -.30},
		new double[] {-.30, 0.00, 0.10, 0.10, 0.10, 0.10, 0.00, -.30},
		new double[] {-.30, 0.00, 0.10, 0.10, 0.10, 0.10, 0.00, -.30},
		new double[] {-.30, 0.00, 0.05, 0.10, 0.10, 0.05, 0.00, -.30},
		new double[] {-.60, -.20, 0.00, 0.00, 0.00, 0.00, -.20, -.60},
		new double[] {-.80, -.60, -.30, -.30, -.30, -.30, -.60, -.80},
	};
	
	public ChessBitModel() {
		this(
				1, (RANK[0] | RANK[7]) & (FILE[2] | FILE[6]), 0L, 0L, RANK[1],
				RANK[0] & (FILE[1] | FILE[6]), RANK[0] & (FILE[2] | FILE[5]),
				RANK[0] & (FILE[0] | FILE[7]), RANK[0] & FILE[3], RANK[0] & FILE[4],
				RANK[6], RANK[7] & (FILE[1] | FILE[6]), RANK[7] & (FILE[2] | FILE[5]),
				RANK[7] & (FILE[0] | FILE[7]), RANK[7] & FILE[3], RANK[7] & FILE[4],
				true
			);
	}
	
	public ChessBitModel(int turn, long castle, long last_from, long last_to, long _P, long _N, long _B, long _R, long _Q, long _K, long _p, long _n, long _b, long _r, long _q, long _k, boolean not_capture) {
		this._turn = turn;
		this._castle = castle;
		this._last_from = last_from;
		this._last_to = last_to;
		this._P = _P;
		this._N = _N;
		this._B = _B;
		this._R = _R;
		this._Q = _Q;
		this._K = _K;
		this._p = _p;
		this._n = _n;
		this._b = _b;
		this._r = _r;
		this._q = _q;
		this._k = _k;
		this._white = _P|_N|_B|_R|_Q|_K;
		this._black = _p|_n|_b|_r|_q|_k;
		this._open = ~(_white|_black);
		this._not_capture = not_capture;
	}
	
	public long getP() {
		return _P;
	}
	
	public long getN() {
		return _N;
	}
	
	public long getB() {
		return _B;
	}
	
	public long getR() {
		return _R;
	}
	
	public long getQ() {
		return _Q;
	}
	
	public long getK() {
		return _K;
	}
	
	public long getp() {
		return _p;
	}
	
	public long getn() {
		return _n;
	}
	
	public long getb() {
		return _b;
	}
	
	public long getr() {
		return _r;
	}
	
	public long getq() {
		return _q;
	}
	
	public long getk() {
		return _k;
	}
	
	public long getWhite() {
		return _white;
	}
	
	public long getBlack() {
		return _black;
	}
	
	public long getOpen() {
		return _open;
	}
	
	public String[][] getBoard() {
		String[][] board = new String[8][8];
		long index = 0L;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				index = RANK[i] & FILE[j];
				if ((_P & index) != 0) {
					board[j][i] = "P";
				} else if ((_N & index) != 0) {
					board[j][i] = "N";
				} else if ((_B & index) != 0) {
					board[j][i] = "B";
				} else if ((_R & index) != 0) {
					board[j][i] = "R";
				} else if ((_Q & index) != 0) {
					board[j][i] = "Q";
				} else if ((_K & index) != 0) {
					board[j][i] = "K";
				} else if ((_p & index) != 0) {
					board[j][i] = "p";
				} else if ((_n & index) != 0) {
					board[j][i] = "n";
				} else if ((_b & index) != 0) {
					board[j][i] = "b";
				} else if ((_r & index) != 0) {
					board[j][i] = "r";
				} else if ((_q & index) != 0) {
					board[j][i] = "q";
				} else if ((_k & index) != 0) {
					board[j][i] = "k";
				} else {
					board[j][i] = " ";
				}
			}
		}
		return board;
	}
	
	public int[] getLastMove() {
		int x1 = -1;
		int y1 = -1;
		int x2 = -1;
		int y2 = -1;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((_last_from & RANK[i] & FILE[j]) != 0) {
					x1 = j;
					y1 = i;
				}
				if ((_last_to & RANK[i] & FILE[j]) != 0) {
					x2 = j;
					y2 = i;
				}
			}
		}
		return new int[] {x1, y1, x2, y2};
	}
	
	@Override
	public int getTurn() {
		return _turn;
	}
	
	@Override
	public boolean isStatic(int depth) {
		return depth <= -4 || (_not_capture && !(_turn == 1 ? whiteChecked() : blackChecked()));
	}

	@Override
	public boolean hasWon(int player) {
		if (player == 1) return _turn == -1 && noMoves() && blackChecked();
		if (player == -1) return _turn == 1 && noMoves() && whiteChecked();
		int count = 1;
		for (ChessBitModel model : _history) if (equals(model)) count++;
		return count >= 3 ? true : noMoves() && (_turn == 1 ? !whiteChecked() : !blackChecked());
	}
	
	@Override
	public double evaluate(int depth) {
		int count = 1;
		for (ChessBitModel model : _history) if (equals(model)) count++;
		if (count >= 2) return 0;
		if (noMoves()) {
			if (_turn == -1) {
				return blackChecked() ? getWinEvaluation() + depth : 0;
			} else {
				return whiteChecked() ? -getWinEvaluation() - depth : 0;
			}
		}
		return getCurrentEvaluation();
	}

	@Override
	public List<TwoPlayerAi> getNextModels() {
		List<TwoPlayerAi> next_models = new ArrayList<TwoPlayerAi>();
		long from = 0L;
		long to = 0L;
		
		if (_turn == 1) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					from = RANK[i] & FILE[j];
					if ((_P & from) != 0) {
						to = pawnMoves(from, 1);
					} else if ((_N & from) != 0) {
						to = knightMoves(from, 1);
					} else if ((_B & from) != 0) {
						to = bishopMoves(from, 1);
					} else if ((_R & from) != 0) {
						to = rookMoves(from, 1);
					} else if ((_Q & from) != 0) {
						to = queenMoves(from, 1);
					} else if ((_K & from) != 0) {
						to = kingMoves(from, 1);
					} else {
						continue;
					}
					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {
							if ((to & RANK[k] & FILE[l]) != 0) {
								ChessBitModel move = move(from, to & RANK[k] & FILE[l]);
								if (move != null) {
									if (move.isStatic(0)) {
										next_models.add(move);
									} else {
										next_models.add(0, move);
									}
								}
							}
						}
					}
				}
			}
			
		} else {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					from = RANK[i] & FILE[j];
					if ((_p & from) != 0) {
						to = pawnMoves(from, -1);
					} else if ((_n & from) != 0) {
						to = knightMoves(from, -1);
					} else if ((_b & from) != 0) {
						to = bishopMoves(from, -1);
					} else if ((_r & from) != 0) {
						to = rookMoves(from, -1);
					} else if ((_q & from) != 0) {
						to = queenMoves(from, -1);
					} else if ((_k & from) != 0) {
						to = kingMoves(from, -1);
					} else {
						continue;
					}
					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {
							if ((to & RANK[k] & FILE[l]) != 0) {
								ChessBitModel move = move(from, RANK[k] & FILE[l]);
								if (move != null) {
									if (move.isStatic(0)) {
										next_models.add(move);
									} else {
										next_models.add(0, move);
									}
								}
							}
						}
					}
				}
			}
		}
		return next_models;
	}
	
	private boolean noMoves() {
		long from = 0L;
		long to = 0L;
		
		if (_turn == 1) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					from = RANK[i] & FILE[j];
					if ((_P & from) != 0) {
						to = pawnMoves(from, 1);
					} else if ((_N & from) != 0) {
						to = knightMoves(from, 1);
					} else if ((_B & from) != 0) {
						to = bishopMoves(from, 1);
					} else if ((_R & from) != 0) {
						to = rookMoves(from, 1);
					} else if ((_Q & from) != 0) {
						to = queenMoves(from, 1);
					} else if ((_K & from) != 0) {
						to = kingMoves(from, 1);
					} else {
						continue;
					}
					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {
							if ((to & RANK[k] & FILE[l]) != 0) {
								if (move(from, RANK[k] & FILE[l]) != null)
									return false;
							}
						}
					}
				}
			}
			
		} else {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					from = RANK[i] & FILE[j];
					if ((_p & from) != 0) {
						to = pawnMoves(from, -1);
					} else if ((_n & from) != 0) {
						to = knightMoves(from, -1);
					} else if ((_b & from) != 0) {
						to = bishopMoves(from, -1);
					} else if ((_r & from) != 0) {
						to = rookMoves(from, -1);
					} else if ((_q & from) != 0) {
						to = queenMoves(from, -1);
					} else if ((_k & from) != 0) {
						to = kingMoves(from, -1);
					} else {
						continue;
					}
					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {
							if ((to & RANK[k] & FILE[l]) != 0) {
								if (move(from, RANK[k] & FILE[l]) != null)
									return false;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	public ChessBitModel move(int x1, int y1, int x2, int y2) {
		if (x1 < 0 || y1 < 0) throw new IllegalArgumentException();
		ChessBitModel result = move(RANK[y1] & FILE[x1], RANK[y2] & FILE[x2]);
		boolean legal = false;
		for (TwoPlayerAi model : getNextModels()) {
			if (result.equals((ChessBitModel)(model))) {
				legal = true;
			}
		}
		return legal ? result : null;
	}
	
	private ChessBitModel move(long new_from, long new_to) {
		long new_castle = _castle;
		long new_P = _P;
		long new_N = _N;
		long new_B = _B;
		long new_R = _R;
		long new_Q = _Q;
		long new_K = _K;
		long new_p = _p;
		long new_n = _n;
		long new_b = _b;
		long new_r = _r;
		long new_q = _q;
		long new_k = _k;
		
		if (_turn == 1) {
			
			if ((new_P & new_from) != 0) {
				new_P = ~new_from & new_P | new_to;
				if (((new_from << 7 | new_from << 9) & new_to) != 0 && (new_to & _black) == 0) new_p = new_p & ~(new_to >>> 8);
			} else if ((new_N & new_from) != 0) {
				new_N = ~new_from & new_N | new_to;
			} else if ((new_B & new_from) != 0) {
				new_B = ~new_from & new_B | new_to;
			} else if ((new_R & new_from) != 0) {
				new_R = ~new_from & new_R | new_to;
				if ((new_from & RANK[0]) != 0) {
					if ((new_from & FILE[0]) != 0) new_castle &= (RANK[7] | FILE[6]);
					else if ((new_from & FILE[7]) != 0) new_castle &= (RANK[7] | FILE[2]);
				}
			} else if ((new_Q & new_from) != 0) {
				new_Q = ~new_from & new_Q | new_to;
			} else if ((new_K & new_from) != 0) {
				new_K = ~new_from & new_K | new_to;
				new_castle &= RANK[7];
				if ((new_from & RANK[0] & FILE[4]) != 0) {
					if ((new_to & FILE[2]) != 0) {
						new_R = new_R & ~(RANK[0] & FILE[0]) | RANK[0] & FILE[3];
						if (whiteChecked() || (new ChessBitModel(-1, new_castle, new_from, new_to, new_P, new_N, new_B, new_R, new_Q, new_K >>> 1 & _K << 1 | new_K << 1 & _K >>> 1, new_p, new_n, new_b, new_r, new_q, new_k, true)).whiteChecked())
							return null;
					} else if ((new_to & FILE[6]) != 0) {
						new_R = new_R & ~(RANK[0] & FILE[7]) | RANK[0] & FILE[5];
						if (whiteChecked() || (new ChessBitModel(-1, new_castle, new_from, new_to, new_P, new_N, new_B, new_R, new_Q, new_K >>> 1 & _K << 1 | new_K << 1 & _K >>> 1, new_p, new_n, new_b, new_r, new_q, new_k, true)).whiteChecked())
							return null;
					}
				}
			} else {
				return null;
			}
			
			long capture = ~new_to;
			new_p &= capture;
			new_n &= capture;
			new_b &= capture;
			new_r &= capture;
			new_q &= capture;
			
			// Promote
			if ((new_P & RANK[7]) != 0) {
				new_Q |= new_P & RANK[7];
				new_P &= ~RANK[7];
			}
			
			ChessBitModel new_model = new ChessBitModel(-1, new_castle, new_from, new_to, new_P, new_N, new_B, new_R, new_Q, new_K, new_p, new_n, new_b, new_r, new_q, new_k, new_p == _p && new_n == _n && new_b == _b && new_r == _r && new_q == _q && new_k == _k);
			if (new_model.whiteChecked()) {
				return null;
			}
			return new_model;
			
		} else {
			
			if ((new_p & new_from) != 0) {
				new_p = ~new_from & new_p | new_to;
				if (((new_from >>> 7 | new_from >>> 9) & new_to) != 0 && (new_to & _white) == 0) new_P = new_P & ~(new_to << 8);
			} else if ((new_n & new_from) != 0) {
				new_n = ~new_from & new_n | new_to;
			} else if ((new_b & new_from) != 0) {
				new_b = ~new_from & new_b | new_to;
			} else if ((new_r & new_from) != 0) {
				new_r = ~new_from & new_r | new_to;
				if ((new_from & RANK[7]) != 0) {
					if ((new_from & FILE[0]) != 0) new_castle = new_castle & (RANK[0] | FILE[6]);
					else if ((new_from & FILE[7]) != 0) new_castle = new_castle & (RANK[0] | FILE[2]);
				}
			} else if ((new_q & new_from) != 0) {
				new_q = ~new_from & new_q | new_to;
			} else if ((new_k & new_from) != 0) {
				new_k = ~new_from & new_k | new_to;
				new_castle = new_castle & RANK[0];
				if ((new_from & RANK[7] & FILE[4]) != 0) {
					if ((new_to & FILE[2]) != 0) {
						new_r = new_r & ~(RANK[7] & FILE[0]) | RANK[7] & FILE[3];
						if (blackChecked() || (new ChessBitModel(1, new_castle, new_from, new_to, new_P, new_N, new_B, new_R, new_Q, new_K, new_p, new_n, new_b, new_r, new_q, new_k >>> 1 & _k << 1 | new_k << 1 & _k >>> 1, true)).blackChecked())
							return null;
					}
					else if ((new_to & FILE[6]) != 0) {
						new_r = new_r & ~(RANK[7] & FILE[7]) | RANK[7] & FILE[5];
						if (blackChecked() || (new ChessBitModel(1, new_castle, new_from, new_to, new_P, new_N, new_B, new_R, new_Q, new_K, new_p, new_n, new_b, new_r, new_q, new_k >>> 1 & _k << 1 | new_k << 1 & _k >>> 1, true)).blackChecked())
							return null;
					}
				}
			} else {
				return null;
			}
			
			long capture = ~new_to;
			new_P &= capture;
			new_N &= capture;
			new_B &= capture;
			new_R &= capture;
			new_Q &= capture;
			
			// Promote
			if ((new_p & RANK[0]) != 0) {
				new_q |= new_p & RANK[0];
				new_p = new_p & ~RANK[0];
			}
			
			ChessBitModel new_model = new ChessBitModel(1, new_castle, new_from, new_to, new_P, new_N, new_B, new_R, new_Q, new_K, new_p, new_n, new_b, new_r, new_q, new_k, new_P == _P && new_N == _N && new_B == _B && new_R == _R && new_Q == _Q && new_K == _K);
			if (new_model.blackChecked()) {
				return null;
			}
			return new_model;
			
		}		
	}
	
	private long pawnMoves(long pawn, int color) {
		long open = _open;
		
		if (color == 1) {
			long result = pawn << 8 & open;
			if ((pawn & RANK[1]) != 0 && result != 0) {
				result |= pawn << 16 & open;
			}
			return  result | (pawn << 7 & ~FILE[7] | pawn << 9 & ~FILE[0]) & (_black | (_last_to & _p & (_last_from >>> 16)) << 8);
		} else {
			long result = pawn >>> 8 & open;
			if ((pawn & RANK[6]) != 0 && result != 0) {
				result |= pawn >>> 16 & open;
			}
			return result | (pawn >>> 9 & ~FILE[7] | pawn >>> 7 & ~FILE[0]) & (_white | (_last_to & _P & _last_from << 16) >>> 8);
		}
	}
	
	private long knightMoves(long knight, int color) {
		long n1 = ~RANK[0];
		long n2 = ~(RANK[0] | RANK[1]);
		long s1 = ~RANK[7];
		long s2 = ~(RANK[6] | RANK[7]);
		long e1 = ~FILE[0];
		long e2 = ~(FILE[0] | FILE[1]);
		long w1 = ~FILE[7];
		long w2 = ~(FILE[6] | FILE[7]);
		
		return (color == 1 ? ~_white : ~_black) &
				(knight << 17 & n2 & e1 |
				knight << 10 & n1 & e2 |
				knight >>> 6 & s1 & e2 |
				knight >>> 15 & s2 & e1 |
				knight >>> 17 & s2 & w1 |
				knight >>> 10 & s1 & w2 |
				knight << 6 & w2 & n1 |
				knight << 15 & n2 & w1);
	}
	
	private long bishopMoves(long bishop, int color) {
		long result = 0L;
		long open = _open;
		long opp = color == 1 ? _black : _white;
		
		for (int i = 0; (bishop & (FILE[7-i] | RANK[7-i])) == 0; i++) {
			long ne = bishop << 9*i+9;
			long value = ne&open;
			if (value == 0) {
				result |= ne&opp;
				break;
			}
			result |= value;
		}
		for (int i = 0; (bishop & (FILE[7-i] | RANK[i])) == 0; i++) {
			long se = bishop >>> 7*i+7;
			long value = se&open;
			if (value == 0) {
				result |= se&opp;
				break;
			}
			result |= value;
		}
		for (int i = 0; (bishop & (FILE[i] | RANK[i])) == 0; i++) {
			long sw = bishop >>> 9*i+9;
			long value = sw&open;
			if (value == 0) {
				result |= sw&opp;
				break;
			}
			result |= value;
		}
		for (int i = 0; (bishop & (FILE[i] | RANK[7-i])) == 0; i++) {
			long nw = bishop << 7*i+7;
			long value = nw&open;
			if (value == 0) {
				result |= nw&opp;
				break;
			}
			result |= value;
		}
		
		return result;
	}
	
	private long rookMoves(long rook, int color) {
		long result = 0L;
		long open = _open;
		long opp = color == 1 ? _black : _white;
		
		for (int i = 0; (rook & FILE[7-i]) == 0; i++) {
			long right = rook << i+1;
			long value = right&open;
			if (value == 0) {
				result |= right&opp;
				break;
			}
			result |= value;
		}
		for (int i = 0; (rook & FILE[i]) == 0; i++) {
			long left = rook >>> i+1;
			long value = left&open;
			if (value == 0) {
				result |= left&opp;
				break;
			}
			result |= value;
		}
		for (int i = 0; (rook & RANK[7-i]) == 0; i++) {
			long up = rook << 8*i+8;
			long value = up&open;
			if (value == 0) {
				result |= up&opp;
				break;
			}
			result |= value;
		}
		for (int i = 0; (rook & RANK[i]) == 0; i++) {
			long down = rook >>> 8*i+8;
			long value = down&open;
			if (value == 0) {
				result |= down&opp;
				break;
			}
			result |= value;
		}
		return result;
	}
	
	private long queenMoves(long queen, int color) {
		return bishopMoves(queen,color)|rookMoves(queen,color);
	}
	
	private long kingMoves(long king, int color) {
		long open = _open;
		long not_same = color == 1 ? ~_white : ~_black;
		
		long n = ~RANK[0];
		long s = ~RANK[7];
		long e = ~FILE[0];
		long w = ~FILE[7];
		
		long result = not_same & (king << 8 & n |
				king << 9 & n & e |
				king << 1 & e |
				king >>> 7 & s & e |
				king >>> 8 & s |
				king >>> 9 & s & w |
				king >>> 1 & w |
				king << 7 & n & w);
		
		if (color == 1) {
			return result | _castle & RANK[0] & open & ((open & FILE[1]) << 1 & (open & FILE[3]) >>> 1 | (open & FILE[5]) << 1);
		} else {
			return result | _castle & RANK[7] & open & ((open & FILE[1]) << 1 & (open & FILE[3]) >>> 1 | (open & FILE[5]) << 1);
		}
	}
	
	private boolean whiteChecked() {
		long king = _K;
		return (bishopMoves(king, 1) & _b) != 0 ||
				(knightMoves(king, 1) & _n) != 0 ||
				(queenMoves(king, 1) & _q) != 0 ||
				(rookMoves(king, 1) & _r) != 0 ||
				(pawnMoves(king, 1) & _p) != 0 ||
				(kingMoves(king, 1) & _k) != 0;
	}
	
	private boolean blackChecked() {
		long king = _k;
		return (bishopMoves(king, -1) & _B) != 0 ||
				(knightMoves(king, -1) & _N) != 0 ||
				(queenMoves(king, -1) & _Q) != 0 ||
				(rookMoves(king, -1) & _R) != 0 ||
				(pawnMoves(king, -1) & _P) != 0 ||
				(kingMoves(king, -1) & _K) != 0;
	}
	
	@Override
	public double getCurrentEvaluation() {
		double white_evaluation = 0;
		double black_evaluation = 0;
		int num_pieces = numOnes(_N | _B | _R | _Q | _n | _b | _r | _q);
		for (int i = 0; i < 64; i++) {
			if ((_P >>> i & 1) == 1) white_evaluation += getValueOfPawn(num_pieces, i%8,i/8,1);
			else if ((_p >>> i & 1) == 1) black_evaluation += getValueOfPawn(num_pieces, i%8,i/8,-1);
			else if ((_N >>> i & 1) == 1) white_evaluation += getValueOfKnight(num_pieces, i%8,i/8,1);
			else if ((_n >>> i & 1) == 1) black_evaluation += getValueOfKnight(num_pieces, i%8,i/8,-1);
			else if ((_B >>> i & 1) == 1) white_evaluation += getValueOfBishop(num_pieces, i%8,i/8,1);
			else if ((_b >>> i & 1) == 1) black_evaluation += getValueOfBishop(num_pieces, i%8,i/8,-1);
			else if ((_R >>> i & 1) == 1) white_evaluation += getValueOfRook(num_pieces, i%8,i/8,1);
			else if ((_r >>> i & 1) == 1) black_evaluation += getValueOfRook(num_pieces, i%8,i/8,-1);
			else if ((_Q >>> i & 1) == 1) white_evaluation += getValueOfQueen(num_pieces, i%8,i/8,1);
			else if ((_q >>> i & 1) == 1) black_evaluation += getValueOfQueen(num_pieces, i%8,i/8,-1);
			else if ((_K >>> i & 1) == 1) white_evaluation += getValueOfKing(num_pieces, i%8,i/8,1);
			else if ((_k >>> i & 1) == 1) black_evaluation += getValueOfKing(num_pieces, i%8,i/8,-1);
		}
		return (white_evaluation + black_evaluation)/(white_evaluation - black_evaluation);
	}
	
	private double getValueOfPawn(int num_pieces, int x, int y, int color) {
		double value = 1.0;
		if (num_pieces > 10) {
			value += PAWN_OPENING[color == 1 ? 7-y : y][x];
		} else if (num_pieces > 4) {
			value += PAWN_MIDDLE[color == 1 ? 7-y : y][x];
		} else {
			value += PAWN_END[color == 1 ? 7-y : y][x];
		}
		
		// Doubled, etc pawns
		value -= 0.10*(numOnes((FILE[x] & (color == 1 ? _P : _p))) - 1);
		
		// Isolated pawns
		if ((((x > 0 ? FILE[x-1] : 0) | (x < 7 ? FILE[x+1] : 0)) & (color == 1 ? _P : _p)) == 0) {
			value -= 0.10;
		}
//		
//		// Connected pawns
//		for (int i: new int[] {-1, 1}) {
//			if (i + x < 0 || i + x > 7) {
//				continue;
//			}
//			if (_board[x][y].equals(_board[x+i][y-1])) {
//				value += .05;
//			}
//		}
//		
		// Passed pawns
		if ((((x > 0 ? FILE[x-1] : 0) | FILE[x] | (x < 7 ? FILE[x+1] : 0)) & (color == 1 ? _p : _P)) == 0) {
			value *= 1.2;
		}
		
		return color*value;
	}
	
	private double getValueOfKnight(int num_pieces, int x, int y, int color) {
		double value = 3.0;
		if (num_pieces >= 10) {
			value += KNIGHT_OPENING[color == 1 ? 7-y : y][x];
		} else if (num_pieces > 4) {
			value += KNIGHT_MIDDLE[color == 1 ? 7-y : y][x];
		} else {
			value += KNIGHT_END[color == 1 ? 7-y : y][x];
		}
		return color*value;
	}

	private double getValueOfBishop(int num_pieces, int x, int y, int color) {
		double value = 3.1;
		if (num_pieces > 10) {
			value += BISHOP_OPENING[color == 1 ? 7-y : y][x];
		} else if (num_pieces > 4) {
			value += BISHOP_MIDDLE[color == 1 ? 7-y : y][x];
		} else {
			value += BISHOP_END[color == 1 ? 7-y : y][x];
		}
		
//		// Bishop blocked by pawns
//		for (int i : new int[] {-1, 1}) {
//			if (i + x < 0 || i + x > 7) {
//				continue;
//			}
//			for (int j : new int[] {-1, 1}) {
//				if (j + y < 0 || j + y > 7) {
//					continue;
//				}
//				if (_board[x+i][y+j].equalsIgnoreCase("P")) {
//					value -= .05;
//				}
//			}
//		}
		
		return color*value;
	}

	private double getValueOfRook(int num_pieces, int x, int y, int color) {
		double value = 5.0;
		if (num_pieces > 10) {
			value += ROOK_OPENING[color == 1 ? 7-y : y][x];
		} else if (num_pieces > 4) {
			value += ROOK_MIDDLE[color == 1 ? 7-y : y][x];
		} else {
			value += ROOK_END[color == 1 ? 7-y : y][x];
		}
		
		// Open/half-open columns
		if ((FILE[x] & _P) == 0) {
			value += .10;
		}
		if ((FILE[x] & _p) == 0) {
			value += .10;
		}
//		
//		// Closed rooks
//		for (int[] i : new int[][] {new int[] {0, 1},new int[] {1, 0},new int[] {0, -1},new int[] {-1, 0}}) {
//			if (x + i[0] < 0 || x + i[0] > 7 || y + i[1] < 0 || y + i[1] > 7 || !_board[x+i[0]][y+i[1]].equals(" ")) {
//				value -= .05;
//			}
//		}
//		
//		// Connected rooks
//		for (int i = x - 1; i >= 0; i--) {
//			if (!_board[i][y].equals(" ")) {
//				if (_board[i][y].equals(_board[x][y])) {
//					value += .1;
//				}
//				break;
//			}
//		}
//		for (int i = x + 1; i < 8; i++) {
//			if (!_board[i][y].equals(" ")) {
//				if (_board[i][y].equals(_board[x][y])) {
//					value += .1;
//				}
//				break;
//			}
//		}
//		for (int i = y - 1; i >= 0; i--) {
//			if (!_board[i][y].equals(" ")) {
//				if (_board[i][y].equals(_board[x][y])) {
//					value += .1;
//				}
//				break;
//			}
//		}
//		for (int i = y + 1; i < 8; i++) {
//			if (!_board[x][i].equals(" ")) {
//				if (_board[x][i].equals(_board[x][y])) {
//					value += .1;
//				}
//				break;
//			}
//		}
		
		return color*value;
	}

	private double getValueOfQueen(int num_pieces, int x, int y, int color) {
		double value = 9.0;
		if (num_pieces > 10) {
			value += QUEEN_OPENING[color == 1 ? 7-y : y][x];
		} else if (num_pieces > 4) {
			value += QUEEN_MIDDLE[color == 1 ? 7-y : y][x];
		} else {
			value += QUEEN_END[color == 1 ? 7-y : y][x];
		}
		return color*value;
	}
	
	private double getValueOfKing(int num_pieces, int x, int y, int color) {
		double value = 3.2;
		if (num_pieces > 10) {
			value += KING_OPENING[color == 1 ? 7-y : y][x];
		} else if (num_pieces > 4) {
			value += KING_MIDDLE[color == 1 ? 7-y : y][x];
			
			// King safety
			if (numOnes(queenMoves(RANK[y] & FILE[x],color)) > 7) {
				value -= .4;
			}
		} else {
			value += KING_END[color == 1 ? 7-y : y][x];
		}
		
		return color*value;
	}
	
	private boolean equals(ChessBitModel other) {
		return _P == other.getP() &&
				_N == other.getN() &&
				_B == other.getB() &&
				_R == other.getR() &&
				_Q == other.getQ() &&
				_K == other.getK() &&
				_p == other.getp() &&
				_n == other.getn() &&
				_b == other.getb() &&
				_r == other.getr() &&
				_q == other.getq() &&
				_k == other.getk();
	}
	
	public static void addHistory(ChessBitModel model) {
		_history.add(model);
	}
	
	private static int numOnes(long l) {
		int count = 0;
		while (l != 0) {
			l &= l-1;
			count++;
		}
		return count;
	}
}
