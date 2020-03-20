package es.upm.etsisi.cf4j.process;

public class Parallelizer {

	public static void exec(Object[] array, Partible partible) {
		exec(array, partible, -1);
	}


	public static void exec(Object[] objects, Partible partible, int numThreads) {

		// use all processors if required
		if (numThreads <= 0) numThreads = Runtime.getRuntime().availableProcessors();

		// execute before exec method
		partible.beforeRun();

		// create and launch threads
		PartibleThread[] pt = new PartibleThread[numThreads];
		for (int i = 0; i < pt.length; i++) {
			pt[i] = new PartibleThread(i, partible, objects, numThreads);
		}

		// wait until all threads end
		try {
			for (int i = 0; i < pt.length; i++) {
				pt[i].getThread().join();
			}
		} catch (InterruptedException ie) {
			System.out.println("ERROR: " + ie);
		}

		// execute after exec method
		partible.afterRun();
	}

	public static class PartibleThread implements Runnable {

		private Thread thread;
		private int threadIndex;
		private int numThreads;
		private Object[] objects;
		private Partible partible;

		public PartibleThread(int threadIndex, Partible partible, Object[] objects, int numThreads) {
			this.threadIndex = threadIndex;
			this.partible = partible;
			this.objects = objects;
			this.numThreads = numThreads;

			this.thread = new Thread (this, String.valueOf(threadIndex));
			this.thread.start();
		}

		public Thread getThread() {
			return this.thread;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Runnable#exec()
		 */
		public void run() {
			//long t1 = (new Date()).getTime() / 1000, t2, t3 = 0;

			for (int i = this.threadIndex; i < this.objects.length; i += this.numThreads ) {
				/*if (this.threadIndex == 0 && this.verbose) {
					t2 = (new Date()).getTime() / 1000;
					if ((t2 - t1) > 5) {
						System.out.print(".");
						t1 = t2;
						t3++;
					}
					if (t3 > 20) {
						System.out.println(((userIndex - this.threadIndex * this.runsPerThread) * 100 / this.runsPerThread) + "%");
						t3 = 0;
					}
				}*/

				partible.run(this.objects[i]);
			}
		}
	}
}
