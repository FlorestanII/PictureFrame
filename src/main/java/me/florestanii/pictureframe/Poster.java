package me.florestanii.pictureframe;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Poster {
    private BufferedImage[] imgs;
    private int width;
    private int height;

    /**
     * Creates a new poster.
     *
     * @param img    image on the poster
     * @param width  width in blocks
     * @param height height in blocks
     */
    public Poster(BufferedImage img, int width, int height) {
        this.imgs = splitImage(img, width, height);
        this.width = width;
        this.height = height;
    }

    public BufferedImage[] geImages() {
        return this.imgs;
    }

    public BufferedImage getImage(int x, int y) {
        return imgs[y * width + x];
    }

    /**
     * Gets the width of this poster, in blocks.
     *
     * @return width of this poster, in blocks
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of this poster, in blocks.
     *
     * @return height of this poster, in blocks
     */
    public int getHeight() {
        return height;
    }

    private static BufferedImage[] splitImage(BufferedImage img, int width, int height) {
        //one map is 128 x 128, so we need to scale the image to (width * 128) x (height * 128)
        img = scaleImage(img, width * 128, height * 128);

        BufferedImage[] images = new BufferedImage[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                images[y * width + x] = img.getSubimage(x * 128, y * 128, 128, 128);
            }
        }

        return images;
    }

    private static BufferedImage scaleImage(final BufferedImage img, final int width, final int height) {
        BufferedImage[] images = new BufferedImage[width * height];
        double ratio = img.getWidth() / (double) img.getHeight();

        int actualWidth = (int) (ratio * height);
        int actualHeight;
        if (actualWidth < width) {
            actualHeight = (int) (width / ratio);
            actualWidth = width;
        } else {
            actualHeight = height;
        }

        int destX = (width - actualWidth) / 2;
        int destY = (height - actualHeight) / 2;
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImage.getGraphics();
        g.drawImage(img, destX, destY, destX + actualWidth, destY + actualHeight, 0, 0, img.getWidth(), img.getHeight(), null);
        g.dispose();

        return newImage;
    }
}
