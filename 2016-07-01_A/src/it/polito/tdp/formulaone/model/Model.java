package it.polito.tdp.formulaone.model;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private FormulaOneDAO dao;
	private List<Driver> piloti;
	private Map<Integer, Driver> pilotiIdMap;
	private Map<Driver, Integer> numeroVittorie; //pilota-#vittorie
	
	private Graph<Driver, DefaultWeightedEdge> grafo;
	
	private Map<Integer, Driver> bestTeam;
	private Integer bestTassoSconfitta ;

	
	public Model() {
		dao = new FormulaOneDAO();
		this.piloti = new ArrayList<Driver>();
		this.pilotiIdMap = new HashMap<Integer, Driver>();
		this.numeroVittorie = new HashMap<Driver, Integer>();
	}


	public void creaGrafo(Year anno) {
		this.grafo = new SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge>(DefaultWeightedEdge.class);
				
		this.piloti = dao.getDriversByYear(anno, this.pilotiIdMap);
		Graphs.addAllVertices(this.grafo, this.piloti);
//		for(Driver d : this.piloti)
//			System.out.println(d.toString());
		
		this.numeroVittorie = dao.getNumeroVittorie(this.pilotiIdMap, anno);
		// alla prima vittoria aggiungo un arco uscente con peso numero totale vittorie
		for(Driver dSource: numeroVittorie.keySet()) {
			for(Driver dDest : this.piloti) {
				if(this.grafo.getEdge(dSource, dDest)==null) {
					if(!dSource.equals(dDest)) {
						Graphs.addEdgeWithVertices(this.grafo, dSource, dDest,
								this.numeroVittorie.get(dSource));
						
					}
				}
			}
		}
		
		System.out.println("GRAFO CREATO: \nVERTICI"+this.grafo.vertexSet().size()+
				"\nARCHI "+this.grafo.edgeSet().size());
		
	}

	public List<Year> getAnni() {		
		return dao.getAllSeasonsYears();
	}

	public Integer numVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public Integer numArchi() {
		return this.grafo.edgeSet().size();
	}
	public Driver getPilotaMigliore() {
		Driver migliore = null;
		Integer punteggio=0;
		for(Driver d : this.grafo.vertexSet()) {
			Integer punti = this.grafo.outgoingEdgesOf(d).size()-
					this.grafo.incomingEdgesOf(d).size();
			if(punti>punteggio) {
				migliore = d;
				punteggio = punti;
			}
		}
		
		return migliore;
	}

	/*
	 * RICORSIONE
	 * 
	 * Soluzione parziale:mappa di driver team
	 * 	(mappa di driver con tasso di sconfitta = vittorie di altri tot)
	 * Livello della ricorsione: numero di piloti nel team
	 * Casi terminali: 
	 * 	1. livello ricorsione diventa k piloti -> Verifica se il team
	 * 		ha min tasso di sconfitta visto fino ad ora
	 * Generazione delle soluzioni: dato un vertice, aggiungo un vertice
	 * 	non ancora parte del percorso
	 * 
	 * Calcolo il tasso di sconfitta come peso archi entranti per ogni pilota
	 * 
	 */
	
	public Map<Integer, Driver> getDreamTeam(Integer k){
		
		// inizializzo qui il tasso di sconofitta perchè se rifaccio ricorsione da zero
		//  e non trovo tasso migliore della ricorsione prec che non c'entra nulla
		// non riesco a trovare un nuovo best
		bestTassoSconfitta = Integer.MAX_VALUE;
		// VARIABILI PER LA RICORSIONE
		Map<Integer, Driver> parziale = new HashMap<Integer, Driver>();
		this.bestTeam = new HashMap<Integer, Driver>();

		Integer tassoSconfitta = 0;
		cerca(parziale, 0, k, tassoSconfitta);
		
		return this.bestTeam;
		
	}


	private void cerca(Map<Integer, Driver> parziale, int livello, Integer k, Integer tassoSconfitta) {
		System.out.println(livello);
		if(livello == k) {
			// verifico se parziale ha il best tasso sconfitta
			if(tassoSconfitta<this.bestTassoSconfitta) {
				this.bestTassoSconfitta = tassoSconfitta;
				this.bestTeam = new HashMap<Integer, Driver>(parziale);
			}
			// RICORDA DI INSERIRE IL RETURN nella condizione di terminazione
			
			// in questo caso è fuori dall'if sul controllo del best
			// perchè ho comunque raggiunto il livello massimo e devo uscire
		return;
		}
		
		for(Driver d : this.grafo.vertexSet()) {
			if(!parziale.containsKey(d.getDriverId())) {
				
				parziale.put(d.getDriverId(), d);

				Integer aggiuntaTasso = calcolaTassoSconfitta(d, parziale);
				tassoSconfitta = tassoSconfitta + aggiuntaTasso;
				
				cerca(parziale, livello+1, k, tassoSconfitta);
				
				parziale.remove(d.getDriverId(), d);
				tassoSconfitta = tassoSconfitta - aggiuntaTasso;
				
			}
		}
	}


	private Integer calcolaTassoSconfitta(Driver d, Map<Integer, Driver> parziale) {
		Integer aggiuntaTasso = 0;
		System.out.println("DREAM TEAM\n");
		for(Driver driv : parziale.values()) {
			System.out.println("dt"+driv.getSurname());
		}
			for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(d)) {
				System.out.println("battenti"+this.grafo.getEdgeSource(e).getSurname());
				if(!parziale.containsKey(this.grafo.getEdgeSource(e).getDriverId())) {
					
					aggiuntaTasso = (int) (aggiuntaTasso + this.grafo.getEdgeWeight(e));
			}
		}
		return aggiuntaTasso;
	}
	
	
	
	
	
	
	

}
