package fanduel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A Lineup takes in a list of Players and a contest format and returns the best lineup
// possible, according to the players' given projections.

public class Lineup {
	
	private List<Player> _players;
	private int _depth;
	private boolean _mvp;
	private int _salary;
	private int[] _position_limits;
	private int _flex, _super_flex, _any_flex;
	
	private List<Player> _best_lineup;
	
	public Lineup(List<Player> players, String format) {
		_players = players;
		
		switch(format) {
		case "Single Game":
			_depth = 3;
			_mvp = true;
			_salary = 60000;
			_position_limits = new int[] {0,0,0,0,0,0};
			_any_flex = 5;
			break;
		case "SuperFlex":
			_depth = 3;
			_salary = 60000;
			_position_limits = new int[] {1,2,2,1,0,0};
			_flex = 1;
			_super_flex = 1;
			break;
		case "3-Man Challenge":
			_depth = 6;
			_mvp = true;
			_salary = 7;
			_position_limits = new int[] {0,0,0,0,0,0};
			_any_flex = 3;
			break;
		case "Full Roster":
		default:
			_depth = 3;
			_salary = 60000;
			_position_limits = new int[] {1,2,3,1,0,1};
			_flex = 1;
		}
		
	}
	
	private Lineup(List<Player> players, int depth, boolean mvp, int salary, int[] position_limits, int flex, int super_flex, int any_flex) {
		_players = players;
		_depth = depth;
		_mvp = mvp;
		_salary = salary;
		_position_limits = position_limits;
		_flex = flex;
		_super_flex = super_flex;
		_any_flex = any_flex;
		
	}
	
	public List<Player> getBestLineup() {
		if (_best_lineup == null)
			_best_lineup = bestLineup();
		return _best_lineup;
	}
	
	// Trims the player list to only consider reasonable picks and then reassigns all
	// flex positions to specific positions.
	private List<Player> bestLineup() {
		trimPlayers(false);
		
		if (_any_flex > 0) {
			Lineup k_best = new Lineup(_players, _depth, _mvp, _salary, incPositionLimit(_position_limits, Position.K), _flex, _super_flex, _any_flex-1);
			Lineup super_flex_best = new Lineup(_players, _depth, _mvp, _salary, _position_limits, _flex, _super_flex+1, _any_flex-1);
			return best(k_best, super_flex_best).getBestLineup();
		}
		if (_super_flex > 0) {
			Lineup qb_best = new Lineup(_players, _depth, _mvp, _salary, incPositionLimit(_position_limits, Position.QB), _flex, _super_flex-1, _any_flex);
			Lineup flex_best = new Lineup(_players, _depth, _mvp, _salary, _position_limits, _flex+1, _super_flex-1, _any_flex);
			return best(qb_best, flex_best).getBestLineup();
		}
		if (_flex > 0) {
			Lineup rb_best = new Lineup(_players, _depth, _mvp, _salary, incPositionLimit(_position_limits, Position.RB), _flex-1, _super_flex, _any_flex);
			Lineup wr_best = new Lineup(_players, _depth, _mvp, _salary, incPositionLimit(_position_limits, Position.WR), _flex-1, _super_flex, _any_flex);
			Lineup te_best = new Lineup(_players, _depth, _mvp, _salary, incPositionLimit(_position_limits, Position.TE), _flex-1, _super_flex, _any_flex);
			return best(best(rb_best, wr_best), te_best).getBestLineup();
		}
		
		trimPlayers(true);
		sortPlayersBy("Cost");
		sortPlayersBy("Projection");
		
		List<Player> best_lineup = bestLineup(_salary, _position_limits, _players.size()-1);
		
		// Takes the best lineup and puts the players in the proper order according to
		// how FanDuel's csv upload wants it.
		if (!_mvp) {
			List<Player> tmp = new ArrayList<Player>();
			sortPlayersBy("Position", best_lineup);
			int[] positions = best_lineup.size() == 9 ? new int[] {1,2,3,1,0,1} : new int[] {1,2,2,1,0,0};
			for (int i = 0; i < 6; i++)
				for (int j = 0; positions[i] > 0; j++)
					if (best_lineup.get(j).getPosition() == Position.values()[i]) {
						tmp.add(best_lineup.remove(j));
						positions[i]--;
						j--;
					}
			
			while (!best_lineup.isEmpty())
				tmp.add(best_lineup.remove(best_lineup.size()-1));
			
			if (tmp.size() == 9)
				tmp.add(tmp.remove(7));
			
			best_lineup = tmp;
		}
		
		return best_lineup;
	}

	// Dynamic programming solution to find the best lineup for a given salary remaining,
	// position limit, and index in the player list (players are sorted by projection).
	// For each player index, it decides whether to include or exclude that player in
	// the lineup.
	private List<Player> bestLineup(int salary_left, int[] positions_left, int index) {
		if (salary_left <= 0 || index < 0) 
			return new ArrayList<Player>();
		
		if (_players.get(index).getCost() > salary_left)
			return bestLineup(salary_left, positions_left, index-1);
		
		if (positions_left[Position.positionToInt(_players.get(index).getPosition())] <= 0)
			return bestLineup(salary_left, positions_left, index-1);
		
		List<Player> add_player = add(_players.get(index), bestLineup(salary_left - _players.get(index).getCost(), decPositionLimit(positions_left, _players.get(index).getPosition()), index-1));
		List<Player> exclude_player = bestLineup(salary_left, positions_left, index-1);

		return best(add_player, exclude_player, positions_left);
	}
	
	// Keeps only the top n players in terms of value for each position. "n" is determined
	// by the _depth value times the maximum number of players that the position can have.
	private void trimPlayers(boolean full_trim) {
		sortPlayersBy("Value");
		List<Player> trimmed = new ArrayList<Player>();
		
		int max_limit = 0;
		for (int i = 0; i < _position_limits.length; i++) {
			max_limit = Math.max(_position_limits[i], max_limit);
		}
		max_limit += _flex + _super_flex + _any_flex;
		
		for (Position position : Position.values()) {
			int count = max_limit;
			if (full_trim)
				count = _position_limits[Position.positionToInt(position)];
			count *= _depth;
			for (int i = 0; i < _players.size(); i++) {
				if (_players.get(i).getPosition() == position) {
					if (count > 0 && _players.get(i).getProjection() > 0) {
						trimmed.add(_players.get(i));
						count--;
					}
				}
			}
		}
		_players = trimmed;
	}
	
	private void sortPlayersBy(String property) {
		sortPlayersBy(property, _players);
	}
	
	private static void sortPlayersBy(String property, List<Player> players) {
		Player._sort_by = property;
		Collections.sort(players);
	}
	
	private static List<Player> add(Player player, List<Player> players) {
		players.add(player);
		return players;
	}
	
	private Lineup best(Lineup lineup1, Lineup lineup2) {
		double total1 = totalProjection(lineup1.getBestLineup(), lineup1._position_limits);
		double total2 = totalProjection(lineup2.getBestLineup(), lineup2._position_limits);
		return total1 >= total2 ? lineup1 : lineup2;
	}
	
	private List<Player> best(List<Player> lineup1, List<Player> lineup2, int[] position_limits) {
		double total1 = totalProjection(lineup1, position_limits);
		double total2 = totalProjection(lineup2, position_limits);
		return total1 >= total2 ? lineup1 : lineup2;
	}
	
	// Calculates the total projection of a given lineup. An illegal lineup receives
	// a projection of -1
	private double totalProjection(List<Player> players, int[] position_limits) {
		if (illegalLineup(players, position_limits))
			return -1;
		
		double highest = 0;
		double total = 0;
		for (Player player : players) {
			highest = Math.max(highest, player.getProjection());
			total += player.getProjection();
		}
		if (_mvp) {
			total += 0.5 * highest;
		}
		return total;
	}
	
	// Determines if that lineup fits FanDuel's additional requirements for a legal lineup
	private boolean illegalLineup(List<Player> players, int[] position_limits) {
		
		int num_players = 0;
		for (int i : position_limits)
			num_players += i;
		
		// Ensures that all player spots get filled
		if (players.size() < num_players)
			return true;
		
		int expected_players = 0;
		for (int i : _position_limits)
			expected_players += i;
		
		// Checks if the lineup is unfinished (so it doesn't have to follow any of the later 
		// requirements yet)
		if (num_players < expected_players)
			return false;
		
		Map<Team, Integer> teams = new HashMap<Team, Integer>();
		for (Player player : players)
			teams.put(player.getTeam(), teams.containsKey(player.getTeam()) ? teams.get(player.getTeam()) + 1 : 0);
		
		// Ensures that there are no more than 4 players from one team
		for (int i : teams.values())
			if (i > 4)
				return true;
		
		// Ensures that players are selected from at least 2 or 3 different teams (depending
		// on contest format)
		return teams.size() < (num_players <= 5 ? 2 : 3);
	}

	private static int[] changePositionLimit(int[] limits, Position position, int change) {
		int[] out = new int[6];
		for (int i = 0; i < out.length; i++) {
			out[i] = limits[i];
		}
		out[Position.positionToInt(position)] = out[Position.positionToInt(position)] + change;
		return out;
	}
	
	private static int[] incPositionLimit(int[] limits, Position position) {
		return changePositionLimit(limits, position, 1);
	}
	
	private static int[] decPositionLimit(int[] limits, Position position) {
		return changePositionLimit(limits, position, -1);
	}
}
