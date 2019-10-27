package pegorov.lesson2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChatFileWork {
    private InputStreamReader inputStreamReader;
    private OutputStreamWriter outputStreamWriter;
    private File f;

    private static String PATH_HISTORY;

    public ChatFileWork(String name) throws FileNotFoundException {
        PATH_HISTORY = "history_" + name + ".txt";

        f = new File(PATH_HISTORY);

        if (!f.exists()) {
            try {
                f.createNewFile();
                System.out.println("Файл чата не найден. Создадим его по пути: " + PATH_HISTORY);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void safeFile(String txt, String newName) throws IOException {
        outputStreamWriter = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);

        /*if (!newName.equalsIgnoreCase("")){
           OutputStreamWriter newOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("history_" + newName + ".txt"), StandardCharsets.UTF_8);

           newOutputStreamWriter.write(txt);
           newOutputStreamWriter.flush();
        }*/
        outputStreamWriter.write(txt);
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    public String loadFile100(int position) throws IOException {
        String allFile = "";

        inputStreamReader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);

        int x;
        while ((x = inputStreamReader.read()) != -1) {
            allFile = allFile + (char) x;
            System.out.println("Прочитан символ из файла: " + (char) x);
        }

        System.out.println("Данные в файле: " + allFile);
        String[] file = allFile.split("\n");
        System.out.println("Количество строк: " + file.length);

        String returnFile = "";

        int count = 2; /// Количество записей для чтения по условию задачи 100
        int pos_Start = ((file.length - position - count) > 0) ? (file.length - position - count) : 0;
        int pos_Finish = ((file.length - position) > 0) ? (file.length - position) : 0;

        for (int i = pos_Start; i< pos_Finish; i++){
            returnFile = returnFile + file[i] + "\n";
        }

        System.out.println("Взяли из файла: " + returnFile);

        inputStreamReader.close();

        return returnFile;
    }

    public void close() throws IOException {


    }
}
