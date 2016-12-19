package datamining;

import java.util.ArrayList;
import java.util.List;

public class Itemset{
    public Itemset(){
        keys = new ArrayList<>();
        support = 0;
    };
    public List<String> keys;
    public int support;
}
