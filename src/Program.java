package COS700_Project;

public class Program {

          private final byte[][] main;                //[level in main brach][position in level]
          private final byte[][][][] conditions; //[level in main branch rooted from][level in sub-branch][sub-branch level][position in level in sub-branch]
          private double fitness;

          /**
           * @throws Exception
           */
          public Program() throws Exception {
                    main = FlyWeight.getInstance().getByteArray2dMain();
                    conditions = FlyWeight.getInstance().addByteArray4d();
                    //_allocateArrays(main_depth);
          }

          /**
           * @param m
           * @param c
           * @throws Exception
           */
          private void _copy(byte[][] m, byte[][][][] c) throws Exception {
                    if (m != null && c != null) {
                              for (int main_depth = 0; main_depth < Parameters.getInstance().getMain_max_depth(); main_depth++) {
                                        System.arraycopy(m[main_depth], 0, this.main[main_depth], 0, (1 << main_depth));
                                        for (int condition = 0; condition < (1 << main_depth); condition++) {
                                                  for (int condition_depth = 0; condition_depth < Parameters.getInstance().getCondition_max_depth(); condition_depth++) {
                                                            System.arraycopy(c[main_depth][condition][condition_depth], 0, this.conditions[main_depth][condition][condition_depth], 0, (1 << condition_depth));
                                                  }
                                        }
                              }
                    } else {
                              throw new Exception("Program could not be instantiated correctly.");
                    }
          }

          public double getFitness() {
                    return fitness;
          }

          public void setFitness(double fitness) {
                    this.fitness = fitness;
          }

          /**
           *
           * @param copy
           * @throws Exception
           */
          public void copy(Program copy) throws Exception {
                    if (copy != null) {
                              _copy(copy.main, copy.conditions);
                    } else {
                              throw new Exception("Cannot copy null Program");
                    }

          }

          /**
           * @return
           */
          public byte[][][][] getConditions() {
                    return conditions;
          }

          /**
           * @return
           */
          public byte[][] getMain() {
                    return main;
          }

}
