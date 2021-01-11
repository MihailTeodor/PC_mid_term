import java.util.concurrent.ConcurrentHashMap;

public class Par_thread_no_future implements Runnable {

    public int N;
    public String MODE;

    private double start, stop;
    private String id;
    private char[] fileString;

    public ConcurrentHashMap<String, Integer> dict;

    StringBuilder builder;


    public Par_thread_no_future(String id, double start, double stop, char[] fileString, ConcurrentHashMap<String, Integer> dict, int num_bigrams, String MODE){
        this.N = num_bigrams;
        this.MODE = MODE;
        this.id = id;
        this.start = start;
        this.stop = stop;
        this.fileString = fileString;
        this.dict = dict;
    }


    public ConcurrentHashMap<String, Integer> compute_words(char[] fileString, ConcurrentHashMap<String, Integer> dict) {

        if(start != 0) {
            while (fileString[(int) start] != '.')
                start -= 1;
            start += 1;
        }

        stop -= (N - 3);
        for (int i = 0; i < N - 1; i++) {
            if(stop <= fileString.length){
                while (stop < fileString.length && fileString[(int) stop] != '.' )
                    stop += 1;
                stop += 1;
            }
        }
        stop -= 1;

        if (stop > fileString.length)
            stop = fileString.length;

        double i = start;
        String key;

        while (i < stop) {
            builder = new StringBuilder();
            int j = 0;
            double k = 0;
            int count = 0;
            key = null;

            while(j < N){
                char tmp;
                if(i < stop)
                    tmp = fileString[(int)i];
                else
                    tmp = '.';

                if (tmp == '.'){
                    if(i < stop)
                        k = i - count;
                    else {
                        if(j != N - 1)
                            j += N;
                        k = i;
                    }
                    i = i+1;
                    j = j+1;
                    builder.append(" ");
                    if(N > 2) {
                        if (j == 1)
                            count = N - 2;
                    }else
                        count = 0;
                }else{
                    i = i+1;
                    builder.append(tmp);
                    count ++;
                }
                if(j == N)
                    key = builder.toString();

            }

            if(key != null) {

                if (dict.putIfAbsent(key, 1) != null) {
                    dict.computeIfPresent(key, (w, val) -> val + 1);
                }
            }
            if(N != 1)
                i = k;
        }
        return dict;
    }


    public ConcurrentHashMap<String, Integer> compute_chars(char[] fileString, ConcurrentHashMap<String, Integer> dict) {

        for(double i = start + N - 1; i <= stop; i++) {
            builder = new StringBuilder();

            for(int j = N - 1; j >= 0; j--) {
                builder.append(fileString[(int)(i - j)]);
            }

            String key = builder.toString();

            if (dict.putIfAbsent(key, 1) != null) {
                dict.computeIfPresent(key, (w, val) -> val + 1);
            }
        }
        return dict;
    }


    public void run() {

        if(MODE.equals("word"))
            compute_words(fileString, dict);
        else
            compute_chars(fileString, dict);
    }
}
