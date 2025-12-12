package getcoffee.antiplagiat.gateway.util;

import java.util.Arrays;

public final class Hashing {
    private Hashing() {}

    public static String hash(byte[] bytes) {
        int hash = Arrays.hashCode(bytes);
        return Integer.toHexString(hash);
    }
}
