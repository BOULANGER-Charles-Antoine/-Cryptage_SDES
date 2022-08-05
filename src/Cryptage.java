/*
Nom du Programme : Cryptage SDES (Simplified Data Encryption Standard)
But : Crypter et décrypter un message
Auteur : Boulanger Charles-Antoine
Date : 07/05/2020
Version : V1.0
*/

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Cryptage {
    public static void main(String[] args) throws IOException {
        String pathToFile = "C:\\";
        String fileToEncrypt = pathToFile + "FichierACrypter.txt";
        String fileToDecrypt = pathToFile + "FichierADecrypter.txt";
        String key = "0010010111";
        SDES sdes = new SDES(key);
        System.out.println("K1 " + Arrays.toString(sdes.getK1()));
        System.out.println("K2 " + Arrays.toString(sdes.getK2()) +"\n");


        // Cryptage du fichier
        System.out.println("Début de cryptage du fichier");
        char ch;
        short[] testShort = new short[8];
        short[] out = new short[8];
        InputStreamReader sr = new InputStreamReader(new FileInputStream(fileToEncrypt), "utf8");
        File outFile = new File(pathToFile + "FichierADecrypter.txt");
        BufferedWriter br = new BufferedWriter(new FileWriter(outFile, StandardCharsets.UTF_8));
        while (sr.ready()){
            ch = (char) sr.read();
            SDES.charToShort(ch, testShort);
            sdes.cryptageSDES(testShort, out);

            // Ecriture du cryptage dans le fichier
            br.write(String.valueOf(SDES.shortToInt(out)));
            br.newLine();
        }
        br.close();
        sr.close();
        System.out.println("Fin de cryptage du fichier\n");

        // Décryptage du fichier
        System.out.println("Début de décryptage du fichier");
        String fichierDecrypter = "";
        BufferedReader file = new BufferedReader(new FileReader(fileToDecrypt));
        while(file.ready()) {
            String str = file.readLine();
            int test = Integer.parseInt(str);

            SDES.charToShort((char) test, testShort);
            sdes.decryptageSDES(testShort, out);
            fichierDecrypter += SDES.shortToChar(out);
        }
        file.close();

        // Affichage du fichier décrypté
        System.out.print("Le fichier décrypté est : \n\n" + fichierDecrypter);
        System.out.println("\n\nFin de décryptage du fichier\n");
    }
}
