package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Model implements KeyListener, ActionListener {
    int[][] matrix;
    public static int pixel = 20;
    public static int DIFFICULTY = 100;
    public static int speed;
    View instView;
    JFrame instWindow;
    boolean cycle = true;
    private Snake snake;

    public static void main(String[] args) {
        new Model();
    }

    private Model() {
        this.matrix = new int[40][40];
        generateMap();
        initializeMatrix();
        createWindow();
        initializeThread();
    }

    private void createWindow() {
        instWindow = new JFrame("Gusanito");
        instWindow.addKeyListener(this);
        SwingUtilities.invokeLater(() -> {
            instWindow.setLayout(new BorderLayout());
            instView = new View(this, pixel * matrix[0].length, pixel * matrix.length);
            instWindow.add(instView, BorderLayout.CENTER);
            menu();
            instWindow.setResizable(false);
            instWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            instWindow.pack();
            instWindow.setLocationRelativeTo(null);
            instWindow.setVisible(true);
        });
    }

    private void menu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Difficulty");

        JMenuItem itemEasy = new JMenuItem("Easy");
        itemEasy.setActionCommand("Easy_Mode");
        itemEasy.addActionListener(this);
        gameMenu.add(itemEasy);

        JMenuItem itemMedium = new JMenuItem("Medium");
        itemMedium.setActionCommand("Medium_Mode");
        itemMedium.addActionListener(this);
        gameMenu.add(itemMedium);

        JMenuItem itemHard = new JMenuItem("TryHard");
        itemHard.setActionCommand("TryHard_Mode");
        itemHard.addActionListener(this);
        gameMenu.add(itemHard);

        menuBar.add(gameMenu);
        instWindow.add(menuBar, BorderLayout.NORTH);
    }

    public int[][] getMatrix() {
        return this.matrix;
    }

    public void setMatrix(int[][] mat) {
        this.matrix = mat;
    }

    private void initializeMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            if (i < matrix.length - 1) {
                matrix[i][1] = 0;
                matrix[i][(matrix[0].length) - 2] = 0;
            }
            matrix[i][0] = -1;
            matrix[i][(matrix[0].length) - 1] = -1;
            if (i < matrix[0].length - 1) {
                matrix[1][i] = 0;
                matrix[matrix.length - 2][i] = 0;
            }
            matrix[0][i] = -1;
            matrix[matrix.length - 1][i] = -1;
        }
    }

    private void initializeThread() {
        this.snake = new Snake(this);
        new Thread(() -> {
            while (cycle) {
                try {
                    this.snake.step();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Score " + this.snake.getLength(), "You died :(", JOptionPane.PLAIN_MESSAGE);
                    matrix = new int[40][40];
                    generateMap();
                    initializeMatrix();
                    this.snake = new Snake(this);
                }
                try {
                    Thread.sleep(DIFFICULTY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                if (!this.snake.getFaceTo().equals("South"))
                    this.snake.setFaceTo("North");
                break;
            case KeyEvent.VK_A:
                if (!this.snake.getFaceTo().equals("East"))
                    this.snake.setFaceTo("West");
                break;
            case KeyEvent.VK_S:
                if (!this.snake.getFaceTo().equals("North"))
                    this.snake.setFaceTo("South");
                break;
            case KeyEvent.VK_D:
                if (!this.snake.getFaceTo().equals("West"))
                    this.snake.setFaceTo("East");
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void moreDifficulty(int x) {
        if (DIFFICULTY - x > 50)
            this.DIFFICULTY -= x;
        else
            this.DIFFICULTY = 50;
    }

    public void setDIFFICULTY(int x) {
        this.DIFFICULTY = x;
    }

    public Snake getSnake() {
        return this.snake;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Easy_Mode":
                this.speed = 0;
                matrix = new int[40][40];
                generateMap();
                initializeMatrix();
                this.snake.spawn();
            case "Medium_Mode":
                this.speed = 2;
                matrix = new int[40][40];
                generateMap();
                initializeMatrix();
                this.snake.spawn();
            case "TryHard_Mode":
                this.speed = 4;
                matrix = new int[40][40];
                generateMap();
                initializeMatrix();
                this.snake.spawn();
        }
    }

    private void generateMap() {
        int[][] m = new int[matrix.length][matrix[0].length];
        Random r = new Random();
        for (int x = 0; x < matrix[0].length; x++) {
            for (int y = 0; y < matrix.length; y++) {
                if (r.nextDouble() < 0.45) {
                    m[x][y] = 0;
                } else
                    m[x][y] = -1;
            }
        }
        for (int i = 0; i < 4; i++) {
            m = doSimulationStep(m);
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                m[matrix.length / 2 + i][matrix[0].length / 2 - j] = 0;
                m[matrix.length / 2 + i][matrix[0].length / 2 + j] = 0;
                m[matrix.length / 2 - i][matrix[0].length / 2 - j] = 0;
                m[matrix.length / 2 - i][matrix[0].length / 2 + j] = 0;
            }
        }
        for (int i = 0; i < 5; i++) {
            m = fixmap(m);
        }
        this.matrix = m;
    }

    private int[][] doSimulationStep(int[][] oldMap) {
        int[][] m = new int[matrix.length][matrix[0].length];
        int deathLimit = 4;
        int birthLimit = 3;
        for (int x = 0; x < oldMap.length; x++) {
            for (int y = 0; y < oldMap[0].length; y++) {
                int nbs = countAliveNeighbours(oldMap, x, y);
                if (oldMap[x][y] == 0) {
                    if (nbs < deathLimit) {
                        m[x][y] = -1;
                    } else {
                        m[x][y] = 0;
                    }
                } else {
                    if (nbs > birthLimit) {
                        m[x][y] = 0;
                    } else {
                        m[x][y] = -1;
                    }
                }
            }
        }
        return m;
    }

    private int countAliveNeighbours(int[][] map, int x, int y) {
        int count = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int neighbour_x = x + i;
                int neighbour_y = y + j;
                if (i == 0 && j == 0) {
                } else if (neighbour_x < 0 || neighbour_y < 0 || neighbour_x >= map.length || neighbour_y >= map[0].length) {
                    count = count + 1;
                } else if (map[neighbour_x][neighbour_y] == 0) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    private int countAdjacent(int[][] map, int x, int y) {
        int count = 0;
        if (x - 1 < map[0].length && x - 1 > 0 && map[x - 1][y] == -1)
            count += 1;

        if (x + 1 < map[0].length && map[x + 1][y] == -1)
            count += 1;

        if (y - 1 < map.length && y - 1 > 0 && map[x][y - 1] == -1)
            count += 1;

        if (y + 1 < map.length && map[x][y + 1] == -1)
            count += 1;
        return count;
    }

    private int[][] fixmap(int[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                int count = countAdjacent(map, i, j);
                if (map[i][j] == 0 && count > 2) {
                    map[i][j] = -1;
                }
            }
        }
        return map;
    }

}
