import java.io.*;

/**
 * This is the class that students need to implement. The code skeleton is provided.
 * Students need to implement rtinit(), rtupdate() and linkhandler().
 * printdt() is provided to pretty print a table of the current costs for reaching
 * other nodes in the network.
 */ 
public class Node { 
    
    public static final int INFINITY = 9999;
    
    int[] lkcost;		/*The link cost between this node and other nodes*/
    int[][] costs;  		/*Define distance table*/
    int nodename;               /*Name of this node*/
    
    /* Class constructor */
    public Node() { }
    
    /* students to write the following two routines, and maybe some others */
    void rtinit(int nodename, int[] initial_lkcost) { 
    	this.lkcost = new int[4];
    	this.costs = new int[4][4];
    	this.nodename = nodename;

    	this.lkcost = initial_lkcost.clone();

    	for(int m = 0; m < 4; m++){
            for(int n = 0; n < 4; n++){
                if(m == n){
                    this.costs[m][n] = initial_lkcost[m];
                }
                else
                    this.costs[m][n] = INFINITY;
            }
        }

        for(int m = 0; m < 4; m++){
            if(m != this.nodename && lkcost[m] != INFINITY){
                Packet p = new Packet(this.nodename, m, lkcost);
                NetworkSimulator.tolayer2(p);
            }
            else 
                continue;
        }
    }    
    
    void rtupdate(Packet rcvdpkt) {
    	int minimum;
        int x; 
        int least_dist;
        boolean time2update = false;
        int next_hop = -1;
     
        int source = rcvdpkt.sourceid;
        int dest = rcvdpkt.destid;
     
        int[] new_minCosts = new int[4];
        int[] wrong= new int[4];
        int [] mincost = rcvdpkt.mincost;        
       
        for(int m=0; m< 4; m++){ 
            x = mincost[m] + lkcost[source];
            if(x != INFINITY && x != costs[m][source]){
                costs[m][source] = x; 
                time2update= true;
            }
        }

        if(time2update==true){
        	for (int n=0; n<4; n++){ 
        		minimum = INFINITY;
        		for (int m=0; m<4; m++){
        			least_dist = costs[n][m];
        			if(least_dist < minimum){
        				minimum = least_dist; 
        			}
        			new_minCosts[n] = minimum;
        		}
        	}

        	minimum = INFINITY;
            for (int n=0; n<4; n++){ 
            	minimum = INFINITY;
                for (int m=0; m<4; m++){
            		least_dist = costs[n][m];
            		if(least_dist < minimum){
            			minimum = least_dist;
            		}
            		if(m == source){
            			next_hop = m;
            		}
            		else
            			next_hop = -1;
            	}
            }

            for(int m = 0; m < 4; m++){
            	wrong[m] = new_minCosts[m];
            }

            for(int n = 0 ; n < 4 ; n++){
            	if(lkcost[n] == INFINITY || n == nodename){ //you cant reach the node and its not you, dont sent it 
            		continue;
            	}
            	else{
            		if(next_hop == n){
                    	wrong[dest] = INFINITY;
                    	Packet p_wrong = new Packet(nodename, n, wrong);
                    }
                    else{
                    	Packet p_2 = new Packet(nodename, n, new_minCosts); 
                    	NetworkSimulator.tolayer2(p_2);
               		}
            	}
        	}
        
        	System.out.println("Distance V table: ");
        	this.printdt();
    	}
    }
    
    
    /* called when cost from the node to linkid changes from current value to newcost*/
    void linkhandler(int linkid, int newcost) {
    	lkcost[linkid] = newcost;
        // reseting minimum cost table
        for(int m = 0 ; m < 4; m++){
            if(this.lkcost[m] == INFINITY || m == this.nodename){ 
                continue;
            }
            else{
                Packet p = new Packet(this.nodename, m, lkcost); 
                NetworkSimulator.tolayer2(p);
            }
        }
    }

    /* Prints the current costs to reaching other nodes in the network */
    void printdt() {
        switch(nodename) {
	
	case 0:
	    System.out.printf("                via     \n");
	    System.out.printf("   D0 |    1     2 \n");
	    System.out.printf("  ----|-----------------\n");
	    System.out.printf("     1|  %3d   %3d \n",costs[1][1], costs[1][2]);
	    System.out.printf("dest 2|  %3d   %3d \n",costs[2][1], costs[2][2]);
	    System.out.printf("     3|  %3d   %3d \n",costs[3][1], costs[3][2]);
	    break;
	case 1:
	    System.out.printf("                via     \n");
	    System.out.printf("   D1 |    0     2    3 \n");
	    System.out.printf("  ----|-----------------\n");
	    System.out.printf("     0|  %3d   %3d   %3d\n",costs[0][0], costs[0][2],costs[0][3]);
	    System.out.printf("dest 2|  %3d   %3d   %3d\n",costs[2][0], costs[2][2],costs[2][3]);
	    System.out.printf("     3|  %3d   %3d   %3d\n",costs[3][0], costs[3][2],costs[3][3]);
	    break;    
	case 2:
	    System.out.printf("                via     \n");
	    System.out.printf("   D2 |    0     1    3 \n");
	    System.out.printf("  ----|-----------------\n");
	    System.out.printf("     0|  %3d   %3d   %3d\n",costs[0][0], costs[0][1],costs[0][3]);
	    System.out.printf("dest 1|  %3d   %3d   %3d\n",costs[1][0], costs[1][1],costs[1][3]);
	    System.out.printf("     3|  %3d   %3d   %3d\n",costs[3][0], costs[3][1],costs[3][3]);
	    break;
	case 3:
	    System.out.printf("                via     \n");
	    System.out.printf("   D3 |    1     2 \n");
	    System.out.printf("  ----|-----------------\n");
	    System.out.printf("     0|  %3d   %3d\n",costs[0][1],costs[0][2]);
	    System.out.printf("dest 1|  %3d   %3d\n",costs[1][1],costs[1][2]);
	    System.out.printf("     2|  %3d   %3d\n",costs[2][1],costs[2][2]);
	    break;
        }
    }
    
}