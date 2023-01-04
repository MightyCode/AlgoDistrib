import io.jbotsim.core.Link;
import io.jbotsim.core.Node;
import io.jbotsim.core.Point;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.JTopology;
import io.jbotsim.ui.JViewer;
import io.jbotsim.ui.painting.JDirectedLinkPainter;


import java.awt.*;
import java.awt.geom.GeneralPath;
import java.lang.reflect.Field;
import java.util.List;

/**
 * <p>The {@link JParentLinkPainter} draws links in an oriented way
 * based on the value of a "parent" variable (if any exists).
 * The link is drawn from a node to its parent with arrow-tips at the parent side.
 * Further customization can be done similarly to a {@link JDirectedLinkPainter},
 * from which the behavior is inherited.</p>
 *
 * To use this painter, create a {@link JTopology} object
 * from your {@link Topology}, then add a {@link JParentLinkPainter} to it,
 * and finally create the {@link JViewer} from this {@link JTopology},
 * as follows:
 * <pre>
 * {@code
 * Topology topology = new Topology();
 * JTopology jTopology = new JTopology(topology);
 * jTopology.addLinkPainter(new JParentLinkPainter());
 * new JViewer(jTopology); // the argument is jTopology here, not topology.
 * }
 * </pre>
 */
public class JParentLinkPainter extends JDirectedLinkPainter {

    @Override
    protected void drawDestinationPartIfNeeded(Graphics2D g2d, Link link) {
        Node srcNode = link.source;
        Node destNode = link.destination;
        try {
            Node srcParent = (Node) srcNode.getClass().getDeclaredField("parent").get(srcNode);
            if (srcParent == destNode)
                paintHead(g2d, srcNode, destNode);
            Node destParent = (Node) destNode.getClass().getDeclaredField("parent").get(destNode);
            if (destParent == srcNode)
                paintHead(g2d, destNode, srcNode);
        } catch (NoSuchFieldException ignored){
        } catch (ClassCastException e1){
            System.err.println("The parent variable should be a Node (or a subclass of Node)");
        } catch (IllegalAccessException e2){
            System.err.println("The parent variable should be public");
        }
    }
    protected void paintHead(Graphics2D g2d, Node srcNode, Node destNode){
        Point srcPoint = srcNode.getLocation();
        Point destPoint = destNode.getLocation();
        double destinationIconSize = destNode.getIconSize() ;
        printDirectLinkEnd(g2d, srcPoint, destPoint, destinationIconSize);
    }
}