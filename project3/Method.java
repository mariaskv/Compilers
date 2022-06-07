import java.util.*;
import java.util.concurrent.*;

public class Method{
    private LinkedList<String> args; //name of args etc. i,j
    private LinkedList<String> args_types; //type of args etc. int, int
    private String name;
    private String type;
    public boolean defined_in_parent;
    LinkedHashMap<String, String> variables;

    public Method(String name, String type){
        this.name = name;
        this.type = type;
        this.variables =  new LinkedHashMap<String, String>();
        this.args = new LinkedList<String>();
        this.args_types = new LinkedList<String>();
    }

    public String GetName2(){
        return this.name;
    }

    public String GetType(){
        return this.type;
    }

    public String LLVMGetType(){
        if(this.type.equals("int"))
            return "i32";
        if(this.type.equals("integer array"))
            return "i32*";
        if(this.type.equals("boolean"))
            return "i1";
        return "i8*";
    }

    public LinkedList<String> GetArgs(){
        return this.args;
    }

    public LinkedList<String> GetArgsTypes(){
        return this.args_types;
    }

    public void Insert(String name, String type){
        this.variables.put(name, type);
    }

    public void Print(){
        System.out.print(this.name + ":");
    }

    public boolean Search(String value){
        return this.variables.containsKey(value);
    }

    public String GetName(){
        return this.name;
    }

    public void AddArg(String arg){
        this.args.add(arg);
    }

    public boolean SearchArgs(String arg){
        return this.args.contains(arg);
    }

    public void AddArgType(String arg){
        this.args_types.add(arg);
    }

    public String TakeArgType(String name){
        if(this.SearchArgs(name)){
            int index = this.args.indexOf(name);
            String type =  this.args_types.get(index);
            return type;
        }
        return null;
    }

    public boolean CheckMethods(Method method){
        LinkedList<String> args_types_m = method.args_types;
        String return_type = method.type;
        if(this.args_types.equals(args_types_m) && return_type.equals(this.type)){
            return true;
        }
        return false;
    }
    public String FieldType(String value){
        return this.variables.get(value);
    }

    public String LoadVar(String name, String id, LinkedHashMap<String, String> vt){
        String return_string;
        String type = null;
        if(this.Search(name)) 
            return_string = this.variables.get(name);
        else if(this.SearchArgs(name))
            return_string = this.TakeArgType(name);
        else{
            return_string = vt.get(name);
            type = return_string;
        }
            // System.out.println(name + return_string);
        String load;
        if(type == null){
            if(return_string.equals("int")){
                type = "i32";
            }
            else if(return_string.equals("integer array")){
                type = "i32*";
            }
            else if(return_string.equals("boolean")){
                type = "i1";
            }
            else if(return_string.equals("boolean array")){
                type = "i8*";
            }
            else{
                type = "i8*";
            }
        }
        vt.put(id, type);
        load = "\t" + id + " = load " + type + ", " + type + "* %" + name;
        return load;
    }

}