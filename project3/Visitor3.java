import syntaxtree.*;
import visitor.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;

class MyThirdVisitor extends GJDepthFirst<String, Void>{

    Method curren_method;
    UserClasses current_class;
    SymbolTable table;
    Offsets offsets;
    LinkedHashMap<String, String> variables;
    LinkedList<String> parameters;
    LinkedList<String> parameters_types;
    LinkedHashMap<String, String> var_type;
    int count = 0;
    int label_count = -1;
    BufferedWriter writer;

    private String CreateVariable(){
       this.count++;
       return "%_" + this.count;
    }  

    private String CreateLabel(){
       this.label_count++;
       return "if" + this.label_count;
    }

    private void print(String s){
        try{
           writer.write(s);
           writer.newLine();
        }
        catch(IOException e){
           System.out.println(e.getMessage());
        }
      // System.out.println(s);
    }


    public MyThirdVisitor(SymbolTable t, Offsets of, BufferedWriter bf){
        this.writer = bf;
        table = t;
        offsets = of;
        current_class = null;
        curren_method = null;
        variables = new LinkedHashMap<String, String>();
        var_type = new LinkedHashMap<String, String>();
        parameters = new LinkedList<String>();
        parameters_types = new LinkedList<String>();
    }

   /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */

    //TOTALY FINE
   public String visit(Goal n, Void argu) throws Exception {
        
      // print vtable info : TO DO


      this.table.Vtables(this.offsets, this.writer);
      
      print("declare i8* @calloc(i32, i32)");
      print("declare i32 @printf(i8*, ...)");
      print("declare void @exit(i32)");
      print("");
      print("@_cint = constant [4 x i8] c\"%d\\0a\\00\"");
      print("@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"");

      print("define void @print_int(i32 %i) {");
      print("\t %_str = bitcast [4 x i8]* @_cint to i8*");
      print("\t call i32 (i8*, ...) @printf(i8* %_str, i32 %i)");
      print("\t ret void");
      print("}");
      print("");

      print("define void @throw_oob() {");
      print("\t %_str = bitcast [15 x i8]* @_cOOB to i8*");
      print("\t call i32 (i8*, ...) @printf(i8* %_str)");
      print("\t call void @exit(i32 1)");
      print("\t ret void");
      print("}");
      print("");

      n.f0.accept(this, argu);
      n.f1.accept(this, argu); 
      return null;
   }

       /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */

   //TOTALY FINE
   public String visit(MainClass n, Void argu) throws Exception {


        String classname = n.f1.accept(this, argu);
        current_class = table.FindClass(classname);

        curren_method = current_class.GetMethod("main");
        
        print("define i32 @main() {");

         String id = n.f11.accept(this, argu);

         n.f14.accept(this, argu);
         n.f15.accept(this, argu);

         print("\tret i32 0\n}\n");

         current_class = null;
         curren_method = null;
         return classname;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   //TOTALY FINE
   public String visit(ClassDeclaration n, Void argu) throws Exception {
       
       String classname = n.f1.accept(this, argu);

        current_class = table.FindClass(classname);
        
      //   n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        return classname;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    //TOTALY FINE
   public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
      String classname = n.f1.accept(this, argu);

      current_class = table.FindClass(classname);

      n.f3.accept(this, argu);
      // n.f5.accept(this, argu);
      n.f6.accept(this, argu);

        return classname;
   }

      /**
    * f0 -> <IDENTIFIER>
    */
    //TOTALY FINE
   public String visit(Identifier n, Void argu) throws Exception {
        String name = n.f0.tokenImage;
        return name;

   }

    /**
        * f0 -> Type()
        * f1 -> Identifier()
        * f2 -> ";"
        */
        //TOTALY FINE
    public String visit(VarDeclaration n, Void argu) throws Exception {
            String type = n.f0.accept(this, argu);
            String name = n.f1.accept(this, argu);

            // print(name + " " + type);

            if(!type.equals("i1") && !type.equals("i32") && !type.equals("i32*"))
               type = "i8*";

            this.var_type.put(name, type);

            // System.out.println(name + " " + type);

            print("\t%" + name + " = alloca " + type);

            return type;
    }

    /**
        * f0 -> "public"
        * f1 -> Type()
        * f2 -> Identifier()
        * f3 -> "("
        * f4 -> ( FormalParameterList() )?
        * f5 -> ")"
        * f6 -> "{"
        * f7 -> ( VarDeclaration() )*
        * f8 -> ( Statement() )*
        * f9 -> "return"
        * f10 -> Expression()
        * f11 -> ";"
        * f12 -> "}"
        */
        //TOTALY FINE
  public String visit(MethodDeclaration n, Void argu) throws Exception {

         String type = n.f1.accept(this, argu);

         String method_name = n.f2.accept(this, argu);

         curren_method = current_class.GetMethod(method_name);

         LinkedList<String> args = curren_method.GetArgs();
         LinkedList<String> args_types = curren_method.GetArgsTypes(); 

         String input_t = "" , input = "";

         if(args.size() > 0){
            String arg_t = args_types.get(0);
            if(arg_t.equals("int")){
               input_t = "i32";
            }
            else if(arg_t.equals("integer array")){
               input_t = "i32*";
            }
            else if(arg_t.equals("boolean")){
               input_t = "i1";
            }
            else if(arg_t.equals("boolean array")){
               input_t = "i8*";
            }
            else{
               input_t = "i8*";
            }
            
            input = ", " + input_t + " %." + args.get(0) + " ";

            for(int i = 1; i < args.size(); i++){
               input = input + ",";
               arg_t = args_types.get(i);
               if(arg_t.equals("int")){
                  input_t = "i32";
               }
               else if(arg_t.equals("integer array")){
                  input_t = "i32*";
               }
               else if(arg_t.equals("boolean")){
                  input_t = "i1";
               }
               else if(arg_t.equals("boolean array")){
                  input_t = "i8*";
               }
               else{
                  input_t = "i8*";
               }
               input = input + input_t + " %." + args.get(i) + " ";
            }
         }


         if(!type.equals("i1") && !type.equals("i32*") && !type.equals("i8*") && !type.equals("i32"))
            type = "i8*";

         print("define " + type + " @" + current_class.GetName() + "." + method_name + "( i8* %this" + input +"){");

         n.f4.accept(this, argu);
         n.f7.accept(this, argu);
         n.f8.accept(this, argu);

        String return_type = n.f10.accept(this, argu);

        String type_r = curren_method.LLVMGetType();

        print("\tret " + type_r + " " + return_type);

        print("}");

        return return_type;
   }


   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
    //TOTALY FINE
   public String visit(FormalParameterList n, Void argu) throws Exception {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return "ok";
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    //TO DO
   public String visit(FormalParameter n, Void argu) throws Exception {
      String type = n.f0.accept(this, argu);
      String name = n.f1.accept(this, argu);
      // print(name + " " + type);
      if(!type.equals("i1") && !type.equals("i32") && !type.equals("i32*"))
         type = "i8*";
      this.var_type.put(name, type);
      print("\t%" + name + " = alloca " + type);
      print("\tstore " + type + " %." + name + ", " + type + "* %" + name); //+ new var
      return type;
   }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
    //TO DO
   public String visit(FormalParameterTail n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
    //TO DO
   public String visit(FormalParameterTerm n, Void argu) throws Exception {
      return n.f1.accept(this, argu);
   }

     /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    //TOTALY FINE
  public String visit(Type n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "int"
    */
    //TOTALY FINE
   public String visit(IntegerType n, Void argu) throws Exception {
      return "i32";
   }

    /**
    * f0 -> "boolean"
    */
    //TOTALY FINE
   public String visit(BooleanType n, Void argu) throws Exception {
      return "i1";
   }

   /**
    * f0 -> BooleanArrayType()
    *       | IntegerArrayType()
    */
    //TOTALY FINE
   public String visit(ArrayType n, Void argu) throws Exception {
      return "i32*";
   }

/**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
    //TOTALY FINE
   public String visit(Statement n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

 /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
    //TOTALY FINE
   public String visit(Block n, Void argu) throws Exception {
      return n.f1.accept(this, argu);
   }

/**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    //TO DO
   public String visit(AssignmentStatement n, Void argu) throws Exception { 
      String id = n.f0.accept(this, argu);
      // print("AAA" + id);
      String expression = n.f2.accept(this, argu);
      String NewVar = null;

      // print("EXP  " + expression + " ID " + id);
      
      String type = null, return_string = null;

      if(curren_method != null){
         if(curren_method.Search(id)){
            return_string = curren_method.FieldType(id);
         }
         else{
            return_string = curren_method.TakeArgType(id);
         }
      }
      else{
         return_string = current_class.FieldType(id);
      }



      if(return_string == null ){
         type = this.var_type.get(id);
      }
      else if(return_string.equals("int")){
            type = "i32";
      }
      else if(return_string.equals("int[]")){
            type = "i32*";
      }
      else if(return_string.equals("boolean")){
            type = "i1";
      }
      else if(return_string.equals("boolean[]")){
            type = "i8*";
      }
      else{
         type = this.var_type.get(expression);
      }

      if(type == null){
         type = "i8*";
      }


      if(curren_method.Search(id)){
         NewVar = "%" + id;
         this.var_type.put(NewVar, type);
         type = this.var_type.get(NewVar);
      }
      else if(curren_method.SearchArgs(id)){
         NewVar = "%" + id;
         this.var_type.put(NewVar, type);  
         type = this.var_type.get(NewVar);
      }
      else if(this.var_type.containsKey(id)){
         // print("VAR " + id);
         NewVar = id;
         this.var_type.put(NewVar, type);
         type = this.var_type.get(NewVar);
         NewVar = "%" + id;
      }
      else{
         String NewVar1 = CreateVariable();
         String NewVar2 = CreateVariable();

         this.var_type.put(NewVar1, type);
         this.var_type.put(NewVar2, type);

         String output = current_class.LoadField(id, this.offsets, NewVar1, NewVar2, this.var_type);
         print(output);
         NewVar = NewVar2;
         type = this.var_type.get(NewVar);
      }


      // print("TT " + expression);
      int exp;

      try{
         exp = Integer.parseInt(expression);
         type = "i32";
         this.var_type.put(NewVar, type);
         print("\tstore " + "i32" + " " + exp + " ," + "i32" + "*" + NewVar);
      }
      catch(NumberFormatException e){
         if(expression.equals("true")){
            expression = "1";
            type = "i1";
            this.var_type.put(NewVar, type);
         }
         if(expression.equals("false")){
            expression = "0";
            type = "i1";
            this.var_type.put(NewVar, type);
         }

         // print(NewVar + " " + type);

         print("\tstore " + type + " " + expression + " ," + type + "*" + NewVar);
      }

      // if(Integer.parseInt(expression).isTrue()){
      //    print("NUM");
      // }

      return NewVar;

   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
   //TO DO
   public String visit(ArrayAssignmentStatement n, Void argu) throws Exception {

      String name = n.f0.accept(this, argu);

      String NewVar = CreateVariable();


      if(curren_method.Search(name)){
         String load = curren_method.LoadVar(name, NewVar, this.var_type);
         print(load);
      }
      else if(curren_method.SearchArgs(name)){
         String load = curren_method.LoadVar(name, NewVar, this.var_type);
         print(load); 
      }
      else if(this.var_type.containsKey(name)){
            String load = curren_method.LoadVar(name, NewVar, this.var_type);
            print(load); 
      }
      else{
         String NewVar1 = CreateVariable();
         String NewVar2 = CreateVariable();
         String NewVar3 = CreateVariable();
         String load = current_class.GetField(name, this.offsets, NewVar1, NewVar2, NewVar3, this.var_type);
         print(load);
         NewVar = NewVar3;
      }

      String NewVar1 = CreateVariable();
      String NewVar2 = CreateVariable();
      String NewVar3 = CreateVariable();
      String NewVar4 = CreateVariable();

      String label1 = CreateLabel();
      String label2 = CreateLabel();
      String label3 = CreateLabel();

      String exp1 = n.f2.accept(this, argu);
      String exp2 = n.f5.accept(this, argu);

      print("\t" + NewVar1 + " = load i32, i32*" + NewVar);
      print("\t" + NewVar2 + " = icmp ult i32 " + exp1 + ", " + NewVar1);
      print("\tbr i1 " + NewVar2 + ", label %" + label1 + ", label %" + label2);
      print("\n" + label1 + ":\n");
      print("\t" + NewVar3 + " = add i32 " + exp1 + ", 1");
      print("\t" + NewVar4 + " = getelementptr i32, i32* " + NewVar + ", i32 " + NewVar3);
      print("\tstore i32 " + exp2 + ", i32* " + NewVar4);
      print("\tbr label %" + label3);
      print("\n" + label2 + ":\n");
      print("\tcall void @throw_oob()");
      print("\tbr label %" + label3);
      print("\n" + label3 + ":\n");

      return NewVar4;

   }

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
    //TOTALY FINE
   public String visit(IfStatement n, Void argu) throws Exception {
      String type = n.f2.accept(this, argu);

      String label1 = CreateLabel();
      String label2 = CreateLabel();
      String label3 = CreateLabel();

      print("\tbr i1 " + type + ", label %" + label1 + ", label %" + label2);

      print(label1 + ":");

      n.f4.accept(this, argu);
 
      print("\tbr label %" + label3 + "\n");

      print(label2 + ":");

      n.f6.accept(this, argu);
            
      print("\tbr label %" + label3 + "\n");

      print(label3 + ":");

      return type;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    //TOTALY FINE
   public String visit(WhileStatement n, Void argu) throws Exception {

      String label1 = CreateLabel();
      String label2 = CreateLabel();
      String label3 = CreateLabel();

      print("\tbr label %" + label1 + "\n");
      print(label1 + ":");

      String exp = n.f2.accept(this, argu);

      print("\tbr i1 " + exp + ", label %" + label2 + ", label %" + label3);

      print(label2 + ":");

      n.f4.accept(this, argu);
 
      print("\tbr label %" + label1 + "\n");
      print(label3 + ":");

      return exp;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    //TOTALY FINE
   public String visit(PrintStatement n, Void argu) throws Exception {
      String type = n.f2.accept(this, argu);

      print("\tcall void (i32) @print_int(i32 " + type + " )" );

      return type;
   }

/**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | Clause()
    */
    //TOTALY FINE
   public String visit(Expression n, Void argu) throws Exception {
      String exp = n.f0.accept(this, argu);
      return exp;
   }

/**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    //TOTALY FINE
   public String visit(AndExpression n, Void argu) throws Exception {
      String NewVar = CreateVariable();
      this.var_type.put(NewVar, "i1");

      String label1 = CreateLabel();
      String label2 = CreateLabel();
      String label3 = CreateLabel();
      String label4 = CreateLabel();


      print("\tbr label %" + label1 +"\n");
      print(label1 +":\n");
      print("\tbr i1 1,  label %" + label2 + ", label %" + label4);
      print(label2 +":\n");

      String clause1 = n.f0.accept(this, argu);
      String clause2 = n.f2.accept(this, argu);

      print("\tbr label %" + label3);
      print(label3 + ":\n");

      print("\tbr label %" + label4);
      print(label4 + ":\n");

      print("\t" + NewVar + " = phi i1 [" + clause1 + ", %" + label1 + "], [" + clause2 + ", %" + label3 + "]");
      
      return NewVar;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    //ok
   public String visit(CompareExpression n, Void argu) throws Exception {
      String name = n.f0.accept(this,argu);
      String name2 = n.f2.accept(this,argu);

      String NewVar = CreateVariable();
      this.variables.put(name, NewVar);

      print("\t" + NewVar + " = icmp slt i32 " + name + ", " + name2);

      return NewVar;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    //TOTALY FINE
   public String visit(PlusExpression n, Void argu) throws Exception {
      String name = n.f0.accept(this,argu);
      String name2 = n.f2.accept(this,argu);

      String NewVar = CreateVariable();
      this.var_type.put(NewVar, "i32");

      print("\t" + NewVar + " = add i32 " + name + ", " + name2);
      return NewVar;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    //TOTALY FINE
   public String visit(MinusExpression n, Void argu) throws Exception {

      String name = n.f0.accept(this,argu);
      String name2 = n.f2.accept(this,argu);

      String NewVar = CreateVariable();
      this.var_type.put(NewVar, "i32");

      print("\t" + NewVar + " = sub i32 " + name + ", " + name2);
      return NewVar;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    //TOTALY FINE
   public String visit(TimesExpression n, Void argu) throws Exception {
      String name = n.f0.accept(this,argu);
      String name2 = n.f2.accept(this,argu);
      
      String NewVar = CreateVariable();
      this.var_type.put(NewVar, "i32");

      print("\t" + NewVar + " = mul i32 " + name + ", " + name2);

      return NewVar;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    //TOTALY FINE
   public String visit(ArrayLookup n, Void argu) throws Exception {
      String exp1 = n.f0.accept(this, argu);
      String exp2 = n.f2.accept(this, argu);
      
      String NewVar1 = CreateVariable();
      String NewVar2 = CreateVariable();
      String NewVar3 = CreateVariable();
      String NewVar4 = CreateVariable();
      String NewVar5 = CreateVariable();

      String label1 = CreateLabel();
      String label2 = CreateLabel();
      String label3 = CreateLabel();

      print("\t" + NewVar1 + " = load i32, i32* " + exp1);
      print("\t" + NewVar2 + " = icmp ult i32 " + exp2 +", " + NewVar1);
      print("\tbr i1 " + NewVar2 + ", label %" + label1 + ", label %" + label2);
      print("\n" + label1 + ":\n");
      print("\t" + NewVar3 + " = add i32 " + exp2 + ", 1");
      print(NewVar4 + " = getelementptr i32, i32* " + exp1 + ", i32 " + NewVar3);
      print("\t" + NewVar5 + " = load i32, i32* "+ NewVar4);
      print("\tbr label %" + label3);
      print("\n" + label2 +":\n");
      print("\tcall void @throw_oob()");
      print("\tbr label %" + label3);
      print("\n" + label3 +":\n");

      return NewVar5;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    //TOTALY FINE
   public String visit(ArrayLength n, Void argu) throws Exception {
      String exp = n.f0.accept(this, argu);
      String NewVar = CreateVariable();
      String NewVar2 = CreateVariable();

      print("\t" + NewVar + " = getelementptr i32, i32* " + exp + ", i32 0");
      print("\t" + NewVar2 + " = load i32, i32*" + NewVar);

      return NewVar2;

   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    //TO DO
   public String visit(MessageSend n, Void argu) throws Exception {
      String r1 = n.f0.accept(this, argu);
      String r2 = n.f2.accept(this, argu);
      String r3 = n.f4.accept(this, argu);

      Method m = this.table.FindMethod(r2);

      UserClasses user_cl = this.table.ClassByName(r2);

      // String type = curren_method.GetType();

      String type = m.GetType();

      String return_type;
      if(type.equals("int")){
         return_type = "i32";
      }
      else if(type.equals("boolean")){
         return_type = "i1";
      }
      else if(type.equals("int[]")){
         return_type = "i32*";
      }
      else{
         return_type = "i8*";
      }

      LinkedList<String> parms = m.GetArgsTypes();
      LinkedList<String> parms1 = m.GetArgs();

      String params = "";
      String s2 = "";
      String s3 = "";

      for(int i = 0; i < this.parameters.size(); i++){
         // print(parms.get(i));
         if(parms.get(i).equals("int")){
            params += ", i32";
            s2 += ", i32 ";
            s3 += ", i32 ";
         }
         else if(parms.get(i).equals("integer array")){
            params += ", i32*";
            s2 += ", i32* ";
            s3 += ", i32* ";
         }
         else if(parms.get(i).equals("boolean")){
            params += ", i1";
            s2 += ", i1 ";
            s3 += ", i1 ";
         }
         else if(parms.get(i).equals("boolean array")){
            params += ", i8*";
            s2 += ", i8* ";
            s3 += ", i8* ";
         }
         else{
            params += ", i8*";
            s2 += ", i8* ";
            s3 += ", i8* ";            
         }
         s2 += this.parameters.get(i);
      }

      this.parameters.clear();

      String var1 = CreateVariable();
      String var2 = CreateVariable();
      String var3 = CreateVariable();
      String var4 = CreateVariable();
      String var5 = CreateVariable();
      String var6 = CreateVariable();
      
      String method_of_class = user_cl.GetName() + "." + r2;

      int of = this.offsets.GetOffset(method_of_class);
      print("\t" + var1 + " = bitcast i8* " + r1 + " to i8***"); //NOO
      print("\t" + var2 + " = load i8**, i8*** " + var1);
      // print("OF " + of);
      // print(method_of_class);
      print("\t" + var3 + " = getelementptr i8*, i8** " + var2 + ", i32 " + of/8);
      print("\t" + var4 + " = load i8*, i8** " + var3);
      print("\t" + var5 + " = bitcast i8* " + var4 + " to " + return_type + "(i8* " + s3 + ")*"); //NOO

      this.var_type.put(var6, return_type);
      this.var_type.put(var5, return_type);

      // print(r1 + " " + s2);
      print("\t" + var6 + " = call " + return_type + " " + var5 + " (i8* " + r1 +  s2 + ")");
      return var6;
   }

   /**
    * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
    //TOTALY FINE
   public String visit(Clause n, Void argu) throws Exception {
      String type = n.f0.accept(this, argu);
      return type;
   }

/**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
    //TO DO
   public String visit(PrimaryExpression n, Void argu) throws Exception {
      String name = n.f0.accept(this, argu);
      if(n.f0.which == 3){
         if(curren_method.Search(name)){
            String NewVar = CreateVariable();
            String load = curren_method.LoadVar(name, NewVar, this.var_type);
            print(load);
            return NewVar;
         }
         else if(curren_method.SearchArgs(name)){
            String NewVar = CreateVariable();
            String load = curren_method.LoadVar(name, NewVar, this.var_type);
            print(load); 
            return NewVar;
         }
         else if(this.var_type.containsKey(name)){
            String NewVar = CreateVariable();
            String load = curren_method.LoadVar(name, NewVar, this.var_type);
            print(load); 
            return NewVar;
         }
         else{
            String NewVar1 = CreateVariable();
            String NewVar2 = CreateVariable();
            String NewVar3 = CreateVariable();
            // print("AA" + name);
            String load = current_class.GetField(name, this.offsets, NewVar1, NewVar2, NewVar3, this.var_type);
            print(load);
            return NewVar3;
         }
      }
      return name;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
    //TOTALY FINE
   public String visit(IntegerLiteral n, Void argu) throws Exception {
      String id = n.f0.toString();

      // String NewVar = CreateVariable();
      // this.var_type.put(id, "i32");
      
      // print("\t" + NewVar + " = add i32 0, " + id);

      return id;
   }

   /**
    * f0 -> "true"
    */
    //TOTALY FINE
   public String visit(TrueLiteral n, Void argu) throws Exception {
      return "true";
   }

   /**
    * f0 -> "false"
    */
    //TOTALY FINE
   public String visit(FalseLiteral n, Void argu) throws Exception {
      return "false";
   }

   /**
    * f0 -> "this"
    */
    //TOTALY FINE 
    //PROBABLY ADD IT TO PARMS
   public String visit(ThisExpression n, Void argu) throws Exception {
      return "%this";
   }

   /**
    * f0 -> BooleanArrayAllocationExpression()
    *       | IntegerArrayAllocationExpression()
    */
    //TO DO
   public String visit(ArrayAllocationExpression n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "new"
    * f1 -> "boolean"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    // TO DO
   public String visit(BooleanArrayAllocationExpression n, Void argu) throws Exception {
       String type = n.f3.accept(this, argu);
       
       String label1 = CreateLabel();
       String label2 = CreateLabel();

       String NewVar = CreateVariable();
       String NewVar2 = CreateVariable();
       String NewVar3 = CreateVariable();
       String NewVar4 = CreateVariable();

      print("\t" + NewVar + " = icmp slt i32 " + type + ", 0");
      print("\t br i1 " + NewVar + ", label %" + label1 + ", label %" + label2);
      print(label1 + ":\n");
      print("\tcall void @throw_oob()");
      print("\tbr label %" + label2);
      print(label2 + ":\n");
      print("\t" + NewVar2 + " = add i32 " + type +", 1");
      print("\t" + NewVar3 + " = call i8* @calloc(i32 4, i32 " + NewVar2 +")");
      print("\t" + NewVar4 + " = bitcast i8* " + NewVar3 + " to i32*"); //NOO
      print("\tstore i32 " + type + ", i32* " + NewVar4);

       return NewVar4;
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    //TO DO
   public String visit(IntegerArrayAllocationExpression n, Void argu) throws Exception {
       String type = n.f3.accept(this, argu);
       
       String label1 = CreateLabel();
       String label2 = CreateLabel();

       String NewVar = CreateVariable();
       String NewVar2 = CreateVariable();
       String NewVar3 = CreateVariable();
       String NewVar4 = CreateVariable();

      print("\t" + NewVar + " = icmp slt i32 " + type + ", 0");
      print("\t br i1 " + NewVar + ", label %" + label1 + ", label %" + label2);
      print(label1 + ":\n");
      print("\tcall void @throw_oob()");
      print("\tbr label %" + label2);
      print(label2 + ":\n");
      print("\t" + NewVar2 + " = add i32 " + type +", 1");
      print("\t" + NewVar3 + " = call i8* @calloc(i32 4, i32 " + NewVar2 +")");
      print("\t" + NewVar4 + " = bitcast i8* " + NewVar3 + " to i32*"); //NOO
      this.var_type.put(NewVar4, "i32*");
      print("\tstore i32 " + type + ", i32* " + NewVar4);

       return NewVar4;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    // TO DO
   public String visit(AllocationExpression n, Void argu) throws Exception {
      String classname = n.f1.accept(this, argu);

      String NewVar1 = CreateVariable();
      String NewVar2 = CreateVariable();
      String NewVar3 = CreateVariable();

      UserClasses class_ = this.table.FindClass(classname);

      int field_size = this.offsets.SizeOffset(classname);
      UserClasses parent = class_.GetParent();

      int size = this.offsets.Size(classname);

      if(size == 0 && parent != null){
            size = this.offsets.Size(parent.GetName());
      }

      if(class_.GetVtable())
         field_size += 8;

      this.var_type.put(NewVar1, "i8*");

      // print(classname + " " + field_size);

      // print("CLL " + classname + " " + size + " " + field_size);

      print("\t" + NewVar1 +" = call i8* @calloc(i32 1, i32 " + field_size + ")"); //+ object size
      print("\t" + NewVar2 + " = bitcast i8* " + NewVar1 + " to i8***"); //NOO
      print("\t" + NewVar3 + " = getelementptr [" + size + " x i8*], [" + size + " x i8*]* @." + classname + "_vtable, i32 0, i32 0");
      print("\tstore i8** " + NewVar3 + ", i8*** " + NewVar2 );

      return NewVar1;
   }

   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
    //TOTALY FINE
   public String visit(NotExpression n, Void argu) throws Exception {

      String expression = n.f1.accept(this, argu);
      String NewVar = CreateVariable();
      
      this.var_type.put(NewVar, "i1");
      
      print("\t" + NewVar + " = xor i1 1,  " + expression);
      
      return expression;
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    //TOTALY FINE
   public String visit(BracketExpression n, Void argu) throws Exception {
      String type = n.f1.accept(this, argu);
      return type;
   }

  /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
    //TOTALY FINE
   public String visit(ExpressionList n, Void argu) throws Exception {
         
         String r1 = n.f0.accept(this, argu);

         this.parameters.add(r1);
         
         String r2 = n.f1.accept(this, argu);

         return r1;
   }

   /**
    * f0 -> ( ExpressionTerm() )*
    */
    //TOTALY FINE
   public String visit(ExpressionTail n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
    //TOTALY FINE
   public String visit(ExpressionTerm n, Void argu) throws Exception {
      String r =  n.f1.accept(this, argu);
      this.parameters.add(r);
      return r;
   }
      /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public String visit(TypeDeclaration n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

}