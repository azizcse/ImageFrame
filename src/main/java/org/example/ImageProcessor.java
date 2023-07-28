package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessor {
    private List<String> sourceImageList = new ArrayList<>();
    File sourceFiles = new File("images\\sourceimages");
    File frameImages = new File("images\\frameimages");
    private String outputFolderName = "\\outputimages\\";

    private int frameThickness = 500;
    private String portraitFrameImage;
    private String landscapeFrameImage;

    public ImageProcessor(int frameThickness){
        this.frameThickness = frameThickness;
    }


    public void addImageFrame() {
        if (sourceImageList.isEmpty()) {
            System.out.println("Source image folder is empty");
            return;
        }

        if (portraitFrameImage == null || landscapeFrameImage == null) {
            System.out.println("Portrait or landscape image not found");
            return;
        }

        try {
            System.out.println("***** Frame combination started *******");
            for (String itemImage : sourceImageList) {
                BufferedImage mainImgBuffer = ImageIO.read(new File(itemImage));

                if (mainImgBuffer == null) {
                    System.out.println("Main image is null");
                }

                int mainImgWidth = mainImgBuffer.getWidth() + frameThickness;
                int mainImgHeight = mainImgBuffer.getHeight() + frameThickness;

                File frameFile = null;
                if (mainImgWidth < mainImgHeight) {
                    frameFile = new File(portraitFrameImage);
                } else {
                    frameFile = new File(landscapeFrameImage);
                }

                BufferedImage frameImgBuffer = ImageIO.read(frameFile);
                Image resizedImage = frameImgBuffer.getScaledInstance(mainImgWidth, mainImgHeight, Image.SCALE_SMOOTH);
                BufferedImage resizeFrameImage = new BufferedImage(mainImgWidth, mainImgHeight, BufferedImage.TYPE_INT_RGB);

                Graphics2D graphics2D = resizeFrameImage.createGraphics();
                graphics2D.drawImage(resizedImage, 0, 0, null);
                graphics2D.dispose();

                String framePath = frameFile.getAbsolutePath();
                String newFramePath = getNewFrameImagePath(framePath);
                //System.out.println("New frame path "+newFramePath);
                String ext = framePath.substring(framePath.lastIndexOf("."));
                ext = ext.replace(".", "");
                //System.out.println("EXT "+ext);
                File outputFile = new File(newFramePath);
                ImageIO.write(resizeFrameImage, ext, outputFile);

                BufferedImage frameImageBuffer = ImageIO.read(new File(newFramePath));

                // Create a new BufferedImage with the same dimensions as the frame image
                BufferedImage combinedImage = new BufferedImage(frameImageBuffer.getWidth(), frameImageBuffer.getHeight(), BufferedImage.TYPE_INT_ARGB);

                // Create a graphics object for the combined image
                Graphics2D g2d = combinedImage.createGraphics();
                // Draw the frame image onto the combined image
                g2d.drawImage(frameImageBuffer, 0, 0, null);
                g2d.drawImage(mainImgBuffer, frameThickness / 2, frameThickness / 2, null);

                g2d.dispose();


                // Write the resulting image to the output file
                String outputPath = getOutputFilePath(newFramePath, itemImage);

                String newExt = outputPath.substring(outputPath.lastIndexOf("."));
                newExt = newExt.replace(".", "");

                ImageIO.write(combinedImage, newExt, new File(outputPath));

                File newFrameImg = new File(newFramePath);
                newFrameImg.delete();

                System.out.println("Frame added : " + itemImage);

            }
            System.out.println("***** Frame combination finished *******");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getOutputFilePath(String inputPath, String mainImagePath) {
        String mainFileName = mainImagePath.substring(mainImagePath.lastIndexOf("\\") + 1);
        mainFileName = mainFileName.substring(0, mainFileName.lastIndexOf("."));
        String frameFileName = inputPath.substring(inputPath.lastIndexOf("\\") + 1);
        frameFileName = frameFileName.substring(frameFileName.lastIndexOf("."));
        //System.out.println("Path: "+inputPath+" 0  last "+inputPath.lastIndexOf("\\"));
        String path = inputPath.substring(0, inputPath.lastIndexOf("\\"));
        path = path.substring(0, path.lastIndexOf("\\"));
        return path + "" + outputFolderName + "" + mainFileName + "" + frameFileName;
    }

    private String getNewFrameImagePath(String filePath) {
        String pathWithoutExt = filePath.substring(0, filePath.lastIndexOf("."));
        String ext = filePath.substring(filePath.lastIndexOf("."));
        //System.out.println("Path : " + pathWithoutExt + " Ext: " + ext);
        long time = System.currentTimeMillis();
        return pathWithoutExt + "" + time + "" + ext;
    }

    public void loadImages() {
        try {
            sourceImageList.clear();
            File[] files = sourceFiles.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (isFile(file.getAbsolutePath())) {
                        sourceImageList.add(file.getAbsolutePath());
                        //System.out.println(file.getAbsolutePath());
                    }
                }
            }

            files = frameImages.listFiles();
            for (File file : files) {
                if (isFile(file.getAbsolutePath())) {
                    BufferedImage imgBuf = ImageIO.read(file);
                    int W = imgBuf.getWidth();
                    int H = imgBuf.getHeight();

                    if (W > H) {
                        landscapeFrameImage = file.getAbsolutePath();
                        //System.out.println("Portrait: "+landscapeFrameImage);
                    } else {
                        portraitFrameImage = file.getAbsolutePath();
                        //System.out.println("Landscape: "+portraitFrameImage);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("******* All images initialized *****");
    }

    private boolean isFile(String filePath) {
        filePath = filePath.toLowerCase();
        if (filePath.endsWith(".png") || filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return true;
        } else {
            return false;
        }
    }
}