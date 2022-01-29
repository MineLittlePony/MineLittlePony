package com.kenza

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Quaternion

object KenzaRenderInjector {

    private val mc: MinecraftClient
        get() = MinecraftClient.getInstance()


    @JvmStatic
    fun <T> renderTextIfNeed(
        entity: T,
        stack: MatrixStack,
    ): Boolean {

        if (entity is VillagerEntity) {
            renderText(entity, stack)
            return false
        } else {
            return true
        }

    }

    fun renderText(
        passedEntity: VillagerEntity,
        matrices: MatrixStack,
    ) {
        val mc = MinecraftClient.getInstance()

        matrices.push()

        val renderManager = MinecraftClient.getInstance().entityRenderDispatcher
        matrices.translate(
            0.0,
            0.0 + passedEntity.getHeight() + .5f,
            0.0
        )

        val immediate = mc.bufferBuilders.entityVertexConsumers

        val rotation: Quaternion = renderManager.camera.getRotation().copy()
        rotation.scale(-1.0f)
        matrices.multiply(rotation)

        val scale = 0.02f
        matrices.scale(-scale, -scale, scale)

        val professionName = passedEntity.toVillagerEntityExtension()?.professionName ?: ""
        val text1 = passedEntity.toVillagerEntityExtension()?.run {
            ponyName
        }
        val text2 = "\"The $professionName\""

        val offset = (-mc.textRenderer.getWidth(text1) / 2).toFloat()
        val offset2 = (-mc.textRenderer.getWidth(text2) / 2).toFloat()
        val modelViewMatrix: Matrix4f = matrices.peek().positionMatrix

        val target = mc.crosshairTarget as? EntityHitResult
//        val showProfession = target?.entity?.id == passedEntity.id && professionName.isNotEmpty()
        val showProfession = mc.player?.pos?.isInRange(passedEntity.pos, 5.0) == true && professionName.isNotEmpty()

        var shiftY = if (!showProfession) {
            0f
        } else {
            5f
        }
        var seeThrough = false


        /** extra render text for Iris mod fix transparent problem */
        mc.textRenderer.draw(
            matrices,
            text1,
            offset,
            -shiftY,
            553648127
        )


        mc.textRenderer.draw(
            text1,
            offset,
            -shiftY,
            553648127,
            false,
            modelViewMatrix,
            immediate,
            seeThrough,
            1056964608,
            15728640
        )
        mc.textRenderer.draw(text1, offset, -shiftY, -1, false, modelViewMatrix, immediate, seeThrough, 0, 15728640)

        if (showProfession) {

            val scale = 0.8f
            matrices.scale(scale, scale, scale)

            mc.textRenderer.draw(
                text2,
                offset2,
                shiftY + shiftY * (1 - scale) + 0.001f,
//                shiftY,
                553648127,
                false,
                modelViewMatrix,
                immediate,
                seeThrough,
                1056964608,
                15728640
            )
            mc.textRenderer.draw(text2, offset2, shiftY, -1, false, modelViewMatrix, immediate, seeThrough, 0, 15728640)
        }


        matrices.pop()
    }

}
