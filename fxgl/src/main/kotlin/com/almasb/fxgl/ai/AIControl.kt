/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.ai

import com.almasb.ents.AbstractControl
import com.almasb.ents.Entity
import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.FXGL
import com.badlogic.gdx.ai.btree.BehaviorTree
import java.util.*

/**
 * TODO: each task / condition / action before doing should report with message to bubble
 * Allows to attach a behavior tree to a game entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AIControl
private constructor() : AbstractControl() {

    private lateinit var behaviorTree: BehaviorTree<com.almasb.fxgl.entity.GameEntity>

    val bubble = AIBubble()

    /**
     * Constructs AI control with given [behaviorTree].
     */
    constructor(behaviorTree: BehaviorTree<com.almasb.fxgl.entity.GameEntity>) : this() {
        this.behaviorTree = behaviorTree
    }

    /**
     * Constructs AI control with behavior tree parsed from the asset with name [treeName].
     */
    constructor(treeName: String) : this() {

        var tree = parsedTreesCache[treeName]

        if (tree == null) {
            tree = FXGL.getAssetLoader().loadBehaviorTree(treeName)
            parsedTreesCache[treeName] = tree
        }

        this.behaviorTree = tree!!.cloneTask() as BehaviorTree<com.almasb.fxgl.entity.GameEntity>
    }

    companion object {

        private val parsedTreesCache = HashMap<String, BehaviorTree<com.almasb.fxgl.entity.GameEntity> >()
    }

    fun setBubbleMessage(message: String) {
        bubble.setMessage(message)
    }

    override fun onAdded(entity: Entity) {
        if (entity is com.almasb.fxgl.entity.GameEntity) {
            behaviorTree.`object` = entity

            if (FXGL.getSettings().applicationMode != ApplicationMode.RELEASE)
                entity.mainViewComponent.view.addNode(bubble)

        } else {
            throw IllegalArgumentException("Entity $entity is not GameEntity")
        }
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
        behaviorTree.step()
    }
}