package map.graph.graphElements;

import java.util.Comparator;

public class Node implements Comparable<Node>{

    private final long id;
    private Double longitude;
    private Double latitude;

    protected Node (long id, se.kodapan.osm.domain.Node n){
        this.id = id;
        initiateWithNode(n);
    }

    private void initiateWithNode(se.kodapan.osm.domain.Node n) {
        this.longitude = n.getLongitude();
        this.latitude = n.getLatitude();
    }

    public long getId() {
        return id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    @Override
    public int compareTo(Node n) {
        if(id == n.getId())
            return 0;
        else
            return id > n.getId() ? 1 : -1;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Node){
            Node n = (Node) obj;
            return id == n.getId();
        }
        else
            return super.equals(obj);
    }
}
