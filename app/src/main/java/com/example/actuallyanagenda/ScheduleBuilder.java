package com.example.actuallyanagenda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Helper class containing the algorithms that auto-generate a schedule from a list of preferences.
 */
public final class ScheduleBuilder {


    // Convex Hull Trick
    static class Partial {
        private int x, tot, idx;

        public Partial(int a, int b, int c) {
            x = a;
            tot = b;
            idx = c;
        }

        public int cost(long y) {
            return (int) Math.pow(x - y, 2) + tot;
        }

        public int idx() {
            return idx;
        }

        public int intersect(Partial comp) {
            return ((comp.tot - tot + (comp.x * comp.x) - (x * x)) / (2 * (comp.x - x)));
        }
    }


    public static String calcMinCost(String S, int K) {
        int[] binaryArr = new int[S.length()];
        for(int i = 0; i < S.length(); i++) binaryArr[i] = S.charAt(i) - '0';
        int[] pref = new int[S.length()];

        pref[S.length() - 1] = binaryArr[S.length() - 1];
        for(int i = S.length() - 2; i >= 0; i--) {
            pref[i] = pref[i + 1] + binaryArr[i];
        }

        // steps for iteration:
        /*

        calc all i for dp[i][0][0]
        calc all i for dp[i][1][1]
        calc all i for dp[i][1][0]
        calc all i for dp[i][2][1]
        calc all i for dp[i][2][0]
        etc......
         */

        int N = S.length();
        int[][][] dp = new int[S.length()][pref[0] + 1][2];
        int[][][] prev = new int[S.length()][pref[0] + 1][2];
        for(int[][] i: dp)
            for(int[] j: i)
                Arrays.fill(j, Integer.MIN_VALUE);

        // initializing dp[i][0][0]

        for(int i = S.length() - 1, val = -1; i >= 0; i--) {
            dp[i][0][0] = val * val;
            prev[i][0][0] = N;
            val++;
        }



        // dp[N - 1][1][1] = 1 if pref[N - 1] = 1
        // dp[i is from N - 2 to 0][1][1] = dp[i + 1][0][0] + 1

        if(pref[N - 1] == 1) {
            dp[N - 1][1][1] = 1;
            prev[N - 1][1][1] = N;
        }
        for(int i = N - 2; i >= 0; i--) {
            if (pref[i] >= 1) {
                dp[i][1][1] = dp[i + 1][0][0] + 1;
                prev[i][1][1] = i + 1;
            }
        }
        // Calculating dp[i][1][0]

        for(int i = N - 2; i >= 0; i--) {
            if(pref[i] < 1) continue;
            int min = Integer.MAX_VALUE;
            int preM = -1;
            for(int j = i + 1; j < N; j++) {
                if(dp[j][1][1] == Integer.MIN_VALUE) break;
                int newCost = dp[j][1][1] + (j - i - K) * (j - i - K);
                if(newCost < min) {
                    min = newCost;
                    preM = j;
                }
            }
            if(min != Integer.MAX_VALUE) {
                dp[i][1][0] = min;
                prev[i][1][0] = preM;
            }
        }
        // Calculating the rest

        for(int oC = 2; oC <= pref[0]; oC++) {
            // Calc 1 and then 0 after 1
            for(int i = N - 2; i >= 0; i--) {
                if(pref[i] < oC) continue;
                int min = Integer.MAX_VALUE;
                int preM = -1;
                for(int j = i + 1; j < N; j++) {
                    int shiftedOC = oC - (j - i);
                    if(shiftedOC < 0) break;
                    if(dp[j][shiftedOC][0] == Integer.MIN_VALUE) continue;
                    int newCost = dp[j][shiftedOC][0] + (j - i - K) * (j - i - K);
                    if(newCost < min) {
                        min = newCost;
                        preM = j;
                    }
                }
                if(min != Integer.MAX_VALUE) {
                    dp[i][oC][1] = min;
                    prev[i][oC][1] = preM;
                }
            }
            // Temporary array for CHT -> Replaces old nested loop
            Partial[] deque = new Partial[N + 1];

            int startIdx = -1;
            for(int i = N - 1; i >= 0; i--) {
                if(dp[i][oC][1] != Integer.MIN_VALUE) {
                    startIdx = i;
                    break;
                }
            }
            // [1, startIdx] will be used as offset values for the vertex that will map to [0, startIdx - 1]
            int l = 0, r = 0;
            for(int i = startIdx - 1; i >= 0; i--) {
                // if(pref[i] < oC) continue; -> unnecessary since we're only adding 0s
                Partial part = new Partial(startIdx - (i + 1 - K), dp[i + 1][oC][1], i + 1);
                while (l - r > 1 && deque[l - 2].intersect(deque[l - 1]) > deque[l - 2].intersect(part)) l--;
                deque[l++] = part;
                while (r + 1 < l && deque[r].cost((startIdx - i)) >= deque[r + 1].cost((startIdx - i))) r++;
                int best = deque[r].cost((startIdx - i));
                int idx = deque[r].idx();
                dp[i][oC][0] = best;
                prev[i][oC][0] = idx;
            }
        }

        //printDPArray(dp);

        StringBuilder sb = new StringBuilder();
        int curI = 0, curJ = pref[0], curK = 1;

        while(curI != N) {
            int next = prev[curI][curJ][curK];
            int dist = next - curI; // length of current subsequence
            for(int i = 0; i < dist; i++) sb.append(curK);
            // System.out.println(curI + " " + curJ + " " + curK);
            curI = next;
            if(curK == 1) curJ -= dist;
            curK ^= 1;
        }
        return sb.toString();
    }


    /**
     * Takes in tasks & events inputted by the user and spits out a partitioned schedule containing work sessions
     * for the tasks as well as the already set events, making sure nothing overlaps.
     *
     * This list/schedule is sorted by default.
     * @param tasks
     * @param events
     * @param sleep
     * @param wake
     * @param meals
     * @param mealDuration
     * @param optimalK
     * @return
     */
    public static ArrayList<StaticTask> autoPartitionDynamicTasks(DynamicTask[] tasks, StaticTask[] events, int sleep, int wake, int[] meals, int mealDuration, int optimalK) {
        long currentEpochIndex = (System.currentTimeMillis() / (1000 * 60 * 15) + 2);
        System.out.println(currentEpochIndex);
        Arrays.sort(tasks, (a, b) -> Long.compare(a.due, b.due));
        HashMap<Long, Integer> mapEvents = new HashMap<>(); // -1: contains event, 1: latest work period, 0 / null: nothing
        for(int i = 0; i < events.length; i++) {
            for(int j = 0; j < events[i].duration; j++) {
                mapEvents.put(events[i].start + j, -1);
            }
        }

        int duringMealSleep = 0;
        for(long i = currentEpochIndex - 96; i <= tasks[tasks.length - 1].due; i++) {


            if(i % 96 == sleep % 96) duringMealSleep++;
            for(int meal: meals) {
                if(i % 96 == (meal) % 96) duringMealSleep++;
            }

            if((i % 96 == wake % 96) && duringMealSleep > 0) duringMealSleep--;
            for(int meal: meals) {
                if(i % 96 == (meal + mealDuration) % 96 && duringMealSleep > 0) duringMealSleep--;
            }

            if(duringMealSleep > 0) mapEvents.put(i, -1);
        }

        final int dayCycle = 96;
        if(tasks[0].due < currentEpochIndex) {
            // Do something to signify that "One of your due dates is too close to create a schedule!"
            System.out.println("baf");
        }

        for(int i = tasks.length - 1; i >= 0; i--) {
            long currentIdx = tasks[i].due - 1;
            int toPartition = tasks[i].duration;
            while(toPartition > 0) {
                if(mapEvents.get(currentIdx) != null) {
                    currentIdx--;
                    continue;
                }
                if(currentIdx < currentEpochIndex) {
                    // Do something to signal that it is literally impossible to make a schedule
                    System.out.println("baf");
                }
                mapEvents.put(currentIdx, 1);
                currentIdx--;
                toPartition--;
            }
        }

        StringBuilder sb = new StringBuilder();
        long[] stringIdxToRealTime = new long[50000];
        int curIdx = 0;
        for(long i = currentEpochIndex; i <= tasks[tasks.length - 1].due; i++) {
            System.out.println(mapEvents.get(i));
            if(mapEvents.get(i) == null || mapEvents.get(i) == 1) {
                stringIdxToRealTime[curIdx] = i;
                sb.append(mapEvents.get(i) == null? '0': '1');
                curIdx++;
            }
        }

        String optimal = calcMinCost(sb.toString(), optimalK);

        ArrayList<StaticTask> rawChunks = new ArrayList<>();
        // Identify all 15 minute chunks of work
        // Merge consecutive chunks of 0s
        curIdx = 0;
        for(int i = 0; i < tasks.length; i++) {
            for(int j = 0; j < tasks[i].duration; j++) {
                while(optimal.charAt(curIdx) != '1') curIdx++;
                long trueIdx = stringIdxToRealTime[curIdx];

                // first check if you should merge with previous instead of adding a new one
                if(!rawChunks.isEmpty()) {
                    StaticTask top = rawChunks.get(rawChunks.size() - 1);
                    if(top.ID.equals(tasks[i].ID) && top.start + top.duration == trueIdx)
                        top.duration++;
                    else rawChunks.add(new StaticTask(tasks[i].ID, 1, trueIdx));
                }
                else rawChunks.add(new StaticTask(tasks[i].ID, 1, trueIdx));
                curIdx++;
            }
        }

        for(int i = 0; i < events.length; i++) {
            for(int j = 0; j < events[i].duration; j++) {
                rawChunks.add(events[i]);
            }
        }

        Collections.sort(rawChunks, (a, b) -> Long.compare(a.start, b.start));

        return rawChunks;


//        System.out.println(sb);
//        for(int i = 0; i < sb.length(); i++) {
//            System.out.print(stringIdxToRealTime[i] + " ");
//        }
    }

}
