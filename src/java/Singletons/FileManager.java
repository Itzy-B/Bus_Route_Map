package src.java.Singletons;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class FileManager {
    private static FileManager instance;

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }


    public List<String> getFile(String pathToRead) {
        List<String> lines;
        try {
            Path path = Paths.get(pathToRead);
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                System.out.println(line);
            }
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("IO Error");
        }

        return lines;
    }

    public void writeToFile(String path, String fileName, String contentToWrite) {
        String fullPath = path + "/" + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            writer.write(contentToWrite);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToDisk(Object object, String path) {

    }

    public ObjectOutputStream serializeObject(Object object, String path) {
        ObjectOutputStream out = null;

        try {
            FileOutputStream fileOut = new FileOutputStream("userObject.ser");
            out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in userObject.ser");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    public Object getObject (String filePath) throws IOException, ClassNotFoundException {
        Object object = null;
        try (FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            object = (Object) ois.readObject();
        }
        return object;
    }

    }