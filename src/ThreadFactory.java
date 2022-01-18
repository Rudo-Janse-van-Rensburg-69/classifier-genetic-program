/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COS700_Project;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import BinPacking.BinPacking;
import FlowShop.FlowShop;
import PersonnelScheduling.PersonnelScheduling;
import SAT.SAT;
import VRP.VRP;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import travelingSalesmanProblem.TSP;

/**
 *
 * @author rudo
 */
public class ThreadFactory {

          private static ThreadFactory factory = null;

          private ThreadFactory() {
          }

          public static ThreadFactory instance() {
                    if (factory == null) {
                              factory = new ThreadFactory();
                    }
                    return factory;
          }

          public F1Thread getF1Thread(CountDownLatch latch, Program prog) {
                    return new F1Thread(latch, prog);
          }

          public GeneticOperatorThread getGeneticOperatorThread(CountDownLatch latch, Program[] parents, char operation, int max_depth, long seed) throws Exception {
                    return new GeneticOperatorThread(latch, parents, operation, max_depth, seed);
          }

          public GeneticOperatorThread getGeneticOperatorThread(CountDownLatch latch, Program[] parents, char operation, long seed) throws Exception {
                    return new GeneticOperatorThread(latch, parents, operation, seed);
          }

          public RunnerThread getRunnerThread(CountDownLatch latch, Program prog, long seed, int domain, int instance) throws Exception {
                    return new RunnerThread(latch, prog, domain, instance, 60000, seed);
          }

          public RunnerThread getRunnerThread(CountDownLatch latch,
                    Program prog,
                    int domain,
                    int instance,
                    long time_limit,
                    long seed) throws Exception {
                    return new RunnerThread(latch, prog, domain, instance, time_limit, seed);
          }

          public CompetitionRunnerThread getCompetitionRunnerThread(CountDownLatch latch,
                    Program prog,
                    int domain,
                    int instance,
                    long time_limit,
                    long[] seeds,
                    int runs) throws Exception {
                    return new CompetitionRunnerThread(latch, prog, domain, instance, time_limit, seeds, runs);
          }

}

class F1Thread extends Thread {

          private double f1;
          Program prog;
          private final CountDownLatch latch;

          public F1Thread(CountDownLatch latch, Program prog) {
                    this.prog = prog;
                    this.latch = latch;
          }

          @Override
          public synchronized void run() {
                    try {
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
                              precision = precision / Data.initialiseData().getNumberClasses();
                              recall = recall / Data.initialiseData().getNumberClasses();
                              f1 = (2.0 * (precision * recall) / (precision + recall));
                              latch.countDown();
                    } catch (Exception e) {
                              e.printStackTrace();
                    }

          }

          public double getF1() {
                    return f1 >= 0 ? f1 : 0;
          }

}

class CompetitionRunnerThread extends Thread {

          private static HyperHeuristic loadHyperHeuristic(Program prog, long timeLimit, long seed) throws Exception {
                    HyperHeuristic h;
                    h = new SelectivePeturbativeHyperHeuristic(prog, seed);
                    h.setTimeLimit(timeLimit);
                    return h;
          }

          private static ProblemDomain loadProblemDomain(int problem, long instanceseed) {
                    ProblemDomain p;
                    switch (problem) {
                              case 0:
                                        p = new SAT(instanceseed);
                                        break;
                              case 1:
                                        p = new BinPacking(instanceseed);
                                        break;
                              case 2:
                                        p = new PersonnelScheduling(instanceseed);
                                        break;
                              case 3:
                                        p = new FlowShop(instanceseed);
                                        break;
                              case 4:
                                        p = new TSP(instanceseed);
                                        break;
                              case 5:
                                        p = new VRP(instanceseed);
                                        break;
                              default:
                                        System.err.println("there is no problem domain with this index");
                                        System.exit(0);
                                        p = null;
                    }
                    return p;
          }

          private final CountDownLatch latch;
          private final List<String> printer_content;
          private final StringBuilder buffer_printer_content;
          private final String resultsfolder;
          public final int problem, instance;
          private final int runs;
          private final long[] seeds;
          private final Program prog;
          private final long time;
          private final Random rng;

          @Override
          public void run() {
                    printer_content.add("PROBLEM DOMAIN " + resultsfolder + "\n");
                    printer_content.add("  instance " + instance + " " + "\n");
                    CountDownLatch runner_latch = new CountDownLatch(this.runs);
                    ExecutorService runner_service = Executors.newFixedThreadPool(this.runs);
                    List<RunnerThread> threads = new ArrayList<>();
                    try {
                              for (int run = 0; run < this.runs; run++) {
                                        long instanceseed = seeds[run];
                                        RunnerThread thread = ThreadFactory.instance().getRunnerThread(runner_latch, this.prog, instanceseed, problem, instance);
                                        runner_service.execute(thread);
                                        threads.add(thread);
                              }
                              runner_latch.await();
                              runner_service.shutdown();
                              for (int run = 0; run < this.runs; run++) {
                                        printer_content.add("    RUN " + run);
                                        printer_content.add("\t" + threads.get(run).getRunner().getBestSolutionValue()
                                                  + "\t" + (threads.get(run).getRunner().getElapsedTime() / 1000.0)
                                                  + "\t" + (threads.get(run).getRunner().getRunTime()) / 1000.0
                                                  + "\t" + threads.get(run).getRunner().getTotalHeuristicCalls() + "\n");
                                        buffer_printer_content.append(threads.get(run).getRunner().getBestSolutionValue()).append(" ");
                                        StringBuilder buffer_printer_content_1 = new StringBuilder();
                                        double[] u = threads.get(run).getRunner().getFitnessTrace();
                                        for (double y : u) {
                                                  buffer_printer_content_1.append(y).append(" ");
                                        }
                                        buffer_printer_content_1.append("\n");
                                        try {
                                                  Path pathtofile = Paths.get("./" + Parameters.getInstance().toString() + "/results/" + resultsfolder + "/time" + instance + ".txt");
                                                  if (!Files.exists(pathtofile)) {
                                                            Files.createDirectories(pathtofile.getParent());
                                                            Files.createFile(pathtofile);
                                                  }
                                                  Files.write(pathtofile, printer_content.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                                        } catch (Exception e) {
                                                  e.printStackTrace();
                                        }
                                        buffer_printer_content.append("\n");
                              }
                              try {
                                        Path pathtofile = Paths.get("./" + Parameters.getInstance().toString() + "/results/" + problem + "-" + instance + ".txt");
                                        if (!Files.exists(pathtofile)) {
                                                  Files.createDirectories(pathtofile.getParent());
                                                  Files.createFile(pathtofile);
                                        }
                                        Files.write(pathtofile, printer_content.toString().getBytes(), StandardOpenOption.CREATE);
                                        pathtofile = Paths.get("./" + Parameters.getInstance().toString() + "/results/" + resultsfolder + "/instance" + instance + ".txt");
                                        if (!Files.exists(pathtofile)) {
                                                  Files.createDirectories(pathtofile.getParent());
                                                  Files.createFile(pathtofile);
                                        }
                                        Files.write(pathtofile, buffer_printer_content.toString().getBytes(), StandardOpenOption.CREATE);

                              } catch (Exception e) {
                                        e.printStackTrace();
                              }
                              latch.countDown();
                    } catch (Exception e) {
                              e.printStackTrace();
                    }
          }

          public CompetitionRunnerThread(
                    CountDownLatch latch,
                    Program prog,
                    int domain,
                    int instance,
                    long time_limit,
                    long[] seeds,
                    int runs
          ) throws Exception {
                    this.rng = new Random();
                    this.time = time_limit;
                    this.runs = runs;
                    this.seeds = seeds;
                    this.prog = prog;
                    this.latch = latch;
                    this.instance = instance;
                    this.problem = domain;
                    this.printer_content = new ArrayList<>();
                    this.buffer_printer_content = new StringBuilder();
                    switch (domain) {
                              case 0:
                                        resultsfolder = "SAT";
                                        break;
                              case 1:
                                        resultsfolder = "BinPacking";
                                        break;
                              case 2:
                                        resultsfolder = "PersonnelScheduling";
                                        break;
                              case 3:
                                        resultsfolder = "FlowShop";
                                        break;
                              case 4:
                                        resultsfolder = "TSP";
                                        break;
                              case 5:
                                        resultsfolder = "VRP";
                                        break;
                              default:
                                        resultsfolder = "not worked";
                                        System.err.println("wrong input for the problem domain");
                                        System.exit(-1);
                    }
          }

          @Override
          public String toString() {
                    return "SPHH";
          }
}

class RunnerThread extends Thread {

          private final CountDownLatch latch;
          private final Runner runner;
          public final int domain;
          public final int instance;

          @Override
          public void run() {
                    runner.execute();
                    StringBuilder content = new StringBuilder();
                    content.append("problem, instance, best_solution_value, elapsed_time, run_time, total_heuristic_call \n");
                    String problem = "";
                    switch (this.domain) {
                              case 0:
                                        problem = "SAT";
                                        break;
                              case 1:
                                        problem = "BinPacking";
                                        break;
                              case 2:
                                        problem = "PersonnelScheduling";
                                        break;
                              case 3:
                                        problem = "FlowShop";
                                        break;
                              case 4:
                                        problem = "TSP";
                                        break;
                              case 5:
                                        problem = "VRP";
                                        break;
                              default:
                                        System.exit(-1);
                    }
                    content
                              .append(problem).append(" ,")
                              .append(this.instance).append(" ,")
                              .append(this.runner.getBestSolutionValue()).append(" ,")
                              .append(this.runner.getElapsedTime() / 1000.0).append(" ,")
                              .append(this.runner.getRunTime() / 1000.0).append(" ,")
                              .append(this.runner.getTotalHeuristicCalls()).append(" \n");
                    synchronized(this){
                              try {
                                        Path pathtofile = Paths.get("./" +problem+"-"+this.instance+".csv");
                                        Files.write(pathtofile, content.toString().getBytes(), StandardOpenOption.CREATE);
                              } catch (Exception ex) {
                                        ex.printStackTrace();
                              }
                    }
                    latch.countDown();
          }

          public Runner getRunner() {
                    return runner;
          }

          public RunnerThread(CountDownLatch latch, Program prog, int domain, int instance, long time_limit, long seed) throws Exception {
                    this.runner = new Runner(prog, domain, instance, time_limit, seed);
                    this.latch = latch;
                    this.domain = domain;
                    this.instance = instance;
          }

          @Override
          public String toString() {
                    return "SPHH";
          }
}

class GeneticOperatorThread extends Thread {

          private final Program[] parents;
          private final char operation;
          private final int max_depth;
          private final long seed;
          private final CountDownLatch latch;

          public GeneticOperatorThread(CountDownLatch latch, Program[] parents, char operation, int max_depth, long seed) throws Exception {
                    this.parents = new Program[parents.length];
                    setParents(parents);
                    this.operation = operation;
                    this.max_depth = max_depth;
                    this.seed = seed;
                    this.latch = latch;
          }

          public GeneticOperatorThread(CountDownLatch latch, Program[] parents, char operation, long seed) throws Exception {
                    this.parents = new Program[parents.length];
                    setParents(parents);
                    this.operation = operation;
                    this.max_depth = Parameters.getInstance().getMain_max_depth();
                    this.seed = seed;
                    this.latch = latch;
          }

          @Override
          public void run() {
                    try {
                              switch (operation) {
                                        case Meta.MUTATE:
                                                  GeneticOperators.mutate(parents[0], seed);
                                                  parents[0].setFitness(Fitness.getInstance().evaluate(parents[0]));
                                                  latch.countDown();
                                                  break;
                                        case Meta.CROSSOVER:
                                                  GeneticOperators.crossover(parents[0], parents[1], seed);
                                                  parents[0].setFitness(Fitness.getInstance().evaluate(parents[0]));
                                                  parents[1].setFitness(Fitness.getInstance().evaluate(parents[1]));
                                                  latch.countDown();
                                                  break;
                                        case Meta.HOIST:
                                                  GeneticOperators.hoist(parents[0], seed);
                                                  parents[0].setFitness(Fitness.getInstance().evaluate(parents[0]));
                                                  latch.countDown();
                                                  break;
                                        case Meta.GROW:
                                                  GeneticOperators.grow(parents[0], max_depth, seed);
                                                  parents[0].setFitness(Fitness.getInstance().evaluate(parents[0]));
                                                  latch.countDown();
                                                  break;
                                        case Meta.FULL:
                                                  GeneticOperators.full(parents[0], max_depth, seed);
                                                  parents[0].setFitness(Fitness.getInstance().evaluate(parents[0]));
                                                  latch.countDown();
                                                  break;
                              }
                    } catch (Exception e) {
                              e.printStackTrace();
                    }
          }

          public Program[] getParents() {
                    return parents;
          }

          public char getOperation() {
                    return operation;
          }

          private void setParents(Program[] parents) throws Exception {
                    for (int i = 0; i < parents.length; i++) {
                              Program prog = FlyWeight.getInstance().getProgram();
                              prog.copy(parents[i]);
                              this.parents[i] = prog;
                    }
          }

}
