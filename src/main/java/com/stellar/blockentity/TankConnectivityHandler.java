package com.stellar.blockentity;

import com.stellar.block.TankBlock;
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

public class TankConnectivityHandler {

    public static void formTanks(TankBlockEntity tankBlockEntity) {
        TankSearchCache cache = new TankSearchCache();
        List<TankBlockEntity> frontier = new ArrayList<>();
        frontier.add(tankBlockEntity);
        formTanks(tankBlockEntity.getType(), tankBlockEntity.getWorld(), cache, frontier);
    }

    private static void formTanks(BlockEntityType<?> type, World world, TankSearchCache cache, List<TankBlockEntity> frontier) {
        PriorityQueue<Pair<Integer, TankBlockEntity>> creationQueue = makeCreationQueue();
        Set<BlockPos> visited = new HashSet<>();

        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        for (TankBlockEntity tankBlockEntity : frontier) {
            BlockPos pos = tankBlockEntity.getPos();
            minX = Math.min(pos.getX(), minX);
            minZ = Math.min(pos.getZ(), minZ);
        }
        minX -= TankBlockEntity.getMaxWidth();
        minZ -= TankBlockEntity.getMaxWidth();

        while (!frontier.isEmpty()) {
            TankBlockEntity tank = frontier.remove(0);
            BlockPos tankPos = tank.getPos();
            if (visited.contains(tankPos))
                continue;

            visited.add(tankPos);

            int amount = tryToFormNewTank(tank, cache, true);
            if (amount > 1)
                creationQueue.add(Pair.of(amount, tank));

            for (Direction.Axis axis : Iterate.axes) {
                Direction d = Direction.get(Direction.AxisDirection.NEGATIVE, axis);
                BlockPos next = tankPos.offset(d);

                if (next.getX() <= minX || next.getZ() <= minZ)
                    continue;
                if (visited.contains(next))
                    continue;
                TankBlockEntity nextTank = tankAt(type, world, next);
                if (nextTank == null)
                    continue;
                if (nextTank.isRemoved())
                    continue;
                frontier.add(nextTank);
            }
        }
        visited.clear();

        while (!creationQueue.isEmpty()) {
            Pair<Integer, TankBlockEntity> next = creationQueue.poll();
            TankBlockEntity toCreate = next.getValue();
            if (visited.contains(toCreate.getPos()))
                continue;
            visited.add(toCreate.getPos());
            tryToFormNewTank(toCreate, cache, false);
        }

    }

    public static void splitTank(TankBlockEntity tankBlockEntity) {
        splitTankAndInvalidate(tankBlockEntity, null, false);
    }

    private static int tryToFormNewTank(TankBlockEntity tankBlockEntity, TankSearchCache cache, boolean simulate) {
        int bestWidth = 1;
        int bestAmount = -1;

        if (!tankBlockEntity.isController())
            return 0;

        for (int width = 1; width <= TankBlockEntity.getMaxWidth(); width++) {
            int amount = tryToFormNewTankOfWidth(tankBlockEntity, width, cache, true);
            if (amount < bestAmount)
                continue;
            bestWidth = width;
            bestAmount = amount;
        }

        if (!simulate) {
            if (tankBlockEntity.width == bestWidth && tankBlockEntity.width * tankBlockEntity.width * tankBlockEntity.height == bestAmount)
                return bestAmount;

            splitTankAndInvalidate(tankBlockEntity, cache, false);
            tryToFormNewTankOfWidth(tankBlockEntity, bestWidth, cache, simulate);
            tankBlockEntity.updateConnectivity = false;
            tankBlockEntity.width = bestWidth;
            tankBlockEntity.height = bestAmount / bestWidth / bestWidth;

            BlockState state = tankBlockEntity.getCachedState();
            if (TankBlock.isTank(state)) {
                state = state.with(TankBlock.BOTTOM, true);
                state = state.with(TankBlock.TOP, tankBlockEntity.height == 1);
                tankBlockEntity.getWorld().setBlockState(tankBlockEntity.getPos(), state, 22);
            }

            tankBlockEntity.setWindows(tankBlockEntity.window);
            tankBlockEntity.markDirty();
        }
        return bestAmount;
    }

    private static int tryToFormNewTankOfWidth(TankBlockEntity tankBlockEntity, int width, TankSearchCache cache, boolean simulate) {
        int amount = 0;
        int height = 0;
        BlockEntityType<?> type = tankBlockEntity.getType();
        World world = tankBlockEntity.getWorld();
        BlockPos origin = tankBlockEntity.getPos();

        Search:
        for (int yOffset = 0; yOffset < TankBlockEntity.getMaxHeight(); yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = origin.add(xOffset, yOffset, zOffset);
                    Optional<TankBlockEntity> tank = cache.getOrCache(type, world, pos);
                    if (!tank.isPresent())
                        break Search;

                    TankBlockEntity controller = tank.get();
                    int otherWidth = controller.width;
                    if (otherWidth > width)
                        break Search;

                    BlockPos controllerPos = controller.getPos();
                    if (!controllerPos.equals(origin)) {
                        if (controllerPos.getX() < origin.getX())
                            break Search;
                        if (controllerPos.getZ() < origin.getZ())
                            break Search;
                        if (controllerPos.getX() + otherWidth > origin.getX() + width)
                            break Search;
                        if (controllerPos.getZ() + otherWidth > origin.getZ() + width)
                            break Search;
                    }
                }
            }

            amount += width * width;
            height++;
        }

        if (simulate)
            return amount;

        boolean opaque = false;

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = origin.add(xOffset, yOffset, zOffset);
                    TankBlockEntity tank = tankAt(type, world, pos);
                    if (tank == tankBlockEntity)
                        continue;

                    opaque |= !tank.window;

                    splitTankAndInvalidate(tank, cache, false);
                    tank.setController(origin);
                    tank.updateConnectivity = false;
                    cache.put(pos, tankBlockEntity);

                    BlockState state = world.getBlockState(pos);
                    if (!TankBlock.isTank(state))
                        continue;
                    state = state.with(TankBlock.BOTTOM, yOffset == 0);
                    state = state.with(TankBlock.TOP, yOffset == height - 1);
                    world.setBlockState(pos, state, 22);
                }
            }
        }
        tankBlockEntity.setWindows(!opaque);

        return amount;
    }

    private static void splitTankAndInvalidate(TankBlockEntity tankBlockEntity, @Nullable TankSearchCache cache, boolean tryReconnect) {
        tankBlockEntity = tankBlockEntity.getControllerEntity();
        if (tankBlockEntity == null)
            return;

        int height = tankBlockEntity.height;
        int width = tankBlockEntity.width;
        if (width == 1 && height == 1)
            return;

        World world = tankBlockEntity.getWorld();
        BlockPos origin = tankBlockEntity.getPos();
        List<TankBlockEntity> frontier = new ArrayList<>();

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = origin.add(xOffset, yOffset, zOffset);
                    TankBlockEntity tankAt = tankAt(tankBlockEntity.getType(), world, pos);
                    if (tankAt == null)
                        continue;
                    if (!tankAt.getController()
                            .equals(origin))
                        continue;
                    TankBlockEntity controllerTE = tankAt.getControllerEntity();
                    tankAt.window = controllerTE == null || controllerTE.window;
                    tankAt.removeController();

                    if (tryReconnect) {
                        frontier.add(tankAt);
                        tankAt.updateConnectivity = false;
                    }
                    if (cache != null)
                        cache.put(pos, tankAt);
                }
            }
        }

        if (tryReconnect)
            formTanks(tankBlockEntity.getType(), world, cache == null ? new TankSearchCache() : cache, frontier);
    }

    private static PriorityQueue<Pair<Integer, TankBlockEntity>> makeCreationQueue() {
        return new PriorityQueue<>(new Comparator<Pair<Integer, TankBlockEntity>>() {
            @Override
            public int compare(Pair<Integer, TankBlockEntity> o1, Pair<Integer, TankBlockEntity> o2) {
                return o2.getKey() - o1.getKey();
            }
        });
    }

    @Nullable
    public static TankBlockEntity tankAt(BlockEntityType<?> type, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TankBlockEntity && blockEntity.getType() == type)
            return (TankBlockEntity) blockEntity;
        return null;
    }

    @Nullable
    public static TankBlockEntity anyTankAt(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TankBlockEntity)
            return (TankBlockEntity) blockEntity;
        return null;
    }

    private static class TankSearchCache {
        Map<BlockPos, Optional<TankBlockEntity>> controllerMap;

        public TankSearchCache() {
            controllerMap = new HashMap<>();
        }

        void put(BlockPos pos, TankBlockEntity target) {
            controllerMap.put(pos, Optional.of(target));
        }

        void putEmpty(BlockPos pos) {
            controllerMap.put(pos, Optional.empty());
        }

        boolean hasVisited(BlockPos pos) {
            return controllerMap.containsKey(pos);
        }

        Optional<TankBlockEntity> getOrCache(BlockEntityType<?> type, World world, BlockPos pos) {
            if (hasVisited(pos))
                return controllerMap.get(pos);
            TankBlockEntity tankAt = tankAt(type, world, pos);
            if (tankAt == null) {
                putEmpty(pos);
                return Optional.empty();
            }
            TankBlockEntity controller = tankAt.getControllerEntity();
            if (controller == null) {
                putEmpty(pos);
                return Optional.empty();
            }
            put(pos, controller);
            return Optional.of(controller);
        }

    }

    public static boolean isConnected(BlockRenderView world, BlockPos tankPos, BlockPos otherTankPos) {
        BlockEntity blockEntity1 = world.getBlockEntity(tankPos);
        BlockEntity blockEntity2 = world.getBlockEntity(otherTankPos);
        if (!(blockEntity1 instanceof TankBlockEntity) || !(blockEntity2 instanceof TankBlockEntity))
            return false;
        return ((TankBlockEntity) blockEntity1).getController().equals(((TankBlockEntity) blockEntity2).getController());
    }
}
