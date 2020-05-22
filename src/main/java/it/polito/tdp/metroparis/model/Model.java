package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.*;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

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
		
//		//CREZIONE ARCHI-- METODO 1 (COPPIE DI VERTICI)
//		
//		for(Fermata fp: this.fermate) {
//			for(Fermata fa: this.fermate) {
//				//Se esiste una connessione tra fa e fb
//				if(dao.fermateConnesse(fp, fa)) {
//					this.graph.addEdge(fp, fa);
//				}
//					
//			}
//		}
//		
//		
//		//CREAZIONE DEGLI ARCHI-- METODO 2( DA UN VERTICE, TROVA TUTTI I CONNESSI)
//		
//		for(Fermata fp: this.fermate) {
//			
//			List<Fermata> connesse= dao.fermateSuccessive(fp, fermateIdMap);
//					
//					for(Fermata fa: connesse) {
//						this.graph.addEdge(fp, fa);
//					}
//			
//		}
		
		//CREZIONE ARCHI-- METODO 3 (IL DB TI DA GIA' L'ELENCO DEGLI ARCHI) 
		
		List<CoppiaFermate> coppie=dao.coppieFermate(fermateIdMap);
		for(CoppiaFermate c: coppie) {
			this.graph.addEdge(c.getFp(), c.getFa());
		}
		

		//System.out.println(this.graph);
		//stampo numero insieme 
		System.out.format("grafo caricato con", this.graph.vertexSet().size(), this.graph.edgeSet().size());
		
	}
	
	/**
	 * Visita l'intero grafo con la strategia Breath First e ritorna l'insieme dei vertici incontrati.
	 * @param source vertice di partenza della visita
	 * @return insieme dei vertici incontrati 
	 */
	public  List<Fermata> visitaAmpiezza(Fermata source) {
		
		List<Fermata> visita= new ArrayList<>();
		BreadthFirstIterator<Fermata,DefaultEdge> bfv=new BreadthFirstIterator<>(graph,source);
		
		while(bfv.hasNext()) {
			visita.add(bfv.next());
		}
		
		return visita; 
	}
	
public  List<Fermata> visitaProfondita(Fermata source) {
		
		List<Fermata> visita= new ArrayList<>();
		GraphIterator<Fermata,DefaultEdge> bfv=new DepthFirstIterator<>(graph,source);
		
		while(bfv.hasNext()) {
			visita.add(bfv.next());
		}
		
		return visita; 
	}

/**
 * CAMMINO  MINIMO CON DIJKSTRA, SUPER EASY
 * @param partenza
 * @param arrivo
 * @return
 */
public List<Fermata> camminiMinimi(Fermata partenza, Fermata arrivo) {
	DijkstraShortestPath<Fermata,DefaultEdge> dij= new DijkstraShortestPath<>(graph);
	
	 GraphPath<Fermata,DefaultEdge> cammino=dij.getPath(partenza, arrivo);
	 
	  return cammino.getVertexList();
}
	
public Map<Fermata, Fermata>alberoVisita(Fermata source){
	Map<Fermata,Fermata> albero=new HashMap<>();
	albero.put(source,null); //aggiungo la sorgente, non ha padre
	
	GraphIterator<Fermata,DefaultEdge> bfv=new BreadthFirstIterator<>(graph,source);
	bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

		@Override
		public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {}

		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {}

		@Override
		public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
			// la visita sta considerando un nuovo arco
			//questo arco ha scoperto un nuovo vertice?
			//se si, provenendo da dove?
			
			DefaultEdge edge =e.getEdge(); //(a,b): ho scoperto a partendo da b oppure ho scoperto b da a 
			Fermata a = graph.getEdgeSource(edge);
			Fermata b= graph.getEdgeTarget(edge);
			
			//già conosco a come chiave della mappa? vedo se è nella mappa
			if(albero.containsKey(a)) {
				albero.put(b,a);
			}else {
				albero.put(a,b);
			}
			
			
		}

		@Override
		public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void vertexFinished(VertexTraversalEvent<Fermata> e) {
			// TODO Auto-generated method stub
			
		}
		
	});
	
	while(bfv.hasNext()) {
		bfv.next(); //estrai elemento ed ignoralo, attraversare l'arco che mi serve
	}
	return albero; 
}


}
