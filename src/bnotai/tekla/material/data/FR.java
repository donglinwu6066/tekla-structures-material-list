package bnotai.tekla.material.data;

import java.util.*;

public class FR {
    public String code;
    public ArrayList<Double> x;
    public ArrayList<Double> y;
    public FR(){
    	code = "";
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
