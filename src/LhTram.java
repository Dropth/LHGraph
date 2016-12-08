//package TPNOTE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.APSP.APSPInfo;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.GraphParseException;

/**
 * LhTram.java
 * 
 * Fichier concernant l'étude du graph avec tram
 * 
 * @author Florian
 *
 */

public class LhTram {
	
	final static double PIED = 4.5;
	final static double VOITURE = 15.0;
	final static double TRAM = 40.0;
	final static double TMPFIXE = 0.25;
	final static double T = 5;
	
	static ArrayList<Edge> alEdge;
	
	public static void main(String[] args) throws ElementNotFoundException, IOException, GraphParseException {
		// TODO Auto-generated method stub
		
		long debut = System.currentTimeMillis();

		alEdge = new ArrayList<Edge>();
		
		Graph graph = new SingleGraph("LH");
		graph.read("data/lh_tram.dgs");
		System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph.addAttribute("ui.stylesheet", "url('resources/lh.css')");
		//graph.addAttribute("ui.stylesheet", "url(./lh.css)");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        
        Graph tram = initGraphTram(graph);
        tram.addAttribute("ui.stylesheet", "url(./lh.css)");
        tram.addAttribute("ui.quality");
        tram.addAttribute("ui.antialias");
        
        tram.display();
        
        double startMin =-1;
		double endMin =-1;
		
		String departTram="";
		String arriveeTram="";
        
        int cpt=0;
        int cptPied =0;
        int cptVoiture =0;
        int cptTram=0;
        
        double distanceFinal=0;
        double distancePied=0;
        double distanceVoiture=0;
        double distanceTramAf =0;
        
        double tempsTotale=0;
        
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        APSP apsp = new APSP(tram,"length", false);

		// Compute the shortest paths in g from A to all nodes
		dijkstra.init(graph);
		
		for(Node n: graph.getEachNode()){
			dijkstra.setSource(n);
			dijkstra.compute();
			String nodeTramLePlusCourt="";
			double trajetTramAPied=-1;
			
			for(Node n1: tram.getEachNode() ){

					if((trajetTramAPied > dijkstra.getPathLength(graph.getNode(n1.getId())) || nodeTramLePlusCourt == "") && 
																(n1.hasAttribute("tramA") || n1.hasAttribute("tramB"))){
						
						nodeTramLePlusCourt=n1.getId();
						trajetTramAPied = dijkstra.getPathLength(graph.getNode(n1.getId()));
						
					}

			}
			n.setAttribute("nodeTramLePlusCourt", nodeTramLePlusCourt);
			n.setAttribute("trajetTramAPied", trajetTramAPied);
		}
		
		for(Node start:graph) {
			
			dijkstra.setSource(start);
			dijkstra.compute();
			
			for (Node end : graph) {
				
				double distanceTotale = dijkstra.getPathLength(end)*0.001 ;
				
				double tempsPied = distanceTotale/PIED;
				double tempsVoiture = distanceTotale/VOITURE + TMPFIXE;
				
				double tmpDisPiedTram = start.getAttribute("trajetTramAPied");
				double distancePiedAlleTram = tmpDisPiedTram*0.001;
				double tempsPiedAlleTram = (distancePiedAlleTram)/PIED;
				double tempsPiedFinTram = 0.0;
				double tempsTram = 0.0;
				double tempsTramTotal = 0.0;
				
				double distancePiedFinTram = 0.0;
				double distanceTram = 0.0;
				
				double distanceTramTot = distancePiedAlleTram;
				
				distancePiedFinTram = (double) end.getAttribute("trajetTramAPied") *0.001;
				
				String debutTram = start.getAttribute("nodeTramLePlusCourt");
				String finTram = end.getAttribute("nodeTramLePlusCourt");
				
				apsp.compute();
				
				APSP.APSPInfo info;
				
				info = tram.getNode(debutTram).getAttribute(APSP.APSPInfo.ATTRIBUTE_NAME);
				
				distanceTram = info.getLengthTo(tram.getNode(finTram).getId())* 0.001;
				
				/*dijkstra2.setSource(tram.getNode(debutTram));
				dijkstra2.compute();
				distanceTram = dijkstra2.getPathLength(tram.getNode(finTram)) *0.001;*/
				
				int changements=1;
				
				
				
				distanceTramTot += distanceTram + distancePiedFinTram;
				
				tempsPiedFinTram = distancePiedFinTram/PIED;
				tempsTram = distanceTram/TRAM;
				
				tempsTramTotal = tempsPiedAlleTram + tempsPiedFinTram + tempsTram +((T/60)/2) * changements;
				
				
				if(tempsVoiture > tempsTramTotal) {
					
					if(tempsTramTotal > tempsPied){
						
						cptPied++;
						distancePied += distanceTotale;
						tempsTotale += tempsPied;
					}
					else {
						//tram
						cptTram++;
						distanceTramAf += distanceTramTot;
						tempsTotale += tempsTramTotal;
						
					}
				}
				else {
					
					if(tempsVoiture > tempsPied) {
						
						cptPied++;
						distancePied += distanceTotale;
						tempsTotale += tempsPied;
					}
					else {
						
						cptVoiture++;
						distanceVoiture += distanceTotale;
						tempsTotale += tempsVoiture;
					}
				}
				
				cpt++;
				distanceFinal += distanceTotale;
			}
		}
		
		
		int pcTrajetPied=(int)Math.round(cptPied*(100.0/(cptPied+cptVoiture+cptTram)));
		int pcTrajetVoiture=(int)Math.round(cptVoiture*(100.0/(cptPied+cptVoiture+cptTram)));
		int pcTrajetTram=(int)Math.round(cptTram*(100.0/(cptPied+cptVoiture+cptTram)));
		
		int pcDistVoiture=(int)Math.round(distanceVoiture*(100.0/(distanceVoiture+distancePied+distanceTramAf)));
		int pcDistPied=(int)Math.round(distancePied*(100.0/(distanceVoiture+distancePied+distanceTramAf)));
		int pcDistTram=(int)Math.round(distanceTramAf*(100.0/(distanceVoiture+distancePied+distanceTramAf)));
		
		
		int tempsMoyen = (int) ((tempsTotale/cpt)*60);
		
		System.out.println("-------------------------------------------------------");
		System.out.println("|                                                     |");
		System.out.println("|                  Parts des trajets                  |");
		System.out.println("|                                                     |");
		System.out.println("-------------------------------------------------------");
		
		System.out.println(pcTrajetPied + " % des trajets à pied");
		System.out.println(pcTrajetVoiture + " % des trajets en voiture");
		System.out.println(pcTrajetTram + " % des trajets en tram\n");
		
		System.out.println("-------------------------------------------------------");
		System.out.println("|                                                     |");
		System.out.println("|                 Parts des distances                 |");
		System.out.println("|                                                     |");
		System.out.println("-------------------------------------------------------");
		
		System.out.println(pcDistPied + " % de la distance à pied");
		System.out.println(pcDistVoiture + " % de la distance en voiture");
		System.out.println(pcDistTram + " % de la distance en tram\n");
		
		System.out.println("-------------------------------------------------------");
		System.out.println("|                                                     |");
		System.out.println("|               Temps Moyen d'un trajet               |");
		System.out.println("|                                                     |");
		System.out.println("-------------------------------------------------------");
		
		System.out.println(tempsMoyen + " minutes est le temps moyen d'un trajet\n");
		
		graph.display(false);
		
		System.out.println("Temps d'execution : " + (System.currentTimeMillis()-debut)*0.001 + " secondes");
	}
	
	public static Graph initGraphTram(Graph g) {
		
		for(Edge e:g.getEachEdge()) {
			
			if (e.hasAttribute("tramA") || e.hasAttribute("tramB"))
				if(!alEdge.contains(e)) {
					alEdge.add(e);
				}
		}
		
		Graph tram = new SingleGraph("Tram");
		
		for(Edge ed:alEdge) {
			
			Node n0 = ed.getNode0();
			Node n1 = ed.getNode1();
			
			
			if(tram.getNode(n0.getId()) == null) {
				
				tram.addNode(n0.getId()).addAttribute("xy", n0.getAttribute("xy"));
				
				if(n0.hasAttribute("tramA")){
					tram.getNode(n0.getId()).setAttribute("tramA", n0.getAttribute("tramA"));
				}
				
				if(n0.hasAttribute("tramB")){
					tram.getNode(n0.getId()).setAttribute("tramB", n0.getAttribute("tramB"));
				}
			}
			
			if(tram.getNode(n1.getId()) == null) {
				
				tram.addNode(n1.getId()).addAttribute("xy", n1.getAttribute("xy"));
				
				if(n1.hasAttribute("tramA")){
					tram.getNode(n1.getId()).setAttribute("tramA", n1.getAttribute("tramA"));
				}
				
				if(n0.hasAttribute("tramB")){
					tram.getNode(n1.getId()).setAttribute("tramB", n1.getAttribute("tramB"));
				}
			}
			
			tram.addEdge(ed.getId(), n0.getId(), n1.getId());
			
			if(ed.hasAttribute("tramA")){
				tram.getEdge(ed.getId()).setAttribute("tramA", ed.getAttribute("tramA"));
			}
			
			if(ed.hasAttribute("tramB")){
				tram.getEdge(ed.getId()).setAttribute("tramB", ed.getAttribute("tramB"));
			}
			
			if(ed.hasAttribute("length")){
				tram.getEdge(ed.getId()).setAttribute("length", ed.getAttribute("length"));
			}
		}
		
		
		return tram;
			
	}

}
