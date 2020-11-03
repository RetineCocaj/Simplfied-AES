import java.util.Arrays;
public class MultiplicationInverseTable{

   public static int[][] mulTable(){
      int[][] mulTable = new int[16][16];
      boolean[] polynom = {true, false, false, true, true};//10011
      int temp, count = 0;
      for(int i = 0; i < mulTable.length; i++){
         for(int j = 0; j < mulTable[0].length; j++){
            if(i!=0 && j!=0){
               boolean[] row = dec2bin(i, binaryLength(i));
               boolean[] col = dec2bin(j, binaryLength(j));
               boolean[] mult = multGF16(row,col);
               mult = polynomMod(mult,polynom);
               mulTable[i][j] = bin2dec(mult);
            }
         }
      }
      return mulTable;
   }
   
   public static boolean[] polynomMod(boolean[] mult, boolean[] polynom){
      if(mult.length > 4){
         while(mult.length > 5){
            int polDec = bin2dec(polynom);//19
            polDec *= Math.pow(2, mult.length-polynom.length); 
            mult = arrayXOR(mult, dec2bin(polDec, mult.length));
            mult = optimizedArray(mult);
         }
         if(mult.length == 5) mult = arrayXOR(mult, polynom);
      }
      return mult;
   }
   
   public static boolean[] multGF16(boolean[] a, boolean[] b){
      boolean[] XORed = new boolean[a.length+b.length];
      while(true){
         boolean[] tempCol = removeLastOnes(b);
         int countLast0s = countLastZeros(tempCol);
         boolean[] result = new boolean[a.length + countLast0s];
         for(int k = 0; k < a.length; k++) result[k] = a[k];
         XORed = arrayXOR(XORed, result);
         b = removeFirstOne(b);
         //int count0s = countLastZeros(b);
         if(isFalseArr(b)) break;

      }
      XORed = optimizedArray(XORed);

      return XORed;
   }
   
   public static boolean isFalseArr(boolean[] a){
      for(int i = 0; i < a.length; i++){
         if(a[i]) return false;
      }
      return true;
   }
   public static boolean[] optimizedArray(boolean[] a){
      for(int i = 0; i < a.length; i++){
         if(a[i]){
             a = Arrays.copyOfRange(a, i, a.length);
             break;
         }
      }
      return a;
   }
   
   public static int binaryLength(int a){
      int len = 0;
      while(a > 0){
         len++;
         a = a/2;
      }
      return len;
   }
   
   public static int countLastZeros(boolean[] a){
      int count = 0;
      for(int i = a.length-1; i >= 0; i--){
         if(a[i]) break;
         else count++;
      }
      return count;
   }
   
   public static boolean[] removeFirstOne(boolean[] a){
      for(int i = 0; i < a.length; i++){
         if(a[i]){
            a[i] = false;
            break;
         }
      }
      return a;
   }
   
   public static boolean[] removeLastOnes(boolean[] a){
      boolean result[] = new boolean[a.length];
      for(int i = 0; i < a.length; i++){
         if(a[i]){
            result[i] = a[i];
            break;
         }
      }
      return result;
   }
   
   public static boolean[] arrayXOR(boolean[] a, boolean[] b){
      boolean[] result = new boolean[a.length];
      
      if(a.length < b.length){ a = dec2bin(bin2dec(a), b.length); }
      else if(a.length > b.length){  b = dec2bin(bin2dec(b), a.length); }
      
      for(int i = 0; i < result.length; i++)
         result[i] = a[i]^b[i];
      return result;
   }
   
   public static int bin2dec(boolean[] a){
      int result = 0;
      for(int i = 0; i<a.length; i++)
         if(a[i]) result += (int)Math.pow(2,a.length-i-1);
      return result;
   }
   
   public static boolean[] dec2bin(int a, int k){
      boolean[] result = new boolean[k];
      for(int i = k-1; i >= 0; i--){
         if(Math.pow(2,i) <= a) { 
            result[k-i-1] = true; 
            a = a - (int)Math.pow(2,i);
         }
         else result[k-i-1] = false;
      }
      return result;
   }
   
   public static int[][] multInverse(int[][] a){
      int[] invVect = new int[16];
      for(int i = 0; i < a.length; i++){
         for(int j = 0; j < a[0].length; j++){
            if(a[i][j] == 1){
               invVect[i] = j;
               break;
            }
         }
      }
      int[][] invTable = array2matrix(invVect); 
      return invTable;
   }
   
   public static int[][] array2matrix(int[] a){
      int[][] result = new int[(int)Math.sqrt(a.length)][(int)Math.sqrt(a.length)];
      for(int i = 0; i < result.length; i++)//result.length=4
         result[i] = Arrays.copyOfRange(a, result.length*i, result.length*i+result.length);
      return result;
   }
   
   public static String checkSymbol2(int a){
      String c = ""; 
      switch(a){  
         case 10: c = "A"; 
            break; 
         case 11: c = "B"; 
            break; 
         case 12: c = "C"; 
            break;
         case 13: c = "D"; 
            break; 
         case 14: c = "E"; 
            break; 
         case 15: c = "F"; 
            break;
         default: c = a+"";
      }
      return c;
   }
   
   
   public static void main(String[] args){
      int[][] mulTable = mulTable();
      System.out.println("Multiplication Table in GF(16)\n");
      for(int i = 0; i < mulTable.length; i++){
         System.out.print("|");
         for(int j = 0; j < mulTable[0].length; j++){
            if(mulTable[i][j] >= 10) System.out.print(mulTable[i][j]+"|");
            else System.out.print(" "+mulTable[i][j]+"|");
         }
         System.out.println();
      }
      
      System.out.println("\n\nMultiplicative Inverse Table\n");
      int[][] invTable = multInverse(mulTable);
      for(int i = 0; i < invTable.length; i++){
         for(int j = 0; j < invTable.length; j++)
            System.out.print(checkSymbol2(invTable[i][j])+" ");
            System.out.println();
      }
   }
}