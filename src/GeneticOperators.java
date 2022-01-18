package COS700_Project;

import java.util.Random;

public class GeneticOperators {

    /**
     * @param prog - program to full
     * @param depth - depth to grow to
           * @param seed
     * @throws Exception
     */
    public static void grow(Program prog, int depth, long seed) throws Exception {
        if (prog != null) { 
            Random rand = FlyWeight.getInstance().getRandom();
            rand.setSeed(seed);
            Helper._createMain(prog, depth, 0, 0, false, rand);
            FlyWeight.getInstance().addRandom(rand);
        } else {
            throw new Exception("Cannot grow null program.");
        }
    }

    /**
     * @param prog - program to full
     * @param depth - depth to grow to
           * @param seed
     * @throws Exception
     */
    public static void full(Program prog, int depth, long seed) throws Exception {
        if (prog != null) {
            Random rand = FlyWeight.getInstance().getRandom();
            rand.setSeed(seed);
            Helper._createMain(prog, depth, 0, 0, true, rand);
            FlyWeight.getInstance().addRandom(rand);
        } else {
            throw new Exception("Cannot full null program.");
        }
    }

    /**
     * @param prog - program to mutate.
           * @param seed
     * @throws Exception
     */
    public static void mutate(Program prog, long seed) throws Exception {
        if (prog != null) {
            Random rand = FlyWeight.getInstance().getRandom();
            rand.setSeed(seed);
            if (rand.nextBoolean() || !Helper._mutateCondition(prog, rand)) {
                /*MUTATE MAIN TREE*/
                Helper._mutateMain(prog, rand);
            }
            FlyWeight.getInstance().addRandom(rand);
        } else {
            throw new Exception("Cannot mutate null program.");
        }
    }

    /**
     * @param prog
           * @param seed
     * @throws Exception
     */
    public static void hoist(Program prog, long seed) throws Exception {
        if (prog != null) {
            Random rand = FlyWeight.getInstance().getRandom();
            rand.setSeed(seed);
            int[] pos = Helper.getMainFunction(prog.getMain(), true, rand);
            FlyWeight.getInstance().addRandom(rand);
            int level = pos[0];
            int position = pos[1];
            byte[][] main = prog.getMain();
            byte[][][][] cond = prog.getConditions();
            for (int hoist_level = 0; hoist_level < (Parameters.getInstance().getMain_max_depth() - level) ; hoist_level++) {
                int number_positions = 1 << hoist_level;
                int start_position = position <<  hoist_level;
                for (int hoist_position = 0; hoist_position < number_positions; hoist_position++) {
                    main[hoist_level][hoist_position] = main[level + hoist_level][start_position+hoist_position] ;
                    
                    for (int cd = 0; cd < Parameters.getInstance().getCondition_max_depth();cd++) {
                        System.arraycopy(cond[level + hoist_level][start_position+hoist_position][cd], 0, cond[hoist_level][hoist_position][cd], 0, 1 << cd);
                    } 
                }
            } 
        } else {
            throw new Exception("Cannot hoist null program.");
        }
    }

    /**
     * @param a - parent A.
     * @param b - parent B.
     * @param seed
     * @throws Exception
     */
    public static void crossover(Program a, Program b, long seed) throws Exception {
        if (a != null && b != null) {
            Random rand = FlyWeight.getInstance().getRandom();
            rand.setSeed(seed);
           if (rand.nextBoolean() || !Helper._crossoverCondition(a, b, rand)) {
                Helper._crossoverMain(a, b, rand);
            } 
            FlyWeight.getInstance().addRandom(rand);
        } else {
            throw new Exception("Cannot crossover null programs.");
        }
    }

    /**
     * @param prog - program to edit.
     * @param seed
     * @throws Exception
     */
    private static void edit(Program prog, long seed) throws Exception {
        if (prog != null) {
            Random rand = FlyWeight.getInstance().getRandom();
            rand.setSeed(seed);
            byte[][] tree = null, tree_copy = null;
            int[] position = null;
            int depth;
            Program copy = FlyWeight.getInstance().getProgram();
            copy.copy(prog);
            if (rand.nextBoolean()) {
                /*Edit Main Tree*/
                position = Helper.getMainFunction(prog.getMain(), true, rand);
                tree = prog.getMain();
                tree_copy = copy.getMain();
                depth = Parameters.getInstance().getMain_max_depth();
            } else {
                /*Edit Condition Sub-tree*/
                position = Helper.getMainFunction(prog.getMain(), true, rand);
                tree = prog.getConditions()[position[0]][position[1]];
                tree_copy = copy.getConditions()[position[0]][position[1]];
                position = Helper.getConditionFunction(tree, true,rand);
                depth = Parameters.getInstance().getCondition_max_depth();
            }  
            
            int start_level = position[0],
                    left_start_position = position[1],
                    right_start_position = position[1]+1; 
            for (int level_offset = 0; level_offset < (depth - start_level); level_offset++) {
                for (int position_offset = 0; position_offset < right_start_position; position_offset++) {
                    tree[start_level + level_offset][left_start_position+position_offset] = tree_copy[start_level + level_offset][right_start_position+position_offset];
                    tree[start_level + level_offset][right_start_position+position_offset] = tree_copy[start_level + level_offset][left_start_position+position_offset] ;
                }
                left_start_position = left_start_position << 1;
                right_start_position = right_start_position << 1;
            }  
            FlyWeight.getInstance().addProgram(copy);
            FlyWeight.getInstance().addRandom(rand);
        } else {
            throw new Exception("Cannot edit null program.");
        }
    }

}
