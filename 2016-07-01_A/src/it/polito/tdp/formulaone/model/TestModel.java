package it.polito.tdp.formulaone.model;

import java.time.Year;

public class TestModel {

	public static void main(String[] args) {
		Model model = new Model();
		Integer anno = 1994;
		model.creaGrafo(Year.of(anno));

		System.out.println("Il pilota migliore della stagione "+anno+" è "+
				model.getPilotaMigliore().getSurname().toString()+
				"\nGRANDE CAMPIONE!");
	}

}
