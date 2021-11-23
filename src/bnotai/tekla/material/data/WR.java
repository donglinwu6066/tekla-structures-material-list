package bnotai.tekla.material.data;

import java.util.*;


public class WR {
    public String code = "";
    public ArrayList<Double> x = null;
    public ArrayList<Double> z = null;
    public WR(){
    	code = "";
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
