import java.util.*;


public class Bigrams_seq {

    public static HashMap<String, Integer> compute_words(char[] fileString, HashMap<String, Integer> hashMap, int N) {

        int i = 0;
        String key;
        while (i < fileString.length) {
            StringBuilder builder = new StringBuilder();
            int j = 0;
            int k = 0;
            int count = 0;
            key = null;
            while(j < N){

                char tmp;
                if(i < fileString.length)
                    tmp = fileString[i];
                else tmp = '.';

                if (tmp == '.'){
                    if(i < fileString.length)
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
                if (!hashMap.containsKey(key)) {
                    hashMap.put(builder.toString(), 1);
                } else if (hashMap.containsKey(key)) {
                    hashMap.put(builder.toString(), hashMap.get(key) + 1);
                }
            }

            if(N != 1)
                i = k;
        }
        return hashMap;
    }


    public static HashMap<String, Integer> compute_chars(char[] fileString, HashMap<String, Integer> hashMap, int N) {

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


    public static HashMap<String, Integer> iterate_txt(LinkedList<String> txtList, HashMap<String, Integer> dict, String MODE, int N) {

        //System.out.println("Computing " + N + "-grams of " + MODE);

        while(!txtList.isEmpty()) {

                String txtName = txtList.poll();
                char[] file = pre_process.process_txt(txtName, MODE);
                if(MODE.equals("word"))
                    compute_words(file, dict, N);
                else
                    compute_chars(file, dict, N);

        }
        return dict;
    }


    public static void main(String[] args) {

        String MODE = "words";
        int N = 10;

        Scanner sc= new Scanner(System.in);
        System.out.println("Insert name of directory of files to load");
        String directory= sc.nextLine();

        LinkedList<String> txtList = new LinkedList<String>();
        pre_process.loadDatasets(txtList, directory);

        HashMap<String, Integer> dict = new HashMap<String, Integer>();
        long start, end;

        start = System.currentTimeMillis();
        iterate_txt(txtList, dict, MODE, N);
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
