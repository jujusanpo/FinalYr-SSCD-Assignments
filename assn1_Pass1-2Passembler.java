package Lab1; 
import java.io.*; 
import java.util.*; 
public class assign1 { 
   static ﬁnal Map<String, String[]> optab = new HashMap<>() {{ 
       put("DC", new String[]{"01", "DL"}); 
       put("DS", new String[]{"02", "DL"}); 
       put("START", new String[]{"01", "AD"}); 
       put("ORIGIN", new String[]{"02", "AD"}); 
       put("EQU", new String[]{"03", "AD"}); 
       put("LTORG", new String[]{"04", "AD"}); 
       put("END", new String[]{"05", "AD"}); 
       put("STOP", new String[]{"00", "IS"}); 
       put("ADD", new String[]{"01", "IS"}); 
       put("SUB", new String[]{"02", "IS"}); 
       put("MULT", new String[]{"03", "IS"}); 
       put("DIV", new String[]{"04", "IS"}); 
       put("MOVER", new String[]{"05", "IS"}); 
       put("MOVEM", new String[]{"06", "IS"}); 
       put("JMP", new String[]{"07", "IS"}); 
       put("BC", new String[]{"08", "IS"}); 
       put("READ", new String[]{"09", "IS"}); 
       put("PRINT", new String[]{"10", "IS"}); 
   }}; 
   static ﬁnal Map<String, String> regtable = new HashMap<>() {{ 
       put("AREG", "01"); 
       put("BREG", "02"); 
       put("CREG", "03"); 
       put("DREG", "04"); 
   }}; 
   static ﬁnal Map<String, Integer> symtab = new LinkedHashMap<>(); 
   static int LC = 0; 
   public static void main(String[] args) { 
       List<String[]> output = new ArrayList<>(); 
       try (Scanner ﬁleScanner = new Scanner(new File("input.asm"))) { 
           while (ﬁleScanner.hasNextLine()) { 
               String line = ﬁleScanner.nextLine(); 
               if (!line.trim().isEmpty()) { 
                   processLine(line, output); 
               } 
           } 
       } catch (FileNotFoundException e) { 
           System.out.println("Error: input.asm not found."); 


           return; 
       } 
       // Display on console 
       printOutput(output); 
       printSymbolTable(); 
       // Save to IC.txt 
       saveToFile(output, "IC.txt"); 
   } 
   private static void processLine(String line, List<String[]> output) { 
       Scanner sc = new Scanner(line); 
       String label = null, instruction = null, op1 = null, op2 = null; 
       int tokenCount = 0; 
       while (sc.hasNext()) { 
           String token = sc.next(); 
           tokenCount++; 
           if (tokenCount == 1 && !optab.containsKey(token)) { 
               label = token; 
               symtab.put(label, LC); 
           } else if (optab.containsKey(token)) { 
               instruction = token; 
           } else if (regtable.containsKey(token)) { 
               op1 = token; 
           } else { 
               op2 = token; 
               if (!op2.matches("\\d+") && !op2.startsWith("='")) { 
                   symtab.putIfAbsent(op2, -1); 
               } 
           } 
       } 
       String[] instr = optab.getOrDefault(instruction, new String[]{"00", "IS"}); 
       String opCode = instr[1] + "-" + instr[0]; 
       String op2Resolved = resolveOperand(op2); 
       output.add(new String[]{ 
               instruction != null && instr[1].equals("AD") ? "-" : String.valueOf(LC), 
               opCode, 
               op1 != null ? regtable.getOrDefault(op1, "-") : "-", 
               op2Resolved 
       }); 
       if ("START".equals(instruction) || "ORIGIN".equals(instruction)) { 
           LC = Integer.parseInt(op2); 
       } else if ("DS".equals(instruction)) { 
           LC += Integer.parseInt(op2); 
       } else if (!"END".equals(instruction)) { 
           LC++; 


       } 
       sc.close(); 
   } 
   private static String resolveOperand(String op) { 
       if (op == null) return "-"; 
       if (op.matches("\\d+")) return "C-" + op; 
       if (op.startsWith("='")) return "L-" + op.substring(2, op.length() - 1); 
       if (symtab.containsKey(op)) { 
           if (symtab.get(op) == -1) symtab.put(op, LC); 
           return "S-" + (new ArrayList<>(symtab.keySet()).indexOf(op) + 1); 
       } 
       return "-"; 
   } 
   private static void printOutput(List<String[]> output) { 
       System.out.println("\nIntermediate Code:"); 
       System.out.println("LC\tOPCODE\tOP1\tOP2"); 
       for (String[] line : output) { 
           System.out.printf("%s\t%s\t%s\t%s\n", (Object[]) line); 
       } 
   } 
   private static void printSymbolTable() { 
       System.out.println("\nSymbol Table:"); 
       System.out.println("ID\tNAME\tADDR"); 
       int id = 1; 
       for (Map.Entry<String, Integer> entry : symtab.entrySet()) { 
           System.out.printf("%d\t%s\t%s\n", id++, entry.getKey(), entry.getValue() == -1 
? "NULL" : entry.getValue()); 
       } 
   } 
   // Save IC & Symbol Table to ﬁle 
   private static void saveToFile(List<String[]> output, String ﬁlename) { 
       try (PrintWriter writer = new PrintWriter(new FileWriter(ﬁlename))) { 
           writer.println("Intermediate Code:"); 
           writer.println("LC\tOPCODE\tOP1\tOP2"); 
           for (String[] line : output) { 
               writer.printf("%s\t%s\t%s\t%s\n", (Object[]) line); 
           } 
           writer.println("\nSymbol Table:"); 
           writer.println("ID\tNAME\tADDR"); 
           int id = 1; 
           for (Map.Entry<String, Integer> entry : symtab.entrySet()) { 
               writer.printf("%d\t%s\t%s\n", id++, entry.getKey(), entry.getValue() == -1 ? 
"NULL" : entry.getValue()); 
           } 


           System.out.println("\nOutput saved to " + ﬁlename); 
       } catch (IOException e) { 
           System.out.println("Error writing to ﬁle: " + e.getMessage()); 
       } 
   } 
} 
