import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Heuristique {
    private static Scanner input = new Scanner(System.in);

    private static String programName;
    private static int nbC=0, nbZ=0;
    private static int[] dureeVie;
    private static List<int[]> zones = new ArrayList<>();

    private static void format(String file) {
        List<Object> ans = Reader.read(file);
        programName = file;
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
        List<ArrayList<Integer>> combiElem = getCombiElem();
        System.out.println(combiElem);
        writeLpFile(combiElem);
    }

    private static void writeLpFile(List<ArrayList<Integer>> combiElem) {
        File lpFile = new File("lpSources/"+programName+".lp");
        lpFile.setWritable(true);
        try {
            PrintWriter writer = new PrintWriter(lpFile);
            StringBuilder sb = new StringBuilder();
            sb.append("maximize ");
            for (int i = 0; i < combiElem.size(); i++) {
                sb.append("t"+i);
                if (i!=combiElem.size()-1){
                    sb.append(" + ");
                }
            }
            writer.println(sb.toString());
            writer.println("subject to");
            for (int i = 0; i < nbC; i++) {
                sb = new StringBuilder();
                for (int j=0; j<combiElem.size(); j++){
                    ArrayList<Integer> combi = combiElem.get(j);
                    if (combi.contains(i)){
                        sb.append("t"+j+" + ");
                    }
                }
                if (sb.length()>2){
                    sb.deleteCharAt(sb.length()-1);
                    sb.deleteCharAt(sb.length()-1);
                    sb.append("<= "+dureeVie[i]);
                }
                writer.println(sb.toString());
            }
            writer.println("end");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Process p = Runtime.getRuntime().exec("glpsol --cpxlp lpSources/"+programName+".lp -o lpOutput/"+programName);
//            Reader reader = new FileReader("lpOutput/"+programName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<ArrayList<Integer>> getCombiElem(){
        //On recupere les capteurs par zone
        ArrayList<Integer> capteursParZone[] = getCapteurParZone();

        ArrayList<ArrayList<Integer>> allConfigs = new ArrayList<>();

        //nombre d'iteration limite
        for(int iter = 0; iter < nbC+nbZ; iter++){

            ArrayList<Integer> config = new ArrayList<>();
            //
            for(ArrayList<Integer> zone : capteursParZone){
                boolean toAdd = true;
                for(int capteur : zone){
                    //On verifie si la config contient deja un capteur de la zone
                    if(config.contains(capteur)){
                        toAdd = false;
                    }
                }
                if(toAdd){
                    //Si il n'y a pas de capteur de la zone
                    config.add(zone.get((int)(Math.random()*zone.size())));

                }
            }

            boolean ok = true;
            //On enleve les capteurs inutiles de la configuration
            config = elementarise(config, zones);

            //Si on a pas encore trouver cette combinaison on l'ajoute a la liste de retour
            for(ArrayList x : allConfigs){
                if(configEgale(config, x)) ok = false;
            }
            if(ok){
                allConfigs.add(config);
            }
        }
        return allConfigs;
    }

    private static boolean configEgale(ArrayList<Integer> config, ArrayList x) {
        if(config.size() != x.size()) return false;

        for(int i : config){
            if(!x.contains(i)){
                return false;
            }
        }
        return true;
    }


    //On regarde les zones couvertes de chaque capteur de la configuration et on les met dans une liste
    //Si une zone est couverte par un autre capteur de la configuration on l'enleve de la liste
    //Si au bout du compte cette liste est vide le capteur est inutile et enlever de la configuration
    private static ArrayList<Integer> elementarise(ArrayList<Integer> config, List<int[]> zones) {
        int idx = 0;
        while(idx < config.size()){
            ArrayList<Integer> current = new ArrayList<>();
            for(int i : zones.get(config.get(idx))) current.add(i);
            for(int capteur : config){
                if(capteur == config.get(idx)){
                    continue;
                }
                int i = 0;
                while(i < current.size()){
                    if(Arrays.binarySearch(zones.get(capteur), current.get(i)) >= 0){
                        current.remove(i);
                    }
                    else i++;
                }
            }
            if(current.size() == 0) config.remove(idx);
            else idx ++;
        }
        return config;
    }

    private static ArrayList<Integer>[] getCapteurParZone(){
        ArrayList<Integer> capteursParZone[] = new ArrayList[nbZ];

        for(int i = 0; i < capteursParZone.length; i++){
            capteursParZone[i] = new ArrayList<Integer>();
        }

        for(int[] zonesParCapteur : zones){
            for(int zone : zonesParCapteur){
                if(!capteursParZone[zone-1].contains(zones.indexOf(zonesParCapteur))){
                    capteursParZone[zone-1].add(zones.indexOf(zonesParCapteur));
                }
            }
        }
        return capteursParZone;
    }
}
