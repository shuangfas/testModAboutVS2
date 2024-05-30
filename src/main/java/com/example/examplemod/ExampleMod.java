package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mod(ExampleMod.MODID)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    private static final Logger LOGGER = LogUtils.getLogger();
    public ExampleMod()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    public static Vec3 toVec3(BlockPos blockPos){
        return new Vec3(blockPos.getX(),blockPos.getY(),blockPos.getZ());
    }
    public static Vec3 toWorldVec3(Level level,Vec3 vec3){
        Ship ship = VSGameUtilsKt.getShipManagingPos(level,vec3);
        if(ship!=null){
            return VSGameUtilsKt.toWorldCoordinates(ship,vec3);
        }
        return vec3;
    }
    public static Vec3 toWorldVec3(Ship ship,Vec3 vec3){
        if(ship!=null){
            return VSGameUtilsKt.toWorldCoordinates(ship,vec3);
        }
        return vec3;
    }
    public static Vec3 vec3Below(Vec3 vec3,double distance){
        Direction direction = Direction.DOWN;
        return new Vec3(vec3.x + direction.getStepX() * distance, vec3.y + direction.getStepY() * distance, vec3.z + direction.getStepZ() * distance);
    }
    public static Vec3 getFront(Direction direction,BlockPos blockPos){
        switch (direction){
            case EAST -> {
                return new Vec3(blockPos.getX()+1, blockPos.getY()+0.5, blockPos.getZ()+0.5);
            }
            case SOUTH -> {
                return new Vec3(blockPos.getX()+0.5,blockPos.getY()+0.5, blockPos.getZ()+1);
            }
            case WEST -> {
                return new Vec3(blockPos.getX(),blockPos.getY()+0.5,blockPos.getZ()+0.5);
            }
            case NORTH -> {
                return new Vec3(blockPos.getX()+0.5, blockPos.getY()+0.5,blockPos.getZ());
            }
            case UP -> {
                return new Vec3(blockPos.getX()+0.5, blockPos.getY()+1, blockPos.getZ()+0.5);
            }
            default -> {
                return new Vec3(blockPos.getX()+0.5, blockPos.getY(), blockPos.getZ()+0.5);
            }
        }
    }
    public static Vec3 toShipyardCoordinates(Ship ship,Vec3 vec3){
        Vector3d vector3d = ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(vec3));
        return VectorConversionsMCKt.toMinecraft(vector3d);
    }
    public static BlockPos vec3FloorToBlockpos(Vec3 vec3){
        return new BlockPos(new BlockPos((int)Math.floor(vec3.x),(int)Math.floor(vec3.y),(int)Math.floor(vec3.z)));
    }
}
