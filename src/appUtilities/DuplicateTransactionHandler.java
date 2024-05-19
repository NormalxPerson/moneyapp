package appUtilities;

import model.Transaction;

import java.util.*;

public class DuplicateTransactionHandler {
    private List<Transaction> potentialDuplicates;
    private Scanner scan;
    private String displayHeader;

    public DuplicateTransactionHandler(List<Transaction> potentialDuplicates, int from) {
        this.potentialDuplicates = new ArrayList<>(potentialDuplicates);  // Make a copy to avoid modifying the original list
        this.scan = new Scanner(System.in);
        if (from == 1) {
            this.displayHeader = "These Transactions appear to be Duplicates:";
        } else if (from == 2) {
            this.displayHeader = "These Transactions appear to already be added to the Database:";
        }
    }

    public List<Transaction> processPotentialDuplicates() {
        if (potentialDuplicates.isEmpty()) {
            System.out.println("No potential duplicates to review.");
            return potentialDuplicates;
        }
        System.out.println("\n" + displayHeader);
        int batchSize = 25;
        int index = 0;

        while (index < potentialDuplicates.size()) {
            int end = Math.min(index + batchSize, potentialDuplicates.size());
            printBatch(index, end);

            System.out.println("Enter 'all' if all are duplicates, 'none' if none are duplicates, or specific numbers for certain duplicates.");
            System.out.println("Enter the numbers of the actual duplicates, separated by commas (e.g., 1,3,5): ");

            String input = scan.nextLine().trim();
            if ("all".equalsIgnoreCase(input)) {
                deleteBatch(index, end);
            } else if ("none".equalsIgnoreCase(input)) {
                // Do nothing, move to the next batch
                index = end;
            } else {
                List<Integer> indicesToRemove = parseUserInput(input, end - index);
                deleteSpecificTransactions(index, indicesToRemove);
                index = end;
            }
        }
        return potentialDuplicates;
    }

    private void printBatch(int start, int end) {
        for ( int i = start; i < end; i++ ) {
            System.out.printf("%d: %s\n", i - start + 1, potentialDuplicates.get(i));
        }
    }

    private void deleteBatch(int start, int end) {
        for ( int i = end - 1; i >= start; i-- ) {
            System.out.println("Removing: " + potentialDuplicates.get(i));
            potentialDuplicates.remove(i);
        }
    }

    private List<Integer> parseUserInput(String input, int batchSize) {
        List<Integer> indices = new ArrayList<>();
        String[] parts = input.split(",");
        for ( String part : parts ) {
            try {
                int index = Integer.parseInt(part.trim()) - 1;
                if (index >= 0 && index < batchSize) {
                    indices.add(index);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + part);
            }
        }
        return indices;
    }

    private void deleteSpecificTransactions(int batchStartIndex, List<Integer> indicesToRemove) {
        Collections.sort(indicesToRemove, Collections.reverseOrder());
        for ( int index : indicesToRemove ) {
            int actualIndex = batchStartIndex + index;
            System.out.println("Removing: " + potentialDuplicates.get(actualIndex));
            potentialDuplicates.remove(actualIndex);
        }
    }
}

