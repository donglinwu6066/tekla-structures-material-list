package bnotai.tekla.material.data;

import java.util.*;

public class FR {
    String comp;
    ArrayList<Double> x;
    ArrayList<Double> y;
    public FR(){
    	comp = "";
        this.x = new ArrayList<Double>();
        this.y = new ArrayList<Double>();
    }
    public void addx(double x){
        this.x.add(x);
    }
    public void addy(double y){
        this.y.add(y);
    }
}
