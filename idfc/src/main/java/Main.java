import java.util.*;

public class Main {

    static class Log {
        String api;
        int code;
        int time;

        public Log(String api, int code, int time) {
            this.api = api;
            this.code = code;
            this.time = time;
        }

        public Log() {

        }
    }

    static class ApiCount {
        String api;
        int count;


        public ApiCount(String api, int count) {
            this.api = api;
            this.count = count;
        }
    }

    static class ApiTime {
        String api;
        long time;


        public ApiTime(String api, long time) {
            this.api = api;
            this.time = time;
        }
    }



    public static void main(String[] args) {

        String input = "INFO[GET][/api/users][200][120ms] INFO[GET][/api/orders][404][60ms] " +
                "INFO[GET][/api/users][500][95ms] INFO[GET][/api/orders][200][180ms] " +
                "INFO[POST][/api/orders][200][310ms] DEBUG [OPTIONS][/api/orders][204][-] " +
                 "INFO[POST][/api/users][210][250ms]";
        Main.printSummary(input);


    }

    public static void printSummary(String input) {

        String[] strings = input.split("\\[|]");

        ArrayList<Log> logs = new ArrayList<>();
        for(int i = 3; i < strings.length; i+=4) {

                String s1 = null, s2 = null, s3 = null;
                int i2 = 0, i3 = 0;
                    String s = strings[i-2];
                    s1 = s.substring(1);

                    s = strings[i-1];

                    s2 = s.substring(1);
                    i2 = Integer.parseInt(s2);

                    s = strings[i];
                    if(!s.contains("-")) {
                        s3 = s.substring(1);
                        s3 = s3.substring(0, s3.length() - 2);
                        i3 = Integer.parseInt(s3);
                    } else {
                        i3 = Integer.MAX_VALUE;

                    }


                Log log = new Log(s1, i2, i3);
                logs.add(log);

        }

        Map<String, Integer> m = new HashMap<>();

        for(Log l : logs) {
            m.put(l.api, m.getOrDefault(l.api, 0) + 1);
        }

        List<ApiCount> apiCountList = new ArrayList<>();
        for(Map.Entry<String, Integer> e : m.entrySet()) {
            ApiCount apiConunt = new ApiCount(e.getKey(), e.getValue());
            apiCountList.add(apiConunt);
        }

        apiCountList.sort((a, b) -> b.count - a.count);

        System.out.println("Requests per Endpoint");
        for(ApiCount a : apiCountList) {
            System.out.println(a.api + " " + a.count);
        }

        //------------------
        Map<String, List<Integer>> m1 = new HashMap<>();
        for(Log l : logs) {
             m1.putIfAbsent(l.api, new ArrayList<>());
            List<Integer> al = m1.get(l.api);
            al.add(l.time);

        }


        List<ApiTime> apiTimes = new ArrayList<>();
        for(Map.Entry<String, List<Integer>> e : m1.entrySet()) {

            int n = e.getValue().size();
            double sum = 0;
            for(Integer i : e.getValue()) {

                if(i == Integer.MAX_VALUE) {
                    n--;
                    continue;
                }
                sum += i;
            }
            long avg = Math.round(sum/n);

            ApiTime apiTime = new ApiTime(e.getKey(), avg);
            apiTimes.add(apiTime);
        }

        System.out.println("Avg Response Time");
        for(ApiTime a : apiTimes) {
            System.out.println(a.api + " " + a.time);
        }

        //----------------------
        logs.sort((a,b) -> b.time - a.time);
        System.out.println("Slowest call endpoint");
        Log log = null;
        for (Log l : logs) {
            if(l.time == Integer.MAX_VALUE) {
                continue;
            }

            log = l;
            break;
        }
        if(log != null) {
            System.out.println(log.api + " " + log.time);
        }


        //------------------------

        double count = 0;
        double n = logs.size();

        for(Log l : logs) {
            if(l.code >= 400) {
                count++;
            }
        }

        double err = (count * 100 )/n;
        System.out.println("Error Rate");
        System.out.println(err);





















    }
}
