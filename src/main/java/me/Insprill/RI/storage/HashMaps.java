package me.Insprill.RI.storage;

import java.io.*;
import java.util.HashMap;

public class HashMaps {

    public static void saveHashMap(String filePath, HashMap<?, ?> hashMap) {
        File file = new File(filePath);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            oos.writeObject(hashMap);
            oos.flush();
        } catch (Exception e) {
            System.out.println("An error occurred while saving '" + filePath + "'! Error: " + e.getMessage());
        }
    }

    public static HashMap<?, ?> loadHashMap(String filePath) {
        File file = new File(filePath);
        HashMap<?, ?> temp = new HashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            temp = (HashMap<?, ?>) in.readObject();
            System.out.println("Successfully loaded " + filePath);
            return temp;
        } catch (EOFException ignored) {
            return temp;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new HashMap<>();
        }
    }

}
