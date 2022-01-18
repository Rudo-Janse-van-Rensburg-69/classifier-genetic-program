package COS700_Project;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;

public class SelectivePeturbativeHyperHeuristic extends HyperHeuristic {

          private final Program prog;
          private double attributes[];

          public SelectivePeturbativeHyperHeuristic(Program prog, long seed) throws Exception {
                    super(seed);
                    this.prog = prog;
                    this.attributes = new double[Data.initialiseData().getNumberAttributes()];
                    for (int i = 0; i < Data.initialiseData().getNumberAttributes(); i++) {
                              this.attributes[i] = 0;
                    }
          }

          private void add(double delta, int last_action) throws Exception {
                    int i = 0;
                    for (; i < 7; i++) {
                              this.attributes[i] = this.attributes[i + 1];
                    }
                    this.attributes[i] = delta;
                    i ++;
                    for (; i < Data.initialiseData().getNumberAttributes() - 1; i++) {
                              this.attributes[i] = this.attributes[i + 1];
                    }
                    this.attributes[i] = last_action;
          }

          @Override
          protected void solve(ProblemDomain problem) {

                    try {
                              //initialise the variable which keeps track of the current objective function value
                              double current_obj_function_value = Double.POSITIVE_INFINITY;

                              //initialise the solution at index 0 in the solution memory array
                              problem.initialiseSolution(0);

                              int num_applied = 0;

                              //the main loop of any hyper-heuristic, which checks if the time limit has been reached
                              while (!hasTimeExpired()) {
                                        ProblemDomain.HeuristicType type;
                                        int heuristic_to_apply;
                                        if (num_applied > 8) {
                                                   heuristic_to_apply = (int) Interpreter.getInstance().Interpret(prog, attributes);

                                        } else {
                                                  heuristic_to_apply = rng.nextInt(Data.initialiseData().getNumberClasses());
                                        }
                                        switch (heuristic_to_apply) {
                                                  case 0:
                                                            type = ProblemDomain.HeuristicType.MUTATION;
                                                            break;
                                                  case 1:
                                                            type = ProblemDomain.HeuristicType.RUIN_RECREATE;
                                                            break;
                                                  case 2:
                                                            type = ProblemDomain.HeuristicType.LOCAL_SEARCH;
                                                            break;
                                                  default:
                                                            type = ProblemDomain.HeuristicType.CROSSOVER;
                                                            break;
                                        }
                                        int[] applicable_heuristics = problem.getHeuristicsOfType(type);
                                        double new_obj_function_value = problem.applyHeuristic(applicable_heuristics[rng.nextInt(applicable_heuristics.length)], 0, 1);
                                        ++num_applied;
                                        double delta = current_obj_function_value - new_obj_function_value;
                                        add(delta, heuristic_to_apply);
                                        problem.copySolution(1, 0);
                                        current_obj_function_value = new_obj_function_value;
                              }
                    } catch (Exception ex) {
                              ex.printStackTrace();
                              System.exit(-1);
                    }

          }

          @Override
          public String toString() {
                    return "Selective Perturbative Hyper Heuristic";

          }

}
