package com.example.examplemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Map;

@Mixin(DispenserBlock.class)
public abstract class DispenseItemBehaviorMixin {
    @Shadow @Final private static Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY;
    private static final DispenseItemBehavior behavior=new OptionalDispenseItemBehavior() {
        @Override
        protected @NotNull ItemStack execute(BlockSource pSource, @NotNull ItemStack pStack) {
            this.setSuccess(true);
            Level level = pSource.getLevel();
            BlockPos selfPos = pSource.getPos();
            Direction direction = pSource.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = selfPos.relative(direction);
            if(level.getBlockState(blockpos).isAir()){
                Vec3 vec3From = toWorldPos(level,blockposToVec(blockpos));
                Vec3 vec3To = toWorldPos(level,blockposToVec(blockpos.relative(direction)));
                BlockHitResult blockHitResult = level.clip(new ClipContext(vec3From,vec3To,ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE,null));
                blockpos=blockHitResult.getBlockPos();
            }
            if (!BoneMealItem.growCrop(pStack, level, blockpos) && !BoneMealItem.growWaterPlant(pStack, level, blockpos, null)) {
                this.setSuccess(false);
            } else if (!level.isClientSide) {
                level.levelEvent(1505, blockpos, 0);
            }
            return pStack;
        }
    };
    @Inject(method = "registerBehavior", at = @At(value = "RETURN"))
    private static void registerBehavior(ItemLike pItem, DispenseItemBehavior pBehavior, CallbackInfo ci) {
        if(pItem.asItem().equals(Items.BONE_MEAL)){
            System.out.print("aaaa");
            DISPENSER_REGISTRY.put(pItem.asItem(),behavior);
        }
    }
    private static Vec3 toWorldPos(Level level,Vec3 vec3){
        Ship ship=VSGameUtilsKt.getShipManagingPos(level,vec3);
        if(ship!=null){
            return VSGameUtilsKt.toWorldCoordinates(ship,vec3);
        }
        return vec3;
    }
    private static Vec3 blockposToVec(BlockPos blockPos){
      return new Vec3(blockPos.getX(),blockPos.getY(),blockPos.getZ());
    }
    /**private static BlockPos addBlockpos(Direction direction,BlockPos blockPos){
        return new BlockPos(blockPos.getX() + direction.getStepX()*2, blockPos.getY() + direction.getStepY()*2, blockPos.getZ() + direction.getStepZ()*2);
    }**/
}
