package com.sidukov.weatherapp.domain.geo_api

data class Result(
    val address: Address,
    val boundingBox: BoundingBox,
    val dataSources: DataSources,
    val entityType: String,
    val id: String,
    val matchConfidence: MatchConfidence,
    val position: Position,
    val score: Float,
    val type: String,
    val viewport: Viewport
)