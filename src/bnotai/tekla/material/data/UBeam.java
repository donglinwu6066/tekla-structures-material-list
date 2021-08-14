package bnotai.tekla.material.data;

/**
 * Recording basic Universal Beam information
 * @param {String} spec
 * @param {String} mater
 * @param {double} len
 */
public class UBeam {
	public String spec;
	public String mater;
	public double len;
	public UBeam() {
		spec = "";
		mater = "";
		len = 0;
	}
	public UBeam(String spec, String mater, double len) {
		this.spec = spec;
		this.mater = mater;
		this.len = len;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UBeam) {
			UBeam ubeam = (UBeam) obj;
			double lenComp = this.len - ubeam.len;
			if(this.spec.equals(ubeam.spec) && this.mater.equals(ubeam.mater) && Math.abs(lenComp)<0.0001) {
				 return true;
			}
		}
			return false;
	}
}
