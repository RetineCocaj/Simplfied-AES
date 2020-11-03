import java.util.Arrays;
public class SBOXConstruction{
   
   static MultiplicationInverseTable mult = new MultiplicationInverseTable();
   static int[][] multMatrix = mult.mulTable();  
   static int[][] invMatrix = mult.multInverse(multMatrix);
   static int[][] sBox = new int[invMatrix.length][invMatrix.length];
   
   public static int[][] SBOX(){
      for(int i = 0; i < sBox.length; i++){
         for(int j = 0; j < sBox.length; j++){
            sBox[i][j] = mult.bin2dec(affineTransform(mult.dec2bin(invMatrix[i][j],4)));
         }
      }
      return sBox;
   }
   
   public static boolean[] affineTransform(boolean[] b){
      boolean[][] matrix = {{true,false,true,true},
                            {false,true,true,true},
                            {true,true,true,false},
                            {true,true,false,true}};
      boolean[] vector = {false,true,true,false};
      boolean[] result = new boolean[b.length];
      
      for(int i = 0; i < matrix.length; i++){
         boolean[] tempArr = arrayAND(matrix[i], b); //matrix[i]: reshti i i-te i matrices
         boolean temp = elementsXOR(tempArr);
         result[i] = temp;
      }
      result = arrayXOR(result,vector); 
      return result;
   }
   
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
      
      if(a.length < b.length){ a = mult.dec2bin(mult.bin2dec(a), b.length); }
      else if(a.length > b.length){  b = mult.dec2bin(mult.bin2dec(b), a.length); }
      
      for(int i = 0; i < result.length; i++)
         result[i] = a[i]^b[i];
      return result;
   }
   
   public static void main(String[] args){
      System.out.println("INV");
      for(int i = 0; i < invMatrix.length; i++){
         for(int j = 0; j < invMatrix.length; j++)
            System.out.print(mult.checkSymbol2(invMatrix[i][j])+" ");
            System.out.println();
      }
      
      int[][] s = SBOX();
      System.out.println("\nSBOX");
      for(int i = 0; i < sBox.length; i++){
         for(int j = 0; j < sBox.length; j++)
            System.out.print(mult.checkSymbol2(sBox[i][j])+" ");
            System.out.println();
      }
   }
}