package bnotai.tekla.material.data;
import java.util.Comparator;

public class FRdrill{
	Pair<Double, Double> first = null;
	Pair<Double, Double> second = null;

	public FRdrill(double x1, double x2, double y1, double y2) {
		first = new Pair<Double, Double>(x1, x2);
		second = new Pair<Double, Double>(y1, y2);
	}
	Double get1x() {
		return first.getFirst();
	}
	Double get1y() {
		return first.getSecond();
	}
	Double get2x() {
		return second.getFirst();
	}
	Double get2y() {
		return second.getSecond();
	}
    @Override
    public String toString() {
        return String.format("[%d, %d; %d, %d]", first.getFirst(), first.getSecond(), second.getFirst(), second.getSecond());
    }
    public static class Comparators {
        public static Comparator<FRdrill> ASC = new Comparator<FRdrill>() {
            @Override
            public int compare(FRdrill fr1, FRdrill fr2) {
                if(fr1.get1x() < 0.0001) {
                	return (int)(fr2.get1y() - fr1.get1y());
                }
                return (int)(fr2.get1x() - fr1.get1x());
            }
        };
    }

}

