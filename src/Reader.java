import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reader {
    public static List<Object> read(String filename) {
        List<Object> retour = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            try {
                int nbC = Integer.parseInt(br.readLine());
                retour.add(nbC);

                int nbZ = Integer.parseInt(br.readLine());
                retour.add(nbZ);

                String[] vies = br.readLine().split(" ");
                int[] vie = new int[nbC];
                for (int i=0; i<nbC; i++) {
                    vie[i] = Integer.parseInt(vies[i]);
                }
                retour.add(vie);

                for (int i=0; i<nbC; i++) {
                    String[] zones = br.readLine().split(" ");
                    int[] z = new int[zones.length];
                    for (int j=0; j<zones.length; j++) {
                        z[j] = Integer.parseInt(zones[j]);
                    }
                    retour.add(z);
                }

                br.close();
            } catch (IOException e) {
                System.out.println("Le fichier n'utilise pas le bon format de données");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Le fichier spécifié n'existe pas ou n'a pas été trouvé.");
        }
        return retour;
    }
}