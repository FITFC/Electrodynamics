package physica.core.common.tile;

import net.minecraft.tileentity.TileEntity;
import physica.api.core.abstraction.AbstractionLayer;
import physica.api.core.abstraction.FaceDirection;
import physica.api.core.electricity.IElectricityHandler;
import physica.api.core.tile.ITileBasePowered;
import physica.core.common.event.FulminationEventHandler;
import physica.library.tile.TileBase;

public class TileFulmination extends TileBase implements ITileBasePowered, IElectricityHandler {

	public static int	MAX_ENERGY_STORED	= 500000;
	private int			energyStored;

	@Override
	public int getElectricityStored()
	{
		return energyStored;
	}

	@Override
	public void setElectricityStored(int energy)
	{
		energyStored = Math.min(energy, MAX_ENERGY_STORED);
	}

	@Override
	public void updateServer(int ticks)
	{
		super.updateServer(ticks);
		if (!FulminationEventHandler.INSTANCE.isRegistered(this))
		{
			FulminationEventHandler.INSTANCE.register(this);
		}
		for (FaceDirection dir : FaceDirection.VALID_DIRECTIONS)
		{
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if (tile != null)
			{
				if (AbstractionLayer.Electricity.isElectricReceiver(tile))
				{
					if (AbstractionLayer.Electricity.canConnectElectricity(tile, dir.getOpposite()))
					{
						AbstractionLayer.Electricity.receiveElectricity(tile, dir.getOpposite(), Math.min(5000, energyStored), false);
					}
				}
			}
		}
	}

	@Override
	public int getElectricCapacity(FaceDirection from)
	{
		return MAX_ENERGY_STORED;
	}

	@Override
	public boolean canConnectElectricity(FaceDirection from)
	{
		return true;
	}

	@Override
	public int extractElectricity(FaceDirection from, int maxExtract, boolean simulate)
	{
		if (!simulate)
		{
			energyStored -= Math.min(5000, energyStored);
		}
		return Math.min(5000, energyStored);
	}

	@Override
	public int getElectricityUsage()
	{
		return 0;
	}

	@Override
	public int receiveElectricity(FaceDirection from, int maxReceive, boolean simulate)
	{
		return 0;
	}

	@Override
	public int getElectricityStored(FaceDirection from)
	{
		return ITileBasePowered.super.getElectricityStored(from);
	}
}
