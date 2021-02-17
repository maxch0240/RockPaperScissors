import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

public class Main {
    public static void checkArgs(String[] args) {
        if(args.length < 3 || args.length % 2 == 0 || !isArrayUnique(args)) {
            System.out.println("wrong arguments");
            System.exit(0);
        }
    }

    public static boolean checkMove(String[] args, int myMove) {
        boolean isMoveRight = false;
        if(myMove == 0) {
            System.exit(0);
        }
        else if(myMove > 0 && myMove <= args.length) {
            isMoveRight = true;
        }
        else isMoveRight = false;
        return  isMoveRight;
    }

    public static boolean isArrayUnique(String[] args) {
        boolean isUnique = true;

        for(int i = 0; i < args.length; i++) {
            for(int j = i; j < args.length - 1; j++) {
                if(args[i].equals(args[j + 1])) {
                    isUnique = false;
                }
            }
        }
        return isUnique;
    }

    public static int[] moves(String[] args) {
        int[] moves = new int[args.length];
        for(int i = 0; i < moves.length; i++) {
            moves[i] = i + 1;
        }
        return moves;
    }

    public static String moveName(int moveNumber,String[] args) {
        return args[moveNumber - 1];
    }

    public static void availableMoves(String[] args) {
        System.out.println("Available moves:");
        for(int i = 0; i < args.length; i++) {
            System.out.println((i + 1) + " - " + args[i]);
        }
        System.out.println("0 - exit");
    }

    public static String byteArrayToString(byte[] values) {
        StringBuilder sb = new StringBuilder();
        for (byte b : values) {
            sb.append(String.format("%02x", b));
        }
        return  sb.toString();
    }

    public static String keyGenerator(SecureRandom randomKey) throws NoSuchAlgorithmException {
        randomKey = SecureRandom.getInstanceStrong();
        byte[] values = new byte[32]; // 256 bit
        randomKey.nextBytes(values);

        return  byteArrayToString(values);
    }

    public static int computerRandomMove(int movesAmount, SecureRandom randomKey) {
        return randomKey.nextInt(movesAmount) + 1;
    }

    public static String HMAC(String secretKey, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        sha256_HMAC.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));
        byte[] result =  sha256_HMAC.doFinal(data.getBytes());
        return  byteArrayToString(result);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        checkArgs(args);

        SecureRandom randomKey = new SecureRandom();
        String key = keyGenerator(randomKey);

        int[] moves = moves(args);

        int computerMove = computerRandomMove(moves.length, randomKey);
        String HMACString = HMAC(key, moveName(computerMove, args));

        Scanner sc = new Scanner(System.in);

        System.out.println("HMAC: " + HMACString);

        int myMove;

        while(true) {
            availableMoves(args);
            System.out.println("Enter your move");
            myMove = sc.nextInt();
            if(checkMove(args, myMove)) break;
            else System.out.println("wrong move");
        }

        System.out.println("Your move: " + moveName(myMove, args));
        System.out.println("Computer move");

        System.out.println(computerMove);
        System.out.println("Computer move: " + moveName(computerMove, args));

        boolean flag = false;
        for (int i = 0; i < moves.length / 2; i++) {
            if(computerMove == moves[(myMove + i) % moves.length]) {
                flag = true;
                break;
            }
        }

        if(flag) {
            System.out.println("Computer win");
        }
        else {
            System.out.println("You win");
        }
        System.out.println("key: " + key);

    }
}
