package HIP;

import java.util.Timer;

class myTimer implements Runnable{	
	controller parent; 
	public myTimer(controller controller) {
		// TODO Auto-generated constructor stub
		this.parent = controller;
	}
	
    public void run() {
    	while(true){
    		parent.drive();
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
 }