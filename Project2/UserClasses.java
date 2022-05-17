import java.util.*;
import java.util.concurrent.*;

public class UserClasses{
    LinkedHashMap<String, String> fields;
    LinkedHashMap<String, Method> methods;
    
    private String name;
    private String parent_name;
    public UserClasses parent_class;

    public UserClasses(String name){
        parent_name = null;
        parent_class = null;
        this.name = name;
        fields = new LinkedHashMap<String, String>();
        methods = new LinkedHashMap<String, Method>();
    }

    public String GetName(){
        return this.name;
    }

    public void SetParentName(String name){
        this.parent_name = name;
    }
    public String GetParentName(){
        return this.parent_name;
    }

    public void InsertField(String name, String type){
        this.fields.put(name, type);
    }
    public void InsertMethod(String name, Method method){
        this.methods.put(name, method);
    }
    public void Print(GlobalVars global){
        for(Map.Entry<String,String> entry : this.fields.entrySet()){
            System.out.println(this.name+"."+entry.getKey()+":"+global.fields_offset); 
            if(entry.getValue() == "int")
                global.fields_offset += 4;
            else if(entry.getValue() == "boolean")
                global.fields_offset += 1;
            else    
                global.fields_offset +=8;
        }
        for(Map.Entry<String,Method> entry : this.methods.entrySet()){

            if(parent_class != null){
                boolean flag = parent_class.SearchMethod(entry.getValue().GetName());
                if(flag == true)
                    continue;
            }
            System.out.print(this.name+"."); entry.getValue().Print(); System.out.println(global.methods_offset);
            global.methods_offset += 8;
        }
        //to do: search for parent process methods
    }
    public boolean SearchField(String value){
        return this.fields.containsKey(value);
    }
    public String FieldType(String value){
        return this.fields.get(value);
    }
    public boolean SearchMethod(String value){
        return this.methods.containsKey(value);
    }
    public Method GetMethod(String name){
        return this.methods.get(name);
    }
}