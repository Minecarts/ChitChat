package helper;

public class StringHelper {
    
    public static String join(String[] pieces) {
        return join(pieces, 0);
    }
    public static String join(String[] pieces, String delimiter) {
        return join(pieces, delimiter, 0);
    }
    public static String join(String[] pieces, String delimiter, int offset) {
        return join(pieces, offset, delimiter);
    }
    
    public static String join(String[] arr, int offset) {
        return join(arr, offset, " ");
    }
    
    public static String join(String[] arr, int offset, String delim) {
        String str = "";

        if ((arr == null) || (arr.length == 0)) {
            return str;
        }

        for (int i = offset; i < arr.length; i++) {
            str = str + arr[i] + delim;
        }

        return str.trim();
    }
    
}
