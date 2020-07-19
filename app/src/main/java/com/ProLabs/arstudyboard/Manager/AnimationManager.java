package com.ProLabs.arstudyboard.Manager;

import android.util.ArraySet;

import com.google.android.filament.gltfio.Animator;
import com.google.android.filament.gltfio.FilamentAsset;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;

public class AnimationManager {
    private final Set<AnimationInstance> animators = new ArraySet<>();
    private static class AnimationInstance {
        Animator animator;
        Long startTime;
        float duration;
        int index;

        AnimationInstance(Animator animator, int index, Long startTime) {
            this.animator = animator;
            this.startTime = startTime;
            this.duration = animator.getAnimationDuration(index);
            this.index = index;
        }
    }

    TransformableNode transformableNode;

    public AnimationManager(TransformableNode transformableNode) {
        this.transformableNode = transformableNode;
    }

    public void buildFilamentAnimation()
    {
        FilamentAsset filamentAsset = transformableNode.getRenderableInstance().getFilamentAsset();
        if (filamentAsset.getAnimator().getAnimationCount() > 0) {
            animators.add(new AnimationInstance(filamentAsset.getAnimator(), 0, System.nanoTime()));
        }
    }

    public void animateModel()
    {
        Long time = System.nanoTime();
        for (AnimationInstance animator : animators) {
            animator.animator.applyAnimation(
                    animator.index,
                    (float) ((time - animator.startTime) / (double) SECONDS.toNanos(1))
                            % animator.duration);
            animator.animator.updateBoneMatrices();
        }
    }

}
