import io.jbotsim.core.Topology;
import io.jbotsim.gen.basic.TopologyGenerators;
import io.jbotsim.ui.JTopology;
import io.jbotsim.ui.JViewer;


public abstract class Examples {
    public final static int n = 101;
    public final static float factor_mult = 3f;
    public final static float factor_add = 200;

    public static void Circle(){
        Topology tp = new Topology();
        tp.setDefaultNodeModel(ColoringNode.class); // algo des noeuds = ColoringNode
        TopologyGenerators.generateRing(tp, n); // topologie = cycle à n noeuds
        tp.disableWireless(); // pour avoir un vrai anneau pour tout n
        tp.shuffleNodeIds(); // permutation des IDs dans [0,n[

        for (int i = 0; i < n; i++){ // pour chaque noeuds
            ColoringNode u = (ColoringNode) tp.getNodes().get(i); // u = noeuds i
            u.setLocation(u.getX() * factor_mult + factor_add, u.getY() * factor_mult + factor_add); // décaler sa position
            u.parent = tp.getNodes().get((i+1) % n); // son parent
        }

        JTopology jtp = new JTopology(tp);
        jtp.addLinkPainter(new JParentLinkPainter()); // ajoute l'orientation
        new JViewer(jtp); // dessine la topologie

        tp.start(); // démarre l'aglorithme
        tp.pause(); // mode pas-à-pas
    }
}
