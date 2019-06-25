package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	public List<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT year FROM races ORDER BY year " ;
		
		try {
			Connection conn = ConnectDB.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year " ;
		
		try {
			Connection conn = ConnectDB.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Year> getAllSeasonsYears() {
		
		String sql = "SELECT year FROM seasons ORDER BY year " ;
		
		try {
			Connection conn = ConnectDB.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Year> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(Year.of(rs.getInt("year")));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name ";

		try {
			Connection conn = ConnectDB.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors() {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name ";

		try {
			Connection conn = ConnectDB.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}

	public List<Driver> getDriversByYear(Year anno, Map<Integer, Driver> pilotiIdMap) {
		String sql = "SELECT DISTINCT drivers.driverId, drivers.driverRef, drivers.number, drivers.CODE, " + 
				"	drivers.forename,	drivers.surname, drivers.dob, " + 
				"	drivers.nationality, drivers.url " + 
				"FROM drivers, races, results " + 
				"WHERE drivers.driverId = results.driverId AND " + 
				"	races.raceId = results.raceId and " + 
				"	YEAR(DATE) = ? ";

		try {
			Connection conn = ConnectDB.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno.getValue());

			ResultSet rs = st.executeQuery();

			List<Driver> piloti = new ArrayList<>();
			while (rs.next()) {
				Driver d = new Driver(rs.getInt("drivers.driverId"),
						rs.getString("drivers.driverRef"), rs.getInt("drivers.number"),
						rs.getString("drivers.CODE"),rs.getString("drivers.forename"),
						rs.getString("drivers.surname"),rs.getDate("drivers.dob").toLocalDate(), 
						rs.getString("drivers.nationality"), rs.getString("drivers.url"));
				piloti.add(d);
				pilotiIdMap.put(d.getDriverId(), d);
			}

			conn.close();
			return piloti;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}

	public Map<Driver, Integer> getNumeroVittorie(Map<Integer, Driver> pilotiIdMap, Year anno) {
		String sql = "SELECT drivers.driverId as id, count(*) as count " + 
				"FROM drivers, races, results " + 
				"WHERE drivers.driverId = results.driverId AND " + 
				"	races.raceId = results.raceId and " + 
				"	YEAR(DATE) = ? AND position = 1 " + 
				"GROUP BY drivers.driverId, drivers.surname ";

		try {
			Connection conn = ConnectDB.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno.getValue());

			ResultSet rs = st.executeQuery();

			Map<Driver, Integer> vittorie = new HashMap<Driver, Integer>();
			while (rs.next()) {
				Driver d = pilotiIdMap.get(rs.getInt("id"));
				vittorie.put(d, rs.getInt("count"));
			}

			conn.close();
			return vittorie;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
}
