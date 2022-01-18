package COS700_Project;

import java.util.Stack;

public class Interpreter {

    private static Interpreter singleton = null;

    private Interpreter() {
    }

    public static Interpreter getInstance() {
        if (singleton == null) {
            singleton = new Interpreter();
        }
        return singleton;
    }

    /**
     * @param p
     * @param instance
     * @return
     * @throws Exception
     */
    public synchronized  double Interpret(Program p, double[] instance) throws Exception {
        byte result = 0;
        if (p != null) {
            Stack<Integer> row = FlyWeight.getInstance().getStackInteger();
            Stack<Integer> pos = FlyWeight.getInstance().getStackInteger();
            row.push(0);
            pos.push(0);
            double value = _interpretMain(p, row, pos, instance);
            FlyWeight.getInstance().addStackInteger(pos);
            FlyWeight.getInstance().addStackInteger(row);
            return value;
        } else {
            throw new Exception("Cannot interpret a null program.");
        }
    }

    /**
     * *
     * @param prog - a program to interpret.
     * @param row - a stack for maintaining row position.
     * @param pos - a stack for maintaining col position.
     * @return - an int representing a class.
     * @throws Exception
     */
    private double _interpretMain(Program prog, Stack<Integer> row, Stack<Integer> pos, double[] instance) throws Exception {
        if (row != null && pos != null && !row.empty() && !pos.empty()) {
            int r, p;
            do {
                r = row.pop();
                if (r <= Parameters.getInstance().getMain_max_depth()) {
                    /*Still not at the leaf of a main sub-branch*/
                    p = pos.pop();
                    int ch = prog.getMain()[r][p];
                    if (ch  < Meta.MAINS.length) { 
                        if (_interpretCondition(
                                prog.getConditions()[r][p],
                                0,
                                0,
                                instance
                        ) != 0) {
                            /*Condition Evaluated True*/
                            row.push(r + 1);
                            pos.push(p << 1);
                        } else {
                            /*Condition Evaluated False*/
                            row.push(r + 1);
                            pos.push((p << 1) + 1);
                        }
                    } else {
                        return ch - Meta.MAINS.length;
                    }
                } else {
                    throw new Exception("Main program did not terminate within the maximum depth.");
                }
            } while (!row.empty() && !pos.empty());
            throw new Exception("Program completed without producing a result.");
        } else {
            throw new Exception("Cannot interpret empty main program.");
        }

    }

    private double _interpretCondition(byte[][] cond, int row, int pos, double[] instance) throws Exception {
        if (cond != null) {
            if (row < Parameters.getInstance().getCondition_max_depth() && pos < (1 << row)) {
                int ch = cond[row][pos];
                if (ch < Meta.CONDITIONS.length) {
                    switch (ch) {
                        case Meta.GREATER_THAN:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) > _interpretCondition(cond, row + 1, (pos << 1) + 1, instance) ? 1 : 0;
                        case Meta.LESS_THAN:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) < _interpretCondition(cond, row + 1, (pos << 1) + 1, instance) ? 1 : 0;
                        case Meta.GREATER_OR_EQUAL:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) >= _interpretCondition(cond, row + 1, (pos << 1) + 1, instance) ? 1 : 0;
                        case Meta.LESS_OR_EQUAL:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) <= _interpretCondition(cond, row + 1, (pos << 1) + 1, instance) ? 1 : 0;
                        case Meta.EQUAL:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) == _interpretCondition(cond, row + 1, (pos << 1) + 1, instance) ? 1 : 0;
                        case Meta.NOT_EQUAL:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) != _interpretCondition(cond, row + 1, (pos << 1) + 1, instance) ? 1 : 0;
                        case Meta.ADDITION:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) + _interpretCondition(cond, row + 1, (pos << 1) + 1, instance);
                        case Meta.SUBTRACTION:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) - _interpretCondition(cond, row + 1, (pos << 1) + 1, instance);
                        case Meta.DIVISION:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) / _interpretCondition(cond, row + 1, (pos << 1) + 1, instance);
                        case Meta.MULTIPLICATION:
                            return _interpretCondition(cond, row + 1, pos << 1, instance) * _interpretCondition(cond, row + 1, (pos << 1) + 1, instance);
                        case Meta.BITWISE_AND:
                            return (int) Math.round(_interpretCondition(cond, row + 1, pos << 1, instance)) & (int) Math.round(_interpretCondition(cond, row + 1, (pos << 1) + 1, instance));
                        case Meta.BITWISE_OR:
                            return (int) Math.round(_interpretCondition(cond, row + 1, pos << 1, instance)) | (int) Math.round(_interpretCondition(cond, row + 1, (pos << 1) + 1, instance));
                        case Meta.BITWISE_XOR:
                            return (int) Math.round(_interpretCondition(cond, row + 1, pos << 1, instance)) ^ (int) Math.round(_interpretCondition(cond, row + 1, (pos << 1) + 1, instance));
                        case Meta.LOGICAL_AND:
                            return (_interpretCondition(cond, row + 1, pos << 1, instance) != 0) && (_interpretCondition(cond, row + 1, (pos << 1) + 1, instance) != 0) ? 1 : 0;
                        case Meta.LOGICAL_OR:
                            return (_interpretCondition(cond, row + 1, pos << 1, instance) != 0) || (_interpretCondition(cond, row + 1, (pos << 1) + 1, instance) != 0) ? 1 : 0;

                    }
                } else {
                    if (Meta.debug && ch - Meta.CONDITIONS.length < 0) {
                        System.out.println("" + (ch));
                    }
                    return instance[ch - Meta.CONDITIONS.length];
                }

            } else {
                throw new Exception("Cannot interpret row " + row + " of " + Parameters.getInstance().getCondition_max_depth() + " at position " + pos + " of " + (1 << row));
            }
        } else {
            throw new Exception("Cannot interpet null condition.");
        }
        return 0;
    }

}
