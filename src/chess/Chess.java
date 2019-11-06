package chess;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Chess {

	public static void main(String[] args) {
		JFrame main_frame = new JFrame();
		main_frame.setTitle("Chess");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ChessBitModel model = new ChessBitModel();
		ChessView view = new ChessView();
 		ChessController controller = new ChessController(model, view, 1, 4);

 		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(view, BorderLayout.CENTER);
		main_frame.setContentPane(panel);

		main_frame.pack();
		main_frame.setVisible(true);
	}
	
}
