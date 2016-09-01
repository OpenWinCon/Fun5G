/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gotham_hazel;


import java.util.List;

/**
 *
 * @author junhoya924@khu.ac.kr
 */
public class Matrix {
    private List<String>[] c_matrix;       //Matrix of link - It shows only {next hop}(BATMAN's definition)
    private List<String> c_originList;     //Originator node List
    private List<String> c_outNodeList;    //Originator node List which GOTHAM program is dead

    
    /*
         * input  - matrix from hazelcast(matrix) : List<String>[], 
                    originator list from hazelcast(originList) : List<String>,
                    outnodeList from hazelcast(outnodeList) : List<String>, 
                    max node count(MAX_NODE) : int,
                    status that it is master(isMaster) : boolean
         * doing  - initialize member variables
         * output - 
     */
    public Matrix(List<String>[] matrix, List<String> originList, List<String> outnodeList, int MAX_NODE, boolean isMaster) {
        c_matrix = matrix;
        
        if (isMaster) {
            for (int i = 0; i < MAX_NODE; i++) {
                for (int j = 0; j < MAX_NODE; j++) {
                    c_matrix[i].add("null");
                }
            }
        }
        
        c_originList = originList;
        c_outNodeList = outnodeList;
        
        System.out.println("finish matrix starter");
    }

    /*
         * input  - originator MAC which is added(originName) : String                     
         * doing  - add originator to originator list(if originator list doesn't have originName)
         * output - boolean(return true if add the new originator)
     */    
    public boolean addOrigin(String originName) {
        if (c_originList.size() == 99) {
            return false;
        }
        if (c_originList.contains(originName)) {             
            return false;
        }
        c_originList.add(originName);

        return true;
    }

    /*
         * input  - all of new originator node name, MAC address(new_origin) : String[]                     
         * doing  - if there is no new originator node name in nodeList, remove it from originList)
                    And change the matrix of link
         * output - boolean(return true, if this function removes at least one node)
     */
    public boolean removeOrigin(String[] new_origin) {
        if (c_originList.size() == 0) {
            return false;
        }
        int removeCount = 0;
        int[] removeIndex = new int[new_origin.length];
        for (int i = 0; i < c_originList.size(); i++) {
            for (int j = 0; j < new_origin.length; j++) {
                if (new_origin[j].equalsIgnoreCase(c_originList.get(i))) {
                    break;
                }
                if (j == new_origin.length - 1) {
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
            for (int i = index; i < c_originList.size(); i++) {
                for (int j = 0; j < c_originList.size(); j++) {                    
                    c_matrix[i].set(j, c_matrix[i+1].get(j));
                }
            }

            for (int i = index; i < c_originList.size(); i++) {
                for (int j = 0; j < c_originList.size(); j++) {
                    c_matrix[j].set(i, c_matrix[j].get(i+1));
                }
            }
            c_originList.remove(index);
        }
        return true;
    }

    /*
         * input  - originator's MAC(myMAC) : String, 
                    nexthop MAC array(nextHop) : String[]
         * doing  - add node to nodeList(if nodeList doesn't have input String)
         * output - boolean(return true if update succesfully)
     */     
    public boolean updateNextHop(String myMAC, String[] nextHop) {
        System.out.println();
        int nodeNumber = -1;
        int[] nextHopNumber = new int[nextHop.length];
        int count = 0;
        
        nodeNumber = c_originList.indexOf(myMAC);
        for (int i = 0; i < nextHop.length; i++) {
            nextHopNumber[i] = c_originList.indexOf(nextHop[i]);
            count++;
            System.out.println("nexthopnumber : " +nextHopNumber[i]);
        }
        if (count != nextHop.length || nodeNumber == -1) {
            return false;
        }

        System.out.println("finish find nexhopnumber");

        for (int i = 0; i < c_originList.size(); i++) {
            c_matrix[nodeNumber].set(i, "X");
        }
        for (int i = 0; i < nextHopNumber.length; i++) {
            c_matrix[nodeNumber].set(nextHopNumber[i], "O");
        }

        return true;
    }

    /*
         * input  - originator's MAC(mac) : String
         * doing  - print matrix with information source MAC
         * output - void
     */
    public void printMatrix(String mac) {
        System.out.println("get from : " + mac);
        for (int i = 0; i < c_originList.size(); i++) {
            for (int j = 0; j < c_originList.size(); j++) {
                System.out.print(c_matrix[i].get(j) + "\t");
            }
            System.out.println();
        }
        
        System.out.println("node list : " );
        for( int  i =0; i < c_originList.size(); i++){
            System.out.println(c_originList.get(i));
        }
    }

    /*
         * input  - void
         * doing  - 
         * output - originator's count(c_originList.size()) : int
     */    
    public int getNodeCount() {
        return c_originList.size();
    }

    /*
         * input  - void
         * doing  - 
         * output - return originator list(c_originList) : List<String>
     */    
    public List<String> getOriginList() {
        return c_originList;
    }

    /*
         * input  - void
         * doing  - 
         * output - matrix(c_matrix) : List<String>[]
     */
    public List<String>[] getMatrix() {
        return c_matrix;
    }

    /*
         * input  - node's MAC address which is out(mac) : String
         * doing  - When the GOTHAM program in each node is dead, update status matrix and add MAC to outNodeList
         * output - void
     */
    public void nodeOut(String mac) {
        System.out.println(mac);
        int nodeNumber = -1;
        nodeNumber = c_originList.indexOf(mac);
        for (int i = 0; i < c_originList.size(); i++) {           
            c_matrix[nodeNumber].set(i, "-");
        }
        c_outNodeList.add(mac);
        
        System.out.println();
        for(int i = 0; i < c_outNodeList.size(); i++){
            System.out.println("outNodeList : ");
            System.out.println(c_outNodeList.get(i));
        }
    }

    /*
         * input  - node's MAC address which is in(mac) : String
         * doing  - if each node's GOTHAM program revive, remove MAC address in outNodeList
         * output - void
     */
    public void nodeIn(String mac) {
        if (c_outNodeList.contains(mac)) {
            c_outNodeList.remove(mac);
        }
        
    }
    
    /*
         * input  - void
         * doing  - 
         * output - out node list(c_outNodeList) : List<String>
     */
    public List<String> getOutNodeList(){
        return c_outNodeList;
    }
}
