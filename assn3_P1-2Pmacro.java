package Lab1; 
import java.io.*; 
import java.util.*; 

//Macro-Name Table 
class MNT { 
    int index; 
    String macroName; 
    int mdtIndex; 

    MNT(int index, String macroName, int mdtIndex) { 
        this.index = index; 
        this.macroName = macroName; 
        this.mdtIndex = mdtIndex; 
    } 
} 

 

//Macro-Deﬁnition Table 
class MDT { 
    int index; 
    String instruction; 

    MDT(int index, String instruction) { 
        this.index = index; 
        this.instruction = instruction; 
    } 
} 

//Argument-List Array 
class ALA { 
    String argIndex; 
    String formalArg; 
    String actualArg; 
    String macroName;  

    ALA(String argIndex, String formalArg, String actualArg, String macroName) { 
        this.argIndex = argIndex; 
        this.formalArg = formalArg; 


        this.actualArg = actualArg; 
        this.macroName = macroName; 
    } 
}  

public class assign3 { 
    
    static List<MNT> mntTable = new ArrayList<>(); 
    static List<MDT> mdtTable = new ArrayList<>(); 
    static List<ALA> alaTable = new ArrayList<>(); 
    static List<String> outputLines = new ArrayList<>(); // For O/P ﬁle (source code without 
MACRO)  

    //File read 
    public static void main(String[] args) { 
        List<String> sourceLines = readFile("macro-input.txt"); 

        processMacros(sourceLines); //process the input ﬁle 
        printMNT(); 
        printALA(); 
        printMDT(); 
        printOutputFile(); 
    }  

    private static List<String> readFile(String ﬁleName) { 
        List<String> lines = new ArrayList<>(); 
        try (BufferedReader br = new BufferedReader(new FileReader(ﬁleName))) { 
            String line; 
            while ((line = br.readLine()) != null) { 
                if (!line.trim().isEmpty()) lines.add(line.trim()); 
            } 
        } catch (IOException e) { 
            System.out.println("Cannot read ﬁle " + ﬁleName); 
        } 
        return lines; 
    }  

    private static void processMacros(List<String> lines) { 


        int mdtIdx = 1; 
        int mntIdx = 1; 
        String macroName = null; 
        Map<String, String> argMap = new LinkedHashMap<>(); 
        boolean insideMacro = false;     
//        int mdlc = 0;  

        for (String line : lines) { 
            if (line.equalsIgnoreCase("MACRO")) {  //checking for macro deﬁnition 
                insideMacro = true; 
//                mdlc++; 
                macroName = null; 
                argMap.clear(); 
                continue; 
            }  

            if (insideMacro) { 
                if (macroName == null) { 
                    // First line after MACRO -> macro deﬁnition/name 
                    String[] parts = line.split("\\s+", 2); 
                    macroName = parts[0]; 

 

                    // Add to mnt macroname 
                    mntTable.add(new MNT(mntIdx++, macroName, mdtIdx)); 

 

                    // Add arguments to ALA 
                    if (parts.length > 1) addArguments(parts[1], macroName, argMap);  

                    // Add line to MDT 
                    mdtTable.add(new MDT(mdtIdx++, line)); 
                } else if (line.equalsIgnoreCase("MEND")) { 
                    mdtTable.add(new MDT(mdtIdx++, "MEND")); 
                    insideMacro = false; // End of macro 
                } else { 
                    // Replace args with indices in ala and store to mdt 
                    addToMDT(line, argMap, mdtIdx++); 
                } 
            } else { 
                // Outside macro----> goes to output (OP ﬁle) 


                outputLines.add(line); 
            } 
        } 
    }  

    //method to add arguments in ala and create argMap 
    private static void addArguments(String argsLine, String macroName, Map<String, 
String> argMap) { 
        String[] args = argsLine.split(","); 
        for (int i = 0; i < args.length; i++) { 
            String arg = args[i].trim(); 
            if (!arg.isEmpty()) { 
                String idx = "#" + (i + 1); 
                argMap.put(arg, idx); 
                alaTable.add(new ALA(idx, arg, "-", macroName)); 
            } 
        } 
    } 

    //replacing formal arguments 
    private static void addToMDT(String line, Map<String, String> argMap, int mdtIdx) { 
        String newLine = line; 
        for (Map.Entry<String, String> entry : argMap.entrySet()) { 
            newLine = newLine.replace(entry.getKey(), entry.getValue()); 
        } 
        mdtTable.add(new MDT(mdtIdx, newLine)); 
    } 

    private static void printMNT() { 
        System.out.println("\n===== MNT (Macro Name Table) ====="); 
        System.out.printf("+-----------+------------+-----------+\n"); 
        System.out.printf("| %-9s | %-10s | %-9s |\n", "Index", "Macro", "MDT Start"); 
        System.out.printf("+-----------+------------+-----------+\n"); 
        for (MNT m : mntTable) { 
            System.out.printf("| %-9d | %-10s | %-9d |\n", m.index, m.macroName, m.mdtIndex); 
        } 
        System.out.printf("+-----------+------------+-----------+\n"); 
    } 

    private static void printALA() { 
        System.out.println("\n===== ALA (Argument List Array) ====="); 
        System.out.printf("+---------+-----------------+-----------------+------------+\n"); 
        System.out.printf("| %-7s | %-15s | %-15s | %-10s |\n", "Index", "Formal Arg", "Actual 
Arg", "Macro"); 
        System.out.printf("+---------+-----------------+-----------------+------------+\n"); 
        for (ALA a : alaTable) { 
            System.out.printf("| %-7s | %-15s | %-15s | %-10s |\n", 
                    a.argIndex, a.formalArg, a.actualArg, a.macroName); 
        } 
        System.out.printf("+---------+-----------------+-----------------+------------+\n"); 
    } 


    private static void printMDT() { 
        System.out.println("\n===== MDT (Macro Deﬁnition Table) ====="); 
        System.out.printf("+-----------+-------------------------------+\n"); 
        System.out.printf("| %-9s | %-29s |\n", "Index", "Instruction"); 
        System.out.printf("+-----------+-------------------------------+\n"); 
        for (MDT m : mdtTable) { 
            System.out.printf("| %-9d | %-29s |\n", m.index, m.instruction); 
        } 
        System.out.printf("+-----------+-------------------------------+\n"); 
    } 

    private static void printOutputFile() { 
        System.out.println("\n===== OUTPUT (OP File) ====="); 
        for (String line : outputLines) { 
            System.out.println(line); 
        } 
    } 
} 
