import syntaxtree.*;
import visitor.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

class MySecondVisitor extends GJDepthFirst<String, Void>{

   boolean flag = true; //true means class and false means method
   Method curren_method;
   UserClasses current_class;
   SymbolTable table;

   public MySecondVisitor(SymbolTable t){
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
        current_class = table.FindClass(classname);
         current_class.YesVtable();
        curren_method = current_class.GetMethod("main");
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        current_class = null;
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

        current_class = table.FindClass(classname);

         current_class.YesVtable();

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

      //   current_class = null;
    
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

       current_class = table.FindClass(classname);
      
         current_class.YesVtable();

        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
    
      //   current_class = null;
        return classname;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Void argu) throws Exception {
       String type = n.f0.accept(this, argu);

       if(!type.equals("int") && !type.equals("boolean") && !type.equals("integer array") && !type.equals("boolean array") && !table.Search(type)){
            throw new Exception("There is not such a var type");
       }
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
   public String visit(MethodDeclaration n, Void argu) throws Exception {
        String method_name = n.f2.accept(this, argu);

        curren_method = current_class.GetMethod(method_name);

        String type = n.f1.accept(this, argu);

        if(!type.equals("int") && !type.equals("boolean") && !type.equals("integer array") && !type.equals("boolean array") && !table.Search(type)){
            throw new Exception("There is not such a return type");
        }

        n.f8.accept(this, argu);

        String return_type = n.f10.accept(this, argu);

        if(!type.equals(return_type)){
            throw new Exception("The return type is not right");
        }

        String type_r = curren_method.GetType();

      //   curren_method = null;
        return type_r;
   }

      /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Identifier n, Void argu) throws Exception {
        String name = n.f0.tokenImage;
        if(curren_method != null){
            if(curren_method.Search(name)){
                return curren_method.FieldType(name);
            }
            if(curren_method.SearchArgs(name)){
                return curren_method.TakeArgType(name);
            }
        }
        if(current_class != null){
            if(current_class.SearchField(name)){
                return current_class.FieldType(name);
            }
        }    
        return name;

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
      return "int";
   }

    /**
    * f0 -> "boolean"
    */
   public String visit(BooleanType n, Void argu) throws Exception {
      return "boolean";
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

   /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
   public String visit(Statement n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

 /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public String visit(Block n, Void argu) throws Exception {
      return n.f1.accept(this, argu);
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
   public String visit(ArrayAssignmentStatement n, Void argu) throws Exception {
      n.f0.accept(this, argu);
      String type = n.f2.accept(this, argu);
      if(!type.equals("int")){
         throw new Exception("This is a wrong index");
      }
      n.f5.accept(this, argu);
      return type;
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
   public String visit(IfStatement n, Void argu) throws Exception {
      String type = n.f2.accept(this, argu);
      if(!type.equals("boolean")){
         throw new Exception("This is a wrong statement in if");
      }
      n.f4.accept(this, argu);
      n.f6.accept(this, argu);
      return type;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public String visit(WhileStatement n, Void argu) throws Exception {
      String type = n.f2.accept(this, argu);
      if(!type.equals("boolean")){
         throw new Exception("This is a wrong statement in while loop");
      }
      n.f4.accept(this, argu);
      return type;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public String visit(PrintStatement n, Void argu) throws Exception {
      String type = n.f2.accept(this, argu);
      if(!type.equals("int") && !type.equals("integer array")){
         throw new Exception("This is a wrong print statement");
      }
      return type;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, Void argu) throws Exception { //to do
        String type1 = n.f0.accept(this,argu);
        String type2 = n.f2.accept(this,argu);

        if(type2.equals("integer array") && (type1.equals("int") || type1.equals("integer array"))){
           return type1;
        }
        else if(type2.equals("boolean array") && (type1.equals("boolean") || type1.equals("boolean array"))){
           return type1;
        }  
         else if(type1.equals("integer array") && (type2.equals("int") || type2.equals("integer array"))){
           return type1;
        }
        else if(type1.equals("boolean array") && (type2.equals("boolean") || type2.equals("boolean array"))){
           return type1;
        }    
        else if(type1.equals(type2)){
           return type1;
        }

         throw new Exception("This is a wrong assignment");

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
   public String visit(Expression n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

/**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
   public String visit(AndExpression n, Void argu) throws Exception {
      String type1 = n.f0.accept(this,argu);
      String type2 = n.f0.accept(this,argu);
      if(!type1.equals("boolean") || !type2.equals("boolean")){
         throw new Exception("This is a wrong and expression");
      }
      return "boolean";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public String visit(CompareExpression n, Void argu) throws Exception {
      String type1 = n.f0.accept(this,argu);
      String type2 = n.f0.accept(this,argu);
      if(!type1.equals("int") || !type2.equals("int")){
         throw new Exception("This is a wrong compare expression");
      }
      return "boolean";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public String visit(PlusExpression n, Void argu) throws Exception {
      String type1 = n.f0.accept(this,argu);
      String type2 = n.f0.accept(this,argu);
      if(!type1.equals("int") || !type2.equals("int")){
         throw new Exception("This is a wrong plus expression");
      }
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public String visit(MinusExpression n, Void argu) throws Exception {

      String type1 = n.f0.accept(this,argu);
      String type2 = n.f0.accept(this,argu);
      if(!type1.equals("int") || !type2.equals("int")){
         throw new Exception("This is a wrong mimus expression");
      }
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public String visit(TimesExpression n, Void argu) throws Exception {
      String type1 = n.f0.accept(this,argu);
      String type2 = n.f0.accept(this,argu);
      if(!type1.equals("int") || !type2.equals("int")){
         throw new Exception("This is a wrong times expression");
      }
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public String visit(ArrayLookup n, Void argu) throws Exception {
      String array = n.f0.accept(this, argu);
      if(!array.equals("integer array") && !array.equals("boolean array")){
         throw new Exception("This is not an array");
      }
      String index = n.f2.accept(this, argu);
      if(!index.equals("int")){
         throw new Exception("This is not a right index");
      }
      //here should be a check for the index to be at most the size of the array - 1
      if(array.equals("integer array")){
         return "int";
      }
      return "boolean";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public String visit(ArrayLength n, Void argu) throws Exception {
      String array = n.f0.accept(this, argu);
      if(!array.equals("integer array") || !array.equals("boolean array")){
         throw new Exception("This is not an array");
      }
      String length = n.f2.accept(this, argu);
      return length;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public String visit(MessageSend n, Void argu) throws Exception {
      String r1 = n.f0.accept(this, argu);
      String r2 = n.f2.accept(this, argu);
      String r3 = n.f4.accept(this, argu);
      String type = curren_method.GetType();
      UserClasses classes = table.FindClass(r1);
      Method method = classes.GetMethod(r2);
      return method.GetType();
   }

   /**
    * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
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
   public String visit(PrimaryExpression n, Void argu) throws Exception {
        String type = n.f0.accept(this, argu);
        return type;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, Void argu) throws Exception {
      return "int";
   }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, Void argu) throws Exception {
      return "boolean";
   }

   /**
    * f0 -> "false"
    */
   public String visit(FalseLiteral n, Void argu) throws Exception {
      return "boolean";
   }

   /**
    * f0 -> "this"
    */
   public String visit(ThisExpression n, Void argu) throws Exception {
      return current_class.GetName();
   }

   /**
    * f0 -> BooleanArrayAllocationExpression()
    *       | IntegerArrayAllocationExpression()
    */
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
   public String visit(BooleanArrayAllocationExpression n, Void argu) throws Exception {
       String type = n.f3.accept(this, argu);
       if(!type.equals("boolean")){
           throw new Exception("This boolean array has been declared wrong");
       }
       return type;
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(IntegerArrayAllocationExpression n, Void argu) throws Exception {
       String type = n.f3.accept(this, argu);
       if(!type.equals("int")){
           throw new Exception("This integer array has been declared wrong");
       }
       return type;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public String visit(AllocationExpression n, Void argu) throws Exception {
       String classname = n.f1.accept(this, argu);
       if(!table.ExistsClass(classname)){
           throw new Exception("This class has not been declared");
       }
       return classname;
   }

   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
   public String visit(NotExpression n, Void argu) throws Exception {

      n.f0.accept(this, argu);
      String type = n.f1.accept(this, argu);
      if(!type.equals("boolean"))
         throw new Exception("Not expression is not boolean");
      return "boolean";

   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public String visit(BracketExpression n, Void argu) throws Exception {
      
    String type = n.f1.accept(this, argu);
        
    return type;
   }

  /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
   public String visit(ExpressionList n, Void argu) throws Exception {
         String r1 = n.f0.accept(this, argu);
         String r2 = n.f1.accept(this, argu);
         return r1;
   }

   /**
    * f0 -> ( ExpressionTerm() )*
    */
   public String visit(ExpressionTail n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public String visit(ExpressionTerm n, Void argu) throws Exception {
      return n.f1.accept(this, argu);
   }


}