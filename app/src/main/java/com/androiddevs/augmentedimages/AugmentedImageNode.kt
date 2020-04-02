package com.androiddevs.augmentedimages

import android.content.Context
import android.widget.Toast
import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import kotlin.math.max

class AugmentedImageNode(
    val context: Context
) : AnchorNode() {

    var image: AugmentedImage? = null

    private var modelCompletableFuture = ModelRenderable.builder()
        .setSource(context, R.raw.beedrill)
        .build()
    private lateinit var renderable: ModelRenderable

    fun setAugmentedImage(image: AugmentedImage) {
        this.image = image
        if(!modelCompletableFuture.isDone) {
            modelCompletableFuture.thenAccept {
                renderable = it
                setAugmentedImage(image)
            }.exceptionally {
                Toast.makeText(context, "Error creating renderable", Toast.LENGTH_LONG).show()
                null
            }
            return
        }
        anchor = image.createAnchor(image.centerPose)

        val modelNode = Node().apply {
            setParent(this@AugmentedImageNode)
            renderable = this@AugmentedImageNode.renderable
        }

        val renderableBox = renderable.collisionShape as Box
        val maxEdgeSize = max(renderableBox.size.x, renderableBox.size.z)
        val maxImageEdge = max(image.extentX, image.extentZ)
        val modelScale = (maxImageEdge / maxEdgeSize) / 2f

        modelNode.localScale = Vector3(modelScale, modelScale, modelScale)

        startAnimation()
    }

    private fun startAnimation() {
        if(renderable.animationDataCount == 0) {
            return
        }
        val animationData = renderable.getAnimationData("Beedrill_Animation")
        ModelAnimator(animationData, renderable).apply {
            repeatCount = ModelAnimator.INFINITE
            start()
        }
    }
}







