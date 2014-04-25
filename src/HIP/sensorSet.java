package HIP;
import java.util.ArrayList;

public class sensorSet {
	static int numberOfSensors = 15;
	static int numberOfTypes = 3;	
	static public int[][] types = new int[numberOfTypes][3];// lr, w, count
	
	ArrayList<ArrayList<Integer>> set = new ArrayList<ArrayList<Integer>>();
    int circleCount = 0;
    int rectangleCount = 0;
    
    public sensorSet(){
		types[0][0] = 100;
		types[0][1] = -1;
		types[0][2] = 5;
		types[1][0] = 175;
		types[1][1] = 175;
		types[1][2] = 5;
		types[2][0] = 200;
		types[2][1] = 150;
		types[2][2] = 5;
//		types[3][0] = 175;
//		types[3][1] = 175;
//		types[3][2] = 5;
		init();
    }
    
    public int getNum(){
    	return set.size();
    }
    
    public void print(){
    	for (int i = 0; i < set.size(); i++) {
			for (int j = 0; j < set.get(i).size(); j++) {
				System.out.print(set.get(i).get(j) + " ");
			}
			System.out.println();
		}    	
    } 
    
    public void init(){
    	for (int i = 0; i < numberOfTypes; i++) {
    		for (int j = 0; j < types[i][2]; j++) {
    			ArrayList<Integer> s = new ArrayList<Integer>();
    	    	s.add(types[i][0]);
    	    	s.add(types[i][1]);
    	    	set.add(s);
			}			
		}
    }
    
    public int getRectangleCount(){
    	return rectangleCount;
    }
    public int getCircleCount(){
    	return circleCount;
    } 
}
