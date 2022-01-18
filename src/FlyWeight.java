package COS700_Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.CyclicBarrier;

public class FlyWeight {

          private static FlyWeight singleton = null;
          private final ArrayList<ArrayList<int[]>> al_int_arr;
          private final ArrayList<Stack<int[]>> stack_int_arr;
          private final ArrayList<ArrayList<Integer>> al_int;
          private final ArrayList<Stack<Integer>> stack_int;
          private final ArrayList<byte[][]> shrt_arr_2D_m;
          private final ArrayList<byte[][]> shrt_arr_2D_c;
          private final ArrayList<byte[][][][]> shrt_arr_4D;
          private final ArrayList<Program> programs;
          private final ArrayList<Generation> generations; 
          private final ArrayList<ArrayList<RunnerThread>> run_arr_threads;
          private final ArrayList<RunnerThread> run_threads;
          private final ArrayList<Random> random_arr;
          private final ArrayList<CyclicBarrier> cb_arr;
          private final ArrayList<int[]> instance_arrays;
          private final ArrayList<double[]> score_arrays;
          private final ArrayList<double[]> attribute_arrays;
          private final ArrayList<double[][]> problem_score_arrays;
 

          private FlyWeight() {
                    score_arrays = new ArrayList<>();
                    attribute_arrays = new ArrayList<>();
                    instance_arrays = new ArrayList<>();
                    problem_score_arrays = new ArrayList<>();
                    stack_int_arr = new ArrayList<>();
                    run_threads = new ArrayList<>();
                    run_arr_threads = new ArrayList<>();
                    cb_arr = new ArrayList<>();
                    random_arr = new ArrayList<>();
                    al_int_arr = new ArrayList<>();
                    al_int = new ArrayList<>();
                    stack_int = new ArrayList<>();
                    shrt_arr_2D_m = new ArrayList<>();
                    shrt_arr_2D_c = new ArrayList<>();
                    shrt_arr_4D = new ArrayList<>();
                    programs = new ArrayList<>();
                    generations = new ArrayList<>();
          }

          /**
           * @return singleton.
           */
          public static FlyWeight getInstance() {
                    if (singleton == null) {
                              singleton = new FlyWeight();
                    }
                    return singleton;
          }

          public void addCyclicBarrier(CyclicBarrier obj) throws Exception {
                    if (obj != null) {
                              cb_arr.add(obj);
                    } else {
                              throw new Exception("Cannot add null CyclicBarrier object");
                    }
          }

          public CyclicBarrier getCyclicBarrier() {
                    if (cb_arr.isEmpty()) {
                              return new CyclicBarrier(0);
                    } else {
                              return cb_arr.remove(0);
                    }
          }
           

          public synchronized void addRunnerThread(RunnerThread thread) throws Exception {
                    if (thread != null) {
                              run_threads.add(thread);
                    } else {
                              throw new Exception("Cannot add null GeneticOperatorThread thread");
                    }

          } 
 
 

          public synchronized void addRunnerThreads(ArrayList<RunnerThread> obj) throws Exception {
                    if (obj != null) {
                              run_arr_threads.add(obj);
                    } else {
                              throw new Exception("Cannot add null ArrayList<GeneticOperatorThread> obj ");
                    }
          }

          public synchronized int[] getInstanceArray() {
                    if (instance_arrays.isEmpty()) {
                              return new int[9];
                    } else {
                              int[] arr = instance_arrays.remove(0);
                              Arrays.fill(arr, 0);
                              return arr;
                    }
          }

          public synchronized void addInstanceArray(int[] instances) throws Exception {
                    if (instances != null) {
                              instance_arrays.add(instances);
                    } else {
                              throw new Exception("Cannot add null int[] to instances array.");
                    }
          }
 

          public synchronized double[] getAttributeArray() throws Exception {
                    if (attribute_arrays.isEmpty()) {
                              return new double[Data.initialiseData().getNumberAttributes()];
                    } else {
                              double[] arr = attribute_arrays.remove(0);
                              Arrays.fill(arr, 0);
                              return arr;
                    }
          }

          public synchronized void addAttributeArray(double[] attributes) throws Exception {
                    if (attributes != null) {
                              attribute_arrays.add(attributes);
                    } else {
                              throw new Exception("Cannot add null double[] to attributes array.");
                    }
          }
          
          public synchronized double[] getScoreArray() {
                    if (score_arrays.isEmpty()) {
                              return new double[9];
                    } else {
                              double[] arr = score_arrays.remove(0);
                              Arrays.fill(arr, 0);
                              return arr;
                    }
          }

          public synchronized void addScoreArray(double[] scores) throws Exception {
                    if (scores != null) {
                              score_arrays.add(scores);
                    } else {
                              throw new Exception("Cannot add null double[] to score array.");
                    }
          }

          public synchronized Stack<int[]> getStack_int_arr() {
                    if (stack_int_arr.isEmpty()) {
                              return new Stack<int[]>();
                    } else {
                              stack_int_arr.clear();
                              return stack_int_arr.remove(0);
                    }
          }

          public synchronized void addStack_int_arr(Stack<int[]> obj) throws Exception {
                    if (obj != null) {
                              stack_int_arr.add(obj);
                    } else {
                              throw new Exception("Cannot add null Stack<int[]> obj ");
                    }
          }
 

          public synchronized ArrayList<RunnerThread> getRunnerThreads() {
                    if (run_arr_threads.isEmpty()) {
                              return new ArrayList<RunnerThread>();
                    } else {
                              ArrayList<RunnerThread> threads = run_arr_threads.remove(0);
                              threads.clear();
                              return threads;
                    }
          }
          
          public synchronized void addRandom(Random obj) throws Exception {
                    synchronized (singleton) {
                              if (obj != null) {
                                        random_arr.add(obj);
                              } else {
                                        throw new Exception("Cannot add null Random obj");
                              }
                    }
          }

          public synchronized Random getRandom() {
                    synchronized (singleton) {
                              if (random_arr.isEmpty()) {
                                        return new Random();
                              } else {
                                        return random_arr.remove(0);
                              }
                    }
          }

          public void addProblem_score_arrays(double [][] problem_scores) throws Exception{
                    if(problem_scores != null){
                              problem_score_arrays.add(problem_scores);
                    }else{
                              throw new Exception("Cannot add null double [][]" );
                    }
          }
          
          public double[][] getProblem_score_arrays() {
                    if (problem_score_arrays.isEmpty()) {
                              double[] YourSAT = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                              double[] YourBinPacking = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                              double[] YourPersonnelScheduling = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                              double[] YourFlowshop = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

                              return new double[][]{YourSAT, YourBinPacking, YourPersonnelScheduling, YourFlowshop};
                    } else {
                              return  problem_score_arrays.remove(0);
                    }
          }

          /**
           * @return ArrayList<Integer>
           */
          public synchronized ArrayList<Integer> getArrayListInt() {
                    ArrayList<Integer> al;
                    if (!al_int.isEmpty()) {
                              al = al_int.remove(0);
                              al.clear();
                    } else {
                              al = new ArrayList<>();
                    }

                    return al;
          }

          /**
           * @param al
           * @throws Exception
           */
          public synchronized void addArrayListInt(ArrayList<Integer> al) throws Exception {
                    if (al != null) {
                              al_int.add(al);
                    } else {
                              throw new Exception("Cannot add a null ArrayList<Integer> object to flyweight. ");
                    }

          }

          /**
           * @return a Generation
           * @throws Exception
           */
          public synchronized Generation getGeneration() throws Exception {
                    Generation gen = null;
                    if (!generations.isEmpty()) {
                              gen = generations.remove(0);
                    } else {
                              gen = new Generation();
                    }
                    return gen;
          }

          /**
           * @param generation
           * @throws Exception
           */
          public synchronized void addGeneration(Generation generation) throws Exception {
                    if (generation != null) {
                              generations.add(generation);
                    } else {
                              throw new Exception("Cannot add a null Generation object to flyweight.");
                    }
          }

          /**
           * @return program
           * @throws Exception
           */
          public synchronized Program getProgram() throws Exception {
                    Program prog = null;
                    if (!programs.isEmpty()) {
                              prog = programs.remove(0);
                    } else {
                              prog = new Program();
                    }
                    return prog;
          }

          public synchronized void addProgram(Program prog) throws Exception {
                    if (prog != null) {
                              programs.add(prog);
                    } else {
                              throw new Exception("Cannot add a null Program object to flyweight.");
                    }
          }

          /**
           * @return ArrayList<int[]>
           */
          public synchronized ArrayList<int[]> getArrayListIntArray() {
                    ArrayList<int[]> obj = null;
                    if (!al_int_arr.isEmpty()) {
                              obj = al_int_arr.remove(0);
                              obj.clear();
                    } else {
                              obj = new ArrayList<>();
                    }
                    return obj;
          }

          /**
           * @param obj
           * @throws Exception
           */
          public synchronized void addArrayListIntArray(ArrayList<int[]> obj) throws Exception {
                    if (obj != null) {
                              al_int_arr.add(obj);
                    } else {
                              throw new Exception("Cannot add null ArrayList<int[]> object to flyweight.");
                    }
          }

          /**
           * @return Stack<Integer>
           */
          public synchronized Stack<Integer> getStackInteger() {
                    synchronized (singleton) {
                              if (!stack_int.isEmpty()) {
                                        Stack<Integer> obj = stack_int.remove(0);
                                        obj.clear();
                                        return obj;
                              } else {
                                        return new Stack<Integer>();
                              }
                    }
          }

          /**
           * @param obj
           * @throws Exception
           */
          public synchronized void addStackInteger(Stack<Integer> obj) throws Exception {
                    synchronized (singleton) {
                              if (obj != null) {
                                        stack_int.add(obj);
                              } else {
                                        throw new Exception("Cannot add null Stack<Integer> object to flyweight.");
                              }
                    }

          }

          /**
           * @return byte[][]
           * @throws Exception
           */
          public synchronized byte[][] addByteArray2dMain() throws Exception {
                    byte[][] arr;
                    if (!shrt_arr_2D_m.isEmpty()) {
                              arr = shrt_arr_2D_m.remove(0);
                    } else {
                              arr = new byte[Parameters.getInstance().getMain_max_depth()][2 << (Parameters.getInstance().getMain_max_depth() - 1)];
                              for (int i = 0; i < Parameters.getInstance().getMain_max_depth(); i++) {
                                        Arrays.fill(arr[i], (byte) -1);
                              }
                    }
                    return arr;
          }

          /**
           * @return byte[][]
           * @throws Exception
           */
          public synchronized byte[][] getByteArray2dCondtion() throws Exception {
                    byte[][] arr;
                    if (!shrt_arr_2D_c.isEmpty()) {
                              arr = shrt_arr_2D_c.remove(0);
                    } else {
                              arr = new byte[Parameters.getInstance().getCondition_max_depth()][2 << (Parameters.getInstance().getCondition_max_depth() - 1)];
                    }
                    return arr;
          }

          public synchronized byte[][] getByteArray2dMain() throws Exception {
                    byte[][] arr;
                    if (!shrt_arr_2D_m.isEmpty()) {
                              arr = shrt_arr_2D_m.remove(0);
                    } else {
                              arr = new byte[Parameters.getInstance().getMain_max_depth()][2 << (Parameters.getInstance().getMain_max_depth() - 1)];
                    }
                    return arr;
          }

          /**
           * @param arr
           */
          public synchronized void addByteArray2dMain(byte[][] arr) throws Exception {
                    if (arr != null) {
                              shrt_arr_2D_m.add(arr);
                    } else {
                              throw new Exception("Cannot add null byte[][] object to flyweight.");
                    }
          }

          /**
           * @param arr
           */
          public synchronized void addByteArray2dCondition(byte[][] arr) throws Exception {
                    if (arr != null) {
                              shrt_arr_2D_c.add(arr);
                    } else {
                              throw new Exception("Cannot add null byte[][] object to flyweight.");
                    }
          }

          /**
           * @return byte[][][][]
           * @throws Exception
           */
          public synchronized byte[][][][] addByteArray4d() throws Exception {
                    byte[][][][] arr;
                    if (!shrt_arr_4D.isEmpty()) {
                              arr = shrt_arr_4D.remove(0);
                    } else {
                              int main = 2 << (Parameters.getInstance().getMain_max_depth() - 1);
                              int cond = 2 << (Parameters.getInstance().getCondition_max_depth() - 1);
                              arr = new byte[Parameters.getInstance().getMain_max_depth()][main][Parameters.getInstance().getCondition_max_depth()][cond];
                    }
                    return arr;
          }

          /**
           * @param arr
           * @throws Exception
           */
          public synchronized void addByteArray4D(byte[][][][] arr) throws Exception {
                    if (arr != null) {
                              shrt_arr_4D.add(arr);
                    } else {
                              throw new Exception("Cannot add null byte[][][][] object to flyweight.");
                    }
          }

}
