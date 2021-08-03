package parallelmc.parallelutils.modules.expstorage;

import parallelmc.parallelutils.Parallelutils;

import java.sql.*;

public class ExpDatabase {

	private final Parallelutils puPlugin;

	public ExpDatabase(Parallelutils puPlugin) {
		this.puPlugin = puPlugin;
	}

	/**
	 * Retrieves a player's stored experience from the database
	 * @param uuid The player's UUID
	 * @return The number of stored experience points the player has
	 */
	public int getExpForPlayer(String uuid) {
		try (Connection dbConn = puPlugin.getDbConn()){
			if (dbConn == null) throw new SQLException("Unable to establish connection!");
			Statement statement = dbConn.createStatement();
			statement.setQueryTimeout(15);
			ResultSet result = statement.executeQuery("SELECT * FROM ExpStorage WHERE UUID = '" + uuid +"'");
			if (result.next()) {
				int exp = result.getInt("StoredExp");
				statement.close();
				return exp;
			}
			else {
				// if the player doesn't exist in the db just return 0
				statement.close();
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Stores a player's experience into the database
	 * @param uuid The player's UUID
	 * @param exp How much experience to store
	 */
	public boolean storeExpForPlayer(String uuid, int exp) {
		try (Connection dbConn = puPlugin.getDbConn()){
			if (dbConn == null) throw new SQLException("Unable to establish connection!");

			// use ON DUPLICATE KEY to simply update the data if the player already exists in the db
			// https://dev.mysql.com/doc/refman/8.0/en/insert-on-duplicate.html
			PreparedStatement statement = dbConn.prepareStatement(
					"INSERT INTO ExpStorage (UUID, StoredExp) VALUES (?, ?) ON DUPLICATE KEY UPDATE StoredExp = StoredExp + ?"
			);
			statement.setQueryTimeout(15);
			statement.setString(1, uuid);
			statement.setInt(2, exp);
			statement.setInt(3, exp);
			statement.execute();
			dbConn.commit();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Withdraws exp from the database to give to the player
	 * @param uuid The player's UUID
	 * @param exp How much experience to withdraw
	 */
	public boolean withdrawExpForPlayer(String uuid, int exp) {
		try (Connection dbConn = puPlugin.getDbConn()){
			if (dbConn == null) throw new SQLException("Unable to establish connection!");

			// assume the player already exists in the database
			// if a player has 0 stored experience and tries to withdraw, it should never get here
			// see WithdrawExperience.java L28
			PreparedStatement statement = dbConn.prepareStatement(
					"UPDATE ExpStorage SET StoredExp = StoredExp - ? WHERE UUID = ?"
			);
			statement.setQueryTimeout(15);
			statement.setInt(1, exp);
			statement.setString(2, uuid);
			statement.execute();
			dbConn.commit();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
