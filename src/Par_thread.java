import java.util.HashMap;
import java.util.concurrent.Callable;

public class Par_thread implements Callable<HashMap<String, Integer>> {

    public int N;
    public String MODE;

    private double start, stop;
    private String id;
    private HashMap<String, Integer> thread_dict;
    private char[] fileString;

    StringBuilder builder;


    public Par_thread(String id, double start, double stop, char[] fileString, int N, String MODE){
        this.N = N;
        this.MODE = MODE;
        this.id = id;
        this.start = start;
        this.stop = stop;
        this.fileString = fileString;
        this.thread_dict = new HashMap();
    }


    public HashMap<String, Integer> compute_words(char[] fileString, HashMap<String, Integer> hashMap) {

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
            StringBuilder builder = new StringBuilder();
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
                if (!thread_dict.containsKey(key)) {
                    thread_dict.put(builder.toString(), 1);
                } else if (thread_dict.containsKey(key)) {
                    thread_dict.put(builder.toString(), thread_dict.get(key) + 1);
                }
            }
            if(N != 1)
                i = k;
        }
        return thread_dict;
    }


    public HashMap<String, Integer> compute_chars(char[] fileString, HashMap<String, Integer> hashMap) {

        for(double i = start + N - 1; i <= stop; i++) {
            StringBuilder builder = new StringBuilder();

            for(int j = N - 1; j >= 0; j--) {
                builder.append(fileString[(int)(i - j)]);
            }

            String key = builder.toString();

            if (!thread_dict.containsKey(key)) {
                thread_dict.put(builder.toString(), 1);
            }
            else if (thread_dict.containsKey(key)) {
                thread_dict.put(builder.toString(), thread_dict.get(key) + 1);
            }
        }
        return hashMap;
    }


    public HashMap<String, Integer> call() {

        if(MODE.equals("word"))
            compute_words(fileString, thread_dict);
        else
            compute_chars(fileString, thread_dict);

        return thread_dict;
    }
}
