package COS700_Project;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Helper {

          public static void serializeDataOut(Program prog) throws IOException {
                    String fileName = "Program.txt";
                    FileOutputStream fos = new FileOutputStream(fileName);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(prog);
                    oos.close();
          }

          public static Program serializeDataIn() throws FileNotFoundException, IOException, ClassNotFoundException {
                    String fileName = "Program.txt";
                    FileInputStream fin = new FileInputStream(fileName);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    Program iHandler = (Program) ois.readObject();
                    ois.close();
                    return iHandler;
          }

          public static boolean _crossoverMain(Program a, Program b, Random rand) throws Exception {
                    if (false && Meta.debug) {
                              System.out.println("crossing over main");
                    }
                    /*CROSSOVER MAIN TREE*/
                    Program temp = FlyWeight.getInstance().getProgram();
                    temp.copy(b);
                    int[] pos_A, pos_B;
                    // Get all the main primitives of Tree A - making sure it is not the root of tree A 
                    pos_A = getMainPrimitive(a.getMain(), false, rand);
                    // Get all the main primitives of Tree B  

                    if (a.getMain()[pos_A[0]][pos_A[1]] < Meta.MAINS.length) {
                              //if A is a function, then pick any point in B
                              pos_B = getMainPrimitive(b.getMain(), true, rand);
                    } else {
                              //otherwise pick a non-root point in B
                              pos_B = getMainPrimitive(b.getMain(), false, rand);
                    }
                    int a_level = pos_A[0],
                              a_position = pos_A[1],
                              b_level = pos_B[0],
                              b_position = pos_B[1],
                              a_levels = Parameters.getInstance().getMain_max_depth() - a_level,
                              b_levels = Parameters.getInstance().getMain_max_depth() - b_level;

                    /*Copy A to B */
                    for (int level_offset = 0; level_offset < b_levels && level_offset < a_levels; level_offset++) {
                              int a_curr_level = a_level + level_offset,
                                        b_curr_level = b_level + level_offset;
                              int a_start_pos = a_position << level_offset,
                                        b_start_pos = b_position << level_offset;
                              for (int position_offset = 0; position_offset < (1 << level_offset); position_offset++) {
                                        int to_add = a.getMain()[a_curr_level][a_start_pos + position_offset];
                                        if (b_curr_level == (Parameters.getInstance().getMain_max_depth() - 1) || a_curr_level == (Parameters.getInstance().getMain_max_depth() - 1)) {
                                                  if (to_add < Meta.MAINS.length) {
                                                            b.getMain()[b_curr_level][b_start_pos + position_offset] = (byte) (Meta.MAINS.length + rand.nextInt(Data.initialiseData().getNumberClasses() - 0) + 0);
                                                  } else {
                                                            b.getMain()[b_curr_level][b_start_pos + position_offset] = a.getMain()[a_curr_level][a_start_pos + position_offset];
                                                  }
                                        } else {
                                                  b.getMain()[b_curr_level][b_start_pos + position_offset] = a.getMain()[a_curr_level][a_start_pos + position_offset];
                                        }
                                        for (int cl = 0; cl < Parameters.getInstance().getCondition_max_depth(); cl++) {
                                                  System.arraycopy(a.getConditions()[a_curr_level][a_start_pos + position_offset][cl], 0, b.getConditions()[b_curr_level][b_start_pos + position_offset][cl], 0, 1 << cl);
                                        }
                              }
                    }

                    /*Copy B to A*/
                    for (int level_offset = 0; level_offset < b_levels && level_offset < a_levels; level_offset++) {
                              int a_curr_level = a_level + level_offset,
                                        b_curr_level = b_level + level_offset;
                              int a_start_pos = a_position << level_offset,
                                        b_start_pos = b_position << level_offset;
                              for (int position_offset = 0; position_offset < (1 << level_offset); position_offset++) {
                                        int to_add = temp.getMain()[b_curr_level][b_start_pos + position_offset];
                                        if (b_curr_level == (Parameters.getInstance().getMain_max_depth() - 1) || a_curr_level == (Parameters.getInstance().getMain_max_depth() - 1)) {
                                                  if (to_add < Meta.MAINS.length) {
                                                            a.getMain()[a_curr_level][a_start_pos + position_offset] = (byte) (Meta.MAINS.length + rand.nextInt(Data.initialiseData().getNumberClasses() - 0) + 0);
                                                  } else {
                                                            a.getMain()[a_curr_level][a_start_pos + position_offset] = temp.getMain()[b_curr_level][b_start_pos + position_offset];
                                                  }
                                        } else {
                                                  a.getMain()[a_curr_level][a_start_pos + position_offset] = temp.getMain()[b_curr_level][b_start_pos + position_offset];
                                        }
                                        for (int cl = 0; cl < Parameters.getInstance().getCondition_max_depth(); cl++) {
                                                  System.arraycopy(temp.getConditions()[b_curr_level][b_start_pos + position_offset][cl], 0, a.getConditions()[a_curr_level][a_start_pos + position_offset][cl], 0, 1 << cl);
                                        }
                              }
                    }
                    return true;
          }

          public static boolean _crossoverCondition(Program a, Program b, Random rand) throws Exception {
                    if (false && Meta.debug) {
                              System.out.println("crossing over condition");
                    }
                    /*CROSSOVER Condition TREE*/
                    Program temp = FlyWeight.getInstance().getProgram();
                    temp.copy(b);

                    int[] pos_A, pos_B;
                    // Get all the main primitives of Tree A - making sure it is not the root of tree A 
                    pos_A = getMainFunction(a.getMain(), true, rand);

                    // Get all the main primitives of Tree B  
                    int a_main_level = pos_A[0],
                              a_main_position = pos_A[1];

                    pos_B = getMainFunction(b.getMain(), true, rand);
                    int b_main_level = pos_B[0],
                              b_main_position = pos_B[1];

                    pos_A = getConditionPrimitive(a.getConditions()[a_main_level][a_main_position], false, rand);

                    if (a.getConditions()[a_main_level][a_main_position][pos_A[0]][pos_A[1]] < Meta.CONDITIONS.length) {
                              //if A is a function, then pick any point in B
                              pos_B = getConditionPrimitive(b.getConditions()[b_main_level][b_main_position], true, rand);

                    } else {
                              //otherwise pick a non-root point in B
                              pos_B = getConditionPrimitive(b.getConditions()[b_main_level][b_main_position], false, rand);
                    }
                    int a_level = pos_A[0],
                              a_position = pos_A[1],
                              b_level = pos_B[0],
                              b_position = pos_B[1],
                              a_levels = Parameters.getInstance().getCondition_max_depth() - a_level,
                              b_levels = Parameters.getInstance().getCondition_max_depth() - b_level;

                    /*Copy A to B */
                    for (int level_offset = 0; level_offset < b_levels && level_offset < a_levels; level_offset++) {
                              int a_curr_level = a_level + level_offset,
                                        b_curr_level = b_level + level_offset;
                              int a_start_pos = a_position << level_offset,
                                        b_start_pos = b_position << level_offset;
                              for (int position_offset = 0; position_offset < (1 << level_offset); position_offset++) {
                                        int to_add = a.getConditions()[a_main_level][a_main_position][a_curr_level][a_start_pos + position_offset];
                                        if (b_curr_level == (Parameters.getInstance().getCondition_max_depth() - 1) || a_curr_level == (Parameters.getInstance().getCondition_max_depth() - 1)) {
                                                  if (to_add < Meta.CONDITIONS.length) {
                                                            b.getConditions()[b_main_level][b_main_position][b_curr_level][b_start_pos + position_offset] = (byte) (Meta.CONDITIONS.length + rand.nextInt(Data.initialiseData().getNumberAttributes() - 0) + 0);
                                                  } else {
                                                            b.getConditions()[b_main_level][b_main_position][b_curr_level][b_start_pos + position_offset] = a.getConditions()[a_main_level][a_main_position][a_curr_level][a_start_pos + position_offset];
                                                  }
                                        } else {
                                                  b.getConditions()[b_main_level][b_main_position][b_curr_level][b_start_pos + position_offset] = a.getConditions()[a_main_level][a_main_position][a_curr_level][a_start_pos + position_offset];
                                        }
                              }
                    }

                    /*Copy B to A*/
                    for (int level_offset = 0; level_offset < b_levels && level_offset < a_levels; level_offset++) {
                              int a_curr_level = a_level + level_offset,
                                        b_curr_level = b_level + level_offset;
                              int a_start_pos = a_position << level_offset,
                                        b_start_pos = b_position << level_offset;
                              for (int position_offset = 0; position_offset < (1 << level_offset); position_offset++) {
                                        int to_add = temp.getConditions()[b_main_level][b_main_position][b_curr_level][b_start_pos + position_offset];
                                        if (b_curr_level == (Parameters.getInstance().getCondition_max_depth() - 1) || a_curr_level == (Parameters.getInstance().getCondition_max_depth() - 1)) {
                                                  if (to_add < Meta.CONDITIONS.length) {
                                                            a.getConditions()[a_main_level][a_main_position][a_curr_level][a_start_pos + position_offset] = (byte) (Meta.CONDITIONS.length + rand.nextInt(Data.initialiseData().getNumberAttributes() - 0) + 0);
                                                  } else {
                                                            a.getConditions()[a_main_level][a_main_position][a_curr_level][a_start_pos + position_offset] = temp.getConditions()[b_main_level][b_main_position][b_curr_level][b_start_pos + position_offset];
                                                  }
                                        } else {
                                                  a.getConditions()[a_main_level][a_main_position][a_curr_level][a_start_pos + position_offset] = temp.getConditions()[b_main_level][b_main_position][b_curr_level][b_start_pos + position_offset];
                                        }
                              }
                    }
                    return true;
          }

          private static boolean _crossoverConditionOld(Program a, Program b, Random rand) throws Exception {
                    if (Meta.debug) {
                              System.out.println("crossing over condition");
                    }
                    Program temp = FlyWeight.getInstance().getProgram();
                    temp.copy(b);
                    /*CROSSOVER CONDITION SUB-TREES*/
                    //pick a condition sub-branch in tree A and B
                    int[] pos_A = getMainFunction(a.getMain(), true, rand),
                              pos_B = getMainFunction(b.getMain(), true, rand);

                    byte[][] tree_A = a.getConditions()[pos_A[0]][pos_A[1]];
                    byte[][] tree_B = b.getConditions()[pos_B[0]][pos_B[1]];
                    byte[][] tree_temp = b.getConditions()[pos_B[0]][pos_B[1]];

                    //pick a crossover point in A and B 
                    pos_A = getConditionPrimitive(tree_A, true, rand);
                    if (tree_A[pos_A[0]][pos_A[1]] < Meta.CONDITIONS.length) {
                              //tree a is function
                              pos_B = getConditionPrimitive(tree_B, true, rand);
                    } else {
                              pos_B = getConditionPrimitive(tree_B, false, rand);
                    }

                    int a_level = pos_A[0],
                              a_position = pos_A[1],
                              b_level = pos_B[0],
                              b_position = pos_B[1],
                              a_levels = Parameters.getInstance().getCondition_max_depth() - a_level,
                              b_levels = Parameters.getInstance().getCondition_max_depth() - b_level;

                    /*
                    Copy A to B
                     */
                    int level = 0;
                    do {
                              if (b_level + level < Parameters.getInstance().getCondition_max_depth() - 1) {
                                        for (int position = 0; position < (1 << level); position++) {
                                                  tree_B[b_level + level][(b_position << level) + position] = tree_A[a_level + level][(a_position << level) + position];
                                        }
                              } else {
                                        for (int position = 0; position < (1 << level); position++) {
                                                  if (tree_A[a_level + level][(a_position << level) + position] < Meta.CONDITIONS.length) {
                                                            tree_B[b_level + level][b_position + position] = (byte) (Meta.CONDITIONS.length + rand.nextInt(Data.initialiseData().getNumberAttributes() + 0) - 0);
                                                  } else {
                                                            tree_B[b_level + level][(b_position << level) + position] = tree_A[a_level + level][(a_position << level) + position];
                                                  }
                                        }
                              }
                              ++level;
                    } while ((b_level + level) < Parameters.getInstance().getCondition_max_depth() && level < a_levels);

                    /*
                    Copy B to A
                     */
                    level = 0;
                    do {
                              if (a_level + level < Parameters.getInstance().getCondition_max_depth() - 1) {
                                        for (int position = 0; position < (1 << level); position++) {
                                                  tree_A[a_level + level][(a_position << level) + position] = tree_temp[b_level + level][(b_position << level) + position];
                                        }
                              } else {
                                        for (int position = 0; position < (1 << level); position++) {
                                                  if (tree_temp[b_level + level][b_position + position] < Meta.CONDITIONS.length) {
                                                            tree_A[a_level + level][(a_position << level) + position] = (byte) (Meta.CONDITIONS.length + rand.nextInt(Data.initialiseData().getNumberAttributes() + 0) - 0);
                                                  } else {
                                                            tree_A[a_level + level][(a_position << level) + position] = tree_temp[b_level + level][(b_position << level) + position];
                                                  }
                                        }
                              }
                              ++level;
                    } while ((a_level + level) < Parameters.getInstance().getCondition_max_depth() && level < b_levels);
                    return true;

          }

          /**
           * @param prog - program to mutate.
           * @param rand
           * @return
           * @throws Exception
           */
          public static boolean _mutateMain(Program prog, Random rand) throws Exception {
                    int[] position = getMainPrimitive(prog.getMain(), true, rand);
                    Helper._createMain(prog, Parameters.getInstance().getMain_max_depth(), position[0], position[1], true, rand);
                    return true;

          }

          /**
           * @param prog
           * @param rand
           * @return
           * @throws Exception
           */
          public static boolean _mutateCondition(Program prog, Random rand) throws Exception {
                    int[] position = getMainFunction(prog.getMain(), true, rand);
                    byte[][] ptr_condition = prog.getConditions()[position[0]][position[1]];
                    position = getConditionPrimitive(prog.getConditions()[position[0]][position[1]], true, rand);
                    _createCondition(ptr_condition, Parameters.getInstance().getCondition_max_depth(), position[0], position[1], true, rand);
                    return true;

          }

          /**
           * @param prog - program to create.
           * @param max_depth - maximum depth of the program.
           * @param start_level - the starting level of the creation.
           * @param start_pos - the starting position of the creation.
           * @param full - whether this should be full or grow method.
           * @param rand
           * @throws Exception
           */
          public static void _createMain(Program prog, int max_depth, int start_level, int start_pos, boolean full, Random rand) throws Exception {
                    if (max_depth >= 2 && max_depth <= Parameters.getInstance().getMain_max_depth()) {
                              byte[][] main_tree = prog.getMain();
                              for (int level_offset = 0; level_offset < max_depth - start_level; level_offset++) {
                                        for (int position_offset = 0; position_offset < (1 << level_offset); position_offset++) {
                                                  if (Factory.createMainPrimitive(main_tree, start_level + level_offset, (start_pos << level_offset) + position_offset, max_depth, full, rand)) {
                                                            _createCondition(prog.getConditions()[start_level + level_offset][(start_pos << level_offset) + position_offset], Parameters.getInstance().getCondition_max_depth(), 0, 0, full, rand);
                                                  }
                                        }
                              }
                    } else {
                              throw new Exception("Depth of main program to create is invalid.");
                    }
          }

          /**
           * @param max_depth - the maximum depth of the branch.
           * @param start_level - the start level of the condition branch.
           * @param start_pos - the start position of the level.
           * @param full - whether full or grow method should be used.
           * @throws Exception
           */
          public static void _createCondition(byte[][] condition, int max_depth, int start_level, int start_pos, boolean full, Random rand) throws Exception {
                    if (max_depth >= 2 && max_depth <= Parameters.getInstance().getCondition_max_depth()) {
                              for (int depth_offset = 0; depth_offset < Parameters.getInstance().getCondition_max_depth() - (start_level); depth_offset++) {
                                        for (int position_offset = 0; position_offset < (1 << depth_offset); position_offset++) {
                                                  int lvl = (start_level + depth_offset);
                                                  int pos = ((start_pos << depth_offset) + position_offset);
                                                  Factory.createConditionPrimitive(
                                                            condition,
                                                            lvl,
                                                            pos,
                                                            max_depth,
                                                            full,
                                                            rand);
                                        }
                              }
                    } else {
                              throw new Exception("Depth of condition to create is invalid.");
                    }
          }

          /**
           * @param tree
           * @param include_root
           * @param rand
           * @return
           * @throws Exception
           */
          public static int[] getMainFunction(byte[][] tree, boolean include_root, Random rand) throws Exception {
                    if (tree != null) {
                              ArrayList<int[]> points = FlyWeight.getInstance().getArrayListIntArray();
                              Stack<Integer> levels = FlyWeight.getInstance().getStackInteger();
                              Stack<Integer> positions = FlyWeight.getInstance().getStackInteger();
                              levels.add(0);
                              positions.add(0);
                              do {
                                        int level = levels.pop();
                                        int position = positions.pop();
                                        int ch = tree[level][position];
                                        if (ch < Meta.MAINS.length) {
                                                  /*A main function*/
                                                  if (!include_root) {
                                                            if (level > 0) {
                                                                      points.add(new int[]{level, position});
                                                            }
                                                  } else {
                                                            points.add(new int[]{level, position});
                                                  }

                                                  for (int i = 0; i < 2; i++) {
                                                            levels.push(level + 1);
                                                            positions.push((position << 1) + i);
                                                  }
                                        }
                              } while (!levels.empty() && !positions.empty());
                              FlyWeight.getInstance().addStackInteger(levels);
                              FlyWeight.getInstance().addStackInteger(positions);
                              int[] point = points.remove(rand.nextInt(points.size() + 0) - 0);
                              FlyWeight.getInstance().addArrayListIntArray(points);
                              return point;
                    } else {
                              throw new Exception("Can not get points of an empty main tree.");
                    }
          }

          /**
           * @param tree
           * @param include_root
           * @param rand
           * @return
           * @throws Exception
           */
          public static int[] getMainTerminal(byte[][] tree, boolean include_root, Random rand) throws Exception {
                    if (tree != null) {
                              ArrayList<int[]> points = FlyWeight.getInstance().getArrayListIntArray();
                              Stack<Integer> levels = FlyWeight.getInstance().getStackInteger();
                              Stack<Integer> positions = FlyWeight.getInstance().getStackInteger();
                              levels.add(0);
                              positions.add(0);
                              do {
                                        int level = levels.pop();
                                        int position = positions.pop();
                                        int ch = tree[level][position];
                                        if (ch < Meta.MAINS.length) {
                                                  /*A main function*/

                                                  for (int i = 0; i < 2; i++) {
                                                            levels.push(level + 1);
                                                            positions.push((position << 1) + i);
                                                  }
                                        } else {
                                                  if (!include_root) {
                                                            if (level > 0) {
                                                                      points.add(new int[]{level, position});
                                                            }
                                                  } else {
                                                            points.add(new int[]{level, position});
                                                  }
                                        }
                              } while (!levels.empty() && !positions.empty());
                              FlyWeight.getInstance().addStackInteger(levels);
                              FlyWeight.getInstance().addStackInteger(positions);
                              int[] point = points.remove(rand.nextInt(points.size() + 0) - 0);
                              FlyWeight.getInstance().addArrayListIntArray(points);
                              return point;
                    } else {
                              throw new Exception("Can not get points of an empty main tree.");
                    }
          }

          /**
           * @param tree
           * @param include_root
           * @param rand
           * @return
           * @throws Exception
           */
          public static int[] getMainPrimitive(byte[][] tree, boolean include_root, Random rand) throws Exception {
                    if (tree != null) {
                              ArrayList<int[]> points = FlyWeight.getInstance().getArrayListIntArray();
                              Stack<Integer> levels = FlyWeight.getInstance().getStackInteger();
                              Stack<Integer> positions = FlyWeight.getInstance().getStackInteger();
                              levels.add(0);
                              positions.add(0);
                              do {
                                        int level = levels.pop();
                                        int position = positions.pop();
                                        int ch = tree[level][position];
                                        if (ch < Meta.MAINS.length) {
                                                  /*A main function*/
                                                  if (!include_root) {
                                                            if (level > 0) {
                                                                      points.add(new int[]{level, position});
                                                            }
                                                  } else {
                                                            points.add(new int[]{level, position});
                                                  }

                                                  for (int i = 0; i < 2; i++) {
                                                            levels.push(level + 1);
                                                            positions.push((position << 1) + i);
                                                  }
                                        } else {
                                                  if (!include_root) {
                                                            if (level > 0) {
                                                                      points.add(new int[]{level, position});
                                                            }
                                                  } else {
                                                            points.add(new int[]{level, position});
                                                  }
                                        }
                              } while (!levels.empty() && !positions.empty());
                              FlyWeight.getInstance().addStackInteger(levels);
                              FlyWeight.getInstance().addStackInteger(positions);
                              int[] point = points.remove(rand.nextInt(points.size() + 0) - 0);
                              FlyWeight.getInstance().addArrayListIntArray(points);
                              return point;
                    } else {
                              throw new Exception("Can not get points of an empty main tree.");
                    }
          }

          /**
           * @param tree
           * @param include_root
           * @param rand
           * @return
           * @throws Exception
           */
          public static int[] getConditionPrimitive(byte[][] tree, boolean include_root, Random rand) throws Exception {
                    if (tree != null) {
                              ArrayList<int[]> points = FlyWeight.getInstance().getArrayListIntArray();
                              Stack<Integer> levels = FlyWeight.getInstance().getStackInteger();
                              Stack<Integer> positions = FlyWeight.getInstance().getStackInteger();
                              levels.add(0);
                              positions.add(0);
                              do {
                                        int level = levels.pop();
                                        int position = positions.pop();
                                        int ch = tree[level][position];
                                        if (ch < Meta.CONDITIONS.length) {
                                                  if (!include_root) {
                                                            if (level > 0) {
                                                                      points.add(new int[]{level, position});
                                                            }
                                                  } else {
                                                            points.add(new int[]{level, position});
                                                  }

                                                  for (int i = 0; i < 2; i++) {
                                                            levels.push(level + 1);
                                                            positions.push((position << 1) + i);
                                                  }
                                        } else {
                                                  if (!include_root) {
                                                            if (level > 0) {
                                                                      points.add(new int[]{level, position});
                                                            }
                                                  } else {
                                                            points.add(new int[]{level, position});
                                                  }
                                        }
                              } while (!levels.empty() && !positions.empty());
                              FlyWeight.getInstance().addStackInteger(levels);
                              FlyWeight.getInstance().addStackInteger(positions);
                              int[] point;
                              synchronized (points) {
                                        point = points.remove(rand.nextInt(points.size() + 0) - 0);
                              }

                              FlyWeight.getInstance().addArrayListIntArray(points);
                              return point;
                    } else {
                              throw new Exception("Can not get points of an empty tree.");
                    }
          }

          /**
           * @param tree
           * @param include_root
           * @param rand
           * @return
           * @throws Exception
           */
          public static int[] getConditionFunction(byte[][] tree, boolean include_root, Random rand) throws Exception {
                    if (tree != null) {
                              ArrayList<int[]> points = FlyWeight.getInstance().getArrayListIntArray();
                              Stack<Integer> levels = FlyWeight.getInstance().getStackInteger();
                              Stack<Integer> positions = FlyWeight.getInstance().getStackInteger();
                              levels.add(0);
                              positions.add(0);
                              do {
                                        int level = levels.pop();
                                        int position = positions.pop();
                                        int ch = tree[level][position];
                                        if (ch < Meta.CONDITIONS.length) {
                                                  if (!include_root) {
                                                            if (level > 0) {
                                                                      points.add(new int[]{level, position});
                                                            }
                                                  } else {
                                                            points.add(new int[]{level, position});
                                                  }

                                                  for (int i = 0; i < 2; i++) {
                                                            levels.push(level + 1);
                                                            positions.push((position << 1) + i);
                                                  }
                                        }
                              } while (!levels.empty() && !positions.empty());
                              FlyWeight.getInstance().addStackInteger(levels);
                              FlyWeight.getInstance().addStackInteger(positions);
                              int[] point = points.remove(rand.nextInt(points.size() - 0) + 0);
                              FlyWeight.getInstance().addArrayListIntArray(points);
                              return point;
                    } else {
                              throw new Exception("Can not get points of an empty tree.");
                    }
          }

          /**
           * @param tree
           * @param include_root
           * @param rand
           * @return
           * @throws Exception
           */
          public static int[] getConditionTerminal(byte[][] tree, boolean include_root, Random rand) throws Exception {
                    if (tree != null) {
                              ArrayList<int[]> points = FlyWeight.getInstance().getArrayListIntArray();
                              Stack<Integer> levels = FlyWeight.getInstance().getStackInteger();
                              Stack<Integer> positions = FlyWeight.getInstance().getStackInteger();
                              levels.add(0);
                              positions.add(0);
                              do {
                                        int level = levels.pop();
                                        int position = positions.pop();
                                        byte ch = tree[level][position];
                                        if (ch < Meta.CONDITIONS[Meta.CONDITIONS.length - 1]) {
                                                  for (int i = 0; i < 2; i++) {
                                                            levels.push(level + 1);
                                                            positions.push((position << 1) + i);
                                                  }
                                        } else {
                                                  if (!include_root) {
                                                            if (level > 0) {
                                                                      points.add(new int[]{level, position});
                                                            }
                                                  } else {
                                                            points.add(new int[]{level, position});
                                                  }
                                        }
                              } while (!levels.empty() && !positions.empty());
                              FlyWeight.getInstance().addStackInteger(levels);
                              FlyWeight.getInstance().addStackInteger(positions);
                              int[] point = points.remove(rand.nextInt(points.size() - 0) + 0);
                              FlyWeight.getInstance().addArrayListIntArray(points);
                              return point;
                    } else {
                              throw new Exception("Can not get points of an empty tree.");
                    }
          }

          public static String toString(Program prog) throws Exception {
                    return toStringMain("", prog, prog.getMain(), 0, 0);
          }

          private static String toStringMain(String level, Program prog, byte[][] main, int row, int pos) throws Exception {
                    if (row >= 0 && row < Parameters.getInstance().getMain_max_depth()) {
                              String line = "";
                              byte ch = main[row][pos];
                              if (ch < Meta.MAINS.length) {
                                        line += level + "IF" + toStringCondition(prog, row, pos, 0, 0) + "{";
                                        line += "\n";
                                        line += level + toStringMain(level + "  ", prog, main, row + 1, (pos << 1) + 0);
                                        line += "\n";
                                        line += level + "}ELSE { ";
                                        line += "\n";
                                        line += level + toStringMain(level + "  ", prog, main, row + 1, (pos << 1) + 1);
                                        line += "\n";
                                        line += level + "}";
                                        line += "\n";
                              } else if (ch < Meta.MAINS.length + Data.initialiseData().getNumberClasses()) {
                                        line = level + (ch - Meta.MAINS.length);
                              }
                              return line;
                    } else {
                              throw new Exception("Main row out of bounds.");
                    }
          }

          private static String toStringCondition(Program prog, int m_row, int m_pos, int row, int pos) throws Exception {
                    if (row >= 0 && row < Parameters.getInstance().getCondition_max_depth()) {
                              String line = "(";
                              byte ch = prog.getConditions()[m_row][m_pos][row][pos];
                              if (ch < Meta.CONDITIONS.length) {
                                        switch (ch) {
                                                  case Meta.GREATER_THAN:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " > " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.LESS_THAN:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " < " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.GREATER_OR_EQUAL:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " >= " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.LESS_OR_EQUAL:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " <= " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.EQUAL:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " == " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.NOT_EQUAL:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " != " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.ADDITION:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " + " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.SUBTRACTION:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " - " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.DIVISION:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " / " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.MULTIPLICATION:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " * " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.BITWISE_AND:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " & " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.BITWISE_OR:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " | " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.BITWISE_XOR:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " ^ " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.LOGICAL_AND:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " && " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                                  case Meta.LOGICAL_OR:
                                                            line += toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 0) + " || " + toStringCondition(prog, m_row, m_pos, row + 1, (pos << 1) + 1);
                                                            break;
                                        }
                              } else if (ch < Meta.CONDITIONS.length + Data.initialiseData().getNumberAttributes()) {
                                        int attribute = ch - Meta.CONDITIONS.length;
                                        line += attribute;
                              }
                              return line + ")";
                    } else {
                              throw new Exception("Condition row out of bounds.");
                    }
          }

}
