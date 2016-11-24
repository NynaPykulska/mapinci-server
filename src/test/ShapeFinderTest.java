import map.graph.DataSculptor;
import map.graph.algorithm.ShapeFinder;
import map.graph.algorithm.conditions.ConditionManager;
import map.graph.algorithm.conditions.DirectionCondition;
import map.graph.graphElements.*;
import mapinci.GraphMaker;
import org.apache.lucene.search.Query;
import org.junit.Before;
import org.junit.Test;
import se.kodapan.osm.domain.OsmObject;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.domain.root.indexed.IndexedRoot;
import se.kodapan.osm.parser.xml.OsmXmlParserException;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class ShapeFinderTest {

    private Graph graph;
    private Logger log;

    private void setup(String dataSourceName) throws IOException, OsmXmlParserException {
        OsmFetcher gf = new OsmFetcher();
        DataSculptor ds = new DataSculptor();
        log = Logger.getLogger("ShapeFinderTest");

        IndexedRoot<Query> index = gf.makeGraph(dataSourceName);
        Map<OsmObject, Float> hits = ds.narrowDown(0.0,6.6, 6.6, 0.0, index);
        hits.keySet().forEach(System.out::println);
        graph = ds.rebuildGraph(index,hits);

    }

    /* creates shape
        5 -- 2
        2 -- 3
        3 -- 1
        1 -- 5
    */

    @Test
    public void findPathWithInfiniteSlope() throws IOException, OsmXmlParserException {

        setup("test_inf.osm");

        Node startNode = graph.getNodeByCoordinates(2.0,3.0);
        assert startNode != null;
        log.info(String.format("[Start node] [id: %d] [long: %f] [lat: %f]", startNode.getId(), startNode.getLongitude(), startNode.getLatitude()));

        List<Integer> shapeNodes = new LinkedList<>();
        shapeNodes.add(0,5);
        shapeNodes.add(1,2);
        shapeNodes.add(2,2);
        shapeNodes.add(3,3);
        shapeNodes.add(4,3);
        shapeNodes.add(5,1);
        shapeNodes.add(6,1);
        shapeNodes.add(7,5);
        List<Segment> shape = createShapeSegments(0.0, shapeNodes);

        ConditionManager cm = new ConditionManager();
        cm.addCondition(new DirectionCondition(0.0));
        ShapeFinder shapeFinder = new ShapeFinder(graph,shape,cm);

        Graph foundGraph = shapeFinder.findShape(startNode,0.0,0.0);
        foundGraph.getSegments().values().forEach(System.out::println);
    }

    /* creates shape
        4 -- 2
        2 -- 1
        1 -- 3
        3 -- 4
    */
    @Test
    public void findPerfectlyFittedRoute() throws IOException, OsmXmlParserException {

        setup("test.osm");

        Node startNode = graph.getNodeByCoordinates(2.0,1.0);
        assert startNode != null;
        log.info(String.format("Start node [id: %d] [long: %f] [lat: %f]", startNode.getId(), startNode.getLongitude(), startNode.getLatitude()));


        List<Integer> shapeNodes = new LinkedList<>();
        shapeNodes.add(0,4);
        shapeNodes.add(1,2);
        shapeNodes.add(2,2);
        shapeNodes.add(3,1);
        shapeNodes.add(4,1);
        shapeNodes.add(5,3);
        shapeNodes.add(6,3);
        shapeNodes.add(7,4);
        List<Segment> shape = createShapeSegments(0.0, shapeNodes);

        ConditionManager cm = new ConditionManager();
        cm.addCondition(new DirectionCondition(0.0));
        ShapeFinder shapeFinder = new ShapeFinder(graph,shape,cm);

        Graph foundGraph = shapeFinder.findShape(startNode,0.0,0.0);
        foundGraph.getSegments().values().forEach(System.out::println);
    }


    private List<Segment> createShapeSegments(double noiseRange, List<Integer> shapeNodes ){
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
        nodes.add(5,nf.newNode(2.0 + random.nextDouble()*noiseRange,3.0 + random.nextDouble()*noiseRange));

        nodes.forEach(n -> System.out.println(String.format("[SHAPE NODE][id: %d] [Lon: %f] [Lat %f]", n.getId(), n.getLongitude(), n.getLatitude() )));

        Iterator<Integer> i = shapeNodes.iterator();

        result.add(0,sf.newFullSegment(nodes.get(i.next()),nodes.get(i.next())));
        result.add(1,sf.newFullSegment(nodes.get(i.next()),nodes.get(i.next())));
        result.add(2,sf.newFullSegment(nodes.get(i.next()),nodes.get(i.next())));
        result.add(3,sf.newFullSegment(nodes.get(i.next()),nodes.get(i.next())));

        return result;
    }


}
