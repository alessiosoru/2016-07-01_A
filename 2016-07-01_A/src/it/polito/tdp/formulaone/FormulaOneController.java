package it.polito.tdp.formulaone;

import java.net.URL;
import java.time.Year;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FormulaOneController {
	
	Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Year> boxAnno;

    @FXML
    private TextField textInputK;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	this.txtResult.clear();

    	if(this.boxAnno.getValue()==null) {
    		this.txtResult.appendText("Devi inserire un anno compreso tra 1950-2016, estremi inclusi\n");
    		return;
    	}
    	Year anno = this.boxAnno.getValue();
    	if(anno.isBefore(Year.of(1950))||anno.isAfter(Year.of(2016))) {
    		this.txtResult.appendText("Devi inserire un anno compreso tra 1950-2016, estremi inclusi\n");
    		return;
    	}
    	
    	model.creaGrafo(anno);
    	
    	this.txtResult.appendText("GRAFO CREATO !! \nVERTICI "+model.numVertici()+
				"\nARCHI "+model.numArchi()+"\n");
    	this.txtResult.appendText("****\nIl pilota migliore della stagione "+anno+" è "+
				model.getPilotaMigliore().getSurname().toString()+
				"\nGRANDE CAMPIONE!\n");
    	
    }

    @FXML
    void doTrovaDreamTeam(ActionEvent event) {
    	    	
    	this.txtResult.clear();
    	
    	Integer k;
    	
    	if(this.textInputK.getText().isEmpty()) {
    		this.txtResult.appendText("Devi inserire un numero di piloti per il DREAM TEAM\n");
    		return;
    	}
    	try{
    		k = Integer.parseInt(this.textInputK.getText());
    	}catch(NumberFormatException e) {
    		this.txtResult.appendText("Devi inserire un numero di piloti per il DREAM TEAM\n");
    		return;
    	}
    	
    	Map<Integer, Driver> dreamTeam = model.getDreamTeam(k);
    	
    	this.txtResult.appendText("**** DREAM TEAM della stagione selezionata **** \n");
    	for(Driver d : dreamTeam.values()) {
    		this.txtResult.appendText(d.getSurname()+"\n");    	
    	}
    	this.txtResult.appendText("\n *****  GRANDI CAMPIONI !!! ***** \n");
    	
    }

    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }
    
    public void setModel(Model model){
    	this.model = model;
    	this.boxAnno.getItems().clear();
    	this.boxAnno.getItems().addAll(model.getAnni());
    }
}
