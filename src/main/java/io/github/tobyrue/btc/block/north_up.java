public static VoxelShape makeShape() {
	return VoxelShapes.union(
		VoxelShapes.cuboid(0.25, 0.75, 0, 0.75, 1, 0.5)
	);
}