import java.util.*;
import java.util.concurrent.*;

public class UserClasses{
    LinkedHashMap<String, String> fields;
    LinkedHashMap<String, Method> methods;
    
    private String name;
    private String parent_name;
    public UserClasses parent_class;
    public boolean flag;

    public UserClasses(String name){
        parent_name = null;
        parent_class = null;
        this.name = name;
        fields = new LinkedHashMap<String, String>();
        methods = new LinkedHashMap<String, Method>();
    }

    public int GetSize(){
        int size = this.fields.size() + this.methods.size();
        return size;
    }

    public LinkedHashMap<String, Method> GetMethods(){
        return this.methods;
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

    public UserClasses GetParent(){
        return this.parent_class;
    }

    public void InsertField(String name, String type){
        this.fields.put(name, type);
    }
    public void InsertMethod(String name, Method method){
        this.methods.put(name, method);
    }
    public void Print(GlobalVars global, Offsets ofs){
        for(Map.Entry<String,String> entry : this.fields.entrySet()){
            // System.out.println(this.name+"."+entry.getKey()+":"+global.fields_offset); 
            String input = this.name+"."+entry.getKey();
            ofs.Insert(input, global.fields_offset);
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
            // System.out.print(this.name+"."); entry.getValue().Print(); 
            // System.out.println(global.methods_offset);
            String input = this.name+"."+ entry.getValue().GetName2();
            if(entry.getValue().GetName2().equals("main"))
                continue;
            ofs.InsertMethod(input, global.methods_offset);
            global.methods_offset += 8;
        }
        global.fields_offset = 0;
        global.methods_offset = 0;
    }
    public boolean SearchField(String value){
        return this.fields.containsKey(value);
    }
    public String FieldType(String value){
        if(this.SearchField(value))
            return this.fields.get(value);
        return null;
    }
    public boolean SearchMethod(String value){
        return this.methods.containsKey(value);
    }
    public Method GetMethod(String name){
        return this.methods.get(name);
    }

    public void YesVtable(){
        this.flag = true;
    }

    public boolean GetVtable(){
        return this.flag;
    }

    public String LoadField(String var, Offsets offsets, String new_name, String new_name2, LinkedHashMap<String, String> var_t){
        int offset = 0;
        String type = null, return_string = null;
        String search_offset = this.name + "." + var;
        if(this.SearchField(var)){
            return_string = this.FieldType(var);
            offset = offsets.GetOffset(search_offset);
        }
        else if(var_t.containsKey(var)){
            return_string = var_t.get(var);      
            offset = offsets.GetOffset(search_offset);    
        }
        else{
            search_offset = this.parent_class.name + "." + var;
            return_string = this.parent_class.FieldType(var);
            offset = offsets.GetOffset(search_offset);    
        }


        // System.out.println("AA" + return_string);

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

        // System.out.println("BB" + type);

        if(this.GetVtable())
            offset += 8;

        // System.out.println("OFF " + return_string + " " + type);
        // System.out.println("PPPPPP" + new_name2 );
        var_t.put(new_name2, type);

        String input = "\t" + new_name + " = getelementptr i8, i8* %this, i32 " + offset + "\n";
        input = input + "\t" +new_name2 + " = bitcast i8* " + new_name + " to " + type + "*\n";
        // input = input + "\t" + new_name3 + " = load " + type + " , " + type + " *" + new_name2;
        return input;

    }

    public String GetField(String var, Offsets offsets, String new_name, String new_name2, String new_name3, LinkedHashMap<String, String> var_t){
        int offset = 0;
        String type = null, return_string = null;
        String search_offset = this.name + "." + var;
        UserClasses parent = this.GetParent();
        // System.out.println(var);
        boolean flag = true;
        if(this.SearchField(var)){
            return_string = this.FieldType(var);
            offset = offsets.GetOffset(search_offset);
            flag = false;
        }
        else if(var_t.containsKey(var)){
            return_string = var_t.get(var);      
            offset = offsets.GetOffset(search_offset);
            flag = false;
        }
        // else if{
        //     search_offset = this.parent_class.name + "." + var;
        //     return_string = this.FieldType(var);
        //     offset = offsets.GetOffset(search_offset);  
        //     flag = false;
        // }
        if(flag == true){
            search_offset = parent.name + "." + var;
            if(parent.SearchField(var)){
                return_string = parent.FieldType(var);
                offset = offsets.GetOffset(search_offset);
            }
            else{
                search_offset = parent.parent_class.name + "." + var;
                return_string = parent.FieldType(var);
                offset = offsets.GetOffset(search_offset);    
            }
        }
        // System.out.println("RR " + return_string);


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
        if(this.GetVtable())
            offset += 8;

        String input = "\t" + new_name + " = getelementptr i8, i8* %this, i32 " + offset + "\n";
        input = input + "\t" +new_name2 + " = bitcast i8* " + new_name + " to " + type + "*\n"; //NOO
        input = input + "\t" + new_name3 + " = load " + type + " , " + type + "* " + new_name2 +"\n";
        return input;

    }
}