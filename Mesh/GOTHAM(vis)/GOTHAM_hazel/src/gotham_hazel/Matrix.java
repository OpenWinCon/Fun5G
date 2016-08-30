/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gotham_hazel;


import java.util.List;

/**
 *
 * @author junhoya924
 */
public class Matrix {
    private List<String>[] c_matrix;
    //private int c_nodeCount;
    
    private List<String> c_nodeList;       //Originator node List
    private List<String> c_outNodeList;    //Originator node List which slave program is dead

    public Matrix(List<String>[] matrix, List<String> nodeList, List<String> outnodeList, int MAX_NODE, boolean isMaster) {
        //c_nodeCount = 0;
        //c_matrix = new String[100][100];
        c_matrix = matrix;
        
        if (isMaster) {
            for (int i = 0; i < MAX_NODE; i++) {
                for (int j = 0; j < MAX_NODE; j++) {
                    c_matrix[i].add("null");
                }
            }
        }else{
            //c_nodeCount = c_nodeList.size();
        }
        
        c_nodeList = nodeList;
        c_outNodeList = outnodeList;
        
        System.out.println("finish matrix starter");
    }

    /*
         * input node name(MAC) Sring
         * add node to nodeList(if nodeList doesn't have input String)
         * output boolean(if add return true)
     */
    public boolean addNode(String nodeName) {
        //System.out.println("addnode : " +nodeName);
        //System.out.println("node list : " );
        /*for( int  i =0; i < c_nodeList.size(); i++){
            System.out.println(c_nodeList.get(i));
        }*/
        if (c_nodeList.size() == 99) {
            return false;
        }
        if (c_nodeList.contains(nodeName)) { 
            //System.out.println("contains : " + nodeName);
            return false;
        }
        //c_nodeCount++;
        c_nodeList.add(nodeName);
        

        return true;
    }

    /*
         * input all originator node name(MAC) Sring[]
         * if there is no originator node name in nodeList, remove it
         *     (In recent originator, past originator node is disapeared)
         * output boolean(if remove at least one node, return true)
     */
    public boolean removeNode(String[] origin) {
        if (c_nodeList.size() == 0) {
            return false;
        }
        int removeCount = 0;
        int[] removeIndex = new int[origin.length];
        for (int i = 0; i < c_nodeList.size(); i++) {
            for (int j = 0; j < origin.length; j++) {
                if (origin[j].equalsIgnoreCase(c_nodeList.get(i))) {
                    break;
                }
                if (j == origin.length - 1) {
                    removeIndex[removeCount] = i;
                    removeCount++;
                }
            }

        }
        if (removeCount == 0) {
            return false;
        }

        for (int k = 0; k < removeCount; k++) {
            int index = removeIndex[k];
            for (int i = index; i < c_nodeList.size(); i++) {
                for (int j = 0; j < c_nodeList.size(); j++) {
                    //c_matrix[i][j] = c_matrix[i + 1][j];
                    c_matrix[i].set(j, c_matrix[i+1].get(j));
                }
            }

            for (int i = index; i < c_nodeList.size(); i++) {
                for (int j = 0; j < c_nodeList.size(); j++) {
                    //c_matrix[j][i] = c_matrix[j][i + 1];
                    c_matrix[j].set(i, c_matrix[j].get(i+1));
                }
            }
            c_nodeList.remove(index);
            //c_nodeCount--;
        }
        return true;
    }

    /*
         * input originator's MAC(myMAC) Sring, nexthop array String[]
         * update next hop with each originator
         * output boolean(if update return true)
     */
    public boolean updateNextHop(String myMAC, String[] nextHop) {

        int nodeNumber = -1;
        int[] nextHopNumber = new int[nextHop.length];
        int count = 0;
        System.out.println();
        

        nodeNumber = c_nodeList.indexOf(myMAC);
        for (int i = 0; i < nextHop.length; i++) {
            nextHopNumber[i] = c_nodeList.indexOf(nextHop[i]);
            count++;
            System.out.println("nexthopnumber : " +nextHopNumber[i]);
        }
        if (count != nextHop.length || nodeNumber == -1) {
            return false;
        }

        System.out.println("finish find nexhopnumber");

        for (int i = 0; i < c_nodeList.size(); i++) {
            //c_matrix[nodeNumber][i] = "X";
            c_matrix[nodeNumber].set(i, "X");
        }
        for (int i = 0; i < nextHopNumber.length; i++) {
            //c_matrix[nodeNumber][nextHopNumber[i]] = "O";
            c_matrix[nodeNumber].set(nextHopNumber[i], "O");
        }

        return true;
    }

    /*
         * input node name(MAC, information source MAC) Sring
         * print matrix with information source MAC
         * output void
     */
    public void printMatrix(String mac) {
        System.out.println("get from : " + mac);
        for (int i = 0; i < c_nodeList.size(); i++) {
            for (int j = 0; j < c_nodeList.size(); j++) {
                System.out.print(c_matrix[i].get(j) + "\t");
            }
            System.out.println();
        }
        
        System.out.println("node list : " );
        for( int  i =0; i < c_nodeList.size(); i++){
            System.out.println(c_nodeList.get(i));
        }
    }

    /*
         * input void
         * return node count
         * output int(node count)
     */
    public int getNodeCount() {
        return c_nodeList.size();
    }

    /*
         * input void
         * return nodeList
         * output List<String>(nodeList)
     */
    public List<String> getNodeList() {
        return c_nodeList;
    }

    /*
         * input void
         * return status matrix
         * output String[][](status matrix)
     */
    public List<String>[] getMatrix() {
        return c_matrix;
    }

    /*
         * input originator MAC address String
         * When the slave program in slave node is dead, update status matrix and add MAC to outNodeList
         * output void
     */
    public void nodeOut(String mac) {
        System.out.println(mac);
        int nodeNumber = -1;
        nodeNumber = c_nodeList.indexOf(mac);
        for (int i = 0; i < c_nodeList.size(); i++) {
           // c_matrix[nodeNumber][i] = "-";
            c_matrix[nodeNumber].set(i, "-");
        }
        c_outNodeList.add(mac);
        
        for(int i = 0; i < c_outNodeList.size(); i++){
            System.out.println("outNodeList : ");
            System.out.println(c_outNodeList.get(i));
        }
    }

    /*
         * input originator MAC address String
         * if originator's slave program revive, remove MAC address in outNodeList
         * output void
     */
    public void nodeIn(String mac) {
        if (c_outNodeList.contains(mac)) {
            c_outNodeList.remove(mac);
        }
        
    }
    
    public List<String> getOutNodeList(){
        return c_outNodeList;
    }
         
}
