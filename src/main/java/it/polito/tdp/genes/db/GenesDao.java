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
	
	
	//NON CREDO SERVA
	public List<Interactions> getAllInteractions( Map<String,Genes> idMap  ){
		
		String sql = "select i.`GeneID1`, i.`GeneID2`,i.`Type`, i.`Expression_Corr` "
				+ "from interactions i "; 

		
		List<Interactions> result = new ArrayList<Interactions>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
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



	public boolean collegamentoArchi(int c1, int c2) {
		

		String sql = "select distinct i.*\n"
				+ "from interactions i, genes g1, genes g2 "
				+ "where i.`GeneID1`= g1.`GeneID` and i.`GeneID2`= g2.`GeneID` "
				+ "and g1.`Chromosome`= ? and g2.`Chromosome`=? "; 
		
				//+ "and g1.`GeneID`='G234334' and g2.`GeneID`='G234684' "; 

		
		boolean result= false; 
		Connection conn = DBConnect.getConnection();

		try {
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, c1);
			st.setInt(2, c2);

			ResultSet res = st.executeQuery();
			
		
				
			//res.first();
			while (res.next()) {
			
			if(res.getString("i.GeneID1")!= null && res.getString("i.GeneID2")!= null) 
				result= true;  
			//vuol dire che esiste un arco. 
				
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}



	public double getSumCorrelazione(Integer c1, Integer c2) {
		
		
		String sql = "select g1.`Chromosome`,  g2.`Chromosome`,  sum(distinct i.`Expression_Corr`) as cn "
				+ "from interactions i, genes g1, genes g2 "
				+ "where i.`GeneID1`= g1.`GeneID` and i.`GeneID2`= g2.`GeneID`  "
				+ "and g1.`Chromosome`= ? and g2.`Chromosome`= ? " ; 
		
				// 5 , 11

		
		double sum= 0.0; 
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, c1);
			st.setInt(2, c2);

			ResultSet res = st.executeQuery();
			
		
				
			res.first();
			sum = res.getDouble("cn"); 
			//vuol dire che esiste un arco. 
				
				
			res.close();
			st.close();
			conn.close();
			return sum;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}


	
	
}
