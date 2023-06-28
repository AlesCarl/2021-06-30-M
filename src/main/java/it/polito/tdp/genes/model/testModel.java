package it.polito.tdp.genes.model;

public class testModel {

	public static void main(String[] args) {
		Model model = new Model(); 
		model.creaGrafo();
		System.out.println("\nGRADO MAX: "+model.getMax()+", GRADO MIN: "+model.getMinimo());
		
		
		System.out.println("\nNUMERO ARCHI SUP: "+model.ContaArchiSup(3)+", GRADO MIN: "+model.ContaArchiInf(3));
		System.out.println("\nrisultato ricorsione: "+model.getPercorso(3));

		 

	}

}
