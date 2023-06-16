package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;


public class Model {
	
	
	GenesDao dao; 
    private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;  // SEMPLICE, PESATO, NON ORIENTATO
    private List<Integer> allCromosomi ; 
    
    Map <String,Genes>idMapGenes= new HashMap<>();
    

    

    public Model() {
    	
    	this.dao= new GenesDao();  
    	this.graph= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
    	this.allCromosomi= new ArrayList<>();
    	
    }
    
    private void loadAllNodes() {
		
    	allCromosomi= dao.getAllCromosomi(); 
    	dao.getAllGenes(idMapGenes); 
 				
 		}
    
    
    public void creaGrafo() {
		 
		 
		 loadAllNodes(); 
	 		//System.out.println("size: " +this.allNACode.size());

		 /** VERTICI */
	    	Graphs.addAllVertices(this.graph, allCromosomi);
	 		System.out.println("NUMERO vertici GRAFO: " +this.graph.vertexSet().size());
	 		
	 		
	 		/** ARCHI */
	 		
	 		/*
	 		Un arco collega due CROMOSOMI diversi solo se: 
	 		
	 		i due cromosomi contengono due geni (uno per cromosoma) che compaiono 
	 		(nello stesso ordine) nella tabella interactions. 
	 		
	 		Si noti che, per ciascun cromosoma, possono esistere più geni, e 
	 		ciascuno di essi potrebbe essere presente 
	 		più volte (associato a function diverse).
	 		*/
	 		
	 		
	 		
	 		/*
	 1. dato un cromosoma, trovo tutti i geni che hanno quel cromosoma --> listGenesDaCromosoma
	 2. 
	 		 */
	 		
	 		for(Integer c1: this.allCromosomi) {
	 			for(Integer c2: this.allCromosomi) {
	 				
	 				if(c1!=c2) {  // provo così perche orientato
	 					double peso= calcoloPeso(c1,c2); 
	 			 
	 			 				if( peso>0 ) {
	 			 					
	 		 						Graphs.addEdgeWithVertices(this.graph, c1, c2, peso);
	 		 						
	 		        }
	 			}
	 		}
   	 }
	 		System.out.println("\nNUMERO ARCHI GRAFO: " +this.graph.edgeSet().size());
	
	 }

    //arco c1 - c2
	private double calcoloPeso(Integer c1, Integer c2) {
		
		double peso = 0.0; 
		
		List<Genes> listGen1= dao.getGenesCromosoma(c1); //all geni del cromosoma c1
		List<Genes> listGen2= dao.getGenesCromosoma(c2); //all geni del cromosoma c2
		
		
		
		for(Genes g1: listGen1 ) {
			for(Genes g2: listGen2 ) {
				if(g1.getGeneId().compareTo(g2.getGeneId())!= 0) {
					
					if(dao.collegamentoArchi(g1.getChromosome(),g2.getChromosome())== true) { // i due geni sono nella stessa function
						return dao.getSumCorrelazione(c1,c2); //calcolo il peso 
					} 
					else if(dao.collegamentoArchi(g2.getChromosome(),g1.getChromosome())== true) { // i due geni sono nella stessa function
						return dao.getSumCorrelazione(c2,c1);
					
				}
			}
			
		}
		
		
	}
    
		return peso;
	
	
	}
	
    
    
}