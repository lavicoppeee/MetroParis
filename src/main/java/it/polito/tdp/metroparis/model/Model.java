package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private Graph<Fermata, DefaultEdge> graph;
	private List<Fermata> fermate;
	private Map<Integer, Fermata> fermateIdMap;
	
	
	
	/**
	 * CREO GRAFO E LO POPOLO!
	 * 1. dichiaro il grafo vuoto
	 * 2. creo un metodo per richiamare il dao
	 * 3. Data la lista vuota, la popolo tramite un metodo del dao
	 * 4. Aggiungo tutti i vertici al grafo
	 * 5. Aggiungo archi se esiste una connessione tra fa e fb
	 */ 
	public Model() {
		
		this.graph=new SimpleDirectedGraph<>(DefaultEdge.class);
		
		MetroDAO dao=new MetroDAO();
	
		//CREAZIONE VERITICI
		this.fermate=dao.getAllFermate();
		
		//inizializzo identity map
		//ho una mappa che mi mappa id della fermata con oggetto fermata
		
		this.fermateIdMap=new HashMap<>();
		for(Fermata f: this.fermate) {
			fermateIdMap.put(f.getIdFermata(), f);
		}
		
		
		//CREAZIONE VERITICI
		Graphs.addAllVertices(this.graph, this.fermate);
		
		//System.out.println(this.graph);
		
		//CREZIONE ARCHI-- METODO 1 (COPPIE DI VERTICI)
		
		for(Fermata fp: this.fermate) {
			for(Fermata fa: this.fermate) {
				//Se esiste una connessione tra fa e fb
				if(dao.fermateConnesse(fp, fa)) {
					this.graph.addEdge(fp, fa);
				}
					
			}
		}
		
		
		//CREAZIONE DEGLI ARCHI-- METODO 2( DA UN VERTICE, TROVA TUTTI I CONNESSI)
		
		for(Fermata fp: this.fermate) {
			
			List<Fermata> connesse= dao.fermateSuccessive(fp, fermateIdMap);
					
					for(Fermata fa: connesse) {
						this.graph.addEdge(fp, fa);
					}
			
		}
		
		//CREZIONE ARCHI-- METODO 3 (IL DB TI DA GIA' L'ELENCO DEGLI ARCHI) 
		
		List<CoppiaFermate> coppie=dao.coppieFermate(fermateIdMap);
		for(CoppiaFermate c: coppie) {
			this.graph.addEdge(c.getFp(), c.getFa());
		}
		

		//System.out.println(this.graph);
		//stampo numero insieme 
		System.out.format("grafo caricato con", this.graph.vertexSet().size(), this.graph.edgeSet().size());
		
		
	}
	
	
	
	
}
