public static VoxelShape makeShape() {
	return VoxelShapes.union(
		VoxelShapes.cuboid(0.5, 0, 0, 1, 1, 1),
		VoxelShapes.cuboid(0, 0.25, 0.25, 0.5, 0.75, 0.75)
	);
}