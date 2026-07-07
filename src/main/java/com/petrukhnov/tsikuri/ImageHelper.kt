package com.petrukhnov.tsikuri

import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.AWTException
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.IOException

object ImageHelper {
    private const val DEFAULT_MIN_CONFIDENCE = 0.85


    fun findImage(searchTemplate: Mat, minConfidence: Double = DEFAULT_MIN_CONFIDENCE): ImageSearchResult {

        val screenshot = takeScreenshot()
        val result = Mat()

        Imgproc.matchTemplate(
            screenshot,
            searchTemplate,
            result,
            Imgproc.TM_CCOEFF_NORMED
        )

        val match = Core.minMaxLoc(result)
        if (result.empty() || match.maxVal < minConfidence) {
            return ImageSearchResult.NotFound
        } else {
            val centerX = (match.maxLoc.x + searchTemplate.width() / 2.0)
            val centerY = (match.maxLoc.y + searchTemplate.height() / 2.0)
            return ImageSearchResult.Found(
                location = Point(centerX, centerY),
                confidence = match.maxVal
            )
        }
    }

    fun waitForImage(
        searchTemplate: Mat,
        timeoutMs: Long = 5000,
        minConfidence: Double = DEFAULT_MIN_CONFIDENCE
    ): ImageSearchResult {
        var searchResult =  findImage(searchTemplate, minConfidence)
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs && searchResult == ImageSearchResult.NotFound) {
            Thread.sleep(50)
            searchResult =  findImage(searchTemplate, minConfidence)
        }
        return searchResult
    }

    fun waitForImage(
        imagePath:  String,
        timeoutMs: Long = 5000,
        minConfidence: Double = DEFAULT_MIN_CONFIDENCE
    ): ImageSearchResult {
        val searchTemplate = readImageResource(imagePath)
        return waitForImage(searchTemplate, timeoutMs, minConfidence)
    }

    // todo: requireImage, waitRequiredImage


    @Throws(AWTException::class)
    fun takeScreenshot(): Mat {
        val screenBounds = Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
        val image = Robot().createScreenCapture(screenBounds)
        return bufferedImageToMat(image)
    }

    @Throws(IOException::class)
    fun readImageResource(resourceName: String?): Mat {
        this::class.java.getClassLoader().getResourceAsStream(resourceName).use { inputStream ->
            if (inputStream == null) {
                throw IOException("Resource not found on classpath: $resourceName")
            }
            val image = Imgcodecs.imdecode(
                MatOfByte(*inputStream.readAllBytes()),
                Imgcodecs.IMREAD_COLOR
            )

            if (image.empty()) {
                throw IOException("Resource is not a readable image: $resourceName")
            }
            return image
        }
    }

    fun bufferedImageToMat(image: BufferedImage): Mat {
        val bgrImage = BufferedImage(
            image.width,
            image.height,
            BufferedImage.TYPE_3BYTE_BGR
        )

        val graphics = bgrImage.createGraphics()
        try {
            graphics.drawImage(image, 0, 0, null)
        } finally {
            graphics.dispose()
        }

        val pixels = (bgrImage.raster.getDataBuffer() as DataBufferByte).getData()
        val mat = Mat(bgrImage.height, bgrImage.width, CvType.CV_8UC3)
        mat.put(0, 0, pixels)
        return mat
    }

}
