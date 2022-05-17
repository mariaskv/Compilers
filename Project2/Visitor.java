import syntaxtree.*;
import visitor.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

class MyVisitor extends GJDepthFirst<String, Void>{

   boolean flag = true; //true means class and false means method
   Method curren_method;
   UserClasses current_class;
   SymbolTable table;

   public MyVisitor(SymbolTable t){
      table = t;
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
   public String visit(MainClass n, Void argu) throws Exception {
     String classname = n.f1.accept(this, argu);
      UserClasses user_class = new UserClasses(classname);
         if(this.table.Search(classname)){
         throw new Exception("This class has already been declared");
      }
      current_class = user_class;
      Method m = new Method("main", "int");
      current_class.InsertMethod("main",m);
      this.table.Insert(classname, user_class);
      curren_method = m;
      n.f14.accept(this, argu);
      n.f15.accept(this, argu);
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
   public String visit(ClassDeclaration n, Void argu) throws Exception {
      String classname = n.f1.accept(this, argu);
      UserClasses user_class = new UserClasses(classname);
      current_class = user_class;
      flag = true;
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      if(this.table.Search(classname)){
         throw new Exception("This class has already been declared");
      }
      this.table.Insert(classname, user_class);
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
   public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
      String classname = n.f1.accept(this, argu);
      String parent_name = n.f3.accept(this, argu);
      if(this.table.Search(parent_name) == false){
         throw new Exception("Parent class has not been declared");
      }
      UserClasses user_class = new UserClasses(classname);
      user_class.parent_class = this.table.FindClass(parent_name);
      user_class.SetParentName(parent_name);
      current_class = user_class;
      flag = true;
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      if(this.table.Search(classname)){
         throw new Exception("This class has already been declared");
      }
      this.table.Insert(classname, user_class);
      UserClasses temp = this.table.SymbolTable.get(parent_name);
      return classname;
   }

      /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Identifier n, Void argu) throws Exception {
      return n.f0.tokenImage;
   }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public String visit(Type n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "int"
    */
   public String visit(IntegerType n, Void argu) throws Exception {
      n.f0.accept(this, argu);
      return "int";
   }

    /**
    * f0 -> "boolean"
    */
   public String visit(BooleanType n, Void argu) throws Exception {
      n.f0.accept(this, argu);
      return "boolean";
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Void argu) throws Exception {
      String type = n.f0.accept(this, argu);
      String variable = n.f1.accept(this, argu);
      if(flag == false){
         if(curren_method.Search(variable)){
            throw new Exception("This variable has already been declared in this function");
         }
         if(curren_method.SearchArgs(variable)){
            throw new Exception("This variable has already been declared in the args of this function");
         }
         curren_method.Insert(variable, type);
      }
      else{
         if(current_class.SearchField(variable)){
            throw new Exception("This variable has already been declared in this class");
         }
         current_class.InsertField(variable, type);   
      }
      return variable;
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
   public String visit(MethodDeclaration n, Void argu) throws Exception {
      n.f0.accept(this, argu);
      String type = n.f1.accept(this, argu);
      String id = n.f2.accept(this, argu);
      Method m = new Method(id, type);
      curren_method = m;
      flag = false;
      n.f4.accept(this, argu);
      n.f7.accept(this, argu);
      if(current_class.SearchMethod(id)){
         throw new Exception("This method has already been declared in this class");
      }
      if(current_class != null){
         String parent = current_class.GetParentName();
         if(parent != null){
            UserClasses user = table.FindClass(parent);
            if(user.SearchMethod(id)){
               Method temporary = user.GetMethod(id);
               if(curren_method.CheckMethods(temporary) == false){
                  throw new Exception("This method has been declared differently in parent class");
               }
            }
         }
      }
      current_class.InsertMethod(id, m);
      flag = true;
      return id;
   }

   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public String visit(FormalParameterList n, Void argu) throws Exception {
      String res = n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return res;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter n, Void argu) throws Exception {
      String type = n.f0.accept(this, argu);
      String name = n.f1.accept(this, argu);
      if(this.curren_method.SearchArgs(name))
         throw new Exception("That argument has been declared twice");
      this.curren_method.AddArg(name);
      this.curren_method.AddArgType(type);
      return type;
   }

   /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(BooleanArrayType n, Void argu) throws Exception {
      n.f0.accept(this, argu);
      return "boolean array";
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(IntegerArrayType n, Void argu) throws Exception {
      n.f0.accept(this, argu);
      return "integer array";
   }

}