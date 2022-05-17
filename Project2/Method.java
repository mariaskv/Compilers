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

    public String GetType(){
        return this.type;
    }

    public LinkedList GetArgs(){
        return this.args;
    }

    public LinkedList GetArgsTypes(){
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
        int index = this.args.indexOf(name);
        String type =  this.args_types.get(index);
        return type;
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
}