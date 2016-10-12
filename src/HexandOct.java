
public class HexandOct {

	static String convertString(String str){
		if (str.length() >= 2){
			String beginning = str.substring(0,2);
			int integer = 0 ;
			if (beginning.toLowerCase().contains("0x")){
				//convert to hexadecimal
				String str1 = str.substring(2);
				//System.out.println(" " + Integer.parseInt(str1,16));
				integer = Integer.parseInt(str1,16);
			}else if (beginning.substring(0,1).toLowerCase().contains("0")){
				String str1 = str.substring(1);
				//System.out.println(" asfsdfasdf" + Integer.parseInt(str1,8));

				integer = Integer.parseInt(str1,8);
			}else{
				//System.out.println(" " + Integer.parseInt(str,10));
				integer = Integer.parseInt(str,10);
			}
			return Integer.toString(integer);
		}else
			return str;
	}
		
}
