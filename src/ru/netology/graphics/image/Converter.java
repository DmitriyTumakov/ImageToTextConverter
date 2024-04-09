package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    private int width = 0;
    private int height = 0;
    private double maxRatio = 0;
    private TextColorSchema schema = new ColorConverter();

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        if (maxRatio != 0) {
            double ratioWidth = ((double) img.getWidth() / img.getHeight());
            double ratioHeight = ((double) img.getHeight() / img.getWidth());
            if (ratioWidth > maxRatio) {
                throw new BadImageSizeException(ratioWidth, maxRatio);
            } else if (ratioHeight > maxRatio) {
                throw new BadImageSizeException(ratioHeight, maxRatio);
            }
        }

        int newWidth = 0;
        int newHeight = 0;
        if (width != 0 && height != 0) {
            newWidth = img.getWidth() / width;
            newHeight = img.getHeight() / height;
            int diff = Math.max(newWidth, newHeight);
            if (diff != 0) {
                newWidth = img.getWidth() / diff;
                newHeight = img.getHeight() / diff;
            } else {
                newWidth = img.getWidth();
                newHeight = img.getHeight();
            }
        } else {
            newWidth = img.getWidth();
            newHeight = img.getHeight();
        }

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);

        WritableRaster bwRaster = bwImg.getRaster();

        char[][] chars = new char[bwImg.getHeight()][bwImg.getWidth()];
        for (int w = 0; w < bwImg.getWidth(); w++) {
            for (int h = 0; h < bwImg.getHeight(); h++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                chars[h][w] = c;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int h = 0; h < bwImg.getHeight(); h++) {
            for (int w = 0; w < bwImg.getWidth(); w++) {
                stringBuilder.append(chars[h][w]);
                stringBuilder.append(chars[h][w]);
                stringBuilder.append(chars[h][w]);
            }
            stringBuilder.append('\n');
        }
        String result = stringBuilder.toString();

        return result;
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}
