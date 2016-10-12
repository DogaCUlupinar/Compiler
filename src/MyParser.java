

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

import java_cup.runtime.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
;

public class MyParser extends parser
{

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	MyParser (Lexer lexer, ErrorPrinter errors)
	{
		m_lexer = lexer;
		m_symtab = new SymbolTable ();
		m_errors = errors;
		m_nNumErrors = 0;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean
	Ok ()
	{
		return (m_nNumErrors == 0);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Symbol
	scan ()
	{
		Token		t = m_lexer.GetToken ();

		//	We'll save the last token read for error messages.
		//	Sometimes, the token is lost reading for the next
		//	token which can be null.
		m_strLastLexeme = t.GetLexeme ();

		switch (t.GetCode ())
		{
			case sym.T_ID:
			case sym.T_ID_U:
			case sym.T_STR_LITERAL:
			case sym.T_FLOAT_LITERAL:
			case sym.T_INT_LITERAL:
			case sym.T_CHAR_LITERAL:
				return (new Symbol (t.GetCode (), t.GetLexeme ()));
			default:
				return (new Symbol (t.GetCode ()));
		}
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	syntax_error (Symbol s)
	{
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	report_fatal_error (Symbol s)
	{
		m_nNumErrors++;
		if (m_bSyntaxError)
		{
			m_nNumErrors++;

			//	It is possible that the error was detected
			//	at the end of a line - in which case, s will
			//	be null.  Instead, we saved the last token
			//	read in to give a more meaningful error 
			//	message.
			m_errors.print (Formatter.toString (ErrorMsg.syntax_error, m_strLastLexeme));
		}
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	unrecovered_syntax_error (Symbol s)
	{
		report_fatal_error (s);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	DisableSyntaxError ()
	{
		m_bSyntaxError = false;
	}

	public void
	EnableSyntaxError ()
	{
		m_bSyntaxError = true;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public String 
	GetFile ()
	{
		return (m_lexer.getEPFilename ());
	}

	public int
	GetLineNum ()
	{
		return (m_lexer.getLineNumber ());
	}

	public void
	SaveLineNum ()
	{
		m_nSavedLineNum = m_lexer.getLineNumber ();
	}

	public int
	GetSavedLineNum ()
	{
		return (m_nSavedLineNum);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoProgramStart()
	{
		// Opens the global scope.
		m_symtab.openScope ();

		m_CodeGen.writeGlobalDef();
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoProgramEnd()
	{
		m_CodeGen.dispose();
		m_symtab.closeScope ();
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoExit(STO expr)
	{
		if (!(expr.getType() instanceof IntType)){
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error7_Exit, expr.getType().getName()));
			return;
		}
		
		m_CodeGen.writeExit(expr);
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoVarDecl (HashMap<String,STO> lstIDs, Type type, String status)
	{
	
		Iterator<Map.Entry<String, STO>> it = lstIDs.entrySet().iterator();

		while (it.hasNext()) {
					
		  Map.Entry<String, STO> entry = it.next();
		  String key = entry.getKey();
		  STO Value = entry.getValue();
		  
		  if (Value != null && Value instanceof ErrorSTO)
			  return ;
		  
		  HelperSTO help = (HelperSTO)Value;
		  STO optInit = help.getOptInit();
		  STO mod = help.getModifierSTO();
		  
		  if (optInit instanceof ErrorSTO){
			  return;
		  }
		  
	
			
			
		  if (mod instanceof ConstSTO) {
			  ConstSTO temp = (ConstSTO)mod;
			  
			  if (temp.getIsForArray()) {
				  
					//Array initial
				if(optInit != null) {
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, optInit.getType().getName()
											, type.getName()+"["+temp.getIntValue()+"]"));
					return ;
			
				}		
				  DoArrayDecl(lstIDs,type);
				  return ;
			  }
			  
			  if (temp.getIsForPointer()) {
				  
				  if (status != null && status.equals("true") && optInit!= null) {
						if (!(optInit instanceof ConstSTO)) {
							m_nNumErrors++;
							
							if (optInit.getName().equals("&") && optInit instanceof ExprSTO
									&& ((ExprSTO)optInit).getUnderName() != null) {
								m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, ((ExprSTO)optInit).getUnderName()));
							}else {
								m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, optInit.getName()));
							}
						}
				  }
				    
				  DoPointerDecl(lstIDs,type);
				  return ;
			  }
		  }
		  
			if (m_symtab.accessLocal (key) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, key));
			}
			
			if (optInit != null && type instanceof FunctionPointerType) {
				
				if (DoFuncPtrInitalCheck(optInit,type,key) == false) {
					m_nNumErrors++;
					
					if (type.getIsTypedef()) {
						m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, FunctionFactory(optInit),
								type.getName()));
					}else {
						m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, FunctionFactory(optInit),
									FunctionFactory_2((FunctionPointerType)type)));
					}
					return ;
							
				}
				else {
					
					if (optInit instanceof FuncSTO) {
						
						((FunctionPointerType)type).setBaseFunc((FuncSTO)optInit);
						
					}else if (optInit.getType() instanceof FunctionPointerType){
						
						FunctionPointerType funcTyp = (FunctionPointerType)optInit.getType();
						
						((FunctionPointerType)type).setBaseFunc(funcTyp.getBaseFunc());
					}
					
					VarSTO 		sto = new VarSTO (key,type);
					if(type.getLabelName() != null) {
						sto.setLabelName(type.getLabelName());
					}
			
					int offset = m_symtab.getLocalOffset();
					sto.setOffset(offset);
					m_symtab.insert (sto);
					
					if (m_symtab.getLevel() == 1 || sto.getIsStatic() == true) {// int x; global
						//sto.setIsGlobal();
						if (m_symtab.getLevel() == 1){
							sto.setIsGlobal();
						}

						if (sto.getIsStatic() == true){
							DoNameMangle(sto);
						}

						m_CodeGen.increaseIndent();
						m_CodeGen.writeUninitialVar(sto);
						m_CodeGen.decreaseIndent();
					}
					
					
					
					m_CodeGen.writeLoad(optInit,"%l1",0);
					m_CodeGen.writeStore(sto,null); // int x = foo ;
					
					
					return ;
				}
	
			}
			
			
			if(optInit != null ) {
				
				if (optInit.getType() instanceof StructType ||
						type instanceof StructType) {
					
					if (DoStructAssign(type, optInit.getType(), true) instanceof ErrorSTO) {
						return ;
					}				
					
				}
			}
			
			
			
			
			if (optInit instanceof FuncSTO) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, optInit.getName()
								, type.getName()));
				return ;
			}
			else if ( optInit != null && type.getClass() != (optInit.getType().getClass())) {
				if (!(type instanceof FloatType && optInit.getType() instanceof IntType)) {
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, optInit.getType().getName()
									, type.getName()));
					return ;
				}
			}
				

			VarSTO 		sto = new VarSTO (key,type);
			
			if (status != null && status.equals("true") 
								&& optInit != null ) {
				if (!(optInit instanceof ConstSTO)) {
					m_nNumErrors++;
					
					if (optInit.getName().equals("&") && optInit instanceof ExprSTO
							&& ((ExprSTO)optInit).getUnderName() != null) {
						m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, ((ExprSTO)optInit).getUnderName()));
					}else {
						m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, optInit.getName()));
					}
				}
			}
			
			
			if (status !=null && status.equals("true")) {
				sto.setIsStatic(true);
			}
			
			if (type.getIsTypedef() == true) {
				sto.setIsTypedef(true);
				
			}
			
			//sto.setOffset(offset);
			//sto.setLabelName("tmp1");
			m_symtab.insert (sto);
			
			
			if (optInit  == null) {
				if (m_symtab.getLevel() == 1 || sto.getIsStatic() == true) {// int x; global
					//sto.setIsGlobal();
					if (m_symtab.getLevel() == 1){
						sto.setIsGlobal();
					}
					
					if (sto.getIsStatic() == true){
						DoNameMangle(sto);
					}
					
					
					if (sto.getType() instanceof StructType){					
						int structSize = ((ConstSTO)DoSizeOf(sto)).getIntValue();
						m_CodeGen.increaseIndent();
						m_CodeGen.writeStruct(sto,structSize);
					}else{
						m_CodeGen.increaseIndent();
						m_CodeGen.writeUninitialVar(sto);
						m_CodeGen.decreaseIndent();
					}
					
				}else {// int x; local
					int offset = 0;
					
					if (sto.getType() instanceof StructType){					
						int structSize = ((ConstSTO)DoSizeOf(sto)).getIntValue();
						offset = m_symtab.getLocalOffset(structSize/4); //need to divide by 4 because this does not take number of bytes
				
						
					}else{
						offset = m_symtab.getLocalOffset();
					}
					
					sto.setOffset(offset);
				}
				
				
			}else {
				
				if (m_symtab.getLevel() == 1 || sto.getIsStatic() == true) {
					//sto.setIsGlobal();
					
					if (m_symtab.getLevel() == 1){
						sto.setIsGlobal();
					}
					
					if (sto.getIsStatic() == true){
						DoNameMangle(sto);
					}
					
					
					m_CodeGen.writeInitialVar(sto,optInit);
					
				}else {
					int offset = m_symtab.getLocalOffset();
					sto.setOffset(offset);
					// int x = 3; local
					if (optInit.getName().equals("Literal")) {
				/*		if (optInit.getType() instanceof IntType){
							m_CodeGen.writeInitLocalLiteral((ConstSTO)optInit,sto);
							
						}else if( optInit.getType() instanceof FloatType){
							//float x = 4.20 local
							m_CodeGen.writeInitLocalLiteral((ConstSTO)optInit,sto);
						}*/
						
						m_CodeGen.writeInitLocalLiteral((ConstSTO)optInit,sto);
						
					}else { // int x =y; local
						
						m_CodeGen.writeLoad(optInit,"%l1",0);
						m_CodeGen.writeStore(sto,null);
						
						//m_CodeGen.writeInitLocalVar(optInit.getName(),offset);
					}
				}
				
			}
			
			
		
		}
		
		//m_CodeGen.dispose();
		
/*
		for (int i = 0; i < lstIDs.size (); i++)
		{
			String id = lstIDs.(i);
		
			if (m_symtab.accessLocal (id) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id));
			}
			
			VarSTO 		sto = new VarSTO (id,type);
			
			if (status.equals("true")) {
				
			}
			m_symtab.insert (sto);
		}*/
	}
	
	
	void DoNameMangle(STO sto){
		String name = sto.getName();
		if (sto.getIsGlobal() != true){
			this.m_staticCounter++;
			name = name + Integer.toString(m_staticCounter);
			sto.setLabelName(name);
		}
			
	}
	
	
	void DoAutoDecl (String status , STO optInit, String name ,int flag) {
		
		STO var = null;

		if (flag == 1) {
			var = new ConstSTO(name,optInit.getType(),((ConstSTO)optInit).getIntValue());
		}else {
			var = new VarSTO(name, optInit.getType());
		}
		
		if (status != null && status.equals("true")) {
			var.setIsStatic(true);
		}
		
		
		
		if (m_symtab.getLevel() == 1 || var.getIsStatic() == true) {
			//sto.setIsGlobal();
			
			if (m_symtab.getLevel() == 1){
				var.setIsGlobal();
			}
			
			if (var.getIsStatic() == true){
				DoNameMangle(var);
			}
			
			m_CodeGen.writeInitialVar(var,optInit);				
			m_symtab.insert (var);
		
			return;
			
		}
			
		
		int offset = m_symtab.getLocalOffset();
		var.setOffset(offset);
		
		if (optInit.getName().equals("Literal")) {

			
			m_CodeGen.writeInitLocalLiteral((ConstSTO)optInit,var);
			
		}else { // int x =y; local
			
			m_CodeGen.writeLoad(optInit,"%l1",0);
			m_CodeGen.writeStore(var,null);

		}		
		
		m_symtab.insert (var);
	}
	
	
	void DoConstAutoDecl (String status , STO sto, String name) {
		DoAutoDecl (status, sto , name ,1);
		
	}
	
	
	
	boolean
	DoFuncPtrInitalCheck(STO exprSto, Type typ, String name) {
		
		if (exprSto instanceof ErrorSTO) {
			return false ;
		}
		
		VarSTO desSto = new VarSTO (name,typ);
		
		

		
		
		if (exprSto instanceof FuncSTO) {// x = foo
			return checkFunctionPtr2(desSto, exprSto);
		}
		else {//x = funcptr
			return checkFunctionPtr(desSto, exprSto);
		}
		
		
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	
	void
	DoArrayDecl(HashMap<String,STO> lstIDs, Type typ) {
		
		
		Iterator<Map.Entry<String, STO>> it = lstIDs.entrySet().iterator();

		while (it.hasNext()) {
			
			  Map.Entry<String, STO> entry = it.next();
			  String key = entry.getKey();
			  STO Value = entry.getValue();
			  
			  HelperSTO help = (HelperSTO)Value;
			  STO mod = help.getModifierSTO();
			  
			  ConstSTO Consto = (ConstSTO)mod;
			  
			  if (mod == null) {
				  VarSTO 		sto = new VarSTO (key,typ);
				  m_symtab.insert (sto);
				  return ;
			  }
			  ArrayType at = new ArrayType(key,Consto.getIntValue());
			  VarSTO 		sto = new VarSTO (key,at);
			  
			  sto.getType().setUnderType(typ);
			  at.setName(ArrayFactory(at));
			  sto.setIsAddressable(true);
			  sto.setIsModifiable(false);

			  if (m_symtab.getLevel() == 1 || sto.getIsStatic() == true) {// int x; global
				  sto.setIsGlobal();
			  }

			  m_symtab.insert (sto);
			  int offset = 0;
			  if (sto.getIsGlobal() == false && sto.getIsStatic() == false){
				  offset = at.getArraySize();
			  }
			  

			  if (sto.getType().getUnderType() instanceof StructType){
				  int size = ((ConstSTO)DoSizeOfType(sto.getType().getUnderType())).getIntValue();
				  m_CodeGen.writeArray(sto, size);  
				  offset = (at.getArraySize()*size/4);
			  }else{
				  m_CodeGen.writeArray(sto);  
			  }
			  
			  offset = m_symtab.getLocalOffset(offset);
			  sto.setOffset(offset);
					
		}
	
	}
	
	void
	DoPointerDecl(HashMap<String,STO> lstIDs, Type typ) {
		
		Iterator<Map.Entry<String, STO>> it = lstIDs.entrySet().iterator();
		

		while (it.hasNext()) {
			
			  Map.Entry<String, STO> entry = it.next();
			  String key = entry.getKey();
			  STO Value = entry.getValue();
			  
			  HelperSTO help = (HelperSTO)Value;
			  
			  STO optInit = help.getOptInit();
			  STO mod = help.getModifierSTO();
			  ConstSTO modd = (ConstSTO) mod;
			

			  //MIGHT HAVE TO CHANGE THIS, DEPENDS ON INITILIZATION IS A TRANSCENDENT PROPERTY 
			  if (optInit != null) {
				  if (DoPointerInitialCheck(typ,optInit, modd.getnumOfPointers()) == false) {
					  return ;
				  }
				  
			  }
			  
			  PointerType pt = null;
			  PointerType pt1 = null;
			  PointerType pre = null;
			  /* do it for every pointer to construct a pointer to pointer*/
			  if (modd == null) {
				  VarSTO 		sto = new VarSTO (key,typ);
				  m_symtab.insert (sto);
				  return ;
			  }
			
			  for (int i = 1; i <= modd.getnumOfPointers(); i++){

				  if (i == 1){
					  pt = new PointerType(key);
					  pt.setPointerTo(typ);
					  pt.setBaseType(typ);
					  pt.setnumOfPointers(i);
					  pre = pt;
				  }else{
					  
					  pt1 = new PointerType(key);
					  pt1.setPointerTo(pre);
					  pt1.setnumOfPointers(i);
					  pt1.setBaseType(typ);
					  pre = pt1;
					  pt1.setName(PointerFactory(pre));
				  }
				  
			  }
			  //UGLY
			  if (modd.getnumOfPointers() == 1){
				  pt1 = pt;
			  }
			  //System.out.println(pt1.getName());
			  pt1.setName(PointerFactory(pt1));
			  pt1.setnumOfPointers(modd.getnumOfPointers());
			  pt1.setBaseType(typ);
			  
			  pt1.setName(PointerFactory(pt1));
			  VarSTO 		sto = new VarSTO (key,pt1);
			 
			  
			//CHANGE2 from here
			  int offset = 0;
			  if (optInit  == null) { //int* x ; global
				  if (m_symtab.getLevel() == 1 || sto.getIsStatic() == true) {// int x; global
					  sto.setIsGlobal();
						  m_CodeGen.increaseIndent();
						  m_CodeGen.writeUninitialVar(sto);
						  m_CodeGen.decreaseIndent();
				  }else{ // int* x; local
					  offset = m_symtab.getLocalOffset();
				  }
				  
			  }	else {
				  
				  if (m_symtab.getLevel() == 1 || sto.getIsStatic() == true) {// int x; global
					  sto.setIsGlobal();
						  m_CodeGen.increaseIndent();
						  m_CodeGen.writeInitialVar(sto, optInit);
						  m_CodeGen.decreaseIndent();
				  }else{ // int* x; local
					  offset = m_symtab.getLocalOffset();
					  sto.setOffset(offset);
					  m_CodeGen.writeLoad(optInit, "%l1", 0);//QQQQQQ
					  m_CodeGen.writeStore(sto, "%l1" );//QQQQQQQ
				  }
						
			  }
			  
			  
			  sto.setOffset(offset);
			  m_symtab.insert (sto);
					
		}
	
	}
	
	boolean DoForPointer (STO sto) {
		
		if (sto instanceof ConstSTO) {
			
			ConstSTO cs = (ConstSTO)sto;
			if (cs.getIsForPointer())
				return true;
		}
		
		return false;
	}
	
	STO
	GetPointerExpr(Vector<String> pointerList) {
		
		if (pointerList.get(0).equals("1")){
	
			ConstSTO a = new ConstSTO("*");
			a.setIsForPointer();
			a.setnumOfPointers(pointerList.size());
			return a;
		
		} 
		return new ExprSTO("*");
			
	}
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	
	STO 
	DoNewCheck( STO sto) {
		
		if (sto instanceof ErrorSTO) {
			return new ErrorSTO("err");
		}
		
		if (!sto.isModLValue()) {
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error16_New_var);	
			return new ErrorSTO("err");
		}
		
		if (!(sto.getType() instanceof PointerType)) {
			m_nNumErrors++;	
			m_errors.print (Formatter.toString(ErrorMsg.error16_New, 
					sto.getType().getName()));
			return new ErrorSTO("err");
		}
		
		//CHANGE2 here
		int size = ((ConstSTO)DoSizeOfType(sto.getType())).getIntValue();
		m_CodeGen.writeNew(sto, size);
		//to here
				
		
		return null;
	}
	
	STO 
	DoDeleteCheck( STO sto) {
		
		if (sto instanceof ErrorSTO) {
			return new ErrorSTO("err");
		}
		
		
		if (!sto.isModLValue()) {
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error16_Delete_var);	
			return new ErrorSTO("err");
		}
		
		if (!(sto.getType() instanceof PointerType)) {
			m_nNumErrors++;	
			m_errors.print (Formatter.toString(ErrorMsg.error16_Delete, 
					sto.getType().getName()));
			return new ErrorSTO("err");
		}
		
		//CHANGE2 here
		m_CodeGen.writeDerefCheck(sto);
		m_CodeGen.writeDelete(sto);

		
		
		//to here
		
		return null;
	}
	
	
	
	
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoIndexCheck( STO sto) {
		
		if (sto instanceof ErrorSTO) {
			return new ErrorSTO("err");
		}
		
		
		if (!(sto.getType() instanceof IntType)) {
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error10i_Array, 
							sto.getType().getName()));
			return new ErrorSTO("err");
		}
		
		if (!(sto instanceof ConstSTO)) {

			m_nNumErrors++;
			m_errors.print (ErrorMsg.error10c_Array);	
			return new ErrorSTO("err");
		}
		
		ConstSTO a = (ConstSTO)sto;
		
		if (a.getIntValue() <= 0) {
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error10z_Array,
							a.getIntValue()));
			return new ErrorSTO("err");
		}
		
		a.setIsForArray();
		return a;
		
	}
	
	
	 
	String 
	DoInitialCheck( STO sto ) {
		
		if (sto instanceof ErrorSTO) {
			return "0";
		}
		
		
		int level = this.m_symtab.getLevel();
		
		if (level == 1) {
			
			if (sto instanceof FuncSTO ||
					sto.getType() instanceof FunctionPointerType) {
				return "1";
			}
			if (!(sto instanceof ConstSTO)) {
				
				m_nNumErrors++;
				
				if (sto.getName().equals("&") && sto instanceof ExprSTO
						&& ((ExprSTO)sto).getUnderName() != null) {
					m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, ((ExprSTO)sto).getUnderName()));
				}else {
					m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, sto.getName()));
				}
				
				return "0";
			}
		}
		
		return "1";
	}
	
	
	
	STO
	DoAddressCheck( STO varSto ){
		
		if (varSto instanceof ErrorSTO) {
			return new ErrorSTO("err");
		}
		
	
		if (varSto.getIsAddressable() == false ){
			
			m_nNumErrors++;
			
			if (varSto instanceof FuncSTO) {
				m_errors.print (Formatter.toString(ErrorMsg.error21_AddressOf, 
						varSto.getName()));
			}else {
				m_errors.print (Formatter.toString(ErrorMsg.error21_AddressOf, 
							varSto.getType().getName()));
			}
			return new ErrorSTO("address of");
			
		}else{
			//create a pointer to that type
			PointerType returnType = new PointerType("doesntmatter", 4);
			returnType.setPointerTo(varSto.getType());
			
			if (varSto.getType() instanceof PointerType){
				
				returnType.setnumOfPointers(((PointerType)(varSto.getType())).getnumOfPointers()+1);
				returnType.setBaseType(((PointerType)(varSto.getType())).getBaseType());
				
			}else{
				returnType.setBaseType(varSto.getType());
			}
			returnType.setName(PointerFactory(returnType));
			ExprSTO returnSTO = new ExprSTO( "&", returnType);
			returnSTO.setIsModLValue(false);
			returnSTO.setUnderName(varSto.getName());
			
			int offset = m_CodeGen.writeAddressOf(varSto);
			//varSto.setIsAddressOf(true); //might be sketch
			returnSTO.setIsAddressOf(true);
			returnSTO.setOffset(offset);
			return returnSTO; //YOU MIGHT HAVE TO CHANGE THIS
			//return varSto; //CHANGE2 this line

		
		}
	
		
	}
	
	
	boolean
	DoPointerInitialCheck(Type typ, STO exprSTO, int numPointers) {
		if (exprSTO instanceof ErrorSTO){
			return false;
		}
		
		PointerType temp = new PointerType("x");
		temp.setBaseType(typ);
		temp.setnumOfPointers(numPointers);
		
		
		if (exprSTO != null) {
			
				
			if (exprSTO.getType() instanceof NullPointerType) {
				return true;
			}
			
			//HAVE
			if (exprSTO.getName().equals("&")){
				if (((PointerType)exprSTO.getType()).getnumOfPointers() == numPointers) {
					return true;
				}
				
			}
			
			if (exprSTO.getType() instanceof ArrayType && numPointers == 1){
				
				ArrayType at = (ArrayType) exprSTO.getType();
				
				if (at.getUnderType() instanceof StructType) {
					
					if (DoStructEqualCheck(typ, at.getUnderType()) instanceof ErrorSTO){

						m_nNumErrors++;
						m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, 
								at.getUnderType().getName()+"["+at.getArraySize()+"]",PointerFactory(temp)));
						return false;
					}
					else {
						return true;
					}
					
					
				}else {
					
					if (at.getUnderType().getClass() == typ.getClass()) {
						return true;
					}else {
						
						m_nNumErrors++;
						m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, 
								at.getUnderType().getName()+"["+at.getArraySize()+"]",PointerFactory(temp)));
						return false;
					}
					
				}
			
			}else if (exprSTO.getType() instanceof ArrayType){
				//qqq
				
				if (exprSTO.getType().getUnderType() != null && 
						exprSTO.getType().getUnderType() instanceof PointerType) {
					
					if (numPointers != ((PointerType)exprSTO.getType().getUnderType()).getnumOfPointers()+1){
						m_nNumErrors++;
						return false;
					}
					else {
						return true;
					}
				}
			
			}
			
			
			if(exprSTO.getType() instanceof PointerType) {
				
				PointerType pt = (PointerType) exprSTO.getType();//pt ----> expr right
				if (pt.getnumOfPointers() != numPointers){

					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, 
									PointerFactory(pt),PointerFactory(temp)));
					return false;
				}
				
				

				if (typ instanceof StructType 
						|| pt.getBaseType() instanceof StructType) {


					if (DoStructEqualCheck(typ, pt.getBaseType()) instanceof ErrorSTO){

						m_nNumErrors++;
						m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, 
								PointerFactory(pt),PointerFactory(temp)));
						return false;
					}
					else {
						return true;
					}
				}


				if (pt.getBaseType().getClass() != (temp.getBaseType().getClass())) {
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
							PointerFactory(pt),PointerFactory(temp)));
					return false;
				}

				return true;
			}

			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, 
					exprSTO.getType().getName(),PointerFactory(temp)));

			return false;
			
		}
		
		return false;
	}
	

	STO
	DoArrowCheck(STO sto, String str) {
		
		if (sto instanceof ErrorSTO) {
			return new ErrorSTO("err");
		}
		
		
		Type typ = sto.getType();
		if(!(typ instanceof PointerType)) {
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error15_ReceiverArrow, 
							typ.getName()));
			return new ErrorSTO("x");
		}
		
		PointerType pt = (PointerType)typ;
		if (!(pt.getPointerTo() instanceof StructType)) {
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error15_ReceiverArrow, 
							pt.getPointerTo().getName()));
			return new ErrorSTO("x");
		}
		
		boolean flag = false;
		
		StructType st = (StructType) pt.getPointerTo();
		Vector<STO> fields = m_StructInfo.get(st.getName());
		STO returnSTO = null;
		
		
		int offsetFromStruct = 0;
		for (STO id : fields) {
			if (id.getName().equals(str)) {
				flag = true;
				returnSTO = id;
				break;
			}else{ 
				offsetFromStruct += ((ConstSTO)DoSizeOf(id)).getIntValue(); 
			}
			
		}
		if (flag == false) {
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error14f_StructExp, 
							str,st.getName()));
			return new ErrorSTO("x");
		}
		returnSTO.setIsGlobal(sto.getIsGlobal());
		if (sto.getIsGlobal() == true){
				returnSTO.setLabelName(sto.getName());
		}
		returnSTO.setExtraOffset(offsetFromStruct);
		returnSTO.setOffset(sto.getOffset());
		returnSTO.setNeedsExtraOffSet(true);
		returnSTO.setIsArrow(true);
		m_CodeGen.writeArrow(returnSTO);
		
		return returnSTO;
		
		
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	
	int
	DoInitialCheck2( STO sto ,String name) {
		if (sto instanceof ErrorSTO){
			return 0;
		}
		//if (!(sto instanceof ConstSTO)) {
		//	m_nNumErrors++;
		//	m_errors.print (Formatter.toString(ErrorMsg.error8b_CompileTime, name));
		//	return 0;
		//}
		return 1;
	}
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoExternDecl (Vector<String> lstIDs)
	{
		for (int i = 0; i < lstIDs.size (); i++)
		{
			String id = lstIDs.elementAt (i);

			if (m_symtab.accessLocal (id) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id));
			}

			VarSTO 		sto = new VarSTO (id);
			sto.setIsExtern(true);
			
			m_symtab.insert (sto);
		}
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoConstDecl (Vector<ConstSTO> lstIDs, Type typ, String status)
	{

		if (lstIDs == null)
			return ;
		
		for (int i = 0; i < lstIDs.size (); i++)
		{
			String id = lstIDs.elementAt (i).getName();
			boolean isLiteral = false;

			/*if (m_symtab.accessLocal (id) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString (ErrorMsg.redeclared_id, id));
			}*/
			
			ConstSTO 	sto = lstIDs.elementAt(i);
			if (sto.isModLValue() == false) {
				isLiteral = true;
			}
			sto.setIsAddressable(true);
			
			if (typ.getClass() != (sto.getType().getClass())) {
				if (!(typ instanceof FloatType && sto.getType() instanceof IntType)) {
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, sto.getType().getName()
									, typ.getName()));
					return ;
				}
			}
			
				
			sto.setType(typ);
			if (status != null && status.equals("true")) {
				sto.setIsStatic(true);
			}

			m_symtab.insert (sto);
			
			
			
			
			if (m_symtab.getLevel() == 1 || sto.getIsStatic() == true) {
				sto.setIsGlobal();
				m_CodeGen.writeInitialVar(sto,sto);
				
			}else {
				int offset = m_symtab.getLocalOffset();
				sto.setOffset(offset);
				// int x = 3; local
				if (isLiteral == true) {
					
					ConstSTO init = new ConstSTO("Literal",sto.getValue());
					init.setType(sto.getType());
					m_CodeGen.writeInitLocalLiteral(init,sto);
					
				}else { // int x =y; local
					
					m_CodeGen.writeLoad(sto,"%l1",0);
					m_CodeGen.writeStore(sto,null);
	
				}
			}
				
			
			
			
			
			
			
		}
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoTypedefDecl (Vector<String> lstIDs, Type typ)
	{

		for (int i = 0; i < lstIDs.size (); i++)
		{
			String id = lstIDs.elementAt (i);

			if (m_symtab.accessLocal (id) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id));
			}
			
			TypedefSTO 	sto = new TypedefSTO (id,typ);
			
			
			if (typ instanceof ArrayType) {

				sto.getType().setUnderType(typ.getUnderType());
			}
			
			if (typ instanceof StructType) {
				if (((StructType) typ).getBaseName() != null ) {
					((StructType)sto.getType()).setBaseName(((StructType) typ).getBaseName());
				}else {
					((StructType)sto.getType()).setBaseName(typ.getName());
				}
				
			}
			//int size = ((ConstSTO)DoSizeOfType(typ)).getIntValue();
			//sto.getType().setSize(size);
			m_symtab.insert (sto);
		}
	}

	
	Type
	GetArrOrPoint(STO exprSto , Type typ) {
		if (exprSto instanceof ConstSTO) {
			ConstSTO temp = (ConstSTO)exprSto;		
			
			if (temp.getIsForArray()) {
				
				if (typ instanceof StructType) {
					
					ArrayType at = new ArrayType("TYPEDEL",temp.getIntValue());
					at.setUnderType(typ);
					at.setName(ArrayFactory(at));
					return at;
				}else {
					ArrayType at = new ArrayType("TYPEDEL",temp.getIntValue());
					at.setUnderType(typ);
					at.setName(ArrayFactory(at));
					return at;
				}
			}
			if (temp.getIsForPointer()) {
				  PointerType pt = null;
				  PointerType pt1 = null;
				  PointerType pre = null;
				if (typ instanceof StructType) {
					
					  for (int i = 1; i <= temp.getnumOfPointers(); i++){

						  if (i == 1){
							  pt = new PointerType("x");
							  pt.setPointerTo(typ);
							  pt.setBaseType(typ);
							  pt.setnumOfPointers(i);
							  pre = pt;
						  }else{
							  
							  pt1 = new PointerType("x");
							  pt1.setPointerTo(pre);
							  pt1.setnumOfPointers(i);
							  pt1.setBaseType(typ);
							  pre = pt1;
							  pt1.setName(PointerFactory(pre));
						  }
						  
					  }
					  //UGLY
					  if (temp.getnumOfPointers() == 1){
						  pt1 = pt;
					  }
					  //System.out.println(pt1.getName());
					  pt1.setName(PointerFactory(pt1));
					  pt1.setnumOfPointers(temp.getnumOfPointers());
					  pt1.setBaseType(typ);
					return pt1;
					
				}else {

					  /* do it for every pointer to construct a pointer to pointer*/
					  for (int i = 1; i <= temp.getnumOfPointers(); i++){

						  if (i == 1){
							  pt = new PointerType("x");
							  pt.setPointerTo(typ);
							  pt.setBaseType(typ);
							  pt.setnumOfPointers(i);
							  pre = pt;
						  }else{
							  
							  pt1 = new PointerType("x");
							  pt1.setPointerTo(pre);
							  pt1.setnumOfPointers(i);
							  pt1.setBaseType(typ);
							  pre = pt1;
							  pt1.setName(PointerFactory(pre));
						  }
						  
					  }
					  //UGLY
					  if (temp.getnumOfPointers() == 1){
						  pt1 = pt;
					  }
					  //System.out.println(pt1.getName());
					  pt1.setName(PointerFactory(pt1));
					  pt1.setnumOfPointers(temp.getnumOfPointers());
					  pt1.setBaseType(typ);
					return pt1;
				}
			}
		}

		
		return null;
		
	}

	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoStructdefDecl (String id)
	{
		if (m_symtab.accessLocal (id) != null)
		{
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id));
		}
		
		TypedefSTO 	sto = new TypedefSTO (id,new StructType(id));
		m_symtab.insert (sto);
	}
	
	/*
	THIS IS PUT IN FOR CHECK 15a
	*/
	STO
	DoDereferenceCheck( STO sto)
	{
		if (sto instanceof ErrorSTO) {
			return new ErrorSTO("err");
		}
		
		if ( !(sto.getType() instanceof PointerType)){
			//System.out.println("we are here");
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error15_Receiver, sto.getType().getName()));
			return new ErrorSTO("error");
		}
		
		Type typ = sto.getType();
		
		if (typ instanceof FunctionPointerType) {
			return new VarSTO(sto.getName(),typ);
		}
		


		//CHANGE2  form here
		if (typ instanceof PointerType) {

			PointerType type = (PointerType)typ;
			Type ToType = type.getPointerTo();
			VarSTO vs = new VarSTO(sto.getName(),ToType);
			vs.copyProperties(sto);
			m_CodeGen.writePointerDeRef(vs);
			vs.setIsPointerDeRef(true);
			
			
			m_CodeGen.writeDerefCheck(sto);
			
			return vs;


		}
		/* when are we ever here ?????????????????? */

		VarSTO vs = new VarSTO(sto.getName(),typ);//out
		vs.copyProperties(sto);
		
		m_CodeGen.writeDerefCheck(sto);
		
		return vs;

		//to here]
		

		
	}
	
	
	
	void
	DoStructCheck (Vector<HashMap<Type,Vector<String>>> FieldLists, String struct_name) {
			
		for (HashMap<Type,Vector<String>> Field: FieldLists) {		
			
		Iterator itr = Field.keySet().iterator();
		while (itr.hasNext()) {
			
			Type typ = (Type)itr.next();
			Vector<String> names = Field.get(typ);
	
			
			if (typ.getName().equals(struct_name)) {
				if (!(typ instanceof PointerType)) {
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error13b_Struct, typ.getName()));
					continue ;
				}
			}
			
			
			for (String id : names) {
				if (m_symtab.accessLocal (id) != null)
				{
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error13a_Struct, id));
				}
			
				VarSTO 	sto = new VarSTO (id,typ);
				m_symtab.insert (sto);	
				
				if (m_StructInfo.get(struct_name) == null) {
					Vector<STO> v = new Vector<STO>();
					v.add(new VarSTO(id,typ));
					m_StructInfo.put(struct_name, v);
				}else {
					Vector<STO> v = m_StructInfo.get(struct_name);
					VarSTO r = new VarSTO(id,typ);
					if (typ instanceof ArrayType) {
						r.setIsModifiable(false);
						r.setIsAddressable(true);
					}
					v.add(r);		
				}
			
				}
			}
		}
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void DoFuncDecl_1 (Type typ, String id, String ref)
	{
		FuncSTO funcSTO = null;
		FuncSTO sto = null;
		
		if ((funcSTO=(FuncSTO)m_symtab.accessLocal (id)) != null)
		{
			this.m_funcTable.add(funcSTO);
			m_symtab.remove(funcSTO);
			sto = new FuncSTO (id);
			sto.setIsOverLoading(true);
	/*		
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id));*/
		}else {
	
			sto = new FuncSTO (id);
		}
		sto.setType(new FunctionPointerType("x",0));
		sto.setFuncType(typ);
		
		if ((ref != null) && (ref.equals("1")) ){
			sto.setIsReference(true);
		}
		
		sto.setIsModLValue(false);
		m_CodeGen.writeFuncOpen(id);
		m_symtab.insert (sto);
		m_symtab.openScope ();
		m_symtab.setFunc (sto);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoFuncDecl_2 ()
	{
    	FuncSTO funcsto = this.m_symtab.getFunc();
    	if (funcsto.getHasReturn() == false && !(funcsto.getFuncType() instanceof VoidType)){
    		m_nNumErrors++;
			m_errors.print (ErrorMsg.error6c_Return_missing);
			return;
    	}
    	
    	int flag = 0;
    	if( funcsto.getFuncType() instanceof VoidType) {
    		flag = 1;
    	}
    	int offset = m_symtab.getLocalOffset();
    	String name = m_symtab.getFunc().getName();
    	m_CodeGen.increaseIndent();
    	m_CodeGen.writeFuncClose( offset, name, flag);
    	m_CodeGen.decreaseIndent();
		m_symtab.closeScope ();
	}


	Type
	doFunctionPointerType(String strName, int size, Type returnType, String optRef, Vector<Parameter> paramsList){
		Type funcPtrType = new FunctionPointerType(strName, size, returnType, optRef, paramsList);
		funcPtrType.setName(FunctionFactory_2((FunctionPointerType)funcPtrType));
		funcPtrType.setLabelName(strName);
		return funcPtrType;
	}
	
	boolean DoOverloadingCheck(FuncSTO fsto , Vector<Parameter> params) {
		boolean flag;
		

		for (FuncSTO inList : this.m_funcTable) {
			if (inList.getName().equals(fsto.getName())) {
		
				flag = DoParamsCheck(inList.getParameters() , params);
				if (flag == false)
					return false;
			}
		}
		return true;
	}
	
	
	
	
	boolean
	DoParamsCheck (Vector<Parameter> p1 , Vector<Parameter> p2) {
		
		if (p1 == null && p2 == null)
			return false;
		
		if (p1 == null || p2 == null)
			return true;
		
		if (p1.size() != p2.size())
			return true;
		
		for (int i=0 ; i<p1.size() ; i ++) {
	
			if ( p1.get(i).getType().getClass() != (p2.get(i).getType().getClass())) {
				return true;
			}
			//CHANGE
			if ( p1.get(i).getType() instanceof PointerType ) {
				PointerType px = (PointerType)p1.get(i).getType();
				PointerType py = (PointerType)p2.get(i).getType();
				
				if (px.getnumOfPointers() != py.getnumOfPointers()) {
					return true;
				}
				
				if (px.getBaseType().getClass() != py.getBaseType().getClass()) {
					return true;
				}
				
				if (px.getBaseType() instanceof StructType) {
					if (DoStructEqualCheck2(px.getBaseType(), py.getBaseType()) == false) {
						return true;
					}
				}
			}
			
			if ( p1.get(i).getType() instanceof ArrayType ) {
				ArrayType px = (ArrayType)p1.get(i).getType();
				ArrayType py = (ArrayType)p2.get(i).getType();
				
				if (px.getUnderType().getClass() != py.getUnderType().getClass()) {
					return true;
				}
				
				if (px.getArraySize() != px.getArraySize()) {
					return true;
				}
				
				if (px.getUnderType() instanceof StructType ) {
					if (DoStructEqualCheck2(px.getUnderType(), py.getUnderType()) == false) {
						return true;
					}
				}
			}
			//CHANGE
		}
		
		return false;
	}
	
	
	boolean
	DoStructEqualCheck2(Type des , Type expr) {
	
		if (!(des instanceof StructType)
				|| !(expr instanceof StructType)) {
			return false;
		}
		
		StructType Des = (StructType)des;
		StructType Expr = (StructType)expr;
		
		String DesName = null;
		String ExprName = null;
		
		if (Des.getBaseName() == null) {
			DesName = Des.getName();
		}else {
			DesName = Des.getBaseName();
		}
		
		if (Expr.getBaseName() == null) {
			ExprName = Expr.getName();
		}else {
			ExprName = Expr.getBaseName();
		}
		
		
		
		if (!DesName.equals(ExprName) ) {
			return false;
		}
		
		
		return true;
	}

	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoFormalParams (Vector<Parameter> params)
	{
		if (m_symtab.getFunc () == null)
		{
			m_nNumErrors++;
			m_errors.print ("internal: DoFormalParams says no proc!");
			return;
		}

		FuncSTO fsto = m_symtab.getFunc();
		
		fsto.setParameters(params);
		if (fsto.getIsOverLoading()) {
			
			if (DoOverloadingCheck(fsto,params) == false) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error22_Decl, fsto.getName()));
			}
		}
		
		if (params == null){
			fsto.setArgCount(0);
			return;
		}
		
		
		fsto.setArgCount(params.size());
		
		for (int i = 0; i < params.size(); i++)
		{
			Parameter id = params.elementAt (i);
		
			if (m_symtab.accessLocal (id.getName()) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id.getName()));
			}
 
			VarSTO 		sto = new VarSTO (id.getName(),id.getType());
			
			sto.setOffset(68 + i*4);
			if (id.getReference() == true) {
				sto.setIsPassedByRef(true); // DANGEROUS : Maybe causes some problems 
				
			}else {
				sto.setIsPassedByValue(true);
			}
			m_symtab.insert (sto);
		}
		
		

		m_CodeGen.writeStoreParameters(params);
		

		// insert parameters here
	}
	
	void 
	DoWithinWhile(String str) {
		/*if (this.m_symtab.getIsWhile() == false) {
			m_nNumErrors++;
			if (str.equals("break"))
				m_errors.print (ErrorMsg.error12_Break);
			else
				m_errors.print (ErrorMsg.error12_Continue);
			
			System.exit(0);
		}*/
		
		if (str.equals("break")) {
			m_CodeGen.writeBreak();
		}else {
			m_CodeGen.writeContinue();
		}
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoBlockOpen ()
	{
		// Open a scope.
		m_symtab.openScope ();
	}
	
	void
	DoBlockOpen (String str)
	{
		// Open a scope.
		if (str.equals("while")) {
			m_symtab.openScope(str);
		}else if (str.equals("if")) {
			m_symtab.openScope(str);
		}else  {
			m_symtab.openScope ();
		}
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoBlockClose()
	{
		
		m_symtab.closeScope ();
	}
	
	
	
	void
	DoBlockClose(String name)
	{
		
		if (name.equals("if") && m_IfStack.empty() == false) {
			m_CodeGen.writeLabel(m_IfStack.pop());
			
		}else if (m_WhileEndStack.empty() == false){
			m_CodeGen.writeLabel(m_WhileEndStack.pop());
		}
		
		m_symtab.closeScope ();
	}
	
	

	boolean
	checkFunctionPtr(STO stoDes ,STO stoExpr)
	{
	
		if (stoDes instanceof ErrorSTO || stoExpr instanceof ErrorSTO) {
			return false;
		}
		
		FunctionPointerType desType = (FunctionPointerType) stoDes.getType();
		FunctionPointerType exprType = (FunctionPointerType) stoExpr.getType();
		
		if (desType.getReturnType().getClass() != exprType.getReturnType().getClass()){
			return false;
		}
		
		
		
		if (desType.getReturnType() instanceof PointerType) {
			if(((PointerType)desType.getReturnType()).getnumOfPointers() !=
					((PointerType)exprType.getReturnType()).getnumOfPointers()) {
				return false;
			}
		}
		
		
		
		if (desType.getOptRef() != exprType.getOptRef()){
			return false;
		}
		
		
		if (desType.getReturnType() instanceof StructType
				|| exprType.getReturnType() instanceof StructType) {
		
		if (DoStructEqualCheck(desType.getReturnType(), exprType.getReturnType()) instanceof ErrorSTO){
			
			return false;
		}
	
		}
		
		
		
		if (desType.getParamsList()== null || exprType.getParamsList() == null) {
			if (!(desType.getParamsList()== null && exprType.getParamsList() == null)) {
				return false;
			}
		}
		

		
		
		if (desType.getParamsList()== null && exprType.getParamsList() == null) {
				return true;
		}else if (desType.getParamsList().size() != exprType.getParamsList().size()){
			return false;
		}
		
		
		
		for (int i = 0; i < desType.getParamsList().size(); i++){
			
			Parameter desParam = desType.getParamsList().get(i);
			Parameter exprParam = exprType.getParamsList().get(i);
			
			if (desParam.getType().getClass() != (exprParam.getType().getClass())) {
				return false;
			}
		
			//HEH
			if (desParam.getType() instanceof PointerType) {
				
				PointerType p1 = (PointerType)desParam.getType();
				PointerType p2 = (PointerType)exprParam.getType();
				
				if (p1.getnumOfPointers() != p2.getnumOfPointers()) {
					return false;
				}
				
				
				if (p1.getBaseType() instanceof StructType 
						|| p2.getBaseType() instanceof StructType) {		
				
					if (DoStructEqualCheck(p1.getBaseType(), p2.getBaseType()) instanceof ErrorSTO){
						
						return false;
					}
				}
	
				if (p1.getBaseType().getClass() != (p2.getBaseType().getClass())) {
					return false;
				}
						
			}
			
			
			if (desParam.getReference() != exprParam.getReference()){
				return false;
			}
			
		
			
			if (desParam.getType() instanceof StructType) {
				
				if (DoStructEqualCheck(desParam.getType(), exprParam.getType()) instanceof ErrorSTO){
					
					return false;
				}
				else {
					return true;
				}
				
			}
		}
		
		return true;

	}
	
	
	
	
	boolean
	checkFunctionPtr2(STO stoDes ,STO stoFunction)
	{
		if (stoDes instanceof ErrorSTO || stoFunction instanceof ErrorSTO) {
			return false;
		}
		
		FunctionPointerType desType = (FunctionPointerType) stoDes.getType();
		FuncSTO stoFunc = (FuncSTO)stoFunction;
		
		if (desType.getReturnType().getClass() != stoFunc.getFuncType().getClass()){
			return false;
		}
		
		if (desType.getReturnType() instanceof PointerType) {
			if(((PointerType)desType.getReturnType()).getnumOfPointers() !=
					((PointerType)stoFunc.getFuncType()).getnumOfPointers()) {
				return false;
			}
		}
		
		
		if (desType.getOptRef() != stoFunc.getIsReference()){
			return false;
		}
		
		if (desType.getReturnType() instanceof StructType
					|| stoFunc.getFuncType() instanceof StructType) {
			
			if (DoStructEqualCheck(desType.getReturnType(), stoFunc.getFuncType()) instanceof ErrorSTO){
				
				return false;
			}
		
		}
		
		
		
		if (desType.getParamsList()== null || stoFunc.getParameters() == null) {
			if (!(desType.getParamsList()== null && stoFunc.getParameters() == null)) {
				return false;
			}
		}
		
		
		if (desType.getParamsList()== null && stoFunc.getParameters() == null) {
			return true;
		}else if (desType.getParamsList().size() != stoFunc.getParameters().size()){
			return false;
		}
		
		for (int i = 0; i < desType.getParamsList().size(); i++){
			
			Parameter desParam = desType.getParamsList().get(i);
			Parameter exprParam = stoFunc.getParameters().get(i);
			
			if (desParam.getType().getClass() != (exprParam.getType().getClass())) {
				return false;
			}
			
			
			
		if (desParam.getType() instanceof PointerType) {
				
				PointerType p1 = (PointerType)desParam.getType();
				PointerType p2 = (PointerType)exprParam.getType();
				
				if (p1.getnumOfPointers() != p2.getnumOfPointers()) {
					return false;
				}
				
				
				if (p1.getBaseType() instanceof StructType 
						|| p2.getBaseType() instanceof StructType) {		
				
					if (DoStructEqualCheck(p1.getBaseType(), p2.getBaseType()) instanceof ErrorSTO){
						
						return false;
					}
				}
	
				if (p1.getBaseType().getClass() != (p2.getBaseType().getClass())) {
					return false;
				}
						
			}
				
			
			
			if (desParam.getReference() != exprParam.getReference()){
				return false;
			}
			
			if (desParam.getType() instanceof StructType) {
				
				if (DoStructEqualCheck(desParam.getType(), exprParam.getType()) instanceof ErrorSTO){
					
					return false;
				}
				else {
					return true;
				}		
			}
	
		}
		
		return true;

	}
	
	
	
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	String PointerFactory (PointerType x) {
		
		if (x.getIsTypedef() == true) {
			return x.getName();
		}
		
		String name = x.getBaseType().getName();
		boolean isArray = false;
		String modifier = "";
		if (x.getBaseType() instanceof ArrayType && x.getBaseType().getIsTypedef() == false){
			String[] parts = name.split("\\[");
			name = parts[0];
			modifier = parts[1];
			isArray = true;
		}
		
		for (int i=0 ; i<x.getnumOfPointers() ;i++) {
			name += "*";
		}
		if (isArray){
			name+= "[" + modifier;
		}
		
		return name;
	}
	
	
	
	String
	FunctionFactory(STO sto1){
		
		if (sto1 instanceof VarSTO) {
			
			if (sto1.getIsTypedef() == true) {
				return sto1.getType().getName();
			}
			VarSTO v = (VarSTO)sto1;
			return FunctionFactory_2((FunctionPointerType)v.getType());
		}
		
		FuncSTO sto = (FuncSTO)sto1;
		String funcName = "funcptr : ";
		String returnName = "";
		if (sto.getReturnType() instanceof PointerType){
			returnName = PointerFactory((PointerType)sto.getReturnType());
			
		}else{
			returnName= sto.getFuncType().getName();
		}

		String refName = "";
		if (sto.getIsReference() == true ){
			refName = "& ";
		}
		String parameterName = "";
		int count = 0;
		
		if (sto.getArgCount() != 0) {
		for (Parameter P: sto.getParameters()){
			
			if (P.getType() instanceof PointerType){
				
				parameterName+=PointerFactory((PointerType)P.getType());
			}else{
				
				parameterName+=P.getType().getName();
			}
			
			
			parameterName+=" ";
			
			if (P.getReference() == true) {
				parameterName += "&";
			}
			
			parameterName+=P.getName();
			if (count < sto.getParameters().size()-1){
				parameterName+=", ";
			}
			count++;
		}
		}
		funcName = funcName + returnName + " " + refName + "(" + parameterName + ")";
		return funcName;
		
	
	}
	
	
	
	
	String
	FunctionFactory_2(FunctionPointerType typ){
		
		
		String funcName = "funcptr : ";
		String returnName = "";
		
		if (typ.getReturnType() instanceof PointerType){
			returnName = PointerFactory((PointerType)typ.getReturnType());
			
		}else{
			returnName= typ.getReturnType().getName();
		}

		String refName = "";
		
		if (typ.getOptRef() == true ){
			refName = "& ";
		}
		String parameterName = "";
		int count = 0;
		
		if (typ.getParamsList() != null) {
		for (Parameter P: typ.getParamsList()){
			
			if (P.getType() instanceof PointerType){
				
				parameterName+=PointerFactory((PointerType)P.getType());
			}else{
				
				parameterName+=P.getType().getName();
			}
			
			
			parameterName+=" ";
			
			if (P.getReference() == true) {
				parameterName += "&";
			}
			
			parameterName+=P.getName();
			if (count < typ.getParamsList().size()-1){
				parameterName+=", ";
			}
			count++;
		}
		}
		
		funcName = funcName + returnName + " " + refName + "(" + parameterName + ")";
		return funcName;
		
	
	}
	
	
	
	String
	ArrayFactory(Type aT){
		String arrayName = "";
		if (aT.getUnderType() instanceof BasicType || aT.getUnderType().getIsTypedef() == true){
			arrayName = aT.getUnderType().getName() + "[" + aT.getSize() + "]";
		}else{
			arrayName = aT.getName();
		}
		//System.out.println(arrayName);
		return arrayName;
	}
	
	boolean
	compareArrays(ArrayType arrDes, ArrayType arrExpr){
		ArrayType pointerBase = arrDes;
		ArrayType arrayBase = arrExpr;
		boolean hasUnderType = true;
		while (hasUnderType){
			Type tempPointerBase = null;
			Type tempArrayBase = null;
			if (pointerBase instanceof ArrayType){
				if (pointerBase.getSize() != arrayBase.getSize()){
					return false;
				}
				tempArrayBase = arrayBase.getUnderType();
				tempPointerBase = pointerBase.getUnderType();
				if (tempPointerBase.getClass() != tempArrayBase.getClass()){
					return false;
				}
				if (!(tempPointerBase instanceof ArrayType) || !(tempArrayBase instanceof ArrayType)){
					if (tempPointerBase.getClass() != tempArrayBase.getClass()){
						return false;
					}
					return true;
					
				}
				
				
			}
			pointerBase = (ArrayType) tempPointerBase;
			arrayBase = (ArrayType) tempArrayBase;
			
		}
		return true;
	}
	
	
	STO
	DoAssignExpr (STO stoDes ,STO stoExpr)
	{	
		if (stoDes.getType() instanceof StructType){
			int size = ((ConstSTO)DoSizeOfType(stoDes.getType())).getIntValue();
			m_CodeGen.writeStructAssignment(stoDes,stoExpr,size);
			return stoDes;
		}
		
		if (stoExpr.getName().equals("Literal")) {
			//CHANGE
			//m_CodeGen.writeLiteral(((ConstSTO)stoExpr).getIntValue());
			m_CodeGen.increaseIndent();
			m_CodeGen.writeLiteral((ConstSTO)stoExpr);
			m_CodeGen.decreaseIndent();
		}else {
			m_CodeGen.increaseIndent();

			if (stoExpr.getNeedsExtraOffSet() == true){
				m_CodeGen.writeLoad(stoExpr,"%l1",3); 
			}

			m_CodeGen.writeLoad(stoExpr,"%l1",0);
		}
		
		m_CodeGen.increaseIndent();
		
		
		if (stoDes.getType() instanceof FloatType && !(stoExpr.getType() instanceof FloatType)) {
			
			STO temporary = new ExprSTO("promote",stoDes.getType());
			temporary.setOffset(MyParser.getSymTable().getLocalOffset());
			
			m_CodeGen.writeStore(temporary,"%l1");
			m_CodeGen.writeLoad(temporary,"%f0",1);
			m_CodeGen.writeFileInfo(Template.THREE_PARAM, Template.FITOS , "%f0", "%f0");
			
		}
		
		

		m_CodeGen.writeStore(stoDes,null);
		m_CodeGen.decreaseIndent();
		return stoDes;
		/*
		if (stoDes instanceof ErrorSTO || stoExpr instanceof ErrorSTO){
			return new ErrorSTO("a");
		}
		

		//check if it is a functionPointertype
		if(stoDes.getType() instanceof FunctionPointerType){

			FunctionPointerType funtype = (FunctionPointerType) stoDes.getType();

			if ( stoExpr instanceof FuncSTO){
				//this is if expr is instanceof funcsto

				if (checkFunctionPtr2(stoDes, stoExpr) == false ) {
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
							FunctionFactory(stoExpr),
							FunctionFactory(stoDes)));
					return new ErrorSTO("a");
				}

				((FunctionPointerType) stoDes.getType()).setBaseFunc((FuncSTO) stoExpr);
				return stoDes;


			}else if (stoExpr.getType() instanceof FunctionPointerType){
				//this is if expr is functionpointer type
				
				if (checkFunctionPtr(stoDes, stoExpr) == false ) {
					
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
							FunctionFactory(stoExpr),
							FunctionFactory(stoDes)));
					
					return new ErrorSTO("a");
				}				

				FunctionPointerType exprTyp = (FunctionPointerType) stoExpr.getType();

				((FunctionPointerType) stoDes.getType()).setBaseFunc((exprTyp.getBaseFunc()));

				return stoDes;

			}

		}

		if (!stoDes.isModLValue() || stoDes.getType() instanceof ArrayType)
		{
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error3a_Assign);
			return new ErrorSTO("a");
			// Good place to do the assign checks
		}


		if (stoDes.getType() instanceof PointerType 
				&& stoExpr.getType() instanceof PointerType) {

			PointerType p1 = (PointerType)stoDes.getType();
			PointerType p2 = (PointerType)stoExpr.getType();
			
			
			if (stoExpr.getType() instanceof NullPointerType) {

				return stoDes;
			}
			
			
			if (p1.getnumOfPointers() != p2.getnumOfPointers() || p1.getBaseType().getClass() != p2.getBaseType().getClass()) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
						PointerFactory(p2),PointerFactory(p1)));
				return new ErrorSTO("a");
			}
			
			if (p1.getBaseType() instanceof ArrayType){
				//need to check each arraytype they are both the same base type
				if (compareArrays( (ArrayType )p1.getBaseType(), (ArrayType)p2.getBaseType()) == false){
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
							PointerFactory(p2),PointerFactory(p1)));
					return new ErrorSTO("a");
				}
			}
			
			if (p1.getBaseType() instanceof StructType 
					|| p2.getBaseType() instanceof StructType) {
				
				
				if (DoStructEqualCheck(p1.getBaseType(), p2.getBaseType()) instanceof ErrorSTO){
					
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
							PointerFactory(p2),PointerFactory(p1)));
					return new ErrorSTO("err");
				}
				else {
					return stoDes;
				}
			}
			


			if (p1.getBaseType().getClass() != (p2.getBaseType().getClass())) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
						PointerFactory(p2),PointerFactory(p1)));
				return new ErrorSTO("a");
			}

			return stoDes;
	

		}else if (stoDes.getType() instanceof PointerType
				&& stoExpr.getType() instanceof ArrayType){
			
			
			PointerType p1 = (PointerType)stoDes.getType();
			ArrayType p2 = (ArrayType)stoExpr.getType();
			
			if (p2.getUnderType() != null && p2.getUnderType() instanceof PointerType){
				if (p1.getnumOfPointers() != ((PointerType)p2.getUnderType()).getnumOfPointers()+1){
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
							p2.getUnderType().getName()+"["+p2.getArraySize()+"]",PointerFactory(p1)));
					return new ErrorSTO("a");
				}
			}else if (p1.getnumOfPointers() != 1) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
						p2.getUnderType().getName()+"["+p2.getArraySize()+"]",PointerFactory(p1)));
				return new ErrorSTO("a");
				
			}
			
			
			if (p2.getUnderType() instanceof StructType) {
				
				if (DoStructEqualCheck(p1.getBaseType(), p2.getUnderType()) instanceof ErrorSTO){

					m_nNumErrors++;
					
					m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
							p2.getName(),PointerFactory(p1)));
					return new ErrorSTO("a");
				}
				else {
					return stoDes;
				}
				
			}
			
			if (p1.getBaseType() instanceof ArrayType){
				ArrayType pointerBase = (ArrayType) p1.getBaseType();
				ArrayType arrayBase = (ArrayType) p2.getUnderType();
				boolean hasUnderType = true;
				while (hasUnderType){
					Type tempPointerBase = null;
					Type tempArrayBase = null;
					if (pointerBase instanceof ArrayType){
						if (pointerBase.getSize() != arrayBase.getSize()){
							//error
							m_nNumErrors++;
							m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
								p2.getName(),PointerFactory(p1)));
						return new ErrorSTO("a");
						}
						tempArrayBase = arrayBase.getUnderType();
						tempPointerBase = pointerBase.getUnderType();
						if (tempPointerBase.getClass() != tempArrayBase.getClass()){
							//error
							m_nNumErrors++;
							m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
								p2.getName(),PointerFactory(p1)));
							return new ErrorSTO("a");
						}
						if (!(tempPointerBase instanceof ArrayType) || !(tempArrayBase instanceof ArrayType)){
							if (tempPointerBase.getClass() != tempArrayBase.getClass()){
								//error
								m_nNumErrors++;
								m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
									p2.getName(),PointerFactory(p1)));
								return new ErrorSTO("a");
							}
							break;
							
						}
						
						
					}
					pointerBase = (ArrayType) tempPointerBase;
					arrayBase = (ArrayType) tempArrayBase;
					
				}
				if (p1.getBaseType().getUnderType().getClass() != p2.getUnderType().getUnderType().getClass()){
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
							p2.getName(),PointerFactory(p1)));
					return new ErrorSTO("a");
				}
			}
			if (p1.getPointerTo().getClass() == p2.getUnderType().getClass()) {

				return stoDes;

			}else {

				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
						p2.getName(),p1.getName()));
				
				return new ErrorSTO("a");
			}
			
		}else if (stoDes.getType() instanceof StructType 
						|| stoExpr.getType() instanceof StructType) {
			
			if (DoStructAssign(stoDes.getType(), stoExpr.getType(), false) 
						instanceof ErrorSTO) {
				return new ErrorSTO("err");
			}
			else {
				return stoDes;
			}
				
			
		}
		else if (stoDes.getType().getClass() == stoExpr.getType().getClass()) {
			
			return stoDes;
			
		}else if ((stoDes.getType() instanceof FloatType) 
				&& stoExpr.getType() instanceof IntType) {

			return stoDes;

		}
		else {
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
					stoExpr.getType().getName(),stoDes.getType().getName()));
			return new ErrorSTO("a");
		}
*/
	}
	
	
	
	
	STO
	DoStructAssign(Type des , Type expr , boolean init) {
	
		
		if (!(des instanceof StructType) ||
				!(expr instanceof StructType)) {		

			m_nNumErrors++;
			if (init == false) {
				m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
						expr.getName(),des.getName()));
			}else {
				m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, 
						expr.getName(),des.getName()));
			}
			
			return new ErrorSTO("err");
		}
		
		StructType Des = (StructType)des;
		StructType Expr = (StructType)expr;
		
		String DesName = null;
		String ExprName = null;
		
		if (Des.getBaseName() == null) {
			DesName = Des.getName();
		}else {
			DesName = Des.getBaseName();
		}
		
		if (Expr.getBaseName() == null) {
			ExprName = Expr.getName();
		}else {
			ExprName = Expr.getBaseName();
		}
		
		
		
		if (!DesName.equals(ExprName) ) {
			m_nNumErrors++;
			
			if (init == false) {
				m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
						expr.getName(),des.getName()));
			}else {
				m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, 
						expr.getName(),des.getName()));
			}
			return new ErrorSTO("err");
		}
		
		
		return new VarSTO("x");
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoBinaryExpr( STO a, Operator o, STO b)
	{
		if (a instanceof ErrorSTO || b instanceof ErrorSTO){
			return new ErrorSTO("a");
		}

	
		STO result = o.checkOperands( a, b ,o);
		int offset = m_symtab.getLocalOffset();
		result.setOffset(offset);
		
		m_CodeGen.writeOperator(a,o,b,result);
	
		
		if (result instanceof ErrorSTO) {
			m_nNumErrors++;
			return result;
		}
/*		if ( a instanceof ConstSTO && b instanceof ConstSTO){
			ConstSTO constA = (ConstSTO)a;
			Double valueA = constA.getValue();
			ConstSTO constB = (ConstSTO)b;
			Double valueB = constB.getValue();
			valueB.f
			ConstSTO result = new ConstSTO(result.getName(),result.getType(),)
		}*/
		result.setIsAddressable(false);
		return result;
	}
	
	

	void
	DoAmpersand(STO sto)
	{
		AndCounter++;
		String mark  = "flabel" + AndCounter;
		pushAndStack(AndCounter);
		m_CodeGen.writeAmpersand(sto,mark);
	}
	
	

	void
	DoOr(STO sto)
	{
		OrCounter++;
		String mark  = "Orflabel" + OrCounter;
		pushOrStack(OrCounter);
		m_CodeGen.writeOr(sto,mark);
	}
	
	
	
	
	
	STO
	DoCast(Type castType, STO sto){
		
		if (sto instanceof ErrorSTO) {
			return (new ErrorSTO (sto.getName ())); 
		}
		
		if (castType instanceof ArrayType 
				|| castType instanceof StructType ){
			
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error20_Cast, 
					sto.getType().getName(),castType.getName()));
			return (new ErrorSTO (sto.getName ())); 
		}
		
		if (sto.getType() instanceof ArrayType || sto.getType() instanceof FunctionPointerType
				||sto.getType() instanceof StructType) {
			
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error20_Cast, 
					sto.getType().getUnderType().getName(),castType.getName()));
			return (new ErrorSTO (sto.getName ())); 
		}
		
		if (sto instanceof ConstSTO){//(int)(2.3)
			Type type = sto.getType();
			Double constValue = ((ConstSTO) sto).getValue();
			Double returnValue = null;
			
			if ( castType instanceof BoolType ){
				if (constValue.floatValue() != 0)
					returnValue = new Double(1); //true
				else
					returnValue = new Double(0); //false
			}else if (castType instanceof FloatType){//special
					returnValue = constValue;
			}else if (castType instanceof IntType){
					returnValue = new Double(constValue.intValue());
			}else if (castType instanceof PointerType){
					returnValue = new Double(constValue.intValue());	
			}
			
			
			ConstSTO reSto = new ConstSTO(sto.getName(),castType,returnValue);
			int offset = getSymTable().getLocalOffset();
			reSto.setOffset(offset);
			m_CodeGen.writeCast(reSto, sto.getType());
			return reSto;
		}
		
		
		
		VarSTO sto1 =  new VarSTO(sto.getName(),castType);
		sto1.setIsModLValue(false);
		
		int offset = getSymTable().getLocalOffset();
		sto1.setOffset(offset);// (int)a
		
		if (sto.getType() instanceof FloatType) {
			
			m_CodeGen.writeLoad(sto,"%f0",1);//load a
			m_CodeGen.writeStore(sto1,"%f0");//store to temp
			m_CodeGen.writeCast(sto1, sto.getType());
			
		}else {
			m_CodeGen.writeLoad(sto,"%l2",0);//load a
			m_CodeGen.writeStore(sto1,"%l2");//store to temp
			m_CodeGen.writeCast(sto1, sto.getType());
		}
		
		return sto1;
	}
	
	
	static int calculateStructSize ( StructType  st) {
		
		int size = 0;
		Vector<STO> fields;
		
		if ((fields=m_StructInfo.get(st.getName())) == null ) {
			return 0;
		}else {
			for (STO sto : fields) {
				
				if (sto.getType() instanceof StructType) {
					
					STO tempSto = DoSizeOfType(sto.getType());
					ConstSTO cs = (ConstSTO)tempSto;
					size += cs.getIntValue();
					
				}else {
					
					STO tempSto = DoSizeOfType(sto.getType());
					ConstSTO cs = (ConstSTO)tempSto;
					size += cs.getIntValue();
					
				}
			}	
			return size;
		}
	
	}
	
	
	
	
	public static STO
	DoSizeOfType(Type typ){
		
		int size = 1;
		
		if (typ instanceof ArrayType) {
			
			ArrayType at = (ArrayType)typ;
			
			if (at.getUnderType() instanceof ArrayType) {
				size *= ((ArrayType) typ).getArraySize();
				
				while (true) {
					ArrayType temp = (ArrayType)at.getUnderType();
					size *= temp.getArraySize();
					
					if (temp.getUnderType() instanceof ArrayType) {
						at = temp;
						continue;
					}else {
						size*= temp.getUnderType().getSize();
						break;
					
					}
				}
			}else {
				size = at.getUnderType().getSize() * typ.getSize();
			}
			
			//System.out.println("the size is " + size);
			return new ConstSTO("Literal",new IntType(), size);
			
		}else if (typ instanceof StructType) {
			
			StructType st = (StructType)typ;
			size = calculateStructSize(st);
			
		}else {
			size = typ.getSize();
		}
		
		//System.out.println("the size is " + size);
		return new ConstSTO("Literal",new IntType(), size);
	}
	
	
	
	STO
	DoSizeOf(STO sto){
		
		if (sto instanceof ErrorSTO) {
			return new ErrorSTO("a");
		}
		
		/*if (sto.getIsAddressable() == false){
			m_nNumErrors++;
			m_errors.print(ErrorMsg.error19_Sizeof);
			return new ErrorSTO("a");
		}*/
		
		Type typ = sto.getType();
		STO returnSto = DoSizeOfType(typ);
		
		return returnSto;
	}
	
	
	void
	DoWhileMark ()
	{
		String mark = "_beginWhile" + WhileCounter;	
		WhileCounter++;	
		m_WhileStack.push(mark);
		
		m_CodeGen.writeWhileMark(mark);
		
		
		
	}
	
	
	void
	DoEndWhile()
	{
		String mark = popWhileStack(); 
		m_CodeGen.writeEndWhile(mark);
		
	}
	
	void
	DoEndIf()
	{
	
		String mark = null;
		if (m_ElseStack.isEmpty() == false) {
			mark = m_ElseStack.peek(); 
			m_CodeGen.writeEndIf(mark);
		}
		
	}
	
	void
	DoEndElse()
	{
		String mark = popElseStack(); 
		m_CodeGen.writeEndElse(mark);
		
	}

	void
	DoOptElse (String opt) {
		if (opt != null && opt.equals("empty")) {
			
			if (m_ElseStack.isEmpty() == false) {
				
				m_CodeGen.writeElseExtraLabel(popElseStack());
			}
		}
		
	}
	
	void
	DoElseStart()
	{
		String label = "_endElse" + ElseCounter;
		ElseCounter++;	
		m_ElseStack.push(label);
		
	}
	
	STO
	DoConditional( STO expr ,String name)
	{
		/*if (expr instanceof ErrorSTO) {
			m_nNumErrors++;
			return (new ErrorSTO (expr.getName ())); 
		}*/
	
		String label =  null ;
		
		if (name.equals("if")) {
			
			label = "_endif" + IfCounter;
			IfCounter++;
			m_IfStack.push(label);
			
		}else {
			
			label = "_endwhile" + WhileEndCounter;
			WhileEndCounter++;
			m_WhileEndStack.push(label);
			
		}
		
		m_CodeGen.writeConditional(expr, label);
		
		return expr;
		
		/*if (expr.getType() instanceof BoolType
				|| expr.getType() instanceof IntType){
			return expr;
		}
		
		else{
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error4_Test, 
					expr.getType().getName()));
			return (new ErrorSTO (expr.getName ())); 
		}*/
			
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoUnaryExpr( STO a, Operator o)
	{
		STO result = o.checkOperand( a,o);
		
		if (result instanceof ErrorSTO) {
			
			m_nNumErrors++;
			return (new ErrorSTO (a.getName ()));
		}
		
		if (o.getName().equals("!")) {
			result = m_CodeGen.writeUnaryNot(a , o);
		}
		
		return result;
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	
	
	

	STO
	DoOverLoadingCall ( FuncSTO Sto , Vector<STO> args) {

	
		FuncSTO updateSTO = (FuncSTO) m_symtab.access(Sto.getName());
		this.m_funcTable.add(updateSTO);
		
		for (FuncSTO elem : this.m_funcTable) {
			if (elem.getName().equals(Sto.getName())) {

				int numOfArg;
				Vector<Parameter> params;

				FuncSTO funcSTO = elem;
				numOfArg = funcSTO.getArgCount();
				params = funcSTO.getParameters();


				int argSize = 0;
				if (args == null){
					argSize = 0;

				}else {
					argSize = args.size();
				}
				if ( numOfArg != argSize){
					continue ;

				}

				if (numOfArg == 0)
					return (Sto);

				int counter = 0;
				boolean pass = true;

				for (STO arg: args){
					if (!params.get(counter).getReference()){
					
						if (arg.getType().getClass() != (params.get(counter).getType().getClass())){
							//Here
							pass = false;;
							break;
						}
						
						if (arg.getType() instanceof StructType || 
								params.get(counter).getType() instanceof StructType){
									
							if (!(arg.getType() instanceof StructType) || 
										!(params.get(counter).getType() instanceof StructType)){	
								pass = false;;
								break;
								
							}
									
							if (DoStructEqualCheck(arg.getType() , params.get(counter).getType()) instanceof ErrorSTO) {
								pass = false;;
								break;
			    			}						
						
						}
						
						
						if (arg.getType() instanceof PointerType) {
							PointerType px = (PointerType)arg.getType();
							PointerType py = (PointerType)params.get(counter).getType();
							
							if (px.getnumOfPointers() != py.getnumOfPointers()) {
								pass = false;
								break;
							}
							
							if (px.getBaseType().getClass() != py.getBaseType().getClass()) {
								pass = false;
								break;
							}
							
							if (px.getBaseType() instanceof StructType ) {
								if (DoStructEqualCheck2(px.getBaseType(), py.getBaseType()) == false) {
									pass = false;
									break;
								}
							}
						}
						
						if (arg.getType() instanceof ArrayType) {
							ArrayType pa = (ArrayType)arg.getType();
							ArrayType pb = (ArrayType)params.get(counter).getType();
							
							if (pa.getArraySize() != pb.getArraySize()) {
								pass = false;
								break;
							}
							
							if (pa.getUnderType().getClass() != pb.getUnderType().getClass()) {
								pass = false;
								break;
							}
						}
						
					}
					else{
						
						if((arg.getType().getClass() !=(params.get(counter).getType().getClass()))) {
							pass = false;
							break;
						}
						
						if (!arg.isModLValue()) {
							pass = false;
							break;
						}
						
						if (arg.getType() instanceof StructType || 
								params.get(counter).getType() instanceof StructType){
									
							if (!(arg.getType() instanceof StructType) || 
										!(params.get(counter).getType() instanceof StructType)){	
								pass = false;;
								break;
								
							}
									
							if (DoStructEqualCheck(arg.getType() , params.get(counter).getType()) instanceof ErrorSTO) {
								pass = false;;
								break;
			    			}						
						
						}
						
						
						
						if (arg.getType() instanceof PointerType) {
							PointerType px = (PointerType)arg.getType();
							PointerType py = (PointerType)params.get(counter).getType();
							
							if (px.getnumOfPointers() != py.getnumOfPointers()) {
								pass = false;
								break;
							}
							
							if (px.getBaseType().getClass() != py.getBaseType().getClass()) {
								pass = false;
								break;
							}
							
							if (px.getBaseType() instanceof StructType ) {
								if (DoStructEqualCheck2(px.getBaseType(), py.getBaseType()) == false) {
									pass = false;
									break;
								}
							}
						}
						
						if (arg.getType() instanceof ArrayType) {
							ArrayType pa = (ArrayType)arg.getType();
							ArrayType pb = (ArrayType)params.get(counter).getType();
							
							if (pa.getArraySize() != pb.getArraySize()) {
								pass = false;
								break;
							}
							
							if (pa.getUnderType().getClass() != pb.getUnderType().getClass()) {
								pass = false;
								break;
							}
						}
						
						
					}
					counter++;			
				}

				if (pass == true) {


					this.m_funcTable.remove(updateSTO);

					if (Sto instanceof FuncSTO){
						FuncSTO f = (FuncSTO)Sto;
						if( f.getIsReference() == true ){
							VarSTO xx = new VarSTO(f.getName(),f.getFuncType());
							xx.setIsFuncReturn(true);
							return xx;
							
						}else{
							ExprSTO expr = new ExprSTO(f.getName(),f.getFuncType());
							expr.setIsModLValue(false);
							expr.setIsFuncReturn(true);
							return expr;
						}
					}

					if (Sto.getType() instanceof FunctionPointerType) {

						FunctionPointerType f = (FunctionPointerType) Sto.getType();

						if( f.getOptRef() == true ){
							
							VarSTO xx = new VarSTO(f.getName(),f.getReturnType());
							xx.setIsFuncReturn(true);
							
							return xx;
							
						}else{

							ExprSTO expr = new ExprSTO(f.getName(),f.getReturnType());
							expr.setIsModLValue(false);
							expr.setIsFuncReturn(true);
							
							return expr;
						}
					}

				}
			}
		}

		this.m_funcTable.remove(updateSTO);
		m_nNumErrors++;
		m_errors.print (Formatter.toString(ErrorMsg.error22_Illegal, 
				Sto.getName()));
		return (new ErrorSTO (Sto.getName ()));

	}
	
	
	
	
	void
	DoCout (STO args) {
		m_CodeGen.increaseIndent();
		m_CodeGen.writeCout(args);
		m_CodeGen.decreaseIndent();
	}
	
	void 
	DoCin (STO args) {	
		m_CodeGen.writeCin(args);
	
	}
	

    STO
	DoFuncCall (STO funcSto, Vector<STO> args)
	{
    	int struct_size = 0;
    	
    	if (funcSto instanceof FuncSTO) {
    		FuncSTO ft = (FuncSTO)funcSto;
    		if (ft.getFuncType() instanceof StructType) {
    			struct_size = ((ConstSTO)DoSizeOfType(ft.getFuncType())).getIntValue();
    		}
    	}
    	
    	m_CodeGen.writeFuncCall(funcSto,args,struct_size);
    	
    	
    	/*
    	if (funcSto instanceof ErrorSTO) {
    		m_nNumErrors++;
    		return (new ErrorSTO (funcSto.getName ()));
    	}
    	
		if (!funcSto.isFunc() && !(funcSto.getType() instanceof FunctionPointerType))
		{
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.not_function, funcSto.getName()));
			return (new ErrorSTO (funcSto.getName ()));
		}
		
		
		if (funcSto.isFunc()) {
			
			FuncSTO f = (FuncSTO)funcSto;
		
			if (f.getIsOverLoading() == true) {	
				
				return DoOverLoadingCall( f, args);
			}
			
		}else if (funcSto.getType() instanceof FunctionPointerType) {
			
			FuncSTO baseFunc = ((FunctionPointerType)funcSto.getType()).getBaseFunc();
			
			if (baseFunc!=null && baseFunc.getIsOverLoading() == true) {
				return DoOverLoadingCall( baseFunc , args );
			}
		}
		
		
		int numOfArg;
		Vector<Parameter> params;
		
		if (funcSto instanceof FuncSTO) {
			
			FuncSTO funcSTO = (FuncSTO) funcSto;
			numOfArg = funcSTO.getArgCount();
			params = funcSTO.getParameters();
			
		}else {
			
			FunctionPointerType funcType = (FunctionPointerType)funcSto.getType();
			params= funcType.getParamsList();
			if (params == null) {
				numOfArg = 0;
			}else {
				numOfArg = params.size();
			}
			
		}
		int argSize = 0;
		if (args == null){
			argSize = 0;
		}else{
			argSize = args.size();
		}
		
		if ( numOfArg != argSize){
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error5n_Call, args.size(), numOfArg));
			return (new ErrorSTO (funcSto.getName ()));
		}
		boolean skipParameterCheck = false;
		if (numOfArg == 0)
			skipParameterCheck = true;
			//return (funcSto);
		
		
		int counter = 0;
		
		boolean argError = true;
		
		if(skipParameterCheck == false){
			//check the paramenters
			for (STO arg: args){
				
				if (arg instanceof FuncSTO){
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
							FunctionFactory(arg),params.get(counter).getName(), params.get(counter).getType().getName()));
					argError = false;
					counter++;	
					continue;
				}
				
				
				
				if (!params.get(counter).getReference()){
					//no reference
					if (arg.getType() instanceof StructType || 
							params.get(counter).getType() instanceof StructType){
								
						if (!(arg.getType() instanceof StructType) || 
									!(params.get(counter).getType() instanceof StructType)){	
							m_nNumErrors++;
							m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
									arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
							argError = false;
							counter++;	
							continue;
							
						}
								
						if (DoStructEqualCheck(arg.getType() , params.get(counter).getType()) instanceof ErrorSTO) {
		    				m_nNumErrors++;
		    				m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
									arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
		    				argError = false;
		    				counter++;	
							continue;
		    			}						
			
					
					}else if(params.get(counter).getType() instanceof PointerType){
						//parameter is a pointer
						PointerType paramPointer = (PointerType)params.get(counter).getType();
						
						if(arg.getType() instanceof ArrayType){
							//argument is an array

							if (arg.getType().getUnderType() != null && arg.getType().getUnderType() instanceof PointerType){
								if (paramPointer.getnumOfPointers() != ((PointerType)arg.getType().getUnderType()).getnumOfPointers()+1){
									m_nNumErrors++;
									m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
											arg.getType().getUnderType().getName()+"["+arg.getType()+"]",PointerFactory(paramPointer)));
									argError = false;
									counter++;	
									continue;
								}
							}else if (paramPointer.getnumOfPointers() != 1) {
								m_nNumErrors++;
								m_errors.print (Formatter.toString(ErrorMsg.error3b_Assign, 
										arg.getType().getUnderType().getName()+"["+arg.getType()+"]",PointerFactory(paramPointer)));
								argError = false;
								counter++;	
								continue;
								
							}

	
							
							
							if (arg.getType().getUnderType() instanceof StructType) {
								
								if (DoStructEqualCheck(paramPointer.getBaseType(), arg.getType().getUnderType()) instanceof ErrorSTO){

									m_nNumErrors++;
									m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
											arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
									argError = false;
									counter++;	
									continue;
									
								}
						
							}
							
							if(arg.getType().getUnderType() instanceof PointerType){
								PointerType pt = (PointerType) arg.getType().getUnderType();
								if (pt.getBaseType().getClass() != paramPointer.getBaseType().getClass()){
									m_nNumErrors++;
									m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
											arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
									argError = false;
									counter++;	
									continue;
								}
							}else if(arg.getType().getUnderType().getClass() != paramPointer.getBaseType().getClass()){
								
								m_nNumErrors++;
								m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
										arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
								argError = false;
								counter++;	
								continue;
							}
							
							
						}else if (arg.getType() instanceof PointerType){
							//cast to pointer type
							PointerType argPointer = (PointerType) arg.getType();
							if (argPointer.getBaseType().getClass() == paramPointer.getBaseType().getClass()){
								if (argPointer.getBaseType() instanceof ArrayType){
									if(argPointer.getBaseType().getUnderType().getClass() != paramPointer.getBaseType().getUnderType().getClass()){
										m_nNumErrors++;
										m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
												arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
										argError = false;
										counter++;	
										continue;
									}
								}
							}
							if (argPointer.getnumOfPointers() != paramPointer.getnumOfPointers() ||
									argPointer.getBaseType().getClass() != paramPointer.getBaseType().getClass()){
								m_nNumErrors++;
								m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
										arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
								argError = false;
								counter++;	
								continue;
							}
						}else if (!(arg.getType() instanceof ArrayType) || !(arg.getType() instanceof PointerType)){
							m_nNumErrors++;
							m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
									arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
							argError = false;
							counter++;	
							continue;
						}
						
					}
					else if (arg.getType().getClass() !=(params.get(counter).getType().getClass())){
						if(!(arg.getType() instanceof IntType && params.get(counter).getType() instanceof FloatType)){
							m_nNumErrors++;
							m_errors.print (Formatter.toString(ErrorMsg.error5a_Call, 
									arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
							argError = false;
							counter++;	
							continue;
						}
						
						
					}		
				}
				else{
					//pass by reference
					if (arg.getType() instanceof StructType || 
							params.get(counter).getType() instanceof StructType){
								
						if (!(arg.getType() instanceof StructType) || 
									!(params.get(counter).getType() instanceof StructType)){	
							m_nNumErrors++;
							m_errors.print (Formatter.toString(ErrorMsg.error5r_Call, 
									arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
							argError = false;
							counter++;	
							continue;
							
						}
								
						if (DoStructEqualCheck(arg.getType() , params.get(counter).getType()) instanceof ErrorSTO) {
		    				m_nNumErrors++;
		    				m_errors.print (Formatter.toString(ErrorMsg.error5r_Call, 
									arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
		    				argError = false;
		    				counter++;	
							continue;
		    			}						
			
					
					}else if(arg.getType() instanceof ArrayType ||
								params.get(counter).getType() instanceof ArrayType ){
						//if both are not array type, say error
						if (!(arg.getType() instanceof ArrayType) || 
								!(params.get(counter).getType() instanceof ArrayType)){	
							//might need to change this
							m_nNumErrors++;
							m_errors.print (Formatter.toString(ErrorMsg.error5r_Call, 
									arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
							argError = false;
							counter++;	
							continue;
						
						}
						//at this point both are guaranteed to be array type 
						if (arg.getType().getUnderType().getClass() != params.get(counter).getType().getUnderType().getClass()|| 
								arg.getType().getSize() != params.get(counter).getType().getSize()){
							m_nNumErrors++;
							m_errors.print (Formatter.toString(ErrorMsg.error5r_Call, 
									arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
							argError = false;
							counter++;	
							continue;
						
						}
						
						if (arg.getType().getUnderType() instanceof StructType) {
							
							if (DoStructEqualCheck(params.get(counter).getType(), arg.getType().getUnderType()) instanceof ErrorSTO){

								m_nNumErrors++;
								m_errors.print (Formatter.toString(ErrorMsg.error5r_Call, 
										arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
								argError = false;
								counter++;	
								continue;
								
							}
					
						}
					
						
					}
					else if((arg.getType().getClass() !=(params.get(counter).getType().getClass()))) {
		
						m_nNumErrors++;
						m_errors.print (Formatter.toString(ErrorMsg.error5r_Call, 
								arg.getType().getName(),params.get(counter).getName(), params.get(counter).getType().getName()));
						argError = false;
						counter++;	
						continue;
					}
					
					if (!arg.isModLValue() && !(arg.getType() instanceof ArrayType)) {
						m_nNumErrors++;
						m_errors.print (Formatter.toString(ErrorMsg.error5c_Call, 
								params.get(counter).getName(), params.get(counter).getType().getName()));
						argError = false;
						counter++;	
						continue;
						
					}
				}
				
				counter++;		
				
			}

		}
		
		
		if (argError == false)
			return (new ErrorSTO ("x"));
		*/
		
		if (funcSto instanceof FuncSTO){
			FuncSTO f = (FuncSTO)funcSto;
			if( f.getIsReference() == true ){
				
				VarSTO xx = new VarSTO(f.getName(),f.getFuncType());
				xx.setIsFuncReturn(true);
				xx.setIsReturnByRef(true);
				
				if (f.getFuncType() instanceof StructType) {
					xx.setIsReturnStructByRef(true);
				}
				
				if (f.getFuncType() instanceof ArrayType ) {
					xx.setIsReturnArrayByRef(true);
				}
				
				return xx;
				
			}else{
				ExprSTO expr = new ExprSTO(f.getName(),f.getFuncType());
				expr.setIsModLValue(false);
				expr.setIsFuncReturn(true);
				
				if (f.getFuncType() instanceof StructType) {
					expr.setIsReturnStructByValue(true);
				}
				
				return expr;
			}
		}
		
		if (funcSto.getType() instanceof FunctionPointerType) {
			
			FunctionPointerType f = (FunctionPointerType) funcSto.getType();
			
			if( f.getOptRef() == true ){
				
				VarSTO xx = new VarSTO(f.getName(),f.getReturnType());
				xx.setIsFuncReturn(true);
				xx.setIsReturnByRef(true);
				
				if (f.getReturnType() instanceof StructType) {
					xx.setIsReturnStructByRef(true);
				}
				
				if (f.getReturnType() instanceof ArrayType ) {
					xx.setIsReturnArrayByRef(true);
				}
				
				return xx;
				
			}else{
				
				ExprSTO expr = new ExprSTO(f.getName(),f.getReturnType());
				expr.setIsModLValue(false);
				expr.setIsFuncReturn(true);
				
				
				if (f.getReturnType() instanceof StructType) {
					expr.setIsReturnStructByValue(true);
				}
				
				return expr;
			}
		}
	
		return null;
		
	}
    
    STO
    DoUnarySign( STO sto, String sign)
    {
    	if (sto instanceof ErrorSTO) {
    		return (new ErrorSTO ("x"));
    	}
    	

    	STO result = m_CodeGen.writeUnaryMinusOp(sto,sign);

    	
    	
    	if (sto instanceof ConstSTO && (sto.getType() instanceof NumericType)){
    		ConstSTO cS = (ConstSTO) sto;
    		if (sign.equals("-")){
    			ConstSTO csNew = new ConstSTO(cS.getName(),cS.getType(),cS.getFloatValue()*-1);
    			csNew.setOffset(result.getOffset());
    			return csNew;
    		}
    	}
    	
		return result;
    }
    
    
    STO
    DoReturnExpr( STO exprSto ) {
    	FuncSTO funcsto = this.m_symtab.getFunc();
    	//if (this.m_symtab.isInFunc() == false){
    	//	return (new ErrorSTO (funcsto.getName ()));
    	//}
    	funcsto.setHasReturn(true);
    	
    	if (funcsto.getIsReference() == true) {
    		exprSto.setIsReturnByRef(true);
    	}
    	
    	
    	int size = 0;
		if (exprSto.getType() instanceof StructType){
			size = ((ConstSTO)DoSizeOfType(exprSto.getType())).getIntValue();
		}
		
    	m_CodeGen.writeReturn(exprSto , funcsto , size);
    	
    	/*exprSto.setIsFuncReturn(true);//XXXXX
    	
    	if (funcsto.getIsReference() == true && funcsto.getFuncType() instanceof StructType) {
    		exprSto.setIsReturnStructByRef(true);
    	}
    	
    	if (funcsto.getIsReference() == true && funcsto.getFuncType() instanceof ArrayType) {
    		exprSto.setIsReturnArrayByRef(true);
    	}*/
    
    	/*
    	//check if the local scope has get Func
    	FuncSTO funcsto = this.m_symtab.getFunc();
    	if (this.m_symtab.isInFunc() == false){
    		return (new ErrorSTO (funcsto.getName ()));
    	}
    	
    	funcsto.setHasReturn(true);
    	
    	if (exprSto instanceof ErrorSTO) {
    		return (new ErrorSTO ("err"));
    	}
    	
    	if (funcsto.getIsReference() == true) {
    		
    		if (exprSto.getType().getClass() != (funcsto.getFuncType().getClass())) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error6b_Return_equiv, 
						exprSto.getType().getName(),funcsto.getFuncType().getName()));
				return (new ErrorSTO (funcsto.getName ()));
    		}
    		
    		if (exprSto.getType() instanceof StructType) {
    			
    			if (DoStructEqualCheck(funcsto.getFuncType() , exprSto.getType()) instanceof ErrorSTO) {
    				m_nNumErrors++;
    				m_errors.print (Formatter.toString(ErrorMsg.error6b_Return_equiv, 
    						exprSto.getType().getName(),funcsto.getFuncType().getName()));
    				return new ErrorSTO("x");
    				
    			}
    		}
    		
    		if (!exprSto.isModLValue()) {
    			m_nNumErrors++;
				m_errors.print (ErrorMsg.error6b_Return_modlval);
				return new ErrorSTO("x");
    		}
    	}
    	else {	
    		
    		
    		if (exprSto.getType() instanceof NullPointerType && funcsto.getFuncType() instanceof PointerType) {
    			return exprSto;
    		}
    		
    		if (exprSto.getType().getClass() != (funcsto.getFuncType().getClass())){
				if(!(exprSto.getType() instanceof IntType && funcsto.getFuncType() instanceof FloatType) && !(exprSto.getType() instanceof ArrayType)){
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error6a_Return_type, 
							exprSto.getType().getName(),funcsto.getFuncType().getName()));
					return (new ErrorSTO ("x"));
				}
			}
    		
    		
    		if (exprSto.getType() instanceof PointerType || funcsto.getFuncType() instanceof PointerType){
    			//check that funcsto is array then by default exprsto is pointer
    			if (exprSto.getType() instanceof ArrayType){
    				PointerType pRet = (PointerType) funcsto.getFuncType();
    				ArrayType aT = (ArrayType)exprSto.getType();
    				if (aT.getUnderType().getClass() != pRet.getPointerTo().getClass()){
    					m_nNumErrors++;
    					m_errors.print (Formatter.toString(ErrorMsg.error6a_Return_type, 
    							exprSto.getType().getName(),funcsto.getFuncType().getName()));
    					return (new ErrorSTO ("x"));
    				}
    			}
    			if (funcsto.getFuncType() instanceof PointerType && exprSto.getType() instanceof PointerType){

    			//both are pointers
    				if (exprSto.getType() instanceof NullPointerType) {
    					return exprSto;
    				}
	    			PointerType pFun = (PointerType) exprSto.getType();
					PointerType pRet = (PointerType) funcsto.getFuncType();
					
					
					if(pFun.getnumOfPointers() != pRet.getnumOfPointers() ||
							pFun.getBaseType().getClass() != pRet.getBaseType().getClass()){
	    				m_nNumErrors++;
	    				m_errors.print (Formatter.toString(ErrorMsg.error6a_Return_type, 
	    						exprSto.getType().getName(),funcsto.getFuncType().getName()));
	    				return new ErrorSTO("x");
					}
					//if one is a struct
					if (pFun.getBaseType() instanceof StructType) {
						
						if (DoStructEqualCheck(pFun.getBaseType() , pRet.getBaseType()) instanceof ErrorSTO) {
		    				m_nNumErrors++;
		    				m_errors.print (Formatter.toString(ErrorMsg.error6a_Return_type, 
		    						exprSto.getType().getName(),funcsto.getFuncType().getName()));
		    				return new ErrorSTO("x");
		    				
		    			}
					}
    			}
    			
    			if (funcsto.getFuncType() instanceof BasicType && exprSto.getType() instanceof BasicType){
    				m_nNumErrors++;
    				m_errors.print (Formatter.toString(ErrorMsg.error6a_Return_type, 
    						exprSto.getType().getName(),funcsto.getFuncType().getName()));
    				return new ErrorSTO("x");
    			}
						

    		}
    			
    		
    		if (exprSto.getType() instanceof StructType) {
    			
    			if (DoStructEqualCheck(funcsto.getFuncType() , exprSto.getType()) instanceof ErrorSTO) {
    				m_nNumErrors++;
    				m_errors.print (Formatter.toString(ErrorMsg.error6a_Return_type, 
    						exprSto.getType().getName(),funcsto.getFuncType().getName()));
    				return new ErrorSTO("x");
    				
    			}
    		}
    		
    	}
    	*/
		return exprSto;
    	
    }
    
    
    
	STO
	DoStructEqualCheck(Type des , Type expr) {
	
		if (!(des instanceof StructType)
				|| !(expr instanceof StructType)) {
			return new ErrorSTO("err");
		}
		
		StructType Des = (StructType)des;
		StructType Expr = (StructType)expr;
		
		String DesName = null;
		String ExprName = null;
		
		if (Des.getBaseName() == null) {
			DesName = Des.getName();
		}else {
			DesName = Des.getBaseName();
		}
		
		if (Expr.getBaseName() == null) {
			ExprName = Expr.getName();
		}else {
			ExprName = Expr.getBaseName();
		}
		
		
		
		if (!DesName.equals(ExprName) ) {
			m_nNumErrors++;
			return new ErrorSTO("err");
		}
		
		
		return new VarSTO("x");
	}
    
    
    
    
    
    
    
    
    
    STO
    DoReturnVoid( ) {
    	
    	FuncSTO funcsto = this.m_symtab.getFunc();
    	funcsto.setHasReturn(true);
    	
    	if (!(this.m_symtab.getFunc().getFuncType() instanceof VoidType)) {
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error6a_Return_expr);
			return (new ErrorSTO (null));
    	} 	
		return null; 
    }


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
    Type
    DoDeclType( STO exprSTO ) {
 
    	int mark = m_DeclStack.pop();
    	m_CodeGen.writeDeclType(mark);
    	
    	return exprSTO.getType();
    }
    
    
    void
    DoDeclType2() {
    	DeclCounter++;
    	m_CodeGen.writeDeclTypePass(DeclCounter);
    	m_DeclStack.push(DeclCounter);

    }
    
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
   
	STO
	DoDesignator2_Dot (STO sto, String strID)
	{

		
		boolean flag = false;
		if (!(sto.getType() instanceof StructType)) {
			m_nNumErrors++;
		 	m_errors.print (Formatter.toString(ErrorMsg.error14t_StructExp, 
		 			sto.getType().getName()));	
		 	return sto;
		}
		// Good place to do the struct checks

		Vector<STO>fields = m_StructInfo.get(sto.getType().getName());
		STO temp = null;
		
		
		int offsetFromStruct = 0;
		
		for (STO id : fields) {
			
			if (id.getName().equals(strID)) {
				flag = true;
				temp = id;
				break;
			}else{ //CHANGED THIS LINE
				offsetFromStruct += ((ConstSTO)DoSizeOf(id)).getIntValue(); //CHANGED THIS LINE
			}//CHANGED THIS LINE
		}
		
		if (flag == false) {
			m_nNumErrors++;
		 	m_errors.print (Formatter.toString(ErrorMsg.error14f_StructExp, 
		 			strID,sto.getType().getName()));	
			
		}
		
		
		if (sto.getLabelName() == null){
			//first dot derefernce
			temp.setLabelName(sto.getName());

		}else{
			//every dereference after that
			temp.setLabelName(sto.getLabelName());
		}

		temp.setIsGlobal(sto.getIsGlobal());
		temp.setExtraOffset(offsetFromStruct);
		temp.setOffset(sto.getOffset());    //TEST IN
		temp.setNeedsExtraOffSet(true);
		
		if ( sto.getExtraOffset() == 0){
			//top level struct
			temp.setOffset(sto.getOffset());
		}else{
			//struct within a struct
			//temp.setOffset(sto.getExtraOffset()+sto.getOffset()); // TEST OUT
			temp.setExtraOffset(sto.getExtraOffset() + offsetFromStruct);	//TEST IN
		}


		temp.setIsStructPassedByRef(sto.getIsPassedByRef());
		temp.setIsPassedByRef(sto.getIsPassedByRef());
		
		temp.setIsPassedByValue(sto.getIsPassedByValue());
		temp.setIsStructPassedByValue(sto.getIsPassedByValue());
		
		temp.setIsFuncReturn(sto.getIsFuncReturn());
		
		temp.setIsReturnStructByRef(sto.getIsReturnStructByRef());
		temp.setIsReturnStructByValue(sto.getIsReturnStructByValue());
		return temp;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	
	STO
	DoDesignator2_Array (STO sto, STO exprSTO)
	{
	
		if (sto instanceof ErrorSTO || exprSTO instanceof ErrorSTO) {
			return sto;
		}
		
		// Good place to do the array checks
		if (!(sto.getType() instanceof ArrayType)
				&& !(sto.getType() instanceof PointerType)) {
			m_nNumErrors++;
		 	m_errors.print (Formatter.toString(ErrorMsg.error11t_ArrExp, 
		 			sto.getType().getName()));	
		}
		
		
		if (sto.getType() instanceof NullPointerType) {
			m_nNumErrors++;
		 	m_errors.print (Formatter.toString(ErrorMsg.error11t_ArrExp, 
		 			sto.getType().getName()));	
			return new ErrorSTO ("err");
		}
		
		
		
		if (!(exprSTO.getType() instanceof IntType)) {
			m_nNumErrors++;
		 	m_errors.print (Formatter.toString(ErrorMsg.error11i_ArrExp, 
		 			exprSTO.getType().getName()));	
			return new ErrorSTO ("err");
		}

		if ((exprSTO instanceof ConstSTO)
				&& (sto.getType() instanceof ArrayType)) {
			ConstSTO valueSto = (ConstSTO)exprSTO;

			int value = valueSto.getIntValue();
			//System.out.println(value);
		
			
			//System.out.println(sto.getType().getUnderType().getUnderType().getSize());
			
			if (value < 0 || value > sto.getType().getSize() - 1) {
				m_nNumErrors++;
			 	m_errors.print (Formatter.toString(ErrorMsg.error11b_ArrExp, 
			 			value,sto.getType().getSize()));	
				return new ErrorSTO ("err");
			}
		}
		
		VarSTO vs = null;
		
		if (sto.getType() instanceof PointerType) {
			vs = new VarSTO(sto.getName(),((PointerType)sto.getType()).getPointerTo());
		}else {
			vs = new VarSTO(sto.getName(),sto.getType().getUnderType());
		}
		

		if (sto.getLabelName() == null){
			vs.setLabelName(sto.getName());
		}else{
			vs.setLabelName(sto.getLabelName());
		}
		
		vs.setOffset(sto.getOffset());
		
		if (sto.getType() instanceof PointerType){
			//do an extra load
			m_CodeGen.writeLoad(vs, "%l7", 0);
			vs.setIsPtrArray(true);
		}
		
		vs.setNeedsExtraOffSet(true);
		vs.setIsGlobal(sto.getIsGlobal());
		
		


		if  (exprSTO instanceof VarSTO || exprSTO instanceof ExprSTO){
			vs.setIsWrappedArray(true);
			if (exprSTO.getNeedsExtraOffSet() == false){
				m_CodeGen.writeLoad(exprSTO, "%l6", 0);
				m_CodeGen.writeFileInfo(Template.FOUR_PARAM, "sll", "%l6", "2", "%l5");
			}else{
				m_CodeGen.writeFileInfo(Template.FOUR_PARAM, "sll", "%l6", "2", "%l5");
				if (vs.getType() instanceof IntType){
					m_CodeGen.writeLoad(vs, "%l6", 0);
				}
			}
			m_CodeGen.writeBoundCheck(vs,sto);

		}else{
			int scaled = 0;
			vs.setIsWrappedArray(false);

			// basic type constan
			if ((sto.getType() instanceof PointerType)){
				
				scaled = ((ConstSTO)DoSizeOfType(sto.getType())).getIntValue();
			}else{
				scaled = ((ConstSTO)DoSizeOfType(sto.getType().getUnderType())).getIntValue();
			}
			vs.setExtraOffset(((ConstSTO)exprSTO).getIntValue()*scaled + sto.getExtraOffset());
			m_CodeGen.writeLoad(vs, "%l6", 0);
			//m_CodeGen.writeFileInfo(Template.FOUR_PARAM, "sll", "%l6", "2", "%l5");  moved this up ^
		}

		
		vs.setIsReturnArrayByRef(sto.getIsReturnArrayByRef());
		return vs;
	}


	
	void
	DoDebug()
	{
		m_CodeGen.writeDebug();
	}
	
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoDesignator3_ID (String strID)
	{
		STO		sto;
		

		if ((sto = m_symtab.access (strID)) == null)
		{

			m_nNumErrors++;
		 	m_errors.print (Formatter.toString(ErrorMsg.undeclared_id, strID));	
			sto = new ErrorSTO (strID);
		}
		return (sto);
	}
	
	STO
	DoDesignator4_ID (String strID)
	{
		STO		sto;
		

		if ((sto = m_symtab.accessGlobal (strID)) == null)
		{

			m_nNumErrors++;
		 	m_errors.print (Formatter.toString(ErrorMsg.error0g_Scope, strID));	
			sto = new ErrorSTO (strID);
		}
		return (sto);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoQualIdent (String strID)
	{
		STO		sto;

		if ((sto = m_symtab.access (strID)) == null)
		{
			m_nNumErrors++;
		 	m_errors.print (Formatter.toString(ErrorMsg.undeclared_id, strID));	
			return (new ErrorSTO (strID));
		}

		if (!(sto instanceof TypedefSTO))
		{
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.not_type, sto.getName ()));
			return (new ErrorSTO (sto.getName ()));
		}

		
		return (sto);
	}

	public static AssemblyCodeGenerator getCodeGen() {
		return m_CodeGen;
	}
	
	
	public static SymbolTable		getSymTable() {
		return m_symtab;
	}
	
	public static String popIfStack() {
		return m_IfStack.pop();
	}
	
	
	public static void	pushIfStack( String label ) {
		m_IfStack.push(label);
	}
	
	
	public static String popWhileStack() {
		return m_WhileStack.pop();
	}
	
	
	public static void	pushWhileStack( String mark ) {
		m_WhileStack.push(mark);
	}
	
	public static String popWhileEndStack() {
		return m_WhileEndStack.pop();
	}
	
	
	public static void	pushWhileEndStack( String mark ) {
		m_WhileEndStack.push(mark);
	}
	
	
	
	public static String popElseStack() {
		return m_ElseStack.pop();
	}
	
	
	public static void	pushElseStack( String mark ) {
		m_ElseStack.push(mark);
	}
	
	
	public static int popAndStack() {
		return m_AndStack.pop().intValue();
	}

	
	public static void	pushAndStack( int num ) {
		m_AndStack.push(Integer.valueOf(num));
	}
	
	
	
	public static int popOrStack() {
		return m_OrStack.pop().intValue();
	}

	
	public static void	pushOrStack( int num ) {
		m_OrStack.push(Integer.valueOf(num));
	}
	
	
	
	
	
//----------------------------------------------------------------
//	Instance variables
//----------------------------------------------------------------
	private int 			m_staticCounter = 0;
	private Lexer			m_lexer;
	public static ErrorPrinter		m_errors;
	private int 			m_nNumErrors;
	private String			m_strLastLexeme;
	private boolean			m_bSyntaxError = true;
	private int			m_nSavedLineNum;
	private static HashMap<String,Vector<STO>>	m_StructInfo = new HashMap<String, Vector<STO>>();
	
	private ArrayList<FuncSTO>		m_funcTable = new ArrayList<FuncSTO>();
	private static SymbolTable		m_symtab;
	
	private static AssemblyCodeGenerator		m_CodeGen = new AssemblyCodeGenerator("rc.s");
	private static int IfCounter = 0;	
	private static int WhileCounter = 0;
	private static int ElseCounter = 0;
	private static int WhileEndCounter = 0;
	private static int AndCounter = 0;
	private static int OrCounter = 0;
	private static int DeclCounter = 0;
	
	
	private static Stack<String> 	m_IfStack = new Stack<String>();
	static Stack<String> 	m_WhileStack = new Stack<String>();
	static Stack<String> 	m_WhileEndStack = new Stack<String>();
	private static Stack<String> 	m_ElseStack = new Stack<String>();
	private static Stack<Integer> 	m_AndStack = new Stack<Integer>();
	private static Stack<Integer> 	m_OrStack = new Stack<Integer>();
	private static Stack<Integer> 	m_DeclStack = new Stack<Integer>();
	
	
	
}
