package fanduel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

// Reads the lineup template file downloaded from FanDuel (selects the most recently
// downloaded csv). All players in the file are read and stored as player objects
// and are assigned a projection based on data from www.fantasypros.com. The lineup
// template is then edited to be filled with the most optimal lineup (made by a Lineup
// object) according to the given inputs.

public class Reader {

	private static List<Player> _players = new ArrayList<Player>();
	private static File _file;
	private static String _format;

	public static void main(String[] args) throws IOException {
		loadPlayers();
		loadProjections();
		setLineup();
	}

	private static void loadPlayers() throws IOException {
		_file = mostRecentDownload();
		BufferedReader csvReader = new BufferedReader(new FileReader(_file));
		String row = csvReader.readLine();
		setFormat(row);
		csvReader.readLine();
		csvReader.readLine();
		csvReader.readLine();
		csvReader.readLine();
		csvReader.readLine();
		csvReader.readLine();
		while ((row = csvReader.readLine()) != null) {
			Scanner data = new Scanner(row);
			data.useDelimiter("\",\"");
			data.next();
			String id = data.next();
			while (id.equals("")) {
				id = data.next();
			}
			id = data.next();
			String position = data.next();
			data.next();
			String name = data.next();
			data.next();
			data.next();
			data.next();
			int salary = data.nextInt();
			data.next();
			String team = data.next();
			data.close();
			_players.add(new Player(id, name, position, team, salary));
		}
		csvReader.close();
	}
	
	private static void loadProjections() {
		for (Position position : Position.values()) {

			String format = ".php?scoring=" + "HALF"; // "STD", "HALF", "PPR"
			if (position != Position.RB && position != Position.WR && position != Position.TE) {
				format = "";
			}

			Document fp;
			try {
				fp = Jsoup.connect(
						"https://www.fantasypros.com/nfl/projections/" + position.toString().toLowerCase() + format)
						.get();
			} catch (Exception e) {
				System.err.println("Could not connect to projections");
				return;
			}

			String table = fp.getElementsByClass("mobile-table").toString();
			Scanner row = new Scanner(table);
			row.useDelimiter("/tr>[\\s]*<tr");
			if (position != Position.K && position != Position.DST)
				row.next();
			while (row.hasNext()) {
				Scanner cell = new Scanner(row.next());
				cell.useDelimiter("/td>[\\s]*<td");
				Scanner name = new Scanner(cell.next());
				name.useDelimiter("class=\"player-name\">");
				name.next();
				Scanner data = new Scanner(name.next());
				data.useDelimiter("</a> ");
				String full_name = data.next();
				Team team = position != Position.DST ? Team.valueOf(data.next().replaceAll(" [^$]+", "")) : null;
				name.close();
				data.close();

				while (cell.hasNext()) {
					String info = cell.next();
					if (cell.hasNext())
						continue;
					Scanner projection = new Scanner(info);
					projection.useDelimiter("\">");
					projection.next();
					setPlayerProjection(full_name, position, team,
							Double.parseDouble(projection.next().replaceAll("[^0-9.]+", "")));
					projection.close();
				}
				cell.close();
			}
			row.close();
		}
	}
	
	private static void setLineup() throws IOException {
		BufferedReader csvReader = new BufferedReader(new FileReader(_file));
		List<String> rows = new ArrayList<String>();
		String row;
		while ((row = csvReader.readLine()) != null) {
			rows.add(row);
		}
		csvReader.close();

		PrintWriter writer = new PrintWriter(_file);
		for (int i = 0; i < rows.size(); i++) {
			if (i == 1) {
				if (rows.get(0).startsWith("\"entry_id\",\"contest_id\",\"contest_name\","))
					writer.print(rows.get(1).substring(0,2+rows.get(1).indexOf("\",\"", 38)));
				for (Player p : (new Lineup(_players, _format)).getBestLineup())
					writer.print("\"" + p.getId() + "\",");
				writer.println();
			} else
				writer.println(rows.get(i));
		}
		writer.close();
	}

	private static void setFormat(String top_row) {
		if (top_row.contains("MVP")) {
			if (top_row.contains("AnyFLEX\",\"AnyFLEX\",\"AnyFLEX")) {
				_format = "Single Game";
			} else {
				_format = "3-Man Challenge";
			}
		} else {
			if (top_row.contains("Tier")) {
				_format = "QuickPick Kickers";
			} else if (top_row.contains("SuperFlex")) {
				_format = "SuperFlex";
			} else {
				_format = "Full Roster";
			}
		}
	}

	private static void setPlayerProjection(String name, Position position, Team team, double projection) {
		for (Player player : _players) {
			if (player.getPosition() != position || team != null && player.getTeam() != team)
				continue;

			Scanner s1 = new Scanner(name);
			Scanner s2 = new Scanner(player.getName());

			int full_match = 0;
			int partial_match = 0;
			while (s1.hasNext() && s2.hasNext()) {
				String name1 = s1.next().toLowerCase().replaceAll("[^a-z]", "");
				String name2 = s2.next().toLowerCase().replaceAll("[^a-z]", "");

				full_match += name1.equals(name2) ? 1 : 0;

				partial_match += partialMatch(name1, name2) ? 1 : 0;

				if (full_match > 0 && partial_match > 1) {
					player.setProjection(projection);
				}
			}
			s1.close();
			s2.close();
		}
	}

	private static boolean partialMatch(String name1, String name2) {
		if (name1.contains(name2))
			return true;
		if (name2.contains(name1))
			return true;

		for (int i = name1.length(); i >= 3; i--) {
			if (name2.contains(name1.subSequence(0, i)))
				return true;
		}

		for (int i = name2.length(); i >= 3; i--) {
			if (name1.contains(name2.subSequence(0, i)))
				return true;
		}

		return false;
	}

	private static File mostRecentDownload() throws IOException {
		File fl = new File(System.getProperty("user.home") + "/" + "Downloads");
		File[] files = fl.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".csv");
			}
		});
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				choice = file;
				lastMod = file.lastModified();
			}
		}
		if (choice == null)
			throw new IOException("Lineup template not found");
		return choice;
	}
}
