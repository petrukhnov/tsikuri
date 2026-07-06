package com.petrukhnov.tsikuri;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    static {
        OpenCV.loadLocally();
    }

    public static void main(String[] args) throws AWTException, IOException {
        Mat template = readImageResource("button.png");
        Mat screenshot = takeScreenshot();
        Mat result = new Mat();

        Imgproc.matchTemplate(
                screenshot,
                template,
                result,
                Imgproc.TM_CCOEFF_NORMED
        );

        Core.MinMaxLocResult match = Core.minMaxLoc(result);
        System.out.printf(
                "Best match confidence %.3f at x=%.0f, y=%.0f%n",
                match.maxVal,
                match.maxLoc.x,
                match.maxLoc.y
        );

        clickCenterOfMatch(match, template);
    }

    private static Mat readImageResource(String resourceName) throws IOException {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found on classpath: " + resourceName);
            }

            Mat image = Imgcodecs.imdecode(
                    new MatOfByte(inputStream.readAllBytes()),
                    Imgcodecs.IMREAD_COLOR
            );

            if (image.empty()) {
                throw new IOException("Resource is not a readable image: " + resourceName);
            }

            return image;
        }
    }

    public static Mat takeScreenshot() throws AWTException {
        Rectangle screenBounds = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage image = new Robot().createScreenCapture(screenBounds);
        return bufferedImageToMat(image);
    }

    private static void clickCenterOfMatch(Core.MinMaxLocResult match, Mat template) throws AWTException {
        int centerX = (int) Math.round(match.maxLoc.x + template.width() / 2.0);
        int centerY = (int) Math.round(match.maxLoc.y + template.height() / 2.0);

        Robot robot = new Robot();
        robot.mouseMove(centerX, centerY);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static Mat bufferedImageToMat(BufferedImage image) {
        BufferedImage bgrImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR
        );

        Graphics2D graphics = bgrImage.createGraphics();
        try {
            graphics.drawImage(image, 0, 0, null);
        } finally {
            graphics.dispose();
        }

        byte[] pixels = ((DataBufferByte) bgrImage.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(bgrImage.getHeight(), bgrImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }
}
