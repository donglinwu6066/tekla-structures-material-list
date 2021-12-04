package bnotai.tekla.material.fileio;
import java.util.*;

import bnotai.tekla.material.data.Pair;
import bnotai.tekla.material.data.Triple;
import bnotai.tekla.material.data.FRdrill;
import bnotai.tekla.material.data.Wdrill;

public class Interpreter {
	int drillwidth = 500; 
	Hashtable<String, Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>> rawncl = null;
	public Interpreter(Hashtable<String, Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>> rawncl){
		this.rawncl = rawncl; 
	}
	public void genConnCode() {
        List<FRdrill> lwArr = new ArrayList<>();
        List<Wdrill> absArr = new ArrayList<>();
        List<FRdrill> rwArr = new ArrayList<>();
        
        // Getting an iterator
        Iterator nclIte = rawncl.entrySet().iterator();
        
        while (nclIte.hasNext()) {
            Map.Entry mapElement = (Map.Entry)nclIte.next();
//            int marks = ((int)mapElement.getValue() + 10);
//            System.out.println(mapElement.getKey() + " : " + marks);
            Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>
            	plates = (Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>)mapElement.getValue();
            List<Triple<Double, Double, Double>> lw = plates.getFirst();
            List<Triple<Double, Double, Double>> abs = plates.getSecond();
            List<Triple<Double, Double, Double>> rw = plates.getThird();

            if(lw != null) {
            	for(int i=0; i<lw.size() ; i+=2) {
            		readPlatesR(lwArr, lw, i);
            	}
            }
            if(abs != null) {
            	int i = 0;
            	while(i<abs.size()) {
            		i += readPlatesW(absArr, abs, i);
            	}
            }
            if(rwArr != null) {
            	for(int i=0; i<rw.size() ; i+=2) {
            		readPlatesR(rwArr, rw, i);
            	}
            }
            
            
        }
        Collections.sort(lwArr, FRdrill.Comparators.ASC);
        Collections.sort(rwArr, FRdrill.Comparators.ASC);
        // System.out.println("lwArr \n" + lwArr);
	}
	public void readPlatesR(List<FRdrill> arr, List<Triple<Double, Double, Double>> plate, int idx){
		if(plate.get(idx).getFirst()-plate.get(idx+1).getFirst()<0.001) {
			arr.add(new FRdrill(0, plate.get(idx).getSecond(), 0, plate.get(idx+1).getSecond()));
			System.out.println("lwArr \n" + plate.get(idx).getSecond() + " " + plate.get(idx+1).getSecond());
		}
		else if (plate.get(idx).getSecond()-plate.get(idx+1).getSecond()<0.001){
			arr.add(new FRdrill(plate.get(idx).getFirst(), 0, plate.get(idx+1).getFirst(), 0));
			System.out.println("lwArr \n" + plate.get(idx).getFirst() + " " + plate.get(idx+1).getFirst());
		}
	}
	public int readPlatesW(List<Wdrill> arr, List<Triple<Double, Double, Double>> plate, int idx) {
		return 1;
		
		
	}
//    public int compare(Pair<Double, Double> p1, Pair<Double, Double> p2) {
//        int result = p1.getFirst().compareTo(p2.getSecond()) ;
//        return (result == 0) ? p1.getSecond().compareTo(p2.getSecond()) : result;
//    }
}
