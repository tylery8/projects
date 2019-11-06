package chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface TwoPlayerAi {

	// Returns 1 if it is the first player's turn and returns -1
	// if it is the second player's turn.
	int getTurn();

	// Takes in a player and returns true if that player has won
	// in the current position and false if they have not. An
	// input of 1 should return true if the first player has won,
	// an input of -1 should return true if the second player has
	// won, and an input of 0 should return true if it is a draw.
	// If the game is not over, it should always return false.
	boolean hasWon(int player);

	// Returns a list of all possible moves from the current
	// position. The moves should be represented as a new
	// TwoPlayerAi.
	List<TwoPlayerAi> getNextModels();

	// Returns the evaluation of a non-winning board in its
	// current state. Formulas can be very simple and should
	// just give a general idea of which side is currently
	// winning. No evaluation should have a greater magnitude
	// than the win evaluation. A positive value indicates that
	// the first player is winning while a negative value indicates
	// that the second player is winning.
	double getCurrentEvaluation();

	// Returns the value for the magnitude of the evaluation of
	// a winning position. No non-winning position should have
	// an evaluation that is greater in magnitude.
	default double getWinEvaluation() {
		return 1000;
	}
	
	default boolean isStatic(int depth) {
		return true;
	}

	default double evaluate(int depth) {
		if (hasWon(1))
			return getWinEvaluation() + depth;
		if (hasWon(-1))
			return -getWinEvaluation() - depth;
		if (hasWon(0))
			return 0;
		return getCurrentEvaluation();
	}

	// Returns the number of nodes at a given depth.
	default int countNodes(int depth) {
		if (depth == 0)
			return 1;
		int nodes = 0;
		for (TwoPlayerAi next_model : getNextModels())
			nodes += next_model.countNodes(depth - 1);
		return nodes;
	}

	// Evaluates the board at a certain depth, or half-moves into the
	// future (i.e. a depth of 8 would look ahead 4 moves for each player).
	// This assumes both players play optimally and does not consider any
	// branches that are worse than what has already been considered (stored
	// in alpha and beta values).
	default double alphabeta(int depth, double alpha, double beta) {
		// If it is the end of the line, evaluate at the current position
		if (depth <= 0 && isStatic(depth))
			return evaluate(depth);

		// Otherwise, look at all the next positions and compare them
		if (getTurn() == 1) {
			boolean looped = false;
			double max = -getWinEvaluation() - depth;
			for (TwoPlayerAi next_model : getNextModels()) {
				looped = true;
				double evaluation = next_model.alphabeta(depth - 1, alpha, beta);
				if (evaluation > max)
					max = evaluation;
				if (evaluation > alpha)
					alpha = evaluation;
				if (alpha >= beta)
					return alpha;
			}
			return looped ? max : evaluate(depth);
		} else {
			boolean looped = false;
			double min = getWinEvaluation() + depth;
			for (TwoPlayerAi next_model : getNextModels()) {
				looped = true;
				double evaluation = next_model.alphabeta(depth - 1, alpha, beta);
				if (evaluation < min)
					min = evaluation;
				if (evaluation < beta)
					beta = evaluation;
				if (alpha >= beta)
					return beta;
			}
			return looped ? min : evaluate(depth);
		}
	}

	// Returns the best move at a given depth
	default TwoPlayerAi getBestMove(int depth) {
		double alphabeta_mag = getWinEvaluation() + depth;
		List<TwoPlayerAi> next_models = getNextModels();
		if (next_models.size() == 0)
			return null;

		if (depth > 0) {
			double best_evaluation = 0;
			List<Double> evaluations = new ArrayList<Double>();
			next_models.forEach(
					next_model -> evaluations.add(next_model.alphabeta(depth - 1, -alphabeta_mag, alphabeta_mag)));

			if (getTurn() == 1)
				best_evaluation = Collections.max(evaluations);
			else
				best_evaluation = Collections.min(evaluations);

			for (int i = 0; i < evaluations.size(); i++) {
				if (evaluations.get(i) != best_evaluation) {
					evaluations.remove(i);
					next_models.remove(i);
					i--;
				}
			}
		}

		return next_models.get((new Random()).nextInt(next_models.size()));
	}
}