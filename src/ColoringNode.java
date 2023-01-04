import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

import java.util.ArrayList;

public class ColoringNode extends Node {
    public Node parent; // défini dans Main()
    public int n;

    private int colorId; // Dans l'algorithme, c'est x

    // While l != lp, 6 coloration, else it's 3 coloration algorithm or finished
    private int l;
    private int lp; // Condition d'arrêt du 6-coloration lp == l

    /**
     * Première ronde à 6 -> Envoie des id au voisin
     * Si remainingRounds <= 5 Traitement des noeuds à l'id remainingRounds + envoie des ID.
     * Si remainingRounds < 2 fin de l'algorithme de 3-coloration
     */
    private int remainingRounds = 6; // 6 = première ronde où envoie d'id, 5 > changement d'id des noeuds à la couleur number

    @Override
    public void onStart() {
        n = getTopology().getNodes().size();
        colorId = getID();
        setColor(Color.getColorAt(colorId)); // couleur = ID
        l = log2Ceil(n);
        lp = -1;
    }

    @Override
    public void onClock(){
        if (lp == l) {
            if (remainingRounds > 2) {
                coloration3Send();

                if (remainingRounds == colorId)
                    coloration3Receive();

                --remainingRounds;
            }
        } else
            coloration6();
    }

    private void coloration6(){
        int parentColor = -1; // y
        int childColor = -1; // z
        // colorId -> x

        for (Message m : getMailbox()){
            if (m.getSender() == parent)
                parentColor = (int)m.getContent();
            else
                childColor = (int)m.getContent();
        }

        if (parentColor != -1 && childColor != -1) {
            colorId = posDif(posDif(colorId, childColor), posDif(parentColor, colorId));
            setColor(Color.getColorAt(colorId));

            lp = l;

            l = 1 + log2Ceil(1 + log2Ceil(l));
        }

        getMailbox().clear();


        if (lp != l){
            for (Node node : getNeighbors()) {
                // Ancien cas où 6-coloration non optimisé
                //if (node.getID() != parent.getID())
                    send(node, new Message(colorId));
            }
        }
    }

    private void coloration3Send(){
        for (Node node : getNeighbors()){
            send(node, new Message(remainingRounds +  " " + colorId));
            System.out.println("Send to neighbors : " + colorId);
        }
    }

    private void coloration3Receive(){
        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = getMailbox().size() - 1; i >= 0; --i){
            Message message = getMailbox().get(i);
            String[] arr = ((String)message.getContent()).split(" ");

            // If the message was sent the previous round
            if (Integer.parseInt(arr[0]) == remainingRounds + 1)
                colors.add(Integer.parseInt(arr[1]));

            getMailbox().remove(i);
        }

        // First free
        for (int i = 0; i < colorId; ++i){
            boolean founded = false;
            for (Integer inte : colors){
                if (inte == i)
                    founded = true;
            }

            if (!founded){
                colorId = i;
                setColor(Color.getColorAt(colorId));
            }
        }
    }

    private int posDif(int x, int y){
        int p = 0;

        while (x % 2 == y % 2) {
            p++;
            x /= 2;
            y /= 2;

            if (x == 0 && y == 0)
                return 0;
        }

        return 2 * p + x % 2;
    }

    private int log2Ceil(int k){
        return (int)Math.log(k);
    }
}