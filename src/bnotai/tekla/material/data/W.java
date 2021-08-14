package bnotai.tekla.material.data;

import java.util.*;

public class W {
    String comp;
    ArrayList<Double> x = null;
    ArrayList<Double> z = null;
    public W(){
    	comp = "";
        this.x = new ArrayList<Double>();
        this.z = new ArrayList<Double>();
    }
    public void addx(double x){
        this.x.add(x);
    }
    public void addz(double z){
        this.z.add(z);
    }
}
