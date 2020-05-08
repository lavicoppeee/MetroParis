package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.CoppiaFermate;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}
	
	public boolean fermateConnesse(Fermata fp, Fermata fa) {
		final String sql = "SELECT COUNT(*) as C FROM connessione WHERE id_stazP=? and id_stazA=? ";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, fp.getIdFermata());
			st.setInt(2, fa.getIdFermata());
			ResultSet rs = st.executeQuery();
			
			rs.first();
			int linee=rs.getInt("C");
			conn.close();
			
			return linee >= 1;

			

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
	
		
	}
	
	/**
	 * Data una fermata di partenza dammi tutte quelle di arrivo 
	 * @param fp
	 * @return
	 */
	public List<Fermata> fermateSuccessive(Fermata fp, Map<Integer, Fermata> fermateIdMap) {
		final String sql = "SELECT DISTINCT id_stazA as C FROM connessione WHERE id_stazP=? ";

		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, fp.getIdFermata());
			ResultSet rs = st.executeQuery();

			// tramite id map dai numeri passo ai valori
			// creo idMap prima
			while (rs.next()) {
				int id_fa = rs.getInt("id_stazA");
				fermate.add(fermateIdMap.get(id_fa));
			}
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<CoppiaFermate> coppieFermate(Map<Integer, Fermata> fermateIdMap) {
		// TODO Auto-generated method stub
		
		final String sql = "SELECT DISTINCT id_stazP, id_stazA as C FROM connessione ";
		
		List<CoppiaFermate> fermate = new ArrayList<CoppiaFermate>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			// tramite id map dai numeri passo ai valori
			// creo idMap, converto gli interi in oggetti ed accordarli nella lista
			//Ho dovuto creare la classe coppiaFermate per avere una corrispondeza univoca e facilitare il tutto
			
			while (rs.next()) {
				int id_fa = rs.getInt("id_stazA");
				int id_fp = rs.getInt("id_stazP");
				
				CoppiaFermate c=new CoppiaFermate(fermateIdMap.get(id_fp),fermateIdMap.get(id_fa));
				fermate.add(c);
	
			}
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
		
	}

	//lista di coppie di fermate
	


}
