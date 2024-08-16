package uma.autopsy.Utils;

public class HexExtractor {

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i += 16) {
            sb.append(String.format("0x%08X: ", i));

            // Hex values
            for (int j = 0; j < 16; j++) {
                if (i + j < bytes.length) {
                    sb.append(String.format("%02X ", bytes[i + j]));
                } else {
                    sb.append("   ");
                }
            }

            sb.append(" ");

            // ASCII values
            for (int j = 0; j < 16; j++) {
                if (i + j < bytes.length) {
                    char c = (char) bytes[i + j];
                    if (c < 32 || c > 126) {
                        sb.append('.');
                    } else {
                        sb.append(c);
                    }
                }
            }

            sb.append('\n');
        }
        return sb.toString();
    }

}
