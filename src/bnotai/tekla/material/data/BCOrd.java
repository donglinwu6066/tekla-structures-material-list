package bnotai.tekla.material.data;

import java.util.Vector;
//import org.apache.commons.math3.util.Pair;
/**
 * Recording Universal Beam cutting Order
 * @param {String} spec
 * @param {String} mater
 */
public class BCOrd {
	/**
	 * @param {Vector<String>} comps a vector contains cutting Order of a Universal Beam
	 */
	public int len;
	public Vector<Pair<String, Integer>> comps;
	public BCOrd() {
		len = 0;
		comps = new Vector<Pair<String, Integer>>();
	}
	public void add(String comp) {

		int i=0;
		for(i=0 ;i<comps.size() ;i++) {
			if(this.comps.get(i).getFirst().equals(comp)) {
				int cnt = this.comps.get(i).getSecond()+1;
				this.comps.get(i).setSecond(cnt);
				break;
			}
		}
		if(i == comps.size()) {
			this.comps.add(new Pair<String, Integer>(comp, 1));
		}
	}

	public int size() {
		return comps.size();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BCOrd) {
			BCOrd bcord = (BCOrd) obj;
			if(this.comps.size() == bcord.size()) {
				for(int i = 1 ; i<this.comps.size() ; i++) {
					if(!this.comps.get(i).equals(bcord.comps.get(i))) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comps == null) ? 0 : comps.hashCode());
        return result;
    } 
}
