import java.io.*;

public class SDES {
    private String key;
    private final int KEYLENGTH = 10;
    private final int BYTELENGTH = 8;
    private short[] keyB;
    private short[] K1;
    private short[] K2;

    // Constructeur du SDES
    public SDES(String key){
        this.key = key;

        // Calcul des clés K1 et K2
        keyB = new short[KEYLENGTH];
        for(int i = 0; i < KEYLENGTH; i++)
            keyB[i] = (short) (this.key.charAt(i) - 48); // this.key.charAt(i) renvoie le code ASCII donc -48

        // Calcul de la clé K1
        short[] P10 = new short[]{keyB[2], keyB[4], keyB[1], keyB[6], keyB[3], keyB[9], keyB[0], keyB[8], keyB[7], keyB[5]};
        short[] bitsGauche = new short[KEYLENGTH / 2];
        short[] bitsDroite = new short[KEYLENGTH / 2];
        for(int j = 0; j < KEYLENGTH / 2; j++){
            bitsGauche[j] = P10[j];
            bitsDroite[j] = P10[j + KEYLENGTH / 2];
        }
        leftShift1(bitsGauche);
        leftShift1(bitsDroite);
        for(int j = 0; j < KEYLENGTH / 2; j++){
            P10[j] = bitsGauche[j];
            P10[j + KEYLENGTH / 2] = bitsDroite[j];
        }
        K1 = new short[]{ P10[5], P10[2], P10[6], P10[3], P10[7], P10[4], P10[9], P10[8] };

        // Calcul de la clé K2
        leftShift1(bitsGauche);
        leftShift1(bitsGauche);
        leftShift1(bitsDroite);
        leftShift1(bitsDroite);
        for(int j = 0; j < KEYLENGTH / 2; j++){
            P10[j] = bitsGauche[j];
            P10[j + KEYLENGTH / 2] = bitsDroite[j];
        }
        K2 = new short[]{ P10[5], P10[2], P10[6], P10[3], P10[7], P10[4], P10[9], P10[8] };
    }


    // Méthode effectuant un décalage circulaire gauche de 1 bit
    private void leftShift1(short[] listeB){
        short temp = listeB[0];
        for(int i = 0; i < BYTELENGTH / 2 - 1; i++){
            listeB[i] = listeB[i + 1];
        }
        listeB[BYTELENGTH / 2] = temp;
    }


    public short[] getK1(){
        return K1;
    }


    public short[] getK2() {
        return K2;
    }


    // Méthode effectuant la permutation IP
    public void makeIP(short[] liste){
        short[] temp = new short[]{liste[1], liste[5], liste[2], liste[0], liste[3], liste[7], liste[4], liste[6]};
        System.arraycopy(temp, 0, liste, 0, 8);
    }


    // Méthode effectuant la permutation IP-1
    public void makeIP_1(short[] liste){
        short[] temp = new short[]{liste[3], liste[0], liste[2], liste[4], liste[6], liste[1], liste[7], liste[5]};
        System.arraycopy(temp, 0, liste, 0, 8);
    }


    // Applique une opération OU Exclusif entre deux shorts passé en paramètres
    public short xor(short entree1, short entree2){
        if(entree1 == entree2)
            return 0;
        else
            return 1;
    }


    // Méthode effectuant le cryptage d'un caractère entree et le stocke dans sortie
    public void fk(short[] entree, short[] sortie, short[] cle){
        short[][]  S1= { {1, 0, 3, 2},
                         {3, 2, 1, 0},
                         {0, 2, 1, 3},
                         {3, 1, 3, 2} };
        short[][]  S2= { {0, 1, 2, 3},
                         {2, 0, 1, 3},
                         {3, 0, 1, 0},
                         {2, 1, 0, 3} };

        short[] bitsGauche = new short[4];
        short[] bitsDroite = new short[4];
        for(int i = 0; i < BYTELENGTH / 2; i++){
            bitsGauche[i] = entree[i];
            bitsDroite[i] = entree[i + BYTELENGTH / 2];
        }

        short[] EP = new short[]{ bitsDroite[3], bitsDroite[0], bitsDroite[1], bitsDroite[2], bitsDroite[1], bitsDroite[2], bitsDroite[3], bitsDroite[0] };
        for(int i = 0; i < BYTELENGTH; i++)
            EP[i] = xor(EP[i], cle[i]);
        int p0 = EP[0] * 10 + EP[3], //p0 = (p0,0 p0,3)
            p1 = EP[1] * 10 + EP[2]; //p1 = (p0,1 p0,2)
        int p2 = EP[4] * 10 + EP[7], //p2 = (p1,0 p1,3)
            p3 = EP[5] * 10 + EP[6]; //p3 = (p1,1 p1,2)

        short nbS1 = conversionBinaire( S1[conversionDecimal(p0)][conversionDecimal(p1)] );
        short nbS2 = conversionBinaire( S2[conversionDecimal(p2)][conversionDecimal(p3)] );

        short[] P4 = new short[] {(short)(nbS1 % 10), (short)(nbS2 % 10), (short)(nbS2 / 10), (short)(nbS1 / 10) };

        for(int i = 0; i < BYTELENGTH / 2; i++) {
            sortie[i] = xor(bitsGauche[i], P4[i]);
            sortie[i + BYTELENGTH / 2] = bitsDroite[i];
        }
    }


    // Convertit un nombre décimal en nombre binaire
    public static short conversionBinaire(int decimal) {
        short bin = 0;
        int i = 0;
        while(true) {
            if(decimal == 0) {
                break;
            } else {
                bin += (decimal % 2) * Math.pow(10, i);
                decimal /= 2;
                i++;
            }
        }

        return bin;
    }


    // Convertit un nombre binaire en nombre déciamal
    public static short conversionDecimal(int bin){
        short decimal = 0;
        int i = 0;
        while(true){
            if(bin == 0){
                break;
            } else {
                decimal += (bin % 10) * Math.pow(2, i);
                bin /= 10;
                i++;
            }
        }

        return decimal;
    }


    // Effectue le cryptage d'un short passé dans entree et le renvoie dans sortie
    public void cryptageSDES(short[] entree, short[] sortie) throws IOException {
        for(int i = 0; i < BYTELENGTH; i++)
            if(entree[i] != 0 && entree[i] != 1)
                entree[i] = 0;

        short[] temp = new short[BYTELENGTH];

        makeIP(entree);
        fk(entree, temp, K1);
        entree = temp;

        short temp2;
        for(int i = 0; i < BYTELENGTH / 2; i++){
            temp2 = entree[i + BYTELENGTH / 2];
            entree[i + BYTELENGTH /2] = entree[i];
            entree[i] = temp2;
        }

        fk(entree, sortie, K2);
        makeIP_1(sortie);
    }


    // Méthode effectuant le décryptage d'un caractère entree et le stocke dans sortie
    public void fk_1(short[] entree, short[] sortie, short[] cle){
        short[][] S1= { {1, 0, 3, 2},
                        {3, 2, 1, 0},
                        {0, 2, 1, 3},
                        {3, 1, 3, 2} };
        short[][] S2= { {0, 1, 2, 3},
                        {2, 0, 1, 3},
                        {3, 0, 1, 0},
                        {2, 1, 0, 3} };

        short[] bitsGauche = new short[BYTELENGTH / 2];
        short[] bitsDroite = new short[BYTELENGTH / 2];
        for(int i = 0; i < BYTELENGTH / 2; i++){
            bitsGauche[i] = entree[i];
            bitsDroite[i] = entree[i + BYTELENGTH / 2];
        }

        short[] EP = new short[]{ bitsDroite[3], bitsDroite[0], bitsDroite[1], bitsDroite[2], bitsDroite[1], bitsDroite[2], bitsDroite[3], bitsDroite[0] };
        for(int i = 0; i < BYTELENGTH; i++)
            EP[i] = xor(EP[i], cle[i]);
        int p0 = EP[0] * 10 + EP[3], //p0 = (p0,0 p0,3)
            p1 = EP[1] * 10 + EP[2]; //p1 = (p0,1 p0,2)
        int p2 = EP[4] * 10 + EP[7], //p2 = (p1,0 p1,3)
            p3 = EP[5] * 10 + EP[6]; //p3 = (p1,1 p1,2)

        short nbS1 = conversionBinaire( S1[conversionDecimal(p0)][conversionDecimal(p1)] );
        short nbS2 = conversionBinaire( S2[conversionDecimal(p2)][conversionDecimal(p3)] );

        short[] P4 = new short[] {(short)(nbS1 % 10), (short)(nbS2 % 10), (short)(nbS2 / 10), (short)(nbS1 / 10) };
        for(int i = 0; i < BYTELENGTH / 2; i++) {
            if(P4[i] == bitsGauche[i])
                sortie[i] = 0;
            else
                sortie[i] = 1;

            sortie[i + BYTELENGTH / 2] = bitsDroite[i];
        }
    }


    // Décrypte un short passé dans entree et renvoie le décryptage dans sortie
    public void decryptageSDES(short[] entree, short[] sortie){
        for(int i = 0; i < BYTELENGTH; i++)
            if(entree[i] != 0 && entree[i] != 1)
                entree[i] = 0;

        short[] temp = new short[BYTELENGTH];

        makeIP(entree);
        fk_1(entree, temp, K2);
        entree = temp;

        short temp3;
        for(int i = 0; i < BYTELENGTH / 2; i++){
            temp3 = entree[i + BYTELENGTH / 2];
            entree[i + BYTELENGTH / 2] = entree[i];
            entree[i] = temp3;
        }

        fk_1(entree, sortie, K1);
        makeIP_1(sortie);
    }


    // Convertit un short passé en paramètre en int
    public static int shortToInt(short[] entree){
        int bin = 0;
        for(int i = 0; i < 8; i++){
            bin += entree[i] * Math.pow(10, 7 - i);
        }
        return conversionDecimal(bin);
    }


    // Convertit un char passé en paramètre en short
    public static void charToShort(char ch, short[] sortie){
        String str = Integer.toBinaryString(ch);
        for(int i = 0; i < 8; i++) {
            sortie[i] = (short) (String.format("%8s", str).charAt(i) - 48); //renvoie l'ascii donc -48
        }

    }


    // Convertit un short passé en paramètre en char
    public static char shortToChar(short[] entree){
        int val = 0;
        for(int i = 0; i < 8; i++){
            val += entree[i] * (int)Math.pow(10, 7-i);
        }
        return (char) (conversionDecimal(val));
    }
}