package datamining;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class DataMining {
    
    public static HashMap<String, List<Integer>> index;
    //result stream.
    public static List<String> OUTPUT_STREAM = new LinkedList<String>();
    public static void main(String[] args) throws Exception{
        
        //////////////BUILD INVERTED INDEX//////////////////////////////////////
        index = new HashMap<>();
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
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(tranId);
                    index.put(item, list);
                }
                terms++;
            }
            tranId++;
        }
        minSupport = (int)(tranId*minSupP);
      
        ////////////////MINING FREQUENT 1-ITEMSET////////////////////////
        Iterator<Map.Entry<String,List<Integer>>> it;
        it = index.entrySet().iterator();
        
        while(it.hasNext()){
            Map.Entry<String, List<Integer>> entry = it.next();
            if (entry.getValue().size() < minSupport){
                it.remove();
            }
        }
        System.out.println("Frequent 1-itemset: " +index.size());
        
        
        ////////////////GENERAL CASE MINING//////////////////////////////
        HashMap<List<String>, Integer> fSet;
        //gen initial frequent 2-itemset
        fSet = genInitSet();

        //print fSet - ignore fSet with 1 item 
        //generate cSet from fSet
        fSet = genCSet(fSet);
        
        //while candidate set != null
        while(fSet == null){
            //print fSet
            for(Map.Entry<List<String>, Integer> e : fSet.entrySet()){
                StringBuilder buffer = new StringBuilder();
                for(String key : e.getKey()){
                    buffer.append(key).append(";");
                }
                buffer.append("\t").append(e.getValue());
                OUTPUT_STREAM.add(buffer.toString());
            }
            
            //gen cSet
            fSet = genCSet(fSet);
        }



        //write output
        path = Paths.get(OUTPUT_FILE_NAME);
        Files.write(path, OUTPUT_STREAM, ENCODING);
        
        
    }
    
    static HashMap<List<String>, Integer> genInitSet(){
        HashMap<List<String>, Integer> result = new HashMap<>();
        for (Map.Entry<String,List<Integer>> entry : index.entrySet()){
            List<String> item = new ArrayList<>();
            item.add(entry.getKey());
            result.put(item, entry.getValue().size());
        }
        return result;
    }
            
    static HashMap genCSet(HashMap<List<String>,Integer> fSet){
        if (fSet.size() <= 1) return null;
            
        HashMap<List<String>,Integer> result = new HashMap<>();
        
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
                        if(!tempSet.isEmpty()){
                            prunned = true;
                            break;
                        }
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
        HashMap<Integer, Integer> commonTrans = new HashMap<>();
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
        for(Map.Entry<Integer,Integer> e : commonTrans.entrySet()){
            if(e.getValue() == numItem) count++;
        }
        
        return count;
    }
    

    static Integer minSupport;
    final static double minSupP = 0.01;
    final static String INPUT_FILE_NAME = 
        "C:/Users/8470p/Desktop/dataMining/testData.txt";
    final static String OUTPUT_FILE_NAME =
        "C:/Users/8470p/Desktop/dataMining/output.txt";
    final static Charset ENCODING = StandardCharsets.UTF_8;


 
}

