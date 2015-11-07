package me.florestanii.pictureframe;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Poster {
    private BufferedImage src;
    private BufferedImage[] imgs;
    private HashMap<Integer, String> numberMap;
    private int partCount;
    private int column;

    public Poster(BufferedImage img) {
        this.src = img;
        this.numberMap = new HashMap<Integer, String>();
        scaleImg();
    }

    public BufferedImage[] getPoster() {
        return this.imgs;
    }

    public HashMap<Integer, String> getNumberMap() {
        return numberMap;
    }

    public int getNbColonne() {
        return this.column;
    }

    private void scaleImg() {

//        int columnCount = src.getWidth() % 128 == 0 ? ((int) (src.getWidth() / 128)) : ((int) (src.getWidth() / 128)) + 1;
//        int rowCount = src.getHeight() % 128 == 0 ? ((int) (src.getHeight() / 128)) : ((int) (src.getHeight() / 128)) + 1;
//        src = Util.toBufferedImage(src.getScaledInstance(columnCount * 128, rowCount * 128, 4));

        int x = 0;
        int y = 0;
        int index = 0;
        int restX = src.getWidth() % 128;
        int restY = src.getHeight() % 128;
        int line;
        if (src.getWidth() / 128 <= 0) {
            line = 1;
        } else {
            if (src.getWidth() % 128 != 0) {
                line = src.getWidth() / 128 + 1;
            } else {
                line = src.getWidth() / 128;
            }
        }
        int column;
        if (src.getHeight() <= 0) {
            column = 1;
        } else {
            if (src.getHeight() % 128 != 0) {
                column = src.getHeight() / 128 + 1;
            } else {
                column = src.getHeight() / 128;
            }
        }
        this.column = column;
        partCount = (line * column);
        imgs = new BufferedImage[partCount];
        for (int lig = 0; lig < line; lig++) {
            y = 0;
            if ((lig == line - 1) && (restX != 0)) {
                for (int col = 0; col < column; col++) {
                    if ((col == column - 1) && (restY != 0)) {
                        imgs[index] = src.getSubimage(x, y, restX, restY);
                    } else {
                        imgs[index] = src.getSubimage(x, y, restX, 128);
                        y += 128;
                    }
                    numberMap.put(index, "column " + (lig + 1) + ", row " + (col + 1));
                    index++;
                }
            } else {
                for (int col = 0; col < column; col++) {
                    if ((col == column - 1) && (restY != 0)) {
                        imgs[index] = this.src.getSubimage(x, y, 128, restY);
                    } else {
                        imgs[index] = this.src.getSubimage(x, y, 128, 128);
                        y += 128;
                    }
                    numberMap.put(index, "column " + (lig + 1) + ", row " + (col + 1));
                    index++;
                }
                x += 128;
            }
        }
    }
}
