import map.graph.DataSculptor;
import map.graph.algorithm.ShapeFinder;
import map.graph.graphElements.*;
import org.apache.commons.logging.Log;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.search.Query;
import org.junit.Before;
import org.junit.Test;
import se.kodapan.osm.domain.OsmObject;
import se.kodapan.osm.domain.Way;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.domain.root.indexed.IndexedRoot;
import se.kodapan.osm.parser.xml.OsmXmlParserException;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShapeFinderTest {

    private Graph graph;
    private Logger log;

    @Before
    public void setup() throws IOException, OsmXmlParserException {
        OsmFetcher gf = new OsmFetcher();
        DataSculptor ds = new DataSculptor();
        IndexedRoot<Query> index = gf.makeGraph("test.osm");

        Map<OsmObject, Float> hits = ds.narrowDown(0.0,6.6, 6.6, 0.0, index);

        graph = ds.rebuildGraph(index,hits);

        Collection<Segment> segments = graph.getSegments().values();
        log = Logger.getLogger("ShapeFinderTest");

        segments.forEach(System.out::println);
    }

    @Test
    public void findPerfectlyFittedRoute(){

        Node startNode = graph.getNodeByCoordinates(2.0,1.0);
        log.info(String.format("Start node [id: %d] [long: %f] [lat: %f]", startNode.getId(), startNode.getLongitude(), startNode.getLatitude()));

        List<Segment> shape = createShapeSegments(0.0);
        ShapeFinder shapeFinder = new ShapeFinder(graph,shape);
        Graph foundGraph = shapeFinder.findShape(startNode,0.0,0.0);
        foundGraph.getSegments().values().forEach(System.out::println);
        System.out.println("----------------------------------------------------");
        for(Segment s: foundGraph.getSegments().values()){
            System.out.println(s);
        }
    }

    /* creates shape
        4 -- 2
        2 -- 1
        1 -- 3
        3 -- 4
    */
    private List<Segment> createShapeSegments(double noiseRange ){
        List<Segment> result = new LinkedList<>();
        List<Node> nodes = new ArrayList<>(7);
        NodeFactory nf = new NodeFactory();
        SegmentFactory sf = new SegmentFactory();
        Random random = new Random();

        nodes.add(0,nf.newNode(0.0,0.0));
        nodes.add(1,nf.newNode(2.0 + random.nextDouble()*noiseRange,1.0 + random.nextDouble()*noiseRange));
        nodes.add(2,nf.newNode(4.0 + random.nextDouble()*noiseRange,1.0 + random.nextDouble()*noiseRange));
        nodes.add(3,nf.newNode(5.0 + random.nextDouble()*noiseRange,3.0 + random.nextDouble()*noiseRange));
        nodes.add(4,nf.newNode(3.0 + random.nextDouble()*noiseRange,4.0 + random.nextDouble()*noiseRange));
        nodes.add(5,nf.newNode(1.0 + random.nextDouble()*noiseRange,3.0 + random.nextDouble()*noiseRange));

        nodes.forEach(n -> System.out.println(String.format("[id: %d] [Lon: %f] [Lat %f]", n.getId(), n.getLongitude(), n.getLatitude() )));

        result.add(0,sf.newFullSegment(nodes.get(4),nodes.get(2)));
        result.add(1,sf.newFullSegment(nodes.get(2),nodes.get(1)));
        result.add(2,sf.newFullSegment(nodes.get(1),nodes.get(3)));
        result.add(3,sf.newFullSegment(nodes.get(3),nodes.get(4)));

        return result;
    }


}
