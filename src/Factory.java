/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COS700_Project;

import java.util.Random;

/**
 *
 * @author rudo
 */
public class Factory {
            
            
            public static boolean  createMainPrimitive(byte[][] tree,int current_depth,int current_pos, int max_depth, boolean full,Random rand) throws Exception{
                        if(current_depth >= max_depth || current_pos > 1 << current_depth){
                                    throw new Exception("Cannot create primitive at depth "+current_depth+" of max depth "+max_depth+" at position "+current_pos +" of max position "+(1 << current_depth));
                        }
                        if(current_depth == 0){ 
                                     tree[current_depth][current_pos] = (byte) Meta.MAINS[ rand.nextInt(Meta.MAINS.length - 0) + 0];
                                     return true;
                        }else if(current_depth > 0 && current_depth < max_depth - 1){
                                    if(full){
                                             tree[current_depth][current_pos] = (byte)  Meta.MAINS[ rand.nextInt(Meta.MAINS.length - 0) + 0];
                                             return true;
                                    }else{
                                                if(rand.nextBoolean()){
                                                            
                                                            tree[current_depth][current_pos] = (byte)  Meta.MAINS[ rand.nextInt(Meta.MAINS.length - 0) + 0];
                                                            return true;
                                                }else{ 
                                                            tree[current_depth][current_pos] =  (byte) (Meta.MAINS.length  + rand.nextInt(Data.initialiseData().getNumberClasses() - 0) + 0 );
                                                            return false;
                                                }
                                    }
                        }else if( current_depth == max_depth - 1){
                                    tree[current_depth][current_pos] =  (byte) (Meta.MAINS.length +  rand.nextInt(Data.initialiseData().getNumberClasses() - 0) + 0);
                                    return false;
                        } else{
                            return false;
                        }
            }
            
             public static boolean  createConditionPrimitive(byte[][] tree,int current_depth,int current_pos, int max_depth, boolean full,Random rand) throws Exception{
                        if(current_depth >= max_depth){
                                    throw new Exception("Cannot create primitive at depth "+current_depth+" of max depth "+max_depth);
                        }
                        if(current_depth == 0){ 
                                     tree[current_depth][current_pos] = (byte)  Meta.CONDITIONS[ rand.nextInt(Meta.CONDITIONS.length - 0) + 0];
                                     return true;
                        }else if(current_depth > 0 && current_depth < max_depth - 1){
                                    if(full){
                                             tree[current_depth][current_pos] = (byte)  Meta.CONDITIONS[ rand.nextInt(Meta.CONDITIONS.length - 0) + 0];
                                             return true;
                                    }else{
                                                if(rand.nextBoolean()){
                                                            tree[current_depth][current_pos] = (byte)  Meta.CONDITIONS[ rand.nextInt(Meta.CONDITIONS.length - 0) + 0];
                                                            return true;
                                                }else{ 
                                                            tree[current_depth][current_pos] =   (byte) (Meta.CONDITIONS.length + rand.nextInt(Data.initialiseData().getNumberAttributes() - 0) + 0);
                                                            return false;
                                                }
                                    }
                        }else if( current_depth == max_depth - 1){
                                    tree[current_depth][current_pos] =  (byte)  (Meta.CONDITIONS.length + rand.nextInt(Data.initialiseData().getNumberAttributes()  - 0) + 0);
                                    return false;
                        }else{
                            return false;
                        }
            }
}
