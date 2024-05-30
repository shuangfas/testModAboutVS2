package com.example.examplemod.mixin;

import com.example.examplemod.ExampleMod;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.simibubi.create.foundation.utility.VecHelper.getCoordinate;

@Debug(export = true)
@Mixin(AirCurrent.class)
public abstract class AirCurrentMixin {
    @Shadow public AABB bounds;

    @Unique
    public AABB transformAABBToWorld(AABB aabb, Level level){
        Vec3 newMin = ExampleMod.toWorldVec3(level,new Vec3(aabb.minX,aabb.minY,aabb.minZ));
        Vec3 newMax = ExampleMod.toWorldVec3(level,new Vec3(aabb.maxX,aabb.maxY,aabb.maxZ));
        return new AABB(newMin,newMax);
    }
    @ModifyExpressionValue(method = "tickAffectedEntities",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;intersects(Lnet/minecraft/world/phys/AABB;)Z"),remap = false)
    public boolean removeAABBDetect(boolean original,@Local Entity entity){
        return true;
    }
    @Inject(method = "rebuild",at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;expandTowards(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;",shift = At.Shift.AFTER),@At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;move(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;",shift = At.Shift.AFTER)})
    public void transformAABBtoWorld(CallbackInfo ci,@Local Level world){
        this.bounds = transformAABBToWorld(this.bounds,world);
    }
    @Redirect(method = "tickAffectedEntities",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;alignedDistanceToFace(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)D"),remap = false)
    public double transformPosToWorld(Vec3 pos, BlockPos blockPos, Direction face,@Local(argsOnly = true) Level world){
        Direction.Axis axis = face.getAxis();
        Vec3 newPos = ExampleMod.toWorldVec3(world,ExampleMod.toVec3(blockPos));
        return Math.abs(getCoordinate(pos, axis) - (float)(newPos.get(axis) + (face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 0)));
    }

}
