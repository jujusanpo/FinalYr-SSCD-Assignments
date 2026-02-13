package Lab1; 
import java.io.*; 
import java.util.*; 
public class assign4 { 
   static class ALAEntry { 
       String index; 
       String formalArg; 
       String actualArg; 
       public ALAEntry(String index, String formalArg, String actualArg) { 
           this.index = index; 
           this.formalArg = formalArg; 
           this.actualArg = actualArg; 
       } 
   } 
   static class MacroDeﬁnition { 
       String name; 
       List<String> formalParams; 
       List<String> bodyLines; 
       MacroDeﬁnition(String name, List<String> formalParams, List<String> bodyLines) { 
           this.name = name; 
           this.formalParams = formalParams; 
           this.bodyLines = bodyLines; 
       } 
   } 
   static Map<String, Integer> mntMap = new HashMap<>(); 
   static Map<Integer, String> mdtMap = new HashMap<>(); 
   static Map<String, List<String>> formalArgsMap = new HashMap<>(); 


   static Map<String, MacroDeﬁnition> macroDefs = new HashMap<>(); 
   public static void main(String[] args) { 
       String mntFile = "MNT.txt"; 
       String mdtFile = "MDT.txt"; 
       String alaFile = "ALA.txt"; 
       String opFile = "OP.txt"; 
       String ﬁnalFile = "ExpandedCode.txt"; 
       loadMNT(mntFile); 
       loadMDT(mdtFile); 
       loadALA(alaFile); 
       buildMacroDeﬁnitions(); 
       printMNT(); 
       printMDT(); 
       expandProgram(opFile, ﬁnalFile); 
       System.out.println("\nPass 2 Completed"); 
       System.out.println("Expanded code saved in: " + ﬁnalFile); 
   } 
   private static void loadMNT(String ﬁle) { 
       try (BufferedReader br = new BufferedReader(new FileReader(ﬁle))) { 
           br.readLine(); // skip header 
           String line; 
           while ((line = br.readLine()) != null) { 
               String[] p = line.split(","); 
               if (p.length < 3) continue; 
               String name = p[1].trim(); 
               int start = Integer.parseInt(p[2].trim()); 
               mntMap.put(name, start); 
           } 
       } catch (IOException e) { System.err.println("Error reading MNT: " + e.getMessage()); } 
   } 
   private static void loadMDT(String ﬁle) { 
       try (BufferedReader br = new BufferedReader(new FileReader(ﬁle))) { 
           br.readLine(); // skip header 
           String line; 
           while ((line = br.readLine()) != null) { 
               String[] p = line.split(",", 2); 
               if (p.length < 2) continue; 
               int idx = Integer.parseInt(p[0].trim()); 
               String instr = p[1].trim(); 
               mdtMap.put(idx, instr); 
           } 
       } catch (IOException e) { System.err.println("Error reading MDT: " + e.getMessage()); } 
   } 
   private static void loadALA(String ﬁle) { 
       try (BufferedReader br = new BufferedReader(new FileReader(ﬁle))) { 
           br.readLine(); // skip header 
           String line; 
           while ((line = br.readLine()) != null) { 


               String[] p = line.split(","); 
               if (p.length < 3) continue; 
               String idx = p[0].trim(); 
               String formal = p[1].trim(); 
               String macro = p[2].trim(); 
               formalArgsMap.computeIfAbsent(macro, k -> new ArrayList<>()).add(formal); 
           } 
       } catch (IOException e) { System.err.println("Error reading ALA: " + e.getMessage()); } 
   } 
   private static void buildMacroDeﬁnitions() { 
       for (String macro : mntMap.keySet()) { 
           int start = mntMap.get(macro); 
           List<String> formalParams = formalArgsMap.getOrDefault(macro, new 
ArrayList<>()); 
           List<String> body = new ArrayList<>(); 
           int i = start + 1; 
           while (mdtMap.containsKey(i)) { 
               String instr = mdtMap.get(i); 
               if (instr.equals("MEND")) break; 
               body.add(instr); 
               i++; 
           } 
           macroDefs.put(macro, new MacroDeﬁnition(macro, formalParams, body)); 
       } 
   } 
   private static void expandProgram(String opFile, String ﬁnalFile) { 
       List<String> expandedProgram = new ArrayList<>(); 
       List<ALAEntry> allDynamicALAs = new ArrayList<>(); 
       try (BufferedReader br = new BufferedReader(new FileReader(opFile))) { 
           String line; 
           while ((line = br.readLine()) != null) { 
               String[] parts = line.split("\\s+", 2); 
               String ﬁrst = parts[0]; 
               if (macroDefs.containsKey(ﬁrst)) { 
                   MacroDeﬁnition def = macroDefs.get(ﬁrst); 
                   List<String> actualArgs = new ArrayList<>(); 
                   if (parts.length > 1) { 
                       for (String a : parts[1].split(",")) actualArgs.add(a.trim()); 
                   } 
                   Map<String, String> argMap = new HashMap<>(); 
                   for (int i = 0; i < def.formalParams.size(); i++) { 
                       String formal = def.formalParams.get(i); 
                       String actual = (i < actualArgs.size()) ? actualArgs.get(i) : ""; 
                       String idx = "#" + (i + 1); 
                       argMap.put(idx, actual); 
                       allDynamicALAs.add(new ALAEntry(idx, formal, actual)); 
                   } 
                   for (String bodyLine : def.bodyLines) { 


                       String expanded = bodyLine; 
                       for (Map.Entry<String, String> e : argMap.entrySet()) { 
                           expanded = expanded.replace(e.getKey(), e.getValue()); 
                       } 
                       expandedProgram.add(expanded); 
                   } 
               } else { 
                   expandedProgram.add(line); 
               } 
           } 
           // Print ALA 
           System.out.println("\n===== ALA ====="); 
           System.out.printf("+---------+---------------+---------------+\n"); 
           System.out.printf("| %-7s | %-13s | %-13s |\n", "Index", "FormalArg", "ActualArg"); 
           System.out.printf("+---------+---------------+---------------+\n"); 
           for (ALAEntry a : allDynamicALAs) { 
               System.out.printf("| %-7s | %-13s | %-13s |\n", a.index, a.formalArg, a.actualArg); 
           } 
           System.out.printf("+---------+---------------+---------------+\n"); 
           // Print ﬁnal expanded program 
           System.out.println("\n===== Final Expanded Program ====="); 
           for (String s : expandedProgram) System.out.println(s); 
           // Save expanded code to ﬁle 
           try (PrintWriter pw = new PrintWriter(new FileWriter(ﬁnalFile))) { 
               for (String s : expandedProgram) pw.println(s); 
           } 
       } catch (IOException e) { 
           System.err.println("Error expanding OP: " + e.getMessage()); 
       } 
   } 
   private static void printMNT() { 
       System.out.println("\n===== MNT (Macro Name Table) ====="); 
       System.out.printf("+-------+-----------+-----------+\n"); 
       System.out.printf("| %-5s | %-9s | %-9s |\n", "Index", "Macro", "MDT Start"); 
       System.out.printf("+-------+-----------+-----------+\n"); 
       int idx = 1; 
       for (Map.Entry<String, Integer> e : mntMap.entrySet()) { 
           System.out.printf("| %-5d | %-9s | %-9d |\n", idx++, e.getKey(), e.getValue()); 
       } 
       System.out.printf("+-------+-----------+-----------+\n"); 
   } 
   private static void printMDT() { 
       System.out.println("\n===== MDT (Macro Deﬁnition Table) ====="); 
       System.out.printf("+-------+-----------------------------+\n"); 
       System.out.printf("| %-5s | %-27s |\n", "Index", "Instruction"); 
       System.out.printf("+-------+-----------------------------+\n"); 
       for (Map.Entry<Integer, String> e : mdtMap.entrySet()) { 
           System.out.printf("| %-5d | %-27s |\n", e.getKey(), e.getValue()); 


       } 
       System.out.printf("+-------+-----------------------------+\n"); 
   } 
} 
