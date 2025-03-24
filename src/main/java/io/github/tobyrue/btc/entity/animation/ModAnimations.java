package io.github.tobyrue.btc.entity.animation;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class ModAnimations {

	//Luminary

	public static final Animation ELDRITCH_LUMINARY_WALK = Animation.Builder.create(1.5f).looping()
			.addBoneAnimation("leg_2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leg_1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("legs",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leg1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leg2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation ELDRITCH_LUMINARY_IDLE = Animation.Builder.create(4f).looping()
			.addBoneAnimation("sidearm1",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("middlearm1",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("sidearm2",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("middlearm2",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation ELDRITCH_LUMINARY_CAST = Animation.Builder.create(2f)
			.addBoneAnimation("armfullside1",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(2f, 0f, -1f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-98.06f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-98.06f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-98.06f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.75f, AnimationHelper.createRotationalVector(-98.06f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside2",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(-2f, 0f, -1f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-97.97f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-97.97f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-97.97f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.75f, AnimationHelper.createRotationalVector(-97.97f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR))).build();

	//Copper Golem


	public static final Animation COPPER_WAKE_UP = Animation.Builder.create(6f)
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.25f, AnimationHelper.createTranslationalVector(0f, -1.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.416767f, AnimationHelper.createTranslationalVector(0f, -1.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, -360f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(0f, -720f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2.25f, AnimationHelper.createRotationalVector(0f, -1080f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3.75f, AnimationHelper.createRotationalVector(0f, -1440f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.416767f, AnimationHelper.createTranslationalVector(0f, -1f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, -360f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(0f, -720f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3f, AnimationHelper.createRotationalVector(0f, -1080f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("copper_golem",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(4.708343f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.875f, AnimationHelper.createTranslationalVector(0f, 2.96f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5f, AnimationHelper.createTranslationalVector(0f, 4f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.083433f, AnimationHelper.createTranslationalVector(0f, 4f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.208343f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.458343f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -2f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.416767f, AnimationHelper.createTranslationalVector(0f, -1f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("legs",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leftleg",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(4.676667f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.75f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.834333f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.916767f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.041677f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.083433f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.125f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.167667f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.208343f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.25f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.291677f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.343333f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.416767f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.458343f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightleg",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(4.676667f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.75f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.834333f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4.916767f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.041677f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.083433f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.125f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.167667f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.208343f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.25f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.291677f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.343333f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.416767f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(5.458343f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation COPPER_WALK = Animation.Builder.create(1f).looping()
			.addBoneAnimation("leftleg",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.3433333f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightleg",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.3433333f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leftarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation COPPER_IDLE = Animation.Builder.create(3f).looping()
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation COPPER_PRESS_BUTTON_UP = Animation.Builder.create(2.0416765f)
			.addBoneAnimation("copper_golem",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0.9583434f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.125f, AnimationHelper.createTranslationalVector(0f, 2.96f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createTranslationalVector(0f, 4f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.3433333f, AnimationHelper.createTranslationalVector(0f, 4f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.4583433f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.7083433f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.6766666f, AnimationHelper.createTranslationalVector(0f, -1.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -1440f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.6766666f, AnimationHelper.createTranslationalVector(0f, -1f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.6766666f, AnimationHelper.createTranslationalVector(0f, -1f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.4167667f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5834333f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.7083433f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(-1f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leftarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.4167667f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5834333f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.7083433f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("legs",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leftleg",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.9167666f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.0834333f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.1676667f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.2916767f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.3433333f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.4167667f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.4583433f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5416767f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5834333f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.625f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.6766667f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.7083433f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightleg",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.9167666f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.0834333f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.1676667f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.2916767f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.3433333f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.4167667f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.4583433f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5416767f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5834333f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.625f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.6766667f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.7083433f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation COPPER_PRESS_BUTTON_DOWN = Animation.Builder.create(2f)
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -0.2f, -1f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.7083433f, AnimationHelper.createTranslationalVector(0f, -0.2f, -1f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.7083433f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.7083433f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9167666f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.0834333f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.1676667f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.375f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.6766667f, AnimationHelper.createRotationalVector(-1f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leftarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9167666f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.0834333f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.1676667f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.375f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.6766667f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();

}
