// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class frost_crystal_big<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "frost_crystal_big"), "main");
	private final ModelPart bone8;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bone3;
	private final ModelPart bone4;
	private final ModelPart bone5;
	private final ModelPart bone6;
	private final ModelPart bone7;
	private final ModelPart bone10;
	private final ModelPart bone9;
	private final ModelPart bone11;
	private final ModelPart bone12;
	private final ModelPart bone13;
	private final ModelPart bone14;
	private final ModelPart bone15;
	private final ModelPart bone16;
	private final ModelPart bone17;
	private final ModelPart bone18;

	public frost_crystal_big(ModelPart root) {
		this.bone8 = root.getChild("bone8");
		this.bone = this.bone8.getChild("bone");
		this.bone2 = this.bone8.getChild("bone2");
		this.bone3 = this.bone8.getChild("bone3");
		this.bone4 = this.bone8.getChild("bone4");
		this.bone5 = this.bone8.getChild("bone5");
		this.bone6 = this.bone8.getChild("bone6");
		this.bone7 = this.bone8.getChild("bone7");
		this.bone10 = this.bone8.getChild("bone10");
		this.bone9 = this.bone10.getChild("bone9");
		this.bone11 = this.bone9.getChild("bone11");
		this.bone12 = this.bone11.getChild("bone12");
		this.bone13 = this.bone9.getChild("bone13");
		this.bone14 = this.bone13.getChild("bone14");
		this.bone15 = this.bone9.getChild("bone15");
		this.bone16 = this.bone15.getChild("bone16");
		this.bone17 = this.bone9.getChild("bone17");
		this.bone18 = this.bone17.getChild("bone18");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone8 = partdefinition.addOrReplaceChild("bone8", CubeListBuilder.create(), PartPose.offset(-3.0F, 24.0F, 0.0F));

		PartDefinition bone = bone8.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(28, 60).addBox(-3.5F, -17.5F, -3.5F, 7.0F, 17.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.5F, 2.5F, 0.0F, 0.0F, -0.2182F));

		PartDefinition bone2 = bone8.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(56, 60).addBox(-2.5F, -20.5F, -3.5F, 6.0F, 20.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.5F, 2.5F, 0.8516F, 0.1095F, 0.132F));

		PartDefinition bone3 = bone8.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(64, 0).addBox(-3.5F, -14.5F, -3.5F, 7.0F, 14.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 0.5F, 2.5F, -0.9904F, 0.1096F, 0.0201F));

		PartDefinition bone4 = bone8.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(0, 42).addBox(-3.5F, -20.5F, -3.5F, 7.0F, 20.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 0.5F, -3.5F, 0.5237F, 0.4653F, 0.6131F));

		PartDefinition bone5 = bone8.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(36, 0).addBox(-3.5F, -23.5F, -3.5F, 7.0F, 23.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 0.5F, -3.5F, -0.026F, 0.679F, -0.1312F));

		PartDefinition bone6 = bone8.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(36, 30).addBox(-3.5F, -23.5F, -3.5F, 7.0F, 23.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, 0.5F, -3.5F, -0.5215F, 0.616F, 0.4129F));

		PartDefinition bone7 = bone8.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -33.5F, -3.5F, 9.0F, 33.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, 0.5F, -0.5F, -0.2442F, 0.679F, -0.1312F));

		PartDefinition bone10 = bone8.addOrReplaceChild("bone10", CubeListBuilder.create().texOffs(64, 31).addBox(-2.0F, -13.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -19.0F, 6.0F));

		PartDefinition bone9 = bone10.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(64, 21).addBox(-4.0F, -2.0F, -1.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, -1.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition bone11 = bone9.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(64, 47).addBox(-4.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, 1.0F, 0.215F, -0.0376F, 0.6504F));

		PartDefinition bone12 = bone11.addOrReplaceChild("bone12", CubeListBuilder.create().texOffs(12, 69).addBox(-3.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, 0.0F, 0.2026F, -0.1359F, 1.1892F));

		PartDefinition bone13 = bone9.addOrReplaceChild("bone13", CubeListBuilder.create().texOffs(64, 51).addBox(-4.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, 6.0F, 1.2464F, 1.2035F, 1.2615F));

		PartDefinition bone14 = bone13.addOrReplaceChild("bone14", CubeListBuilder.create().texOffs(0, 73).addBox(-3.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, 0.0F, 0.1443F, -0.1968F, 1.5378F));

		PartDefinition bone15 = bone9.addOrReplaceChild("bone15", CubeListBuilder.create().texOffs(64, 55).addBox(0.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -1.0F, 6.0F, 1.2464F, -1.2035F, -1.2615F));

		PartDefinition bone16 = bone15.addOrReplaceChild("bone16", CubeListBuilder.create().texOffs(10, 73).addBox(0.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 0.0F, 0.0F, 0.2026F, 0.1359F, -1.1892F));

		PartDefinition bone17 = bone9.addOrReplaceChild("bone17", CubeListBuilder.create().texOffs(0, 69).addBox(0.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 6.0F, 1.7148F, -1.2197F, -1.7606F));

		PartDefinition bone18 = bone17.addOrReplaceChild("bone18", CubeListBuilder.create(), PartPose.offsetAndRotation(3.5F, 0.0F, 0.0F, 0.0512F, -0.0592F, -0.8317F));

		PartDefinition bone9_r1 = bone18.addOrReplaceChild("bone9_r1", CubeListBuilder.create().texOffs(76, 47).addBox(8.0F, -2.0F, 0.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, 1.0F, -1.0F, -0.1745F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone8.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}