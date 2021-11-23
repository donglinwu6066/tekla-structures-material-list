package bnotai.tekla.material.data;

import java.util.*;

public class W {
	public String code;
    //xp, xn, zp, zn, os
	public ArrayList<Double> drill = null;
    public W(){
    	code = "";
        this.drill = new ArrayList<Double>();
    }
    public void set(ArrayList<Double> arr){
        this.drill = arr;
    }

}
