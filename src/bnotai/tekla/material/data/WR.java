package bnotai.tekla.material.data;

import java.util.*;


public class WR {
    String comp = "";
    ArrayList<Double> model;
    double xp;
    double xn; 
    double zp;
    double zn;
    double os;
    public WR(){
    	comp = "";
    	model = new ArrayList<Double>(5);
    }
    public void add(ArrayList<Double> model){
    	this.model = model;
    }
}
