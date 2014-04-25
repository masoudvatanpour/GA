package HIP;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class controller {	
		Main main;
		ArrayList<tuple> tuples;
		int mainNumber = 1;
		public void controller(){
			System.out.println("*** A Genetic Algorithm Approach ***");
			addTuples();
			int num = 1;
			int exp = 1;
//			System.out.println("START");
			for (tuple one : tuples) {	
				try{
					FileWriter fstream = new FileWriter("Results.txt",true);
			        BufferedWriter fbw = new BufferedWriter(fstream);       
			    	fbw.write("Exp "+exp+":\n");
			        fbw.close();
				}catch(IOException e){
					e.printStackTrace();
				}
				for (int i = 0; i < num; i++) {
					System.out.println();//"main " + mainNumber + "!");
					main = new Main(one.getm(), one.getn(), one.getu(), one.getItr(), one.getp_c(), one.getp_m(), one.getString(), mainNumber, exp);				
					main.run();
					mainNumber ++;
//					Thread t1 = new Thread(main);		
//					t1.start();
				}
				exp ++;
				mainNumber = 1;
				System.out.println("\nEnd of tuple");
				try{
					FileWriter fstream = new FileWriter("Results.txt",true);
			        BufferedWriter fbw = new BufferedWriter(fstream);       
			    	fbw.write("\n\n\n\n");
			        fbw.close();
				}catch(IOException e){
					e.printStackTrace();
				}
								
			}			
			System.out.println("FINISH");

//			myTimer mytimer = new myTimer(this);
//			Thread t2 = new Thread(mytimer);
//			t2.start();
		}
		
		public void addTuples(){
			tuples = new ArrayList<tuple>();		
			// . . . . . . . . . m    n   u    l    p_c  p_m  filename				
			
//			tuples.add(new tuple(100, 10, 100, 100000, 0.4, 0.5, global.filename));			
//			tuples.add(new tuple(200, 10, 200, 100000, 0.4, 0.5, global.filename));
//			tuples.add(new tuple(300, 10, 300, 100000, 0.4, 0.5, global.filename));
//			tuples.add(new tuple(400, 10, 400, 100000, 0.4, 0.5, global.filename));
			tuples.add(new tuple(500, 10, 500, 100000, 0.4, 0.5, global.filename));
//			tuples.add(new tuple(600, 10, 600, 100000, 0.4, 0.5, global.filename));
		}
		public void drive(){
			try {
				main.driver();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}

