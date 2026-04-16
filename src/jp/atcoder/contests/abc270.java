/**
package jp.atcoder.contests;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class abc270 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] j =  scanner.nextLine().split(" ");

        int N =  Integer.parseInt(j[0]);
        Object[] ovj = new Object[N];

        int X =  Integer.parseInt(j[1]);
        int Y =  Integer.parseInt(j[2]);
        int w = 0;
        int x = 0;

        for (int i = 0; i < N; i++) {
            String[] K = scanner.nextLine().split(" ");
            int U = Integer.parseInt(K[0]);
            int V = Integer.parseInt(K[1]);


        }
    }

    public static @Nullable Map Recursive(@NotNull Map<Integer, Object> map, int U, int V) {
        if (map.isEmpty()) {
            return (Map<Integer, Map<Integer, Object>>) map.put(U, new HashMap<Integer, Object>().put(V, new HashMap<Integer, Object>()));
        }

        if (!map.containsKey(U)) {
            RecursiveMap((Map<Integer, Object>) map.values(), U, V);
        }

        if (map.containsKey(U)) {
            return (Map<Integer, Map<Integer, Object>>) map.get(U).put(V, new HashMap<Integer, Object>());
        }
        return null;
    }
}
 */