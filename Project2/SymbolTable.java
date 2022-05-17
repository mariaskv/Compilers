import java.util.*;
import java.util.concurrent.*;

public class SymbolTable{

    LinkedHashMap<String, UserClasses> SymbolTable = new LinkedHashMap<String, UserClasses>();

    public void Insert(String name, UserClasses class_input){
        this.SymbolTable.put(name, class_input);
    }
    public void Print(GlobalVars global){
        for(Map.Entry<String,UserClasses> entry : this.SymbolTable.entrySet()){
            entry.getValue().Print(global);
        }
    }
    public boolean Search(String value){
        return this.SymbolTable.containsKey(value);
    }
    public UserClasses FindClass(String name){
        return this.SymbolTable.get(name);
    }
    public boolean ExistsClass(String name){
        return this.SymbolTable.containsKey(name);
    }
}


