package tschipp.subnauticredstone.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.WeightedPressurePlateBlock;
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

@Mixin(WeightedPressurePlateBlock.class)
public abstract class WeightedPressurePlateMixin extends Block implements Waterloggable {

    private static final BooleanProperty waterlogged = Properties.WATERLOGGED;

    public WeightedPressurePlateMixin() {
        super(Block.Settings.of(Material.STONE));
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onInit(int var1, Block.Settings var2, CallbackInfo info) {
        this.setDefaultState(this.getDefaultState().with(waterlogged, false));
     }

   //  @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
   //  public void onRenderState(BlockState var1, Direction var2, BlockState var3, IWorld var4, BlockPos var5, BlockPos var6, CallbackInfoReturnable<BlockState> info) {
   //      BlockState state = info.getReturnValue();
   //      if(state != null && !state.isAir())
   //      {
   //      state.with(waterlogged, var1.get(waterlogged));
   //      info.setReturnValue(state);
   //      info.cancel();
   //      }
   //   }

   @Override
     public BlockState getStateForNeighborUpdate(BlockState state, Direction var2, BlockState var3, IWorld var4, BlockPos var5,
           BlockPos var6) {
            if(state != null && !state.isAir())
             {
            state.with(waterlogged, state.get(waterlogged));
            return state;
            }
            return state;
      }

      @Override
      public BlockState getPlacementState(ItemPlacementContext var1) {
          FluidState var3 = var1.getWorld().getFluidState(var1.getPos());
          BlockState state = super.getPlacementState(var1);
          return  state.with(waterlogged, var3.getFluid() == Fluids.WATER);
     }


     @Inject(method = "appendProperties", at = @At("HEAD"))
     protected void onProps(StateFactory.Builder<Block, BlockState> var1, CallbackInfo info ) {
        var1.with(waterlogged);
     }

     @Override
     public FluidState getFluidState(BlockState var1) {
        return (Boolean)var1.get(waterlogged) ? Fluids.WATER.getState(false) : super.getFluidState(var1);
     }

     @Override
     public void neighborUpdate(BlockState state, World world, BlockPos thispos, Block var4, BlockPos otherpos) {
      super.neighborUpdate(state, world, thispos, var4, otherpos);
         BlockPos offset = thispos.offset(Direction.DOWN);
         Material mat = world.getBlockState(offset).getMaterial();
         
         if (mat == Material.AIR || mat == Material.WATER)
             world.breakBlock(thispos, true);
     }


   //   @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
   //   public void onPlacementState(ItemPlacementContext var1, CallbackInfoReturnable<BlockState> info ) {     
   //      FluidState var3 = var1.getWorld().getFluidState(var1.getPos());
   //      BlockState state = info.getReturnValue().with(waterlogged, var3.getFluid() == Fluids.WATER);

   //      info.setReturnValue(state);
   //      info.cancel();
   //  }
    
     


}