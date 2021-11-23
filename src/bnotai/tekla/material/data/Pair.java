package bnotai.tekla.material.data;

public class Pair< U, V> {

    private U first;
    private V second;

    public Pair(U i, V j) {
        this.first = i;
        this.second = j;
    }
    
    public U getFirst() { return first; }
    public void setFirst(U first) { this.first = first; }
    public V getSecond() { return second; }
    public void setSecond(V second) { this.second = second; }
    public String toString() {
        return "[" + getFirst() + ", " + getSecond() + "]";
    }
}