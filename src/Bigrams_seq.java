import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Bigrams_seq {

    public static String MODE = "word";
    public static int N = 2;


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

    public static HashMap<String, Integer> compute_words(char[] fileString, HashMap<String, Integer> hashMap) {

        int i = 1;
        while (i < fileString.length) {
            StringBuilder builder = new StringBuilder();
            int j = 0;
            int k = 0;
            int count = 0;
            while(j < N){

                char tmp;

                if(i < fileString.length)
                    tmp = fileString[i];
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

            if (!hashMap.containsKey(key)) {
                hashMap.put(builder.toString(), 1);
            }
            else if (hashMap.containsKey(key)) {
                hashMap.put(builder.toString(), hashMap.get(key) + 1);
            }
            if(N != 1)
                i = k;
        }
        return hashMap;
    }

    public static HashMap<String, Integer> compute_chars(char[] fileString, HashMap<String, Integer> hashMap) {


        for(int i = 0; i < fileString.length - N + 1; ++i) {
            StringBuilder builder = new StringBuilder();

            for(int j = 0; j < N; ++j) {
                builder.append(fileString[i + j]);
            }

            String key = builder.toString();

            if (!hashMap.containsKey(key)) {
                hashMap.put(builder.toString(), 1);
            }
            else if (hashMap.containsKey(key)) {
                hashMap.put(builder.toString(), hashMap.get(key) + 1);
            }
        }

        return hashMap;
    }


    public static HashMap<String, Integer> iterate_txt(LinkedList<String> txtList, HashMap<String, Integer> dict) {


        while(!txtList.isEmpty()) {

                String txtName = txtList.poll();
                char[] file = process_txt(txtName);
                if(MODE.equals("word"))
                    compute_words(file, dict);
                else
                    compute_chars(file, dict);

        }
        return dict;
    }



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


    public static void main(String[] args) {

        LinkedList<String> txtList = new LinkedList<String>();

        loadDatasets(txtList);

        HashMap<String, Integer> dict = new HashMap<String, Integer>();
        long start, end;

        start = System.currentTimeMillis();
        iterate_txt(txtList, dict);
        end = System.currentTimeMillis();

        Set set = dict.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry map_entry = (Entry) iterator.next();
            System.out.print("key: " + map_entry.getKey() + " , value: ");
            System.out.println(map_entry.getValue());
        }

        System.out.println(end - start);

    }
}
