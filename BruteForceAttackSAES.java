public class BruteForceAttackSAES{
   static SimplifiedAES_Encryption enc = new SimplifiedAES_Encryption();
   static String encMsg = "EAEB";
   
   public static void bruteforce(){
      for(int i = 0; i < Math.pow(2,16); i++){
         for(int j = 0; j < Math.pow(2,16); j++){
            if(enc.simplifiedAes(enc.dec2hex(i,4),enc.dec2hex(j,4)).equals(encMsg)){
               System.out.println("Plain text: " + enc.dec2hex(i,4) + 
                                  "\nKey:        " + enc.dec2hex(j,4));
            }
         }
      }
   }
   
   public static void main(String[] args){
      System.out.println("Mesazhi i enkriptuar: " + encMsg);
      System.out.println("Gjenerimet ");
      bruteforce();
   }
   
}