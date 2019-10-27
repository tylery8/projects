package fanduel;

// A Player object stores a given players' id, name, position, team, projeciton, and cost.
// Players can be sorted by position, projection, cost, or value (projection/cost).

public class Player implements Comparable<Player> {
	
	private String _id;
	private String _name;
	private Position _position;
	private Team _team;
	private double _projection;
	private int _cost;
	
	public static String _sort_by = "";
	
	public Player(String id, String name, String position, String team, int cost) {
		this(id, name, position, team, 0.0, cost);
	}
	
	public Player(String id, String name, String position, String team, double projection, int cost) {
		this(id, name, Position.stringToPosition(position), Team.valueOf(team), projection, cost);
	}

	public Player(String id, String name, Position position, Team team, double projection, int cost) {
		_id = id;
		_name = name;
		_position = position;
		_team = team;
		_projection = projection;
		_cost = cost;
	}
	
	public String getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}
	
	public Position getPosition() {
		return _position;
	}
	
	public Team getTeam() {
		return _team;
	}
	
	public double getProjection() {
		return _projection;
	}
	
	public int getCost() {
		return _cost;
	}
	
	public double getValue() {
		return _projection/_cost;
	}
	
	public void setProjection(double projection) {
		_projection = projection;
	}
	
	public String toString() {
		return _name + ": " + _projection + " $" + _cost;
	}

	@Override
	public int compareTo(Player other) {
		if (other == null)
			return 1;
		double my_num = 0;
		double other_num = 0;
		switch (_sort_by) {
		case "Value":
			my_num = getValue();
			other_num = other.getValue();
			break;
		case "Position":
			my_num = -Position.positionToInt(getPosition());
			other_num = -Position.positionToInt(other.getPosition());
			break;
		case "Cost":
			my_num = getCost();
			other_num = other.getCost();
			break;
		case "Projection":
		default:
			my_num = getProjection();
			other_num = other.getProjection();
		}
		if (my_num < other_num) {
			return 1;
		} else if (my_num > other_num) {
			return -1;
		}
		return 0;
	}

}
