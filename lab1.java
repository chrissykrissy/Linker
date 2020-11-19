import java.io.*;
import java.util.*;

public class lab1 {
    static class Module {
        int numbers;
//        int base;
        HashMap<String, ArrayList<Integer>> useList;
        List<Integer> memoryMap;

        public Module (int number, HashMap<String, ArrayList<Integer>> useList, ArrayList<Integer> memoryMap){
            this.numbers = number;
//            this.base = base;
            this.useList = useList;
            this.memoryMap = memoryMap;
        }
    }

    public static String getKeyMine(Map<String, ArrayList<Integer>> useList, int value){
        for (Map.Entry<String,ArrayList<Integer>> entry : useList.entrySet()){
            String key = entry.getKey();
            ArrayList<Integer> values = entry.getValue();
            if (values.contains(value)){
                return entry.getKey();
            }
        }return null;
    }


    public static ArrayList<String> inputList;
    public static HashMap<String,Integer> symbolMap;
    public static ArrayList<String> usedSymbol;
    public static HashMap<String, ArrayList<Integer>> useMap;
    public static ArrayList<Module> modules;
    public static int totalMem = 0;
    public static int[] base;
    public static HashMap<String, String> symbolError;
    public static HashMap<Integer, String> memError;
    public static HashMap<String, String> warning;
    public static HashMap<String, Integer> multipleUsage;


    public static void main(String[] args) throws IOException {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(reader);

//        Scanner input = new Scanner(System.in);

        inputList = new ArrayList<>();
        symbolMap = new LinkedHashMap<>();
        modules = new ArrayList<>();

        StringBuilder SB = new StringBuilder();
        String input;

        // Important stuff
        int currentBase = 0;

        // Input Parsing
//        while (input.hasNext()){
//            SB.append(input.next().trim() + " ");
//        }

        while (((input = in.readLine()) != null)){
//            SB.append(input.trim()+" ");
            if (input.trim().length() > 0) {
                SB.append(input.trim()+" ");
//                System.out.println("test: " + SB.toString());
            }
            if (!in.ready()){
                break;
            }
        }
//        if (in.ready()){
//            SB.append(input.trim()+" ");
//        }
        reader.close();
        in.close();


        String[] inputArray = SB.toString().trim().replaceAll(" +"," ").split(" ");
        inputList.addAll(Arrays.asList(inputArray));

        for (int i = 0; i < inputList.size(); i++) {
            if (inputList.get(i).equals("")) {
                inputList.remove(inputList.get(i));
            }
        }

        // Module Creation
        Iterator<String> iter = inputList.iterator();
        int numModules = Integer.parseInt(iter.next());
        int baseSize = numModules;
        base = new int[baseSize];

        symbolError = new HashMap<>();
        multipleUsage = new HashMap<>();
        usedSymbol = new ArrayList<>();

        //pass one
        while (numModules > 0){
            Module m = new Module(numModules, new HashMap<String, ArrayList<Integer>>(), new ArrayList<Integer>());
            ArrayList<Integer> uses;
            useMap = new HashMap<>();

            int numSymb = Integer.parseInt(iter.next());
            for (int i = 0; i < numSymb; i++){
                String nextOne = iter.next();
                int nextSym = Integer.parseInt(iter.next());

                if (symbolMap.containsKey(nextOne)){
                    symbolMap.remove(nextOne);
                    symbolError.put(nextOne,"Error: This variable is multiply defined; last value used.");
                    symbolMap.put(nextOne, symbolMap.getOrDefault(symbolMap.get(nextOne), nextSym + currentBase));
                }else{
                    symbolMap.put(nextOne, symbolMap.getOrDefault(symbolMap.get(nextOne), nextSym + currentBase));
                }
            }

//            boolean multipleUsage = false;
            int numUses = Integer.parseInt(iter.next());
            while (numUses > 0){
                uses = new ArrayList<>();

                String useSymbol = iter.next();
                if (!usedSymbol.contains(useSymbol)){
                    usedSymbol.add(useSymbol);
                }
                while (iter.hasNext()){
                    int num = Integer.parseInt(iter.next());
                    if (num == -1){
                        numUses--;
                        break;
                    }
                    else{
                        for (Map.Entry entry : useMap.entrySet()){
                            if (useMap.get(entry.getKey()).contains(num)) {
                                useMap.get(entry.getKey()).remove(num);
                                multipleUsage.put(useSymbol,num);
                            }
                        }
                        uses.add(num);
                    }
                    useMap.put(useSymbol, uses);
                }
            }
//            System.out.println(useMap);

//            System.out.println(multipleUsage == null ? "empty" : "not empty");


            int memReferences = Integer.parseInt(iter.next());
//            m.base = memReferences;
            totalMem += memReferences;
            for (int i = 0; i < memReferences; i++){
                m.memoryMap.add(Integer.parseInt(iter.next()));
            }
            base[baseSize-numModules] = currentBase;
            currentBase += memReferences;

            m.useList = useMap;
            modules.add(m);

            for (String entry: symbolMap.keySet()) {
                if (symbolMap.get(entry) > totalMem-1) {
                    symbolMap.put(entry, totalMem - 1);
                    symbolError.put(entry, "Error: Definition exceeds module size; last word in module used.");
                }
            }

            numModules--;

        }

//        System.out.println(multipleUsage);

        warning = new LinkedHashMap<>();
        int defmodule = 0;
//        for (Module m : modules) {
            for (String s : symbolMap.keySet()) {
                if (!usedSymbol.contains(s)) {
                    for (int k = 1; k < base.length; k++) {
//                        if(k+1 > base.length){
//                            if (symbolMap.get(s) >= base[k]){
//                                defmodule = k;
//                                break;
//                            }
//                        }
//                        else if (!(k+1 > base.length)) {
                        if (symbolMap.get(s) >= base[k-1] && symbolMap.get(s) < base[k]) {
                            defmodule = k-1;
                            break;
                        }else if (symbolMap.get(s) >= base[base.length-1]){
                            defmodule = base.length-1;
                            break;
                        }
                    }warning.put(s, "Warning: " + s + " was defined in module " + defmodule + " but never used.");
                }
            }

//        for (String s : symbolMap.keySet()){
//            if (usedSymbol.contains(s){
//                for (int k = 0; k < base.length; k++){
//                    if (symbolMap.get(s) <= base[k]){
//                        defmodule = k;
//                        break;
//                    }
//                }
//                warning.put(s,"Warning: "+s+" was defined in module "+defmodule+" but never used.");
//            }
//        }

//        System.out.println(totalMem);
//        for (String entry: symbolMap.keySet()) {
//            if (symbolMap.get(entry) > totalMem-1) {
//                symbolMap.put(entry, totalMem - 1);
//                symbolError.put(entry, "Error: Definition exceeds module size; last word in module used.");
//            }
//        }

        System.out.println("Symbol Table");
        for (String entry: symbolMap.keySet()){
            String key = entry.toString();
            String value = symbolMap.get(entry).toString();
            System.out.println(symbolError.keySet().contains(key) ? key+"="+value + " "+symbolError.get(key) : key+"="+value);
        }

        memError = new HashMap<>();
        //pass two
        System.out.println();
        System.out.println("Memory Map");
        HashMap<Integer, String> printMemMap = new HashMap<>();
        for (int i = 0; i < modules.size(); i++){
            for (int j = base[i]; j < modules.get(i).memoryMap.size()+base[i]; j++){
                String address = String.valueOf(modules.get(i).memoryMap.get(j-base[i]));
                if (address.substring(address.length()-1).equals("1")){
                    String newAddress = address.substring(0,address.length()-1);
                    printMemMap.put(i,newAddress);
                }
                if (address.substring(address.length()-1).equals("2")){
                    String newAddress = address.substring(0,address.length()-1);
//                    System.out.println(newAddress.substring(1,newAddress.length()));
                    if (Integer.parseInt(newAddress.substring(1)) > 299){
                        String modifiedAddress = String.valueOf(Integer.parseInt(newAddress)-Integer.parseInt(newAddress.substring(1))+299);
//                        System.out.println(modifiedAddress);
                        printMemMap.put(i,modifiedAddress);
                        memError.put(j,"Error: Absolute address exceeds machine size; largest legal value used.");
                    }else{
                        printMemMap.put(i,newAddress);
                    }
                }if (address.substring(address.length()-1).equals("3")){
                    String newAddress = address.substring(0, address.length()-1);
                    printMemMap.put(i,String.valueOf(Integer.parseInt(newAddress)+base[i]));
                }if (address.substring(address.length()-1).equals("4")){
                    String newAddress = String.valueOf(Integer.parseInt(address.substring(0,address.length()-1))-Integer.parseInt(address.substring(1, address.length()-1)));
//                    System.out.println(getKey(modules.get(i).useList,j-base[i]));
                    if (symbolMap.get(getKeyMine(modules.get(i).useList,j-base[i])) == null){
                        memError.put(j, "Error: "+ getKeyMine(modules.get(i).useList,j-base[i])+" is not defined; 111 used.");
                        printMemMap.put(i,String.valueOf(Integer.parseInt(newAddress)+111));
                    }else if (multipleUsage.containsKey(getKeyMine(modules.get(i).useList,j-base[i]))){
                        if (multipleUsage.get(getKeyMine(modules.get(i).useList,j-base[i])) == j-base[i]){
                            memError.put(j,"Error: Multiple variables used in instruction; all but last ignored.");
                            printMemMap.put(i,String.valueOf(Integer.parseInt(newAddress)+symbolMap.get(getKeyMine(modules.get(i).useList,j-base[i]))));
                        }
                    }else{
                        printMemMap.put(i,String.valueOf(Integer.parseInt(newAddress)+symbolMap.get(getKeyMine(modules.get(i).useList,j-base[i]))));
                    }
                }
                System.out.println(!memError.keySet().contains(j) ? j+": "+printMemMap.get(i) : j+": "+printMemMap.get(i)+ " "+memError.get(j));
            }
        }

        if (!warning.isEmpty()){
            System.out.println();
            for (String entry: warning.keySet()){
                System.out.println(warning.get(entry));
            }
        }
    }
}


