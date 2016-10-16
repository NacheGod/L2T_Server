/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package l2server.gameserver.model;

import l2server.Config;
import l2server.gameserver.datatables.ItemTable;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.itemcontainer.PcInventory;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.*;
import l2server.gameserver.network.serverpackets.L2ItemListPacket.ItemInstanceInfo;
import l2server.gameserver.templates.item.L2Item;
import l2server.gameserver.util.Util;
import l2server.log.Log;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static l2server.gameserver.model.itemcontainer.PcInventory.MAX_ADENA;

/**
 * @author Advi
 */
public class TradeList
{
	public static class TradeItem implements ItemInstanceInfo
	{
		private int objectId;
		private final L2Item item;
		private int location;
		private int enchant;
		private int type1;
		private int type2;
		private long count;
		private long storeCount;
		private Map<L2Item, Long> priceItems = new HashMap<>();
		private long price;
		private boolean isSoulEnhanced;
		private int[] ensoulEffectIds;
		private int[] ensoulSpecialEffectIds;
		private boolean isAugmented;
		private long augmentationId;
		private boolean elemEnchanted = false;
		private final byte elemAtkType;
		private final int elemAtkPower;
		private int[] elemDefAttr = {0, 0, 0, 0, 0, 0};
		private int appearance;

		public TradeItem(L2ItemInstance item, long count, long price)
		{
			this.objectId = item.getObjectId();
			this.item = item.getItem();
			this.location = item.getLocationSlot();
			this.enchant = item.getEnchantLevel();
			this.type1 = item.getCustomType1();
			this.type2 = item.getCustomType2();
			this.count = count;
			this.price = price;
			this.isSoulEnhanced = item.isSoulEnhanced();
			this.ensoulEffectIds = item.getEnsoulEffectIds();
			this.ensoulSpecialEffectIds = item.getEnsoulSpecialEffectIds();
			if (item.isAugmented())
			{
				this.isAugmented = true;
				this.augmentationId = item.getAugmentation().getId();
			}
			else
			{
				this.isAugmented = false;
			}
			this.elemAtkType = item.getAttackElementType();
			this.elemAtkPower = item.getAttackElementPower();
			if (this.elemAtkPower > 0)
			{
				this.elemEnchanted = true;
			}
			for (byte i = 0; i < 6; i++)
			{
				this.elemDefAttr[i] = item.getElementDefAttr(i);
				if (this.elemDefAttr[i] > 0)
				{
					this.elemEnchanted = true;
				}
			}
			this.appearance = item.getAppearance();
		}

		public TradeItem(L2Item item, long count, long price)
		{
			this.objectId = 0;
			this.item = item;
			this.location = 0;
			this.enchant = 0;
			this.type1 = 0;
			this.type2 = 0;
			this.count = count;
			this.storeCount = count;
			this.price = price;
			this.elemEnchanted = false;
			this.elemAtkType = Elementals.NONE;
			this.elemAtkPower = 0;
			this.appearance = 0;
		}

		public TradeItem(TradeItem item, long count, long price)
		{
			this.objectId = item.getObjectId();
			this.item = item.getItem();
			this.location = item.getLocationSlot();
			this.enchant = item.getEnchantLevel();
			this.type1 = item.getCustomType1();
			this.type2 = item.getCustomType2();
			this.count = count;
			this.storeCount = count;
			this.price = price;
			this.elemAtkType = item.getAttackElementType();
			this.elemAtkPower = item.getAttackElementPower();
			if (this.elemAtkPower > 0)
			{
				this.elemEnchanted = true;
			}
			for (byte i = 0; i < 6; i++)
			{
				this.elemDefAttr[i] = item.getElementDefAttr(i);
				if (this.elemDefAttr[i] > 0)
				{
					this.elemEnchanted = true;
				}
			}
		}

		public void setObjectId(int objectId)
		{
			this.objectId = objectId;
		}

		@Override
		public int getObjectId()
		{
			return this.objectId;
		}

		@Override
		public L2Item getItem()
		{
			return this.item;
		}

		@Override
		public int getLocationSlot()
		{
			return this.location;
		}

		public void setEnchant(int enchant)
		{
			this.enchant = enchant;
		}

		@Override
		public int getEnchantLevel()
		{
			return this.enchant;
		}

		public int getCustomType1()
		{
			return this.type1;
		}

		public int getCustomType2()
		{
			return this.type2;
		}

		public void setCount(long count)
		{
			this.count = count;
		}

		@Override
		public long getCount()
		{
			return this.count;
		}

		public long getStoreCount()
		{
			return this.storeCount;
		}

		public void setPrice(long price)
		{
			this.price = price;
		}

		public long getPrice()
		{
			return this.price;
		}

		@Override
		public boolean isSoulEnhanced()
		{
			return this.isSoulEnhanced;
		}

		@Override
		public int[] getEnsoulEffectIds()
		{
			return this.ensoulEffectIds;
		}

		@Override
		public int[] getEnsoulSpecialEffectIds()
		{
			return this.ensoulSpecialEffectIds;
		}

		@Override
		public boolean isAugmented()
		{
			return this.isAugmented;
		}

		@Override
		public long getAugmentationBonus()
		{
			return this.augmentationId;
		}

		@Override
		public boolean isElementEnchanted()
		{
			return this.elemEnchanted;
		}

		@Override
		public byte getAttackElementType()
		{
			return this.elemAtkType;
		}

		@Override
		public int getAttackElementPower()
		{
			return this.elemAtkPower;
		}

		@Override
		public int getElementDefAttr(byte i)
		{
			return this.elemDefAttr[i];
		}

		@Override
		public int getAppearance()
		{
			return this.appearance;
		}

		public Map<L2Item, Long> getPriceItems()
		{
			return this.priceItems;
		}

		@Override
		public int getMana()
		{
			return -1;
		}

		@Override
		public int getRemainingTime()
		{
			return -9999;
		}

		@Override
		public boolean isEquipped()
		{
			return false;
		}
	}

	private final L2PcInstance owner;
	private L2PcInstance partner;
	private final List<TradeItem> items;
	private String title;
	private boolean packaged;

	private boolean confirmed = false;
	private boolean locked = false;

	public TradeList(L2PcInstance owner)
	{
		this.items = new CopyOnWriteArrayList<>();
		this.owner = owner;
	}

	public L2PcInstance getOwner()
	{
		return this.owner;
	}

	public void setPartner(L2PcInstance partner)
	{
		this.partner = partner;
	}

	public L2PcInstance getPartner()
	{
		return this.partner;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return this.title;
	}

	public boolean isLocked()
	{
		return this.locked;
	}

	public boolean isConfirmed()
	{
		return this.confirmed;
	}

	public boolean isPackaged()
	{
		return this.packaged;
	}

	public void setPackaged(boolean value)
	{
		this.packaged = value;
	}

	/**
	 * Retrieves items from TradeList
	 */
	public TradeItem[] getItems()
	{
		return this.items.toArray(new TradeItem[this.items.size()]);
	}

	/**
	 * Returns the list of items in inventory available for transaction
	 *
	 * @return L2ItemInstance : items in inventory
	 */
	public TradeList.TradeItem[] getAvailableItems(PcInventory inventory)
	{
		ArrayList<TradeList.TradeItem> list = new ArrayList<>();
		for (TradeList.TradeItem item : this.items)
		{
			item = new TradeItem(item, item.getCount(), item.getPrice());
			inventory.adjustAvailableItem(item);
			list.add(item);
		}
		return list.toArray(new TradeItem[list.size()]);
	}

	/**
	 * Returns Item List size
	 */
	public int getItemCount()
	{
		return this.items.size();
	}

	/**
	 * Adjust available item from Inventory by the one in this list
	 *
	 * @param item : L2ItemInstance to be adjusted
	 * @return TradeItem representing adjusted item
	 */
	public TradeItem adjustAvailableItem(L2ItemInstance item)
	{
		if (item.isStackable())
		{
			for (TradeItem exclItem : this.items)
			{
				if (exclItem.getItem().getItemId() == item.getItemId())
				{
					if (item.getCount() <= exclItem.getCount())
					{
						return null;
					}
					else
					{
						return new TradeItem(item, item.getCount() - exclItem.getCount(), item.getReferencePrice());
					}
				}
			}
		}
		return new TradeItem(item, item.getCount(), item.getReferencePrice());
	}

	/**
	 * Adjust ItemRequest by corresponding item in this list using its <b>ObjectId</b>
	 *
	 * @param item : ItemRequest to be adjusted
	 */
	public void adjustItemRequest(ItemRequest item)
	{
		for (TradeItem filtItem : this.items)
		{
			if (filtItem.getObjectId() == item.getObjectId())
			{
				if (filtItem.getCount() < item.getCount())
				{
					item.setCount(filtItem.getCount());
				}
				return;
			}
		}
		item.setCount(0);
	}

	/**
	 * Add simplified item to TradeList
	 *
	 * @param objectId : int
	 * @param count    : int
	 * @return
	 */
	public synchronized TradeItem addItem(int objectId, long count)
	{
		return addItem(objectId, count, 0);
	}

	/**
	 * Add item to TradeList
	 *
	 * @param objectId : int
	 * @param count    : long
	 * @param price    : long
	 * @return
	 */
	public synchronized TradeItem addItem(int objectId, long count, long price)
	{
		if (isLocked())
		{
			Log.warning(this.owner.getName() + ": Attempt to modify locked TradeList!");
			return null;
		}

		L2Object o = L2World.getInstance().findObject(objectId);
		if (!(o instanceof L2ItemInstance))
		{
			Log.warning(this.owner.getName() + ": Attempt to add invalid item(" + o.getName() + ") to TradeList!");
			return null;
		}

		L2ItemInstance item = (L2ItemInstance) o;

		if (!(item.isTradeable() || getOwner().isGM() && Config.GM_TRADE_RESTRICTED_ITEMS) || item.isQuestItem())
		{
			return null;
		}

		if (!getOwner().getInventory().canManipulateWithItemId(item.getItemId()))
		{
			return null;
		}

		if (count <= 0 || count > item.getCount())
		{
			return null;
		}

		if (!item.isStackable() && count > 1)
		{
			Log.warning(this.owner.getName() + ": Attempt to add non-stackable item to TradeList with count > 1!");
			return null;
		}

		if (PcInventory.MAX_ADENA / count < price)
		{
			Log.warning(this.owner.getName() + ": Attempt to overflow adena !");
			return null;
		}

		for (TradeItem checkitem : this.items)
		{
			if (checkitem.getObjectId() == objectId)
			{
				return null;
			}
		}

		TradeItem titem = new TradeItem(item, count, price);
		this.items.add(titem);

		// If Player has already confirmed this trade, invalidate the confirmation
		invalidateConfirmation();
		return titem;
	}

	/**
	 * Add item to TradeList
	 *
	 * @param count : long
	 * @param price : long
	 * @return
	 */
	public synchronized TradeItem addItemByItemId(int itemId, long count, long price)
	{
		if (isLocked())
		{
			Log.warning(this.owner.getName() + ": Attempt to modify locked TradeList!");
			return null;
		}

		L2Item item = ItemTable.getInstance().getTemplate(itemId);
		if (item == null)
		{
			Log.warning(this.owner.getName() + ": Attempt to add invalid item to TradeList!");
			return null;
		}

		if (!item.isTradeable() || item.isQuestItem())
		{
			return null;
		}

		if (!item.isStackable() && count > 1)
		{
			Log.warning(this.owner.getName() + ": Attempt to add non-stackable item to TradeList with count > 1!");
			return null;
		}

		if (PcInventory.MAX_ADENA / count < price)
		{
			Log.warning(this.owner.getName() + ": Attempt to overflow adena !");
			return null;
		}

		TradeItem titem = new TradeItem(item, count, price);
		this.items.add(titem);

		// If Player has already confirmed this trade, invalidate the confirmation
		invalidateConfirmation();
		return titem;
	}

	/**
	 * Remove item from TradeList
	 *
	 * @param objectId : int
	 * @param count    : int
	 * @return
	 */
	public synchronized TradeItem removeItem(int objectId, int itemId, long count)
	{
		if (isLocked())
		{
			Log.warning(this.owner.getName() + ": Attempt to modify locked TradeList!");
			return null;
		}

		for (TradeItem titem : this.items)
		{
			if (titem.getObjectId() == objectId || titem.getItem().getItemId() == itemId)
			{
				// If Partner has already confirmed this trade, invalidate the confirmation
				if (this.partner != null)
				{
					TradeList partnerList = this.partner.getActiveTradeList();
					if (partnerList == null)
					{
						Log.warning(this.partner.getName() + ": Trading partner (" + this.partner.getName() +
								") is invalid in this trade!");
						return null;
					}
					partnerList.invalidateConfirmation();
				}

				// Reduce item count or complete item
				if (count != -1 && titem.getCount() > count)
				{
					titem.setCount(titem.getCount() - count);
				}
				else
				{
					this.items.remove(titem);
				}

				return titem;
			}
		}
		return null;
	}

	/**
	 * Update items in TradeList according their quantity in owner inventory
	 */
	public synchronized void updateItems()
	{
		for (TradeItem titem : this.items)
		{
			L2ItemInstance item = this.owner.getInventory().getItemByObjectId(titem.getObjectId());
			if (item == null || titem.getCount() < 1)
			{
				removeItem(titem.getObjectId(), -1, -1);
			}
			else if (item.getCount() < titem.getCount())
			{
				titem.setCount(item.getCount());
			}
		}
	}

	/**
	 * Lockes TradeList, no further changes are allowed
	 */
	public void lock()
	{
		this.locked = true;
	}

	/**
	 * Clears item list
	 */
	public synchronized void clear()
	{
		this.items.clear();
		this.locked = false;
	}

	/**
	 * Confirms TradeList
	 *
	 * @return : boolean
	 */
	public boolean confirm()
	{
		if (this.confirmed)
		{
			return true; // Already confirmed
		}

		// If Partner has already confirmed this trade, proceed exchange
		if (this.partner != null)
		{
			TradeList partnerList = this.partner.getActiveTradeList();
			if (partnerList == null)
			{
				Log.warning(this.partner.getName() + ": Trading partner (" + this.partner.getName() +
						") is invalid in this trade!");
				return false;
			}

			// Synchronization order to avoid deadlock
			TradeList sync1, sync2;
			if (getOwner().getObjectId() > partnerList.getOwner().getObjectId())
			{
				sync1 = partnerList;
				sync2 = this;
			}
			else
			{
				sync1 = this;
				sync2 = partnerList;
			}

			synchronized (sync1)
			{
				synchronized (sync2)
				{
					this.confirmed = true;
					if (partnerList.isConfirmed())
					{
						partnerList.lock();
						lock();
						if (!partnerList.validate())
						{
							return false;
						}
						if (!validate())
						{
							return false;
						}

						doExchange(partnerList);
					}
					else
					{
						this.partner.onTradeConfirm(this.owner);
					}
				}
			}
		}
		else
		{
			this.confirmed = true;
		}

		return this.confirmed;
	}

	/**
	 * Cancels TradeList confirmation
	 */
	public void invalidateConfirmation()
	{
		this.confirmed = false;
	}

	/**
	 * Validates TradeList with owner inventory
	 */
	private boolean validate()
	{
		// Check for Owner validity
		if (this.owner == null || L2World.getInstance().getPlayer(this.owner.getObjectId()) == null)
		{
			Log.warning("Invalid owner of TradeList");
			return false;
		}

		// Check for Item validity
		for (TradeItem titem : this.items)
		{
			L2ItemInstance item = this.owner.checkItemManipulation(titem.getObjectId(), titem.getCount(), "transfer");
			if (item == null || item.getCount() < 1)
			{
				Log.warning(this.owner.getName() + ": Invalid Item in TradeList");
				return false;
			}
		}

		return true;
	}

	/**
	 * Transfers all TradeItems from inventory to partner
	 */
	private boolean TransferItems(L2PcInstance partner, InventoryUpdate ownerIU, InventoryUpdate partnerIU)
	{
		for (TradeItem titem : this.items)
		{
			L2ItemInstance oldItem = this.owner.getInventory().getItemByObjectId(titem.getObjectId());
			if (oldItem == null)
			{
				return false;
			}
			L2ItemInstance newItem = this.owner.getInventory()
					.transferItem("Trade", titem.getObjectId(), titem.getCount(), partner.getInventory(), this.owner,
							this.partner);
			if (newItem == null)
			{
				return false;
			}

			// Add changes to inventory update packets
			if (ownerIU != null)
			{
				if (oldItem.getCount() > 0 && oldItem != newItem)
				{
					ownerIU.addModifiedItem(oldItem);
				}
				else
				{
					ownerIU.addRemovedItem(oldItem);
				}
			}

			if (partnerIU != null)
			{
				if (newItem.getCount() > titem.getCount())
				{
					partnerIU.addModifiedItem(newItem);
				}
				else
				{
					partnerIU.addNewItem(newItem);
				}
			}
		}
		return true;
	}

	/**
	 * Count items slots
	 */
	public int countItemsSlots(L2PcInstance partner)
	{
		int slots = 0;

		for (TradeItem item : this.items)
		{
			if (item == null)
			{
				continue;
			}
			L2Item template = ItemTable.getInstance().getTemplate(item.getItem().getItemId());
			if (template == null)
			{
				continue;
			}
			if (!template.isStackable())
			{
				slots += item.getCount();
			}
			else if (partner.getInventory().getItemByItemId(item.getItem().getItemId()) == null)
			{
				slots++;
			}
		}

		return slots;
	}

	/**
	 * Calc weight of items in tradeList
	 */
	public int calcItemsWeight()
	{
		long weight = 0;

		for (TradeItem item : this.items)
		{
			if (item == null)
			{
				continue;
			}
			L2Item template = ItemTable.getInstance().getTemplate(item.getItem().getItemId());
			if (template == null)
			{
				continue;
			}
			weight += item.getCount() * template.getWeight();
		}

		return (int) Math.min(weight, Integer.MAX_VALUE);
	}

	/**
	 * Proceeds with trade
	 */
	private void doExchange(TradeList partnerList)
	{
		boolean success = false;

		// check weight and slots
		if (!getOwner().getInventory().validateWeight(partnerList.calcItemsWeight()) ||
				!partnerList.getOwner().getInventory().validateWeight(calcItemsWeight()))
		{
			partnerList.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
		}
		else if (!getOwner().getInventory().validateCapacity(partnerList.countItemsSlots(getOwner())) ||
				!partnerList.getOwner().getInventory().validateCapacity(countItemsSlots(partnerList.getOwner())))
		{
			partnerList.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SLOTS_FULL));
			getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SLOTS_FULL));
		}
		else
		{
			// Prepare inventory update packet
			InventoryUpdate ownerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
			InventoryUpdate partnerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();

			// Transfer items
			partnerList.TransferItems(getOwner(), partnerIU, ownerIU);
			TransferItems(partnerList.getOwner(), ownerIU, partnerIU);

			// Send inventory update packet
			if (ownerIU != null)
			{
				this.owner.sendPacket(ownerIU);
			}
			else
			{
				this.owner.sendPacket(new ItemList(this.owner, false));
			}

			if (partnerIU != null)
			{
				this.partner.sendPacket(partnerIU);
			}
			else
			{
				this.partner.sendPacket(new ItemList(this.partner, false));
			}

			// Update current load as well
			StatusUpdate playerSU = new StatusUpdate(this.owner);
			playerSU.addAttribute(StatusUpdate.CUR_LOAD, this.owner.getCurrentLoad());
			this.owner.sendPacket(playerSU);
			playerSU = new StatusUpdate(this.partner);
			playerSU.addAttribute(StatusUpdate.CUR_LOAD, this.partner.getCurrentLoad());
			this.partner.sendPacket(playerSU);

			success = true;
		}
		// Finish the trade
		partnerList.getOwner().onTradeFinish(success);
		getOwner().onTradeFinish(success);
	}

	/**
	 * Buy items from this PrivateStore list
	 *
	 * @return int: result of trading. 0 - ok, 1 - canceled (no adena), 2 - failed (item error)
	 */
	public synchronized int privateStoreBuy(L2PcInstance player, HashSet<ItemRequest> items)
	{
		if (this.locked)
		{
			player.sendMessage("This store is locked.");
			return 1;
		}

		if (!validate())
		{
			lock();
			return 1;
		}

		if (!this.owner.isOnline() || !player.isOnline())
		{
			return 1;
		}

		int slots = 0;
		int weight = 0;
		long totalPrice = 0;

		final PcInventory ownerInventory = this.owner.getInventory();
		final PcInventory playerInventory = player.getInventory();

		for (ItemRequest item : items)
		{
			boolean found = false;

			for (TradeItem ti : this.items)
			{
				if (ti.getObjectId() == item.getObjectId())
				{
					if (ti.getPrice() == item.getPrice())
					{
						if (ti.getCount() < item.getCount())
						{
							item.setCount(ti.getCount());
						}
						found = true;
					}
					break;
				}
			}
			// item with this objectId and price not found in tradelist
			if (!found)
			{
				if (isPackaged())
				{
					Util.handleIllegalPlayerAction(player, "[TradeList.privateStoreBuy()] Player " + player.getName() +
									" tried to cheat the package sell and buy only a part of the package! Ban this player for bot usage!",
							Config.DEFAULT_PUNISH);
					return 2;
				}

				item.setCount(0);
				continue;
			}

			// check for overflow in the single item
			if (MAX_ADENA / item.getCount() < item.getPrice())
			{
				// private store attempting to overflow - disable it
				lock();
				return 1;
			}

			totalPrice += item.getCount() * item.getPrice();
			// check for overflow of the total price
			if (MAX_ADENA < totalPrice || totalPrice < 0)
			{
				// private store attempting to overflow - disable it
				lock();
				return 1;
			}

			// Check if requested item is available for manipulation
			L2ItemInstance oldItem = this.owner.checkItemManipulation(item.getObjectId(), item.getCount(), "sell");
			if (oldItem == null || !oldItem.isTradeable())
			{
				// private store sell invalid item - disable it
				lock();
				return 2;
			}

			L2Item template = ItemTable.getInstance().getTemplate(item.getItemId());
			if (template == null)
			{
				continue;
			}
			weight += item.getCount() * template.getWeight();
			if (!template.isStackable())
			{
				slots += item.getCount();
			}
			else if (playerInventory.getItemByItemId(item.getItemId()) == null)
			{
				slots++;
			}
		}

		if (totalPrice > playerInventory.getAdena())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return 1;
		}

		if (!playerInventory.validateWeight(weight))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return 1;
		}

		if (!playerInventory.validateCapacity(slots))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SLOTS_FULL));
			return 1;
		}

		// Prepare inventory update packets
		final InventoryUpdate ownerIU = new InventoryUpdate();
		final InventoryUpdate playerIU = new InventoryUpdate();

		final L2ItemInstance adenaItem = playerInventory.getAdenaInstance();
		if (!playerInventory.reduceAdena("PrivateStore", totalPrice, player, this.owner))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return 1;
		}
		playerIU.addItem(adenaItem);
		ownerInventory.addAdena("PrivateStore", totalPrice, this.owner, player);
		player.sendPacket(new ExAdenaInvenCount(player.getAdena(), player.getInventory().getSize(false)));
		this.owner.sendPacket(new ExAdenaInvenCount(this.owner.getAdena(), this.owner.getInventory().getSize(false)));
		//ownerIU.addItem(ownerInventory.getAdenaInstance());

		boolean ok = true;

		// Transfer items
		for (ItemRequest item : items)
		{
			if (item.getCount() == 0)
			{
				continue;
			}

			// Check if requested item is available for manipulation
			L2ItemInstance oldItem = this.owner.checkItemManipulation(item.getObjectId(), item.getCount(), "sell");
			if (oldItem == null)
			{
				// should not happens - validation already done
				lock();
				ok = false;
				break;
			}

			// Proceed with item transfer
			L2ItemInstance newItem = ownerInventory
					.transferItem("PrivateStore", item.getObjectId(), item.getCount(), playerInventory, this.owner, player);
			if (newItem == null)
			{
				ok = false;
				break;
			}
			removeItem(item.getObjectId(), -1, item.getCount());

			// Add changes to inventory update packets
			if (oldItem.getCount() > 0 && oldItem != newItem)
			{
				ownerIU.addModifiedItem(oldItem);
			}
			else
			{
				ownerIU.addRemovedItem(oldItem);
			}
			if (newItem.getCount() > item.getCount())
			{
				playerIU.addModifiedItem(newItem);
			}
			else
			{
				playerIU.addNewItem(newItem);
			}

			// Send messages about the transaction to both players
			if (newItem.isStackable())
			{
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S3_S2_S);
				msg.addString(player.getName());
				msg.addItemName(newItem);
				msg.addItemNumber(item.getCount());
				this.owner.sendPacket(msg);

				msg = SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S3_S2_S_FROM_C1);
				msg.addString(this.owner.getName());
				msg.addItemName(newItem);
				msg.addItemNumber(item.getCount());
				player.sendPacket(msg);
			}
			else
			{
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S2);
				msg.addString(player.getName());
				msg.addItemName(newItem);
				this.owner.sendPacket(msg);

				msg = SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S2_FROM_C1);
				msg.addString(this.owner.getName());
				msg.addItemName(newItem);
				player.sendPacket(msg);
			}
		}

		// Send inventory update packet
		this.owner.sendPacket(ownerIU);
		player.sendPacket(playerIU);
		if (ok)
		{
			return 0;
		}
		else
		{
			return 2;
		}
	}

	/**
	 * Sell items to this PrivateStore list
	 *
	 * @return : boolean true if success
	 */
	public synchronized boolean privateStoreSell(L2PcInstance player, ItemRequest[] items)
	{
		if (this.locked)
		{
			return false;
		}

		if (!this.owner.isOnline() || !player.isOnline())
		{
			return false;
		}

		boolean ok = false;

		final PcInventory ownerInventory = this.owner.getInventory();
		final PcInventory playerInventory = player.getInventory();

		// Prepare inventory update packet
		final InventoryUpdate ownerIU = new InventoryUpdate();
		final InventoryUpdate playerIU = new InventoryUpdate();

		long totalPrice = 0;

		for (ItemRequest item : items)
		{
			// searching item in tradelist using itemId
			boolean found = false;

			for (TradeItem ti : this.items)
			{
				if (ti.getItem().getItemId() == item.getItemId())
				{
					// price should be the same
					if (ti.getPrice() == item.getPrice())
					{
						// if requesting more than available - decrease count
						if (ti.getCount() < item.getCount())
						{
							item.setCount(ti.getCount());
						}
						found = item.getCount() > 0;
					}
					break;
				}
			}
			// not found any item in the tradelist with same itemId and price
			// maybe another player already sold this item ?
			if (!found)
			{
				continue;
			}

			// check for overflow in the single item
			if (MAX_ADENA / item.getCount() < item.getPrice())
			{
				lock();
				break;
			}

			long totalPrice2 = totalPrice + item.getCount() * item.getPrice();
			// check for overflow of the total price
			if (MAX_ADENA < totalPrice2 || totalPrice2 < 0)
			{
				lock();
				break;
			}

			if (ownerInventory.getAdena() < totalPrice2)
			{
				continue;
			}

			// Check if requested item is available for manipulation
			int objectId = item.getObjectId();
			L2ItemInstance oldItem = player.checkItemManipulation(objectId, item.getCount(), "sell");
			// private store - buy use same objectId for buying several non-stackable items
			if (oldItem == null)
			{
				// searching other items using same itemId
				oldItem = playerInventory.getItemByItemId(item.getItemId());
				if (oldItem == null)
				{
					continue;
				}
				objectId = oldItem.getObjectId();
				oldItem = player.checkItemManipulation(objectId, item.getCount(), "sell");
				if (oldItem == null)
				{
					continue;
				}
			}

			if (oldItem.getItemId() != item.getItemId())
			{
				Util.handleIllegalPlayerAction(player, player + " is cheating with sell items", Config.DEFAULT_PUNISH);
				return false;
			}

			if (!oldItem.isTradeable())
			{
				continue;
			}

			// Proceed with item transfer
			L2ItemInstance newItem = playerInventory
					.transferItem("PrivateStore", objectId, item.getCount(), ownerInventory, player, this.owner);
			if (newItem == null)
			{
				continue;
			}

			removeItem(-1, item.getItemId(), item.getCount());
			ok = true;

			// increase total price only after successful transaction
			totalPrice = totalPrice2;

			// Add changes to inventory update packets
			if (oldItem.getCount() > 0 && oldItem != newItem)
			{
				playerIU.addModifiedItem(oldItem);
			}
			else
			{
				playerIU.addRemovedItem(oldItem);
			}
			if (newItem.getCount() > item.getCount())
			{
				ownerIU.addModifiedItem(newItem);
			}
			else
			{
				ownerIU.addNewItem(newItem);
			}

			// Send messages about the transaction to both players
			if (newItem.isStackable())
			{
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S3_S2_S_FROM_C1);
				msg.addString(player.getName());
				msg.addItemName(newItem);
				msg.addItemNumber(item.getCount());
				this.owner.sendPacket(msg);

				msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S3_S2_S);
				msg.addString(this.owner.getName());
				msg.addItemName(newItem);
				msg.addItemNumber(item.getCount());
				player.sendPacket(msg);
			}
			else
			{
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S2_FROM_C1);
				msg.addString(player.getName());
				msg.addItemName(newItem);
				this.owner.sendPacket(msg);

				msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S2);
				msg.addString(this.owner.getName());
				msg.addItemName(newItem);
				player.sendPacket(msg);
			}
		}

		if (totalPrice > 0)
		{
			// Transfer adena
			if (totalPrice > ownerInventory.getAdena())
			// should not happens, just a precaution
			{
				return false;
			}
			final L2ItemInstance adenaItem = ownerInventory.getAdenaInstance();
			ownerInventory.reduceAdena("PrivateStore", totalPrice, this.owner, player);
			ownerIU.addItem(adenaItem);
			playerInventory.addAdena("PrivateStore", totalPrice, player, this.owner);
			playerIU.addItem(playerInventory.getAdenaInstance());
			player.sendPacket(new ExAdenaInvenCount(player.getAdena(), player.getInventory().getSize(false)));
			this.owner.sendPacket(new ExAdenaInvenCount(this.owner.getAdena(), this.owner.getInventory().getSize(false)));
		}

		if (ok)
		{
			// Send inventory update packet
			this.owner.sendPacket(ownerIU);
			player.sendPacket(playerIU);
		}
		return ok;
	}
}
