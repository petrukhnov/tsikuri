package com.petrukhnov.tsikuri

import net.sourceforge.tess4j.ITessAPI
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_BYTE_BINARY
import java.awt.image.BufferedImage.TYPE_BYTE_GRAY
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object TextRecognitionHelper {

    private const val DEFAULT_ASCII_WHITELIST =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" +
            " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"

    private const val TESSDATA_RESOURCE_DIRECTORY = "tessdata"
    private val bundledDatapaths = mutableMapOf<String, File>()

    data class Options(
        val language: String = "eng",
        val pageSegmentationMode: Int = ITessAPI.TessPageSegMode.PSM_SINGLE_LINE,
        val characterWhitelist: String? = DEFAULT_ASCII_WHITELIST,
        val datapath: String? = null,
        val scale: Double = 2.0,
        val threshold: Boolean = false,
        val trimResult: Boolean = true,
        val variables: Map<String, String> = emptyMap()
    )

    fun readText(
        topLeftX: Int,
        topLeftY: Int,
        bottomRightX: Int,
        bottomRightY: Int,
        options: Options = Options()
    ): String {
        return readText(rectangleFromCoordinates(topLeftX, topLeftY, bottomRightX, bottomRightY), options)
    }

    fun readText(
        rectangle: Rectangle,
        options: Options = Options()
    ): String {
        val screenshot = Robot().createScreenCapture(rectangle)
        return readText(screenshot, options)
    }

    fun readText(
        image: BufferedImage,
        options: Options = Options()
    ): String {
        val preparedImage = prepareImage(image, options)
        val text = createTesseract(options).doOCR(preparedImage)
        return if (options.trimResult) text.trim() else text
    }

    fun optionsForExpectedText(
        expectedLines: Int? = null,
        approximateCharacters: Int? = null,
        characterWhitelist: String? = DEFAULT_ASCII_WHITELIST,
        threshold: Boolean = false
    ): Options {
        val pageSegmentationMode = when {
            expectedLines == 1 && approximateCharacters == 1 -> ITessAPI.TessPageSegMode.PSM_SINGLE_CHAR
            expectedLines == 1 && approximateCharacters != null && approximateCharacters <= 16 -> ITessAPI.TessPageSegMode.PSM_SINGLE_WORD
            expectedLines == 1 -> ITessAPI.TessPageSegMode.PSM_SINGLE_LINE
            expectedLines != null && expectedLines > 1 -> ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK
            else -> ITessAPI.TessPageSegMode.PSM_AUTO
        }

        return Options(
            pageSegmentationMode = pageSegmentationMode,
            characterWhitelist = characterWhitelist,
            threshold = threshold
        )
    }

    private fun createTesseract(options: Options): ITesseract {
        return Tesseract().apply {
            setDatapath(resolveDatapath(options).absolutePath)
            setLanguage(options.language)
            setPageSegMode(options.pageSegmentationMode)
            options.characterWhitelist?.let { setVariable("tessedit_char_whitelist", it) }
            options.variables.forEach { (name, value) -> setVariable(name, value) }
        }
    }

    private fun resolveDatapath(options: Options): File {
        options.datapath?.let { return requireLanguageData(languageDataDirectories(File(it)), options.language) }

        val candidateDirectories = listOfNotNull(
            System.getenv("TESSDATA_PREFIX")?.takeIf { it.isNotBlank() }?.let(::File),
            File("src/main/resources/$TESSDATA_RESOURCE_DIRECTORY"),
            File(TESSDATA_RESOURCE_DIRECTORY)
        ).flatMap(::languageDataDirectories)
            .distinctBy { it.absoluteFile.normalize().absolutePath }

        candidateDirectories.firstOrNull { containsLanguageData(it, options.language) }?.let {
            return it
        }

        copyBundledLanguageData(options.language)?.let { return it }

        val requiredFiles = languageDataFilenames(options.language).joinToString(", ")
        throw IllegalArgumentException(
            "Tesseract language data is missing for '${options.language}'. " +
                "Expected $requiredFiles in one of: " +
                candidateDirectories.joinToString(", ") { it.absolutePath } +
                ". Add the traineddata file to src/main/resources/$TESSDATA_RESOURCE_DIRECTORY, " +
                "pass Options(datapath = \"/path/to/tessdata\"), or set TESSDATA_PREFIX."
        )
    }

    private fun requireLanguageData(datapaths: List<File>, language: String): File {
        datapaths.firstOrNull { containsLanguageData(it, language) }?.let {
            return it
        }

        val requiredFiles = languageDataFilenames(language).joinToString(", ")
        throw IllegalArgumentException(
            "Tesseract language data is missing for '$language'. " +
                "Expected $requiredFiles in one of: ${datapaths.joinToString(", ") { it.absolutePath }}."
        )
    }

    private fun languageDataDirectories(path: File): List<File> {
        return listOf(path, File(path, TESSDATA_RESOURCE_DIRECTORY))
    }

    private fun containsLanguageData(datapath: File, language: String): Boolean {
        return datapath.isDirectory && languageDataFilenames(language).all { File(datapath, it).isFile }
    }

    private fun languageDataFilenames(language: String): List<String> {
        return language.split("+")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { "$it.traineddata" }
    }

    private fun copyBundledLanguageData(language: String): File? {
        synchronized(bundledDatapaths) {
            bundledDatapaths[language]?.let { return it }
        }

        val classLoader = TextRecognitionHelper::class.java.classLoader
        val filenames = languageDataFilenames(language)
        val resources = filenames.map { filename ->
            filename to "$TESSDATA_RESOURCE_DIRECTORY/$filename"
        }

        if (resources.any { (_, resourcePath) -> classLoader.getResource(resourcePath) == null }) {
            return null
        }

        val tessdataDirectory = Files.createTempDirectory("tsikuri-tessdata-").toFile()
        tessdataDirectory.deleteOnExit()

        resources.forEach { (filename, resourcePath) ->
            val target = File(tessdataDirectory, filename)
            classLoader.getResourceAsStream(resourcePath).use { input ->
                requireNotNull(input) { "Bundled Tesseract resource disappeared: $resourcePath" }
                Files.copy(input, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            target.deleteOnExit()
        }

        synchronized(bundledDatapaths) {
            bundledDatapaths[language] = tessdataDirectory
        }

        return tessdataDirectory
    }

    private fun rectangleFromCoordinates(
        topLeftX: Int,
        topLeftY: Int,
        bottomRightX: Int,
        bottomRightY: Int
    ): Rectangle {
        val x = min(topLeftX, bottomRightX)
        val y = min(topLeftY, bottomRightY)
        val width = max(1, abs(topLeftX - bottomRightX))
        val height = max(1, abs(topLeftY - bottomRightY))
        return Rectangle(x, y, width, height)
    }

    private fun prepareImage(image: BufferedImage, options: Options): BufferedImage {
        val scaledImage = scaleImage(image, options.scale)
        val grayscaleImage = convertToGrayscale(scaledImage)
        return if (options.threshold) thresholdImage(grayscaleImage) else grayscaleImage
    }

    private fun scaleImage(image: BufferedImage, scale: Double): BufferedImage {
        if (scale == 1.0) {
            return image
        }

        val width = max(1, (image.width * scale).roundToInt())
        val height = max(1, (image.height * scale).roundToInt())
        val scaledImage = BufferedImage(width, height, TYPE_INT_RGB)
        val graphics = scaledImage.createGraphics()

        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
            graphics.drawImage(image, 0, 0, width, height, null)
        } finally {
            graphics.dispose()
        }

        return scaledImage
    }

    private fun convertToGrayscale(image: BufferedImage): BufferedImage {
        val grayscaleImage = BufferedImage(image.width, image.height, TYPE_BYTE_GRAY)
        val graphics = grayscaleImage.createGraphics()

        try {
            graphics.drawImage(image, 0, 0, null)
        } finally {
            graphics.dispose()
        }

        return grayscaleImage
    }

    private fun thresholdImage(image: BufferedImage): BufferedImage {
        val thresholdImage = BufferedImage(image.width, image.height, TYPE_BYTE_BINARY)
        val graphics: Graphics2D = thresholdImage.createGraphics()

        try {
            graphics.color = Color.WHITE
            graphics.fillRect(0, 0, image.width, image.height)

            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val gray = image.raster.getSample(x, y, 0)
                    if (gray < 160) {
                        thresholdImage.raster.setSample(x, y, 0, 0)
                    }
                }
            }
        } finally {
            graphics.dispose()
        }

        return thresholdImage
    }
}
