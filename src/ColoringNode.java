import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

import java.util.ArrayList;

public class ColoringNode extends Node {
    public Node parent; // défini dans Main()
    public int n;

    private int colorId;

    // While l != lp, 6 coloration, else it's 3 coloration algorithm or finished
    private int l;
    private int lp;
    private int numberToCheck = 6;

    @Override
    public void onStart() {
        colorId = getID();
        setColor(Color.getColorAt(colorId)); // couleur = ID
        l = log2Ceil(n);
        lp = -1;
    }

    @Override
    public void onClock(){
        if (lp == l) {
            if (numberToCheck > 2) {
                lastRoundSend();

                if (numberToCheck == colorId)
                    lastRoundReceive();

                --numberToCheck;
            }
        } else
            normalRound();
    }

    private void normalRound(){
        System.out.println("Normal round");

        for (Message m : getMailbox()){
            if (m.getSender() != parent)
                continue;

            int y_color = (int)m.getContent();

            colorId = posDif(colorId, y_color);
            setColor(Color.getColorAt(colorId));

            lp = l;

            l = 1 + log2Ceil(l);
        }
        getMailbox().clear();


        if (lp != l){
            for (Node node : getNeighbors()) {
                if (node.getID() != parent.getID())
                    send(node, new Message(colorId));
            }
        }
    }

    private void lastRoundSend(){
        for (Node node : getNeighbors()){
            send(node, new Message(numberToCheck +  " " + colorId));
            System.out.println("Send to neighbors : " + colorId);
        }
    }

    private void lastRoundReceive(){
        ArrayList<Integer> colors = new ArrayList<>();

        for (Message message : getMailbox()){
            String[] arr = ((String)message.getContent()).split(" ");

            if (Integer.parseInt(arr[0]) == numberToCheck + 1)
                colors.add(Integer.parseInt(arr[1]));
        }

        getMailbox().clear();

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