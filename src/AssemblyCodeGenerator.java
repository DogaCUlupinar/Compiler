import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;


public class AssemblyCodeGenerator {
	
	private FileWriter fileWriter;
	private int indent_level = 0;
	private int tempLabel = 0;

	private int derefCheckLabel = 0;
	private int boundCheckLabel = 0;
	
	private int extraCredit1label = 0;
	private int extraCredit1checklabel = 0;

	
	// Indent thing
    public void decreaseIndent() {
        indent_level--;
    }
    
    public void dispose() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            System.err.println(Template.ERROR_IO_CLOSE);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void increaseIndent() {
        indent_level++;
    }
   // Indent thing
    
    
    public void writeDebug(){
    	writeFileInfo(Template.BLANK,"!!!");
    }
    
    
  //CHANGED FROM HERE
  	public void writeStruct (STO sto, int StructSize) {
  		//increaseIndent()();
  		//this is for structs.. DUH
  		writeFileInfo(Template.GLOBAL,sto.getName());
  		writeFileInfo(Template.SECTION_DATA);
  		writeFileInfo(Template.ALIGN,"4");
  		//decreaseIndent()();
  		writeFileInfo(Template.LABEL,sto.getName());
  		int numtimes = StructSize/4;
  		for (int i = 0; i < numtimes ; i++){
  			writeFileInfo(Template.WORD,Integer.toString(0));
  		}
  		writeFileInfo(Template.NewLine);
  		
  	}
  	//CHANGED TO HERE
  	
    
  
    
    // Constructor
	public AssemblyCodeGenerator(String fileToWrite) {
		try {
		
			fileWriter = new FileWriter(fileToWrite);
			writeFileInfo(Template.FILE_HEADER, (new Date()).toString());
			
		} catch (IOException e) {
			System.err.printf(Template.ERROR_IO_CONSTRUCT, fileToWrite);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	
	// Write File Info
	public void writeFileInfo(String template, String ... params) {
		StringBuilder asStmt = new StringBuilder();

		for (int i=0; i < indent_level; i++) {
			asStmt.append(Template.SEPARATOR);
		}

		asStmt.append(String.format(template, (Object[])params));

		try {
			fileWriter.write(asStmt.toString());
		} catch (IOException e) {
			
			System.err.println(Template.ERROR_IO_WRITE);
			e.printStackTrace();
		}
	}
	
	public void writeUninitialVar (STO sto) {
		
		
		
		//increaseIndent()();

		String name = "";
		if (sto.getLabelName() == null){
			name = sto.getName();
		}else{
				name = sto.getLabelName();
		}
		if (sto.getIsGlobal() == true){
			writeFileInfo(Template.GLOBAL,name); //CHANGE4 this line
		}
		
		
		writeFileInfo(Template.SECTION_DATA);
		writeFileInfo(Template.ALIGN,"4");
		//decreaseIndent()();
		writeFileInfo(Template.LABEL,name);//CHANGE4 this line
		
		if (sto.getType() instanceof FloatType) {
			writeFileInfo(Template.SINGLE,Integer.toString(0));
		}else {
			writeFileInfo(Template.WORD,Integer.toString(0));
		}
		
		
		
		if (sto.getIsStatic() == true){
			writeFileInfo(Template.SECTION_TEXT);
			writeFileInfo(Template.ALIGN,Integer.toString(4));
			writeFileInfo(Template.NewLine);
		}
		
		
		writeFileInfo(Template.NewLine);
		
	}
	
	
	public void writeInitialVar (STO left , STO right) {
		//increaseIndent()();
	

		String name = "";
		if (left.getLabelName() == null){
			name = left.getName();
		}else{
				name = left.getLabelName();
		}
		if (left.getIsGlobal() == true){
			writeFileInfo(Template.GLOBAL,name);//CHANGE4 this
		}
		
		
		writeFileInfo(Template.SECTION_DATA);
		writeFileInfo(Template.ALIGN,"4");
		//decreaseIndent()();
		writeFileInfo(Template.LABEL,name);//CHANGE4 this line
		
		if (left.getType() instanceof FloatType) {
			writeFileInfo(Template.SINGLE,Float.toString(((ConstSTO)right).getFloatValue()));
		}else if (left.getType() instanceof BoolType) {
			
			if (((ConstSTO)right).getIntValue() == 0) {
				//writeFileInfo(Template.ASCIZ,"\"false\"");
				writeFileInfo(Template.WORD,"0");
			}else {
				//writeFileInfo(Template.ASCIZ,"\"true\"");
				writeFileInfo(Template.WORD,"1");
			}
			
		}
		else {
			writeFileInfo(Template.WORD,Integer.toString(((ConstSTO)right).getIntValue()));
		};
		
		if (left.getIsStatic() == true){
			writeFileInfo(Template.SECTION_TEXT);
			writeFileInfo(Template.ALIGN,Integer.toString(4));
			writeFileInfo(Template.NewLine);
		}
		
		writeFileInfo(Template.NewLine);
		
	}
	
	
	public void writeFuncOpen (String name) {
		
		//increaseIndent()();
		
		writeFileInfo(Template.SECTION_TEXT);
		writeFileInfo(Template.GLOBAL,name);
		writeFileInfo(Template.ALIGN,Integer.toString(4));
		
		//decreaseIndent()();
		writeFileInfo(Template.LABEL2,name);
		//increaseIndent()();
		
		writeFileInfo(Template.CSAVE_OP,name+",","%g1");
		writeFileInfo(Template.FOUR_PARAM ,Template.SAVE_OP ,"%sp", "%g1", "%sp");
		//decreaseIndent()();
		writeFileInfo(Template.NewLine);
		
	}
	
	
	public void writeFuncClose (int offset, String name , int flag) {
		
		//increaseIndent()();
		writeExtraCredit1();
		
		
		
		writeFileInfo(Template.LABEL2,name+"XoX");
		
		if (flag == 1) {
		writeFileInfo(Template.RET_OP);
		writeFileInfo(Template.RESTORE_OP);
		}
		
		//decreaseIndent()();
		writeFileInfo(Template.NewLine);
		
		//increaseIndent()();
		//finish SAVE.main;
		offset = offset*-1 - 4;
		writeFileInfo(Template.SAVEND,name,Integer.toString(offset));
		
	}

	
	

	
	public void writeLiteral(ConstSTO sto){
		String name = null;
		if (sto.getType() instanceof FloatType){
			//a literal float 4.2;
			tempLabel++;
			name = "tmp"+Integer.toString(tempLabel);
			//increaseIndent()();
			writeFileInfo(Template.SECTION_DATA);
			writeFileInfo(Template.ALIGN,Integer.toString(4));
			writeFileInfo(Template.LABEL,name);
			writeFileInfo(Template.SINGLE,String.valueOf(sto.getFloatValue()));
	
			
			writeFileInfo(Template.SECTION_TEXT);
			writeFileInfo(Template.ALIGN,Integer.toString(4));
			writeFileInfo(Template.NewLine);
			sto.setLabelName(name);
			this.writeLoad(sto,"%f0",0);
			//decreaseIndent()();
		}else if (sto.getType() instanceof StrType){
			//a literal string "wow i am so cool";
			tempLabel++;
			name = "tmp"+Integer.toString(tempLabel);
			//increaseIndent()();
			writeFileInfo(Template.SECTION_DATA);
			writeFileInfo(Template.ALIGN,Integer.toString(4));
			writeFileInfo(Template.LABEL,name);
			writeFileInfo(Template.ASCIZ,"\""+sto.getStrValue()+"\"");
	
			
			writeFileInfo(Template.SECTION_TEXT);
			writeFileInfo(Template.ALIGN,Integer.toString(4));
			writeFileInfo(Template.NewLine);
			sto.setLabelName(name);
			writeFileInfo(Template.THREE_PARAM, Template.SET_OP , name , "%o1");
			//decreaseIndent()();
		}else if (sto.getType() instanceof BoolType) {
			
			if (sto.getIntValue() == 0) {
				writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "0" , "%l1");
			}else {
				writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "1" , "%l1");
			}
		}
		else{
			//increaseIndent()();
			writeFileInfo(Template.THREE_PARAM, Template.SET_OP , String.valueOf(sto.getIntValue()) , "%l1");
			//decreaseIndent()();
			sto.setLabelName(name);
			//this.writeLoad(sto,"%l1");   ******** comment this but.........
		}
		//load to register

		
	}
	

	public void writeInitLocalLiteral(ConstSTO csto, STO sto) {
		// int x = 3;
		writeLiteral(csto);
		
		if (sto.getType() instanceof FloatType && !(csto.getType() instanceof FloatType)) {
			
			STO temporary = new ExprSTO("promote",sto.getType());
			temporary.setOffset(MyParser.getSymTable().getLocalOffset());
			
			writeStore(temporary,"%l1");
			writeLoad(temporary,"%f0",1);
			writeFileInfo(Template.THREE_PARAM, Template.FITOS , "%f0", "%f0");
			
		}
		
		writeStore(sto,null);
		writeFileInfo(Template.NewLine);

	}
	
	
	public void writeLoadFunction(STO sto, String spec)
	{
		
		String fname = sto.getName();	
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, fname,  spec);
		
	}
	
	public void writePtrArray(STO sto, String spec){
		String offset = Integer.toString(sto.getExtraOffset());
		writeFileInfo(Template.NewLine);
		if (sto.getIsWrappedArray() == false){
			writeFileInfo(Template.THREE_PARAM, Template.SET_OP, offset, "%l5");
		}
		
		writeFileInfo(Template.FOUR_PARAM, Template.ADD_OP, "%l7", "%l5", "%l0");
		writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP, "[%l0]", spec);
	}
	
	public void writeLoad(STO sto, String spec, int flag){//somethimes we load to %o1/%l1 . Using spec
		//loads 
		String var = null;
		String reg = null;
		boolean ArrayOrStructPassedByRef = false;
		boolean ArrayOrStructFlag = false;
		
		boolean FuncReturnStructOrArray = false;
		if (sto.getIsPtrArray() == true){
			writePtrArray(sto,spec);
			return;
		}
		if (sto.getIsReturnStructByRef() || sto.getIsReturnArrayByRef()) {
			FuncReturnStructOrArray = true;
		}
		
		if (sto instanceof FuncSTO) // a = foo ; for function pointer
		{
			writeLoadFunction(sto,spec);
			return;
		}
		//CHANGE2 from here
		/* to do pointer deref */
		if (sto.getIsPointerDeRef() == true){
			if (sto.getType() instanceof FloatType){
				spec = "%f0";
			}
			writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP, "%l5", "%l0");
			
			if (sto.getIsPassedByRef() == true ) {
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP, "[%l0]", "%l0");
			}
			
			writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[%l0]" , spec);
			return;
		}
		//to here
				
				
		
		if (sto.getIsArrayPassedByRef() || sto.getIsStructPassedByRef() ) {
			ArrayOrStructFlag = true;
		}
		
		if (sto.getIsStructPassedByValue() == true ) {//struct pass by value
			ArrayOrStructFlag = true;
		}
		
		
		if (sto.getIsFuncReturn() == true && FuncReturnStructOrArray == false) {
			
			if (sto.getIsReturnByRef() == true) {//return by ref
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[%o0]" , spec);
			}else { // return by value
				
				if (sto.getType() instanceof FloatType) {
					writeFileInfo(Template.THREE_PARAM, Template.FMOVE_OP , "%f30" , spec);
				}else if (sto.getIsReturnStructByValue()) {
					writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[%sp + 64]" , spec);
				}
				else {
					writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP , "%o0" , spec);
				}
			}
			
			return ;
			
		}
		
		if (sto.getType() instanceof BoolType && (flag != 1)) {
			if (sto.getName().equals("Literal")){ // ???????????????????? problem ?????
				
				if (((ConstSTO)sto).getIntValue() == 0) {
					var = "0";
				}else {
					var = "1";
				}
				
				
				writeFileInfo(Template.THREE_PARAM, Template.SET_OP , var , spec);
				return;
			}else{
				var = Integer.toString(sto.getOffset());
			}
			
			
		}
		
	if (sto.getName().equals("Literal") && !(sto.getType() instanceof StrType) && !(sto.getType() instanceof FloatType)){
			writeFileInfo(Template.THREE_PARAM, Template.SET_OP , 
				Integer.toString(((ConstSTO)sto).getIntValue()) , spec);
		return ;
		}
		
		if (sto.getType() instanceof FloatType && (flag != 1)){
			spec = "%f0";
		}
		
		if (sto.getIsGlobal() || sto.getIsExtern() || sto.getIsStatic() ){
			
			if (sto.getLabelName() == null){
				var = sto.getName();
			}else{
				var = sto.getLabelName();
			}
			reg = "%g0";
			
		}else if(sto.getName().equals("Literal")){ //&& !(sto.getType() instanceof BoolType)){

			if (sto.getIsInitialized() == false){
				var = sto.getLabelName();
				//sto.setIsInitialized(true);  //?????????????
				reg = "%g0";

				
			}else{
			
				var = Integer.toString(sto.getOffset());
				reg = "%fp";
			}
			
		}else if (sto.getIsArrayPassedByRef() || sto.getIsStructPassedByRef() || 
						sto.getIsStructPassedByValue()) {//pass array by ref
			ArrayOrStructPassedByRef = true;

		}
		else{
			var = Integer.toString(sto.getOffset());
			reg = "%fp";
		}
		//increaseIndent()();
		
		
		if (sto.getIsArrow() == true){
			writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP , "%l5" , "%l0");
		}else {
			
			if (ArrayOrStructPassedByRef == false  ) {
				//if (sto is func return
				//move
				if (FuncReturnStructOrArray == true) {
					writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP , "%o0" , "%l0");
				}else {
					writeFileInfo(Template.THREE_PARAM, Template.SET_OP , var , "%l0");
				}
				
			}else { // ArrayPassedByRef
				var = "[%fp + "+ sto.getOffset() + "]";
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , var , "%l0");
			}
		}
		//CHANGE FROM HERE
		if (sto.getNeedsExtraOffSet()){
			String offset = "";
			if (sto.getIsWrappedArray() == true){
				//this is a qrapped array a[a[2]]
				if (flag == 3){
					//this is the final layer of the array
					writeFileInfo(Template.FOUR_PARAM, "sll", "%l6", "2", "%l5");
				}
				offset = "%l5";
			}else{

				offset = Integer.toString(sto.getExtraOffset());
				writeFileInfo(Template.THREE_PARAM, Template.SET_OP, offset, "%l5");
			}
			//add the array offset
			
			writeFileInfo(Template.FOUR_PARAM, Template.ADD_OP, "%l0", "%l5" , "%l0");
		}
		//TO HERE
				
				
				
		if (ArrayOrStructPassedByRef == false && sto.getIsArrow() == false &&
				FuncReturnStructOrArray == false) {	// Special for array passed by ref
			writeFileInfo(Template.FOUR_PARAM, Template.ADD_OP, reg, "%l0" , "%l0");
		}
		
	
		if (sto.getIsPassedByRef() == true && ArrayOrStructFlag == false) { // DANGEROUS!!!!!1
			
			if (sto.getType() instanceof FloatType) {
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[%l0]" , "%l3");
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[%l3]" , spec);
				
			}else {
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[%l0]" , spec);
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[" + spec + "]" , spec);
			}
			
		}else {
			if (sto.getType() instanceof ArrayType){
				writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP , "%l0" , "%l1");
			}else{
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[%l0]" , spec);
			}
		}
		
		
		//decreaseIndent()();
		writeFileInfo(Template.NewLine);
	}

	
	
	public void writeStore(STO sto , String specx) {
		//stores
	
		String var = null;
		String reg = null;
		String spec = "%l1";
		boolean normalStore = true;
		
		if (sto.getIsArrayPassedByRef() || sto.getIsStructPassedByRef() ||
					sto.getIsStructPassedByValue()) {
			normalStore  = false;
		}
		
		if ( sto.getIsAddressOf() == true ){
			//writeLoad(sto, "%l1" , 0);//QQQ
			return;
		}
		if (sto.getType() instanceof FloatType) {
			spec = "%f0";
		}
		if (sto.getIsPointerDeRef() == true){
			
			if (sto.getIsPassedByRef() == true) {
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP, "[%l5]" , "%l5");
			}
			
			writeFileInfo(Template.THREE_PARAM, Template.STORE_OP, spec , "[%l5]");
			return;
		}

		if (sto.getIsGlobal() || sto.getIsExtern() || sto.getIsStatic() ){
			
			if ( sto.getLabelName() == null){
				var = sto.getName();
			}else{
				var = sto.getLabelName();
			}
			
			reg = "%g0";
			
			
		}else{
			var = Integer.toString(sto.getOffset());
			reg = "%fp";
		}
		
		if (sto.getType() instanceof FloatType) {
			spec = "%f0";
		}
		//increaseIndent()();
		
		if (specx != null) {
			spec = specx;
		}
		
		if (sto.getIsFuncReturn() && sto.getIsReturnByRef()) { // store into func return
			writeFileInfo(Template.THREE_PARAM, Template.STORE_OP, spec , "[%o0]");
			return ;
		}
		
		
		if (sto.getIsArrow() == true){
			
			writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP , "%l5" , "%l0");
		}else {
		
			if (normalStore == true && sto.getIsArrow() == false) {
				writeFileInfo(Template.THREE_PARAM, Template.SET_OP , var , "%l0");
				
			}else { // ArrayPassedByRef
				var = "[%fp + "+ sto.getOffset() + "]";
				writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , var , "%l0");
			}
		}
		
		
		if (sto.getNeedsExtraOffSet()){
			//add the array offset
			String offset = Integer.toString(sto.getExtraOffset());
			if (sto.getIsWrappedArray() == true){
				offset = "%l5";
			}else{
				writeFileInfo(Template.THREE_PARAM, Template.SET_OP, offset, "%l5");
			}
			writeFileInfo(Template.FOUR_PARAM, Template.ADD_OP, "%l0", "%l5" , "%l0");
		}
		
		
		if (normalStore == true && sto.getIsArrow() == false) {
			writeFileInfo(Template.FOUR_PARAM, Template.ADD_OP, reg, "%l0" , "%l0");
		}
		
		if (sto.getIsPassedByRef() == true && normalStore == true) {
			writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP, "[%l0]" , "%l0");  //DANGEROUS !!!!!!!!!
		}

		writeFileInfo(Template.THREE_PARAM, Template.STORE_OP, spec , "[%l0]");
		
		//decreaseIndent()();
		writeFileInfo(Template.NewLine);
		
	}

	
	
	
	public void writeAllocateExtraSpace(Type typ , int m_size) {
		StructType st = (StructType)typ;
		int offset = MyParser.getSymTable().getLocalOffset(m_size);
		writeFileInfo(Template.FOUR_PARAM, Template.ADD_OP, "%fp" , String.valueOf(offset) , "%l0" );
		writeFileInfo(Template.THREE_PARAM, Template.STORE_OP, "%l0" , "[%sp + 64]" );
		
	}
	
	
	
	public void writeReturnStruct(STO stoExpr, int size) {
		String structSize = Integer.toString(size);
		String varExpr = "";
		String regExpr = "%fp";
		if (stoExpr.getIsGlobal() || stoExpr.getIsExtern() || stoExpr.getIsStatic() ){
			
			if (stoExpr.getLabelName() == null){
				varExpr = stoExpr.getName();
			}else{
				varExpr = stoExpr.getLabelName();
			}
			regExpr = "%g0";
		}else{
			//
			varExpr = Integer.toString(stoExpr.getOffset());
		}
		
		writeFileInfo(Template.THREE_PARAM,Template.SET_OP,varExpr,"%l0");
		writeFileInfo(Template.FOUR_PARAM,Template.ADD_OP,regExpr,"%l0","%o1");
		writeFileInfo(Template.NewLine);
		
		//designator
	
		writeFileInfo(Template.FOUR_PARAM,Template.ADD_OP,"%fp","64","%o0");
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP,structSize,"%o2");
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP,"memmove");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//assign back into des
		
	}
	
	
	
	
	public void writeFuncCall(STO funcSto, Vector<STO> args , int struct_size) {
		
		String var = null;
		String reg = null;
		Vector<Parameter> fparams = null;
		
		
		
		if (funcSto instanceof FuncSTO) {//normal function call
			fparams = ((FuncSTO)funcSto).getParameters();
			
			if ( ((FuncSTO)funcSto).getFuncType() instanceof StructType
					&& ((FuncSTO)funcSto).getIsReference() == false) {//call function with struct return by value
				writeAllocateExtraSpace(((FuncSTO)funcSto).getFuncType() , struct_size);  //allocate extra space for return value
				
			}
			
			
			
		}else if (funcSto.getType() instanceof FunctionPointerType) {//function pointer call
			
			FunctionPointerType temptyp = (FunctionPointerType)funcSto.getType();
			fparams = temptyp.getParamsList();
		}
		
		//increaseIndent()();
		
		if (args == null) {
			
		}else {
			for (int i=0; i<args.size() ;i++) {
				STO arg = args.get(i);
				reg = "%o"+i;
				
				if (i >= 6) // pass args more than 6
				{
					reg = "%l0";
				}
				
				if (args.get(i).getName().equals("Literal")) {
					writeLiteral((ConstSTO)args.get(i));
				}
				
				if (fparams.get(i).getReference() == true || 
						fparams.get(i).getType() instanceof StructType) { // pass by ref or pass struct
					writeLoad(arg,"%l4", 0);
					
					if (arg.getIsReturnByRef()) {
						moveRegister("%o0",reg);
					}else {
						moveRegister("%l0",reg);
					}
					
				}else { // pass by value
					
					if (arg.getType() instanceof FloatType) {
						
						if (arg.getIsFuncReturn() == true) {
							writePassFloatReturn(arg ,reg);
						}else {
							writeLoad(arg, reg, 1);
						}
						
					}else {
						
						writeLoad(arg,"%l4", 0);
						
						if (arg.getIsAddressOf() == false) {
							moveRegister("%l4",reg);
						}else {				
						
							writeLoad (arg, reg, 0);//QQQ
							
						}
						
						if (arg.getType() instanceof IntType
								&& fparams.get(i).getType() instanceof FloatType) {
							writePassPromote(reg);
					
						}
					}
						
				}
				
				if (i >= 6) {
					int add = 92 + 4*(i - 6);
					String tempx = "[%sp + " + add + "]";
					writeFileInfo(Template.THREE_PARAM, Template.STORE_OP , reg ,  tempx);
				}
				
				
		
			}//end for
		
		}
		
		if (funcSto instanceof FuncSTO) { // normal function call
			writeFileInfo(Template.TWO_PARAM, Template.CALL_OP , funcSto.getName());
		}else {
			writeLoad(funcSto, "%l0" ,0);
			writeFileInfo(Template.TWO_PARAM, Template.CALL_OP , "%l0");
		}
		
		writeFileInfo(Template.NOP_OP);
		//decreaseIndent()();
		writeFileInfo(Template.NewLine);
		
		if (args != null && args.size() > 6) {
			int vx = (args.size() - 6)*4*-1;
			String v  = vx + "& -8";
			writeFileInfo(Template.FOUR_PARAM, Template.SUB_OP , "%sp" , v , "%sp" );
		
		}
		
	}

	
	
	
	public void writePassPromote ( String reg ) {
		
		int offset = MyParser.getSymTable().getLocalOffset();
		String temp = "[%fp " + offset + "]";
		writeFileInfo(Template.THREE_PARAM , Template.STORE_OP , reg , temp);
		writeFileInfo(Template.THREE_PARAM , Template.LOAD_OP , temp , "%f3");
		
		writeFileInfo(Template.THREE_PARAM,Template.FITOS, "%f3" , "%f3");//MMC
		writeFileInfo(Template.THREE_PARAM , Template.STORE_OP , "%f3" , temp);
		writeFileInfo(Template.THREE_PARAM , Template.LOAD_OP , temp , reg);
		
		writeFileInfo(Template.NewLine);
		
	}
	
	
	
	public void writePassFloatReturn (STO sto , String reg)
	{
		int offset = MyParser.getSymTable().getLocalOffset();
		String temp = "[%fp " + offset + "]";
		writeFileInfo(Template.THREE_PARAM , Template.STORE_OP , "%f30" , temp);
		writeFileInfo(Template.THREE_PARAM , Template.LOAD_OP , temp , reg);
		
		
	}
	
	
	public void writeGlobalDef() {

		//increaseIndent()();

		writeFileInfo(Template.SECTION_RODATA);

		//decreaseIndent()();
		writeFileInfo(Template.LABEL,"_endl");
		writeFileInfo(Template.ASCIZ, "\"\\n\"");
		writeFileInfo(Template.LABEL,"_intFmt");
		writeFileInfo(Template.ASCIZ, "\"%d\"");
		writeFileInfo(Template.LABEL,"_strFmt");
		writeFileInfo(Template.ASCIZ, "\"%s\"");
		
		writeFileInfo(Template.LABEL,"_boolT");
		writeFileInfo(Template.ASCIZ, "\"true\"");
		writeFileInfo(Template.LABEL,"_boolF");
		writeFileInfo(Template.ASCIZ, "\"false\"");
		
		writeFileInfo(Template.LABEL,Template.ARRAYERROR);
		writeFileInfo(Template.ASCIZ, "\"Index value of %d is outside legal range [0,%d)\\n\"");
		writeFileInfo(Template.LABEL,Template.POINTERERROR);
		writeFileInfo(Template.ASCIZ, "\"Attempt to dereference NULL pointer.\\n\"");
		
		writeFileInfo(Template.LABEL,Template.ECONEERROR);
		writeFileInfo(Template.ASCIZ, "\"Attempt to dereference a pointer into deallocated stack space.\\n\"");
		writeFileInfo(Template.GLOBAL,"lowestSP");
		writeFileInfo(Template.SECTION_DATA);
		writeFileInfo(Template.ALIGN, "4");
		writeFileInfo(Template.LABEL, "lowestSP");
		writeFileInfo(Template.WORD,"0");

	
		writeFileInfo(Template.NewLine);
	}

	public void writeCout(STO args) {

		String var = null;
		String Fmt = null;
		String funcName = "printf";
		//increaseIndent()();
	
		
		if (args == null) {//Cout no arg ???	
			
		}else {


			int flag = 0;

			if (args instanceof VarSTO && 
					args.getIsFuncReturn() == true) {//return by ref

				//args.get(i).setName("%o0"); // !!!!!!!!!!! DANGEROUS : return by ref, set it's name be %o0
				flag = 1;
			}else if (args instanceof ExprSTO  && 
					args.getIsFuncReturn() == true) { // return by value

				flag = 2;
			}

			Type typ = args.getType();

			if (args.getName().equals("Literal")) {	//cout<<3;

				if (typ instanceof StrType) {
					writeLiteral((ConstSTO)args);
					Fmt = "_strFmt";

					writeFileInfo(Template.THREE_PARAM, Template.SET_OP , Fmt , "%o0");
					writeFileInfo(Template.TWO_PARAM, Template.CALL_OP , funcName);
					writeFileInfo(Template.NOP_OP);
					writeFileInfo(Template.NewLine);
					return;
				}
				else if (typ instanceof FloatType) {
					funcName = "printFloat";
					writeLiteral((ConstSTO)args);

					writeFileInfo(Template.TWO_PARAM, Template.CALL_OP , funcName);
					writeFileInfo(Template.NOP_OP);
					writeFileInfo(Template.NewLine);

					return;

				}
				else {
					//bool & int
					Fmt = getFormat(args);
					var = Integer.toString(((ConstSTO)(args)).getIntValue());

				}

				writeFileInfo(Template.THREE_PARAM, Template.SET_OP , Fmt , "%o0");
				if (!(typ instanceof BoolType)) {
					writeFileInfo(Template.THREE_PARAM, Template.SET_OP , var , "%o1");
				}



			}else if (args.getName().equals("endl")) {

				Fmt = "_endl";
				funcName = "printf";
				writeFileInfo(Template.THREE_PARAM, Template.SET_OP , Fmt , "%o0");
				writeFileInfo(Template.TWO_PARAM, Template.CALL_OP , funcName);
				writeFileInfo(Template.NOP_OP);
				writeFileInfo(Template.NewLine);
				return;
			}
			else {//cout<<a;

				Fmt = getFormat(args);
				var = args.getName();

				
				if (Fmt == null) {//cout<<a; a is float
					
					funcName = "printFloat";
					args.setIsInitialized(true);			
					//SKETCH
					if (args.getNeedsExtraOffSet() == true && (args.getIsArrayPassedByRef() == false)){
						writeLoad(args,"%f0",1);
					}
					writeLoad(args,"%f0",0);

					
					/*if (flag == 2 || flag == 1) {
						fmoveRegister("%f30" , "%f0");
					}
					else {
						writeLoad(args,"%f0",0);
					}*/
				


				}else {
					/*if (flag == 2){
						moveRegister("%o0" , "%o1");

					}else if (flag == 1) {
						writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP , "%o0" , "%o1");
					}
					else{
						writeLoad(args,"%o1",0);
					}*/
					//SKETCH
					if (args.getNeedsExtraOffSet() == true && (args.getIsArrayPassedByRef() == false)){
						writeLoad(args,"%o1",3);
					}else{
					
						writeLoad(args,"%o1",0);
					}
					writeFileInfo(Template.THREE_PARAM, Template.SET_OP , Fmt , "%o0");
	

				}

			}

			if (args.getType() instanceof BoolType) {
				writeCoutBool(args);
			}

			writeFileInfo(Template.TWO_PARAM, Template.CALL_OP , funcName);
			writeFileInfo(Template.NOP_OP);
			writeFileInfo(Template.NewLine);
			
		
		}
		
		//decreaseIndent()();
		
		
	}

	
	private void writeCoutBool(STO args) {
		
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP , "%o1" , "%g0");
		writeFileInfo(Template.TWO_PARAM, Template.BE_OP,   "BFALSE" + boolcounter );
		writeFileInfo(Template.NOP_OP);
		
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "_boolT" , "%o1");
		writeFileInfo(Template.TWO_PARAM, Template.BA_OP , "BEND" + boolcounter );
		writeFileInfo(Template.NOP_OP);
		
		writeFileInfo(Template.LABEL2, "BFALSE" + boolcounter );
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "_boolF" , "%o1");
		
		writeFileInfo(Template.LABEL2, "BEND" + boolcounter  );
		writeFileInfo(Template.NewLine);
		
		boolcounter ++ ;
		
	}

	private void moveRegister(String src, String dest) {
		writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP , src , dest);
	
	}
	
	private void fmoveRegister(String src, String dest) {
		writeFileInfo(Template.THREE_PARAM, Template.FMOVE_OP , src , dest);
	
	}
	
	
	private String getFormat(STO sto) {
	
		String Fmt = null;
		Type typ = sto.getType();
		
		if (typ instanceof FloatType) {//Not work for float now
		
			Fmt = null;
		
		}else if (typ instanceof IntType){
		
			Fmt = "_intFmt";
			
		}else if (typ instanceof BoolType) {
			
			if (sto instanceof ConstSTO) {//dont know what happen for bool a; cout<<a;  
				if ( ((ConstSTO)(sto)).getIntValue() == 1) {
					Fmt = "_boolT";
				}else {
					Fmt = "_boolF";
				}
			}else {
				Fmt = Template.STRFMR;
			}
		}
		
		return Fmt;
	}

	public void writeReturn(STO exprSto , STO funcsto, int size) {
		
		Type funcType = ((FuncSTO)funcsto).getFuncType();
		//String fname =((FuncSTO)funcsto).getName() + "XoX"; 
		
		if (exprSto.getName().equals("Literal")) { // return by value : return 3;
			writeLiteral((ConstSTO)exprSto);
			if (exprSto.getType() instanceof FloatType) {
				fmoveRegister("%f0","%f30");
			}else {
				moveRegister("%l1","%i0");
			}
			
		}else if (((FuncSTO)funcsto).getIsReference() == true) { // return by ref : return a;
			
			
			writeLoad(exprSto,"%i0",1);
			moveRegister("%l0","%i0");
			/*if (exprSto.getType() instanceof FloatType) {
				writeLoad(exprSto,"%f0",1);
				fmoveRegister("%f0","%f30");
				
			}else {
				writeLoad(exprSto,"%i0",1);
				moveRegister("%l0","%i0");
			}*/
			
		}
		else {// return by value : return a
			exprSto.setIsInitialized(true); //DANGEROUS 
			
			if (exprSto.getType() instanceof StructType) {
				writeReturnStruct(exprSto,size);
				return ;
			}
			
			if (exprSto.getType() instanceof FloatType) {
				writeLoad(exprSto,"%f30",1);
				
			} else if(funcType instanceof FloatType &&
						exprSto.getType() instanceof IntType) {
				
				writeLoad(exprSto,"%f30",1);
				writeFileInfo(Template.THREE_PARAM, Template.FITOS , "%f30" , "%f30");
			}else if (funcType instanceof PointerType) {
				writeLoad(exprSto,"%i0",0);
			}
			else {
				writeLoad(exprSto,"%i0",1);
			}
		}
		
		//writeFileInfo(Template.TWO_PARAM, Template.BA_OP , fname);
		writeFileInfo(Template.RET_OP);
		writeFileInfo(Template.RESTORE_OP);
		writeFileInfo(Template.NewLine);
		
		
	}

	public void writeOperator(STO a, Operator o, STO b, STO result) {
		
		if (a.getName().equals("Literal")) {
			this.writeLiteral((ConstSTO)a);
		}
		
		if (b.getName().equals("Literal")) {
			this.writeLiteral((ConstSTO)b);
		}
		
		
		boolean fflag = false;
		if (a.getType() instanceof FloatType
				|| b.getType() instanceof FloatType) {
			fflag = true;
		}
		
		if (o.getName().equals("+")) {
			
			if (fflag == true) {
				writeAddDiv(Template.FADD_OP,a,b,result);	
			}else {
				writeAddDiv(Template.ADD_OP,a,b,result);
			}		
			
		}else if (o.getName().equals("-")) {
			
			if (fflag == true) {
				writeAddDiv(Template.FSUB_OP,a,b,result);
				
			}else {
				writeAddDiv(Template.SUB_OP,a,b,result);
			}
			
		}else if (o.getName().equals("/")) {
			
			if (fflag == true) {
				writeFloatMulDiv(Template.FDIV_OP, a , b, result);
			}else {
				writeFuncBinary(Template.DIV_OP ,a ,b, result);
			}
			
			
		}else if (o.getName().equals("*")) {
			
			if (fflag == true) {
				writeFloatMulDiv(Template.FMUL_OP, a , b, result);
			}else {
				writeFuncBinary(Template.MUL_OP ,a ,b, result);
			}
			
		}else if (o.getName().equals("%")) {
			writeFuncBinary(Template.REM_OP ,a ,b, result);
			
		}else if (o.getName().equals("^")) {
			writeBitwiseBinary(Template.XOR_OP ,a ,b, result);
			
		}else if (o.getName().equals("|")) {
			writeBitwiseBinary(Template.OR_OP ,a ,b, result);
			
		}else if (o.getName().equals("&")) {
			writeBitwiseBinary(Template.AND_OP ,a ,b, result);
			
		}else if (o.getName().equals(">")) {
			
			if (fflag == true) {
				writeCompareBinary(Template.FBG_OP ,a ,b, result);
			}else {
				writeCompareBinary(Template.BG_OP ,a ,b, result);
			}
			
			
		}else if (o.getName().equals(">=")) {
			
			
			if (fflag == true) {
				writeCompareBinary(Template.FBGE_OP ,a ,b, result);
			}else {
				writeCompareBinary(Template.BGE_OP ,a ,b, result);
			}
			
			
		}else if (o.getName().equals("<")) {
			
			
			if (fflag == true) {
				writeCompareBinary(Template.FBL_OP ,a ,b, result);
			}else {
				writeCompareBinary(Template.BL_OP ,a ,b, result);
			}
			
			
		}else if (o.getName().equals("<=")) {
			
			if (fflag == true) {
				writeCompareBinary(Template.FBLE_OP ,a ,b, result);
			}else {
				writeCompareBinary(Template.BLE_OP ,a ,b, result);
			}
			
			
		}else if (o.getName().equals("==")) {
			
			
			if (fflag == true) {
				writeCompareBinary(Template.FBE_OP ,a ,b, result);
			}else {
				writeCompareBinary(Template.BE_OP ,a ,b, result);
			}
			
			
		}else if (o.getName().equals("!=")) {
			
			if (fflag == true) {
				writeCompareBinary(Template.FBNE_OP ,a ,b, result);
			}else {
				writeCompareBinary(Template.BNE_OP ,a ,b, result);
			}
			
			
		}else if (o.getName().equals("&&")) {
			writeShortCircuitAnd(a ,b, result);
			
		}else if (o.getName().equals("||")) {
			writeShortCircuitOr(a ,b, result);
		}
		
			
		
		
		
	}

	private void writeShortCircuitAnd(STO a, STO b, STO result) {
		
		int num = MyParser.popAndStack();
		String mark = "flabel" + num;
		String mark2 = "endlabel" + num;
		
		writeLoad(b,"%l0",0);
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP , "%l0" , "%g0" );
		writeFileInfo(Template.TWO_PARAM , Template.BE_OP , mark);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		
		writeFileInfo(Template.THREE_PARAM , Template.MOVE_OP , "1" , "%l5");
		writeFileInfo(Template.TWO_PARAM , Template.BA_OP , mark2);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		
		writeFileInfo(Template.LABEL2, mark);
		writeFileInfo(Template.THREE_PARAM , Template.MOVE_OP , "0" , "%l5");
		
		writeFileInfo(Template.LABEL2, mark2);
		writeStore(result,"%l5");
		
		writeFileInfo(Template.NewLine);
		
		
	}
	
	
	
	
	private void writeShortCircuitOr(STO a, STO b, STO result) {
		
		int num = MyParser.popOrStack();
		String mark = "Orflabel" + num;
		String mark2 = "Orendlabel" + num;
		
		writeLoad(b,"%l0",0);
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP , "%l0" , "%g0" );
		writeFileInfo(Template.TWO_PARAM , Template.BNE_OP , mark);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		
		writeFileInfo(Template.THREE_PARAM , Template.MOVE_OP , "0" , "%l5");
		writeFileInfo(Template.TWO_PARAM , Template.BA_OP , mark2);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		
		writeFileInfo(Template.LABEL2, mark);
		writeFileInfo(Template.THREE_PARAM , Template.MOVE_OP , "1" , "%l5");
		
		writeFileInfo(Template.LABEL2, mark2);
		writeStore(result,"%l5");
		
		writeFileInfo(Template.NewLine);
		
		
	}
	
	
	
	

	private void writeCompareBinary(String Op, STO a, STO b, STO result) {
		
		String compare = null;
		String r1 = null;
		String r2 = null;
		int flag = 0;
		
		
		if (a.getType() instanceof PointerType && b.getType() instanceof PointerType) {
			compare = Template.COMPARE_OP;
			r1 = "%l1";
			r2 = "%l2";
			flag = 0;
			
		}
		else if (a.getType() instanceof IntType
				&& b.getType() instanceof IntType) {
			
			compare = Template.COMPARE_OP;
			r1 = "%l1";
			r2 = "%l2";
			flag = 0;
			
			
		}else if (a.getType() instanceof FloatType
				&& b.getType() instanceof FloatType) {
			
			
			compare = Template.FCOMPARE_OP;
			r1 = "%f1";
			r2 = "%f2";
			flag = 1;
			
		}else if (a.getType() instanceof BoolType
					&& b.getType() instanceof BoolType) {
			
			compare = Template.COMPARE_OP;
			r1 = "%l1";
			r2 = "%l2";
			flag = 0;
			
			//writeBoolCompare (Op , a , b, result);
			//return;
		}
		else {
			
			writePromoteCompare(Op, a ,b ,result);
			return ;
		}
		
		writeLoad(a, r1 ,flag);
		writeLoad(b, r2 ,flag);
		
		GeneralCompare(r1, r2 , Op , compare , result);
	
	}
	
	
	

	private void GeneralCompare(String r1, String r2, String Op,  String compare , STO result) {
		
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "0" , "%l4");
		writeFileInfo(Template.THREE_PARAM, compare , r1 , r2);
		writeFileInfo(Template.TWO_PARAM, Op , "HERE" + labelCounter );
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		
		writeStore(result,"%l4");
		writeFileInfo(Template.TWO_PARAM, Template.BA_OP , "END" + labelCounter );
		writeFileInfo(Template.NOP_OP);
		
		writeFileInfo(Template.LABEL2,"HERE" + labelCounter);
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "1" , "%l4");
		writeStore(result,"%l4");
		
		writeFileInfo(Template.LABEL2,"END" + labelCounter);
		labelCounter++;
		
		
		writeFileInfo(Template.NewLine);
	
		
	}
	

	private void writePromoteCompare(String op, STO a, STO b, STO result) {
		
		
		
		if (a.getType() instanceof IntType) { // promote a
			writeLoad(a,"%l1",0);
			STO temporary = new ExprSTO("promote",b.getType());
			temporary.setOffset(MyParser.getSymTable().getLocalOffset());
			
			writeStore(temporary,"%l1");
			writeLoad(temporary,"%f1",1);
			writeFileInfo(Template.THREE_PARAM, Template.FITOS , "%f1", "%f1");
			
			writeLoad(b,"%f2",1);
			
			GeneralCompare("%f1" , "%f2" , op, Template.FCOMPARE_OP, result);
				
		}else { // promote b
			
			writeLoad(b,"%l1",0);
			STO temporary = new ExprSTO("promote",a.getType());
			temporary.setOffset(MyParser.getSymTable().getLocalOffset());
			
			writeStore(temporary,"%l1");
			writeLoad(temporary,"%f2",1);
			writeFileInfo(Template.THREE_PARAM, Template.FITOS , "%f2", "%f2");
			
			writeLoad(a,"%f1",1);
	
			GeneralCompare("%f1" , "%f2" , op, Template.FCOMPARE_OP, result);
		}
		
		writeFileInfo(Template.NewLine);
		
		
	}

	
	
	private void writeFloatMulDiv(String Op, STO a, STO b, STO result) {
		
		if (a.getType() instanceof FloatType &&
				b.getType() instanceof FloatType) {
			
			writeLoad(a,"%f1",1);
			writeLoad(b,"%f2",1);
			
			writeFileInfo(Template.FOUR_PARAM, Op , "%f1" , "%f2" , "%f1");
			
			writeStore(result,"%f1");
			writeFileInfo(Template.NewLine);
			
		}else {
			
			writePromoteForFloat(Op , a, b, result);
		}

			
	}
	
	

	private void writeBitwiseBinary(String Op, STO a, STO b, STO result) {
		
		writeLoad(a,"%l1",0);
		writeLoad(b,"%l2",0);
		
		writeFileInfo(Template.FOUR_PARAM, Op , "%l1" , "%l2" , "%l3");
		
		writeStore(result,"%l3");
		writeFileInfo(Template.NewLine);
		
	}

	private void writeFuncBinary(String Op, STO a, STO b, STO result) {
		
		writeLoad(a,"%o0",0);
		writeLoad(b,"%o1",0);
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP , Op);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		
		writeStore(result,"%o0");
		
		
	}
	
	

	private void writeAddDiv(String Opp, STO a, STO b, STO result) {
		
		if (a.getType() instanceof FloatType
				|| b.getType() instanceof FloatType) {
			
			if (a.getType() instanceof FloatType 
					&& b.getType() instanceof FloatType) {
				writeLoad(a,"%f1",1);
				writeLoad(b,"%f2",1);
				writeFileInfo(Template.FOUR_PARAM, Opp , "%f1", "%f2" , "%f1");
				writeStore(result,"%f1");
				
			}else {
				writePromoteForFloat(Opp, a, b, result);
				
			}
			
		}else {
			writeLoad(a,"%l1",0);
			writeLoad(b,"%l2",0);
			writeFileInfo(Template.FOUR_PARAM, Opp , "%l1", "%l2" , "%l1");
			writeStore(result,null);
		}
	
	
		
	}

	
	
	
	private void writePromoteForFloat(String opp, STO a, STO b, STO result) {
		
		if (a.getType() instanceof IntType) { // promote a
			writeLoad(a,"%l1",0);
			STO temporary = new ExprSTO("promote",b.getType());
			temporary.setOffset(MyParser.getSymTable().getLocalOffset());
			
			writeStore(temporary,"%l1");
			writeLoad(temporary,"%f1",1);
			writeFileInfo(Template.THREE_PARAM, Template.FITOS , "%f1", "%f1");
			
			writeLoad(b,"%f2",1);
			writeFileInfo(Template.FOUR_PARAM, opp , "%f1", "%f2" , "%f1");
			
			writeStore(result,"%f1");
			
		}else { // promote b
			
			writeLoad(b,"%l1",0);
			STO temporary = new ExprSTO("promote",a.getType());
			temporary.setOffset(MyParser.getSymTable().getLocalOffset());
			
			writeStore(temporary,"%l1");
			writeLoad(temporary,"%f2",1);
			writeFileInfo(Template.THREE_PARAM, Template.FITOS , "%f2", "%f2");
			
			writeLoad(a,"%f1",1);
			writeFileInfo(Template.FOUR_PARAM, opp , "%f1", "%f2" , "%f1");
			
			writeStore(result,"%f1");
			
		}
		
		writeFileInfo(Template.NewLine);
		
	}

	
	
	public void writeExit(STO expr) {
		int value = 0;
		
		writeLoad(expr,"%o0",0);
		
	
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, Template.EXIT );
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		decreaseIndent();
		
		
	}

	
	
	public void writePostOp(STO a , String op , int size) { // ++a
		
		String sign= "";
		if (!op.equals(Template.INC_OP)){
			sign = "-";
		}
		if (a.getType() instanceof FloatType) {
			
			String fop = null;
			
			if (op.equals(Template.INC_OP)){
				fop = Template.FADD_OP;
			}else {
				fop = Template.FSUB_OP;
				
			}
			
			int offset2 = MyParser.getSymTable().getLocalOffset();
			VarSTO temp2 = new VarSTO("temp2",new IntType());
			temp2.setOffset(offset2);
			writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "1" , "%l4");
			

			writeStore(temp2, "%l4");
			writeLoad(temp2, "%f4",1);
			writeLoad(a,"%f1",1);
			
			writeFileInfo(Template.THREE_PARAM, Template.FITOS , "%f4" , "%f4");
			writeFileInfo(Template.FOUR_PARAM, fop , "%f1" , "%f4", "%f1");
			
			writeStore(a,"%f1");
			writeFileInfo(Template.NewLine);
			
			
		}else {
			writeLoad(a,"%l1",0);
			if (a.getType() instanceof PointerType){ 
				writeFileInfo(Template.FOUR_PARAM,Template.ADD_OP, "%l1", sign+Integer.toString(size), "%l1"); //CHANGE 4 here
			}else{
				writeFileInfo(Template.TWO_PARAM, op , "%l1");
			}
			writeStore(a,null);
			writeFileInfo(Template.NewLine);
		}
	}

	
	
	public void writePreOp(STO a , STO tempx, String op, int size ) { // a++
		
		int offset = MyParser.getSymTable().getLocalOffset();
		tempx.setOffset(offset);
		String sign= "";
		if (!op.equals(Template.INC_OP)){
			sign = "-";
		}
		if (a.getType() instanceof FloatType) {
			String fop = null;
			
			if (op.equals(Template.INC_OP)){
				fop = Template.FADD_OP;
			}else {
				
				fop = Template.FSUB_OP;
			}
			
			writeLoad(a,"%f1",1);
			writeStore(tempx,"%f1");
			
			int offset2 = MyParser.getSymTable().getLocalOffset();
			VarSTO temp2 = new VarSTO("temp2",new IntType());
			temp2.setOffset(offset2);
			writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "1" , "%l4");
			

			writeStore(temp2, "%l4");
			writeLoad(temp2, "%f4",1);
			
			writeFileInfo(Template.THREE_PARAM, Template.FITOS , "%f4" , "%f4");
			writeFileInfo(Template.FOUR_PARAM, fop , "%f1" , "%f4", "%f1");
			
			writeStore(a,"%f1");
			writeFileInfo(Template.NewLine);
			
			
		}else {
			
			writeLoad(a,"%l1",0);
			writeStore(tempx,null);
			if (a.getType() instanceof PointerType){ 
				writeFileInfo(Template.FOUR_PARAM,Template.ADD_OP, "%l1", sign+Integer.toString(size), "%l1"); //CHANGE 4 here
			}else{
				writeFileInfo(Template.TWO_PARAM, op , "%l1");
			}
			writeStore(a,null);
			writeFileInfo(Template.NewLine);
		
		}
		
		
	}
	
	
	public STO writeUnaryNot(STO sto, Operator o) {
		
		STO result = null;
		if (sto instanceof ConstSTO) {
			int v = ((ConstSTO)sto).getIntValue();
			if (v == 0) {
				v = 1;
			}else {
				v = 0;
			}
			result = new ConstSTO("tempx",sto.getType(), v );
		}else {
			result = new ExprSTO("tempx",sto.getType());
		}
		result.setOffset(MyParser.getSymTable().getLocalOffset());
	
		writeLoad(sto,"%l1",0);

		
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP , "%l1" , "%g0");
		writeFileInfo(Template.TWO_PARAM, Template.BE_OP,   "BFALSE" + boolcounter );
		writeFileInfo(Template.NOP_OP);
		
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "0" , "%l1");
		writeFileInfo(Template.TWO_PARAM, Template.BA_OP , "BEND" + boolcounter );
		writeFileInfo(Template.NOP_OP);
		
		writeFileInfo(Template.LABEL2, "BFALSE" + boolcounter );
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP , "1" , "%l1");
		
		writeFileInfo(Template.LABEL2, "BEND" + boolcounter  );
		writeFileInfo(Template.NewLine);
		
		writeStore(result,"%l1");

		boolcounter ++ ;
		return result;
		
	}
	
	
	
	public STO writeUnaryMinusOp(STO sto, String sign) {
		
		ExprSTO result = new ExprSTO("tempx",sto.getType());
		result.setOffset(MyParser.getSymTable().getLocalOffset());
		
		if (sto.getType() instanceof FloatType) {
			
			if (sto.getName().equals("Literal")){
				writeLiteral((ConstSTO)sto);
			}
			
			writeLoad(sto,"%f1",1);
			writeFileInfo(Template.THREE_PARAM, Template.FNEG_OP , "%f1" , "%f2");
			writeStore(result,"%f2");
			
			
		}else {
			
			writeLoad(sto,"%l1",0);
			
			if (sign.equals("-")) {
				writeFileInfo(Template.TWO_PARAM, Template.NEG_OP , "%l1");
			}
			writeStore(result,null);
			
		}
	
		
		return result;
		
	}
	
	
	
	public void writeArray(VarSTO sto) {

		if (sto.getIsGlobal() == true){
			/* if global array */
			//set up label
			writeFileInfo(Template.GLOBAL,sto.getName());
			writeFileInfo(Template.SECTION_DATA);
			writeFileInfo(Template.ALIGN,Integer.toString(4));
			writeLabel(sto.getName());
			//Initialize the values
			ArrayType aT = (ArrayType) sto.getType();
			if (aT.getUnderType() instanceof IntType || aT.getUnderType() instanceof BoolType ){
				//int[] || bool[]
				for (int i = 0; i< aT.getSize(); i++){
					writeFileInfo(Template.WORD, "0" );
				}
			}else if ( aT.getUnderType() instanceof FloatType){
				//float[]
				for (int i = 0; i< aT.getSize(); i++){
					writeFileInfo(Template.SINGLE, "0" );
				}
			}
		}else{
			/* local array*/
			//set up stack space;

		}

	}

	public void writeArray(VarSTO sto, int size) {

		if (sto.getIsGlobal() == true){
			/* if global array */
			//set up label
			// exclusivley for array structs
			writeFileInfo(Template.GLOBAL,sto.getName());
			writeFileInfo(Template.SECTION_DATA);
			writeFileInfo(Template.ALIGN,Integer.toString(4));
			writeLabel(sto.getName());
			//Initialize the values
			ArrayType aT = (ArrayType) sto.getType();
			//int[] || bool[]
			for (int i = 0; i< aT.getSize(); i++){
				for (int j = 0; j < size/4; j++){
					writeFileInfo(Template.WORD, "0" );
				}
			}
		}else{
			/* local array*/
			//set up stack space;

		}

	}
	
	

	public void writeConditional(STO expr, String label) {
		
		writeLoad(expr,"%l1",0);
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP , "%l1" , "%g0");
		writeFileInfo(Template.TWO_PARAM, Template.BE_OP , label);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		
		
	}

	public void writeLabel(String label) {
		
		writeFileInfo(Template.LABEL2 , label);
		writeFileInfo(Template.NewLine);
		
	}
	
	public void writeWhileMark(String mark) {
		writeFileInfo(Template.LABEL2 , mark);
		writeFileInfo(Template.NewLine);
		
	}
	
	

	public void writeEndWhile(String mark) {
		
		writeFileInfo(Template.TWO_PARAM,Template.BA_OP , mark);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		
	}
	
	
	public void writeEndIf(String mark) {
		
		writeFileInfo(Template.TWO_PARAM,Template.BA_OP , mark);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		
	}

	

	public void writeEndElse(String mark) {
		
		writeFileInfo(Template.LABEL2, mark);
		writeFileInfo(Template.NewLine);
		
	}
	
	
	
	private static int labelCounter = 0;
	private static int boolcounter = 0;



	public void writeCin(STO args) {
		
		String ro = null;
		String FuncName = null;
		
		if (args.getType() instanceof IntType) {
			ro = "%o0";
			FuncName = "inputInt";
			
		}else {
			ro = "%f0";
			FuncName = "inputFloat";
		}
		
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP , FuncName);
		writeFileInfo(Template.NOP_OP);
		
		writeStore(args , ro);
		writeFileInfo(Template.NewLine);
		
		
	}

	public void writeElseExtraLabel(String popElseStack) {
		
		writeFileInfo(Template.LABEL2, popElseStack);
		writeFileInfo(Template.NewLine);
		
	}

	public void writeBreak() {
		
		String endWhile = MyParser.m_WhileEndStack.peek();
		
		writeFileInfo(Template.TWO_PARAM, Template.BA_OP, endWhile );
		writeFileInfo(Template.NOP_OP);
		
		writeFileInfo(Template.NewLine);
	}

	public void writeContinue() {
		
		String startWhile = MyParser.m_WhileStack.peek();
		
		writeFileInfo(Template.TWO_PARAM, Template.BA_OP, startWhile );
		writeFileInfo(Template.NOP_OP);
		
		writeFileInfo(Template.NewLine);
		
	}

	public void writeStoreParameters(Vector<Parameter> params) {
	
		for (int i=0 ; i<params.size() ; i++) {
			
			if (i == 6) {
				break;
			}
			
			String src = "%i" + i;
			String dst = "[%fp + " + (68+i*4) +"]";
			writeFileInfo(Template.THREE_PARAM, Template.STORE_OP, src , dst );		
			
		}
		
		writeFileInfo(Template.NewLine);
		
	}

	public void writeAmpersand(STO sto, String mark) {
		writeLoad(sto, "%l0" , 0);
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP, "%l0" , "%g0" );
		writeFileInfo(Template.TWO_PARAM, Template.BE_OP, mark );
		writeFileInfo(Template.NOP_OP);
		
		writeFileInfo(Template.NewLine);
		
	}
	
	
	public void writeOr(STO sto, String mark) {
		writeLoad(sto, "%l0" , 0);
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP, "%l0" , "%g0" );
		writeFileInfo(Template.TWO_PARAM, Template.BNE_OP, mark );
		writeFileInfo(Template.NOP_OP);
		
		writeFileInfo(Template.NewLine);
		
	}


	
	
	public void writePointerDeRef(STO sto){
		String var = null;
		String reg = null;

		if (sto.getIsGlobal()){

			if (sto.getLabelName() == null){
				var = sto.getName();
			}else{
				var = sto.getLabelName();
			}
			reg = "%g0";

		}else{
			var = Integer.toString(sto.getOffset());
			reg = "%fp";
		}
		if ( sto.getIsPointerDeRef() == false ){
			//first time
			writeFileInfo(Template.THREE_PARAM, Template.SET_OP , var , "%l0");		
			writeFileInfo(Template.FOUR_PARAM, Template.ADD_OP, reg, "%l0" , "%l0");
			// now have %fp - offset
			writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[%l0]" , "%l5");
			//have the first load		
			//decreaseIndent()();
		}else{
			var = "%l5";
			writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP , var , "%l0");		
			writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP , "[%l0]" , "%l5");
		}
	}
	
	
	
	
	public int writeAddressOf(STO varSto) {
		//loads 
		String var = null;
		String reg = null;

		if (varSto.getIsGlobal()){

			if (varSto.getLabelName() == null){
				var = varSto.getName();
			}else{
				var = varSto.getLabelName();
			}
			reg = "%g0";
		}else{
			//is local
			var = Integer.toString(varSto.getOffset());
			reg = "%fp";
		}
		
	

		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, var, "%l0" );
		if(varSto.getNeedsExtraOffSet() == true){
				
			String extraOffset = Integer.toString(varSto.getExtraOffset());
			writeFileInfo(Template.FOUR_PARAM, Template.ADD_OP, "%l0", extraOffset, "%l0");
				
		}
		writeFileInfo(Template.FOUR_PARAM, Template.ADD_OP, reg, "%l0", "%l0" );
		int tempOffset = MyParser.getSymTable().getLocalOffset();
		String varOffset = "[%fp " + tempOffset + "]";
		writeFileInfo(Template.THREE_PARAM, Template.STORE_OP, "%l0", varOffset);
		writeFileInfo(Template.NewLine);
		return tempOffset;

	}

	
	

	public void writeNew( STO sto, int Size) {
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, "1", "%o0" ); //might want to change the one
		String size = Integer.toString(Size);
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, size, "%o1");
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "calloc");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		writeStore(sto,"%o0");
	}

	
	
	public void writeDelete(STO sto){
		writeLoad(sto, "%o0", 0);
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "free");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, "0", "%l0");
		writeStore(sto,"%l0");
	}
	
	
	public void writeArrow(STO returnSTO) {
		//first derefernce the pointer to the struct
		writePointerDeRef(returnSTO);
		writeFileInfo(Template.NewLine);
		
	}

	public void writeCast(STO reSto ,Type typ) {
		
		int value = 0;
		String op = null;
		
		if (reSto instanceof ConstSTO) {//cast literal
			value = ((ConstSTO)reSto).getIntValue();
			writeFileInfo(Template.THREE_PARAM, Template.SET_OP, String.valueOf(value), "%l0");
			writeStore(reSto,"%l0");
			return;
		}
		
		if (reSto.getType() instanceof FloatType) { // int->float
			op = Template.FITOS;
		}else if(typ instanceof FloatType){//float->int
			op = Template.FSTOI;
		}
		
		if (op != null) {
			writeLoad(reSto,"%f0",1);
			writeFileInfo(Template.THREE_PARAM, op , "%f0", "%f0");
			writeStore(reSto,"%f0");
		}
		
		
	}
	
	public void writeBoundCheck(VarSTO vs, STO sto) {
		//vs is the dereference and sto is the array
		boundCheckLabel++;
		String lblgtz = "greaterThanZero" +boundCheckLabel;
		String lbllts = "lessThanSize" + boundCheckLabel;
		ArrayType aT = (ArrayType) sto.getType();
		int arrSize = aT.getArraySize();
		//first check if it is greater than 0
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP, "%l6", "%g0");
		writeFileInfo(Template.TWO_PARAM, Template.BGE_OP, lblgtz);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//print error message
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, Template.ARRAYERROR, "%o0");
		writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP, "%l6", "%o1");
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, Integer.toString(arrSize), "%o2");
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "printf");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//call exit
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, "1", "%o0");
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "exit");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.LABEL,lblgtz);
		writeFileInfo(Template.NewLine);
		//now check if it is less than the size
		writeFileInfo(Template.THREE_PARAM,Template.SET_OP,Integer.toString(arrSize), "%l0");
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP, "%l6", "%l0" );
		writeFileInfo(Template.TWO_PARAM, Template.BL_OP, lbllts);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//print error message
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, Template.ARRAYERROR, "%o0");
		writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP, "%l6", "%o1");
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, Integer.toString(arrSize), "%o2");
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "printf");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//call exit
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, "1", "%o0");
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "exit");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.LABEL,lbllts);
		writeFileInfo(Template.NewLine);
		
	}
	
	public void writeDerefCheck(STO sto){
		derefCheckLabel++;
		String reg = "";
		String labelName = "pointerFine" + derefCheckLabel;
		if (sto.getIsPointerDeRef() == true){
			//for dereference
			reg = "%l5";
		}else
		{
			writeLoad(sto, "%l5", 0);
			reg = "%l5";
		}
		
		
		writeExtraCredit1Check(reg);
		
		
		//compare the pointer to 0
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP, reg , "%g0");
		writeFileInfo(Template.TWO_PARAM, Template.BNE_OP, labelName);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//print error message
		//print error message
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, Template.POINTERERROR, "%o0");
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "printf");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//call exit
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, "1", "%o0");
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "exit");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.LABEL,labelName);
		writeFileInfo(Template.NewLine);
	}

	
	
	public void writeExtraCredit1Check(String reg){
		//pointer is already in reg
		//load lowestsp
		this.extraCredit1checklabel++;
		this.increaseIndent();
		String label = "ECOneCheck" + Integer.toString(extraCredit1checklabel);
		
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, "lowestSP", "%l1");
		writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP, "[%l1]", "%l0");
		writeFileInfo(Template.NewLine);
		//compare to lowestSP
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP, reg , "%l0");
		writeFileInfo(Template.TWO_PARAM, Template.BLEU_OP,label);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//compare to current sp
		writeFileInfo(Template.FOUR_PARAM,Template.ADD_OP,"%sp","92","%l4");//might be dangerous
		writeFileInfo(Template.THREE_PARAM, Template.COMPARE_OP, reg , "%l4");
		writeFileInfo(Template.TWO_PARAM, Template.BGEU_OP,label);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//body of if
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, Template.ECONEERROR, "%o0");
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "printf");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//call exit
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, "1", "%o0");
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP, "exit");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//end of if
		this.decreaseIndent();
		writeFileInfo(Template.LABEL2,label);
		writeFileInfo(Template.NewLine);
		
		
	}
	
	
	
	public void writeStructAssignment(STO stoDes, STO stoExpr, int size) {
		//stodes = stoExpr
		String structSize = Integer.toString(size);
		String varExpr = "";
		String regExpr = "%fp";
		if (stoExpr.getIsGlobal() || stoExpr.getIsExtern() || stoExpr.getIsStatic() ){
			
			if (stoExpr.getLabelName() == null){
				varExpr = stoExpr.getName();
			}else{
				varExpr = stoExpr.getLabelName();
			}
			regExpr = "%g0";
		}else{
			//
			varExpr = Integer.toString(stoExpr.getOffset());
		}
		
		if (stoExpr.getIsReturnStructByValue() == false) {
			writeFileInfo(Template.THREE_PARAM,Template.SET_OP,varExpr,"%l0");
			writeFileInfo(Template.FOUR_PARAM,Template.ADD_OP,regExpr,"%l0","%o1");
			writeFileInfo(Template.NewLine);
		}else {
			writeFileInfo(Template.THREE_PARAM,Template.SET_OP,"64","%l0");
			writeFileInfo(Template.FOUR_PARAM,Template.ADD_OP,"%sp","%l0","%o1");
			writeFileInfo(Template.NewLine);
		}
		//designator
		String varDes = "";
		String regDes = "%fp";
		if (stoDes.getIsGlobal() || stoDes.getIsExtern() || stoDes.getIsStatic() ){
			
			if (stoDes.getLabelName() == null){
				varDes = stoDes.getName();
			}else{
				varDes = stoDes.getLabelName();
			}
			regDes = "%g0";
		}else{
			//
			varDes = Integer.toString(stoDes.getOffset());
		}
		writeFileInfo(Template.THREE_PARAM,Template.SET_OP,varDes,"%l0");
		writeFileInfo(Template.FOUR_PARAM,Template.ADD_OP,regDes,"%l0","%o0");
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP,structSize,"%o2");
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.TWO_PARAM, Template.CALL_OP,"memmove");
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//assign back into des
		
		
		
	}
	
	public void writeExtraCredit1(){
		//load sp
		this.extraCredit1label++;
		String elselabel = "elseECOne" + Integer.toString(extraCredit1label);
		String endlabel = "endECOne" + Integer.toString(extraCredit1label);
		increaseIndent();
		writeFileInfo(Template.THREE_PARAM, Template.SET_OP, "lowestSP", "%l1");
		writeFileInfo(Template.THREE_PARAM, Template.LOAD_OP, "[%l1]", "%l0");
		writeFileInfo(Template.NewLine);
		//if lowestSP == 0
		writeFileInfo(Template.THREE_PARAM,Template.COMPARE_OP,"%l0","0");
		writeFileInfo(Template.TWO_PARAM,Template.BNE_OP,elselabel);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//body of if
		writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP, "%sp", "%l0");
		writeFileInfo(Template.TWO_PARAM,Template.BA_OP, endlabel);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//body of else
		decreaseIndent();
		writeFileInfo(Template.LABEL2,elselabel);
		increaseIndent();
		writeFileInfo(Template.NewLine);
		//if (&sp < lowestSP)
		writeFileInfo(Template.THREE_PARAM,Template.COMPARE_OP,"%sp","%l0");
		writeFileInfo(Template.TWO_PARAM, Template.BGEU_OP,endlabel);
		writeFileInfo(Template.NOP_OP);
		writeFileInfo(Template.NewLine);
		//body of second if
		writeFileInfo(Template.THREE_PARAM, Template.MOVE_OP,"%sp", "%l0");
		decreaseIndent();
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.LABEL2,endlabel);
		increaseIndent();
		writeFileInfo(Template.NewLine);
		writeFileInfo(Template.THREE_PARAM, Template.STORE_OP, "%l0", "[%l1]");
		writeFileInfo(Template.NewLine);
		decreaseIndent();
		
	}

	public void writeDeclType(int declCounter) {
		writeFileInfo(Template.LABEL2, "DECLTYPE" + declCounter);
		
	}

	public void writeDeclTypePass(int declCounter) {
		writeFileInfo(Template.TWO_PARAM, Template.BA_OP , "DECLTYPE"+declCounter);
		writeFileInfo(Template.NOP_OP);
		
		
	}





	
	
}













