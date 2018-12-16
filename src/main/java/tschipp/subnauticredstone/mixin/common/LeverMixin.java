package tschipp.subnauticredstone.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.Material;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

@Mixin(LeverBlock.class)
public abstract class LeverMixin extends Block implements Waterloggable {

    private static final BooleanProperty waterlogged = Properties.WATERLOGGED;

    public LeverMixin() {
        super(Block.Settings.of(Material.STONE));
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onInit(Block.Settings var1, CallbackInfo info) {
        this.setDefaultState(this.getDefaultState().with(waterlogged, false));
    }

    // @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"), cancellable
    // = true)
    // public void onRenderState(BlockState var1, Direction var2, BlockState var3,
    // IWorld var4, BlockPos var5, BlockPos var6, CallbackInfoReturnable<BlockState>
    // info) {
    // BlockState state = info.getReturnValue();
    // if(state != null && !state.isAir())
    // {
    // state.with(waterlogged, var1.get(waterlogged));
    // info.setReturnValue(state);
    // info.cancel();
    // }
    // }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction var2, BlockState var3, IWorld var4,
            BlockPos var5, BlockPos var6) {
        if (state != null && !state.isAir()) {
            state.with(waterlogged, state.get(waterlogged));
            return state;
        }
        return state;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext var1) {
        FluidState var3 = var1.getWorld().getFluidState(var1.getPos());
        BlockState state = super.getPlacementState(var1);
        return state.with(waterlogged, var3.getFluid() == Fluids.WATER);
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    protected void onProps(StateFactory.Builder<Block, BlockState> var1, CallbackInfo info) {
        var1.with(waterlogged);
    }

    @Override
    public FluidState getFluidState(BlockState var1) {
        return (Boolean) var1.get(waterlogged) ? Fluids.WATER.getState(false) : super.getFluidState(var1);
    }

   

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos thispos, Block var4, BlockPos otherpos) {
       super.neighborUpdate(state, world, thispos, var4, otherpos);
       WallMountLocation loc = state.get(Properties.WALL_MOUNT_LOCAITON);
 
       BlockPos offset = null;
 
       switch (loc) {
       case WALL:
          offset = thispos.offset(state.get(Properties.FACING_HORIZONTAL).getOpposite());
          break;
       case CEILING:
          offset = thispos.offset(Direction.UP);
          break;
       case FLOOR:
          offset = thispos.offset(Direction.DOWN);
          break;
       }
 
       Material mat = world.getBlockState(offset).getMaterial();
 
       if (mat == Material.AIR || mat == Material.WATER)
          world.breakBlock(thispos, true);
    }

    // @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    // public void onPlacementState(ItemPlacementContext var1,
    // CallbackInfoReturnable<BlockState> info ) {
    // FluidState var3 = var1.getWorld().getFluidState(var1.getPos());
    // BlockState state = info.getReturnValue().with(waterlogged, var3.getFluid() ==
    // Fluids.WATER);

    // info.setReturnValue(state);
    // info.cancel();
    // }

    // @Inject(method = "neighborUpdate", at = @At("HEAD"))
    // public void neighborUpdate(BlockState var1, World var2, BlockPos var3, Block
    // var4, BlockPos var5, CallbackInfo info) {
    // if (!var2.isRemote) {
    // if ((Boolean)var1.get(waterlogged)) {
    // var2.getFluidTickScheduler().schedule(var3, Fluids.WATER,
    // Fluids.WATER.method_15789(var2));
    // }
    // }

    // }

}