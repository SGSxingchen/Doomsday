// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class vulcan<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "vulcan"), "main");
	private final ModelPart all;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bone3;
	private final ModelPart bone4;
	private final ModelPart head;
	private final ModelPart bone19;
	private final ModelPart bone20;
	private final ModelPart bone21;
	private final ModelPart bone22;
	private final ModelPart bone25;
	private final ModelPart bone26;
	private final ModelPart bone23;
	private final ModelPart bone24;
	private final ModelPart bone15;
	private final ModelPart bone16;
	private final ModelPart ZUO;
	private final ModelPart bone11;
	private final ModelPart bone12;
	private final ModelPart bone13;
	private final ModelPart bone14;
	private final ModelPart YOU;
	private final ModelPart bone6;
	private final ModelPart bone7;
	private final ModelPart bone8;
	private final ModelPart bone9;
	private final ModelPart H;

	public vulcan(ModelPart root) {
		this.all = root.getChild("all");
		this.bone = this.all.getChild("bone");
		this.bone2 = this.all.getChild("bone2");
		this.bone3 = this.bone2.getChild("bone3");
		this.bone4 = this.bone3.getChild("bone4");
		this.head = this.bone4.getChild("head");
		this.bone19 = this.head.getChild("bone19");
		this.bone20 = this.bone19.getChild("bone20");
		this.bone21 = this.head.getChild("bone21");
		this.bone22 = this.bone21.getChild("bone22");
		this.bone25 = this.head.getChild("bone25");
		this.bone26 = this.bone25.getChild("bone26");
		this.bone23 = this.head.getChild("bone23");
		this.bone24 = this.bone23.getChild("bone24");
		this.bone15 = this.bone4.getChild("bone15");
		this.bone16 = this.bone4.getChild("bone16");
		this.ZUO = this.all.getChild("ZUO");
		this.bone11 = this.ZUO.getChild("bone11");
		this.bone12 = this.bone11.getChild("bone12");
		this.bone13 = this.bone12.getChild("bone13");
		this.bone14 = this.bone13.getChild("bone14");
		this.YOU = this.all.getChild("YOU");
		this.bone6 = this.YOU.getChild("bone6");
		this.bone7 = this.bone6.getChild("bone7");
		this.bone8 = this.bone7.getChild("bone8");
		this.bone9 = this.bone8.getChild("bone9");
		this.H = root.getChild("H");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 29.0F, 14.0F));

		PartDefinition bone = all.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(50, 25).addBox(-5.5F, -1.5F, -3.5F, 11.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -10.5F, 3.5F, 0.3491F, 0.0F, 0.0F));

		PartDefinition bone2 = all.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 35).addBox(-6.0F, -4.0F, -5.0F, 12.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -11.5F, 3.5F));

		PartDefinition bone3 = bone2.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 14).addBox(-7.0F, -8.0F, -6.0F, 14.0F, 10.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition bone4 = bone3.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -3.0F, -6.0F, 18.0F, 3.0F, 11.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition head = bone4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 51).addBox(-4.0F, -7.0F, -5.0F, 8.0F, 7.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition bone19 = head.addOrReplaceChild("bone19", CubeListBuilder.create().texOffs(0, 18).addBox(-1.5F, -1.0F, 0.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -1.0F, 4.0F, 0.4232F, -0.1096F, 0.2382F));

		PartDefinition bone20 = bone19.addOrReplaceChild("bone20", CubeListBuilder.create().texOffs(39, 18).addBox(0.0F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -0.5F, 3.0F, 0.5672F, 0.0F, 0.0F));

		PartDefinition bone21 = head.addOrReplaceChild("bone21", CubeListBuilder.create().texOffs(0, 14).addBox(-0.5F, -1.0F, 0.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -1.0F, 4.0F, 0.4232F, 0.1096F, -0.2382F));

		PartDefinition bone22 = bone21.addOrReplaceChild("bone22", CubeListBuilder.create().texOffs(39, 14).addBox(-1.0F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -0.5F, 3.0F, 0.5672F, 0.0F, 0.0F));

		PartDefinition bone25 = head.addOrReplaceChild("bone25", CubeListBuilder.create().texOffs(0, 4).addBox(-1.5F, -1.0F, 0.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -1.0F, -3.0F, 2.4718F, -0.8532F, -2.3162F));

		PartDefinition bone26 = bone25.addOrReplaceChild("bone26", CubeListBuilder.create().texOffs(0, 39).addBox(0.0F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -0.5F, 3.0F, 0.5672F, 0.0F, 0.0F));

		PartDefinition bone23 = head.addOrReplaceChild("bone23", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, 0.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.0F, -3.0F, 2.4718F, 0.8532F, 2.3162F));

		PartDefinition bone24 = bone23.addOrReplaceChild("bone24", CubeListBuilder.create().texOffs(0, 35).addBox(-1.0F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -0.5F, 3.0F, 0.5672F, 0.0F, 0.0F));

		PartDefinition bone15 = bone4.addOrReplaceChild("bone15", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.5F, 4.0F, -7.0F, -0.0869F, 0.0874F, 0.0038F));

		PartDefinition bone15_r1 = bone15.addOrReplaceChild("bone15_r1", CubeListBuilder.create().texOffs(18, 82).addBox(-4.0F, -2.0F, -1.0F, 7.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0086F, -5.0F, -0.1305F, 0.2616F, 0.0113F, -0.0421F));

		PartDefinition bone16 = bone4.addOrReplaceChild("bone16", CubeListBuilder.create(), PartPose.offsetAndRotation(3.5F, 4.0F, -7.0F, -0.0869F, -0.0874F, -0.0038F));

		PartDefinition bone16_r1 = bone16.addOrReplaceChild("bone16_r1", CubeListBuilder.create().texOffs(0, 79).addBox(-3.0F, -2.0F, -1.0F, 7.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0086F, -5.0F, -0.1305F, 0.2616F, -0.0113F, 0.0421F));

		PartDefinition ZUO = all.addOrReplaceChild("ZUO", CubeListBuilder.create().texOffs(58, 51).addBox(-7.0F, -3.0F, -5.0F, 8.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.5F, -28.5F, 3.5F, 0.0F, 0.0F, 0.3054F));

		PartDefinition bone11 = ZUO.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(59, 64).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 1.0F, 0.0F, -0.2164F, 0.0283F, 0.1278F));

		PartDefinition bone12 = bone11.addOrReplaceChild("bone12", CubeListBuilder.create().texOffs(33, 40).addBox(-4.0F, -1.0F, -5.0F, 8.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition bone13 = bone12.addOrReplaceChild("bone13", CubeListBuilder.create().texOffs(41, 76).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition bone14 = bone13.addOrReplaceChild("bone14", CubeListBuilder.create().texOffs(79, 26).addBox(-2.5F, -0.5F, -6.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(47, 0).addBox(-4.5F, -1.5F, -8.0F, 9.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(74, 0).addBox(-4.0F, -1.0F, -13.0F, 8.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(58, 39).addBox(-3.5F, -1.0F, -19.8F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.2F))
		.texOffs(33, 35).addBox(-3.0F, -0.5F, -23.55F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(50, 64).addBox(-2.5F, -0.5F, -25.55F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(59, 76).addBox(-1.5F, -0.5F, -28.55F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition YOU = all.addOrReplaceChild("YOU", CubeListBuilder.create().texOffs(25, 60).addBox(-1.0F, -3.0F, -5.0F, 8.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.5F, -28.5F, 3.5F, 0.0F, 0.0F, -0.3054F));

		PartDefinition bone6 = YOU.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(0, 67).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 1.0F, 0.0F, -0.2164F, -0.0283F, -0.1278F));

		PartDefinition bone7 = bone6.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(49, 5).addBox(-4.0F, -1.0F, -5.0F, 8.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition bone8 = bone7.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(65, 76).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition bone9 = bone8.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(79, 39).addBox(-2.5F, -0.5F, 9.85F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(78, 64).addBox(-4.5F, -1.5F, 7.85F, 9.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(74, 7).addBox(-4.0F, -1.0F, 2.85F, 8.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(19, 73).addBox(-3.5F, -1.0F, -3.95F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.2F))
		.texOffs(79, 21).addBox(-3.0F, -0.5F, -7.7F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(67, 48).addBox(-2.5F, -0.5F, -9.7F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(83, 14).addBox(-1.5F, -0.5F, -12.7F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -15.85F, 0.0F, 0.0F, -0.5236F));

		PartDefinition H = partdefinition.addOrReplaceChild("H", CubeListBuilder.create().texOffs(80, 95).addBox(1.0F, -2.0F, 1.0F, 0.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -4.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		all.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		H.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}