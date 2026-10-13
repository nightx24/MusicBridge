package com.musicbridge.app

/** Metadata-only match result; this helper never accesses accounts or networks. */
data class TrackMatchCandidate(val title: String, val artist: String, val isrc: String? = null)

enum class MatchConfidence { EXACT_ISRC, TITLE_AND_ARTIST, REVIEW, NO_MATCH }

data class MatchResult(val candidate: TrackMatchCandidate?, val confidence: MatchConfidence, val reason: String)

object TrackMatcher {
    fun match(source: TrackMatchCandidate, candidates: List<TrackMatchCandidate>): MatchResult {
        if (candidates.isEmpty()) return MatchResult(null, MatchConfidence.NO_MATCH, "No candidates supplied")
        val sourceIsrc = source.isrc?.filter { it.isLetterOrDigit() }?.uppercase()
        if (!sourceIsrc.isNullOrBlank()) {
            candidates.firstOrNull { it.isrc?.filter { c -> c.isLetterOrDigit() }?.uppercase() == sourceIsrc }
                ?.let { return MatchResult(it, MatchConfidence.EXACT_ISRC, "ISRC matches exactly") }
        }
        val title = normalize(source.title)
        val artist = normalize(source.artist)
        val ranked = candidates.map { it to (similarity(title, normalize(it.title)) * 0.6 + similarity(artist, normalize(it.artist)) * 0.4) }
            .sortedByDescending { it.second }
        val best = ranked.first()
        val confidence = when {
            best.second >= 0.92 -> MatchConfidence.TITLE_AND_ARTIST
            best.second >= 0.70 -> MatchConfidence.REVIEW
            else -> MatchConfidence.NO_MATCH
        }
        val selected = if (confidence == MatchConfidence.NO_MATCH) null else best.first
        val reason = when (confidence) {
            MatchConfidence.EXACT_ISRC -> "ISRC matches exactly"
            MatchConfidence.TITLE_AND_ARTIST -> "Title and artist match strongly"
            MatchConfidence.REVIEW -> "Possible match; user review is required"
            MatchConfidence.NO_MATCH -> "No sufficiently close match"
        }
        return MatchResult(selected, confidence, reason)
    }

    private fun normalize(value: String): String = value.lowercase()
        .replace(Regex("[^a-z0-9]+"), " ").trim().replace(Regex("\\s+"), " ")

    private fun similarity(a: String, b: String): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0
        if (a == b) return 1.0
        val maxLength = maxOf(a.length, b.length)
        return (maxLength - distance(a, b)).toDouble() / maxLength
    }

    private fun distance(a: String, b: String): Int {
        var previous = IntArray(b.length + 1) { it }
        for (i in a.indices) {
            val current = IntArray(b.length + 1)
            current[0] = i + 1
            for (j in b.indices) {
                val cost = if (a[i] == b[j]) 0 else 1
                current[j + 1] = minOf(current[j] + 1, previous[j + 1] + 1, previous[j] + cost)
            }
            previous = current
        }
        return previous[b.length]
    }
}
