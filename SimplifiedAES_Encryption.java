import java.util.Arrays;
public class SimplifiedAES_Encryption{

   static SBOXConstruction sb = new SBOXConstruction();
   static MultiplicationInverseTable mult = new MultiplicationInverseTable();
   static int[][] SBOX = sb.SBOX();
   static int gCount = -1;
   static int roundCount = 0;
   
   public static String simplifiedAes(String P, String K){
      
      gCount = -1;
      roundCount = 0;
      int plainDec[][] = new int[P.length()/2][P.length()/2];
      int keyDec[][] = new int[K.length()/2][K.length()/2];
      
      boolean[][] generatedKey = keyExpansion(K); // inicializohen vargjet K0,K1,K2,K3
      int index = 0, range = 0;
      for(int i = 0; i < plainDec.length; i++){
         for(int j = 0; j < plainDec.length; j++){
            plainDec[i][j] = checkSymbol1(P.charAt(index));
            keyDec[i][j] = bin2dec(Arrays.copyOfRange(generatedKey[roundCount], 4*range, 4*range+4));
            index++; range++;
         }
      }
      roundCount++;
      //showMatrix("Plain text: ", plainDec);
      //showMatrix("Key: ", keyDec);

      int[][] plainXORkey = matrixXOR(plainDec, keyDec);     
      //showMatrix("Plain XORed Key: ", plainXORkey);
      
      int[][] encryptedMatrix = new int[plainDec.length][plainDec.length];
      
      while(roundCount <= 3){
         //System.out.println("\nROUND " + roundCount);
         int[][] substitution = subBytes(plainXORkey); 
         //showMatrix("After subBytes: ", substitution);
         int[][] shift = shiftRows(substitution);
         //showMatrix("After shiftRows: ", shift);
         if(roundCount != 3){    
            int[][] mixCol = mixColumn(shift);
            //showMatrix("After mixColumn: ", mixCol);
            int[][] addKey = addRoundKey(mixCol, generatedKey[roundCount]);
            //showMatrix("After addRoundKey: ", addKey);
            plainXORkey = addKey;
            
            roundCount++;
         }else{
            int[][] addKey = addRoundKey(shift, generatedKey[roundCount]);
            //showMatrix("After addRoundKey: ", addKey);
            roundCount++;
            encryptedMatrix = addKey;
         }
      } 
      int[] encryptedArray = matrix2array(encryptedMatrix);
      String encryptedMsg = "";
      for(int i = 0; i < encryptedArray.length; i++){
         encryptedMsg += checkSymbol2(encryptedArray[i]);
      }
      return encryptedMsg;
   }
   
   public static int[][] subBytes(int[][] a){
      int[][] substituted = new int[a.length][a.length];
      int[] aArray = matrix2array(a);
      
      boolean[][] b = new boolean[4][4];
      for(int i = 0; i < aArray.length; i++){
         b[i] = dec2bin(aArray[i], 4);
      }
      
      int[] rows = new int[4];
      int[] cols = new int[4];
      int indexRow = 0, indexCol = 0;
      for(int i = 0; i < b.length; i++){
          rows[indexRow++] = bin2dec(Arrays.copyOfRange(b[i], 0, 2)); 
          cols[indexCol++] = bin2dec(Arrays.copyOfRange(b[i], 2, 4)); 
      }
      int index = 0 ;
      for(int i = 0; i < substituted.length; i++){
         for(int j = 0; j < substituted[0].length; j++){
            substituted[i][j] = SBOX[rows[index]][cols[index]];
            index++;
         }
      }
      return substituted;
   }

   public static int[][] shiftRows(int[][] a){
      int[][] shifted = new int[a.length][a[0].length]; 
      for(int i = 0; i < shifted.length; i++){
         for(int j = i; j < shifted[0].length+i; j++)
         {  shifted[i][j%shifted.length] = a[i][(j+i)%shifted.length]; }
      }
      return shifted;
   }
   
   public static int[][] mixColumn(int[][] a){
      int[][] mixMatrix = {{1,1},{1,2}};
      boolean[] polynom = {true, false, false, true, true};
      boolean[] init;
      int[][] mixed = new int[a.length][a[0].length];
      
      for(int i = 0; i < a.length; i++){
         for(int j = 0; j < a.length; j++){
            init = new boolean[5];
            for(int k = 0; k < a.length; k++){
               boolean[] temp = dec2bin(mixMatrix[i][k]*a[k][j], binaryLength(mixMatrix[i][k]*a[k][j]));
               temp = mult.polynomMod(temp, polynom);
               init = arrayXOR(init, temp);
               mixed[i][j] = bin2dec(init);
            }
         }
      }
      return mixed;
   } 
   
   public static boolean[][] keyExpansion(String key){
      boolean[][] subkeys = new boolean[key.length()][key.length()];
      int n = 0, len = key.length();
      while(n < key.length()){
         subkeys[n] = dec2bin(checkSymbol1(key.charAt(n)),4);
         n++;
      }
      
      boolean[][] generatedKey = new boolean[subkeys.length][subkeys.length*subkeys.length];
      generatedKey[0] = matrix2array(subkeys);
     // System.out.println("K0 = " + dec2hex(bin2dec(generatedKey[0]),4));
      
      for(int i = 1; i < generatedKey.length; i++){
         subkeys[0] = arrayXOR(subkeys[0],gFunction(subkeys[3]));
         subkeys[1] = arrayXOR(subkeys[0],subkeys[1]);
         subkeys[2] = arrayXOR(subkeys[1],subkeys[2]);
         subkeys[3] = arrayXOR(subkeys[2],subkeys[3]);  
         
         generatedKey[i] = matrix2array(subkeys);
         //System.out.println("K" + i + " = " + dec2hex(bin2dec(generatedKey[i]),4));
      }
      return generatedKey;
      //System.out.println();  
   }
   
   public static boolean[] gFunction(boolean[] k){
      gCount++;
      boolean[] b = new boolean[k.length];
      for(int i = 0; i < k.length-1; i++)
         b[i] = k[i+1];
      b[k.length-1] = k[0];
      
      int row = bin2dec(Arrays.copyOfRange(b, 0,b.length/2));
      int col = bin2dec(Arrays.copyOfRange(b, b.length/2,b.length));
      
      b = dec2bin(SBOX[row][col],4);
      
      b = arrayXOR(b,RC(gCount));
      return b;
   }
   
   public static boolean[] RC(int i){//RC(1)=(0001)
      boolean[] result = new boolean[4];
      result[result.length-1-i] = true;
      return result;
   }
   
   public static int[][] addRoundKey(int[][] a, boolean[] b){
      int[][] result = new int[a.length][a.length];
      int range = 0;
      for(int i = 0; i < result.length; i++){
         for(int j = 0; j < result[0].length; j++){
            result[i][j] = bin2dec(Arrays.copyOfRange(b, 4*range, 4*range+4));
            range++;
         }
      }
      result = matrixXOR(a,result);
      
      return result;
   }
   
   public static boolean[] matrix2array(boolean[][] a){
      boolean[] result = new boolean[a.length*a[0].length];
        for(int i = 0; i < a.length; i++) {
            boolean[] row = a[i];
            for(int j = 0; j < row.length; j++) {
                result[i*row.length+j] = a[i][j];
            }
        }
        return result;
   }
   
   public static int[] matrix2array(int[][] a){
      int[] result = new int[a.length*a[0].length];
        for(int i = 0; i < a.length; i++) {
            int[] row = a[i];
            for(int j = 0; j < row.length; j++) {
                result[i*row.length+j] = a[i][j];
            }
        }
        return result;
   }
   
   
   public static boolean[] optimizedArray(boolean[] a){
      int i = 0;
      while(!a[i]){ i++; }
      a = Arrays.copyOfRange(a, i, a.length);
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
   
   
   /*-----------Operacionet buleane--------------*/
   
   public static boolean[] arrayAND(boolean[] a, boolean[] b){
      boolean[] result = new boolean[a.length];
      for(int i = 0; i < result.length; i++)
         result[i] = a[i]&&b[i];
      return result;
   }
   
   public static boolean elementsXOR(boolean[] a){
      boolean temp = a[0];
      for(int i = 1; i < a.length; i++){
         temp = temp^a[i];
      }
      return temp;
   }
   
   public static boolean[] arrayXOR(boolean[] a, boolean[] b){
      boolean[] result = new boolean[a.length];
      
      if(a.length < b.length){ a = dec2bin(bin2dec(a), b.length); }
      else if(a.length > b.length){  b = dec2bin(bin2dec(b), a.length); }
      
      for(int i = 0; i < result.length; i++)
         result[i] = a[i]^b[i];
      return result;
   }
   
   public static int[][] matrixXOR(int[][] a, int[][] b){
      int[][] result = new int[2][2];
      for(int i = 0; i < result.length;i++){
         for(int j = 0; j < result[0].length; j++)
         { result[i][j] = bin2dec(arrayXOR(dec2bin(a[i][j], 4), dec2bin(b[i][j],4))); }
      }
      return result;
   }
   
   
   /* -----------Konvertimet------------ */
   
   public static int checkSymbol1(char c){
      int dec;
      switch(c){  
         case 'A': dec = 10; 
            break; 
         case 'B': dec=11; 
            break; 
         case 'C': dec=12; 
            break;
         case 'D': dec=13; 
            break; 
         case 'E': dec=14; 
            break; 
         case 'F': dec=15; 
            break;
         default: dec = new Integer(c+"").intValue();
      }
      return dec;
   }

   public static int hex2dec(String s){
      int dec = 0;
      for(int i=0; i < s.length(); i++)
      { dec = dec + (int)(Math.pow(16, s.length()-1-i))*checkSymbol1(s.charAt(i)); }
      return dec;
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
   
   public static int bin2dec(boolean[] a){
      int result = 0;
      for(int i = 0; i<a.length; i++)
         if(a[i]) result += (int)Math.pow(2,a.length-i-1);
      return result;
   }
   
   
   public static String dec2hex(int x,int places){
      String hex = "";
      for(int i = places-1;i >= 0; i--){
         for(int j = 0; j<16; j++){
            if(x-(j+1)*Math.pow(16,i)<0){
               hex = hex + checkSymbol2(j); 
               x = x-j*(int)Math.pow(16,i);
               break;
            }
         }
      }
      return hex;
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
   
   public static void showMatrix(String s, int[][] a){
      System.out.println(s);
      for(int i = 0; i < a.length; i++){
         for(int j = 0; j<a[0].length; j++){ 
            System.out.print(dec2hex(a[i][j],1)+" ");
         }
         System.out.println();
      }
   }

   public static void main(String[] args){
      String plainText = "A2E4";
      String key = "CFA1";
      System.out.println("Plain Text :    " + plainText + "\nKey:            " + key);
      System.out.println("Encrypted Text: " + simplifiedAes(plainText, key));
   }
}