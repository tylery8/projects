package chess;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ChessController implements MouseListener, Runnable {
	
	private ChessBitModel _model;
	private ChessView _view;
	private int _players;
	private int _difficulty;
	private int _last_x;
	private int _last_y;
	
	public ChessController(ChessBitModel model, ChessView view, int players, int difficulty) {
		_model = model;
		_view = view;
		_players = players;
		_difficulty = difficulty;
		_last_x = -1;
		_last_y = -1;
		view.setController(this);
		view.refreshAll();
		
		if (players == -1) view.flip();
		
		if (players == 0 || players == -model.getTurn()) {
			(new Thread(this)).start();
		}
	}
	
	public ChessBitModel getModel() {
		return _model;
	}
	
	public int getPlayers() {
		return _players;
	}
	
	public void makeUserMove(int x1, int y1, int x2, int y2) {
		try {
			ChessBitModel new_model = _model.move(x1, y1, x2, y2);
			if (new_model == null) {
				_last_x = x2;
				_last_y = y2;
				return;
			}
			ChessBitModel.addHistory(_model);
//			ChessModel.printNotation(_model, new_model);
			_model = new_model;
			_view.refreshAll();
			_last_x = -1;
			_last_y = -1;
			if (_players < 2  && !(_model.hasWon(1) || _model.hasWon(0) || _model.hasWon(-1))) {
				(new Thread(this)).start();
			}
		}
		catch (Exception ex) {
			_last_x = x2;
			_last_y = y2;
		}
	}
	
	public void makeCPUMove(int depth) {
		ChessBitModel new_model = (ChessBitModel) _model.getBestMove(depth);
		ChessBitModel.addHistory(_model);
//		ChessModel.printNotation(_model, new_model);
		_model = new_model;
		_view.refreshAll();
		if (_players == 0 && !(_model.hasWon(1) || _model.hasWon(0) || _model.hasWon(-1))) {
			(new Thread(this)).start();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = -1;
		int y = -1;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (_view.getPictures()[i][j].equals(e.getSource())) {
					x = _view.getFlipped() ? 7-i : i;
					y = _view.getFlipped() ? 7-j : j;
				}
			}
		}
		
		if (_players != 0  && !(_model.hasWon(1) || _model.hasWon(0) || _model.hasWon(-1)) && (_players == 2 || _model.getTurn() == _players)) {
			try {
				if (_view.getFlipped()) {
					_view.highlight(7-x, 7-y);
					_view.refresh(7-_last_x, 7-_last_y);
				} else {
					_view.highlight(x, y);
					_view.refresh(_last_x, _last_y);
				}
			}
			catch (Exception ex) {
			}
			makeUserMove(_last_x, _last_y, x, y);
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		makeCPUMove(_difficulty);
	}

}
