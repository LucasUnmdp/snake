package com.company;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel {
    private Model instModel;
    private boolean paint = true;

    public View(Model m, int width, int heigh) {
        instModel = m;
        this.setPreferredSize(new Dimension(width, heigh));
        initializeThread();
    }

    private void initializeThread() {
        new Thread(() -> {
            while (paint) {
                this.repaint();
            }

        }).start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        paintProps(g);
    }

    private void paintProps(Graphics g) {
        int[][] mat = instModel.getMatrix();
        int pixel = Model.pixel;
        g.setColor(Color.GRAY);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 100));
        g.drawString(String.valueOf(instModel.getSnake().getLength()),this.getWidth()/2-g.getFontMetrics().stringWidth(String.valueOf(instModel.getSnake().getLength()))/2,this.getHeight()/2);
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                if (mat[i][j] == -1) {
                    g.setColor(Color.WHITE);
                    g.fillRect(j * pixel, i * pixel, pixel, pixel);
                    g.setColor(Color.GRAY);
                    g.drawRect(j * pixel + 5, i * pixel + 5, pixel - 10, pixel - 10);
                }
                if (mat[i][j] == 1 || mat[i][j] == 2 || mat[i][j] == 4) {
                    g.setColor(Color.GREEN);
                    g.fillRect(j * pixel, i * pixel, pixel, pixel);
                    if (mat[i][j] == 2) {
                        g.setColor(Color.ORANGE);
                        g.fillRect(j * pixel, i * pixel, 3, pixel);
                    } else if (mat[i][j] == 4) {
                        g.setColor(Color.ORANGE);
                        g.fillRect(j * pixel, i * pixel, pixel, 3);
                    } else {
                        g.setColor(Color.BLUE);
                        switch (instModel.getSnake().getFaceTo()) {
                            case "West":
                                g.fillArc(j * pixel + 5, i * pixel + 5, 10, 10, 0, 360);
                                break;
                            case "East":
                                g.fillArc(j * pixel + 5, i * pixel + 5, 10, 10, 0, 360);
                                break;
                            case "North":
                                g.fillArc(j * pixel, i * pixel + 2, 5, 10, 0, 360);
                                g.fillArc(j * pixel + pixel - 5, i * pixel + 2, 5, 10, 0, 360);
                                break;
                            case "South":
                                g.fillArc(j * pixel, i * pixel + pixel - 15, 5, 10, 0, 360);
                                g.fillArc(j * pixel + pixel - 5, i * pixel + pixel - 15, 5, 10, 0, 360);
                                break;
                        }
                    }
                }
                if (mat[i][j] == 3) {
                    g.setColor(Color.RED);
                    g.fillArc(j * pixel, i * pixel, pixel, pixel, 0, 360);
                    //g.fillRect(j*pixel,i*pixel,pixel,pixel);
                }
            }
        }
    }
}
