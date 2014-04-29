package HIP;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Main implements Runnable {
	
	//Parameters to set ---------------------------------------------------------
	int m/*Andaje jameiat*/, n/*tedade sensor ha*/, u/*tedade childha*/;	
    int lenghtCoeff = 70;
    //myfile.txt: 55
    //myfile1.txt: 
    //myfile2.txt: 
	int iterationCount;
    int costCount = 0;
    double locacc;
    int sensorEdge = 177;
    
    double accuracy;
    int finalLen;
    
    double crossOverRate;
    double mutationRate;

    DNA bestDNA;
    int[][] room = new int[global.hight][global.width];
    boolean [][]roomCovered = new boolean[global.hight][global.width];
    
    int[][] printable = new int[global.hight][global.width];
    int[][][] image = new int[global.hight][global.width][3];
    int printBest[][] = new int[100][5];
    int seeIfConverged [][] = new int[100][5];
    double traces[][] = new double[753][3];
    int tracesInInt[][] = new int[753][3];
    double posTot;
    long startTime;
    long after1Iter;	
    long endTime;
    int traceCount;
    int mainNumber;
    int expNumber;
    
    public Main(int m, int n, int u, int itr, double p_c, double p_m, String FileName, int mainNumber, int expNumber){
		this.m = m;
		this.n = n;
		this.u = u/2;
		this.iterationCount = itr;
		this.crossOverRate = p_c;
		this.mutationRate = p_m;
		this.mainNumber = mainNumber;
		this.expNumber = expNumber;
	}
    
    public void writeResults(double locaccW, double accuracyW, double durationW, int iterationW, int finalLenW, int mainNumberW) throws IOException{
    	FileWriter fstream = new FileWriter("Results.txt",true);
        BufferedWriter fbw = new BufferedWriter(fstream);
        if(true/*finalLenW == 6 /*any condition*/){
        	System.out.print(accuracyW);
        	fbw.write("" + mainNumberW + ", " + locaccW + ", "+accuracyW + ", " + durationW + ", " + iterationW + ", " + finalLenW + "\n");
        }else{
        	fbw.write("\n"); 
        }
        fbw.close();    
    }
    
    public void driver() throws IOException{
    	FileWriter fstream = new FileWriter("Output.txt",true);
        BufferedWriter fbw = new BufferedWriter(fstream);
        if(bestDNA!=null){
        	fbw.write(""+((double)computeLocalizationAccuracy(bestDNA)/traceCount));
        	fbw.newLine();
        }      
        fbw.close();    	
    }
    
    public double computeLocalizationAccuracy(DNA dna){
    	boolean isCovered[] = new boolean[traceCount];
    	for (int i = 0; i < traceCount; i++) {
			isCovered[i] = false;
		}
    	double acc = 0;
    	for (int i = 0; i < traceCount; i++) {
			for (int j = 0; j < dna.length(); j++) {
				int fromX, fromY, toX, toY;
	        	fromX = dna.getKromX(j)-sensorEdge/2 > 0 ? dna.getKromX(j)-sensorEdge/2 : 0;
	        	fromY = dna.getKromY(j)-sensorEdge/2 > 0 ? dna.getKromY(j)-sensorEdge/2 : 0;
	        	toX = dna.getKromX(j)+sensorEdge/2 < global.hight ? dna.getKromX(j)+sensorEdge/2 : global.hight;
	        	toY = dna.getKromY(j)+sensorEdge/2 < global.width ? dna.getKromY(j)+sensorEdge/2 : global.width;
	        	
				if(tracesInInt[i][2]>=fromX && tracesInInt[i][2] <= toX && 
						tracesInInt[i][1]>=fromY && tracesInInt[i][1]<=toY){								
					isCovered[i] = true;
				}
			}
		}
    	for (int i = 0; i < isCovered.length; i++) {
			if(isCovered[i] == true){
				acc++;
			}
		}    
    	return acc/traceCount;
    }
    
    public void readTraces(){
    	for (int i = 0; i < traces.length; i++) {
  			tracesInInt[i][0] = -1; traces[i][0] = -1;
  			tracesInInt[i][1] = -1; traces[i][1] = -1;
  			tracesInInt[i][2] = -1; traces[i][2] = -1;
  		}
    	
    	File f = new File(global.tracePath);
        FileInputStream fis = null;
        Scanner sc;
		try {
			fis = new FileInputStream(f);
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sc = new Scanner(fis);
		int itr = 0;
		traceCount = 0;
        while(sc.hasNextDouble()){
        	double timeStamp = sc.nextDouble();
        	double y = sc.nextDouble();
        	double x = sc.nextDouble();
        	traceCount ++;
        	traces[itr][0] = timeStamp;
        	traces[itr][1] = y;
        	traces[itr][2] = x;        
        	itr++;
        }
        
        for (int i = 0; i < traces.length; i++) {
			tracesInInt[i][0] = (int)(traces[i][0]*100);
			tracesInInt[i][1] = (int)(traces[i][1]*100);
			tracesInInt[i][2] = (int)(traces[i][2]*100);
		}
//        System.out.println("Traces Ready, traceCount: " + traceCount);
    }
    
   
    public void readImage()
    {    	
    	try {
            BufferedReader in = new BufferedReader(new FileReader(global.filename));
            String str;
            int row = 0;
            str = in.readLine();
            while ((str = in.readLine()) != null) {
                String[] strArray = str.split(",");
//                System.out.println(strArray.length);
                int[] intArray = new int[strArray.length];
                for(int i = 0; i < strArray.length; i++) {
                    intArray[i] = Integer.parseInt(strArray[i]);
                }           
                for (int i = 0; i < intArray.length; i++) {
					room[row][i] = intArray[i];
					room[row][i] *= 1;
				}
                row ++;               
            }
//            System.err.println(row);
            in.close();
        } catch (IOException e) {
            System.err.println("File Read Error");
            System.exit(0);
        }
    	posTot = 0;
    	for (int i = 0; i < global.hight; i++) {
			for (int j = 0; j < global.width; j++) {
//				if(room[i][j] == 0){
//					room[i][j] = 0;
//				}else if(room[i][j] == 1){
//					room[i][j] = 5;
//				}else if(room[i][j] == 2){
//					room[i][j] = 1;
//				}else if(room[i][j] == 3){
//					room[i][j] = 0;
//				}else if(room[i][j] == 4){
//					room[i][j] = 0;
//				}else if(room[i][j] == -1){
//					room[i][j] = -1;
//				}else if(room[i][j] == -2){
//					room[i][j] = -2;
//				}
				if(room[i][j]>0){
					posTot += room[i][j];
				}
			}
    	}
//		System.out.println("Heat to cover: " + posTot);
    }

    public void run(){
		readTraces();
	    readImage();
        //start the clock!
        startTime = System.nanoTime();
        DNA pop[]=new DNA[m];//jamiat avalie
        for (int i=0;i<m;i++){
        	sensorSet sensorSet = new sensorSet();
        	int minLen = Math.min(n, global.maxNumberOfSensors);
            pop[i]=new DNA(minLen);
            pop[i].init(sensorSet);
            pop[i].setChoosed(false);
        }
        int iteration=0;
        int countP=0;
    	int convergedCount = 0;
    	
        while(iteration < iterationCount){	
        	if(convergedCount > iterationCount/3){
//        		System.out.println("One third of total iterations reached");
//        		break;
        	}
            for(int i=0;i<m;i++){//entekhab valedein baraye baztarkibi
            	double randNum = (Math.random());     
            	if(randNum < crossOverRate){ 
                    pop[i].setChoosed(true);
                    countP++;                
                }
            }
            DNA child1[]=new DNA[u];
            DNA child2[]=new DNA[u];
            for(int i=0;i<u;i++){//tolide farzandan
                if(countP >= 2){
	                int p1=(int)(Math.random()*countP),p2;//pedare aval
	                DNA parent1,parent2;
	                do{
	                    p2=(int)(Math.random()*countP);//pedare dovom
	                }while(p1==p2);
	                int temp=0;
	                while(p1!=0){//entekhab p1omin pedare shayeste
	                    if(pop[temp].isChoosed==true) p1--;
	                    temp++;
	                }
	                parent1=new DNA(pop[temp]);
	                temp=0;
	                while(p2!=0){//entekhab p2omin pedare shayeste
	                    if(pop[temp].isChoosed==true) p2--;
	                    temp++;
	                }
	                parent2= new DNA(pop[temp]);               
	                
	                int pointer1=(int)(Math.random()*parent1.length());//pedare aval
	                int pointer2=(int)(Math.random()*parent2.length());//pedare dovom
	                int newSize1 = pointer1 + parent2.length() - pointer2; 
	                int newSize2 = pointer2 + parent1.length() - pointer1;
	                
	                while(newSize1>global.maxNumberOfSensors || newSize2>global.maxNumberOfSensors){
	                	pointer1=(int)(Math.random()*parent1.length());//pedare aval
	 	                pointer2=(int)(Math.random()*parent2.length());//pedare dovom
	 	                newSize1 = pointer1 + parent2.length() - pointer2; 
	 	                newSize2 = pointer2 + parent1.length() - pointer1;	 	               
	                }
	                int genChoose1[]=new int[newSize1];
	                int genChoose2[]=new int[newSize2];
	                child1[i]=new DNA(newSize1);
	                child2[i]=new DNA(newSize2);
	                for(int j=0;j<newSize1;j++){
	                    if(j < pointer1 )
	                        genChoose1[j]=0;//get from parent1
	                    else
	                        genChoose1[j]=1;//get from parent2
	                }
	
	                for(int j=0;j<newSize2;j++){
	                    if(j < pointer2 )
	                        genChoose2[j]=1;//get from parent2
	                    else
	                        genChoose2[j]=0;//get from parent1
	                }
	
	                for(int j=0;j<newSize1;j++){
	                    if(genChoose1[j]==0){
	                    	child1[i].setKromX(j, parent1.getKromX(j));
	                    	child1[i].setKromY(j, parent1.getKromY(j));
	                    	child1[i].setAngle(j, parent1.getAngle(j));
	                    	child1[i].setlr(j, parent1.getlr(j));
	                    	child1[i].setw(j, parent1.getw(j));
	                    }else{
	                    	child1[i].setKromX(j, parent2.getKromX(pointer2 + j - pointer1));
	                    	child1[i].setKromY(j, parent2.getKromY(pointer2 + j - pointer1));
	                    	child1[i].setAngle(j, parent2.getAngle(pointer2 + j - pointer1));
	                    	child1[i].setlr(j, parent2.getlr(pointer2 + j - pointer1));
	                    	child1[i].setw(j, parent2.getw(pointer2 + j - pointer1));
	                    }
	                }
	                
	                for(int j=0;j<newSize2;j++){
	                    if(genChoose2[j]==1){
	                    	child2[i].setKromX(j, parent2.getKromX(j));
	                    	child2[i].setKromY(j, parent2.getKromY(j));
	                    	child2[i].setAngle(j, parent2.getAngle(j));
	                    	child2[i].setlr(j, parent2.getlr(j));
	                    	child2[i].setw(j, parent2.getw(j));
	                    }else{
	                    	child2[i].setKromX(j, parent1.getKromX(pointer1 + j - pointer2));
	                    	child2[i].setKromY(j, parent1.getKromY(pointer1 + j - pointer2));
	                    	child2[i].setAngle(j, parent1.getAngle(pointer1 + j - pointer2));
	                    	child2[i].setlr(j, parent1.getlr(pointer1 + j - pointer2));
	                    	child2[i].setw(j, parent1.getw(pointer1 + j - pointer2));
	                    }
	                }	            
                }else{
                	 child1[i]=new DNA(n);
 	                 child2[i]=new DNA(n);
                }
                //jahesh             	
            	int randNum1, randNum2, randNum3;
            	double jahesh = mutationRate; 
                double isJahesh1=Math.random();
                if(isJahesh1<jahesh){   
                	int whichChange = (int)(Math.random()*2);
                	int place1=(int)(Math.random()*child1[i].length());
                	if(whichChange == 0){ // change LOCATION of one of the sensors in the dna, only                 	                   
                		randNum1 = (int)(Math.random()*global.hight);
                		randNum2 = (int)(Math.random()*global.width);
                		child1[i].setKromX(place1, randNum1);
                		child1[i].setKromY(place1, randNum2);
                	}else{// change SHAPE of one of the sensors in the dna, only
                		child1[i] = child1[i].mutateToAnotherShape(place1, child1[i]);
                	}
                }
                double isJahesh2=Math.random();
                if(isJahesh2<jahesh){   
                	int whichChange = (int)(Math.random()*2);
                	int place2=(int)(Math.random()*child2[i].length());
                	if(whichChange == 0){ // change LOCATION of one of the sensors in the dna, only                 	                   
                		randNum1 = (int)(Math.random()*global.hight);
                		randNum2 = (int)(Math.random()*global.width);
                		child2[i].setKromX(place2, randNum1);
                		child2[i].setKromY(place2, randNum2);
                	}else{// change SHAPE of one of the sensors in the dna, only                		
                		child2[i] = child2[i].mutateToAnotherShape(place2, child2[i]);                	
                	}
                }        
            }
            
            //entekhab bazmandegan
            double allCost[]=new double[m+2*u];
            DNA allPop[]=new DNA[m+2*u];
            for(int i=0;i<m;i++){
                allCost[i]=cost(pop[i]);
                allPop[i]=pop[i];
            }
            for(int i=m;i<m+u;i++){
                allCost[i]=cost(child1[i-m]);
                allPop[i]=child1[i-m];
            }
            for(int i=m+u;i<m+2*u;i++){
                allCost[i]=cost(child2[i-m-u]);
                allPop[i]=child2[i-m-u];
            }
            
            double allCostSorted []= new double[m+2*u];
            DNA allPopSorted []=new DNA[m+2*u];
            
            for (int i = 0; i < allCostSorted.length; i++) {
				double max = Integer.MIN_VALUE;
				int index = 0;
				for (int j = 0; j < allCost.length; j++) {
					if(allCost[j] > max){
						max = allCost[j];
						index = j;
					}										
				}
				allCostSorted[i] = max;
				allPopSorted[i] = allPop[index];
				allCost[index] = Integer.MIN_VALUE;
			}    
//            double best = allCostSorted[0];
            bestDNA = allPopSorted[0];
            int num = allPopSorted[0].length();            
            for(int i=0;i<m;i++){
                pop[i]=allPopSorted[i];
            }
            for(int i=0;i<m;i++){
                pop[i].setChoosed(false);
            }
            countP=0;
//            System.out.print(iteration + ") ");
//            System.out.print((int)best);
            finalLen = num;
//            System.out. print(" len:" + finalLen);
            
//            accuracy = (double)(acc(allPopSorted[0])/posTot);
            if(accuracy >= 1){
            	break;
            }
//            System.out.println("" + iteration + "- Acc: " + accuracy + " Best Cost: " + allCostSorted[0] + " at length: " + num );

//           System.out.println(" Accuracy:" + accuracy);
//           System.out.println(" Localization Accuracy:" + computeLocalizationAccuracy(allPopSorted[0]));           
//            System.out.print(" best:"); 
            locacc = computeLocalizationAccuracy(allPopSorted[0]);
            for (int i = 0; i < printBest.length; i++) {
				printBest[i][0] = -1;
				printBest[i][1] = -1;
				printBest[i][2] = -1;
				printBest[i][3] = -1;
				printBest[i][4] = -1;				
			}
            for (int j = 0; j < allPopSorted[0].length(); j++) {            	
             	printBest[j][0] = allPopSorted[0].krom[j][0];
             	printBest[j][1] = allPopSorted[0].krom[j][1];
             	printBest[j][2] = allPopSorted[0].krom[j][2];
             	printBest[j][3] = allPopSorted[0].krom[j][3];
             	printBest[j][4] = allPopSorted[0].krom[j][4];
 			}
            
            boolean flag = true;
            for (int i = 0; i < allPopSorted[0].length(); i++) {            	
				if(!(allPopSorted[0].krom[i][0] == seeIfConverged[i][0] && 
						allPopSorted[0].krom[i][1] == seeIfConverged[i][1] &&
								allPopSorted[0].krom[i][2] == seeIfConverged[i][2] &&
										allPopSorted[0].krom[i][3] == seeIfConverged[i][3] &&
												allPopSorted[0].krom[i][4] == seeIfConverged[i][4])){
					flag = false;
				}
			}
            if(flag){
            	convergedCount += 1;
            }else{
            	convergedCount = 0;
            }
            
            for (int i = 0; i < seeIfConverged.length; i++) {
				seeIfConverged[i][0] = -1;
				seeIfConverged[i][1] = -1;
				seeIfConverged[i][2] = -1;
				seeIfConverged[i][3] = -1;
				seeIfConverged[i][4] = -1;								
			}                      
            for (int i = 0; i < allPopSorted[0].length(); i++) {
				seeIfConverged[i][0] = allPopSorted[0].krom[i][0];
				seeIfConverged[i][1] = allPopSorted[0].krom[i][1];				
				seeIfConverged[i][2] = allPopSorted[0].krom[i][2];
				seeIfConverged[i][3] = allPopSorted[0].krom[i][3];
				seeIfConverged[i][4] = allPopSorted[0].krom[i][4];
			}    
            
            long now = System.nanoTime();
    	    long fromStart = now - startTime;
    	    fromStart /= 1000000;
//    	    long time = 100000;
    	     
    	    if(iteration % 10 == 0){
    	    	accuracy = (double)(acc(allPopSorted[0])/posTot);
    	    	System.out.println("" + iteration + "- Acc: " + accuracy + " Best Cost: " + allCostSorted[0] + " at length: " + num );
    	    	System.out.println("Time: " + fromStart);
    	    }
    	    
    	    
//    	    if(       fromStart/10000 == 613 || fromStart/10000 == 614
//    	    		||fromStart/10000 == 683 || fromStart/10000 == 684
//    	    		||fromStart/10000 == 753 || fromStart/10000 == 754
//    	    		||fromStart/10000 == 823 || fromStart/10000 == 824
//    	    		||fromStart/10000 == 893 || fromStart/10000 == 894
//    	    		||fromStart/10000 == 963 || fromStart/10000 == 964){ 
//                accuracy = (double)(acc(allPopSorted[0])/posTot);
//                System.out.println("" + iteration + "- Acc: " + accuracy + " Best Cost: " + allCostSorted[0] + " at length: " + num );
//        	    System.out.println("Time: " + fromStart);
//    	    	//break;
//    	    }    	    
            iteration++;            
//	    	System.out .println("itr: " + iteration + " at accuracy " + accuracy);
        }// End of while
        
        //stop the clock!
		endTime = System.nanoTime();
	    long duration = endTime - startTime;
	    long sec = duration/(int)(Math.pow(10, 9));
	    long mili = duration/(int)(Math.pow(10, 6))%1000;
//	    System.out.println("Elapsed time: " + duration);
//	    System.out.println("Elapsed time: "+ sec +" seconds and " + mili + " milliseconds");
//        System.out.println("Number of iterations: " + iteration);
	    double dur = duration / Math.pow(10, 9);
		try {
			writeResults(locacc, accuracy*100, dur, iteration, finalLen, mainNumber);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		saveImage(mainNumber);		        
    }
    
public double acc(DNA dna){
    	double acc = 0;
    	int roomCopy[][] = new int[global.hight][global.width];
    	for (int j = 0; j < global.hight; j++) {
			for (int j2 = 0; j2 < global.width; j2++) {
				roomCopy[j][j2] = room[j][j2];
				roomCovered[j][j2] = false;
			}
		}
     
    	for (int i = 0; i < dna.length(); i++) {	
    		if(dna.getw(i) != -1){ // rectangle
    			if(dna.getAngle(i) == 0){ // 0 degree orientation
		    		int fromX, fromY, toX, toY;
		        	fromX = dna.getKromX(i)-dna.getlr(i)/2 > 0 ? dna.getKromX(i)-dna.getlr(i)/2 : 0;
		        	fromY = dna.getKromY(i)-dna.getw(i)/2 > 0 ? dna.getKromY(i)-dna.getw(i)/2 : 0;
		        	toX = dna.getKromX(i)+dna.getlr(i)/2 < global.hight ? dna.getKromX(i)+dna.getlr(i)/2 : global.hight;
		        	toY = dna.getKromY(i)+dna.getw(i)/2 < global.width ? dna.getKromY(i)+dna.getw(i)/2 : global.width;
		        	
		        	for (int j = fromX; j < toX; j++) {        		
						for (int k = fromY; k < toY; k++) {
							if(roomCopy[j][k]>0){
						        roomCopy[j][k] -= 1;
								roomCovered[j][k] = true;
							}		
						}
					}
    			}else{// 90 degree orientation
    				int fromX, fromY, toX, toY;
		        	fromX = dna.getKromX(i)-dna.getw(i)/2 > 0 ? dna.getKromX(i)-dna.getw(i)/2 : 0;
		        	fromY = dna.getKromY(i)-dna.getlr(i)/2 > 0 ? dna.getKromY(i)-dna.getlr(i)/2 : 0;
		        	toX = dna.getKromX(i)+dna.getw(i)/2 < global.hight ? dna.getKromX(i)+dna.getw(i)/2 : global.hight;
		        	toY = dna.getKromY(i)+dna.getlr(i)/2 < global.width ? dna.getKromY(i)+dna.getlr(i)/2 : global.width;
		        	
		        	for (int j = fromX; j < toX; j++) {        		
						for (int k = fromY; k < toY; k++) {
							if(roomCopy[j][k]>0){
						        roomCopy[j][k] -= 1;
								roomCovered[j][k] = true;
							}		
						}
					}
    			}
    		}else{ // circle 
    			int fromX, fromY, toX, toY;
	        	fromX = dna.getKromX(i)-dna.getlr(i) > 0 ? dna.getKromX(i)-dna.getlr(i) : 0;
	        	fromY = dna.getKromY(i)-dna.getlr(i) > 0 ? dna.getKromY(i)-dna.getlr(i) : 0;
	        	toX = dna.getKromX(i)+dna.getlr(i) < global.hight ? dna.getKromX(i)+dna.getlr(i) : global.hight;
	        	toY = dna.getKromY(i)+dna.getlr(i) < global.width ? dna.getKromY(i)+dna.getlr(i) : global.width;
    			for (int j = fromX; j < toX; j++) {
     				for (int j2 = fromY; j2 < toY; j2++) {
     					if(Math.pow(j-dna.getKromX(i),2) + Math.pow(j2-dna.getKromY(i),2) <= Math.pow(dna.getlr(i), 2)){
     						if(roomCopy[j][j2]>0){
						        roomCopy[j][j2] -= 1;
								roomCovered[j][j2] = true;
							}								
     					}
     				}
    			}
    		}
    	}
    	
    	for (int j = 0; j < global.hight; j++) {
			for (int j2 = 0; j2 < global.width; j2++) {	
				if(roomCovered[j][j2] && room[j][j2]>0){	
					acc += room[j][j2];
				}
			}
		}
    	return acc;
    }

    public double cost(DNA dna){
    	double cost=0;
       	int roomCopy[][] = new int [global.hight][global.width];
    	for (int j = 0; j < global.hight; j++) {
			for (int j2 = 0; j2 < global.width; j2++) {
				roomCopy[j][j2] = room[j][j2];
			}
		}
    	
    	for (int i = 0; i < dna.length(); i++) {	
    		if(dna.getw(i) != -1){ // rectangle
    			if(dna.getAngle(i) == 0){ // 0 degree orientation
		    		int fromX, fromY, toX, toY;
		        	fromX = dna.getKromX(i)-dna.getlr(i)/2 > 0 ? dna.getKromX(i)-dna.getlr(i)/2 : 0;
		        	fromY = dna.getKromY(i)-dna.getw(i)/2 > 0 ? dna.getKromY(i)-dna.getw(i)/2 : 0;
		        	toX = dna.getKromX(i)+dna.getlr(i)/2 < global.hight ? dna.getKromX(i)+dna.getlr(i)/2 : global.hight;
		        	toY = dna.getKromY(i)+dna.getw(i)/2 < global.width ? dna.getKromY(i)+dna.getw(i)/2 : global.width;
		        	
		        	for (int j = fromX; j < toX; j++) {        		
						for (int k = fromY; k < toY; k++) {
							cost += roomCopy[j][k];								
							if(roomCopy[j][k]>0){
						        roomCopy[j][k] -= 1;	        	    			        			       							
							}		
						}
					}
    			}else{// 90 degree orientation
    				int fromX, fromY, toX, toY;
		        	fromX = dna.getKromX(i)-dna.getw(i)/2 > 0 ? dna.getKromX(i)-dna.getw(i)/2 : 0;
		        	fromY = dna.getKromY(i)-dna.getlr(i)/2 > 0 ? dna.getKromY(i)-dna.getlr(i)/2 : 0;
		        	toX = dna.getKromX(i)+dna.getw(i)/2 < global.hight ? dna.getKromX(i)+dna.getw(i)/2 : global.hight;
		        	toY = dna.getKromY(i)+dna.getlr(i)/2 < global.width ? dna.getKromY(i)+dna.getlr(i)/2 : global.width;
//		        	System.out.println(fromX + " : " + toX + " / " + fromY + " : " + toY);
		        	for (int j = fromX; j < toX; j++) {        		
						for (int k = fromY; k < toY; k++) {
							cost += roomCopy[j][k];
							if(roomCopy[j][k]>0){
						        roomCopy[j][k] -= 1;	        	    			        			       							
							}		
						}
					}
    			}
    		}else{ // circle 
    			int fromX, fromY, toX, toY;
	        	fromX = dna.getKromX(i)-dna.getlr(i) > 0 ? dna.getKromX(i)-dna.getlr(i) : 0;
	        	fromY = dna.getKromY(i)-dna.getlr(i) > 0 ? dna.getKromY(i)-dna.getlr(i) : 0;
	        	toX = dna.getKromX(i)+dna.getlr(i) < global.hight ? dna.getKromX(i)+dna.getlr(i) : global.hight;
	        	toY = dna.getKromY(i)+dna.getlr(i) < global.width ? dna.getKromY(i)+dna.getlr(i) : global.width;
    			for (int j = fromX; j < toX; j++) {
     				for (int k = fromY; k < toY; k++) {
     					if(Math.pow(j-dna.getKromX(i),2) + Math.pow(k-dna.getKromY(i),2) <= Math.pow(dna.getlr(i), 2)){
 							cost += roomCopy[j][k];
     						if(roomCopy[j][k]>0){
     							roomCopy[j][k] -= 1;
     						}
     					}
     				}
    			}
    		}
    	}
    	// cost # 1:
//        cost -= Math.pow(dna.length(),3)*lenghtCoeff;
    	
    	//cost # 2:
//    	cost = Math.pow(cost, 1/10);
        cost = cost - 0.5*dna.areaCost(dna) ;//- Math.pow(dna.length(),1)/global.maxNumberOfSensors;
        
        //cost # 3
//    	cost -= 0.5*dna.areaCost(dna);
        return cost;
    }
    
    public void saveImage(int mainNumber){
        for (int i = 0; i < global.hight; i++) {
			for (int j = 0; j < global.width; j++) {
		        printable[i][j]=room[i][j];		        
			}
		}
        for (int i = 0; i < printBest.length; i++) {
        	if(printBest[i][0] != -1){ // its a valid sensor 
	        	if(printBest[i][4] != -1){ // its a rectangle
	        		if(printBest[i][2] == 0){ // 0 degrees orientation
			        	int fromX, fromY, toX, toY;
			        	fromX = printBest[i][0]-printBest[i][3]/2 > 0 ? printBest[i][0]-printBest[i][3]/2 : 0;
			        	fromY = printBest[i][1]-printBest[i][4]/2 > 0 ? printBest[i][1]-printBest[i][4]/2 : 0;
			        	toX = printBest[i][0]+printBest[i][3]/2 < global.hight-1 ? printBest[i][0]+printBest[i][3]/2 : global.hight-1;
			        	toY = printBest[i][1]+printBest[i][4]/2 < global.width-1  ? printBest[i][1]+printBest[i][4]/2 : global.width-1;
			        	if(printBest[i][0]!= -1){	        	  
				        	for (int e = fromX; e <= toX; e++) {
								printable[e][fromY] = 5;			
							}	
							for (int e = fromX; e <= toX; e++) {
								printable[e][toY] = 5;				
							}	
							for (int e = fromY; e <= toY; e++) {
								printable[fromX][e] = 5;				
							}	
							for (int e = fromY; e <= toY; e++) {
								printable[toX][e] = 5;				
							}	
			        	}
	        		}else if(printBest[i][2] == 90){ // 90 degrees orientation
	        			int fromX, fromY, toX, toY;
			        	fromX = printBest[i][0]-printBest[i][4]/2 > 0 ? printBest[i][0]-printBest[i][4]/2 : 0;
			        	fromY = printBest[i][1]-printBest[i][3]/2 > 0 ? printBest[i][1]-printBest[i][3]/2 : 0;
			        	toX = printBest[i][0]+printBest[i][4]/2 < global.hight-1 ? printBest[i][0]+printBest[i][4]/2 : global.hight-1;
			        	toY = printBest[i][1]+printBest[i][3]/2 < global.width-1  ? printBest[i][1]+printBest[i][3]/2 : global.width-1;
			        	if(printBest[i][0]!= -1){	        	  
				        	for (int e = fromX; e <= toX; e++) {
								printable[e][fromY] = 5;			
							}	
							for (int e = fromX; e <= toX; e++) {
								printable[e][toY] = 5;				
							}	
							for (int e = fromY; e <= toY; e++) {
								printable[fromX][e] = 5;				
							}	
							for (int e = fromY; e <= toY; e++) {
								printable[toX][e] = 5;				
							}	
			        	}
	        		}
				}else{ // its a circle
					for (int j = 0; j < global.hight; j++) {
						for (int j2 = 0; j2 < global.width; j2++) {								
							if( ((Math.pow(j-printBest[i][0], 2) +  Math.pow(j2-printBest[i][1], 2)) <= Math.pow(printBest[i][3], 2)) && 
								((Math.pow(j-printBest[i][0], 2) +  Math.pow(j2-printBest[i][1], 2)) >= (printBest[i][3]-1)*(printBest[i][3]-1))){
								printable[j][j2] = 5;
							}						
						}
					}													
				}
        	}
        }
   	
        for (int i = 0; i < global.hight; i++) {
			for (int j = 0; j < global.width; j++) {
				if(printable[i][j] == -2){
					image[i][j][2] = 192; 
					image[i][j][1] = 192;
					image[i][j][0] = 192;
				}else if(printable[i][j] == -1){
					image[i][j][2] = 0; 
					image[i][j][1] = 0;
					image[i][j][0] = 0;
				}else if(printable[i][j] == 0){
					image[i][j][2] = 255; 
					image[i][j][1] = 255;
					image[i][j][0] = 255;			
				}else if(printable[i][j] == 1){
					image[i][j][2] = 255;
					image[i][j][1] = 205;
					image[i][j][0] = 205;
				}else if(printable[i][j] == 2){
					image[i][j][2] = 255; 
					image[i][j][1] = 155;
					image[i][j][0] = 155;
				}else if(printable[i][j] == 3){
					image[i][j][2] = 255;
					image[i][j][1] = 105;
					image[i][j][0] = 105;
				}else if(printable[i][j] == 4){
					image[i][j][2] = 255; 
					image[i][j][1] = 0;
					image[i][j][0] = 0;
				}else if(printable[i][j] == 5){
					image[i][j][2] = 0; 
					image[i][j][1] = 0;
					image[i][j][0] = 0;
				}else if(printable[i][j] == 6){
					image[i][j][2] = 255; 
					image[i][j][1] = 255;
					image[i][j][0] = 255;
				}
			}		
        }        
		BufferedImage theImage = new BufferedImage(global.width, global.hight, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y<global.width; y++){
            for(int x = 0; x<global.hight; x++){            	
            	int value = (image[x][y][2] << 16 | image[x][y][1] << 8 | image[x][y][0]);            	
                theImage.setRGB(y, x, value);
            }           
        }
     
        //Create image        
        File outputfile = new File(global.url + "/saved_" + expNumber + "_" + mainNumber + ".png");
        try {
			ImageIO.write(theImage, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    }
