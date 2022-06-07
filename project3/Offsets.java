import java.util.*;
import java.util.concurrent.*;
public class Offsets{

    private LinkedList<Pair> offsets = new LinkedList<Pair>();
    private LinkedList<Pair> offsets_methods = new LinkedList<Pair>();

    public void Insert(String name, int offset){
        Pair pair = new Pair(name, offset);
        this.offsets.add(pair);
    }

    public void InsertMethod(String name, int offset){
        Pair pair = new Pair(name, offset);
        this.offsets_methods.add(pair);
    }

    public void Print(){
        Pair pair;
        for(int i = 0; i < this.offsets.size(); i++){
            pair = this.offsets.get(i);
            System.out.println(pair.name + " " + pair.offset);
        }
        for(int i = 0; i < this.offsets_methods.size(); i++){
            pair = this.offsets_methods.get(i);
            System.out.println(pair.name + " " + pair.offset);
        }
    }

    public int GetOffset(String name){
        Pair pair;
        String name1 = null;
        for(int i = 0; i < this.offsets_methods.size(); i++){
            pair = this.offsets_methods.get(i);
            name1 = pair.GetName();
            // System.out.println("AAA" + name1);
            if(name.equals(name1)){
                return pair.offset;
            }
            String name2 = "";
            String[] n = name1.split("\\.", 3);
            for(String a : n){
                name2 = a;
            }
            if(name.equals(name2)){
                return pair.offset;
            }
        }
        for(int i = 0; i < this.offsets.size(); i++){
            pair = this.offsets.get(i);
            name1 = pair.GetName();
            // System.out.println("AAA" + name1);
            if(name.equals(name1)){
                return pair.offset;
            }
            String name2 = "";
            String[] n = name1.split("\\.", 3);
            for(String a : n){
                name2 = a;
            }
            if(name.equals(name2)){
                return pair.offset;
            }
        }
        return -1;
    }

    public int SizeOffset(String classname){
        int size = 0;
        Pair pair;
        String name1 = null;
        for(int i = 0; i < this.offsets.size(); i++){
            pair = this.offsets.get(i);
            name1 = pair.GetName();
        
            boolean flag = name1.startsWith(classname);
            if(flag)    {
                size = size + pair.GetOffset();
            }
        }
        return size;
    }

    public int Size(String classname){
        int size = 0;
        Pair pair;
        String name1 = null;
        for(int i = 0; i < this.offsets_methods.size(); i++){
            pair = this.offsets_methods.get(i);
            name1 = pair.GetName();
            boolean flag = name1.startsWith(classname);
            if(flag)    
                size++;

        }
        return size;
    }

}

class Pair{

    String name;
    int offset;

    public Pair(String name, int offset){
        this.name = name;
        this.offset = offset;
    }

    public void Print(){
        System.out.println(name + ":" + offset);
    }

    public String GetName(){
        return this.name;
    }

    public int GetOffset(){
        return this.offset;
    }

}