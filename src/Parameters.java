package COS700_Project;

public class Parameters {

          /*
          =================================
          Genetic Program parameters
          =================================
           */
          private  int max_generation = 50,
                    main_max_depth = 10,
                    condition_max_depth = 5,
                    population_size = 200,
                    tournament_size = 2;

          private  double 
                    train_percent = 0.7,
                    crossover_chance = 50,
                    mutation_chance = 30,
                    hoist_chance = 10;
 
          private static Parameters singleton = null;         //

          /**
           * @param mg
           * @param tp
           * @param mmd
           * @param cmd
           * @param ps
           * @param ts
           * @param cc
           * @param mc
           * @param hc
           */
          private Parameters( int mg, double tp, int mmd, int cmd, int ps, int ts, double cc, double mc, double hc) {
                    this.max_generation = mg;
                    this.train_percent = tp;
                    this.main_max_depth = mmd;
                    this.condition_max_depth = cmd;
                    this.population_size = ps;
                    this.tournament_size = ts;
                    this.crossover_chance = cc;
                    this.mutation_chance = mc;
                    this.hoist_chance = hc;
          }

          /**
           * @param mg
           * @param tp
           * @param mmd - main max depth
           * @param cmd - condition max depth
           * @param ps - population size
           * @param ts - tournament size
           * @param cc - crossover chance
           * @param mc - mutation chance
           * @param hc - hoist chance
           * @return Parameters singleton.
           * @throws Exception
           */
          public static Parameters setParameters(int mg, double tp, int mmd, int cmd, int ps, int ts, double cc, double mc, double hc) throws Exception {
                    if (mmd > 1 && cmd > 1 && ps > 0 && ts > 0 && ps >= ts) {
                              if (singleton != null) {
                                        singleton.max_generation = mg;
                                        singleton.train_percent = tp;
                                        singleton.main_max_depth = mmd;
                                        singleton.condition_max_depth = cmd;
                                        singleton.population_size = ps;
                                        singleton.tournament_size = ts;
                                        singleton.crossover_chance = cc;
                                        singleton.mutation_chance = mc;
                                        singleton.hoist_chance = hc;
                                        //singleton.edit_chance           = ec; 
                              } else {
                                        singleton = new Parameters( mg, tp, mmd, cmd, ps, ts, cc, mc, hc/*,ec*/);
                              }
                              return singleton;
                    } else {
                              throw new Exception("Invalid parameter values.");
                    }
          }
 
          /**
           * @return Parameters singleton.
           * @throws Exception
           */
          public static Parameters getInstance() throws Exception {
                    if (singleton != null) {
                              return singleton;
                    } else {
                              throw new Exception("Parameters have not been set.");
                    }
          }
 
          
          /**
           * @return max generation
           */
          public int getMax_generation() {
                    return max_generation;
          }

          public double getTrain_percent() {
                    return train_percent;
          }

           

          /**
           * @return tournament size
           */
          public int getTournament_size() {
                    return tournament_size;
          }

          /**
           * @return crossover chance.
           */
          public double getCrossover_chance() {
                    return crossover_chance;
          }

          /**
           * @return mutation chance.
           */
          public double getMutation_chance() {
                    return mutation_chance;
          }

          /**
           * @return hoist chance.
           */
          public double getHoist_chance() {
                    return hoist_chance;
          }

          /**
           * @return Population size
           */
          public int getPopulation_size() {
                    return population_size;
          }

          /**
           * @return Condition Max Depth
           */
          public int getCondition_max_depth() {
                    return condition_max_depth;
          }

          /**
           * @return Main Max Depth
           */
          public int getMain_max_depth() {
                    return main_max_depth;
          }

          @Override
          public String toString() { 
                    return  this.max_generation + "," + 
                              this.main_max_depth + "," +
                              this.condition_max_depth  + "," +
                              this.population_size + "," + 
                              this.tournament_size + "," + 
                              this.crossover_chance + "," +
                              this.mutation_chance+ "," +
                              this.hoist_chance + "";
          }
          
          
          
}
