package players;

public class ThreadKiller implements Runnable {
	
	Thread myThread;
	
	public ThreadKiller(Thread t) {
		myThread = t;
	}

	@Override
	public void run() {
		try {
			//Thread.sleep(120000l);
			Thread.sleep(30000l);
			if(myThread.isAlive()) {
				myThread.interrupt();
			}
		} catch (InterruptedException e) {
			
		}
	}

}
