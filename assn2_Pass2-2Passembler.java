package Lab1; 
import java.io.*; 
import java.util.*; 

public class assign2 { 
    public static void main(String[] args) { 
        try { 
            // Input ﬁles 
            String icFile = "IC.txt"; 
            String symtabFile = "symtab.txt"; 

            // Read ﬁles 
            List<String> icLines = readFile(icFile); 
            Map<Integer, Integer> symtab = readSymtab(symtabFile); 

            // Generate machine code 
            List<String> machineCode = generateMachineCode(icLines, symtab); 

            // Print machine code 
            System.out.println("\nMachine Code:"); 
            for (String line : machineCode) { 
                System.out.println(line); 
            } 

            // Save to ﬁle 
            saveToFile(machineCode, "machine_code.txt"); 

        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 

    // Reads ic.txt   
    private static List<String> readFile(String ﬁleName) throws IOException { 
        List<String> lines = new ArrayList<>(); 


        try (BufferedReader reader = new BufferedReader(new 
FileReader(ﬁleName))) { 
            String line; 
            while ((line = reader.readLine()) != null) { 
                line = line.trim(); 
                if (!line.isEmpty()) { 
                    lines.add(line); 
                } 
            } 
        } 
        return lines; 
    } 

    // Reads symtab and store into a map 
    private static Map<Integer, Integer> readSymtab(String ﬁleName) throws 
IOException { 
        Map<Integer, Integer> symtab = new HashMap<>(); 
        try (BufferedReader reader = new BufferedReader(new 
FileReader(ﬁleName))) { 
            String line; 
            boolean headerSkipped = false; 
            while ((line = reader.readLine()) != null) { 
                line = line.trim(); 
                if (line.isEmpty()) continue; 
                // Skip header line 
                if (!headerSkipped) { 
                    headerSkipped = true; 
                    continue; 
                } 
                String[] parts = line.split("\\s+"); 
                if (parts.length == 3 && !parts[2].equalsIgnoreCase("NULL")) { 
                    int id = Integer.parseInt(parts[0]); 
                    int addr = Integer.parseInt(parts[2]); 
                    symtab.put(id, addr); 
                } 
            } 
        } 
        return symtab; 
    } 
    // Generate machine code from IC & Symtab 
    private static List<String> generateMachineCode(List<String> icLines, 
Map<Integer, Integer> symtab) { 
        List<String> machineCode = new ArrayList<>(); 
        boolean headerSkipped = false; 

        for (String line : icLines) { 
            if (!headerSkipped) { // Skip header "LC OPCODE OP1 OP2" 
                headerSkipped = true; 
                continue; 
            } 

            String[] tokens = line.split("\\s+"); 
            if (tokens.length < 4) continue; 

            String lc = tokens[0].equals("-") ? "" : tokens[0]; 
            String opcode = tokens[1]; 
            String op1 = tokens[2].equals("-") ? "0" : tokens[2]; 
            String op2 = tokens[3].equals("-") ? "" : tokens[3]; 

            //AD (Assembler Directives) 
            if (opcode.startsWith("AD")) { 
                continue; 
            } 

            // DL statements (DC/DS) 
            if (opcode.startsWith("DL")) { 
                if (op2.startsWith("C-")) { 
                    machineCode.add(lc + "\t" + "00" + "\t0\t" + op2.substring(2)); 
                } 
                continue; 
            } 

            // IS statements 
            if (opcode.startsWith("IS")) { 
                String mcOp = resolveOpcode(opcode); 
                String mcOp1 = op1.equals("-") ? "0" : op1; 
                String mcOp2 = resolveOperand(op2, symtab);   
                machineCode.add(lc + "\t" + mcOp + "\t" + mcOp1 + "\t" + mcOp2); 
            } 
        } 

 

        return machineCode; 
    } 
    
    private static String resolveOpcode(String opcode) { 
        return opcode.contains("-") ? opcode.split("-")[1] : opcode; 
    } 

    private static String resolveOperand(String operand, Map<Integer, Integer> 
symtab) { 
        if (operand == null || operand.equals("-")) return "0"; 

 

        if (operand.startsWith("S-")) { 
            int id = Integer.parseInt(operand.split("-")[1]); 
            return String.valueOf(symtab.getOrDefault(id, 0)); 
        } 
        if (operand.startsWith("C-")) { 
            return operand.substring(2); 
        } 
        return operand; 
    } 

    // Save machine code to ﬁle 
    private static void saveToFile(List<String> lines, String ﬁlename) { 
        try (PrintWriter writer = new PrintWriter(new FileWriter(ﬁlename))) { 
            writer.println("LC\tOP\tR\tMEM"); 
            for (String line : lines) { 
                writer.println(line); 
            } 
            System.out.println("\nMachine Code saved to " + ﬁlename); 
        } catch (IOException e) { 
            System.out.println("Error saving machine code: " + e.getMessage()); 
        } 
    } 
} 
