// Q1. Code Deterministic FSM which will read an input file with a transition function
// ## structure of the file
// first row ->       0, 1   # input symbols
// second row->  2, -6   #containing transition with respect to initial state
// ...                                # contains transition w.r.t subsequent state
// ...

// The final state may be represented by an extra symbol like "-"
// Implement extended transition function 
// iteratively or 
// recursively
// and check if the given string is acceptable to the given machine

import java.io.*;
import java.util.*;

public class DFSM {
    private int[][] Transition_Table;   // Transition table
    private Set<Integer> Final_State;  // Set of final states
    private int Start_State;            // Start state
    private List<Integer> Input_Symbols; // List of input symbols

    // Constructor to initialize FSM from the input file
    public DFSM(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        
        // Read input symbols (first row)
        line = br.readLine();
        String[] symbols = line.split(",");
        Input_Symbols = new ArrayList<>();
        for (String symbol : symbols) {
            Input_Symbols.add(Integer.parseInt(symbol.trim()));
        }

        // Read transitions
        List<int[]> transitions = new ArrayList<>();
        Final_State = new HashSet<>();
        int stateIndex = 0;
        while ((line = br.readLine()) != null) {
            String[] transitionStr = line.split(",");
            int[] transition = new int[transitionStr.length];
            for (int i = 0; i < transitionStr.length; i++) {
                String value = transitionStr[i].trim();
                if (value.equals("-")) { 
                    Final_State.add(stateIndex);  // Mark state as final (explicit "-")
                    transition[i] = stateIndex;   // Loopback transition (stay in final state)
                } else {
                    int nextState = Integer.parseInt(value); 
                    transition[i] = nextState;

                    // If the next state is negative, consider it a final state
                    if (nextState < 0) {
                        Final_State.add(nextState);  // Add negative states to final states
                    }
                }
            }
            transitions.add(transition);
            stateIndex++;
        }
        
        // Initialize transition table
        Transition_Table = transitions.toArray(new int[0][]);
        
        // Assume start state is state 0
        Start_State = 0;
        br.close();
    }

    // Extended transition function (iterative)
    public boolean isStringAccepted(String input) {
        int currentState = Start_State;

        for (char symbol : input.toCharArray()) {
            int inputIndex = Input_Symbols.indexOf(Character.getNumericValue(symbol));
            if (inputIndex == -1) {
                System.out.println("Invalid input symbol: " + symbol);
                return false;
            }

            // Check if currentState is a final state (negative or in Final_State set)
            if (Final_State.contains(currentState)) {
                return true;  // If we reached a final state, string is accepted
            }

            // Move to the next state based on the transition table
            currentState = Transition_Table[currentState][inputIndex];

            // If the current state is negative, it's a final state
            if (currentState < 0) {
                return true;  // Negative state implies final state, string is accepted
            }
        }

        // After processing the string, check if we are in a final state
        return Final_State.contains(currentState);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java DeterministicFSM <transitionFile> <inputString>");
            return;
        }

        try {
            // Load the FSM from the transition file
            DFSM fsm = new DFSM(args[0]);
            
            // Check if the input string is accepted
            String inputString = args[1];
            if (fsm.isStringAccepted(inputString)) {
                System.out.println("The string is accepted by the FSM.");
            } else {
                System.out.println("The string is not accepted by the FSM.");
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }
}

// Some instructions :-
//Both "-" and any negative state (like "-6") are treated as final states.
