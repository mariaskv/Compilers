import java.util.*;
import java.util.concurrent.*;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class SymbolTable{

    LinkedHashMap<String, UserClasses> SymbolTable = new LinkedHashMap<String, UserClasses>();

    public void Insert(String name, UserClasses class_input){
        this.SymbolTable.put(name, class_input);
    }
    public void Print(GlobalVars global, Offsets ofs){
        for(Map.Entry<String,UserClasses> entry : this.SymbolTable.entrySet()){
            entry.getValue().Print(global, ofs);
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
    public Method FindMethod(String classname){
        for(Map.Entry<String,UserClasses> entry : this.SymbolTable.entrySet()){
            if(entry.getValue().SearchMethod(classname))
                return entry.getValue().GetMethod(classname);
        }
        return null;
    }
    public UserClasses ClassByName(String classname){
        for(Map.Entry<String,UserClasses> entry : this.SymbolTable.entrySet()){
            if(entry.getValue().SearchMethod(classname))
                return entry.getValue();
        }
        return null;    
    }

    public String PrintMethod(String classname, Method m){
        String method_name = m.GetName();
        String vtable = "";
        String type = "";

        if(!method_name.equals("main")){
            // vtable += ", ";

            type = m.LLVMGetType();

            if(type.equals("i32")){
                vtable += "i8* bitcast (i32 (i8*";
            }
            else if(type.equals("i1")){
                vtable += "i8* bitcast (i1 (i8*";
            }
            else if(type.equals("i32*")){
                vtable += "i8* bitcast (i32* (i8*";
            }
            else{
                vtable += "i8* bitcast (i8* (i8*";
            }

            LinkedList<String> args = m.GetArgsTypes();

            for(int i = 0; i < args.size(); i++){
                if(args.get(i).equals("int")){
                    vtable += ",i32";
                }
                else if(args.get(i).equals("boolean")){
                    vtable += ",i1";
                }
                else if(args.get(i).equals("int[]")){
                    vtable += ",i32*";
                }
                else{
                    vtable += ",i8*";
                }
            }
            vtable += ")* @" + classname + "." + method_name + " to i8*)";

        }
            return vtable;
    }

    public String IterateVtables(String classname, Offsets of){
        UserClasses class_a = this.FindClass(classname);
        UserClasses parent_class = class_a.GetParent();
        
        int size = of.Size(classname);

        if(size == 0 && parent_class != null){
            size = of.Size(parent_class.GetName());
        }

        String vtable = "@." + class_a.GetName() + "_vtable = global [" + size + " x i8*][";

        LinkedHashMap<String, Method> methods = class_a.GetMethods();

        int sz = methods.size();
        int i = 0;

        for(Map.Entry<String,Method> entry : methods.entrySet()){
            vtable += PrintMethod(classname, entry.getValue());
             if(i < sz - 1)
                vtable += ",";
            i++;
        }

        if(parent_class != null){
            i = 0;
            LinkedHashMap<String, Method> methods1 = parent_class.GetMethods();
            sz = methods.size();
            
            for(Map.Entry<String,Method> entry1 : methods.entrySet()){

                if(methods.containsKey(entry1.getKey()))
                    continue;
                
                vtable += ",";

                vtable += PrintMethod(parent_class.GetName(), entry1.getValue());
                if(i < sz - 1)
                    vtable += ",";
                i++;       
            }

        }

        return vtable + "]";
    }

    public void Vtables(Offsets of, BufferedWriter writer){
        for(Map.Entry<String, UserClasses> entry : this.SymbolTable.entrySet()){
            String out = this.IterateVtables(entry.getKey(), of);
            try{
                writer.write(out);
                writer.newLine();
            }
            catch(IOException e){
                System.out.println(e.getMessage());
            }
        }    
    }

}


