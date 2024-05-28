// w985540_20222121
// Rinsa Riyal

// Import necessary libraries for file I/O, data structures, and math operations
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class IcePuzzleSolver {
    // Define pre-calculated move directions (up, down, left, right)
    private static final int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    // Declare class member variables
    private int numRows, numCols;
    private char[][] grid;
    private int stepNumber = 1;

    // Constructor to initialize the solver with a map from a file
    public IcePuzzleSolver(String filename) {
        loadGridFromFile(filename);
    }

    // Method to read the map from a file and store it in a grid
    private void loadGridFromFile(String filename) {
        List<char[]> tempGrid = new ArrayList<>();  // Temporary list to hold map data line by line
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                tempGrid.add(line.toCharArray());   // Convert each line to a character array and add to list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert tempGrid to a 2D char array (grid) and store map dimensions
        numRows = tempGrid.size();
        numCols = tempGrid.get(0).length;
        grid = new char[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            grid[i] = tempGrid.get(i);
        }

        // Print the loaded map for verification
        printGrid();
    }

    // Method to print the map grid
    private void printGrid() {
        System.out.println("======================");
        System.out.println("      Input Map      ");
        System.out.println("======================\n");

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /// Method to solve the ice puzzle using A* search algorithm
    public void solve() {
        // Find the start and finish positions
        int startX = -1, startY = -1, finishX = -1, finishY = -1;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (grid[i][j] == 'S') {
                    startX = i;
                    startY = j;
                } else if (grid[i][j] == 'F') {
                    finishX = i;
                    finishY = j;
                }
            }
        }

        System.out.println("Starting position: (" + (startY + 1) + "," + (startX + 1) + ")\n");
        System.out.println();

        // A* algorithm data structures
        PriorityQueue<Node> openSet = new PriorityQueue<>();     // Prioritize nodes based on f-score
        boolean[][] closedSet = new boolean[numRows][numCols];  // Track visited nodes
        Node[][] cameFrom = new Node[numRows][numCols];         // Used to reconstruct the path


        Node startNode = new Node(startX, startY);
        startNode.g = 0;
        startNode.h = computeHeuristic(startX, startY, finishX, finishY);
        startNode.f = startNode.g + startNode.h;
        openSet.add(startNode);

        // Initialize start node with g=0, h (heuristic), and f (g+h)
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();          // Get the node with the lowest f-

            if (current.x == finishX && current.y == finishY) {
                // Reconstruct and print the path
                printPath(cameFrom, current);
                System.out.println("Done");
                return;
            }

            closedSet[current.x][current.y] = true;

            for (int[] dir : directions) {
                int newX = current.x;
                int newY = current.y;

                // Slide in the direction until hitting a wall or a rock or reaching the finish
                while (isValid(newX + dir[0], newY + dir[1])) {
                    newX += dir[0];
                    newY += dir[1];
                    if (grid[newX][newY] == 'F') {
                        // Found the finish, print the path
                        String direction = (newX == current.x) ? (newY > current.y) ? "right" : "left" :
                                (newX > current.x) ? "down" : "up";
                        printPath(cameFrom, current);
                        System.out.println(stepNumber + ". Move " + direction + " to (" + (newY + 1) + ", " + (newX + 1) + ")");
                        stepNumber++;
                        System.out.println(stepNumber + ". Done");
                        return;
                    }
                }

                if (closedSet[newX][newY]) {
                    continue;
                }        // Found the finish, print the path

                Node neighbor = cameFrom[newX][newY];
                int tentativeGScore = current.g + 1; // Assuming uniform cost for each slide

                if (neighbor == null || tentativeGScore < neighbor.g) {
                    // Update neighbor node if not explored or has a lower tentative g-score
                    neighbor = new Node(newX, newY);
                    neighbor.g = tentativeGScore;
                    neighbor.h = computeHeuristic(newX, newY, finishX, finishY);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;

                    openSet.add(neighbor);
                    cameFrom[newX][newY] = current;
                }
            }
        }

        System.out.println("Path not found!");
    }

    // Method to print the steps of the path
    private void printPath(Node[][] cameFrom, Node current) {
        List<String> steps = new ArrayList<>();

        // Traverse backward through the cameFrom array to reconstruct the path
        while (current.parent != null) {
            int prevX = current.parent.x;
            int prevY = current.parent.y;
            int currX = current.x;
            int currY = current.y;

            String direction = (prevX == currX) ? (prevY < currY) ? "right" : "left" :
                    (prevX < currX) ? "down" : "up";

            steps.add("Move " + direction + " to (" + (currY + 1) + ", " + (currX + 1) + ")");
            current = cameFrom[currX][currY];
        }

        // Print the path with adjusted numbering
        System.out.println("======================");
        System.out.println("        Steps         ");
        System.out.println("======================\n");
        for (int i = steps.size() - 1; i >= 0; i--) {
            System.out.println(stepNumber + ". " + steps.get(i));
            stepNumber++;
        }
    }

    // Method to check if a position is valid in the grid
    private boolean isValid(int x, int y) {
        return x >= 0 && x < numRows && y >= 0 && y < numCols && grid[x][y] != '0';
    }

    // Method to compute the heuristic (Manhattan distance)
    private int computeHeuristic(int x, int y, int fx, int fy) {
        return Math.abs(x - fx) + Math.abs(y - fy);
    }

    // Node class for representing each position in the grid
    private static class Node implements Comparable<Node> {
        int x, y;
        int f, g, h; // f = g + h
        Node parent;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Node other) {
            return this.f - other.f;
        }
    }
}
