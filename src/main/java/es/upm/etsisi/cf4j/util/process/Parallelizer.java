package es.upm.etsisi.cf4j.util.process;

/** This class is used to simplify the parallelization of collaborative filtering algorithms */
public class Parallelizer {

  /**
   * Execs Partible for each object contained in the objects array. Each execution is run in
   * parallel way. All available threads of the CPU will be used.
   *
   * @param objects Array of objects to be executed in parallel
   * @param partible Partible to be executed for each object of the array
   */
  public static void exec(Object[] objects, Partible partible) {
    exec(objects, partible, -1);
  }

  /**
   * Execs Partible for each object contained in the objects array. Each execution is run in
   * parallel way.
   *
   * @param objects Array of objects to be executed in parallel
   * @param partible Partible to be executed for each object of the array
   * @param numThreads Number of threads to be launched in parallel
   */
  public static void exec(Object[] objects, Partible partible, int numThreads) {

    // use all processors if required
    if (numThreads <= 0) numThreads = Runtime.getRuntime().availableProcessors();

    // execute beforeRun method once
    partible.beforeRun();

    // create and launch threads
    PartibleThread[] pt = new PartibleThread[numThreads];
    for (int i = 0; i < pt.length; i++) {
      pt[i] = new PartibleThread(i, partible, objects, numThreads);
    }

    // wait until all threads end
    try {
      for (PartibleThread partibleThread : pt) {
        partibleThread.getThread().join();
      }
    } catch (InterruptedException ie) {
      System.err.println("ERROR: " + ie);
    }

    // execute afterRun method once
    partible.afterRun();
  }

  /** Inner class used to handle parallel execution of the objects in the array */
  private static class PartibleThread implements Runnable {

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

      this.thread = new Thread(this, String.valueOf(threadIndex));
      this.thread.start();
    }

    public Thread getThread() {
      return this.thread;
    }

    @Override
    public void run() {
      for (int i = this.threadIndex; i < this.objects.length; i += this.numThreads) {
        partible.run(this.objects[i]);
      }
    }
  }
}
