package COS700_Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Fitness {

          private static Fitness singleton = null;

          private Fitness() {
          }

          /**
           * @return @throws Exception
           */
          public static Fitness getInstance() throws Exception {
                    if (singleton == null) {
                              singleton = new Fitness();
                    }
                    return singleton;
          }

          /**
           * @param prog
           * @return
           * @throws Exception
           */
          public double evaluate(Program prog) throws Exception {
                    if (prog != null) {
                              return f1(prog);
                    } else {
                              throw new Exception("Cannot evaluate null program.");
                    }
          }

          private double f1(Program prog) throws Exception {
                    double f1 = 0;

                    double[] tp = new double[Data.initialiseData().getNumberClasses()];
                    double[] fp = new double[Data.initialiseData().getNumberClasses()];
                    double[] fn = new double[Data.initialiseData().getNumberClasses()];
                    for (int i = 0; i < Data.initialiseData().getNumberClasses(); i++) {
                              tp[i] = 0;
                              fp[i] = 0;
                              fn[i] = 0;
                    }
                    for (double[] instance : Data.initialiseData().getTrainSet()) {
                              int target = (int) instance[Data.initialiseData().getNumberAttributes()];
                              int output = (int) Interpreter.getInstance().Interpret(prog, instance);
                              if (target == output) {
                                        tp[target] += 1;

                              } else {
                                        fp[output] += 1;
                                        fn[target] += 1;
                              }
                    }
                    double precision = 0;
                    double recall = 0;
                    for (int i = 0; i < Data.initialiseData().getNumberClasses(); i++) {
                              precision += tp[i] + fp[i] > 0 ? (tp[i] / (tp[i] + fp[i])) : 0;
                              recall += tp[i] + fn[i] > 0 ? (tp[i] / (tp[i] + fn[i])) : 0;
                    }
                    /*
                              System.out.println("pecision  :" + Arrays.toString(precision));
                              System.out.println("recall  :" + Arrays.toString(recall));
                              return 0; */
                    precision /= Data.initialiseData().getNumberClasses();
                    recall /= Data.initialiseData().getNumberClasses();
                    f1 = (2.0 * (precision * recall) / (precision + recall));
                    return f1 >= 0 ? f1 : 0;
          }
}
