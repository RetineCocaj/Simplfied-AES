public class SimplifiedAES_Decryption{
   
   static SimplifiedAES_Encryption aesENC = new SimplifiedAES_Encryption();
   static MultiplicationInverseTable mult = new MultiplicationInverseTable();
   static SBOXConstruction sb = new SBOXConstruction();
   static int[][] SBOX = sb.SBOX();
   
   public static int[][] invSubBytes(int[][] a){
      int[][] substituted = new int[a.length][a.length];
      int[] aArray = aesENC.matrix2array(a);
      
      int index = 0;
      boolean[][] values = new boolean[4][4];
      while(index < aArray.length){
         ForLoop1:
         for(int i = 0; i < SBOX.length; i++){
            for(int j = 0; j < SBOX.length; j++){
               if(SBOX[i][j] == aArray[index]){
                  values[index] = concatenateBits(aesENC.dec2bin(i,2),aesENC.dec2bin(j,2));
                  index++;
                  break ForLoop1;
               }
            }
         }
      }
      index = 0;
      for(int i = 0; i < substituted.length; i++){
         for(int j = 0; j < substituted.length; j++){
            substituted[i][j] = aesENC.bin2dec(values[index++]);
         }  
      }    
      return substituted;
   }
   
   public static int[][] invShiftRows(int[][] a){
      int[][] shifted = new int[a.length][a[0].length]; 
      for(int i = 0; i < shifted.length; i++){
         for(int j = i; j < shifted[0].length+i; j++)
         {  shifted[i][j%shifted.length] = a[i][(j+i)%shifted.length]; }
      }
      return shifted;
   }
   
   public static int[][] invMixColumn(int[][] a){
      int[][] invMixMatrix = {{15,14},{14,14}};
      boolean[] polynom = {true, false, false, true, true};
      boolean[] init = new boolean[5];
      int[][] mixed = new int[a.length][a[0].length];
      
      for(int i = 0; i < a.length; i++){
         for(int j = 0; j < a.length; j++){
            init = new boolean[5];
            for(int k = 0; k < a.length; k++){
               boolean[] invMixBool = aesENC.dec2bin(invMixMatrix[i][k], 4);
               boolean[] aBool = aesENC.dec2bin(a[k][j], 4);
               boolean[] temp = mult.multGF16(invMixBool, aBool);
               temp = mult.polynomMod(temp, polynom);
               init = aesENC.arrayXOR(init, temp);
               mixed[i][j] =  aesENC.bin2dec(init);
            }
         }
      }
      return mixed;
   }
   
   public static boolean[] concatenateBits(boolean[] a, boolean[] b){
      boolean[] result = new boolean[a.length+b.length];
      int index = 0;
      for(int i = 0; i < result.length; i++){
         if(i < a.length) result[i] = a[index++];
         else result[i] = b[i-index];
      }
      return result;
   }
   
   public static void main(String[] args){
      System.out.println("\nMatrix before addRoundKey: ");
      int[][] a = {{12,6},{13,15}};
      for(int i = 0; i < a.length; i++){
         for(int j = 0; j < a.length; j++){
            System.out.print(a[i][j]+" ");
         }
         System.out.println();
      }
      int[][] mc = invMixColumn(a);
      System.out.println("\nInverse MixColumn: ");
      for(int i = 0; i < mc.length; i++){
         for(int j = 0; j < mc.length; j++){
            System.out.print(mc[i][j]+" ");
         }
         System.out.println();
      }
      
      int[][] shr = invShiftRows(mc);
      System.out.println("\nInverse ShiftRows: ");
      for(int i = 0; i < shr.length; i++){
         for(int j = 0; j < shr.length; j++){
            System.out.print(shr[i][j]+" ");
         }
         System.out.println();
      }
      
      System.out.println("\nInverse SBOX: ");
      int[][] sub = invSubBytes(shr);
      for(int i = 0; i < sub.length; i++){
         for(int j = 0; j < sub.length; j++){
            System.out.print(sub[i][j]+" ");
         }
         System.out.println();
      }     
   }
}