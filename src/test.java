import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class test {
    public static void main(String[] args){
        String[] dirList = {"texts10", "texts15"};
        int[] N_gram = {2, 3, 5, 10};
        int[] num_threads = {2, 4, 8};
        int NUM_ITER = 10;

        String mode = "words";

        String m;
        if(mode.equals("word"))
            m = mode;
        else
            m = "character";

        long start, end;
        long tmp_time;

        for (String dir : dirList) {

            for(int n_gram : N_gram) {

                tmp_time = 0;
                for(int k = 0; k < NUM_ITER; k++) {
                    HashMap<String, Integer> seq_dict = new HashMap<String, Integer>();

                    LinkedList<String> seq_txtList = new LinkedList<String>();
                    pre_process.loadDatasets(seq_txtList, dir);

                    start = System.currentTimeMillis();
                    Bigrams_seq.iterate_txt(seq_txtList, seq_dict, mode, n_gram);
                    end = System.currentTimeMillis();

                    tmp_time += (end - start);

                }

                double seq_time = (tmp_time / (double)NUM_ITER);

                for(int th : num_threads){
                    tmp_time = 0;

                    for(int k=0; k < NUM_ITER; k++ ) {
                        ConcurrentHashMap<String, Integer> par_dict = new ConcurrentHashMap<String, Integer>();

                        LinkedList<String> par_txtList = new LinkedList<String>();
                        pre_process.loadDatasets(par_txtList, dir);

                        start = System.currentTimeMillis();
                        Bigrams_par.iterate_txt(par_txtList, par_dict, mode, n_gram, th);
                        end = System.currentTimeMillis();

                        tmp_time += (end - start);

                    }
                    double par_time = (tmp_time / (double)NUM_ITER);

                    System.out.println("Computation on dataset " + dir + " of " + n_gram + "-gram of " + m + " using " + th + " threads for parallel version." );
                    System.out.println("SEQUENTIAL time: " + seq_time);
                    System.out.println("PARALLEL time: " + par_time);
                    System.out.println("SPEEDUP: " + (seq_time / par_time));
                    System.out.println("");


                }
            }
        }
    }
}