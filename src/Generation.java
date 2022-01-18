package COS700_Project;

public final class Generation {

          private long generation_seed;
          private final Program[] population;
          private final Program best_program,
                    worst_program;
          private final double[] fitnesses;
          private double total_fitness,
                    best_fitness,
                    worst_fitness;
          private int capacity;

          public Generation() throws Exception {
                    generation_seed = Randomness.getInstance().getRandomLong();
                    recycle();  
                    best_program = FlyWeight.getInstance().getProgram();
                    worst_program = FlyWeight.getInstance().getProgram();
                    population = new Program[Parameters.getInstance().getPopulation_size()];
                    fitnesses = new double[Parameters.getInstance().getPopulation_size()];
          }

          public int getNumberNulls() throws Exception {
                    int nulls = 0;
                    for (int i = 0; i < Parameters.getInstance().getPopulation_size(); i++) {
                              if (population[i] == null) {
                                        nulls++;
                              }
                    }
                    return nulls;
          }

          /**
           * @param individual
           * @return
           * @throws Exception
           */
          public synchronized boolean add(Program individual) throws Exception {
                    double fitness = individual.getFitness();
                    if (capacity < population.length) {
                              if (fitness > best_fitness) {
                                        best_fitness = fitness;
                                        best_program.copy(individual);
                              }
                              if (fitness < worst_fitness) {
                                        worst_fitness = fitness;
                                        worst_program.copy(individual);
                              }
                              population[capacity] = individual;
                              fitnesses[capacity] = fitness;
                              total_fitness += fitnesses[capacity];
                              ++capacity;
                              return true;
                    } else {
                              FlyWeight.getInstance().addProgram(individual);
                              return false;
                    }
          }

          /**
           * @return
           */
          public double[] getFitnesses() {
                    return fitnesses;
          }

          /**
           * @return
           */
          public double getBest_fitness() {
                    return best_fitness;
          }

          public double getWorst_fitness() {
                    return worst_fitness;
          }

          /**
           * @return
           */
          public Program getBest_program() {
                    return best_program;
          }

          public Program getWorst_program() {
                    return worst_program;
          }

          /**
           *
           * @return
           */
          public double getAverage_fitness() {
                    return total_fitness > 0 && capacity > 0 ? total_fitness / (1.0 * capacity) : 0;
          }

          /**
           * @return total fitness.
           */
          public double getTotal_fitness() {
                    return total_fitness;
          }

          /**
           * @return whether the generation is empty.
           */
          public boolean isEmpty() {
                    return capacity == 0;
          }

          /**
           * @return get the number of individuals in the generation.
           */
          public int getCapacity() {
                    return capacity;
          }

          /**
           * @param position individual position
           * @return Program.
           * @throws Exception
           */
          public Program getIndividual(int position) throws Exception {
                    if (position >= 0 && position < capacity) {
                              return population[position];
                    } else {
                              throw new Exception("Individual position out of bounds.");
                    }
          }

          public double getFitness(int position) throws Exception {
                    if (position < capacity) {
                              return fitnesses[position];
                    } else {
                              throw new Exception("Fitness position out of bounds.");
                    }
          }

          /**
           * @throws java.lang.Exception
           */
          public void recycle() throws Exception {
                    generation_seed = Randomness.getInstance().getRandomLong();
                    for (int i = 0; i < capacity; i++) {
                              FlyWeight.getInstance().addProgram(population[i]);
                              population[i] = null;
                    }

                    clear();
          }

          /**
           * @throws Exception
           */
          public void clear() throws Exception {
                    generation_seed = Randomness.getInstance().getRandomLong();
                    total_fitness = 0;
                    capacity = 0;
                    best_fitness = Double.NEGATIVE_INFINITY;
                    worst_fitness = Double.POSITIVE_INFINITY;
          }

}
