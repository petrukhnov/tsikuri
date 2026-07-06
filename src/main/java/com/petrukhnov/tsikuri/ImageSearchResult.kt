package com.petrukhnov.tsikuri

import org.opencv.core.Point

interface ImageSearchResult {
    data class Found(
        val location: Point,
        val confidence: Double
    ) : ImageSearchResult

    object NotFound : ImageSearchResult

    data class Error(
        val cause: Throwable
    ) : ImageSearchResult
}