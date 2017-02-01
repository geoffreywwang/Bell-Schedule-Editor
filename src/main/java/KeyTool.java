import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.Random;

/**
 * Class that helps manage/encrypt/decrypt the API KEY
 */
public class KeyTool {

    /**
     * Encode data with key
     *
     * @param data
     * @param key
     * @return encryptedKey
     */
    public static String encode(String data, String key) {
        return base64Encode(KeyTool.XorWithKey(data.getBytes(), key.getBytes()));
    }

    /**
     * Decode data with key
     *
     * @param data
     * @param key
     * @return decryptedKey
     */
    public static String decode(String data, String key) {
        return new String(KeyTool.XorWithKey(base64Decode(data), key.getBytes()));
    }

    /**
     * Run XOR on some data with a key
     *
     * @param data
     * @param key
     * @return x
     */
    private static byte[] XorWithKey(byte[] data, byte[] key) {
        byte[] x = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            x[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return x;
    }

    /**
     * Base encoding method
     *
     * @param data
     * @return Encoded string
     */
    private static String base64Encode(byte[] data) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data).replaceAll("\\s", "");
    }

    /**
     * Base decoding method
     *
     * @param data
     * @return Decoded bytes
     */
    private static byte[] base64Decode(String data) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decoder.decodeBuffer(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Check if the the xor key exists. If it doesn't exist, generate a random xor key and save it to xor.key
     *
     * @return returnKey;
     */
    public static String getXORKey() {
        String returnKey = null;
        File encKey = new File("Keys" + File.separator + "xor.key");
        if (!encKey.exists()) {
            Random r = new Random();
            int key = r.nextInt();
            returnKey = String.valueOf(key);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("Keys" + File.separator + "xor.key"));
                writer.write(returnKey);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("Keys" + File.separator + "xor.key"));
                returnKey = reader.readLine();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnKey;
    }

    /**
     * Save the api key passed in with XOR encryption in api.key
     *
     * @param key
     */
    public static void saveAPIKey(String key) {
        //Create the api.key file if it doesn't exist
        File f = new File("Keys/api.key");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Get the xor key
        String xorKey = getXORKey();

        //Encode api key
        String encodedKey = KeyTool.encode(key, xorKey);

        //Write api key to file
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(encodedKey);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Looks in api.key to find the api key, otherwise return an exception
     *
     * @return APIkey
     */
    public static String getAPIKey() throws FileNotFoundException {
        String apiKey = null;
        File api = new File("Keys" + File.separator + "api.key");
        BufferedReader br = new BufferedReader(new FileReader(api));
        try {
            String user = br.readLine();
            apiKey = KeyTool.decode(user, getXORKey());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiKey;
    }
}
