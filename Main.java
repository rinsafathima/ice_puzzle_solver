// w985540_20222121
// Rinsa Riyal

public class Main {

    public static void main(String[] args) {
        try {
            // Record the start time of the program execution
            long startTime = System.currentTimeMillis();

            // Create an IcePuzzleSolver object, passing the map file name
            IcePuzzleSolver solver = new IcePuzzleSolver("map.txt");

            // Solve the ice puzzle using the A* search algorithm
            solver.solve();

            // Record the end time of the program execution
            long endTime = System.currentTimeMillis();
            // Calculate the total execution time in milliseconds
            long totalTime = endTime - startTime;
            System.out.println();
            System.out.println("======================");
            System.out.println("Total runtime: "+ totalTime +" milliseconds");

        } catch (Exception e) {
            System.out.println("File not found " + e);
        }
    }
}