import syntaxtree.*;
import visitor.*;
import java.util.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length != 1){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }



        FileInputStream fis = null;
        for (String file : args){
            String[] arr = file.split("\\.", 2);
            String b = null;
            for(String a : arr){
                b = a;
                break;
            }
            try{
                fis = new FileInputStream(file);
                MiniJavaParser parser = new MiniJavaParser(fis);

                Goal root = parser.Goal();

                System.err.println("Program parsed successfully.");

                SymbolTable table = new SymbolTable();

                MyVisitor eval = new MyVisitor(table);
                root.accept(eval, null);

                // MySecondVisitor eval2 = new MySecondVisitor(table);
                // root.accept(eval2, null);

                // System.err.println("Program parsed 2nd time successfully.");

                Offsets ofs = new Offsets();

                GlobalVars global = new GlobalVars();
                eval.table.Print(global, ofs);
                ofs.Print();

                System.out.println("\n");

                BufferedWriter bf = new BufferedWriter(new FileWriter(b + ".ll"));

                MyThirdVisitor eval3 = new MyThirdVisitor(table, ofs, bf);
                root.accept(eval3, null);

                bf.close();

                System.out.println("\n");

                System.err.println("Program changed into LLVM code.");
            }
            catch(ParseException ex){
                System.out.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
            }
            finally{
                try{
                    if(fis != null) fis.close();
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}


