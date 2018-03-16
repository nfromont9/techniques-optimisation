import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Heuristique {
    private static Scanner input = new Scanner(System.in);

    private static int nbC=0, nbZ=0;
    private static int[] dureeVie;
    private static List<int[]> zones = new ArrayList<>();

    private static void format(String file) {
        List<Object> ans = Reader.read(file);
        nbC = (int) ans.get(0);
        nbZ = (int) ans.get(1);
        dureeVie = (int[]) ans.get(2);
        for (int i=0; i<nbC; i++) {
            System.out.println(Arrays.toString((int[]) ans.get(i+3)));
            zones.add((int[]) ans.get(i+3));
        }
    }

    private static void getArgs(String[] args) {
        String file = "";
        if (args.length!=1) {
            System.out.println("Nom du fichier à lire : ");
            file = input.next();
        } else {
            file = args[0];
        }

        format(file);
    }

    private static void print() {
        System.out.println("Nombre de capteurs : "+nbC);
        System.out.println("Nombre de zones : "+nbZ);
        System.out.println("Durée de vie de chaque capteur : "+ Arrays.toString(dureeVie));
        System.out.println("Zones couvertes par chaque capteur : ");
        for (int i=0; i<zones.size(); i++)
            System.out.println("    "+i+" : "+ Arrays.toString(zones.get(i)));
    }

    public static void main(String[] args) {
        getArgs(args);
        print();

    }
}
