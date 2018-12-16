package tschipp.subnauticredstone.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneMixin extends Block implements Waterloggable {

    public RedstoneMixin() {
        super(Block.Settings.of(Material.STONE));
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onInit(Block.Settings var1, CallbackInfo info) {
        this.setDefaultState(this.getDefaultState().with(Properties.WATERLOGGED, false));
     }

    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"), cancellable = true)
    public void onRenderState(BlockState var1, Direction var2, BlockState var3, IWorld var4, BlockPos var5, BlockPos var6, CallbackInfoReturnable<BlockState> info) {
        if ((Boolean)var1.get(Properties.WATERLOGGED)) {
           var4.getFluidTickScheduler().schedule(var5, Fluids.WATER, Fluids.WATER.method_15789(var4));
        }  
        BlockState state = info.getReturnValue();
        state.with(Properties.WATERLOGGED, var1.get(Properties.WATERLOGGED));
        info.setReturnValue(state);
        info.cancel();
     }

     @Inject(method = "appendProperties", at = @At("HEAD"))
     protected void onProps(StateFactory.Builder<Block, BlockState> var1, CallbackInfo info ) {
        var1.with(Properties.WATERLOGGED);
     }

     @Override
     public FluidState getFluidState(BlockState var1) {
        return (Boolean)var1.get(Properties.WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(var1);
     }

     @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
     public void onPlacementState(ItemPlacementContext var1, CallbackInfoReturnable<BlockState> info ) {     
        FluidState var3 = var1.getWorld().getFluidState(var1.getPos());
        BlockState state = info.getReturnValue().with(Properties.WATERLOGGED, var3.getFluid() == Fluids.WATER);

        info.setReturnValue(state);
        info.cancel();
    }
   
     


}