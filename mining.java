package datamining;

import java.io.FileInputStream;
import java.io.InputStreamReader;
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

    public static void main(String[] args) throws Exception{
        
        //////////////BUILD INVERTED INDEX////////////////////////////////////////////
        HashMap<String, List<Integer>> index = new HashMap<>();
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
        
        //////
        List<String> result = new LinkedList<String>();
        
        
        ////////////////MINING FREQUENT 1-ITEMSET////////////////////////
        Iterator<Map.Entry<String,List<Integer>>> it;
        it = index.entrySet().iterator();
            
        while(it.hasNext()){
            Map.Entry<String, List<Integer>> entry = it.next();
            if (entry.getValue().size() < minSupport){
                it.remove();
            }
        }
        
        /////////////////MINING FREQUENT 2-ITEMSET///////////////////////
        // List<Map.Entry<String, List<Integer>>> itemList;
        // itemList = new ArrayList<>(index.entrySet());
        // Map.Entry<String,List<Integer>> cItem1;
        // Map.Entry<String,List<Integer>> cItem2;
        // for(int i = 0; i < itemList.size(); i++){
        //     cItem1 = itemList.get(i);
        //     for (int j = i + 1; j < itemList.size(); j++) {
        //         cItem2 = itemList.get(j);
        //         int support = supportCal(cItem1.getValue(), cItem2.getValue());
        //         if(support > minSupport){
        //             result.add(
        //                 cItem1.getKey().toString()+ "; " +
        //                 cItem2.getKey().toString()+ ": " +
        //                 support);
        //         }
        //     }
        // }

        
        ////////////////GENERAL CASE MINING/////////////////
        List<Itemset> cSets, fSets;
        fSets = genInitSet(index.entrySet());
        //print fSet
        
        //gen cSet
        cSets = generateCandidate(fSets, index);
        while(cSets.size()>0){
            //test cSet, update fSet
            update(fSet, cSet, index);
            //print fSet
            
            //gen cSet
            cSets = generateCandidate(fSets, index);
        }



        //write output
        path = Paths.get(OUTPUT_FILE_NAME);
        Files.write(path, result, ENCODING);
        
        System.out.println(index.size());
        return;
        
    }
    
    static List<Itemset> genInitSet(Set<Map.Entry<String,List<Integer>>> index){
        
    }
            
    static class Itemset{
        public Itemset(){};
        public List<String> keys;
        public int support;
    }
    
    static Integer minSupport;
    final static double minSupP = 0.01;
    final static String INPUT_FILE_NAME = 
        "/home/linh/NetBeansProjects/dataMining/src/datamining/data.txt";
    final static String OUTPUT_FILE_NAME =
        "/home/linh/NetBeansProjects/dataMining/src/datamining/output.txt";
    final static Charset ENCODING = StandardCharsets.UTF_8;
    
    
    static int supportCal(List<Integer> l1, List<Integer> l2){
        HashMap<Integer,Integer> occurrences = new HashMap<>();
        int support = 0;
        for (int i = 0; i < l1.size(); i++) {
            int temp = l1.get(i);
            if(!occurrences.containsKey(l1.get(i))){
                occurrences.put(temp, 1);
            }
        }
        for (int i = 0; i < l2.size(); i++) {
            if(occurrences.containsKey(l2.get(i))){
                support++;
            }
        }
        return support;
    }
    

}
