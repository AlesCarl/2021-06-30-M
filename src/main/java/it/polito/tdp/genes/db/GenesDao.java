package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.genes.model.Genes;
import it.polito.tdp.genes.model.Interactions;


public class GenesDao {
	
	
	
	public List<Genes> getGenesCromosoma(int cromosoma){
		String sql = "select g.* "
				+ "from genes g "
				+ "where g.`Chromosome`= ? " ;
		
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, cromosoma);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	
	
	//solo per popolare la mappa 
	public void getAllGenes(Map<String,Genes> idMap){
		String sql = "select g.* "
				+ "from genes g ";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				
				idMap.put(res.getString("GeneID"), genes); 
				
			}
			res.close();
			st.close();
			conn.close();
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	
	
	
	//vertici grafo
	public List<Integer> getAllCromosomi(){
		String sql = " select distinct g.`Chromosome` "
				+ "from genes g "
				+ "where g.`Chromosome`!= 0 "; 
		
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				result.add(res.getInt("g.Chromosome"));
				
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	

	// prendo le distinct interactions di due vertici (cromosomi) 
	public List<Interactions> collegamentoArchi(int c1, int c2, Map<String,Genes> idMap) {
		

		String sql = "select distinct i.* "
				+ "from interactions i, genes g1, genes g2 "
				+ "where i.`GeneID1`= g1.`GeneID` and i.`GeneID2`= g2.`GeneID` "
				+ "and g1.`Chromosome`= ? and g2.`Chromosome`=? "; 
		
				//  5   11
		
		List<Interactions> result = new ArrayList<Interactions>();
		//boolean result= false; 
		Connection conn = DBConnect.getConnection();

		try {
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, c1);
			st.setInt(2, c2);

			ResultSet res = st.executeQuery();
			
		
			while (res.next()) {
				
				Interactions inter = new Interactions (idMap.get(res.getString("i.GeneID1")), idMap.get(res.getString("i.GeneID2")),res.getString("i.Type"),
					                 res.getDouble("i.Expression_Corr"));
				
				result.add(inter);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}





	
	
}
