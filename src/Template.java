
public class Template {
//		ERROR Information
	public static final String ERROR_IO_CLOSE = 
			"Unable to close fileWriter";
	public static final String ERROR_IO_CONSTRUCT = 
			"Unable to construct FileWriter for file %s";
	public static final String ERROR_IO_WRITE = 
			"Unable to write to fileWriter";
	
//		Parameters Information
	public static final String SEPARATOR = "\t";
	public static final String TWO_PARAM = "%s" + SEPARATOR + "%s\n";
	public static final String THREE_PARAM = "%s" + SEPARATOR + "%s, %s\n";
	public static final String FOUR_PARAM = "%s" + SEPARATOR + "%s, %s, %s\n";
	
//		Synthetic Instructions
	
	public static final String SET_OP = "set";
	public static final String CSAVE_OP = "set" + SEPARATOR+"SAVE.%s %s\n";
	public static final String MOVE_OP = "mov";
	public static final String FMOVE_OP = "fmovs";
	public static final String NOP_OP = "nop\n";
	public static final String CLEAR_OP = "clr";
	public static final String INC_OP = "inc";
	public static final String DEC_OP = "dec";
	public static final String NOT_OP = "not";
	public static final String NEG_OP = "neg";
	public static final String FNEG_OP = "fnegs";
	public static final String EXIT = "exit";
	public static final String SAVEND = "SAVE.%s = -(92 + %s) & -8\n";
	
//		Subroutine Instructions
	
	public static final String CALL_OP = "call";
	public static final String RESTORE_OP = "restore\n";	
	public static final String RET_OP = "ret\n";
	public static final String SAVE_OP = "save";
	
	
//		 Branch Instructions
	public static final String COMPARE_OP = "cmp";
	public static final String FCOMPARE_OP = "fcmps";
	public static final String JUMP_OP = "jmp";
	
	
	public static final String BE_OP = "be";
	public static final String BA_OP = "ba";
	public static final String BN_OP = "bn";
	public static final String BNE_OP = "bne";
	public static final String BL_OP = "bl";
	public static final String BLE_OP = "ble";
	public static final String BG_OP = "bg";
	public static final String BGE_OP = "bge";
	
	public static final String FBE_OP = "fbe";
	public static final String FBA_OP = "fba";
	public static final String FBN_OP = "fbn";
	public static final String FBNE_OP = "fbne";
	public static final String FBL_OP = "fbl";
	public static final String FBLE_OP = "fble";
	public static final String FBG_OP = "fbg";
	public static final String FBGE_OP = "fbge";
	
	

	
	
	
//		Arithmetic/Logical Instructions	
	public static final String ADD_OP = "add";
	public static final String FADD_OP = "fadds";
	public static final String SUB_OP = "sub";
	public static final String FSUB_OP = "fsubs";
	public static final String MUL_OP  = ".mul";
	public static final String FMUL_OP  = "fmuls";
	public static final String DIV_OP  = ".div";
	public static final String FDIV_OP  = "fdivs";
	public static final String REM_OP  = ".rem";
	
	
	public static final String AND_OP = "and";
	public static final String ANDN_OP = "andn";
	public static final String OR_OP = "or";
	public static final String ORN_OP = "orn";
	public static final String XOR_OP = "xor";
	public static final String XNOR_OP = "xnor";


	
//		Load/Store Instructions	
	public static final String LOAD_OP = "ld";
	public static final String LOADUBYTE_OP = "ldub";
	public static final String LOADSBYTE_OP = "ldsb";
	public static final String LOADUHALF_OP = "lduh";
	public static final String LOADSHALF_OP = "ldsh";
	
	public static final String STORE_OP = "st";
	public static final String STOREBYTE_OP = "stb";
	public static final String STOREHALF_OP = "sth";
	
	
	
//		Segment Define
	public static final String SECTION_DATA = ".section \".data\"\n "; 
	public static final String SECTION_TEXT = ".section \".text\"\n "; 
	public static final String SECTION_BSS = ".section \".bss\"\n "; 
	public static final String SECTION_RODATA = ".section \".rodata\"\n "; 
	
	
	
	public static final String GLOBAL = ".global  %s\n";
	public static final String ASCIZ = ".asciz  %s\n";
	public static final String ALIGN = ".align  %s\n";
	public static final String WORD = ".word  %s\n";
	public static final String SINGLE = ".single  0r%s\n";
	public static final String SPACE = ".space  %s\n";
	public static final String SKIP = ".skip  %s\n";	
	public static final String LABEL = "%s:" + SEPARATOR ;
	public static final String LABEL2 = "%s:\n";
	public static final String NewLine = "\n";
	
	public static final String ENDL = "_endl";
	public static final String INTFMT = "_intFmt";
	public static final String BOOLT = "_boolT";
	public static final String BOOLF = "_boolF";
	public static final String STRFMR = "_strFmt";
	
	
//	Segment Define
	
	public static final String FITOS = "fitos";
	public static final String FSTOI = "fstoi";
	
	
	public static final String BLANK = "!THE LINE YOU ARE INTERESTED IN IS HERE %s\n";
	
	public static final String ARRAYERROR = "_arrErr";//CHANGE4
	public static final String POINTERERROR = "_ptrErr";//CHANGE4
	public static final String ECONEERROR = "_ec1Err";
	
	public static final String BGEU_OP = "bgeu";
	public static final String BLEU_OP = "bleu";
	
	
//		File Information
    public static final String FILE_HEADER = 
            "/*\n" +
            " * Generated %s\n" + 
            " */\n\n";
	 
	
}
