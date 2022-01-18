package COS700_Project; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Randomness {
    private Random mother, 
                   child;
    private static Randomness singleton = null;
    private Randomness(long seed) {
        mother  = new Random(seed);
        child   = new Random(mother.nextLong());
    }
    
    public static Randomness getInstance(){
        if(singleton == null){
            singleton   = new Randomness(42069);
        }
        return singleton;
    }
    
    public static Randomness getInstance(long seed){
        if(singleton == null){
            singleton   = new Randomness(seed);
        }
        singleton.mother  = new Random(seed);
        singleton.child   = new Random(singleton.mother.nextLong());
        return singleton;
    }
    
    public void shuffle(ArrayList<double[]> list){
        Collections.shuffle(list, child);
    }
    
    public void reseed(){
        child.setSeed(mother.nextLong());
    }
    
    public void setSeed(long seed){
        child.setSeed(seed);
    }
    public long getRandomLong(){
        return child.nextLong();
    }
    
    public int getRandomIntInclusive(int min, int max){
        return child.nextInt(max + 1 - min) + min;
    }
    
    public int getRandomIntExclusive(int min, int max){
        return child.nextInt(max - min) + min;
    }
    
    public boolean getRandomBoolean(){
        return child.nextBoolean();
    }
    
}
