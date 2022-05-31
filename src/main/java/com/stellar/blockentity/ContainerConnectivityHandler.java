package com.stellar.blockentity;

import com.stellar.block.ContainerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

public class ContainerConnectivityHandler {

    public static void formContainers(ContainerBlockEntity containerBlockEntity) {
        ContainerConnectivityHandler.ContainerSearchCache cache = new ContainerConnectivityHandler.ContainerSearchCache();
        List<ContainerBlockEntity> frontier = new ArrayList<>();
        frontier.add(containerBlockEntity);
        formContainers(containerBlockEntity.getType(), containerBlockEntity.getWorld(), cache, frontier);
    }

    private static void formContainers(BlockEntityType<?> type, World world, ContainerConnectivityHandler.ContainerSearchCache cache, List<ContainerBlockEntity> frontier) {
        PriorityQueue<Pair<Integer, ContainerBlockEntity>> creationQueue = makeCreationQueue();
        Set<BlockPos> visited = new HashSet<>();

        int minY = Integer.MAX_VALUE;
        for (ContainerBlockEntity containerBlockEntity : frontier) {
            BlockPos pos = containerBlockEntity.getPos();
            minY = Math.min(pos.getY(), minY);
        }

        minY -= ContainerBlockEntity.getMaxRadius();

        while (!frontier.isEmpty()) {
            ContainerBlockEntity container = frontier.remove(0);
            BlockPos containerPos = container.getPos();
            if (visited.contains(containerPos))
                continue;

            visited.add(containerPos);

            int amount = tryToFormNewContainer(container, cache, true);
            if (amount > 1)
                creationQueue.add(Pair.of(amount, container));

            for (Direction.Axis axis : Iterate.axes) {
                Direction d = Direction.get(Direction.AxisDirection.NEGATIVE, axis);
                BlockPos next = containerPos.offset(d);

                if (next.getY() <= minY)
                    continue;
                if (visited.contains(next))
                    continue;
                ContainerBlockEntity nextContainer = containerAt(type, world, next);
                if (nextContainer == null)
                    continue;
                if (nextContainer.isRemoved())
                    continue;
                frontier.add(nextContainer);
            }
        }

        visited.clear();

        while (!creationQueue.isEmpty()) {
            Pair<Integer, ContainerBlockEntity> next = creationQueue.poll();
            ContainerBlockEntity toCreate = next.getValue();
            if (visited.contains(toCreate.getPos()))
                continue;
            visited.add(toCreate.getPos());
            tryToFormNewContainer(toCreate, cache, false);
        }

    }

    public static void splitContainer(ContainerBlockEntity containerBlockEntity) {
        splitContainerAndInvalidate(containerBlockEntity, null, false);
    }

    private static int tryToFormNewContainer(ContainerBlockEntity containerBlockEntity, ContainerConnectivityHandler.ContainerSearchCache cache, boolean simulate) {
        int bestWidth = 1;
        int bestAmount = -1;

        if (!containerBlockEntity.isController())
            return 0;

        for (int width = 1; width <= ContainerBlockEntity.getMaxRadius(); width++) {
            int amount = tryToFormNewContainerOfRadius(containerBlockEntity, width, cache, true);
            if (amount < bestAmount)
                continue;
            bestWidth = width;
            bestAmount = amount;
        }

        if (!simulate) {
            if (containerBlockEntity.radius == bestWidth && containerBlockEntity.radius * containerBlockEntity.radius * containerBlockEntity.length == bestAmount)
                return bestAmount;

            splitContainerAndInvalidate(containerBlockEntity, cache, false);
            tryToFormNewContainerOfRadius(containerBlockEntity, bestWidth, cache, simulate);
            containerBlockEntity.updateConnectivity = false;
            containerBlockEntity.radius = bestWidth;
            containerBlockEntity.length = bestAmount / bestWidth / bestWidth;

            BlockState state = containerBlockEntity.getCachedState();
            containerBlockEntity.markDirty();
        }
        return bestAmount;
    }

    private static int tryToFormNewContainerOfRadius(ContainerBlockEntity containerBlockEntity, int width, ContainerConnectivityHandler.ContainerSearchCache cache, boolean simulate) {
        int amount = 0;
        int height = 0;
        BlockEntityType<?> type = containerBlockEntity.getType();
        World world = containerBlockEntity.getWorld();
        BlockPos origin = containerBlockEntity.getPos();
        boolean alongZ = ContainerBlock.getContainerBlockAxis(containerBlockEntity.getCachedState()) == Direction.Axis.Z;

        Search:
        for (int yOffset = 0; yOffset < ContainerBlockEntity.getMaxLength(width); yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = alongZ ? origin.add(xOffset, zOffset, yOffset) : origin.add(yOffset, xOffset, zOffset);
                    Optional<ContainerBlockEntity> container = cache.getOrCache(type, world, pos);
                    if (!container.isPresent())
                        break Search;

                    ContainerBlockEntity controller = container.get();
                    int otherWidth = controller.radius;
                    if (otherWidth > width)
                        break Search;
                    if (otherWidth == width && controller.length == ContainerBlockEntity.getMaxLength(width))
                        break Search;
                    if ((ContainerBlock.getContainerBlockAxis(controller.getCachedState()) == Direction.Axis.Z) != alongZ)
                        break Search;

                    BlockPos controllerPos = controller.getPos();
                    if (!controllerPos.equals(origin)) {
                        if (alongZ && controllerPos.getX() < origin.getX())
                            break Search;
                        if (controllerPos.getY() < origin.getY())
                            break Search;
                        if (!alongZ && controllerPos.getZ() < origin.getZ())
                            break Search;
                        if (alongZ && controllerPos.getX() + otherWidth > origin.getX() + width)
                            break Search;
                        if (controllerPos.getY() + otherWidth > origin.getY() + width)
                            break Search;
                        if (!alongZ && controllerPos.getZ() + otherWidth > origin.getZ() + width)
                            break Search;
                    }

                }
            }

            amount += width * width;
            height++;
        }

        if (simulate)
            return amount;

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = alongZ ? origin.add(xOffset, zOffset, yOffset) : origin.add(yOffset, xOffset, zOffset);
                    ContainerBlockEntity container = containerAt(type, world, pos);
                    if (container == containerBlockEntity)
                        continue;

                    splitContainerAndInvalidate(container, cache, false);
                    container.setController(origin);
                    container.updateConnectivity = false;
                    cache.put(pos, containerBlockEntity);

                    BlockState state = world.getBlockState(pos);
                    if (!ContainerBlock.isContainer(state))
                        continue;
                    //state = state.with(ContainerBlock.LARGE, width > 2);
                    world.setBlockState(pos, state, 22);
                }
            }
        }

        return amount;
    }

    private static void splitContainerAndInvalidate(ContainerBlockEntity containerBlockEntity, @Nullable ContainerConnectivityHandler.ContainerSearchCache cache, boolean tryReconnect) {
        containerBlockEntity = containerBlockEntity.getControllerEntity();
        if (containerBlockEntity == null)
            return;

        int height = containerBlockEntity.length;
        int width = containerBlockEntity.radius;
        if (width == 1 && height == 1)
            return;

        World world = containerBlockEntity.getWorld();
        BlockPos origin = containerBlockEntity.getPos();
        BlockState state = containerBlockEntity.getCachedState();
        List<ContainerBlockEntity> frontier = new ArrayList<>();
        boolean alongZ = ContainerBlock.getContainerBlockAxis(state) == Direction.Axis.Z;

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = alongZ ? origin.add(xOffset, zOffset, yOffset) : origin.add(yOffset, xOffset, zOffset);
                    ContainerBlockEntity containerAt = containerAt(containerBlockEntity.getType(), world, pos);
                    if (containerAt == null)
                        continue;
                    if (!containerAt.getController()
                            .equals(origin))
                        continue;

                    containerAt.removeController();

                    if (tryReconnect) {
                        frontier.add(containerAt);
                        containerAt.updateConnectivity = false;
                    }
                    if (cache != null)
                        cache.put(pos, containerAt);
                }
            }
        }

        if (tryReconnect)
            formContainers(containerBlockEntity.getType(), world, cache == null ? new ContainerConnectivityHandler.ContainerSearchCache() : cache, frontier);
    }

    private static PriorityQueue<Pair<Integer, ContainerBlockEntity>> makeCreationQueue() {
        return new PriorityQueue<>(new Comparator<Pair<Integer, ContainerBlockEntity>>() {
            @Override
            public int compare(Pair<Integer, ContainerBlockEntity> o1, Pair<Integer, ContainerBlockEntity> o2) {
                return o2.getKey() - o1.getKey();
            }
        });
    }

    @Nullable
    public static ContainerBlockEntity containerAt(BlockEntityType<?> type, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ContainerBlockEntity && blockEntity.getType() == type)
            return (ContainerBlockEntity) blockEntity;
        return null;
    }

    @Nullable
    public static ContainerBlockEntity anyContainerAt(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ContainerBlockEntity)
            return (ContainerBlockEntity) blockEntity;
        return null;
    }

    private static class ContainerSearchCache {
        Map<BlockPos, Optional<ContainerBlockEntity>> controllerMap;

        public ContainerSearchCache() {
            controllerMap = new HashMap<>();
        }

        void put(BlockPos pos, ContainerBlockEntity target) {
            controllerMap.put(pos, Optional.of(target));
        }

        void putEmpty(BlockPos pos) {
            controllerMap.put(pos, Optional.empty());
        }

        boolean hasVisited(BlockPos pos) {
            return controllerMap.containsKey(pos);
        }

        Optional<ContainerBlockEntity> getOrCache(BlockEntityType<?> type, World world, BlockPos pos) {
            if (hasVisited(pos))
                return controllerMap.get(pos);
            ContainerBlockEntity containerAt = containerAt(type, world, pos);
            if (containerAt == null) {
                putEmpty(pos);
                return Optional.empty();
            }
            ContainerBlockEntity controller = containerAt.getControllerEntity();
            if (controller == null) {
                putEmpty(pos);
                return Optional.empty();
            }
            put(pos, controller);
            return Optional.of(controller);
        }

    }

    public static boolean isConnected(BlockRenderView world, BlockPos containerPos, BlockPos otherContainerPos) {
        BlockEntity blockEntity1 = world.getBlockEntity(containerPos);
        BlockEntity blockEntity2 = world.getBlockEntity(otherContainerPos);
        if (!(blockEntity1 instanceof ContainerBlockEntity) || !(blockEntity2 instanceof ContainerBlockEntity))
            return false;
        return ((ContainerBlockEntity) blockEntity1).getController().equals(((ContainerBlockEntity) blockEntity2).getController());
    }
}
