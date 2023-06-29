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
    private List<Integer> bestCammino ; 
   
    

    

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
	 		
	 		************
	 		i due CROMOSOMI abbiano due geni ( uno a testa ) che compaiano in una riga 
	 		della tabella interactions
	 		************
	 		*
	 		Si noti che, per ciascun cromosoma, possono esistere più geni, e 
	 		ciascuno di essi potrebbe essere presente 
	 		più volte (associato a function diverse).
	 		*/
	 		
	 		
	 		
	 		/*
	 1. dato un cromosoma, trovo tutti i geni che hanno quel cromosoma --> listGenesDaCromosoma
	 2. 
	 		 */
	 	 List<Interactions> allInteractionsCromosomi= new ArrayList<>();
	 		
	 		for(Integer c1: this.allCromosomi) {
	 			for(Integer c2: this.allCromosomi) {
	 				
	 				if(c1!=c2) {  // provo così perche orientato
	 				
	 					allInteractionsCromosomi=dao.collegamentoArchi(c1,c2,idMapGenes);
	 					
	 					  if(allInteractionsCromosomi.size()!=0) {
	 						
	 				    	 double peso= calcoloPeso(allInteractionsCromosomi); 
	 			
	 		 					Graphs.addEdgeWithVertices(this.graph, c1, c2, peso);
	 		 		
	 			 			
	 		        }
	 			}
	 		}
   	 }
	 		System.out.println("\nNUMERO ARCHI GRAFO: " +this.graph.edgeSet().size());
	
	 }
    
    
    
    
    
	List <DefaultWeightedEdge> listArchiSup= new ArrayList<>() ;

    public int ContaArchiSup(int soglia) { // soglia = 5; 
    	
    	
    	for( DefaultWeightedEdge ee: this.graph.edgeSet() ) {

			if( this.graph.getEdgeWeight(ee) > soglia) {
				listArchiSup.add(ee); 
			}
				
		}
		return listArchiSup.size();
		
	}
    public int ContaArchiInf(int soglia) { // soglia = 5; 
    	
    	List<DefaultWeightedEdge> list= new ArrayList<>() ; 
    	
    	for( DefaultWeightedEdge ee: this.graph.edgeSet() ) {

			if( this.graph.getEdgeWeight(ee) < soglia ) {
				list.add(ee); 
			}
				
		}
		return list.size();
		
	}
	
	
    
    //arco c1 - c2
	private double calcoloPeso( List<Interactions> allInteractionsCromosomi) {
		double peso = 0.0; 

			for(Interactions ii: allInteractionsCromosomi) 
				peso+= ii.getExpressionCorr();
		
		return peso;	
	}
	
	
	public double getMinimo() { 
		double min=100000;
		
		for( DefaultWeightedEdge ee: this.graph.edgeSet() ) {

			if( this.graph.getEdgeWeight(ee) < min ) {
				min= graph.getEdgeWeight(ee); 
			}
				
		}
		return min;
		
	}
	
	public double getMax() { 
		double max=0;
		
		for( DefaultWeightedEdge ee: this.graph.edgeSet() ) {

			if( this.graph.getEdgeWeight(ee) > max ) {
				max= graph.getEdgeWeight(ee); 
			}
				
		}
		return max;
		
	}
	
	/*****************  RICORSIONE:  ******************/ 
	
	/* 
	 * determinare il più lungo cammino di vertici (cromosomi) che sia composto esclusivamente da 
	 * archi di peso >S. 
	 
	 * La lunghezza del cammino sarà valutata dalla somma dei pesi degli archi
	 */
	
	
	double bestSumArchi; 
	
	public List<Integer> getPercorso ( int soglia ) {
		
       List <Integer> parziale = new ArrayList<>() ; 
		
		this.bestCammino= new ArrayList<>() ; 
		
		double sumPesiArchi = 0.0; 
		this.bestSumArchi= 0.0; 
		
		//int livello=0; 
		
		parziale.add ( verticeMax()) ;  
/**  ho scelto come partenza il vertice sorgente dell'arco con grado max  */ 
		
		//System.out.println("SIZE LIST: " +listArchiSup.size());
		
		ricorsione(parziale,listArchiSup,sumPesiArchi ); 
		
		
		return this.bestCammino;  //lista vertci 
		
		
		
	}
// ritorna il vertice sorgente dell'arco con grado max. 
	private Integer verticeMax() {
		
		DefaultWeightedEdge maxEdge = null; 
		double max=0; 
			
			for( DefaultWeightedEdge ee: listArchiSup ) {
				
				if( this.graph.getEdgeWeight(ee) > max ) {
					 max= graph.getEdgeWeight(ee); 
					 maxEdge= ee; 
					 
				}
			}
					
		return graph.getEdgeSource(maxEdge);
	}

	
	private void ricorsione(List<Integer> parziale, List<DefaultWeightedEdge> listArchiSup, double sumPesiArchi) {
		
		Integer current = parziale.get(parziale.size()-1);
		
		
		/* condizione uscita **/    // non so quando fermarmi .... 
		
		/* if(parziale.size()==this.listArchiSup.size()) {
			return; 
		} */ 
		
		if(sumPesiArchi> bestSumArchi) {
			
			bestSumArchi= sumPesiArchi; 
			bestCammino= new ArrayList<>(parziale);
		}
		
		
		

		/** continuo ad aggiungere elementi in parziale **/ 
	     
		List<Integer> successori= Graphs.successorListOf(graph, current);
		List<Integer> newSuccessori= new ArrayList<>();  
	
		
		for(Integer ii: successori) {
		    if(!parziale.contains(ii)) {
		    	newSuccessori.add(ii);  // QUI METTO SOLO I VERTICI CHE NON SONO GIA' STATI USATI
		    }
	  }
	
		
		/** ***  condizione di uscita  ***/
	    if( newSuccessori.size()==0 ) {
	    	return; 
	    }
	     
		
	     for(Integer a: newSuccessori) {
	    	 
	    	 
	    	    //********* QUI COLLEGO edge e vertex ********* 
	    	   if( listArchiSup.contains(graph.getEdge(current, a)) ){ 
	    	    	 
	    	      sumPesiArchi+= graph.getEdgeWeight(graph.getEdge(current, a));
		    	  parziale.add(a);
		    	  
		    	  ricorsione(parziale,listArchiSup ,sumPesiArchi);
		    	  
		    	  parziale.remove(a); // backTracking
		    	  sumPesiArchi-= graph.getEdgeWeight(graph.getEdge(current, a)); //backTracking
	    	     }
	     
	      }
		
	    
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
    
    
}