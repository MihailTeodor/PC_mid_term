import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class Par_thread implements Callable<ConcurrentHashMap<String, Integer>> {

    public static int N = Bigrams_par.N;
    public static String MODE = Bigrams_par.MODE;

    private double start, stop;
    private String id;
    private ConcurrentHashMap<String, Integer> thread_dict;
    private char[] fileString;

    StringBuilder builder;

    public Par_thread(String id, double start, double stop, char[] fileString){

        this.id = id;
        this.start = start;
        this.stop = stop;
        this.fileString = fileString;
        this.thread_dict = new ConcurrentHashMap();

    }

    public ConcurrentHashMap<String, Integer> compute_words(char[] fileString, ConcurrentHashMap<String, Integer> hashMap) {

        if (stop > fileString.length-1){
            stop = fileString.length-1;
        }

        double i = start;
        while (i < stop) {
            StringBuilder builder = new StringBuilder();
            int j = 0;
            double k = 0;
            int count = 0;
            while(j < N){

                char tmp;

                if(i < stop)
                    tmp = fileString[(int)i];
                else tmp = '.';

                if (tmp == '.'){
                    k = i - count;
                    i = i+1;
                    j = j+1;
                    builder.append(" ");
                    count = 0;
                }else{
                    i = i+1;
                    builder.append(tmp);
                    count ++;
                }
            }
            String key = builder.toString();

            if (!thread_dict.containsKey(key)) {
                thread_dict.put(builder.toString(), 1);
            }
            else if (thread_dict.containsKey(key)) {
                thread_dict.put(builder.toString(), thread_dict.get(key) + 1);
            }
            if(N != 1)
                i = k;
        }
        return thread_dict;
    }


    public ConcurrentHashMap<String, Integer> compute_chars(char[] fileString, ConcurrentHashMap<String, Integer> hashMap) {

        if (stop > fileString.length-1){
            stop = fileString.length-1;
        }
        for(double i = start + N - 1; i < stop; i++) {
             builder = new StringBuilder();

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


    public ConcurrentHashMap<String, Integer> call() {

        if(MODE.equals("word"))
            compute_words(fileString, thread_dict);
        else
            compute_chars(fileString, thread_dict);

        return thread_dict;
    }
}