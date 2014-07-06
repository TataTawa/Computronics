package pl.asie.computronics.tile;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SimpleComponent;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.Network;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.RadarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

@Optional.Interface(iface = "li.cil.li.oc.network.Environment", modid = "OpenComputers")
public class TileRadar extends TileEntityPeripheralBase implements Environment {

	public static final double EnergyCostPerOperation = Computronics.RADAR_ENERGY_COST;
	protected boolean hasEnergy;
	protected boolean isEnabled = true;
	
	public TileRadar() {
		super("radar");
	}
   
    private AxisAlignedBB getBounds(Arguments args) {
    	if(args.isInteger(0)) {
    		return getBounds(args.checkInteger(0));
    	} else return getBounds(Computronics.RADAR_RANGE);
    }
    
    private AxisAlignedBB getBounds(int d) {
    	int distance = Math.min(d, Computronics.RADAR_RANGE);
    	if(distance < 1) distance = 1;
    	return AxisAlignedBB.
                getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).
                expand(distance, distance, distance);
    }

    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] getEntities(Context context, Arguments args) {
		List<Map> entities = new ArrayList<Map>();
		if(hasEnergy) {
			AxisAlignedBB bounds = getBounds(args);
			entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityPlayer.class));
			entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityLiving.class));
			context.pause(0.5);
			
			// Suck some power
			hasEnergy = ((Connector) node).tryChangeBuffer(-EnergyCostPerOperation * 2);
		}
		// The returned array is treated as a tuple, meaning if we return the
		// entities as an array directly, we'd end up with each entity as an
		// individual result value (i.e. in Lua we'd have to write
		//   result = {radar.getEntities()}
		// and we'd be limited in the number of entities, due to the limit of
		// return values. So we wrap it in an array to return it as a list.
		return new Object[]{entities.toArray()};
    }
	
	@Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] getPlayers(Context context, Arguments args) {
        List<Map> entities = new ArrayList<Map>();
		if(hasEnergy) {
			AxisAlignedBB bounds = getBounds(args);
			entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityPlayer.class));
			context.pause(0.5);
			// Suck some power
			hasEnergy = ((Connector) node).tryChangeBuffer(-EnergyCostPerOperation);
		}
        return new Object[]{entities.toArray()};
    }
	
	@Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] getMobs(Context context, Arguments args) {
        List<Map> entities = new ArrayList<Map>();
		if(hasEnergy) {
			AxisAlignedBB bounds = getBounds(args);
			entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityLiving.class));
			context.pause(0.5);
			// Suck some power
			hasEnergy = ((Connector) node).tryChangeBuffer(-EnergyCostPerOperation);
		}
        return new Object[]{entities.toArray()};
    }

	@Override
    @Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
}