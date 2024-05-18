package appUtilities;

import model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class DuplicateTransactionHandler {
    private List<Transaction> potentialDuplicates;
    private Scanner scan;
    String displayHeader;

    public DuplicateTransactionHandler(List<Transaction> potentialDuplicates, int from) {
        this.potentialDuplicates = potentialDuplicates;
        this.scan = new Scanner(System.in);
        if (from == 1) {this.displayHeader = "These Transactions appear to be Duplicates:";}
        else if (from == 2) {this.displayHeader = "These Transactions appear to already be added to the Database:";}

    }

    public List<Transaction> processPotentialDuplicates() {
        if (potentialDuplicates.isEmpty()) {
            System.out.println("No potential duplicates to review.");
            return potentialDuplicates;
        }

        System.out.println("\n"+displayHeader);
        int batchSize = 9;
        int index = 0;
        int total = potentialDuplicates.size();
        while (index < total) {
            int currentBatchSize = Math.min(index + batchSize, total);

            for ( int i = index; i < currentBatchSize; i++ ) {
                Transaction transaction = potentialDuplicates.get(i);
                System.out.printf("%d: %s\n", index+1, transaction);
                index++;

            }
            System.out.println("Enter 'all' if all are duplicates, 'none' if none are duplicates, or specific numbers for certain duplicates.");
            System.out.println("Enter the numbers of the actual duplicates, separated by commas (e.g., 1,3,5).");

            String input = scan.nextLine().trim();
            if ("all".equalsIgnoreCase(input)) {
                processUserDecision(getAllIntsBefore(currentBatchSize));
            } else if ("none".equalsIgnoreCase(input)) {
                break;
            } else {
                List<Integer> inputs = new ArrayList<>();
                String[] places = input.split(",");
                for ( String place : places ) {
                    inputs.add(Integer.parseInt(place.trim())-1);
                }
                Collections.sort(inputs);
                System.out.println("(processPotentialDuplicate): else statement: "+ inputs);
                Collections.reverse(inputs);
                System.out.println("Reversed user input: "+ inputs);
                processUserDecision(inputs);
            }
        }
        return potentialDuplicates;
    }

    private void processUserDecision(List<Integer> indexOfDuplicates) {
        System.out.println(indexOfDuplicates.toString());
        for ( int i : indexOfDuplicates ) {
            System.out.println("Removing: " + potentialDuplicates.get(i).toString());
            potentialDuplicates.remove(i);
        }
    }

    private void removeDuplicate(Transaction transaction) {
        potentialDuplicates.remove(transaction);
    }


    private List<Integer> getAllIntsBefore(int i) {
        List<Integer> numbers = new ArrayList<>();
        for ( int num = 0; num <= i - 1; num++ ) {
            numbers.add(num);
            System.out.println(num);
        };
        return numbers.reversed();
    }

}

