//package TPNOTE;

import java.io.IOException;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.GraphParseException;

/**
 * LhSansTram.java
 * 
 * Fichier concernant l'étude du graph sans tram
 * 
 * @author Florian
 *
 */

public class LhSansTram {
	
	final static double PIED = 4.5;
	final static double VOITURE = 15.0;
	final static double TMPFIXE = 0.25;
	
	public static void main(String[] args) throws ElementNotFoundException, IOException, GraphParseException {
		// TODO Auto-generated method stub
		
		long debut = System.currentTimeMillis();

		Graph graph = new SingleGraph("LH");
		//graph.read("./lh_tram.dgs");
		graph.read("data/lh_tram.dgs");		
		System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        //graph.addAttribute("ui.stylesheet", "url(./lh.css)");
		graph.addAttribute("ui.stylesheet", "url('resources/lh.css')");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        
        
        
        int cpt=0;
        int cptPied =0;
        int cptVoiture =0;
        
        double distanceFinal=0;
        double distancePied=0;
        double distanceVoiture=0;
        
        double tempsTotale=0;
        
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");

		// Compute the shortest paths in g from A to all nodes
		dijkstra.init(graph);
		
		for(Node start:graph) {
			
			dijkstra.setSource(start);
			dijkstra.compute();
			
			//cpt++;
			//System.out.println(cpt);
			
			for (Node end : graph) {
				
				//end.addAttribute("ui.style", "fill-color: red;");
				
				double distanceTotale = dijkstra.getPathLength(end)*0.001 ;
				
				double tempsPied = distanceTotale/PIED;
				double tempsVoiture = distanceTotale/VOITURE + TMPFIXE;
				
				if(tempsPied <= tempsVoiture){
				
					cptPied++;
					distancePied += distanceTotale;
					tempsTotale += tempsPied;
				}
				else {

					cptVoiture++;
					distanceVoiture += distanceTotale;
					tempsTotale += tempsVoiture;
				}
				
				cpt++;
				distanceFinal += distanceTotale;
				
			}
			
		}
		
		//System.out.println(cpt);
		
		
		
		//int pcTrajetPied = (cptPied*100)/cpt;
		//int pcTrajetVoiture = 100-pcTrajetPied;
		
		int pcTrajetPied=(int)Math.round(cptPied*(100.0/(cptPied+cptVoiture)));
		int pcTrajetVoiture=(int)Math.round(cptVoiture*(100.0/(cptPied+cptVoiture)));
		
		int pcDistPied=(int)Math.round(distancePied*(100.0/(distanceVoiture+distancePied)));
		int pcDistVoiture=(int)Math.round(distanceVoiture*(100.0/(distanceVoiture+distancePied)));
		
		
		//double pcDistPied = (distancePied*100)/distanceFinal;
		//double pcDistVoiture = (distanceVoiture*100)/distanceFinal;
		
		int tempsMoyen = (int) ((tempsTotale/cpt)*60);
		
		System.out.println("-------------------------------------------------------");
		System.out.println("|                                                     |");
		System.out.println("|                  Parts des trajets                  |");
		System.out.println("|                                                     |");
		System.out.println("-------------------------------------------------------");
		
		System.out.println(pcTrajetPied + " % des trajets à pied");
		System.out.println(pcTrajetVoiture + " % des trajets en voiture\n");
		
		System.out.println("-------------------------------------------------------");
		System.out.println("|                                                     |");
		System.out.println("|                 Parts des distances                 |");
		System.out.println("|                                                     |");
		System.out.println("-------------------------------------------------------");
		
		System.out.println(pcDistPied + " % de la distance à pied");
		System.out.println(pcDistVoiture + " % de la distance en voiture\n");
		
		System.out.println("-------------------------------------------------------");
		System.out.println("|                                                     |");
		System.out.println("|               Temps Moyen d'un trajet               |");
		System.out.println("|                                                     |");
		System.out.println("-------------------------------------------------------");
		
		System.out.println(tempsMoyen + " minutes est le temps moyen d'un trajet\n");
		
		graph.display(false);
		
		System.out.println("Temps d'execution : " + (System.currentTimeMillis()-debut)*0.001 + " secondes");
	}

}
