package mapinci.osmHandling;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.common.errors.OsmQueryTooBigException;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.Way;
import map.graph.graphElements.Node;
import map.graph.utils.LengthConverter;

import java.util.List;

public class MapFetcher {


    public MapFragment fetch(Node n, Double searchNodeRadius){
        OsmConnection connection = new OsmConnection("http://api.openstreetmap.org/api/0.6/",
                "mapinci");
        MapStreamHandler handler = new MapStreamHandler();
        LengthConverter lengthConverter = new LengthConverter();

        Double distanceEpsilon = lengthConverter.metersToCoordinatesDifference(2*searchNodeRadius);

        try {
            BoundingBox boundingBox = new BoundingBox(n.getLatitude() - distanceEpsilon / 2, n.getLongitude() - distanceEpsilon / 2, n.getLatitude() + distanceEpsilon / 2, n.getLongitude() + distanceEpsilon / 2);
            new MapDataDao(connection).getMap(boundingBox, handler);
        } catch (OsmQueryTooBigException e){
            return fetch(n,searchNodeRadius - searchNodeRadius*0.1);
        }

        return new MapFragment(handler.getNodes(),handler.getWays());
    }
}
