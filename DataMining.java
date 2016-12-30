package datamining;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DataMining {
    
    
    
    
//    static class itemsetLexicalCmp implements Comparator<List<String>>{
//        
//        public itemsetLexicalCmp(){}
//        
//        @Override
//        public int compare(List<String> o1, List<String> o2) {
//            int result = 0;
//            for (int i = 0; i < o1.size(); i++) {
//                result = o1.get(i).compareTo(o2.get(i));
//                if(result != 0){
//                
//                }
//            }
//        }
//        
//    };
    static Map<String, List<Integer>> index;
    public static void main(String[] args) throws Exception{
        long start = System.currentTimeMillis();
        //////////////BUILD INVERTED INDEX//////////////////////////////////////
        index = new TreeMap<>();
        Path path = Paths.get(INPUT_FILE_NAME);
        Scanner scanner = new Scanner(path, ENCODING.name());
        int tranId = 0;
        int terms = 0;
        int maxItemSet = 0;
        
        while(scanner.hasNextLine() && tranId < 1000000){
            String[] line = scanner.nextLine().split(";");
            for (String item: line){
                if(index.containsKey(item)){
                    index.get(item).add(tranId);
                }
                else{
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(tranId);
                    index.put(item, list);
                }
                terms++;
            }
            tranId++;
        }
        minSupport = (int)(tranId*minSupP);
      
        ////////////////MINING FREQUENT 1-ITEMSET////////////////////////
//        Iterator<Entry<String,List<Integer>>> it;
//        it = index.entrySet().iterator();
//        
//        while(it.hasNext()){
//            Entry<String, List<Integer>> entry = it.next();
//            if (entry.getValue().size() < minSupport){
//                it.remove();
//            }
//        }
//        System.out.println("Frequent 1-itemset: " +index.size());
//        
//        
//        ////////////////GENERAL CASE MINING//////////////////////////////
//        TreeMap<List<String>, Integer> fSet;
//        //gen initial frequent 2-itemset
//        fSet = genInitSet();
//
//        //print fSet - ignore fSet with 1 item 
//        //generate cSet from fSet
//        fSet = genCSet(fSet);
//        
//        //while candidate set != null
//        while(fSet == null){
//            //print fSet
//            for(Entry<List<String>, Integer> e : fSet.entrySet()){
//                StringBuilder buffer = new StringBuilder();
//                for(String key : e.getKey()){
//                    buffer.append(key).append(";");
//                }
//                buffer.append("\t").append(e.getValue());
//                OUTPUT_STREAM.add(buffer.toString());
//            }
//            
//            //gen cSet
//            fSet = genCSet(fSet);
//        }


        //Execution time
        long end = System.currentTimeMillis();
        System.out.println("Execution time: " + (end - start));
        
        //write output
        path = Paths.get(OUTPUT_FILE_NAME);
        Files.write(path, OUTPUT_STREAM, ENCODING);
        
    }
    
    static TreeMap<List<String>, Integer> genInitSet(){
        TreeMap<List<String>, Integer> result = new TreeMap<>();
        
        List<String> currentSet;
        List<String> f1 = new ArrayList<>(index.keySet());
        for (int i = 0; i < f1.size(); i++) {
            for (int j = i + 1; j < f1.size(); j++) {
                currentSet = new ArrayList<>();
                currentSet.add(f1.get(i));
                currentSet.add(f1.get(j));
                
                int support = supportCal(currentSet);
                if(support > minSupport){
                    result.put(currentSet, support);
                }
            }
        }
        
        return result;
    }
            
    static TreeMap genCSet(TreeMap<List<String>,Integer> fSet){
        if (fSet.size() <= 1) return null;
            
        TreeMap<List<String>,Integer> result = new TreeMap<>();
        
        boolean cPrefix, prunned;    
        List<List<String>> itemsets = new ArrayList<>(fSet.keySet());
        int itemsetSize = itemsets.get(0).size();
        
        //iterate through all pairs of itemset
        for (int i = 0; i < itemsets.size(); i++) {
            List<String> set1 = itemsets.get(i);
            for (int j = i + 1; j < itemsets.size(); j++) {
                List<String> set2 = itemsets.get(j);
                
                //common prefix check
                cPrefix = true;
                for (int k = 0; k < itemsetSize - 1; k++) {
                    if(set1.get(k)
                        .compareTo(set2.get(k)) != 0)
                        cPrefix = false;
                }
                
                if(cPrefix){
                    //pruning check
                    //last1, last2: two last elements
                    String last1 = set1.get(itemsetSize - 1);
                    String last2 = set2.get(itemsetSize - 1);
                    List<String> commonSet = new LinkedList<>(set1);
                    commonSet.remove(itemsetSize-1);
                    
                    //tempSet: current set to check existence
                    List<String> tempSet = new LinkedList<>();
                    prunned = false;
                    for (int k = 0; k < itemsetSize; k++) {
                        tempSet.addAll(commonSet);
                        tempSet.remove(k);
                        tempSet.add(last1);
                        tempSet.add(last2);
                        if(!fSet.containsKey(tempSet)){
                            prunned = true;
                            break;
                        }
                        tempSet.clear();
                    }
                    
                    if(!prunned){
                        //candidate itemset = {commonSet} + last1 + last2
                        commonSet.add(last1);
                        commonSet.add(last2);
                        
                        int support = supportCal(commonSet);
                        if(support > minSupport){
                            result.put(commonSet, support);
                        }
                    }
                }
                
            }
        }
        
        fSet.clear();
        return result;
    }

    
    
    static int supportCal(List<String> itemset){
        //List of common transactions 
        TreeMap<Integer, Integer> commonTrans = new TreeMap<>();
        int numItem = itemset.size();
        for(String item : itemset){
            for(Integer tranID : index.get(item)){
                if(commonTrans.containsKey(tranID)){
                    commonTrans
                        .put(tranID, commonTrans.get(tranID) + 1);
                }
                else{
                    commonTrans.put(tranID, 1);
                }
            }
        }
        
        int count = 0;
        for(Entry<Integer,Integer> e : commonTrans.entrySet()){
            if(e.getValue() == numItem) count++;
        }
        
        return count;
    }
    

    static Integer minSupport;
    final static double minSupP = 0.01;
    
    //result stream.
    static List<String> OUTPUT_STREAM = new LinkedList<>();
    final static String INPUT_FILE_NAME = 
        "C:/Users/8470p/Desktop/dataMining/mushrooms.csv";
    final static String OUTPUT_FILE_NAME =
        "C:/Users/8470p/Desktop/dataMining/output.txt";
    final static Charset ENCODING = StandardCharsets.UTF_8;

    


 
}

