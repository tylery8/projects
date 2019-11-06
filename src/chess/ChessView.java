package chess;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import pictures.ColorPixel;
import pictures.Picture;
import pictures.PictureView;
import pictures.Pixel;

public class ChessView extends JPanel implements MouseListener {
	
	private PictureView[][] _pictures;
	private ChessController _controller;
	private boolean _flipped = false;
	
	static private final int[] PAWN_SHAPE = new int[] {6,5,5,3,3,4,5,5,5,4,3,3,2,1,0,0,8,9,9,11,11,10,9,9,9,10,11,11,12,13,14,14};
	static private final int[] KNIGHT_SHAPE = new int[] {7,4,4,3,2,1,1,1,5,5,5,4,3,3,3,3,2,1,0,0,8,9,10,11,11,12,12,13,13,13,13,13,13,12,12,11,12,13,14,14};
	static private final int[] BISHOP_SHAPE = new int[] {6,6,5,4,4,4,4,3,3,4,5,5,5,5,5,5,5,4,3,3,2,1,0,0,8,8,9,10,10,10,10,11,11,10,9,9,9,9,9,9,9,10,11,11,12,13,14,14};
	static private final int[] ROOK_SHAPE = new int[] {1,1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,2,1,0,0,12,12,12,12,11,10,10,10,10,10,10,10,10,10,10,10,11,12,13,13};
	static private final int[] QUEEN_SHAPE = new int[] {6,4,3,3,4,4,4,3,3,4,5,5,5,5,5,5,5,5,5,4,4,4,3,3,2,1,0,0,8,10,11,11,10,10,10,11,11,10,9,9,9,9,9,9,9,9,9,10,10,10,11,11,12,13,14,14};
	static private final int[] KING_SHAPE = new int[] {6,5,6,6,5,4,4,4,4,3,3,4,5,5,5,5,5,5,5,5,5,5,4,4,4,3,3,2,1,0,0,8,9,8,8,9,10,10,10,10,11,11,10,9,9,9,9,9,9,9,9,9,9,10,10,10,11,11,12,13,14,14};
	
	static private final int SIZE = 150;
	
	public ChessView() {		
		setLayout(new GridLayout(8,8));
		_pictures = new PictureView[8][8];
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				_pictures[i][j] = new PictureView(Picture.createSolidPicture(SIZE, SIZE, Pixel.WHITE, true).createObservable());
				_pictures[i][j].addMouseListener(this);
			}
		}
		
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				add(_pictures[i][7-j]);
			}
		}
	}
	
	public PictureView[][] getPictures() {
		return _pictures;
	}
	
	public void setController(ChessController controller) {
		_controller = controller;
	}
	
	public void flip() {
		_flipped = !_flipped;
		refreshAll();
	}
	
	public boolean getFlipped() {
		return _flipped;
	}
	
	public void refreshAll() {
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				refresh(i,j);
			}
		}
		
		//Highlight last squares
		try {
			int x1 = _controller.getModel().getLastMove()[0];
			int y1 = _controller.getModel().getLastMove()[1];
			int x2 = _controller.getModel().getLastMove()[2];
			int y2 = _controller.getModel().getLastMove()[3];
			
			if (_flipped) {
				highlight(7-x1, 7-y1);
				highlight(7-x2, 7-y2);
			} else {
				highlight(x1, y1);
				highlight(x2, y2);
			}
		}
		catch (Exception e) {
		}
	}
	
	public void highlight(int x, int y) {
		Pixel color = new ColorPixel(255.0/256, 255.0/256, 98.0/256);
		_pictures[x][y].getPicture().paint(0, 0, SIZE, SIZE, color, 0.2);
	}
	
	public void refresh(int x, int y) {
		Pixel color = new ColorPixel(171.0/256, 149.0/256, 132.0/256);
		if (x % 2 == y % 2) {
			color = new ColorPixel(87.0/256, 65.0/256, 47.0/256);
		}
		_pictures[x][y].getPicture().paint(0, 0, SIZE, SIZE, color);
		
		String piece = _controller.getModel().getBoard()[_flipped ? 7-x : x][_flipped ? 7-y : y];
		
		for (String white_piece : new String[] {"P", "N", "B", "R", "Q", "K"})
			if (piece.equals(white_piece))
				color = Pixel.WHITE;
		
		for (String black_piece : new String[] {"p", "n", "b", "r", "q", "k"})
			if (piece.equals(black_piece))
				color = Pixel.BLACK;
		
		int[] piece_shape = PAWN_SHAPE;
		switch(piece) {
		case "P":
		case "p":
			piece_shape = PAWN_SHAPE;
			break;
		case "N":
		case "n":
			piece_shape = KNIGHT_SHAPE;
			break;
		case "B":
		case "b":
			piece_shape = BISHOP_SHAPE;
			break;
		case "R":
		case "r":
			piece_shape = ROOK_SHAPE;
			break;
		case "Q":
		case "q":
			piece_shape = QUEEN_SHAPE;
			break;
		case "K":
		case "k":
			piece_shape = KING_SHAPE;
			break;
		}
		
		int size = SIZE/35;
		int x0 = SIZE/2-size*8;
		int y0 = SIZE-size*piece_shape.length/2-SIZE/20;
		for (int k = 0; k < piece_shape.length/2; k++) {
			_pictures[x][y].getPicture().paint(x0+piece_shape[k]*size, y0+k*size, x0+piece_shape[k+piece_shape.length/2]*size, y0+(k+1)*size, color);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		_controller.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
