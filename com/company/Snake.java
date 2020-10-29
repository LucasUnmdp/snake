package com.company;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Snake{
    private Queue<String> queue;
    private int length;
    private Model m;
    private String faceTo = "East";
    private int[] pos = new int[2];
    private int[] cola = new int[2];

    /* -1: wall
        0: void
        1: snake head
        2/4: snake body
        3: food
    */
    public Snake(Model m) {
        this.m = m;
        spawn();
    }

    public void spawn() {
        int[][] matrix;
        this.length = 0;
        queue= new LinkedList<String>();
        m.setDIFFICULTY(100);
        matrix = m.getMatrix();
        matrix[matrix.length / 2][matrix[0].length / 2] = 1;
        this.pos[0] = matrix[0].length / 2;
        this.pos[1] = matrix.length / 2;
        cola[0]=pos[0];
        cola[1]=pos[1];
        m.setMatrix(matrix);
        generateFood();
    }

    public void step() throws Exception{
        int[][] matrix;
        matrix = m.getMatrix();
        if (this.length == 0) {
            matrix[pos[1]][pos[0]] = 0;
        } else {
            if(faceTo.equals("East") || faceTo.equals("West"))
                matrix[pos[1]][pos[0]] = 2;
            else
                matrix[pos[1]][pos[0]] = 4;
            matrix[cola[1]][cola[0]]=0;
        }
        switch (faceTo) {
            case "East":
                pos[0]++;
                queue.add("East");
                break;
            case "West":
                pos[0]--;
                queue.add("West");
                break;
            case "North":
                pos[1]--;
                queue.add("North");
                break;
            case "South":
                pos[1]++;
                queue.add("South");
                break;
        }
        if (matrix[pos[1]][pos[0]] == -1 || matrix[pos[1]][pos[0]] == 2 || matrix[pos[1]][pos[0]] == 4) {
            throw new Exception();
        }
        else {
            if (matrix[pos[1]][pos[0]] == 3) {
                this.length++;
                generateFood();
                m.moreDifficulty(Model.speed);
            }else{
                switch (queue.poll()) {
                    case "East":
                        cola[0]++;
                        break;
                    case "West":
                        cola[0]--;
                        break;
                    case "North":
                        cola[1]--;
                        break;
                    case "South":
                        cola[1]++;
                        break;
                }
            }
            matrix[pos[1]][pos[0]] = 1;
            m.setMatrix(matrix);
        }
    }
    public void generateFood() {
        int[][] matrix=m.getMatrix();
        int i, j;
        Random r = new Random();
        boolean flag = true;
        while (flag) {
            i = 1 + r.nextInt(matrix.length-1);
            j = 1 + r.nextInt(matrix[0].length-1);
            if (matrix[i][j] == 0) {
                flag = false;
                matrix[i][j] = 3;
            }
        }
        m.setMatrix(matrix);

    }

    public String getFaceTo() {
        return faceTo;
    }

    public void setFaceTo(String s) {
        this.faceTo = s;
    }
    public int getLength(){
        return length;
    }
}
