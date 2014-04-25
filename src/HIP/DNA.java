package HIP;

import javax.print.attribute.standard.NumberOfDocuments;

public class DNA {
    int krom[][];   
    int[][] room = new int[global.hight][global.width];
    boolean isChoosed=false;
    
    public DNA(int n){
        krom=new int[n][5];
    }
 
    public DNA(DNA dna){
        krom=new int[dna.length()][5];
        for(int i=0;i<dna.length();i++){
            krom[i][0]=dna.getKromX(i);
        	krom[i][1]=dna.getKromY(i);
        	krom[i][2]=dna.getAngle(i);
        	krom[i][3]=dna.getlr(i);
        	krom[i][4]=dna.getw(i);
        }	
    }

    public void init(sensorSet sensorSet){    	
    	int num = sensorSet.getNum();
    	if(num>0){
    		for(int i=0; i<krom.length; i++){
    			int whichSensor = (int)(Math.random()*num);
//    			System.out.println("num? " + num);
    			krom[i][3] = sensorSet.set.get(whichSensor).get(0);
    			krom[i][4] = sensorSet.set.get(whichSensor).get(1);
    			if(krom[i][4] == -1){
   				 	krom[i][2] = -1;
    			}else{
    				 int angle=(int)(Math.random()*2);//0 or 90 degrees
    				 krom[i][2] = angle*90;
    			}
	            int h = (int)(Math.random()*global.hight);            
	            int w = (int)(Math.random()*global.width);  	            	          
	            krom[i][0] = h;
	            krom[i][1] = w;	     
//	    		System.out.print("Sensor: " + krom[i][0] + " " + krom[i][1] + " " + krom[i][2] + " " + krom[i][3] + " " + krom[i][4] + "\n");
	    		sensorSet.set.remove(whichSensor);
	    		num = sensorSet.getNum();
//	    		sensorSet.print();
    		}	
//	        System.out.println("Num: " + sensorSet.getNum());
    	}else{
    		System.out.println("No more Sensors Left");
    	}
    }

    public void print(DNA dna){
    	System.out.print("DNA: ");
    	for (int i = 0; i < dna.length(); i++) {
        	System.out.print(dna.getKromX(i) + "," + dna.getKromY(i) + "," + dna.getAngle(i) + "," + dna.getlr(i) + "," + dna.getw(i) + "\n ");
		}
    	System.out.println();
    }
    
    public int areaCost(DNA dna){
    	int costArea = 0;//dna.length()*10000;
    	for (int i = 0; i < dna.length(); i++) {
			if(dna.getAngle(i) == -1){
				costArea += Math.PI*Math.pow(dna.getlr(i),2);
			}else{
				costArea += dna.getlr(i)*dna.getw(i);
			}
		}
    	return costArea;    	
    }
    
    public DNA mutateToAnotherShape(int place, DNA dna){
//    	dna.print(dna);
    	int whichtype = (int)(Math.random()*sensorSet.numberOfTypes);
//    	System.out.println("Which: " + whichtype + " place: " + place);
		dna.setlr(place, sensorSet.types[whichtype][0]);
		dna.setw(place, sensorSet.types[whichtype][1]);
		if(dna.getw(place) != -1){ // is rectangle
			dna.setAngle(place, ((int)(Math.random()*2))*90);
		}else{ // is circle 
			dna.setAngle(place, -1);
		}
//		dna.print(dna);
    	return dna;
    }
    
    //get and set X and Y
    public void setKromX(int kromNum,int val){
        krom[kromNum][0]=val;
    }

    public void setKromY(int kromNum,int val){
        krom[kromNum][1]=val;
    }

    public int getKromX(int kromNum){
        return krom[kromNum][0];
    }

    public int getKromY(int kromNum){
        return krom[kromNum][1];
    }
    
    //get and set isChoosed
    public void setChoosed(boolean newVal){
        isChoosed=newVal;
    }

    public boolean getChoosed(){
        return isChoosed;
    }
    
    //get and set Angle
    public void setAngle(int kromNum, int val){
        krom[kromNum][2] = val;
    }

    public int getAngle(int kromNum){
        return krom[kromNum][2];
    }

    //get and set lr
    public void setlr(int kromNum,int val){
        krom[kromNum][3]=val;
    }

    public int getlr(int kromNum){
        return krom[kromNum][3];
    }

    //get and set w
    public void setw(int kromNum,int val){
        krom[kromNum][4]=val;
    }

    public int getw(int kromNum){
        return krom[kromNum][4];
    }

    public int length(){
        return krom.length;
    }
}
