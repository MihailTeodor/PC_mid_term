import java.util.*;
import java.util.concurrent.*;


public class Bigrams_par_no_future {

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {

        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    public static ConcurrentHashMap<String, Integer> iterate_txt(LinkedList<String> txtList, ConcurrentHashMap<String, Integer> dict, String MODE, int num_bigrams, int NUM_THREADS) {

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        while (!txtList.isEmpty()) {

            String txtName = txtList.poll();
            char[] file = pre_process.process_txt(txtName, MODE);

            int fileLen = file.length;
            double k = Math.floor(fileLen / NUM_THREADS);
            double stop;
            for (int i = 0; i < NUM_THREADS; i++) {
                if(i == NUM_THREADS - 1)
                    stop = fileLen -1;
                else
                    stop = ((i + 1) * k) + (num_bigrams - 1) - 1;

                executor.execute(new Par_thread_no_future("t" + i, i * k, stop, file, dict, num_bigrams, MODE));
            }
        }
        awaitTerminationAfterShutdown(executor);
        return dict;
    }


    public static void main(String[] args) {

        String MODE = "words";
        int N = 10;
        int NUM_THREADS = 2;

        Scanner sc= new Scanner(System.in);
        System.out.println("Insert name of directory of files to load");
        String directory= sc.nextLine();

        long start, end;
        LinkedList<String> txtList = new LinkedList<String>();
        pre_process.loadDatasets(txtList, directory);

        ConcurrentHashMap<String, Integer> dict = new ConcurrentHashMap();

        start = System.currentTimeMillis();
        iterate_txt(txtList, dict, MODE, N, NUM_THREADS);
        end = System.currentTimeMillis();

        Set set = dict.entrySet();
        Iterator iterator = set.iterator();

        int values = 0;
        while (iterator.hasNext()) {
            Map.Entry map_entry = (Map.Entry) iterator.next();
//            System.out.print("key: " + map_entry.getKey() + " , value: ");
//            System.out.println(map_entry.getValue());

            values += (int)map_entry.getValue();

        }

        String mode;
        if(MODE.equals("word"))
            mode = MODE;
        else
            mode = "character";

        System.out.println(end - start);
        System.out.println("Number of " + N + "-grams of " + mode + " found: " + values);
        System.out.println("Number of DISTINCT " + N + "-grams of " + mode + " found: " +set.size());
    }
}
