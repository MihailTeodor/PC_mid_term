import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bigrams_par {

    public static String MODE = "word";
    public static int N = 2;
    public static int NUM_THREADS = 2;



    public static void loadDatasets(LinkedList<String> txtListMain) {

        Scanner sc= new Scanner(System.in);
        System.out.println("Insert name of directory of files to load");
        String directory= sc.nextLine();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directory))){
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    txtListMain.add("./" + directory + "/" + path.getFileName().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Loaded " +  txtListMain.size() + " files \n");
    }


    public static char[] process_txt(String txt) {
        Path path = Paths.get(txt);

        try {
            Stream<String> lines = Files.lines(path);
            char[] filestring;
            if(MODE.equals("word")) {
                filestring = (lines.collect(Collectors.joining(" ")))
                        .replaceAll("[ \\uFEFF'();:,\\-\\[\\]\\-?‐!—+”“@*\"=’/{}|_.]+", ".").toCharArray();
            }else {
                filestring = (lines.collect(Collectors.joining(" ")))
                        .replaceAll("[ \\uFEFF'();:,\\-\\[\\]\\-?‐!—+”“@*\"=’/{}|_.]+", "").toCharArray();
            }

            for(int i = 0; i < filestring.length - 1; ++i) {
                if (Character.isUpperCase(filestring[i])) {
                    filestring[i] = Character.toLowerCase(filestring[i]);
                }
            }

            return filestring;
        }

        catch (IOException e) {
            System.out.println(e);
            System.exit(1);
            return null;
        }
    }


    public static ConcurrentHashMap<String, Integer> HashMerge(ConcurrentHashMap<String, Integer> tmp_dict, ConcurrentHashMap<String, Integer> finalDict) {
        for (ConcurrentHashMap.Entry<String, Integer> entry : tmp_dict.entrySet()) {
            int newValue = entry.getValue();
            Integer existingValue = finalDict.get(entry.getKey());
            if (existingValue != null) {
                newValue = newValue + existingValue;
            }
            finalDict.put(entry.getKey(), newValue);
        }
        return finalDict;
    }


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


    public static ConcurrentHashMap<String, Integer> iterate_txt(LinkedList<String> txtList, ConcurrentHashMap<String, Integer> dict) {

        ArrayList<Future> futuresArray = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        while (!txtList.isEmpty()) {

            String txtName = txtList.poll();

            char[] file = process_txt(txtName);
            int fileLen = file.length;
            double k = Math.floor(fileLen / NUM_THREADS);
            double stop;
            for (int i = 0; i < NUM_THREADS; i++) {
                if(i == NUM_THREADS - 1)
                    stop = fileLen -1;
                else
                    stop = ((i + 1) * k) + (N - 1) - 1;

                Future f = executor.submit(new Par_thread("t" + i, i * k, stop, file));
                futuresArray.add(f);
            }
        }
        try {
            for (Future<ConcurrentHashMap<String, Integer>> f : futuresArray) {
                ConcurrentHashMap<String, Integer> tmp_dict = f.get();
                HashMerge(tmp_dict, dict);
            }
            awaitTerminationAfterShutdown(executor);

        } catch (Exception e) {
            System.out.println(e);
        }
        return dict;
    }


    public static void main(String[] args) {

        long start, end;
        LinkedList<String> txtList = new LinkedList<String>();
        loadDatasets(txtList);

        ConcurrentHashMap<String, Integer> dict = new ConcurrentHashMap();

        start = System.currentTimeMillis();
        iterate_txt(txtList, dict);
        end = System.currentTimeMillis();

        Set set = dict.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry map_entry = (Map.Entry) iterator.next();
            System.out.print("key: " + map_entry.getKey() + " , value: ");
            System.out.println(map_entry.getValue());
        }
        System.out.println(end - start);
    }
}
