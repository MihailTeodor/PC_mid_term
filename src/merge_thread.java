import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class merge_thread implements Runnable {

    private ConcurrentHashMap<String, Integer> final_dict;
    private HashMap<String, Integer> dict;

    public merge_thread(HashMap<String, Integer> dict, ConcurrentHashMap<String, Integer> final_dict){
        this.dict = dict;
        this.final_dict = final_dict;
    }

    public void run(){

        for (HashMap.Entry<String, Integer> entry : dict.entrySet()) {

            int newValue = entry.getValue();
            String key = entry.getKey();

            if(final_dict.putIfAbsent(key, newValue) != null) {
                final_dict.computeIfPresent(key, (k, val) -> val + newValue);
            }
        }
    }
}
