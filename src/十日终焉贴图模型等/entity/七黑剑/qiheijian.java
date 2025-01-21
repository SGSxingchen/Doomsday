// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class blue_point_sword_Converted<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "blue_point_sword_converted"), "main");
	private final ModelPart group2;
	private final ModelPart group3;
	private final ModelPart group4;
	private final ModelPart group5;
	private final ModelPart jinagsheng;
	private final ModelPart group6;
	private final ModelPart group;

	public blue_point_sword_Converted(ModelPart root) {
		this.group2 = root.getChild("group2");
		this.group3 = this.group2.getChild("group3");
		this.group4 = this.group3.getChild("group4");
		this.group5 = this.group4.getChild("group5");
		this.jinagsheng = this.group2.getChild("jinagsheng");
		this.group6 = this.jinagsheng.getChild("group6");
		this.group = this.group6.getChild("group");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition group2 = partdefinition.addOrReplaceChild("group2", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition group3 = group2.addOrReplaceChild("group3", CubeListBuilder.create().texOffs(0, 0).addBox(-0.65F, 3.2F, -1.0F, 1.3F, 0.8F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(6, 0).addBox(-0.5F, 2.7F, -0.7F, 1.0F, 0.5F, 1.4F, new CubeDeformation(0.0F))
		.texOffs(4, 9).addBox(-0.35F, 2.3F, -0.6F, 0.7F, 0.4F, 1.2F, new CubeDeformation(0.0F))
		.texOffs(10, 0).addBox(-0.25F, 2.1F, -0.3F, 0.5F, 0.2F, 0.6F, new CubeDeformation(0.0F))
		.texOffs(0, 3).addBox(-0.4F, 4.8F, -0.4F, 0.8F, 3.8F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(0, 10).addBox(-0.25F, 7.7F, -0.25F, 0.5F, 2.2F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(6, 10).addBox(-0.5F, 8.6F, -0.35F, 0.2F, 0.5F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(4, 10).addBox(0.3F, 8.6F, -0.35F, 0.2F, 0.5F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(12, 5).addBox(-0.35F, 8.6F, 0.3F, 0.7F, 0.5F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(12, 4).addBox(-0.35F, 8.6F, -0.5F, 0.7F, 0.5F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(12, 6).addBox(-0.3F, 9.1F, 0.3F, 0.6F, 0.5F, 0.1F, new CubeDeformation(0.0F))
		.texOffs(6, 12).addBox(-0.3F, 9.1F, -0.4F, 0.6F, 0.5F, 0.1F, new CubeDeformation(0.0F))
		.texOffs(12, 2).addBox(-0.4F, 9.1F, -0.3F, 0.1F, 0.5F, 0.6F, new CubeDeformation(0.0F))
		.texOffs(10, 10).addBox(0.3F, 9.1F, -0.3F, 0.1F, 0.5F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = group3.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.75F, -0.2F, -0.2F, 1.5F, 0.4F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.7F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition group4 = group3.addOrReplaceChild("group4", CubeListBuilder.create().texOffs(8, 11).addBox(-0.3F, 1.7163F, -2.6061F, 0.6F, 0.7F, 0.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r2 = group4.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(4, 3).addBox(-0.5F, -0.4F, 0.05F, 1.1F, 0.8F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.05F, 4.1273F, -0.7179F, -0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r3 = group4.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(8, 2).addBox(-0.5F, 0.05F, -0.4F, 1.1F, 1.1F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.05F, 3.42F, -1.3551F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r4 = group4.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(4, 7).addBox(-0.65F, -0.4F, -0.1F, 1.2F, 0.8F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.05F, 3.2478F, -1.7708F, -0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r5 = group4.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(10, 1).addBox(-0.45F, -0.3F, -0.5F, 0.8F, 1.1F, 0.1F, new CubeDeformation(0.0F))
		.texOffs(8, 6).addBox(-0.55F, -0.3F, -0.4F, 1.0F, 1.3F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.05F, 2.5021F, -2.0294F, 0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r6 = group4.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(12, 1).addBox(-0.3F, -0.35F, -0.1F, 0.6F, 0.7F, 0.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0345F, -2.2294F, 0.7854F, 0.0F, 0.0F));

		PartDefinition group5 = group4.addOrReplaceChild("group5", CubeListBuilder.create().texOffs(4, 12).addBox(-0.3F, 1.7163F, 2.4061F, 0.6F, 0.7F, 0.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r7 = group5.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(4, 5).addBox(-0.5F, -0.4F, -1.45F, 1.1F, 0.8F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.05F, 4.1273F, 0.7179F, 0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r8 = group5.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(8, 4).addBox(-0.5F, 0.05F, -0.4F, 1.1F, 1.1F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.05F, 3.42F, 1.3551F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r9 = group5.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 8).addBox(-0.65F, -0.4F, -1.0F, 1.2F, 0.8F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.05F, 3.2478F, 1.7708F, 0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r10 = group5.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(6, 2).addBox(-0.45F, -0.3F, 0.4F, 0.8F, 1.1F, 0.1F, new CubeDeformation(0.0F))
		.texOffs(8, 8).addBox(-0.55F, -0.3F, -0.4F, 1.0F, 1.3F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.05F, 2.5021F, 2.0294F, -0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r11 = group5.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(8, 10).addBox(-0.3F, -0.35F, -0.1F, 0.6F, 0.7F, 0.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0345F, 2.2294F, -0.7854F, 0.0F, 0.0F));

		PartDefinition jinagsheng = group2.addOrReplaceChild("jinagsheng", CubeListBuilder.create().texOffs(0, 0).addBox(-0.2326F, -16.0F, -0.9135F, 0.2F, 19.3F, 0.2F, new CubeDeformation(0.0F)), PartPose.offset(0.1326F, 0.0F, 0.8135F));

		PartDefinition group6 = jinagsheng.addOrReplaceChild("group6", CubeListBuilder.create().texOffs(0, 0).addBox(-8.3F, -23.4F, 6.2F, 0.2F, 18.7F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -22.8F, 5.8F, 0.2F, 18.1F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -23.7F, 6.4F, 0.2F, 19.0F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -22.5F, 5.6F, 0.2F, 17.8F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -21.8F, 5.2F, 0.2F, 17.1F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -22.2F, 5.4F, 0.2F, 17.5F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -21.4F, 5.0F, 0.2F, 16.7F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -23.1F, 6.0F, 0.2F, 18.4F, 0.2F, new CubeDeformation(0.0F)), PartPose.offset(8.0674F, 8.0F, -7.5135F));

		PartDefinition group = group6.addOrReplaceChild("group", CubeListBuilder.create().texOffs(0, 0).addBox(-8.3F, -23.4F, 7.0F, 0.2F, 18.7F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -22.8F, 7.4F, 0.2F, 18.1F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -23.7F, 6.8F, 0.2F, 19.0F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -22.5F, 7.6F, 0.2F, 17.8F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -21.8F, 8.0F, 0.2F, 17.1F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -22.2F, 7.8F, 0.2F, 17.5F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -21.4F, 8.2F, 0.2F, 16.7F, 0.2F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.3F, -23.1F, 7.2F, 0.2F, 18.4F, 0.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		group2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}