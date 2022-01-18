package COS700_Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Data {

          private static Data singleton = null;
          private final Random random;
          private final ArrayList<double[]> data_instances;          // going to hold the instances of the  
          private final int number_classes = 4, // mutation, crossover, ruin-create, local-search 
                    number_attributes = 15;
          private int number_instances;

          private Data() throws Exception {
                    data_instances = new ArrayList<>();
                    random = FlyWeight.getInstance().getRandom();
                    random.setSeed(42069);
                    readData();
                    shuffle();
          }

          private synchronized void readData() {
                    try { 
                              for (String line : Files.readAllLines(Paths.get("data.csv"))) {
                                        double[] instance = new double[number_attributes + 1];
                                        int pos = 0;
                                        for (String attribute : line.split(",")) {
                                                  instance[pos++] = Double.parseDouble(attribute);
                                        }
                                        data_instances.add(instance);
                              }
                              number_instances = data_instances.size(); 
                    } catch (IOException ex) {
                              ex.printStackTrace();
                    } catch (Exception ex) {
                              ex.printStackTrace();
                    }
          }

          public void shuffle() {
                    Collections.shuffle(data_instances, random);
          }

          public synchronized List<double[]> getTrainSet() throws Exception { 
                    return data_instances.subList(0, (int) (Parameters.getInstance().getTrain_percent()*data_instances.size()));

          }

          /**
           * @return @throws Exception
           */
          public synchronized static Data initialiseData() throws Exception {
                    if (singleton == null) {
                              singleton = new Data();
                    }
                    return singleton;
          }

          /**
           * @return number of classes
           */
          public int getNumberClasses() {
                    return number_classes;
          }

          /**
           * @return number of attributes
           */
          public int getNumberAttributes() {
                    return number_attributes;
          }

}
