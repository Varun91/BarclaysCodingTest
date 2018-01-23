package trafficsignal;

import java.util.*;

/*
 * The Traffic Signal class simulates the traffic signals for a four-way intersection.
 * @author Varun Bhat
 * @version 1.0
 * @since 1/14/2018
 */
public class TrafficSignal {

	// this queue simulates the snell road. Cars waiting at the intersection get
	// added to the queue
	static volatile Queue<Integer> snell = new LinkedList<Integer>();
	// this queue simulates the weaver road. Cars waiting at the intersection
	// get added to the queue
	static volatile Queue<Integer> weaver = new LinkedList<Integer>();

	static volatile boolean snellGreen = true; // true represents GREEN and
												// false represents RED.
												// Initially Snell road signal
												// is GREEN
	static volatile boolean weaverGreen = false;// true represents GREEN and
												// false represents RED.
												// Initially weaver road signal
												// is RED

	static volatile int printcount = 0;

	static volatile int snellCount = 0;
	static volatile int weaverCount = 0;

	static final int DISPLAY_PERIOD = 1000; // rate(ms) at which number of cars
											// on the intersection are displayed
	static final int ARRIVAL_PERIOD = 1000; // rate(ms) at which cars arrive
	static final int GREEN_SIG_PERIOD = 3000; // time(ms) for which green signal
												// is ON
	static final int RED_SIG_PERIOD = 1000; // time(ms) for which red signal is
											// ON
	static final int INITIAL_CAR_START = 2000;// time(ms) for 1st car to cross
												// the intersection
	static final int SUB_CAR_START = 1000; // time(ms) for subsequent cars to
											// cross the intersection
	static final int NUM_SEC = 20;// prints for first 20 seconds

	public static void main(String[] args) {
		Snell s = new Snell();
		Weaver w = new Weaver();
		Print p = new Print();
		s.start();
		w.start();
		p.start();
	}

	/*
	 * The Print class prints the state of cars at the intersection after every
	 * 1 second
	 */
	private static class Print extends Thread {
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				System.out.println(printcount + ": " + "N = " + snell.size() + "; " + "S = " + snell.size() + "; "
						+ "E = " + weaver.size() + ";" + "W = " + weaver.size());

				/*
				 * if snell queue is not empty and signal is green OR if signal
				 * is red, add car to the queue
				 */
				if ((!snell.isEmpty() && snellGreen) || !snellGreen) {
					snell.add(1);
				}
				/*
				 * if weaver queue is not empty and signal is green OR if signal
				 * is red, add car to the queue
				 */

				if ((!weaver.isEmpty() && weaverGreen) || !weaverGreen) {
					weaver.add(1);
				}
				/*
				 * stop after 20 seconds
				 */
				if (printcount == NUM_SEC) {
					System.exit(0);
				}
				printcount++;
				try {
					/*
					 * prints after every 1 second
					 */
					Thread.sleep(DISPLAY_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/*
	 * The Snell Class simulates the state of the cars on the snell road
	 */
	private static class Snell extends Thread {
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				if (snellGreen && !snell.isEmpty()) {
					try {
						/*
						 * If its the first car, It will take 2 seconds
						 * snellCount = 0 means that its the first car
						 */
						if (snellCount == 0) {
							snellCount += INITIAL_CAR_START / 1000;
							Thread.sleep(INITIAL_CAR_START);
							snell.poll();

						}
						/*
						 * If its the subsequent car, it will take 1 second to
						 * cross the intersection. snellCount>=2 means
						 * subsequent cars
						 * 
						 */

						else if (snellCount >= INITIAL_CAR_START / 1000 && snellCount < GREEN_SIG_PERIOD / 1000) {
							snellCount++;
							Thread.sleep(SUB_CAR_START);
							snell.poll();

						}
						/*
						 * Turn snell road signal to RED after GREEN signal
						 * period is over i.e after 3 sec in this case
						 */
						else {
							snellGreen = false; // turn snell road signal to RED
							Thread.sleep(RED_SIG_PERIOD);// keep snell road
															// signal RED for 1
															// sec
							weaverGreen = true; // turn weaver road signal to
												// GREEN
							weaverCount = 0;
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				/*
				 * If Snell road signal is green and no cars are in the queue
				 * then no need for cars to wait. They will pass directly
				 * without waiting.
				 */
				else if (snellGreen && snell.isEmpty()) {
					if (snellCount == (GREEN_SIG_PERIOD / 1000)) {

						snellGreen = false; // after green signal period is
											// over, turn snell road signal to
											// RED
						try {
							Thread.sleep(RED_SIG_PERIOD); // keep snell road
															// signal RED for 1
															// sec
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						weaverGreen = true; // turn weaver road signal GREEN
						weaverCount = 0;
					} else {
						try {
							Thread.sleep(ARRIVAL_PERIOD); // cars arrive after 1
															// sec
							snellCount++;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/*
	 * The Weaver Class simulates the state of the cars on the weaver road
	 */
	private static class Weaver extends Thread {
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				if (weaverGreen && !weaver.isEmpty()) {

					try {
						/*
						 * If its the first car, It will take 2 seconds
						 * weaverCount = 0 means that its the first car
						 */
						if (weaverCount == 0) {
							weaverCount += INITIAL_CAR_START / 1000;
							Thread.sleep(INITIAL_CAR_START);
							weaver.poll();

						}
						/*
						 * If its the subsequent car, it will take 1 second to
						 * cross the intersection weaverCount>=2 means
						 * subsequent cars
						 * 
						 */
						else if (weaverCount >= INITIAL_CAR_START / 1000 && weaverCount < GREEN_SIG_PERIOD / 1000) {
							weaverCount++;
							Thread.sleep(SUB_CAR_START);
							weaver.poll();

						}
						/*
						 * Turn weaver road signal to RED after GREEN signal
						 * period is over i.e after 3 sec in this case
						 */
						else {
							weaverGreen = false; // turn weaver road signal to
													// RED
							Thread.sleep(RED_SIG_PERIOD);// keep weaver road
															// signal RED for 1
															// sec
							snellGreen = true; // turn snell road signal to
												// GREEN
							snellCount = 0;
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				/*
				 * If Weaver road signal is green and no cars are in the queue
				 * then no need for cars to wait. They will pass directly
				 * without waiting.
				 */
				else if (weaverGreen && weaver.isEmpty()) {
					if (weaverCount == (GREEN_SIG_PERIOD / 1000)) {
						weaverGreen = false; // after green signal period is
												// over, turn weaver road signal
												// to RED
						try {
							Thread.sleep(RED_SIG_PERIOD); // keep weaver road
															// signal RED for 1
															// sec
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						snellGreen = true; // turn snell road signal to GREEN
						snellCount = 0;
					} else {
						try {
							Thread.sleep(ARRIVAL_PERIOD); // cars arrive after 1
															// sec
							weaverCount++;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			}
		}
	}

}