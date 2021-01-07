import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class pre_process {

    public static void loadDatasets(LinkedList<String> txtListMain, String directory) {

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directory))){
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    txtListMain.add("./" + directory + "/" + path.getFileName().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("Loaded " +  txtListMain.size() + " files \n");
    }


    public static char[] process_txt(String txt, String mode) {
        Path path = Paths.get(txt);

        try {
            Stream<String> lines = Files.lines(path);
            char[] filestring = null;
            if(mode.equals("word")) {
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
}
