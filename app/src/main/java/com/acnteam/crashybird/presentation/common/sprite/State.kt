package com.acnteam.crashybird.presentation.common.sprite

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Animation modes for sprite sheets
 */
enum class AnimationMode {
    Loop,        // Continuously loop the animation
    Once,        // Play once and stop
    PingPong,    // Play forward, then backward, repeat
    Manual       // Manual frame control
}

/**
 * State class to manage sprite sheet animation
 */
class SpriteState(
    initialFrame: Int = 0,
    val totalFrames: Int,
    val framesPerRow: Int,
    private val frameDuration: Long = 100L, // milliseconds
    private val animationMode: AnimationMode = AnimationMode.Loop
) {
    private val _currentFrame = MutableStateFlow(initialFrame)
    val currentFrame: StateFlow<Int> = _currentFrame.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private var animationJob: Job? = null
    private var direction = 1 // 1 for forward, -1 for backward (PingPong mode)

    /**
     * Start animation with given coroutine scope
     */
    fun startAnimation(scope: CoroutineScope) {
        if (_isPlaying.value) return

        _isPlaying.value = true
        _isFinished.value = false

        animationJob = scope.launch {
            when (animationMode) {
                AnimationMode.Loop -> animateLoop()
                AnimationMode.Once -> animateOnce()
                AnimationMode.PingPong -> animatePingPong()
                AnimationMode.Manual -> { /* Manual control, do nothing */ }
            }
        }
    }

    /**
     * Stop the animation
     */
    fun stopAnimation() {
        animationJob?.cancel()
        _isPlaying.value = false
    }

    /**
     * Reset to initial frame
     */
    fun reset() {
        stopAnimation()
        _currentFrame.value = 0
        _isFinished.value = false
        direction = 1
    }

    /**
     * Set frame manually (useful for manual mode)
     */
    fun setFrame(frame: Int) {
        if (frame in 0 until totalFrames) {
            _currentFrame.value = frame
        }
    }

    /**
     * Go to next frame manually
     */
    fun nextFrame() {
        val nextFrame = (_currentFrame.value + 1) % totalFrames
        _currentFrame.value = nextFrame
    }

    /**
     * Go to previous frame manually
     */
    fun previousFrame() {
        val prevFrame = if (_currentFrame.value == 0) totalFrames - 1 else _currentFrame.value - 1
        _currentFrame.value = prevFrame
    }

    private suspend fun animateLoop() {
        while (_isPlaying.value) {
            delay(frameDuration)
            if (_isPlaying.value) {
                _currentFrame.value = (_currentFrame.value + 1) % totalFrames
            }
        }
    }

    private suspend fun animateOnce() {
        while (_isPlaying.value && _currentFrame.value < totalFrames - 1) {
            delay(frameDuration)
            if (_isPlaying.value) {
                _currentFrame.value += 1
            }
        }
        _isPlaying.value = false
        _isFinished.value = true
    }

    private suspend fun animatePingPong() {
        while (_isPlaying.value) {
            delay(frameDuration)
            if (_isPlaying.value) {
                val nextFrame = _currentFrame.value + direction

                if (nextFrame >= totalFrames - 1) {
                    direction = -1
                    _currentFrame.value = totalFrames - 1
                } else if (nextFrame <= 0) {
                    direction = 1
                    _currentFrame.value = 0
                } else {
                    _currentFrame.value = nextFrame
                }
            }
        }
    }
}