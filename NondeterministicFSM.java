import java.io.*;
import java.util.*;

public class NondeterministicFSM {
    private Set<Integer> finalStates;
    private Map<Integer, Map<Character, Set<Integer>>> transitionFunction;
    private Set<Character> inputSymbols;

    public NondeterministicFSM(String filePath) throws IOException {
        transitionFunction = new HashMap<>();
        finalStates = new HashSet<>();
        inputSymbols = new HashSet<>();
        parseFile(filePath);
    }

    private void parseFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();

        // First line: read input symbols
        for (String symbol : line.split(",")) {
            inputSymbols.add(symbol.trim().charAt(0));
        }
        System.out.println("Input symbols: " + inputSymbols);

        int state = 0;
        while ((line = reader.readLine()) != null) {
            if (line.contains("-")) {
                finalStates.add(state);
                line = line.replace("-", "").trim();
            }

            System.out.println("Parsing state " + state + ": " + line);

            String[] transitions = line.split(",");
            Map<Character, Set<Integer>> stateTransitions = new HashMap<>();

            for (int i = 0; i < transitions.length; i++) {
                Set<Integer> targetStates = new HashSet<>();
                String transition = transitions[i].trim();

                if (!transition.isEmpty() && !transition.equals("{}")) {
                    transition = transition.replaceAll("[{}]", "");
                    for (String target : transition.split("\\s+")) {
                        try {
                            targetStates.add(Integer.parseInt(target));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid state number: " + target);
                        }
                    }
                }

                // Map input symbol to transitions; for simplicity, we assume input is '0' or '1'
                char inputSymbol = (char) ('0' + i);
                stateTransitions.put(inputSymbol, targetStates);
                System.out.println("  On input '" + inputSymbol + "' -> " + targetStates);
            }

            transitionFunction.put(state, stateTransitions);
            state++;
        }
        reader.close();

        System.out.println("Final states: " + finalStates);
        System.out.println("Transition function: " + transitionFunction);
    }

    private Set<Integer> extendedTransition(Set<Integer> currentStates, String input, int index) {
        if (index == input.length()) {
            return currentStates; // End of input, return current states
        }

        Set<Integer> nextStates = new HashSet<>();
        char symbol = input.charAt(index);
        System.out.println("Processing input '" + symbol + "' at index " + index);

        for (int state : currentStates) {
            Set<Integer> transitions = transitionFunction.getOrDefault(state, new HashMap<>()).get(symbol);
            if (transitions != null) {
                nextStates.addAll(transitions);
                System.out.println("  State " + state + " on '" + symbol + "' -> " + transitions);
            } else {
                System.out.println("  State " + state + " has no transition on '" + symbol + "'");
            }
        }

        return extendedTransition(nextStates, input, index + 1); // Recurse for the next symbol
    }

    public boolean isAccepted(String input) {
        Set<Integer> startStates = new HashSet<>();
        startStates.add(0); // Start from state 0
        Set<Integer> resultStates = extendedTransition(startStates, input, 0);

        System.out.println("Final states after processing input: " + resultStates);
        for (int state : resultStates) {
            if (finalStates.contains(state)) {
                System.out.println("Accepted: Final state " + state + " reached.");
                return true;
            }
        }
        System.out.println("Rejected: No final state reached.");
        return false;
    }

    public static void main(String[] args) {
        try {
            NondeterministicFSM fsm = new NondeterministicFSM("nfsm.txt");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the string to check: ");
            String inputString = scanner.nextLine();
            scanner.close();

            if (fsm.isAccepted(inputString)) {
                System.out.println("The string \"" + inputString + "\" is accepted.");
            } else {
                System.out.println("The string \"" + inputString + "\" is not accepted.");
            }

        } catch (IOException e) {
            System.err.println("Error reading FSM file: " + e.getMessage());
        }
    }
}
